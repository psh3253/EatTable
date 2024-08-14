package com.astar.eattable.restaurant.event;

import com.astar.eattable.restaurant.command.MenuSectionUpdateCommand;
import com.astar.eattable.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuSectionUpdateEvent {
    private Long restaurantId;
    private Long menuSectionId;
    private MenuSectionUpdateCommand command;
    private User user;
}
