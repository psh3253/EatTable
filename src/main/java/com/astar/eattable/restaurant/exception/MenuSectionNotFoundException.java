package com.astar.eattable.restaurant.exception;

public class MenuSectionNotFoundException extends RuntimeException {
    public MenuSectionNotFoundException(Long menuSectionId) {
        super("메뉴 섹션을 찾을 수 없습니다. ID: " + menuSectionId);
    }
}
