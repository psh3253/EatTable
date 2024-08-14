package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.command.MenuSectionCreateCommand;
import com.astar.eattable.restaurant.event.MenuSectionCreateEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MenuSectionCreateEventPayload {
    private Long restaurantId;
    private Long menuSectionId;
    private MenuSectionCreateCommand command;

    public static MenuSectionCreateEventPayload from(MenuSectionCreateEvent event) {
        return new MenuSectionCreateEventPayload(event.getRestaurantId(), event.getMenuSectionId(), event.getCommand());
    }
}
