package com.astar.eattable.reservation.controller;

import com.astar.eattable.reservation.command.ReservationCreateCommand;
import com.astar.eattable.reservation.dto.MonthlyAvailabilityDTO;
import com.astar.eattable.reservation.dto.ReservationDetailsDTO;
import com.astar.eattable.reservation.dto.ReservationListDTO;
import com.astar.eattable.reservation.dto.TableAvailabilityDTO;
import com.astar.eattable.reservation.service.ReservationCommandService;
import com.astar.eattable.reservation.service.ReservationQueryService;
import com.astar.eattable.security.CurrentUser;
import com.astar.eattable.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@RestController
public class ReservationController {
    private final ReservationQueryService reservationQueryService;
    private final ReservationCommandService reservationCommandService;

    @GetMapping("/monthly-availability")
    public ResponseEntity<MonthlyAvailabilityDTO> getMonthlyAvailability(@RequestParam Long restaurantId, @RequestParam Integer year, @RequestParam Integer month) {
        return ResponseEntity.ok(reservationQueryService.getMonthlyAvailability(restaurantId, year, month));
    }

    @GetMapping("/table-availability")
    public ResponseEntity<List<TableAvailabilityDTO>> getTableAvailability(@RequestParam Long restaurantId, @RequestParam String date, @RequestParam Integer capacity) {
        return ResponseEntity.ok(reservationQueryService.getTableAvailability(restaurantId, date, capacity));
    }

    @PostMapping()
    public ResponseEntity<Void> createReservation(@RequestBody ReservationCreateCommand command, @CurrentUser User currentUser) {
        reservationCommandService.createReservation(command, currentUser);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId, @CurrentUser User currentUser) {
        reservationCommandService.cancelReservation(reservationId, currentUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-reservations")
    public ResponseEntity<List<ReservationListDTO>> getMyReservations(
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @CurrentUser User currentUser
    ) {
        return ResponseEntity.ok(reservationQueryService.getMyReservations(restaurantId, startDate, endDate, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<ReservationListDTO>> getReservations(
            @RequestParam Long restaurantId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long userId,
            @CurrentUser User currentUser
    ) {
        return ResponseEntity.ok(reservationQueryService.getReservations(restaurantId, startDate, endDate, userId, currentUser));
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailsDTO> getMyReservation(@PathVariable Long reservationId, @CurrentUser User currentUser) {
        return ResponseEntity.ok(reservationQueryService.getReservation(reservationId, currentUser));
    }
}
