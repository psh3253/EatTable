package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.event.RestaurantDeleteEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RestaurantDeleteEventPayload {
    private Long restaurantId;

    public static RestaurantDeleteEventPayload from(RestaurantDeleteEvent event) {
        return new RestaurantDeleteEventPayload(event.getRestaurantId());
    }
}
