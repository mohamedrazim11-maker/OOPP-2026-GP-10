package com.faculty.management.exception;

/**
 * Custom exception representing a violation of an academic business rule,
 * e.g. attempting to edit a locked field, or computing a result for a
 * student with no enrollments.
 */
public class BusinessRuleException extends Exception {
    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
