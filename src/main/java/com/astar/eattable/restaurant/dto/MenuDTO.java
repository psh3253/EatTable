package com.astar.eattable.restaurant.dto;

import com.astar.eattable.restaurant.document.MenuDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MenuDTO {
    private final String name;
    private final String description;
    private final Integer price;
    private final String imageUrl;

    public MenuDTO(MenuDocument document) {
        this.name = document.getName();
        this.description = document.getDescription();
        this.price = document.getPrice();
        this.imageUrl = document.getImageUrl();
    }
}
