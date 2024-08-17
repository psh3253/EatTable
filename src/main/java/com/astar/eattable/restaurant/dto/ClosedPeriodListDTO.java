package com.astar.eattable.restaurant.dto;

import com.astar.eattable.restaurant.document.ClosedPeriodDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClosedPeriodListDTO {
    private final Long closedPeriodId;
    private final String startDate;
    private final String endDate;
    private final String reason;

    public ClosedPeriodListDTO(ClosedPeriodDocument document) {
        this.closedPeriodId = document.getId();
        this.startDate = document.getStartDate();
        this.endDate = document.getEndDate();
        this.reason = document.getReason();
    }
}
