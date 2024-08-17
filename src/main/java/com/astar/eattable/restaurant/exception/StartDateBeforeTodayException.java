package com.astar.eattable.restaurant.exception;

public class StartDateBeforeTodayException extends RuntimeException {
    public StartDateBeforeTodayException(String startDate) {
        super("오늘 이전의 날짜는 입력할 수 없습니다. 입력한 날짜: " + startDate);
    }
}
