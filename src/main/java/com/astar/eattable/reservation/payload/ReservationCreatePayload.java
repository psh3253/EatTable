package com.astar.eattable.reservation.payload;

import com.astar.eattable.reservation.command.ReservationCreateCommand;
import com.astar.eattable.reservation.event.ReservationCreateEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class ReservationCreatePayload {
    private Long reservationId;

    private ReservationCreateCommand command;

    private String restaurantName;

    private Long userId;

    private String userNickname;

    public static ReservationCreatePayload from(ReservationCreateEvent event) {
        return new ReservationCreatePayload(event.getReservationId(), event.getCommand(), event.getRestaurantName(), event.getUser().getId(), event.getUser().getNickname());
    }
}
