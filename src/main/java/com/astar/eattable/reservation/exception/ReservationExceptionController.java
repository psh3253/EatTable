package com.astar.eattable.reservation.exception;

import com.astar.eattable.common.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReservationExceptionController {
    @ExceptionHandler({MonthlyAvailabilityNotFoundException.class, TableAvailabilityNotFoundException.class})
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }
}
