package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.command.MenuCreateCommand;
import com.astar.eattable.restaurant.event.MenuCreateEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class MenuCreateEventPayload {
    private Long restaurantId;

    private Long menuSectionId;

    private Long menuId;

    private MenuCreateCommand command;

    public static MenuCreateEventPayload from(MenuCreateEvent event) {
        return new MenuCreateEventPayload(event.getRestaurantId(), event.getMenuSectionId(), event.getMenuId(), event.getCommand());
    }
}
