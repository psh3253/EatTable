package com.astar.eattable.reservation.payload;

import com.astar.eattable.reservation.command.TableCountUpdateCommand;
import com.astar.eattable.reservation.event.TableCountUpdateEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class TableCountUpdatePayload {
    private Long restaurantId;

    private TableCountUpdateCommand command;

    public static TableCountUpdatePayload from(TableCountUpdateEvent event) {
        return new TableCountUpdatePayload(event.getRestaurantId(), event.getCommand());
    }
}
