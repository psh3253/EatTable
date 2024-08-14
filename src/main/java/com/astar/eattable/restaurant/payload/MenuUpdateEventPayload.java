package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.command.MenuUpdateCommand;
import com.astar.eattable.restaurant.event.MenuUpdateEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class MenuUpdateEventPayload {
    private Long restaurantId;
    private Long menuSectionId;
    private Long menuId;
    private MenuUpdateCommand command;

    public static MenuUpdateEventPayload from(MenuUpdateEvent event) {
        return new MenuUpdateEventPayload(event.getRestaurantId(), event.getMenuSectionId(), event.getMenuId(), event.getCommand());
    }
}
