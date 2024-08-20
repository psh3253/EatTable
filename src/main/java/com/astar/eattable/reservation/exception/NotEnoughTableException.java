package com.astar.eattable.reservation.exception;

public class NotEnoughTableException extends RuntimeException{
    public NotEnoughTableException(Long tableAvailabilityId) {
        super("테이블이 부족합니다. 테이블 가용성 ID: " + tableAvailabilityId);
    }
}
