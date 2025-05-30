package com.example.casemanagementsystem.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.casemanagementsystem.entity.AuditLogEntity;
import com.example.casemanagementsystem.enums.ActionType;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
    
    List<AuditLogEntity> findByEntityTypeAndEntityId(String entityType, String entityId);
    
    List<AuditLogEntity> findByUserId(String userId);
    
    List<AuditLogEntity> findByActionType(ActionType actionType);
    
    @Query("SELECT a FROM AuditLogEntity a WHERE a.timestamp BETWEEN :startDate AND :endDate ORDER BY a.timestamp DESC")
    List<AuditLogEntity> findByTimestampBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM AuditLogEntity a WHERE a.entityType = :entityType AND a.entityId = :entityId ORDER BY a.timestamp DESC")
    List<AuditLogEntity> findAuditTrailForEntity(@Param("entityType") String entityType, @Param("entityId") String entityId);
}
