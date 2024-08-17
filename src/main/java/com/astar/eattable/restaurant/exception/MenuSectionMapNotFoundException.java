package com.astar.eattable.restaurant.exception;

public class MenuSectionMapNotFoundException extends RuntimeException {
    // 한글로
    public MenuSectionMapNotFoundException(Long restaurantId) {
        super("메뉴 섹션 맵을 찾을 수 없습니다. 식당 ID: " + restaurantId);
    }
}
