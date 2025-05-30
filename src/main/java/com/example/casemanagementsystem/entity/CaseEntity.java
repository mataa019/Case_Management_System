package com.example.casemanagementsystem.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.example.casemanagementsystem.enums.CaseStatus;
import com.example.casemanagementsystem.enums.CaseType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", unique = true, nullable = false)
    private String caseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_type", nullable = false)
    private CaseType caseType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CaseStatus status;

    @Column(name = "creator_id", nullable = false)
    private String creatorId;

    @Column(name = "assignee_id")
    private String assigneeId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "priority")
    private String priority;

    @Column(name = "flowable_process_instance_id")
    private String flowableProcessInstanceId;

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskEntity> tasks;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = CaseStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
