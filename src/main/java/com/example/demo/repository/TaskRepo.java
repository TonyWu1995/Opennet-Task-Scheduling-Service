package com.example.demo.repository;

import com.example.demo.model.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TaskRepo extends JpaRepository<TaskEntity, Long> {

    Optional<TaskEntity> findByTaskId(String taskId);

    Page<TaskEntity> findByStatus(String status, Pageable pageable);

    List<TaskEntity> findByTaskIdIn(List<String> taskIds);

    @Modifying
    @Transactional
    @Query("update TaskEntity t set t.status = :status, t.updateAt = :updateAt where t.taskId = :taskId and t.status = 'PENDING'")
    void updateStatusTriggeredByTaskIdAndPending(String taskId, String status, Instant updateAt);
}
