package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.command.RestaurantCreateCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RestaurantCreateEventPayload {
    private Long restaurantId;
    private RestaurantCreateCommand command;

    public static RestaurantCreateEventPayload from(Long restaurantId, RestaurantCreateCommand command) {
        return new RestaurantCreateEventPayload(restaurantId, command);
    }
}
