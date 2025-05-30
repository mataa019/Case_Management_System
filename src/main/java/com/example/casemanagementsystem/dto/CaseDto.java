// filepath: d:\Learning\SpringBoot\CaseManagementSystem\src\main\java\com\example\casemanagementsystem\dto\CaseDto.java
package com.example.casemanagementsystem.dto;

import com.example.casemanagementsystem.enums.CaseStatus;
import com.example.casemanagementsystem.enums.CaseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseDto {
    
    private Long id;
    private String caseId;
    private CaseType caseType;
    private CaseStatus status;
    private String creatorId;
    private String assigneeId;
    private String title;
    private String description;
    private String priority;
    private String flowableProcessInstanceId;
    private List<TaskDto> tasks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Request DTOs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateCaseRequest {
        private CaseType caseType;
        private String title;
        private String description;
        private String priority;
        private String creatorId;
        
        // Explicit getters as backup
        public CaseType getCaseType() { return caseType; }
        public void setCaseType(CaseType caseType) { this.caseType = caseType; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        
        public String getCreatorId() { return creatorId; }
        public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssignCaseRequest {
        private String assigneeId;
        
        public String getAssigneeId() { return assigneeId; }
        public void setAssigneeId(String assigneeId) { this.assigneeId = assigneeId; }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateCaseStatusRequest {
        private CaseStatus status;
        private String reason;
        
        public CaseStatus getStatus() { return status; }
        public void setStatus(CaseStatus status) { this.status = status; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateCaseRequest {
        private String title;
        private String description;
        private String priority;
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
    }
}