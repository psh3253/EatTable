package com.astar.eattable.restaurant.event;

import com.astar.eattable.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestaurantDeleteEvent {
    private Long restaurantId;
    private User user;
}
