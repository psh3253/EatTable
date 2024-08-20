package com.astar.eattable.reservation.exception;

public class RestaurantTableNotFoundException extends RuntimeException {
    // 한글로
    public RestaurantTableNotFoundException(Long restaurantId, Integer capacity) {
        super("레스토랑 테이블 정보를 찾을 수 없습니다. 식당 ID: " + restaurantId + ", 수용 인원: " + capacity);
    }
}
