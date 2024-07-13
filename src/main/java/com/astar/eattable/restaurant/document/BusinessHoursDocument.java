package com.astar.eattable.restaurant.document;

import com.astar.eattable.restaurant.dto.Day;
import com.astar.eattable.restaurant.model.BusinessHours;
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

    public BusinessHoursDocument(BusinessHours businessHours) {
        this.day = businessHours.getDay();
        this.startTime = businessHours.getStartTime().toString();
        this.endTime = businessHours.getEndTime().toString();
        if (businessHours.getBreakStartTime() != null) {
            this.breakStartTime = businessHours.getBreakStartTime().toString();
        }
        if (businessHours.getBreakEndTime() != null) {
            this.breakEndTime = businessHours.getBreakEndTime().toString();
        }
        if (businessHours.getLastOrderTime() != null) {
            this.lastOrderTime = businessHours.getLastOrderTime().toString();
        }
    }
}
