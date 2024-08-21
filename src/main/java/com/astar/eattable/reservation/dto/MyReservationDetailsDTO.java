package com.astar.eattable.reservation.dto;

import com.astar.eattable.reservation.document.ReservationDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MyReservationDetailsDTO {
    private final Long reservationId;
    private final Long restaurantId;
    private final String restaurantName;
    private final String date;
    private final String time;
    private final Integer capacity;
    private final String request;

    public MyReservationDetailsDTO(ReservationDocument document) {
        this.reservationId = document.getId();
        this.restaurantId = document.getRestaurantId();
        this.restaurantName = document.getRestaurantName();
        this.date = document.getDate();
        this.time = document.getTime();
        this.capacity = document.getCapacity();
        this.request = document.getRequest();
    }
}

