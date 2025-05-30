package com.example.casemanagementsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.casemanagementsystem.entity.TaskEntity;
import com.example.casemanagementsystem.enums.TaskStatus;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    
    Optional<TaskEntity> findByTaskId(String taskId);
    
    List<TaskEntity> findByStatus(TaskStatus status);
    
    List<TaskEntity> findByAssigneeId(String assigneeId);
    
    List<TaskEntity> findByGroupName(String groupName);
    
    @Query("SELECT t FROM TaskEntity t WHERE t.groupName = :groupName AND t.status = :status")
    List<TaskEntity> findByGroupNameAndStatus(@Param("groupName") String groupName, @Param("status") TaskStatus status);
    
    @Query("SELECT t FROM TaskEntity t WHERE t.caseEntity.id = :caseId")
    List<TaskEntity> findByCaseId(@Param("caseId") Long caseId);
    
    @Query("SELECT t FROM TaskEntity t WHERE t.groupName = 'Investigations' AND t.status IN ('UNASSIGNED', 'ASSIGNED')")
    List<TaskEntity> findInvestigationsQueueTasks();
}
