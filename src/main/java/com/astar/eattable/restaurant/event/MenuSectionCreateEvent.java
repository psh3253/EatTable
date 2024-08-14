package com.astar.eattable.restaurant.event;

import com.astar.eattable.restaurant.command.MenuSectionCreateCommand;
import com.astar.eattable.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuSectionCreateEvent {
    private Long restaurantId;
    private Long menuSectionId;
    private MenuSectionCreateCommand command;
    private User user;
}
