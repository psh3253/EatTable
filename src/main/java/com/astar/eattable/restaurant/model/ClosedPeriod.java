package com.astar.eattable.restaurant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Entity
public class ClosedPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reason;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Builder
    public ClosedPeriod(String reason, LocalDate startDate, LocalDate endDate, Restaurant restaurant) {
        this.reason = reason;
        this.startDate = startDate;
        this.endDate = endDate;
        this.restaurant = restaurant;
    }

    public boolean isOverLap(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return !(this.endDate.isBefore(start) || this.startDate.isAfter(end));
    }

    public void setIdForTest(Long id) {
        this.id = id;
    }
}
