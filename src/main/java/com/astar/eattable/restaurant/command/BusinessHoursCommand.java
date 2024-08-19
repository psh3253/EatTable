package com.astar.eattable.restaurant.command;

import com.astar.eattable.common.dto.Day;
import com.astar.eattable.restaurant.model.BusinessHours;
import com.astar.eattable.restaurant.model.Restaurant;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
public class BusinessHoursCommand {
    @NotNull
    private String day;

    @NotNull
    private String startTime;

    @NotNull
    private String endTime;

    private String breakStartTime;

    private String breakEndTime;

    private String lastOrderTime;

    public BusinessHours toEntity(Restaurant restaurant) {
        return BusinessHours.builder()
                .restaurant(restaurant)
                .day(Day.valueOf(day))
                .startTime(LocalTime.parse(startTime))
                .endTime(LocalTime.parse(endTime))
                .breakStartTime(breakStartTime != null ? LocalTime.parse(breakStartTime) : null)
                .breakEndTime(breakEndTime != null ? LocalTime.parse(breakEndTime) : null)
                .lastOrderTime(lastOrderTime != null ? LocalTime.parse(lastOrderTime) : null)
                .build();
    }
}
