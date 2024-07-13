package com.astar.eattable.restaurant.model;

import com.astar.eattable.restaurant.dto.Day;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Entity
public class BusinessHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Day day;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;
    private LocalTime lastOrderTime;

    @Builder
    public BusinessHours(Restaurant restaurant, Day day, LocalTime startTime, LocalTime endTime, LocalTime breakStartTime, LocalTime breakEndTime, LocalTime lastOrderTime) {
        this.restaurant = restaurant;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
        this.lastOrderTime = lastOrderTime;
    }
}
