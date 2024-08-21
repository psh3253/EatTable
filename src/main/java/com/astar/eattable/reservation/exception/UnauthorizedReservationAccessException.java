package com.astar.eattable.reservation.exception;

public class UnauthorizedReservationAccessException extends RuntimeException {
    public UnauthorizedReservationAccessException(Long reservationId, Long userId) {
        super("예약 정보에 접근할 권한이 없습니다. 예약 ID: " + reservationId + ", 사용자 ID: " + userId);
    }
}
