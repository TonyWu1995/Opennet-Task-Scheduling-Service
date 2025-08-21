package com.example.demo.service;

import com.example.demo.model.Status;
import com.example.demo.model.TaskEntity;
import com.example.demo.repository.TaskRepo;
import com.example.demo.service.exception.TaskIdInvalidException;
import com.example.demo.service.exception.TaskInvalidParameterException;
import com.example.demo.service.param.CreateTaskParam;
import com.example.demo.utils.JsonUtils;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@ToString
@Service
public class TaskService {

    private final TaskRepo taskRepo;
    private final RedisService redisService;
    private final RedisLockService redisLockService;

    public TaskService(final TaskRepo taskRepo, final RedisService redisService, final RedisLockService redisLockService) {
        this.taskRepo = taskRepo;
        this.redisService = redisService;
        this.redisLockService = redisLockService;
    }

    List<String> validate(final CreateTaskParam param) {
        List<String> errors = new ArrayList<>();
        if (param.getTaskId() == null || param.getTaskId().isEmpty()) {
            errors.add("Task ID cannot be null");
        }
        if (param.getExecuteAt() == null) {
            errors.add("Execute time cannot be null");
        }
        if (param.getPayload() == null) {
            errors.add("Payload cannot be null");
        }
        if (param.getExecuteAt() != null && Instant.now().isAfter(param.getExecuteAt())) {
            errors.add("Execute time must be in the future");
        }
        return errors;
    }

    boolean isExist(String id) {
        return taskRepo.findByTaskId(id).isPresent();
    }

    public String createTask(final CreateTaskParam param) {
        log.debug("Creating task: {}", param);
        final List<String> errors = validate(param);
        if (!errors.isEmpty()) {
            log.error("Invalid task parameters: {}", param);
            throw new TaskInvalidParameterException(errors);
        }
        if (isExist(param.getTaskId())) {
            log.error("Task ID already exists: {}", param.getTaskId());
            throw new TaskIdInvalidException();
        }
        Map<String, Object> payload = JsonUtils.fromObject(param.getPayload(), Map.class);
        final TaskEntity taskEntity = TaskEntity.builder()
                .taskId(param.getTaskId())
                .executeAt(param.getExecuteAt())
                .payload(payload)
                .createAt(Instant.now())
                .status(Status.PENDING.name())
                .build();
        log.debug("Task entity created: {}", taskEntity);
        taskRepo.save(taskEntity);
        redisService.addTask(taskEntity.getTaskId(), taskEntity.getExecuteAt());
        return param.getTaskId();
    }

    public void cancelledByTaskId(String taskId) {
        log.debug("Cancelled task with ID: {}", taskId);
        Optional<TaskEntity> entity = this.findByTaskId(taskId);
        if (entity.isEmpty()) {
            log.error("Task with ID {} not found", taskId);
            throw new TaskIdInvalidException();
        }
        if (!Status.PENDING.name().equals(entity.get().getStatus())) {
            log.error("Task with ID {} is not in PENDING status", taskId);
            throw new TaskInvalidParameterException(List.of("Task is not in PENDING status"));
        }
        TaskEntity task = entity.get();
        task.setStatus(Status.CANCELLED.name());
        task.setUpdateAt(Instant.now());
        boolean isLock = false;
        try {
            isLock = redisLockService.tryLock(taskId, taskId);
            if (!isLock) {
                throw new RuntimeException("Failed to get lock for task deletion");
            }
            redisService.removeTask(task.getTaskId());
            taskRepo.save(task);
        } catch (Exception e) {
            log.error("Error while deleting task with ID {}: {}", taskId, e.getMessage());
            throw new RuntimeException("Failed to delete task", e);
        } finally {
            if (isLock) {
                redisLockService.unlock(taskId, taskId);
            }
        }
    }

    public Optional<TaskEntity> findByTaskId(String taskId) {
        return taskRepo.findByTaskId(taskId);
    }

    public List<TaskEntity> findByStatus(String status, int page, int size) {
        log.debug("Finding tasks with status: {}", status);
        final Pageable pageable = PageRequest.of(page, size);
        return taskRepo.findByStatus(status, pageable).stream().toList();
    }
}
