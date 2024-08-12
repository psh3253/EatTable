package com.astar.eattable.restaurant.exception;

public class MenuSectionAlreadyExistsException extends RuntimeException {
    public MenuSectionAlreadyExistsException(Long restaurantId, String name) {
        super("메뉴 섹션이 이미 존재합니다. 식당 ID: " + restaurantId + ", 메뉴 섹션 이름: " + name);
    }
}
