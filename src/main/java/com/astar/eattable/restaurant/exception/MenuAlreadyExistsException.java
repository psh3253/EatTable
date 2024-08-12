package com.astar.eattable.restaurant.exception;

public class MenuAlreadyExistsException extends RuntimeException {
    public MenuAlreadyExistsException(Long menuSectionId, String name) {
        super("메뉴가 이미 존재합니다. 메뉴 섹션 ID: " + menuSectionId + ", 메뉴 이름: " + name);
    }
}
