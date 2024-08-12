package com.astar.eattable.restaurant.listener;

import com.astar.eattable.common.dto.EventTypes;
import com.astar.eattable.common.model.ExternalEvent;
import com.astar.eattable.restaurant.payload.*;
import com.astar.eattable.restaurant.service.RestaurantQueryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RestaurantEventListener {
    private final RestaurantQueryService restaurantQueryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "restaurant-events", groupId = "restaurant-service")
    public void listenRestaurantEvents(@Payload String message, Acknowledgment ack) throws JsonProcessingException {
        ExternalEvent externalRestaurantEvent = objectMapper.readValue(message, ExternalEvent.class);
        handleRestaurantEvent(externalRestaurantEvent);
        ack.acknowledge();
    }

    public void handleRestaurantEvent(ExternalEvent message) throws JsonProcessingException {
        switch (message.getEventType()) {
            case EventTypes.RESTAURANT_CREATED:
                restaurantQueryService.createRestaurant(objectMapper.readValue(message.getPayload(), RestaurantCreateEventPayload.class));
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
        }
    }
}
