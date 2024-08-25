package com.astar.eattable.reservation.exception;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(Long reservationId) {
        super("예약 정보를 찾을 수 없습니다. 예약 ID: " + reservationId);
    }
}
