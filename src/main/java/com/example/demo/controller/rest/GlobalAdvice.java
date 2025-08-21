package com.example.demo.controller.rest;


import com.example.demo.controller.rest.res.ErrorResponse;
import com.example.demo.service.exception.TaskIdInvalidException;
import com.example.demo.service.exception.TaskInvalidParameterException;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalAdvice {

    @Order(0)
    @ExceptionHandler(TaskInvalidParameterException.class)
    public ResponseEntity<ErrorResponse> handleException(TaskInvalidParameterException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder()
                        .errorCode(1)
                        .message("Invalid parameter")
                        .errors(ex.getErrors())
                        .build());
    }


    @Order(1)
    @ExceptionHandler(TaskIdInvalidException.class)
    public ResponseEntity<ErrorResponse> handleException(TaskIdInvalidException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder()
                        .errorCode(2)
                        .message("Task ID is invalid")
                        .errors(List.of(ex.getMessage()))
                        .build());
    }

    @Order(2)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleException(RuntimeException ex) {
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.builder()
                        .errorCode(3)
                        .message("Unexpected error occurred")
                        .errors(List.of(ex.getMessage()))
                        .build());
    }

}
