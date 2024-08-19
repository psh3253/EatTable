package com.astar.eattable.reservation.controller;

import com.astar.eattable.reservation.dto.MonthlyAvailabilityDTO;
import com.astar.eattable.reservation.dto.TableAvailabilityDTO;
import com.astar.eattable.reservation.service.ReservationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@RestController
public class ReservationController {
    private final ReservationQueryService reservationQueryService;

    @GetMapping("/monthly-availability")
    public ResponseEntity<MonthlyAvailabilityDTO> getMonthlyAvailability(@RequestParam Long restaurantId, @RequestParam Integer year, @RequestParam Integer month) {
        return ResponseEntity.ok(reservationQueryService.getMonthlyAvailability(restaurantId, year, month));
    }

    @GetMapping("/table-availability")
    public ResponseEntity<List<TableAvailabilityDTO>> getTableAvailability(@RequestParam Long restaurantId, @RequestParam String date, @RequestParam Integer capacity) {
        return ResponseEntity.ok(reservationQueryService.getTableAvailability(restaurantId, date, capacity));
    }
}
