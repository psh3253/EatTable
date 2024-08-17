package com.astar.eattable.restaurant.exception;

import com.astar.eattable.restaurant.command.ClosedPeriodCreateCommand;

public class ClosedPeriodOverlapException extends RuntimeException{
    public ClosedPeriodOverlapException(Long restaurantId, String startDateTime, String endDateTime) {
        super("이미 등록된 휴무 기간과 중복됩니다. 식당 ID: " + restaurantId + ", 시작 시간: " + startDateTime + ", 종료 시간: " + endDateTime);
    }
}
