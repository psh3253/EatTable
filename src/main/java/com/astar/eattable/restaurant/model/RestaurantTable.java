package com.astar.eattable.restaurant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "restaurant_table")
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

    public void updateCount(Integer count) {
        this.count = count;
    }

    public void setIdForTest(Long id) {
        this.id = id;
    }
}
