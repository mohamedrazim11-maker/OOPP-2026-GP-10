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

    /**
     * Validates a Sri Lankan contact number: 9-10 digits, optionally
     * prefixed with '+', e.g. "0771234567" or "+94771234567".
     * Used by Member 4's profile self-update (contact number is one of
     * the few fields an Undergraduate/Lecturer/Technical Officer may edit).
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return phone.trim().matches("^\\+?[0-9]{9,12}$");
    }
}
