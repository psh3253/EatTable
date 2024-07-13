package com.astar.eattable.restaurant.service;

import com.astar.eattable.common.dto.EventTypes;
import com.astar.eattable.restaurant.command.BusinessHoursUpdateCommand;
import com.astar.eattable.restaurant.command.RestaurantCreateCommand;
import com.astar.eattable.restaurant.command.RestaurantUpdateCommand;
import com.astar.eattable.restaurant.dto.Day;
import com.astar.eattable.restaurant.exception.BusinessHoursNotFoundException;
import com.astar.eattable.restaurant.exception.RestaurantAlreadyExistsException;
import com.astar.eattable.restaurant.exception.RestaurantNotFoundException;
import com.astar.eattable.restaurant.exception.UnauthorizedRestaurantAccessException;
import com.astar.eattable.restaurant.model.BusinessHours;
import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.restaurant.model.RestaurantEvent;
import com.astar.eattable.restaurant.repository.BusinessHoursRepository;
import com.astar.eattable.restaurant.repository.RestaurantEventRepository;
import com.astar.eattable.restaurant.repository.RestaurantRepository;
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
    private final RestaurantEventRepository restaurantEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Long createRestaurant(RestaurantCreateCommand command, User currentUser) throws JsonProcessingException {
        if (restaurantRepository.existsByNameAndAddress(command.getName(), command.getAddress())) {
            throw new RestaurantAlreadyExistsException(command.getName(), command.getAddress());
        }
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
        if (!restaurant.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedRestaurantAccessException(restaurantId, currentUser.getId());
        }
        restaurant.update(command);

        RestaurantEvent restaurantEvent = RestaurantEvent.from(restaurantId, EventTypes.RESTAURANT_UPDATED, objectMapper.writeValueAsString(command));
        restaurantEventRepository.save(restaurantEvent);
    }

    @Transactional
    public void updateBusinessHours(Long restaurantId, BusinessHoursUpdateCommand command, User currentUser) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        if (!restaurant.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedRestaurantAccessException(restaurantId, currentUser.getId());
        }
        command.getBusinessHours().forEach(businessHoursCommand -> {
            BusinessHours businessHours = businessHoursRepository.findByRestaurantIdAndDay(restaurantId, Day.valueOf(businessHoursCommand.getDay())).orElseThrow(() -> new BusinessHoursNotFoundException(restaurantId, businessHoursCommand.getDay()));
            businessHours.update(businessHoursCommand);
        });

        RestaurantEvent restaurantEvent = RestaurantEvent.from(restaurantId, EventTypes.BUSINESS_HOURS_UPDATED, "");
        restaurantEventRepository.save(restaurantEvent);
    }
}
