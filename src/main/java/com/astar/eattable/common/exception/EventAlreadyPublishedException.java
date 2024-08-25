package com.astar.eattable.common.exception;

import java.util.UUID;

public class EventAlreadyPublishedException extends RuntimeException {
    public EventAlreadyPublishedException(UUID eventId) {
        super("이벤트가 이미 발행되었습니다. ID: " + eventId);
    }
}
