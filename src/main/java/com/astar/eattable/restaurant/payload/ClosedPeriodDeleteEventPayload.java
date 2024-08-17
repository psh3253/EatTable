package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.event.ClosedPeriodDeleteEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class ClosedPeriodDeleteEventPayload {
    private Long restaurantId;
    private Long closedPeriodId;

    public static ClosedPeriodDeleteEventPayload from(ClosedPeriodDeleteEvent event) {
        return new ClosedPeriodDeleteEventPayload(event.getRestaurantId(), event.getClosedPeriodId());
    }
}
