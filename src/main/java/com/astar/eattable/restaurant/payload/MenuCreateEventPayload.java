package com.astar.eattable.restaurant.payload;

import com.astar.eattable.restaurant.command.MenuCreateCommand;
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

    public static MenuCreateEventPayload from(Long restaurantId, Long menuSectionId, Long menuId, MenuCreateCommand command) {
        return new MenuCreateEventPayload(restaurantId, menuSectionId, menuId, command);
    }
}
