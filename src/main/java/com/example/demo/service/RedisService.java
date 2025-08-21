package com.example.demo.service;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@ToString
@Slf4j
@Service
public class RedisService {

    private static final int MAX_BATCH_SIZE = 100;
    private static final String TASK_KEY = "scheduled_tasks";

    private final RedisTemplate<String, String> redisTemplate;
    private final ZSetOperations<String, String> zSetOps;

    public RedisService(final RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.zSetOps = redisTemplate.opsForZSet();
    }

    public void addTask(String taskId, Instant executeAt) {
        double score = executeAt.toEpochMilli();
        zSetOps.add(TASK_KEY, taskId, score);
    }

    public Set<String> getDueTasks(Instant now) {
        double maxScore = now.toEpochMilli();
        return zSetOps.rangeByScore(TASK_KEY, 0, maxScore, 0, MAX_BATCH_SIZE);
    }

    public void removeTask(String taskId) {
        zSetOps.remove(TASK_KEY, taskId);
    }
}
