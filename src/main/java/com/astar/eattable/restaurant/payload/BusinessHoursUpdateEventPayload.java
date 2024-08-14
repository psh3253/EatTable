package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.command.BusinessHoursUpdateCommand;
import com.astar.eattable.restaurant.event.BusinessHoursUpdateEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class BusinessHoursUpdateEventPayload {
    private Long restaurantId;
    private BusinessHoursUpdateCommand command;

    public static BusinessHoursUpdateEventPayload from(BusinessHoursUpdateEvent event) {
        return new BusinessHoursUpdateEventPayload(event.getRestaurantId(), event.getCommand());
    }
}
