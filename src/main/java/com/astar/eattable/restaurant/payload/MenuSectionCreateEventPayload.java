package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.command.MenuSectionCreateCommand;
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

    public static MenuSectionCreateEventPayload from(Long restaurantId, Long menuSectionId, MenuSectionCreateCommand command) {
        return new MenuSectionCreateEventPayload(restaurantId, menuSectionId, command);
    }
}
