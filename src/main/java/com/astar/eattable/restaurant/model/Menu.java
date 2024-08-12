package com.astar.eattable.restaurant.model;

import com.astar.eattable.restaurant.command.MenuUpdateCommand;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private Integer price;

    private String imageUrl;

    @JoinColumn(name = "section_id")
    @ManyToOne
    private MenuSection menuSection;

    @JoinColumn(name = "restaurant_id")
    @ManyToOne
    private Restaurant restaurant;

    @Builder
    public Menu(String name, String description, Integer price, String imageUrl, MenuSection menuSection, Restaurant restaurant) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.menuSection = menuSection;
        this.restaurant = restaurant;
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
