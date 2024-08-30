package com.astar.eattable.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReservationSearchCondition {
    private Long restaurantId;
    private String startDate;
    private String endDate;
    private Long userId;
}
