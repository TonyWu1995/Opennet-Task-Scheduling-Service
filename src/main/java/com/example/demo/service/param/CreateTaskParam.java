package com.example.demo.service.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateTaskParam {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Payload {
        private String type;
        private String target;
        private String message;
    }

    private String taskId;

    private Instant executeAt;

    private Payload payload;

}
