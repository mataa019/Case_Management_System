package com.example.casemanagementsystem.enums;

/**
 * Enum representing different types of actions that can be performed on cases and tasks
 */
public enum ActionType {
    // Case actions
    CASE_CREATED("Case Created"),
    CASE_ASSIGNED("Case Assigned"),
    CASE_SUSPENDED("Case Suspended"),
    CASE_RESUMED("Case Resumed"),
    CASE_CLOSED("Case Closed"),
    
    // Task actions
    TASK_CREATED("Task Created"),
    TASK_ASSIGNED("Task Assigned"),
    TASK_REASSIGNED("Task Reassigned"),
    TASK_UNASSIGNED("Task Unassigned"),
    TASK_STARTED("Task Started"),
    TASK_BLOCKED("Task Blocked"),
    TASK_COMPLETED("Task Completed");

    private final String displayName;

    ActionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
