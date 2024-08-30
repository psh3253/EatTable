package com.astar.eattable.reservation.payload;

import com.astar.eattable.reservation.event.ReservationCancelEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class ReservationCancelEventPayload {
    private Long reservationId;

    public static ReservationCancelEventPayload from(ReservationCancelEvent event) {
        return new ReservationCancelEventPayload(event.getReservationId());
    }
}
