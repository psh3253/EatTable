package com.astar.eattable.restaurant.event;

import com.astar.eattable.restaurant.command.RestaurantCreateCommand;
import com.astar.eattable.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestaurantCreateEvent {
    private Long restaurantId;
    private RestaurantCreateCommand command;
    private User createdBy;
}
