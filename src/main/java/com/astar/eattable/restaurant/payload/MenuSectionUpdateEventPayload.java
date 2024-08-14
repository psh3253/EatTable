package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.command.MenuSectionUpdateCommand;
import com.astar.eattable.restaurant.event.MenuSectionUpdateEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class MenuSectionUpdateEventPayload {
    private Long restaurantId;
    private Long menuSectionId;
    private MenuSectionUpdateCommand command;

    public static MenuSectionUpdateEventPayload from(MenuSectionUpdateEvent event) {
        return new MenuSectionUpdateEventPayload(event.getRestaurantId(), event.getMenuSectionId(), event.getCommand());
    }
}
