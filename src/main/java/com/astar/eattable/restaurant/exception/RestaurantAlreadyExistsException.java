package com.astar.eattable.restaurant.exception;

public class RestaurantAlreadyExistsException extends RuntimeException {
    public RestaurantAlreadyExistsException(String name, String address) {
        super("식당 이름: " + name + ", 주소: " + address + "은 이미 존재하는 식당입니다.");
    }
}
