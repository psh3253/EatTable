package com.astar.eattable.restaurant.exception;

import com.astar.eattable.common.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestaurantExceptionHandler {
    @ExceptionHandler({RestaurantAlreadyExistsException.class, MenuSectionAlreadyExistsException.class, MenuAlreadyExistsException.class, ClosedPeriodOverlapException.class, ClosedPeriodPastException.class})
    public ResponseEntity<ErrorResponseDTO> handleBadRequestException(RuntimeException e) {
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler({RestaurantNotFoundException.class, BusinessHoursNotFoundException.class, MenuSectionNotFoundException.class, MenuNotFoundException.class, MenuSectionMapNotFoundException.class, ClosedPeriodNotFoundException.class})
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler({UnauthorizedRestaurantAccessException.class})
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedRestaurantAccessException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseDTO(HttpStatus.FORBIDDEN.value(), e.getMessage()));
    }

    @ExceptionHandler({MenuSectionNotEmptyException.class})
    public ResponseEntity<ErrorResponseDTO> handleConflictException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDTO(HttpStatus.CONFLICT.value(), e.getMessage()));
    }
}
