package com.astar.eattable.restaurant.command;

import com.astar.eattable.restaurant.model.ClosedPeriod;
import com.astar.eattable.restaurant.model.Restaurant;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
public class ClosedPeriodCreateCommand {
    @NotNull
    private String startDate;

    @NotNull
    private String endDate;

    @Size(min = 1, max = 100, message = "휴무 사유는 1자 이상 100자 이하로 입력해주세요.")
    private String reason;

    public ClosedPeriod toEntity(Restaurant restaurant) {
        return ClosedPeriod.builder()
                .startDate(LocalDate.parse(startDate))
                .endDate(LocalDate.parse(endDate))
                .reason(reason)
                .restaurant(restaurant)
                .build();
    }
}
