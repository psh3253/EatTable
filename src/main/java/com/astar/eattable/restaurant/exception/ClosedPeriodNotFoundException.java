package com.astar.eattable.restaurant.exception;

public class ClosedPeriodNotFoundException extends RuntimeException {
    public ClosedPeriodNotFoundException(Long closedPeriodId) {
        super("휴무 기간을 찾을 수 없습니다. ID: " + closedPeriodId);
    }
}
