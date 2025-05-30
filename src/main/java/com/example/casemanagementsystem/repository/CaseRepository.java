package com.example.casemanagementsystem.repository;

import com.example.casemanagementsystem.entity.CaseEntity;
import com.example.casemanagementsystem.enums.CaseStatus;
import com.example.casemanagementsystem.enums.CaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<CaseEntity, Long> {
    
    Optional<CaseEntity> findByCaseId(String caseId);
    
    List<CaseEntity> findByStatus(CaseStatus status);
    
    List<CaseEntity> findByCaseType(CaseType caseType);
    
    List<CaseEntity> findByCreatorId(String creatorId);
    
    List<CaseEntity> findByAssigneeId(String assigneeId);
    
    @Query("SELECT c FROM CaseEntity c WHERE c.status = :status AND c.caseType = :caseType")
    List<CaseEntity> findByStatusAndCaseType(@Param("status") CaseStatus status, @Param("caseType") CaseType caseType);
    
    @Query("SELECT c FROM CaseEntity c WHERE c.status IN ('READY', 'SUSPENDED') ORDER BY c.createdAt DESC")
    List<CaseEntity> findActiveCases();
}