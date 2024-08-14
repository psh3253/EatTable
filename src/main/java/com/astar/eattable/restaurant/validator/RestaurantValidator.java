package com.astar.eattable.restaurant.validator;

import com.astar.eattable.restaurant.exception.UnauthorizedRestaurantAccessException;
import com.astar.eattable.restaurant.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantValidator {
    public void validateRestaurantOwner(Restaurant restaurantId, Long userId) {
        if (!restaurantId.getUser().getId().equals(userId)) {
            throw new UnauthorizedRestaurantAccessException(restaurantId.getId(), userId);
        }
    }
}
