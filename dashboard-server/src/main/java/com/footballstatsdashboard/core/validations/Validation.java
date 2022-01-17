package com.footballstatsdashboard.core.validations;

public class Validation {
    private final ValidationSeverity validationSeverity;
    private final String message;

    public Validation(ValidationSeverity validationSeverity, String message) {
        this.validationSeverity = validationSeverity;
        this.message = message;
    }

    public ValidationSeverity getValidationSeverity() {
        return validationSeverity;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", this.getValidationSeverity(), this.getMessage());
    }
}