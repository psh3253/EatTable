package com.astar.eattable.restaurant.event;

import com.astar.eattable.restaurant.command.ClosedPeriodCreateCommand;
import com.astar.eattable.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ClosedPeriodCreateEvent {
    private Long restaurantId;
    private Long closedPeriodId;
    private ClosedPeriodCreateCommand command;
    private User user;
}
