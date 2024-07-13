package com.astar.eattable.restaurant.exception;

public class BusinessHoursNotFoundException extends RuntimeException {
    public BusinessHoursNotFoundException(Long restaurantId, String day) {
        super("식당 " + restaurantId + "에 대한 " + day + "의 영업시간이 존재하지 않습니다.");
    }
}
