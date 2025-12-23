package com.hpms.commonlib.handler;

import com.hpms.commonlib.dto.ApiResponse;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

@RestControllerAdvice
@Slf4j

public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleInvalidArgument(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        return ApiResponse.builder()
                .ok(false)
                .message(ex.getMessage())
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .build();
    }

    @ExceptionHandler(ServiceCommunicationException.class)
    public ResponseEntity<ApiResponse<Void>> handleServiceCommunicationException(
            ServiceCommunicationException ex) {

        log.error("Service communication error: {}", ex.getMessage(), ex);

        // Return 503 Service Unavailable for service errors
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.<Void>builder()
                        .ok(false)
                        .message("External service error: " + ex.getServiceName())
                        .build());
    }

    // Handle WebClient errors (timeout, connection refused, etc.)
    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleWebClientRequestException(
            WebClientRequestException ex) {

        log.error("Service connection error: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.<Void>builder()
                        .ok(false)
                        .message("Unable to connect to external service")
                        .build());
    }

    // Handle timeout errors
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ApiResponse<Void>> handleTimeoutException(TimeoutException ex) {
        log.error("Service timeout: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.GATEWAY_TIMEOUT)
                .body(ApiResponse.<Void>builder()
                        .ok(false)
                        .message("Service request timeout")
                        .build());
    }

    // Handle resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        log.warn("Resource not found: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>builder()
                        .ok(false)
                        .message("Resource not found")
                        .build());
    }

    // Handle bad request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequestException(
            BadRequestException ex) {

        log.warn("Bad request: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                        .ok(false)
                        .message("Invalid request")
                        .build());
    }

    // Catch-all for unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Void>builder()
                        .ok(false)
                        .message("An unexpected error occurred")
                        .build());
    }

}