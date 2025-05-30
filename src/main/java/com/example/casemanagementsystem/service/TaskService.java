package com.example.casemanagementsystem.service;

import com.example.casemanagementsystem.dto.TaskDto;
import com.example.casemanagementsystem.entity.CaseEntity;
import com.example.casemanagementsystem.entity.TaskEntity;
import com.example.casemanagementsystem.enums.ActionType;
import com.example.casemanagementsystem.enums.TaskStatus;
import com.example.casemanagementsystem.repository.CaseRepository;
import com.example.casemanagementsystem.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final CaseRepository caseRepository;
    private final LoggingService loggingService;
    
    public TaskDto createTask(TaskDto.CreateTaskRequest request) {
        CaseEntity caseEntity = caseRepository.findById(request.getCaseId())
                .orElseThrow(() -> new RuntimeException("Case not found with id: " + request.getCaseId()));
        
        TaskEntity task = TaskEntity.builder()
                .taskId(generateTaskId())
                .taskName(request.getTaskName())
                .description(request.getDescription())
                .status(TaskStatus.UNASSIGNED)
                .groupName(request.getGroupName())
                .caseEntity(caseEntity)
                .dueDate(request.getDueDate())
                .build();
        
        TaskEntity savedTask = taskRepository.save(task);
        
        loggingService.logAction("SYSTEM", ActionType.TASK_CREATED, "TASK", 
                savedTask.getTaskId(), "Task created: " + savedTask.getTaskName());
        
        return convertToDto(savedTask);
    }
    
    public TaskDto assignTask(String taskId, String assigneeId, String userId) {
        TaskEntity task = findTaskByTaskId(taskId);
        
        String oldAssignee = task.getAssigneeId();
        task.setAssigneeId(assigneeId);
        task.setStatus(TaskStatus.ASSIGNED);
        
        TaskEntity savedTask = taskRepository.save(task);
        
        ActionType actionType = oldAssignee != null ? ActionType.TASK_REASSIGNED : ActionType.TASK_ASSIGNED;
        loggingService.logAction(userId, actionType, "TASK", taskId, 
                "Task assigned to: " + assigneeId, oldAssignee, assigneeId);
        
        return convertToDto(savedTask);
    }
    
    public TaskDto unassignTask(String taskId, String userId) {
        TaskEntity task = findTaskByTaskId(taskId);
        
        String oldAssignee = task.getAssigneeId();
        task.setAssigneeId(null);
        task.setStatus(TaskStatus.UNASSIGNED);
        
        TaskEntity savedTask = taskRepository.save(task);
        
        loggingService.logAction(userId, ActionType.TASK_UNASSIGNED, "TASK", taskId, 
                "Task unassigned from: " + oldAssignee, oldAssignee, null);
        
        return convertToDto(savedTask);
    }
    
    public TaskDto updateTaskStatus(String taskId, TaskStatus newStatus, String userId, String reason) {
        TaskEntity task = findTaskByTaskId(taskId);
        
        TaskStatus oldStatus = task.getStatus();
        task.setStatus(newStatus);
        
        TaskEntity savedTask = taskRepository.save(task);
        
        ActionType actionType = mapStatusToActionType(newStatus);
        String description = reason != null ? reason : "Status changed to: " + newStatus;
        
        loggingService.logAction(userId, actionType, "TASK", taskId, description, 
                oldStatus.toString(), newStatus.toString());
        
        return convertToDto(savedTask);
    }
    
    public TaskDto getTask(String taskId) {
        TaskEntity task = findTaskByTaskId(taskId);
        return convertToDto(task);
    }
    
    public List<TaskDto> getTasksByAssignee(String assigneeId) {
        return taskRepository.findByAssigneeId(assigneeId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<TaskDto> getTasksByGroup(String groupName) {
        return taskRepository.findByGroupName(groupName).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<TaskDto> getInvestigationsQueue() {
        return taskRepository.findInvestigationsQueueTasks().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<TaskDto> getTasksByCase(Long caseId) {
        return taskRepository.findByCaseId(caseId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private TaskEntity findTaskByTaskId(String taskId) {
        return taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
    }
    
    private TaskDto convertToDto(TaskEntity task) {
        return TaskDto.builder()
                .id(task.getId())
                .taskId(task.getTaskId())
                .taskName(task.getTaskName())
                .description(task.getDescription())
                .status(task.getStatus())
                .assigneeId(task.getAssigneeId())
                .groupName(task.getGroupName())
                .caseId(task.getCaseEntity().getId())
                .flowableTaskId(task.getFlowableTaskId())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .dueDate(task.getDueDate())
                .build();
    }
    
    private String generateTaskId() {
        return "TASK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private ActionType mapStatusToActionType(TaskStatus status) {
        return switch (status) {
            case ASSIGNED -> ActionType.TASK_ASSIGNED;
            case IN_PROGRESS -> ActionType.TASK_STARTED;
            case BLOCKED -> ActionType.TASK_BLOCKED;
            case COMPLETED -> ActionType.TASK_COMPLETED;
            default -> ActionType.TASK_ASSIGNED;
        };
    }
}
