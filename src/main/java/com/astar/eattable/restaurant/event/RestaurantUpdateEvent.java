package com.astar.eattable.restaurant.event;

import com.astar.eattable.restaurant.command.RestaurantUpdateCommand;
import com.astar.eattable.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestaurantUpdateEvent {
    private Long restaurantId;
    private RestaurantUpdateCommand command;
    private User user;
}
