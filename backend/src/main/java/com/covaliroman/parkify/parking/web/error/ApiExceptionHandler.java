package com.covaliroman.parkify.parking.web.error;

import com.covaliroman.parkify.parking.application.exception.ParkingFacilityNotFoundException;
import com.covaliroman.parkify.parking.application.exception.ParkingLevelCodeAlreadyExistsException;
import com.covaliroman.parkify.parking.application.exception.ParkingLevelNotFoundException;
import com.covaliroman.parkify.parking.application.exception.ParkingSpaceCodeAlreadyExistsException;
import com.covaliroman.parkify.parking.application.exception.ParkingSpaceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({
            ParkingFacilityNotFoundException.class,
            ParkingLevelNotFoundException.class,
            ParkingSpaceNotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFound(RuntimeException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
    }

    @ExceptionHandler({
            ParkingLevelCodeAlreadyExistsException.class,
            ParkingSpaceCodeAlreadyExistsException.class
    })
    public ResponseEntity<ApiError> handleConflict(RuntimeException exception) {
        return response(HttpStatus.CONFLICT, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        var violations = new LinkedHashMap<String, String>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                violations.putIfAbsent(error.getField(), error.getDefaultMessage())
        );
        exception.getBindingResult().getGlobalErrors().forEach(error ->
                violations.putIfAbsent(error.getObjectName(), error.getDefaultMessage())
        );

        return response(
                HttpStatus.BAD_REQUEST,
                "Request validation failed",
                violations
        );
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(RuntimeException exception) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableRequest(
            HttpMessageNotReadableException exception
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "Request body is missing or malformed",
                Map.of()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataConflict(
            DataIntegrityViolationException exception
    ) {
        return response(
                HttpStatus.CONFLICT,
                "Request conflicts with existing data",
                Map.of()
        );
    }

    private ResponseEntity<ApiError> response(
            HttpStatus status,
            String message,
            Map<String, String> violations
    ) {
        return ResponseEntity.status(status).body(new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                violations
        ));
    }
}
