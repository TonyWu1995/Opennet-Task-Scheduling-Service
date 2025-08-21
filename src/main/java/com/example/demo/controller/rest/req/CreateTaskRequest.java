package com.example.demo.controller.rest.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateTaskRequest {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Payload {

        @Schema(example = "email")
        private String type;

        @Schema(example = "hello@example.com")
        private String target;

        @Schema(example = "This is a scheduled task!")
        private String message;
    }

    @Schema(example = "abc-123")
    private String taskId;

    @Schema(example = "2025-07-21T15:00:00Z")
    private Instant executeAt;

    private Payload payload;

}
