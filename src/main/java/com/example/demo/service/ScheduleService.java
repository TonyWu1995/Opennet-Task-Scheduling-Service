package com.example.demo.service;

import com.example.demo.model.Status;
import com.example.demo.model.TaskEntity;
import com.example.demo.mq.Message;
import com.example.demo.mq.RocketMQProducer;
import com.example.demo.repository.TaskRepo;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@ToString
public class ScheduleService {

    private final RedisService redisService;

    private final TaskRepo taskRepo;

    private final RocketMQProducer producer;

    private final RedisLockService redisLockService;

    public ScheduleService(final RedisService redisService, final TaskRepo taskRepo, final RocketMQProducer producer, final RedisLockService redisLockService) {
        this.redisService = redisService;
        this.taskRepo = taskRepo;
        this.producer = producer;
        this.redisLockService = redisLockService;
    }

    public void run(Instant now) {
        log.info("now: {} {}", now, now.toEpochMilli());
        Set<String> taskIdSet = redisService.getDueTasks(now);
        log.info("Due tasks found: {}", taskIdSet);
        final List<TaskEntity> taskEntities = taskRepo.findByTaskIdIn(taskIdSet.stream().toList());
        for (TaskEntity taskEntity : taskEntities) {
            log.info("Processing task: {}", taskEntity);
            final Message msg = Message.builder()
                    .payload(taskEntity.getPayload())
                    .build();
            boolean isLock = false;
            try {
                isLock = redisLockService.tryLock(taskEntity.getTaskId(), taskEntity.getTaskId());
                if (!isLock) {
                    continue;
                }
                Status status = Status.TRIGGERED;
                if (!producer.send(msg)) {
                    status = Status.FAILURE;
                }
                redisService.removeTask(taskEntity.getTaskId());
                this.updateStatus(taskEntity, status);
            } catch (Exception e) {
                log.error("Error while processing task: {}", taskEntity.getTaskId(), e);
            } finally {
                if (isLock) {
                    redisLockService.unlock(taskEntity.getTaskId(), taskEntity.getTaskId());
                }
            }
        }
    }

    void updateStatus(TaskEntity taskEntity, Status status) {
        taskEntity.setStatus(status.name());
        taskEntity.setUpdateAt(Instant.now());
        taskRepo.save(taskEntity);
    }

}
