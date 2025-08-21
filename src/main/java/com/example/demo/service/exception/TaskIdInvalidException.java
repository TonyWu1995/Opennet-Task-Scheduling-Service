package com.example.demo.service.exception;

public class TaskIdInvalidException extends RuntimeException {

    public TaskIdInvalidException() {
        super("Task ID is Duplicated or Invalid");
    }
}
