package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.command.BusinessHoursUpdateCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class BusinessHoursUpdateEventPayload {
    private Long restaurantId;
    private BusinessHoursUpdateCommand command;

    public static BusinessHoursUpdateEventPayload from(Long restaurantId, BusinessHoursUpdateCommand command) {
        return new BusinessHoursUpdateEventPayload(restaurantId, command);
    }
}
