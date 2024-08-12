package com.astar.eattable.restaurant.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RestaurantDeleteEventPayload {
    private Long restaurantId;

    public static RestaurantDeleteEventPayload from(Long restaurantId) {
        return new RestaurantDeleteEventPayload(restaurantId);
    }
}
