package com.astar.eattable.restaurant.exception;

import com.astar.eattable.common.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestaurantExceptionHandler {
    @ExceptionHandler({RestaurantAlreadyExistsException.class, MenuSectionAlreadyExistsException.class})
    public ResponseEntity<ErrorResponseDTO> handleRestaurantAlreadyExistsException(RuntimeException e) {
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler({RestaurantNotFoundException.class, BusinessHoursNotFoundException.class, MenuSectionNotFoundException.class})
    public ResponseEntity<ErrorResponseDTO> handleRestaurantNotFoundException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler({UnauthorizedRestaurantAccessException.class})
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedRestaurantAccessException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseDTO(HttpStatus.FORBIDDEN.value(), e.getMessage()));
    }
}
