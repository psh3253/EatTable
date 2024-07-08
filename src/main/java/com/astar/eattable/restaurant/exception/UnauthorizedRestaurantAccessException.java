package com.astar.eattable.restaurant.exception;

public class UnauthorizedRestaurantAccessException extends RuntimeException {
    public UnauthorizedRestaurantAccessException(Long restaurantId, Long userId) {
        super("사용자 " + userId + "는 식당 " + restaurantId + "에 대한 권한이 없습니다.");
    }
}
