package com.faculty.management.exception;

/**
 * Custom exception representing authentication failures (invalid username/password or inactive status).
 */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
