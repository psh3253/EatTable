package com.astar.eattable.restaurant.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MenuSectionDeleteEventPayload {
    private Long restaurantId;
    private Long menuSectionId;

    public static MenuSectionDeleteEventPayload from(Long restaurantId, Long menuSectionId) {
        return new MenuSectionDeleteEventPayload(restaurantId, menuSectionId);
    }
}
