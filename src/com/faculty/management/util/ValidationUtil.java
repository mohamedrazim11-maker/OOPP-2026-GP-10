package com.faculty.management.util;

import com.faculty.management.exception.ValidationException;

/**
 * Utility class providing validation methods for user inputs.
 */
public class ValidationUtil {

    private ValidationUtil() {
        // Prevent instantiation
    }

    /**
     * Checks if a string is null or whitespace.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validates that username, password, and role are valid and not empty.
     *
     * @param username The entered username
     * @param password The entered password
     * @param role     The selected role from the dropdown
     * @throws ValidationException If any field is invalid or missing
     */
    public static void validateLoginInput(String username, String password, String role) throws ValidationException {
        if (isEmpty(role) || "-- Select Role --".equalsIgnoreCase(role.trim())) {
            throw new ValidationException("Please select your role.");
        }
        if (isEmpty(username)) {
            throw new ValidationException("Username cannot be empty.");
        }
        if (isEmpty(password)) {
            throw new ValidationException("Password cannot be empty.");
        }
        if (username.trim().length() < 3) {
            throw new ValidationException("Username must be at least 3 characters long.");
        }
    }

    /**
     * Validates that username and password are not empty.
     *
     * @param username The entered username
     * @param password The entered password
     * @throws ValidationException If either field is empty
     */
    public static void validateLoginInput(String username, String password) throws ValidationException {
        if (isEmpty(username)) {
            throw new ValidationException("Username cannot be empty.");
        }
        if (isEmpty(password)) {
            throw new ValidationException("Password cannot be empty.");
        }
        if (username.trim().length() < 3) {
            throw new ValidationException("Username must be at least 3 characters long.");
        }
    }
}
