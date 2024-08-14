package com.astar.eattable.restaurant.event;

import com.astar.eattable.restaurant.command.MenuCreateCommand;
import com.astar.eattable.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MenuCreateEvent {
    private Long restaurantId;
    private Long menuSectionId;
    private Long menuId;
    private MenuCreateCommand command;
    private User user;
}
