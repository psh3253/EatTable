package com.astar.eattable.restaurant.service;

import com.astar.eattable.common.dto.EventTypes;
import com.astar.eattable.restaurant.command.*;
import com.astar.eattable.restaurant.dto.Day;
import com.astar.eattable.restaurant.exception.*;
import com.astar.eattable.restaurant.model.BusinessHours;
import com.astar.eattable.restaurant.model.MenuSection;
import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.restaurant.model.RestaurantEvent;
import com.astar.eattable.restaurant.repository.*;
import com.astar.eattable.restaurant.validator.RestaurantValidator;
import com.astar.eattable.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RestaurantCommandService {
    private final RestaurantRepository restaurantRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final MenuSectionRepository menuSectionRepository;
    private final MenuRepository menuRepository;
    private final RestaurantEventRepository restaurantEventRepository;
    private final RestaurantValidator restaurantValidator;
    private final ObjectMapper objectMapper;

    @Transactional
    public Long createRestaurant(RestaurantCreateCommand command, User currentUser) throws JsonProcessingException {
        validateExistingRestaurant(command.getName(), command.getAddress());

        Restaurant restaurant = restaurantRepository.save(command.toEntity(currentUser));
        command.getBusinessHours().forEach(businessHoursCommand -> businessHoursRepository.save(businessHoursCommand.toEntity(restaurant)));

        RestaurantEvent restaurantEvent = RestaurantEvent.from(restaurant.getId(), EventTypes.RESTAURANT_CREATED, objectMapper.writeValueAsString(command));
        restaurantEventRepository.save(restaurantEvent);
        return restaurant.getId();
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantRepository.delete(restaurant);

        RestaurantEvent restaurantEvent = RestaurantEvent.from(restaurantId, EventTypes.RESTAURANT_DELETED, "");
        restaurantEventRepository.save(restaurantEvent);
    }

    @Transactional
    public void updateRestaurant(Long restaurantId, RestaurantUpdateCommand command, User currentUser) throws JsonProcessingException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        restaurantValidator.validateRestaurantOwner(restaurant, currentUser.getId());
        restaurant.update(command);

        RestaurantEvent restaurantEvent = RestaurantEvent.from(restaurantId, EventTypes.RESTAURANT_UPDATED, objectMapper.writeValueAsString(command));
        restaurantEventRepository.save(restaurantEvent);
    }

    @Transactional
    public void updateBusinessHours(Long restaurantId, BusinessHoursUpdateCommand command, User currentUser) throws JsonProcessingException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        restaurantValidator.validateRestaurantOwner(restaurant, currentUser.getId());

        command.getBusinessHours().forEach(businessHoursCommand -> {
            BusinessHours businessHours = businessHoursRepository.findByRestaurantIdAndDay(restaurantId, Day.valueOf(businessHoursCommand.getDay())).orElseThrow(() -> new BusinessHoursNotFoundException(restaurantId, businessHoursCommand.getDay()));
            businessHours.update(businessHoursCommand);
        });

        RestaurantEvent restaurantEvent = RestaurantEvent.from(restaurantId, EventTypes.BUSINESS_HOURS_UPDATED, objectMapper.writeValueAsString(command));
        restaurantEventRepository.save(restaurantEvent);
    }

    @Transactional
    public void createMenuSection(Long restaurantId, MenuSectionCreateCommand command, User currentUser) throws JsonProcessingException{
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantValidator.validateRestaurantOwner(restaurant, currentUser.getId());
        validateExistingMenuSection(restaurantId, command.getName());
        menuSectionRepository.save(command.toEntity(restaurant));

        RestaurantEvent restaurantEvent = RestaurantEvent.from(restaurantId, EventTypes.MENU_SECTION_CREATED, objectMapper.writeValueAsString(command));
        restaurantEventRepository.save(restaurantEvent);
    }

    @Transactional
    public void updateMenuSection(Long restaurantId, Long menuSectionId, MenuSectionUpdateCommand command, User currentUser) throws JsonProcessingException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantValidator.validateRestaurantOwner(restaurant, currentUser.getId());
        validateExistingMenuSection(restaurantId, command.getName());

        MenuSection menuSection = menuSectionRepository.findById(menuSectionId).orElseThrow(() -> new MenuSectionNotFoundException(menuSectionId));
        menuSection.update(command);

        RestaurantEvent restaurantEvent = RestaurantEvent.from(restaurantId, EventTypes.MENU_SECTION_UPDATED, objectMapper.writeValueAsString(command));
        restaurantEventRepository.save(restaurantEvent);
    }

    private void validateExistingRestaurant(String name, String address) {
        if(restaurantRepository.existsByNameAndAddress(name, address)) {
            throw new RestaurantAlreadyExistsException(name, address);
        }
    }

    private void validateExistingMenuSection(Long restaurantId, String name) {
        if(menuSectionRepository.existsByRestaurantIdAndName(restaurantId, name)) {
            throw new MenuSectionAlreadyExistsException(restaurantId, name);
        }
    }
}
