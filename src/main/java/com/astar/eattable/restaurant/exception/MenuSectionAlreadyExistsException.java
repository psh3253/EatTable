package com.astar.eattable.restaurant.exception;

public class MenuSectionAlreadyExistsException extends RuntimeException{
    public MenuSectionAlreadyExistsException(Long restaurantId, String name) {
        super("이미 존재하는 메뉴 섹션입니다. restaurantId: " + restaurantId + ", name: " + name);
    }
}
