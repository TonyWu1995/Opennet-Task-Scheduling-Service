package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisLockService {

    private static final int TTL = 30;
    private static final String LOCK_PREFIX = "task-lock:";

    private final RedisTemplate<String, String> redisTemplate;

    public RedisLockService(final RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryLock(String key, String value) {
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(LOCK_PREFIX + key, value, TTL, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(isLock);
    }

    public void unlock(String key, String value) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del', KEYS[1]) else return 0 end", Long.class);
        redisTemplate.execute(script, Collections.singletonList(LOCK_PREFIX + key), value);
    }

}
