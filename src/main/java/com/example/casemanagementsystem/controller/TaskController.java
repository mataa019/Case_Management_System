package com.example.casemanagementsystem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.casemanagementsystem.dto.TaskDto;
import com.example.casemanagementsystem.service.TaskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto.CreateTaskRequest request) {
        TaskDto createdTask = taskService.createTask(request);
        return ResponseEntity.ok(createdTask);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTask(@PathVariable String taskId) {
        TaskDto task = taskService.getTask(taskId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}/assign")
    public ResponseEntity<TaskDto> assignTask(
            @PathVariable String taskId,
            @RequestBody TaskDto.AssignTaskRequest request,
            @RequestHeader("X-User-Id") String userId) {
        TaskDto assignedTask = taskService.assignTask(taskId, request.getAssigneeId(), userId);
        return ResponseEntity.ok(assignedTask);
    }

    @PutMapping("/{taskId}/unassign")
    public ResponseEntity<TaskDto> unassignTask(
            @PathVariable String taskId,
            @RequestHeader("X-User-Id") String userId) {
        TaskDto unassignedTask = taskService.unassignTask(taskId, userId);
        return ResponseEntity.ok(unassignedTask);
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskDto> updateTaskStatus(
            @PathVariable String taskId,
            @RequestBody TaskDto.UpdateTaskStatusRequest request,
            @RequestHeader("X-User-Id") String userId) {
        TaskDto updatedTask = taskService.updateTaskStatus(taskId, request.getStatus(), userId, request.getReason());
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasks(
            @RequestParam(required = false) String assigneeId,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) Long caseId) {
        
        List<TaskDto> tasks;
        
        if (assigneeId != null) {
            tasks = taskService.getTasksByAssignee(assigneeId);
        } else if (groupName != null) {
            tasks = taskService.getTasksByGroup(groupName);
        } else if (caseId != null) {
            tasks = taskService.getTasksByCase(caseId);
        } else {
            // Return empty list or throw exception for invalid request
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(tasks);
    }
}
