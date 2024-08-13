package com.astar.eattable.restaurant.service;

import com.astar.eattable.common.dto.EventTypes;
import com.astar.eattable.common.model.ExternalEvent;
import com.astar.eattable.common.repository.ExternalEventRepository;
import com.astar.eattable.restaurant.command.*;
import com.astar.eattable.restaurant.dto.Day;
import com.astar.eattable.restaurant.event.RestaurantCreateEvent;
import com.astar.eattable.restaurant.exception.*;
import com.astar.eattable.restaurant.model.BusinessHours;
import com.astar.eattable.restaurant.model.Menu;
import com.astar.eattable.restaurant.model.MenuSection;
import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.restaurant.payload.*;
import com.astar.eattable.restaurant.repository.BusinessHoursRepository;
import com.astar.eattable.restaurant.repository.MenuRepository;
import com.astar.eattable.restaurant.repository.MenuSectionRepository;
import com.astar.eattable.restaurant.repository.RestaurantRepository;
import com.astar.eattable.restaurant.validator.RestaurantValidator;
import com.astar.eattable.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RestaurantCommandService {
    private final RestaurantRepository restaurantRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final MenuSectionRepository menuSectionRepository;
    private final MenuRepository menuRepository;
    private final ExternalEventRepository externalEventRepository;
    private final RestaurantValidator restaurantValidator;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public Long createRestaurant(RestaurantCreateCommand command, User currentUser) {
        validateRestaurantAlreadyExists(command.getName(), command.getAddress());

        Restaurant restaurant = restaurantRepository.save(command.toEntity(currentUser));
        command.getBusinessHours().forEach(businessHoursCommand -> businessHoursRepository.save(businessHoursCommand.toEntity(restaurant)));

        publisher.publishEvent(new RestaurantCreateEvent(restaurant.getId(), command, currentUser));
        return restaurant.getId();
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId, User currentUser) throws JsonProcessingException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantValidator.validateRestaurantOwner(restaurant, currentUser.getId());
        restaurantRepository.delete(restaurant);

        ExternalEvent externalEvent = ExternalEvent.from(EventTypes.RESTAURANT_DELETED, objectMapper.writeValueAsString(RestaurantDeleteEventPayload.from(restaurantId)), currentUser);
        externalEventRepository.save(externalEvent);
    }

    @Transactional
    public void updateRestaurant(Long restaurantId, RestaurantUpdateCommand command, User currentUser) throws JsonProcessingException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        restaurantValidator.validateRestaurantOwner(restaurant, currentUser.getId());
        restaurant.update(command);

        ExternalEvent externalEvent = ExternalEvent.from(EventTypes.RESTAURANT_UPDATED, objectMapper.writeValueAsString(RestaurantUpdateEventPayload.from(restaurantId, command)), currentUser);
        externalEventRepository.save(externalEvent);
    }

    @Transactional
    public void updateBusinessHours(Long restaurantId, BusinessHoursUpdateCommand command, User currentUser) throws JsonProcessingException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        restaurantValidator.validateRestaurantOwner(restaurant, currentUser.getId());

        command.getBusinessHours().forEach(businessHoursCommand -> {
            BusinessHours businessHours = businessHoursRepository.findByRestaurantIdAndDay(restaurantId, Day.valueOf(businessHoursCommand.getDay())).orElseThrow(() -> new BusinessHoursNotFoundException(restaurantId, businessHoursCommand.getDay()));
            businessHours.update(businessHoursCommand);
        });

        ExternalEvent externalEvent = ExternalEvent.from(EventTypes.BUSINESS_HOURS_UPDATED, objectMapper.writeValueAsString(BusinessHoursUpdateEventPayload.from(restaurantId, command)), currentUser);
        externalEventRepository.save(externalEvent);
    }

    @Transactional
    public void createMenuSection(Long restaurantId, MenuSectionCreateCommand command, User currentUser) throws JsonProcessingException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantValidator.validateRestaurantOwner(restaurant, currentUser.getId());
        validateMenuSectionAlreadyExists(restaurantId, command.getName());

        MenuSection menuSection = menuSectionRepository.save(command.toEntity(restaurant));

        ExternalEvent externalEvent = ExternalEvent.from(EventTypes.MENU_SECTION_CREATED, objectMapper.writeValueAsString(MenuSectionCreateEventPayload.from(restaurantId, menuSection.getId(), command)), currentUser);
        externalEventRepository.save(externalEvent);
    }

    @Transactional
    public void updateMenuSection(Long menuSectionId, MenuSectionUpdateCommand command, User currentUser) throws JsonProcessingException {
        validateMenuSectionAlreadyExists(menuSectionId, command.getName());
        MenuSection menuSection = menuSectionRepository.findById(menuSectionId).orElseThrow(() -> new MenuSectionNotFoundException(menuSectionId));
        restaurantValidator.validateRestaurantOwner(menuSection.getRestaurant(), currentUser.getId());
        menuSection.update(command);

        ExternalEvent externalEvent = ExternalEvent.from(EventTypes.MENU_SECTION_UPDATED, objectMapper.writeValueAsString(MenuSectionUpdateEventPayload.from(menuSection.getRestaurant().getId(), menuSectionId, command)), currentUser);
        externalEventRepository.save(externalEvent);
    }

    @Transactional
    public void deleteMenuSection(Long menuSectionId, User currentUser) throws JsonProcessingException {
        MenuSection menuSection = menuSectionRepository.findById(menuSectionId).orElseThrow(() -> new MenuSectionNotFoundException(menuSectionId));
        restaurantValidator.validateRestaurantOwner(menuSection.getRestaurant(), currentUser.getId());
        validateMenuSectionNotEmpty(menuSectionId);
        menuSectionRepository.delete(menuSection);

        ExternalEvent externalEvent = ExternalEvent.from(EventTypes.MENU_SECTION_DELETED, objectMapper.writeValueAsString(MenuSectionDeleteEventPayload.from(menuSection.getRestaurant().getId(), menuSectionId)), currentUser);
        externalEventRepository.save(externalEvent);
    }

    @Transactional
    public void createMenu(Long menuSectionId, MenuCreateCommand command, User currentUser) throws JsonProcessingException {
        MenuSection menuSection = menuSectionRepository.findById(menuSectionId).orElseThrow(() -> new MenuSectionNotFoundException(menuSectionId));
        restaurantValidator.validateRestaurantOwner(menuSection.getRestaurant(), currentUser.getId());
        validateMenuAlreadyExists(menuSectionId, command.getName());
        Menu menu = menuRepository.save(command.toEntity(menuSection));

        ExternalEvent externalEvent = ExternalEvent.from(EventTypes.MENU_CREATED, objectMapper.writeValueAsString(MenuCreateEventPayload.from(menuSection.getRestaurant().getId(), menuSectionId, menu.getId(), command)), currentUser);
        externalEventRepository.save(externalEvent);
    }

    @Transactional
    public void deleteMenu(Long menuId, User currentUser) throws JsonProcessingException {
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new MenuNotFoundException(menuId));
        restaurantValidator.validateRestaurantOwner(menu.getRestaurant(), currentUser.getId());
        menuRepository.delete(menu);

        ExternalEvent externalEvent = ExternalEvent.from(EventTypes.MENU_DELETED, objectMapper.writeValueAsString(MenuDeleteEventPayload.from(menu.getRestaurant().getId(), menu.getMenuSection().getId(), menuId)), currentUser);
        externalEventRepository.save(externalEvent);
    }

    @Transactional
    public void updateMenu(Long menuId, MenuUpdateCommand command, User currentUser) throws JsonProcessingException {
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new MenuNotFoundException(menuId));
        restaurantValidator.validateRestaurantOwner(menu.getRestaurant(), currentUser.getId());
        menu.update(command);

        ExternalEvent externalEvent = ExternalEvent.from(EventTypes.MENU_UPDATED, objectMapper.writeValueAsString(MenuUpdateEventPayload.from(menu.getRestaurant().getId(), menu.getMenuSection().getId(), menuId, command)), currentUser);
        externalEventRepository.save(externalEvent);
    }

    private void validateRestaurantAlreadyExists(String name, String address) {
        if (restaurantRepository.existsByNameAndAddress(name, address)) {
            throw new RestaurantAlreadyExistsException(name, address);
        }
    }

    private void validateMenuSectionAlreadyExists(Long restaurantId, String name) {
        if (menuSectionRepository.existsByRestaurantIdAndName(restaurantId, name)) {
            throw new MenuSectionAlreadyExistsException(restaurantId, name);
        }
    }

    private void validateMenuSectionNotEmpty(Long menuSectionId) {
        if (menuRepository.existsByMenuSectionId(menuSectionId)) {
            throw new MenuSectionNotEmptyException(menuSectionId);
        }
    }

    private void validateMenuAlreadyExists(Long menuSectionId, String name) {
        if (menuRepository.existsByName(name)) {
            throw new MenuAlreadyExistsException(menuSectionId, name);
        }
    }
}
