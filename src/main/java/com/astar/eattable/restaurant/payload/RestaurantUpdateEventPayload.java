package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.command.RestaurantUpdateCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RestaurantUpdateEventPayload {
    private Long restaurantId;
    private RestaurantUpdateCommand command;

    public static RestaurantUpdateEventPayload from(Long restaurantId, RestaurantUpdateCommand command) {
        return new RestaurantUpdateEventPayload(restaurantId, command);
    }
}
