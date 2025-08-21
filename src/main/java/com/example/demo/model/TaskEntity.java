package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Entity
@Table(indexes = {
        @Index(name = "idx_task_id", columnList = "taskId", unique = true),
})
@EqualsAndHashCode(exclude = {})
@ToString(exclude = {})
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long id;

    @Column(unique = true)
    private String taskId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> payload;

    private String status;

    private Instant executeAt;
    private Instant createAt;
    private Instant updateAt;
}
