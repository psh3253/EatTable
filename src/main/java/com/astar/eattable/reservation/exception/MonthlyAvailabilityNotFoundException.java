package com.astar.eattable.reservation.exception;

public class MonthlyAvailabilityNotFoundException extends RuntimeException {
    public MonthlyAvailabilityNotFoundException(Long restaurantId, Integer year, Integer month) {
        super("예약 가능 날짜를 찾을 수 없습니다. 식당 ID: " + restaurantId + ", 년: " + year + ", 월: " + month);
    }
}
