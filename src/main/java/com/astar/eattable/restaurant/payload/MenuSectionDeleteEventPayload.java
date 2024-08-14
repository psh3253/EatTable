package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.event.MenuSectionDeleteEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MenuSectionDeleteEventPayload {
    private Long restaurantId;
    private Long menuSectionId;

    public static MenuSectionDeleteEventPayload from(MenuSectionDeleteEvent event) {
        return new MenuSectionDeleteEventPayload(event.getRestaurantId(), event.getMenuSectionId());
    }
}
