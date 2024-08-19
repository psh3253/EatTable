package com.astar.eattable.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@RequiredArgsConstructor
@Service
public class CommonService {
    public boolean isBreakTime(LocalTime startTime, LocalTime breakStartTime, LocalTime breakEndTime) {
        if (breakStartTime != null && breakEndTime != null) {
            return startTime.isAfter(breakStartTime) && startTime.isBefore(breakEndTime) || startTime.equals(breakStartTime);
        }
        return false;
    }
}
