package com.example.casemanagementsystem.enums;

/**
 * Enum representing the various statuses a task can have
 */
public enum TaskStatus {
    UNASSIGNED("Unassigned"),
    ASSIGNED("Assigned"),
    IN_PROGRESS("In Progress"),
    BLOCKED("Blocked"),
    COMPLETED("Completed");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
