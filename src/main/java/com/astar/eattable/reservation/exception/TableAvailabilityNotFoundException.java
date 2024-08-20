package com.astar.eattable.reservation.exception;

public class TableAvailabilityNotFoundException extends RuntimeException {
    public TableAvailabilityNotFoundException(Long restaurantId, String date, Integer capacity) {
        super("테이블 가용성 정보를 찾을 수 없습니다. 식당 ID: " + restaurantId + ", 날짜: " + date + ", 수용 인원: " + capacity);
    }

    public TableAvailabilityNotFoundException(Long restaurantId, String date, String time, Integer capacity) {
        super("테이블 가용성 정보를 찾을 수 없습니다. 식당 ID: " + restaurantId + ", 날짜: " + date + ", 시간: " + time + ", 수용 인원: " + capacity);
    }
}
