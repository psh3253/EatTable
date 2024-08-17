package com.astar.eattable.restaurant.dto;

import com.astar.eattable.restaurant.document.BusinessHoursDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BusinessHoursDTO {
    private final String day;
    private final String startTime;
    private final String endTime;
    private final String breakStartTime;
    private final String breakEndTime;
    private final String lastOrderTime;

    public BusinessHoursDTO(BusinessHoursDocument document) {
        this.day = document.getDay().name();
        this.startTime = document.getStartTime();
        this.endTime = document.getEndTime();
        this.breakStartTime = document.getBreakStartTime();
        this.breakEndTime = document.getBreakEndTime();
        this.lastOrderTime = document.getLastOrderTime();
    }
}
