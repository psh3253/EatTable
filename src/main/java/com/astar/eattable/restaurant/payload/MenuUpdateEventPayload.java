package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.command.MenuUpdateCommand;
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

    public static MenuUpdateEventPayload from(Long restaurantId, Long menuSectionId, Long menuId, MenuUpdateCommand command) {
        return new MenuUpdateEventPayload(restaurantId, menuSectionId, menuId, command);
    }
}
