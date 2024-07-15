package com.astar.eattable.restaurant.document;

import com.astar.eattable.restaurant.model.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuDocument {
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private String imageUrl;

    public MenuDocument(Menu menu) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.description = menu.getDescription();
        this.price = menu.getPrice();
        this.imageUrl = menu.getImageUrl();
    }
}
