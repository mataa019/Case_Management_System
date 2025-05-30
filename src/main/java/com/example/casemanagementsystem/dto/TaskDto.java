package com.example.casemanagementsystem.dto;

import com.example.casemanagementsystem.enums.TaskStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    
    private Long id;
    private String taskId;
    private String taskName;
    private String description;
    private TaskStatus status;
    private String assigneeId;
    private String groupName;
    private Long caseId;
    private String flowableTaskId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
    
    // Request DTOs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateTaskRequest {
        private String taskName;
        private String description;
        private String groupName;
        private Long caseId;
        private LocalDateTime dueDate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignTaskRequest {
        private String assigneeId;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTaskStatusRequest {
        private TaskStatus status;
        private String reason;
    }
}
