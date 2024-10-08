package com.astar.eattable.reservation.model;

import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.restaurant.model.RestaurantTable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "table_availability", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"date", "start_time", "restaurant_id", "restaurant_table_id"})
})
public class TableAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "restaurant_table_id")
    private RestaurantTable restaurantTable;

    private Integer remainingTableCount;

    @Builder
    public TableAvailability(LocalDate date, LocalTime startTime, LocalTime endTime, Restaurant restaurant, RestaurantTable restaurantTable, Integer remainingTableCount) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.restaurant = restaurant;
        this.restaurantTable = restaurantTable;
        this.remainingTableCount = remainingTableCount;
    }

    public void updateRemainingTableCount(Integer count) {
        this.remainingTableCount = count;
    }

    public void decreaseRemainingTableCount() {
        this.remainingTableCount--;
    }

    public void increaseRemainingTableCount() {
        this.remainingTableCount++;
    }
}
