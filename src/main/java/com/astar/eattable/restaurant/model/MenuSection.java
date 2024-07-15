package com.astar.eattable.restaurant.model;

import com.astar.eattable.restaurant.command.MenuSectionUpdateCommand;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class MenuSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @JoinColumn(name = "restaurant_id")
    @ManyToOne
    private Restaurant restaurant;

    @Builder
    public MenuSection(String name, Restaurant restaurant) {
        this.name = name;
        this.restaurant = restaurant;
    }

    public void update(MenuSectionUpdateCommand command) {
        this.name = command.getName();
    }
}
