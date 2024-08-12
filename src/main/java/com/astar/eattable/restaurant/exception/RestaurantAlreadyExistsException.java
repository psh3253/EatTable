package com.astar.eattable.restaurant.exception;

public class RestaurantAlreadyExistsException extends RuntimeException {
    public RestaurantAlreadyExistsException(String name, String address) {
        super("식당이 이미 존재합니다. 이름: " + name + ", 주소: " + address);
    }
}
