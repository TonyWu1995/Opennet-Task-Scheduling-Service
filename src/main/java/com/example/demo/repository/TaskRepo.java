package com.example.demo.repository;

import com.example.demo.model.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepo extends JpaRepository<TaskEntity, Long> {

    Optional<TaskEntity> findByTaskId(String taskId);

    Page<TaskEntity> findByStatus(String status, Pageable pageable);

    List<TaskEntity> findByTaskIdIn(List<String> taskIds);
}
