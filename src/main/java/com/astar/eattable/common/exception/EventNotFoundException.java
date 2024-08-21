package com.astar.eattable.common.exception;

import java.util.UUID;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(UUID eventId) {
        super("이벤트를 찾을 수 없습니다. ID: " + eventId);
    }
}
