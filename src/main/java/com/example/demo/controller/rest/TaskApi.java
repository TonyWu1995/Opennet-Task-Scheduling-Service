package com.example.demo.controller.rest;

import com.example.demo.controller.rest.req.CreateTaskRequest;
import com.example.demo.controller.rest.res.TaskResponse;
import com.example.demo.model.TaskEntity;
import com.example.demo.service.TaskService;
import com.example.demo.service.param.CreateTaskParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
public class TaskApi {

    private final TaskService service;

    public TaskApi(TaskService service) {
        this.service = service;
    }

    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@RequestBody CreateTaskRequest request) {
        log.info("Received task creation request: {}", request);
        final CreateTaskParam param = CreateTaskParam.builder()
                .taskId(request.getTaskId())
                .executeAt(request.getExecuteAt())
                .payload(CreateTaskParam.Payload.builder()
                        .type(request.getPayload().getType())
                        .target(request.getPayload().getTarget())
                        .message(request.getPayload().getMessage())
                        .build())
                .build();
        final String taskId = service.createTask(param);
        final String location = String.format("/tasks/%s", taskId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create(location))
                .build();
    }

    @GetMapping("/tasks")
    public List<TaskEntity> getTasks(@RequestParam(value = "status", required = false) String status,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Received request to get all tasks with status: {}", status);
        return service.findByStatus(status, page, size);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> findTaskById(@PathVariable String taskId) {
        log.info("Received task find request: {}", taskId);
        return service.findByTaskId(taskId)
                .map(task -> TaskResponse.builder()
                        .taskId(task.getTaskId())
                        .status(task.getStatus())
                        .scheduleAt(task.getExecuteAt())
                        .payload(task.getPayload())
                        .build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<?> cancelledTaskById(@PathVariable String taskId) {
        log.info("Received task cancelled request: {}", taskId);
        service.cancelledByTaskId(taskId);
        return ResponseEntity.noContent().build();
    }


}
