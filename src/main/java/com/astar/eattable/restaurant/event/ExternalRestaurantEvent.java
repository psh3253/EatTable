package com.astar.eattable.restaurant.event;

import com.astar.eattable.common.dto.EventPayload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalRestaurantEvent extends EventPayload {
    private Long restaurantId;

    private String eventType;
}
