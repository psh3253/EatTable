package com.astar.eattable.restaurant.listener;

import com.astar.eattable.common.dto.EventTypes;
import com.astar.eattable.common.model.ExternalEvent;
import com.astar.eattable.common.service.EventService;
import com.astar.eattable.restaurant.event.*;
import com.astar.eattable.restaurant.payload.*;
import com.astar.eattable.restaurant.service.RestaurantCommandService;
import com.astar.eattable.restaurant.service.RestaurantQueryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class RestaurantEventListener {
    private final RestaurantQueryService restaurantQueryService;
    private final RestaurantCommandService restaurantCommandService;
    private final EventService eventService;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleRestaurantCreateEvent(RestaurantCreateEvent event) throws JsonProcessingException {
        restaurantCommandService.initRestaurantTable(event.getRestaurantId());
        eventService.saveExternalEvent(EventTypes.RESTAURANT_CREATED, event.getRestaurantId(), objectMapper.writeValueAsString(RestaurantCreateEventPayload.from(event)), event.getUser());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleRestaurantDeleteEvent(RestaurantDeleteEvent event) throws JsonProcessingException {
        eventService.saveExternalEvent(EventTypes.RESTAURANT_DELETED, event.getRestaurantId(), objectMapper.writeValueAsString(RestaurantDeleteEventPayload.from(event)), event.getUser());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleRestaurantUpdateEvent(RestaurantUpdateEvent event) throws JsonProcessingException {
        eventService.saveExternalEvent(EventTypes.RESTAURANT_UPDATED, event.getRestaurantId(), objectMapper.writeValueAsString(RestaurantUpdateEventPayload.from(event)), event.getUser());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleBusinessHoursUpdateEvent(BusinessHoursUpdateEvent event) throws JsonProcessingException {
        eventService.saveExternalEvent(EventTypes.BUSINESS_HOURS_UPDATED, event.getRestaurantId(), objectMapper.writeValueAsString(BusinessHoursUpdateEventPayload.from(event)), event.getUser());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleMenuSectionCreateEvent(MenuSectionCreateEvent event) throws JsonProcessingException {
        eventService.saveExternalEvent(EventTypes.MENU_SECTION_CREATED, event.getRestaurantId(), objectMapper.writeValueAsString(MenuSectionCreateEventPayload.from(event)), event.getUser());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleMenuSectionDeleteEvent(MenuSectionDeleteEvent event) throws JsonProcessingException {
        eventService.saveExternalEvent(EventTypes.MENU_SECTION_DELETED, event.getRestaurantId(), objectMapper.writeValueAsString(MenuSectionDeleteEventPayload.from(event)), event.getUser());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleMenuSectionUpdateEvent(MenuSectionUpdateEvent event) throws JsonProcessingException {
        eventService.saveExternalEvent(EventTypes.MENU_SECTION_UPDATED, event.getRestaurantId(), objectMapper.writeValueAsString(MenuSectionUpdateEventPayload.from(event)), event.getUser());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleMenuCreateEvent(MenuCreateEvent event) throws JsonProcessingException {
        eventService.saveExternalEvent(EventTypes.MENU_CREATED, event.getRestaurantId(), objectMapper.writeValueAsString(MenuCreateEventPayload.from(event)), event.getUser());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleMenuDeleteEvent(MenuDeleteEvent event) throws JsonProcessingException {
        eventService.saveExternalEvent(EventTypes.MENU_DELETED, event.getRestaurantId(), objectMapper.writeValueAsString(MenuDeleteEventPayload.from(event)), event.getUser());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleMenuUpdateEvent(MenuUpdateEvent event) throws JsonProcessingException {
        eventService.saveExternalEvent(EventTypes.MENU_UPDATED, event.getRestaurantId(), objectMapper.writeValueAsString(MenuUpdateEventPayload.from(event)), event.getUser());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleClosedPeriodCreateEvent(ClosedPeriodCreateEvent event) throws JsonProcessingException {
        eventService.saveExternalEvent(EventTypes.CLOSED_PERIOD_CREATED, event.getRestaurantId(), objectMapper.writeValueAsString(ClosedPeriodCreateEventPayload.from(event)), event.getUser());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleClosedPeriodDeleteEvent(ClosedPeriodDeleteEvent event) throws JsonProcessingException {
        eventService.saveExternalEvent(EventTypes.CLOSED_PERIOD_DELETED, event.getRestaurantId(), objectMapper.writeValueAsString(ClosedPeriodDeleteEventPayload.from(event)), event.getUser());
    }

    @KafkaListener(topics = "restaurant-events", groupId = "restaurant-service")
    public void listenRestaurantEvents(@Payload String message, Acknowledgment ack) throws JsonProcessingException {
        ExternalEvent externalRestaurantEvent = objectMapper.readValue(message, ExternalEvent.class);
        eventService.checkExternalEvent(externalRestaurantEvent.getEventId(), "restaurant", ack);
        handleRestaurantEvent(externalRestaurantEvent);
        ack.acknowledge();
    }

    public void handleRestaurantEvent(ExternalEvent message) throws JsonProcessingException {
        switch (message.getEventType()) {
            case EventTypes.RESTAURANT_CREATED:
                RestaurantCreateEventPayload restaurantCreateEventPayload = objectMapper.readValue(message.getPayload(), RestaurantCreateEventPayload.class);
                restaurantQueryService.createRestaurant(restaurantCreateEventPayload);
                restaurantQueryService.initMenuSections(restaurantCreateEventPayload.getRestaurantId());
                break;
            case EventTypes.RESTAURANT_DELETED:
                restaurantQueryService.deleteRestaurant(objectMapper.readValue(message.getPayload(), RestaurantDeleteEventPayload.class));
                break;
            case EventTypes.RESTAURANT_UPDATED:
                restaurantQueryService.updateRestaurant(objectMapper.readValue(message.getPayload(), RestaurantUpdateEventPayload.class));
                break;
            case EventTypes.BUSINESS_HOURS_UPDATED:
                restaurantQueryService.updateBusinessHours(objectMapper.readValue(message.getPayload(), BusinessHoursUpdateEventPayload.class));
                break;
            case EventTypes.MENU_SECTION_CREATED:
                restaurantQueryService.createMenuSection(objectMapper.readValue(message.getPayload(), MenuSectionCreateEventPayload.class));
                break;
            case EventTypes.MENU_SECTION_DELETED:
                restaurantQueryService.deleteMenuSection(objectMapper.readValue(message.getPayload(), MenuSectionDeleteEventPayload.class));
                break;
            case EventTypes.MENU_SECTION_UPDATED:
                restaurantQueryService.updateMenuSection(objectMapper.readValue(message.getPayload(), MenuSectionUpdateEventPayload.class));
                break;
            case EventTypes.MENU_CREATED:
                restaurantQueryService.createMenu(objectMapper.readValue(message.getPayload(), MenuCreateEventPayload.class));
                break;
            case EventTypes.MENU_DELETED:
                restaurantQueryService.deleteMenu(objectMapper.readValue(message.getPayload(), MenuDeleteEventPayload.class));
                break;
            case EventTypes.MENU_UPDATED:
                restaurantQueryService.updateMenu(objectMapper.readValue(message.getPayload(), MenuUpdateEventPayload.class));
                break;
            case EventTypes.CLOSED_PERIOD_CREATED:
                restaurantQueryService.createClosedPeriod(objectMapper.readValue(message.getPayload(), ClosedPeriodCreateEventPayload.class));
                break;
            case EventTypes.CLOSED_PERIOD_DELETED:
                restaurantQueryService.deleteClosedPeriod(objectMapper.readValue(message.getPayload(), ClosedPeriodDeleteEventPayload.class));
                break;
        }
    }
}
