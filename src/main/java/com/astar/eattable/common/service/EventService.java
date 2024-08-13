package com.astar.eattable.common.service;

import com.astar.eattable.common.model.ExternalEvent;
import com.astar.eattable.common.repository.ExternalEventRepository;
import com.astar.eattable.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final ExternalEventRepository externalEventRepository;

    @Transactional
    public void saveExternalEvent(String eventType, String payload, User createdBy) {
        ExternalEvent externalEvent = ExternalEvent.from(eventType, payload, createdBy);
        externalEventRepository.save(externalEvent);
    }
}
