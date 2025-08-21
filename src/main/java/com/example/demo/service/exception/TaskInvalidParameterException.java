package com.example.demo.service.exception;

import lombok.Getter;

import java.util.List;

public class TaskInvalidParameterException extends RuntimeException {

    @Getter
    private List<String> errors;

    public TaskInvalidParameterException(List<String> errors) {
        super(String.join(",", errors));
        this.errors = errors;
    }

}
