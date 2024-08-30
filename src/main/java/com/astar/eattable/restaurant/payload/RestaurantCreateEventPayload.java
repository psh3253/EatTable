package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.command.RestaurantCreateCommand;
import com.astar.eattable.restaurant.event.RestaurantCreateEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RestaurantCreateEventPayload {
    private Long restaurantId;
    private RestaurantCreateCommand command;
    private Long userId;
    private String userNickname;

    public static RestaurantCreateEventPayload from(RestaurantCreateEvent event) {
        return new RestaurantCreateEventPayload(event.getRestaurantId(), event.getCommand(), event.getUser().getId(), event.getUser().getNickname());
    }
}
