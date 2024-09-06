package com.astar.eattable.common.dto;

public class EventTypes {
    public static final String RESTAURANT_CREATED = "RestaurantCreated";
    public static final String RESTAURANT_DELETED = "RestaurantDeleted";
    public static final String RESTAURANT_UPDATED = "RestaurantUpdated";
    public static final String BUSINESS_HOURS_UPDATED = "BusinessHoursUpdated";
    public static final String MENU_SECTION_CREATED = "MenuSectionCreated";
    public static final String MENU_SECTION_DELETED = "MenuSectionDeleted";
    public static final String MENU_SECTION_UPDATED = "MenuSectionUpdated";
    public static final String MENU_CREATED = "MenuCreated";
    public static final String MENU_DELETED = "MenuDeleted";
    public static final String MENU_UPDATED = "MenuUpdated";
    public static final String CLOSED_PERIOD_CREATED = "ClosedPeriodCreated";
    public static final String CLOSED_PERIOD_DELETED = "ClosedPeriodDeleted";
    public static final String TABLE_COUNT_UPDATED = "TableCountUpdated";
    public static final String RESERVATION_CREATED = "ReservationCreated";
    public static final String RESERVATION_CANCELLED = "ReservationCancelled";

    public static String getTopic(String eventType) {
        return switch (eventType) {
            case RESTAURANT_CREATED, RESTAURANT_DELETED, RESTAURANT_UPDATED, BUSINESS_HOURS_UPDATED,
                 MENU_SECTION_CREATED, MENU_SECTION_DELETED, MENU_SECTION_UPDATED, MENU_CREATED, MENU_DELETED,
                 MENU_UPDATED, CLOSED_PERIOD_CREATED, CLOSED_PERIOD_DELETED, TABLE_COUNT_UPDATED -> "restaurant-events";
            case RESERVATION_CREATED, RESERVATION_CANCELLED -> "reservation-events";
            default -> throw new IllegalArgumentException("알 수 없는 이벤트 타입입니다.");
        };
    }
}
