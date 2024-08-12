package com.astar.eattable.restaurant.exception;

public class UnauthorizedRestaurantAccessException extends RuntimeException {
    public UnauthorizedRestaurantAccessException(Long restaurantId, Long userId) {
        super("해당 식당에 대한 권한이 없습니다. 식당 ID: " + restaurantId + ", 사용자 ID: " + userId);
    }
}
