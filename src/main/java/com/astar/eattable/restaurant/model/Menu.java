package com.astar.eattable.restaurant.model;

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
    private MenuSection section;

    @JoinColumn(name = "restaurant_id")
    @ManyToOne
    private Restaurant restaurant;

    @Builder
    public Menu(String name, String description, Integer price, String imageUrl, MenuSection section) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.section = section;
    }
}
