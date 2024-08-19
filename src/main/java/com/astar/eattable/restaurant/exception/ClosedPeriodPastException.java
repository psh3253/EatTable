package com.astar.eattable.restaurant.exception;

public class ClosedPeriodPastException extends RuntimeException {
    public ClosedPeriodPastException(String startDate) {
        super("오늘 이전 날짜는 입력할 수 없습니다. 시작 날짜: " + startDate);
    }
}
