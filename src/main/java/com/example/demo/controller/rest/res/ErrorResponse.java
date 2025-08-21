package com.example.demo.controller.rest.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ErrorResponse {

    private int errorCode;
    private String message;
    private List<String> errors;
}
