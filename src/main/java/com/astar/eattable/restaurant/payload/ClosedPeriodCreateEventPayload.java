package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.command.ClosedPeriodCreateCommand;
import com.astar.eattable.restaurant.event.ClosedPeriodCreateEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class ClosedPeriodCreateEventPayload {
    private Long restaurantId;
    private Long closedPeriodId;
    private ClosedPeriodCreateCommand command;

    public static ClosedPeriodCreateEventPayload from(ClosedPeriodCreateEvent event) {
        return new ClosedPeriodCreateEventPayload(event.getRestaurantId(), event.getClosedPeriodId(), event.getCommand());
    }
}
