package com.astar.eattable.reservation.event;

import com.astar.eattable.reservation.command.TableCountUpdateCommand;
import com.astar.eattable.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TableCountUpdateEvent {
    private Long restaurantId;
    private TableCountUpdateCommand command;
    private User user;
}
