package com.astar.eattable.restaurant.exception;

public class MenuNotFoundException extends RuntimeException {
    public MenuNotFoundException(Long menuId) {
        super("메뉴를 찾을 수 없습니다. ID: " + menuId);
    }
}
