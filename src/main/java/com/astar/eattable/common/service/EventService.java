package com.astar.eattable.common.service;

import com.astar.eattable.common.exception.EventAlreadyPublishedException;
import com.astar.eattable.common.exception.EventNotFoundException;
import com.astar.eattable.common.lock.DistributedLock;
import com.astar.eattable.common.model.ExternalEvent;
import com.astar.eattable.common.repository.ExternalEventRepository;
import com.astar.eattable.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final ExternalEventRepository externalEventRepository;

    @Transactional
    public void saveExternalEvent(String eventType, Long key, String payload, User user) {
        ExternalEvent externalEvent = ExternalEvent.from(eventType, key, payload, user);
        externalEventRepository.save(externalEvent);
    }

    @DistributedLock(key = "#eventId")
    public void checkExternalEvent(UUID eventId, String domain, Acknowledgment ack) {
        ExternalEvent externalEvent = externalEventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        if (externalEvent.isConsumed(domain)) {
            ack.acknowledge();
            throw new EventAlreadyPublishedException(eventId);
        }
        externalEvent.consumeEvent(domain);
    }
}
