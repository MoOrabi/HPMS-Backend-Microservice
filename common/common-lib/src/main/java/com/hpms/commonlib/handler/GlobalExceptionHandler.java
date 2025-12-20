package com.hpms.commonlib.handler;

import com.hpms.commonlib.dto.ApiResponse;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.logging.Level;

@RestControllerAdvice
@Log
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleInvalidArgument(MethodArgumentNotValidException ex) {
        log.log(Level.SEVERE, ex.getMessage(), ex);
        return ApiResponse.builder()
                .ok(false)
                .message(ex.getMessage())
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleInvalidArgument(Exception ex) {
        ex.printStackTrace();
        return ApiResponse.builder()
                .ok(false)
                .message(ex.getMessage())
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .build();
    }

}