package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.event.MenuDeleteEvent;
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

    public static MenuDeleteEventPayload from(MenuDeleteEvent event) {
        return new MenuDeleteEventPayload(event.getRestaurantId(), event.getMenuSectionId(), event.getMenuId());
    }
}
