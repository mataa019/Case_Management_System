package com.example.casemanagementsystem.service;

import java.util.List;
import java.util.Map;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlowableWorkflowService implements JavaDelegate {

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    @Override
    public void execute(DelegateExecution execution) {
        String activityId = execution.getCurrentActivityId();
        String caseId = (String) execution.getVariable("caseId");
        
        log.info("Executing Flowable service task: {} for case: {}", activityId, caseId);
        
        switch (activityId) {
            case "initializeCase":
                initializeCase(execution);
                break;
            case "closeCase":
                closeCase(execution);
                break;
            default:
                log.warn("Unknown activity: {}", activityId);
        }
    }

    private void initializeCase(DelegateExecution execution) {
        String caseId = (String) execution.getVariable("caseId");
        log.info("Initializing case: {}", caseId);
        
        // Set default variables for the process
        execution.setVariable("action", "INVESTIGATE");
        execution.setVariable("assigneeId", execution.getVariable("creatorId"));
    }

    private void closeCase(DelegateExecution execution) {
        String caseId = (String) execution.getVariable("caseId");
        log.info("Closing case: {}", caseId);
        
        // Additional cleanup logic can be added here
        execution.setVariable("status", "CLOSED");
    }

    public void signalEvent(String executionId, String signalName, Map<String, Object> variables) {
        runtimeService.signalEventReceived(signalName, executionId, variables);
    }

    public List<Task> getTasksByAssignee(String assignee) {
        return taskService.createTaskQuery().taskAssignee(assignee).list();
    }

    public List<Task> getTasksByGroup(String groupName) {
        return taskService.createTaskQuery().taskCandidateGroup(groupName).list();
    }

    public void completeTask(String taskId, Map<String, Object> variables) {
        taskService.complete(taskId, variables);
    }

    public void claimTask(String taskId, String userId) {
        taskService.claim(taskId, userId);
    }

    public void unclaimTask(String taskId) {
        taskService.unclaim(taskId);
    }

    public void reassignTask(String taskId, String newUserId) {
        taskService.setAssignee(taskId, newUserId);
    }

    public Task getTaskByProcessInstanceId(String processInstanceId) {
        return taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
    }
}
