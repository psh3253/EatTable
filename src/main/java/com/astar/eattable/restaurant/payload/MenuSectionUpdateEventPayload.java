package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.command.MenuSectionUpdateCommand;
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

    public static MenuSectionUpdateEventPayload from(Long restaurantId, Long menuSectionId, MenuSectionUpdateCommand command) {
        return new MenuSectionUpdateEventPayload(restaurantId, menuSectionId, command);
    }
}
