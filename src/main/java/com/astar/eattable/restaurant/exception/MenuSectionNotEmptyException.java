package com.astar.eattable.restaurant.exception;

public class MenuSectionNotEmptyException extends RuntimeException {
    public MenuSectionNotEmptyException(Long menuSectionId) {
        super("메뉴 섹션에 메뉴가 존재합니다. 메뉴 섹션 ID: " + menuSectionId);
    }
}
