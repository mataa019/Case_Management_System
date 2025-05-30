package com.example.casemanagementsystem.enums;

/**
 * Enum representing the various statuses a case can have in its lifecycle.
 * Valid transitions: DRAFT → READY → SUSPENDED → READY → CLOSED
 */
public enum CaseStatus {
    DRAFT("Draft"),
    READY("Ready"),
    SUSPENDED("Suspended"),
    CLOSED("Closed");

    private final String displayName;

    CaseStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Validates if transition from current status to target status is allowed
     */
    public boolean canTransitionTo(CaseStatus targetStatus) {
        return switch (this) {
            case DRAFT -> targetStatus == READY;
            case READY -> targetStatus == SUSPENDED || targetStatus == CLOSED;
            case SUSPENDED -> targetStatus == READY || targetStatus == CLOSED;
            case CLOSED -> false; // No transitions allowed from CLOSED
        };
    }
}
