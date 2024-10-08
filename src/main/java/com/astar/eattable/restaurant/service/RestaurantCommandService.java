package com.astar.eattable.restaurant.service;

import com.astar.eattable.common.dto.Day;
import com.astar.eattable.reservation.command.TableCountUpdateCommand;
import com.astar.eattable.reservation.exception.RestaurantTableNotFoundException;
import com.astar.eattable.restaurant.command.*;
import com.astar.eattable.restaurant.event.*;
import com.astar.eattable.restaurant.exception.*;
import com.astar.eattable.restaurant.model.*;
import com.astar.eattable.restaurant.repository.*;
import com.astar.eattable.restaurant.validator.RestaurantValidator;
import com.astar.eattable.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RestaurantCommandService {
    private final RestaurantRepository restaurantRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final MenuSectionRepository menuSectionRepository;
    private final MenuRepository menuRepository;
    private final ClosedPeriodRepository closedPeriodRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final RestaurantValidator restaurantValidator;
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
    public void deleteRestaurant(Long restaurantId, User currentUser) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantValidator.validateRestaurantOwner(restaurant, currentUser.getId());
        restaurantRepository.delete(restaurant);

        publisher.publishEvent(new RestaurantDeleteEvent(restaurantId, currentUser));
    }

    @Transactional
    public void updateRestaurant(Long restaurantId, RestaurantUpdateCommand command, User currentUser) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantValidator.validateRestaurantOwner(restaurant, currentUser.getId());
        restaurant.update(command);

        publisher.publishEvent(new RestaurantUpdateEvent(restaurantId, command, currentUser));
    }

    @Transactional
    public void updateBusinessHours(Long restaurantId, BusinessHoursUpdateCommand command, User currentUser) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantValidator.validateRestaurantOwner(restaurant, currentUser.getId());

        command.getBusinessHours().forEach(businessHoursCommand -> {
            BusinessHours businessHours = businessHoursRepository.findByRestaurantIdAndDay(restaurantId, Day.valueOf(businessHoursCommand.getDay())).orElseThrow(() -> new BusinessHoursNotFoundException(restaurantId, businessHoursCommand.getDay()));
            businessHours.update(businessHoursCommand);
        });

        publisher.publishEvent(new BusinessHoursUpdateEvent(restaurantId, command, currentUser));
    }

    @Transactional
    public void createMenuSection(Long restaurantId, MenuSectionCreateCommand command, User currentUser) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantValidator.validateRestaurantOwner(restaurant, currentUser.getId());
        validateMenuSectionAlreadyExists(restaurantId, command.getName());

        MenuSection menuSection = menuSectionRepository.save(command.toEntity(restaurant));
        publisher.publishEvent(new MenuSectionCreateEvent(restaurantId, menuSection.getId(), command, currentUser));
    }

    @Transactional
    public void updateMenuSection(Long menuSectionId, MenuSectionUpdateCommand command, User currentUser) {
        validateMenuSectionAlreadyExists(menuSectionId, command.getName());
        MenuSection menuSection = menuSectionRepository.findById(menuSectionId).orElseThrow(() -> new MenuSectionNotFoundException(menuSectionId));
        restaurantValidator.validateRestaurantOwner(menuSection.getRestaurant(), currentUser.getId());
        menuSection.update(command);

        publisher.publishEvent(new MenuSectionUpdateEvent(menuSection.getRestaurant().getId(), menuSectionId, command, currentUser));
    }

    @Transactional
    public void deleteMenuSection(Long menuSectionId, User currentUser) {
        MenuSection menuSection = menuSectionRepository.findById(menuSectionId).orElseThrow(() -> new MenuSectionNotFoundException(menuSectionId));
        restaurantValidator.validateRestaurantOwner(menuSection.getRestaurant(), currentUser.getId());
        validateMenuSectionNotEmpty(menuSectionId);
        menuSectionRepository.delete(menuSection);

        publisher.publishEvent(new MenuSectionDeleteEvent(menuSection.getRestaurant().getId(), menuSectionId, currentUser));
    }

    @Transactional
    public void createMenu(Long menuSectionId, MenuCreateCommand command, User currentUser) {
        MenuSection menuSection = menuSectionRepository.findById(menuSectionId).orElseThrow(() -> new MenuSectionNotFoundException(menuSectionId));
        restaurantValidator.validateRestaurantOwner(menuSection.getRestaurant(), currentUser.getId());
        validateMenuAlreadyExists(menuSectionId, command.getName());
        Menu menu = menuRepository.save(command.toEntity(menuSection));

        publisher.publishEvent(new MenuCreateEvent(menu.getRestaurant().getId(), menuSectionId, menu.getId(), command, currentUser));
    }

    @Transactional
    public void deleteMenu(Long menuId, User currentUser) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new MenuNotFoundException(menuId));
        restaurantValidator.validateRestaurantOwner(menu.getRestaurant(), currentUser.getId());
        menuRepository.delete(menu);

        publisher.publishEvent(new MenuDeleteEvent(menu.getRestaurant().getId(), menu.getMenuSection().getId(), menuId, currentUser));
    }

    @Transactional
    public void updateMenu(Long menuId, MenuUpdateCommand command, User currentUser) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new MenuNotFoundException(menuId));
        restaurantValidator.validateRestaurantOwner(menu.getRestaurant(), currentUser.getId());
        menu.update(command);

        publisher.publishEvent(new MenuUpdateEvent(menu.getRestaurant().getId(), menu.getMenuSection().getId(), menuId, command, currentUser));
    }

    @Transactional
    public void createClosedPeriod(Long restaurantId, ClosedPeriodCreateCommand command, User currentUser) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantValidator.validateRestaurantOwner(restaurant, currentUser.getId());
        List<ClosedPeriod> closedPeriods = closedPeriodRepository.findAllByRestaurantId(restaurantId);
        restaurantValidator.validateNoOverlapClosedPeriod(closedPeriods, command.getStartDate(), command.getEndDate(), restaurantId);
        restaurantValidator.validateClosePeriodNotBeforeToday(command.getStartDate());
        ClosedPeriod closedPeriod = closedPeriodRepository.save(command.toEntity(restaurant));

        publisher.publishEvent(new ClosedPeriodCreateEvent(restaurantId, closedPeriod.getId(), command, currentUser));
    }

    @Transactional
    public void deleteClosedPeriod(Long closedPeriodId, User currentUser) {
        ClosedPeriod closedPeriod = closedPeriodRepository.findById(closedPeriodId).orElseThrow(() -> new ClosedPeriodNotFoundException(closedPeriodId));
        restaurantValidator.validateRestaurantOwner(closedPeriod.getRestaurant(), currentUser.getId());
        closedPeriodRepository.delete(closedPeriod);

        publisher.publishEvent(new ClosedPeriodDeleteEvent(closedPeriod.getRestaurant().getId(), closedPeriodId, currentUser));
    }

    @Transactional
    public void initRestaurantTable(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        int[] capacities = {1, 2, 3, 4};
        List<RestaurantTable> restaurantTables = new ArrayList<>();
        for (int capacity : capacities) {
            RestaurantTable restaurantTable = RestaurantTable.builder().capacity(capacity).count(0).restaurant(restaurant).build();
            restaurantTables.add(restaurantTable);
        }
        restaurantTableRepository.saveAll(restaurantTables);
    }

    @Transactional
    public void updateTableCount(TableCountUpdateCommand command, User currentUser) {
        RestaurantTable restaurantTable = restaurantTableRepository.findByRestaurantIdAndCapacity(command.getRestaurantId(), command.getCapacity()).orElseThrow(() -> new RestaurantTableNotFoundException(command.getRestaurantId(), command.getCapacity()));
        restaurantValidator.validateRestaurantOwner(restaurantTable.getRestaurant(), currentUser.getId());
        restaurantTable.updateCount(command.getCount());

        publisher.publishEvent(new TableCountUpdateEvent(command.getRestaurantId(), command, currentUser));
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
        if (menuRepository.existsByMenuSectionIdAndName(menuSectionId, name)) {
            throw new MenuAlreadyExistsException(menuSectionId, name);
        }
    }

    public List<Long> getAllRestaurantIds() {
        return restaurantRepository.findAll().stream().map(Restaurant::getId).toList();
    }
}
