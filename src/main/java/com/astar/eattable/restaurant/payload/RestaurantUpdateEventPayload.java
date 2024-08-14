package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.command.RestaurantUpdateCommand;
import com.astar.eattable.restaurant.event.RestaurantUpdateEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RestaurantUpdateEventPayload {
    private Long restaurantId;
    private RestaurantUpdateCommand command;

    public static RestaurantUpdateEventPayload from(RestaurantUpdateEvent event) {
        return new RestaurantUpdateEventPayload(event.getRestaurantId(), event.getCommand());
    }
}
