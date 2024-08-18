package com.astar.eattable.reservation.dto;

import com.astar.eattable.reservation.document.MonthlyAvailabilityDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.util.List;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MonthlyAvailabilityDTO {
    private final List<String> availableDates;

    public MonthlyAvailabilityDTO(MonthlyAvailabilityDocument document) {
        this.availableDates = document.getAvailableDates();
    }
}
