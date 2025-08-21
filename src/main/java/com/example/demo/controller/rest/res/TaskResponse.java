package com.example.demo.controller.rest.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskResponse {

    String taskId;

    String status;

    Instant scheduleAt;

    Map<String, Object> payload;

}
