package com.astar.eattable.reservation.event;

import com.astar.eattable.reservation.command.ReservationCreateCommand;
import com.astar.eattable.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReservationCreateEvent {
    private Long reservationId;

    private ReservationCreateCommand command;

    private String restaurantName;

    private User user;
}
