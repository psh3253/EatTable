package com.astar.eattable.reservation.model;

import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.user.model.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "reservation")
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "table_availability_id")
    private TableAvailability tableAvailability;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer capacity;

    private String request;

    private boolean canceled;

    @Builder
    public Reservation(Restaurant restaurant, TableAvailability tableAvailability, User user, Integer capacity, String request) {
        this.restaurant = restaurant;
        this.tableAvailability = tableAvailability;
        this.user = user;
        this.capacity = capacity;
        this.request = request;
        this.canceled = false;
    }

    public void cancel() {
        this.canceled = true;
    }
}
