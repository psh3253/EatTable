package com.astar.eattable.reservation.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeAvailabilityDocument {
    private String time;

    private Integer remainCount;
}
