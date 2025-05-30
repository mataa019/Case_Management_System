package com.example.casemanagementsystem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.casemanagementsystem.dto.TaskDto;
import com.example.casemanagementsystem.service.TaskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/queues")
@RequiredArgsConstructor
public class QueueController {

    private final TaskService taskService;

    @GetMapping("/investigations")
    public ResponseEntity<List<TaskDto>> getInvestigationsQueue() {
        List<TaskDto> queueTasks = taskService.getInvestigationsQueue();
        return ResponseEntity.ok(queueTasks);
    }

    @GetMapping("/{groupName}")
    public ResponseEntity<List<TaskDto>> getGroupQueue(@PathVariable String groupName) {
        List<TaskDto> queueTasks = taskService.getTasksByGroup(groupName);
        return ResponseEntity.ok(queueTasks);
    }
}
