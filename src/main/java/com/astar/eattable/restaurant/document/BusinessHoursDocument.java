package com.astar.eattable.restaurant.document;

import com.astar.eattable.common.dto.Day;
import com.astar.eattable.restaurant.command.BusinessHoursCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessHoursDocument {
    private Day day;
    private String startTime;
    private String endTime;
    private String breakStartTime;
    private String breakEndTime;
    private String lastOrderTime;

    public BusinessHoursDocument(BusinessHoursCommand command) {
        this.day = Day.valueOf(command.getDay());
        this.startTime = command.getStartTime();
        this.endTime = command.getEndTime();
        this.breakStartTime = command.getBreakStartTime();
        this.breakEndTime = command.getBreakEndTime();
        this.lastOrderTime = command.getLastOrderTime();
    }
}
