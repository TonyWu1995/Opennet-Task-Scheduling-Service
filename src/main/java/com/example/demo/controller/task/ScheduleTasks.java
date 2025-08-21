package com.example.demo.controller.task;

import com.example.demo.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class ScheduleTasks {

    private final ScheduleService service;

    public ScheduleTasks(final ScheduleService service) {
        this.service = service;
    }

    @Scheduled(fixedRate = 1000)
    public void run() {
        service.run(Instant.now());
    }

}
