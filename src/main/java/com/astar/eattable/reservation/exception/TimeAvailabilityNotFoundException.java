package com.astar.eattable.reservation.exception;

public class TimeAvailabilityNotFoundException extends RuntimeException {
    public TimeAvailabilityNotFoundException(Long restaurantId, String date, String time, Integer capacity) {
        super("시간 가용성을 찾을 수 없습니다. 식당 ID: " + restaurantId + ", 날짜: " + date + ", 시간: " + time + ", 수용 인원: " + capacity);
    }
}
