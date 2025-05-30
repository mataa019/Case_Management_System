package com.example.casemanagementsystem.service;

import com.example.casemanagementsystem.entity.AuditLogEntity;
import com.example.casemanagementsystem.enums.ActionType;
import com.example.casemanagementsystem.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoggingService {
    
    private final AuditLogRepository auditLogRepository;
    
    public void logAction(String userId, ActionType actionType, String entityType, 
                         String entityId, String description) {
        logAction(userId, actionType, entityType, entityId, description, null, null);
    }
    
    public void logAction(String userId, ActionType actionType, String entityType, 
                         String entityId, String description, String oldValue, String newValue) {
        try {
            AuditLogEntity auditLog = AuditLogEntity.builder()
                    .userId(userId)
                    .actionType(actionType)
                    .entityType(entityType)
                    .entityId(entityId)
                    .description(description)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .timestamp(LocalDateTime.now())
                    .build();
            
            auditLogRepository.save(auditLog);
            log.info("Audit log created: {} performed {} on {} {}", userId, actionType, entityType, entityId);
        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage(), e);
        }
    }
    
    public List<AuditLogEntity> getAuditTrail(String entityType, String entityId) {
        return auditLogRepository.findAuditTrailForEntity(entityType, entityId);
    }
    
    public List<AuditLogEntity> getAuditLogsByUser(String userId) {
        return auditLogRepository.findByUserId(userId);
    }
    
    public List<AuditLogEntity> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByTimestampBetween(startDate, endDate);
    }
}
