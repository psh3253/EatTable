package com.astar.eattable.restaurant.event;

import com.astar.eattable.restaurant.command.MenuUpdateCommand;
import com.astar.eattable.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MenuUpdateEvent {
    private Long restaurantId;
    private Long menuSectionId;
    private Long menuId;
    private MenuUpdateCommand command;
    private User user;
}
