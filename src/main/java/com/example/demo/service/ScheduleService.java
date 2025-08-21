package com.example.demo.service;

import com.example.demo.model.Status;
import com.example.demo.model.TaskEntity;
import com.example.demo.mq.Message;
import com.example.demo.mq.RocketMQProducer;
import com.example.demo.repository.TaskRepo;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.demo.utils.UUIDUtils.generateUUID;

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

    @Data
    static class Triple {
        String taskId;
        boolean isLock;
        String uuid;

        public Triple(String taskId, boolean isLock, String uuid) {
            this.taskId = taskId;
            this.isLock = isLock;
            this.uuid = uuid;
        }
    }

    //find all and publish
    public void run(Instant now) {
        log.info("now: {} {}", now, now.toEpochMilli());
        Set<String> taskIdSet = redisService.getDueTasks(now);
        log.info("Due tasks found: {}", taskIdSet);
        final Map<String, String> lockMap = taskIdSet
                .stream()
                .map(id -> {
                    String uuid = generateUUID();
                    boolean isLock = redisLockService.tryLock(id, uuid);
                    return new Triple(id, isLock, uuid);
                })
                .filter(triple -> triple.isLock)
                .collect(Collectors.toMap(Triple::getTaskId, Triple::getUuid));
        final List<TaskEntity> taskEntities = taskRepo.findByTaskIdIn(taskIdSet.stream().toList());
        for (TaskEntity taskEntity : taskEntities) {
            try {
                if (!lockMap.containsKey(taskEntity.getTaskId())) {
                    log.warn("Task {} is not locked, skipping", taskEntity.getTaskId());
                    continue;
                }
                final Message msg = Message.builder()
                        .payload(taskEntity.getPayload())
                        .build();
                Status status = Status.TRIGGERED;
                if (!producer.send(msg)) {
                    status = Status.FAILURE;
                }
                redisService.removeTask(taskEntity.getTaskId());
                this.updateStatus(taskEntity, status);
            } catch (Exception ex) {
                log.error("Error while processing task: {}", taskEntity.getTaskId(), ex);
            } finally {
                // Unlock tasks
                log.debug("Unlocking task: {}", taskEntity.getTaskId());
                String uuid = lockMap.get(taskEntity.getTaskId());
                redisLockService.unlock(taskEntity.getTaskId(), uuid);
            }
        }
    }

    void updateStatus(TaskEntity taskEntity, Status status) {
        taskRepo.updateStatusTriggeredByTaskIdAndPending(taskEntity.getTaskId(), status.name(), Instant.now());
    }

}
