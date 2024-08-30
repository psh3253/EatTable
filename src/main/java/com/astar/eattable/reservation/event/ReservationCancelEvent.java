package com.astar.eattable.reservation.event;

import com.astar.eattable.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReservationCancelEvent {
    private Long reservationId;

    private User user;
}
