package com.example.demo.service;

import com.example.demo.repository.TaskRepo;
import com.example.demo.service.param.CreateTaskParam;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

class TaskServiceTest {

    TaskRepo taskRepo;
    RedisService redisService;
    RedisLockService redisLockService;

    TaskService getInstance() {
        taskRepo = Mockito.mock(TaskRepo.class);
        redisService = Mockito.mock(RedisService.class);
        redisLockService = Mockito.mock(RedisLockService.class);
        return new TaskService(taskRepo, redisService, redisLockService);
    }

    @Test
    void test_validate_is_param_all_correct() {
        TaskService taskService = getInstance();
        CreateTaskParam param = CreateTaskParam.builder()
                .taskId("abc-123")
                .executeAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .payload(CreateTaskParam.Payload.builder()
                        .message("Hello World!")
                        .target("abc-123")
                        .type("abc")
                        .build())
                .build();
        List<String> result = taskService.validate(param);
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void test_validate_time_error() {
        TaskService taskService = getInstance();
        CreateTaskParam param = CreateTaskParam.builder()
                .taskId("abc-123")
                .executeAt(Instant.ofEpochSecond(1624613234))
                .payload(CreateTaskParam.Payload.builder()
                        .message("Hello World!")
                        .target("abc-123")
                        .type("abc")
                        .build())
                .build();
        List<String> result = taskService.validate(param);
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0)).isEqualTo("Execute time must be in the future");
    }

    @Test
    void test_validate_time_is_null() {
        TaskService taskService = getInstance();
        CreateTaskParam param = CreateTaskParam.builder()
                .taskId("abc-123")
                .executeAt(null)
                .payload(CreateTaskParam.Payload.builder()
                        .message("Hello World!")
                        .target("abc-123")
                        .type("abc")
                        .build())
                .build();
        List<String> result = taskService.validate(param);
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0)).isEqualTo("Execute time cannot be null");
    }

    @Test
    void test_validate_taskId_is_null() {
        TaskService taskService = getInstance();
        CreateTaskParam param = CreateTaskParam.builder()
                .taskId(null)
                .executeAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .payload(CreateTaskParam.Payload.builder()
                        .message("Hello World!")
                        .target("abc-123")
                        .type("abc")
                        .build())
                .build();
        List<String> result = taskService.validate(param);
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0)).isEqualTo("Task ID cannot be null");

        param.setTaskId("");
        result = taskService.validate(param);
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0)).isEqualTo("Task ID cannot be null");
    }

    @Test
    void test_validate_payload_null() {
        TaskService taskService = getInstance();
        CreateTaskParam param = CreateTaskParam.builder()
                .taskId("abc-123")
                .executeAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .payload(null)
                .build();
        List<String> result = taskService.validate(param);
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0)).isEqualTo("Payload cannot be null");
    }



}