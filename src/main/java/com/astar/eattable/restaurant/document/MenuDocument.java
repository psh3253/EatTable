package com.astar.eattable.restaurant.document;

import com.astar.eattable.restaurant.command.MenuCreateCommand;
import com.astar.eattable.restaurant.command.MenuUpdateCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuDocument {
    private String name;
    private String description;
    private Integer price;
    private String imageUrl;

    public MenuDocument(MenuCreateCommand command) {
        this.name = command.getName();
        this.description = command.getDescription();
        this.price = command.getPrice();
        this.imageUrl = command.getImageUrl();
    }

    public void update(MenuUpdateCommand command) {
        if (command.getName() != null) {
            this.name = command.getName();
        }
        if (command.getDescription() != null) {
            this.description = command.getDescription();
        }
        if (command.getPrice() != null) {
            this.price = command.getPrice();
        }
        if (command.getImageUrl() != null) {
            this.imageUrl = command.getImageUrl();
        }
    }
}
