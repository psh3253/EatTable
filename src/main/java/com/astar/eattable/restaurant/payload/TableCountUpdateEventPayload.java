package com.astar.eattable.restaurant.payload;

import com.astar.eattable.reservation.command.TableCountUpdateCommand;
import com.astar.eattable.restaurant.event.TableCountUpdateEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class TableCountUpdateEventPayload {
    private Long restaurantId;

    private TableCountUpdateCommand command;

    public static TableCountUpdateEventPayload from(TableCountUpdateEvent event) {
        return new TableCountUpdateEventPayload(event.getRestaurantId(), event.getCommand());
    }
}
