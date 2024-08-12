package com.astar.eattable.restaurant.exception;

public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(Long id) {
        super("식당을 찾을 수 없습니다. ID: " + id);
    }
}
