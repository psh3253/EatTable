package com.astar.eattable.restaurant.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class MenuDeleteEventPayload {
    private Long restaurantId;

    private Long menuSectionId;

    private Long menuId;

    public static MenuDeleteEventPayload from(Long restaurantId, Long menuSectionId, Long menuId) {
        return new MenuDeleteEventPayload(restaurantId, menuSectionId, menuId);
    }
}
