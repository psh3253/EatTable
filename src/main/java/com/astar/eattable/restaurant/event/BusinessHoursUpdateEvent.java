package com.astar.eattable.restaurant.event;

import com.astar.eattable.restaurant.command.BusinessHoursUpdateCommand;
import com.astar.eattable.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BusinessHoursUpdateEvent {
    private Long restaurantId;
    private BusinessHoursUpdateCommand command;
    private User user;
}
