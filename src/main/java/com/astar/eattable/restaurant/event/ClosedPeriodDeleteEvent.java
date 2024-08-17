package com.astar.eattable.restaurant.event;

import com.astar.eattable.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ClosedPeriodDeleteEvent {
    private Long restaurantId;
    private Long closedPeriodId;
    private User user;
}
