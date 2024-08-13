package com.astar.eattable.reservation.model;

import com.astar.eattable.restaurant.model.Restaurant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer capacity;

    @NotNull
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Builder
    public RestaurantTable(Integer capacity, Integer count, Restaurant restaurant) {
        this.capacity = capacity;
        this.count = count;
        this.restaurant = restaurant;
    }
}
