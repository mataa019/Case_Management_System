package com.example.casemanagementsystem.enums;

/**
 * Enum representing the various types of cases that can be created
 */
public enum CaseType {
    FRAUD("Fraud Investigation"),
    DISPUTE("Payment Dispute"),
    COMPLIANCE("Compliance Review"),
    RISK("Risk Assessment");

    private final String displayName;

    CaseType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
