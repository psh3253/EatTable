package com.astar.eattable.restaurant.listener;

import com.astar.eattable.common.dto.EventTypes;
import com.astar.eattable.restaurant.event.ExternalRestaurantEvent;
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
        ExternalRestaurantEvent externalRestaurantEvent = objectMapper.readValue(message, ExternalRestaurantEvent.class);
        handleRestaurantEvent(externalRestaurantEvent);
        ack.acknowledge();
    }

    public void handleRestaurantEvent(ExternalRestaurantEvent message) {
        switch (message.getEventType()) {
            case EventTypes.RESTAURANT_CREATED:
                restaurantQueryService.createRestaurant(message.getRestaurantId());
                break;
            case EventTypes.RESTAURANT_DELETED:
                restaurantQueryService.deleteRestaurant(message.getRestaurantId());
                break;
            case EventTypes.RESTAURANT_UPDATED:
                restaurantQueryService.updateRestaurant(message.getRestaurantId());
                break;
            case EventTypes.BUSINESS_HOURS_UPDATED:
                restaurantQueryService.updateBusinessHours(message.getRestaurantId());
                break;
            case EventTypes.MENU_SECTION_CREATED:
            case EventTypes.MENU_SECTION_DELETED:
            case EventTypes.MENU_SECTION_UPDATED:
                restaurantQueryService.updateMenu(message.getRestaurantId());
                break;
        }
    }
}
