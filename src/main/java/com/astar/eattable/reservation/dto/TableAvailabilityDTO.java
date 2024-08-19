package com.astar.eattable.reservation.dto;

import com.astar.eattable.reservation.document.TimeAvailabilityDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TableAvailabilityDTO {
    private final String time;
    private final Integer remainCount;

    public TableAvailabilityDTO(TimeAvailabilityDocument document) {
        this.time = document.getTime();
        this.remainCount = document.getRemainCount();
    }
}
