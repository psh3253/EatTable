package com.astar.eattable.restaurant.exception;

public class BusinessHoursNotFoundException extends RuntimeException {
    public BusinessHoursNotFoundException(Long restaurantId, String day) {
        super("영업 시간을 찾을 수 없습니다. 식당 ID: " + restaurantId + ", 요일: " + day);
    }
}
