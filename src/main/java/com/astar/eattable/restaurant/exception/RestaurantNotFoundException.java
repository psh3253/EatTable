package com.astar.eattable.restaurant.exception;

public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(Long id) {
        super("식당 " + id + "을 찾을 수 없습니다.");
    }
}
