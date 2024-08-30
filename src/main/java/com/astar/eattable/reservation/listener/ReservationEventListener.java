package com.astar.eattable.reservation.listener;

import com.astar.eattable.common.dto.EventTypes;
import com.astar.eattable.common.model.ExternalEvent;
import com.astar.eattable.common.service.EventService;
import com.astar.eattable.reservation.event.ReservationCancelEvent;
import com.astar.eattable.reservation.event.ReservationCreateEvent;
import com.astar.eattable.reservation.event.TableCountUpdateEvent;
import com.astar.eattable.reservation.payload.ReservationCancelEventPayload;
import com.astar.eattable.reservation.payload.ReservationCreateEventPayload;
import com.astar.eattable.reservation.payload.TableCountUpdateEventPayload;
import com.astar.eattable.reservation.service.ReservationCommandService;
import com.astar.eattable.reservation.service.ReservationQueryService;
import com.astar.eattable.restaurant.event.RestaurantCreateEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ReservationEventListener {
    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;
    private final EventService eventService;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleRestaurantCreateEvent(RestaurantCreateEvent event) {
        reservationCommandService.initRestaurantTable(event.getRestaurantId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRestaurantCreateEventAsync(RestaurantCreateEvent event) {
        reservationCommandService.createTableAvailabilities(event.getRestaurantId());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleTableCountUpdateEvent(TableCountUpdateEvent event) throws JsonProcessingException {
        eventService.saveExternalEvent(EventTypes.TABLE_COUNT_UPDATED, objectMapper.writeValueAsString(TableCountUpdateEventPayload.from(event)), event.getUser());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTableCountUpdateEventAsync(TableCountUpdateEvent event) {
        reservationCommandService.updateTableAvailability(event.getRestaurantId(), event.getCommand().getCapacity());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleReservationCreateEvent(ReservationCreateEvent event) throws JsonProcessingException {
        eventService.saveExternalEvent(EventTypes.RESERVATION_CREATED, objectMapper.writeValueAsString(ReservationCreateEventPayload.from(event)), event.getUser());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleReservationCancelEvent(ReservationCancelEvent event) throws JsonProcessingException {
        eventService.saveExternalEvent(EventTypes.RESERVATION_CANCELLED, objectMapper.writeValueAsString(ReservationCancelEventPayload.from(event)), event.getUser());
    }

    @KafkaListener(topics = "reservation-events", groupId = "reservation-service")
    public void listenReservationEvents(@Payload String message, Acknowledgment ack) throws JsonProcessingException {
        ExternalEvent externalReservationEvent = objectMapper.readValue(message, ExternalEvent.class);
        eventService.checkExternalEvent(externalReservationEvent.getEventId(), ack);
        handleReservationEvent(externalReservationEvent);
        ack.acknowledge();
    }

    public void handleReservationEvent(ExternalEvent message) throws JsonProcessingException {
        switch (message.getEventType()) {
            case EventTypes.TABLE_COUNT_UPDATED:
                reservationQueryService.updateTableAvailability(objectMapper.readValue(message.getPayload(), TableCountUpdateEventPayload.class));
                break;
            case EventTypes.RESERVATION_CREATED:
                reservationQueryService.createReservation(objectMapper.readValue(message.getPayload(), ReservationCreateEventPayload.class));
                break;
            case EventTypes.RESERVATION_CANCELLED:
                reservationQueryService.cancelReservation(objectMapper.readValue(message.getPayload(), ReservationCancelEventPayload.class));
                break;
        }
    }
}
