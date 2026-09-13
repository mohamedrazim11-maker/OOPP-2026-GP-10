package com.faculty.management.util;

import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.User;

/**
 * Utility class providing validation methods for user inputs, user creation, and updates.
 * Demonstrates Encapsulation and Error/Exception Handling.
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

    /**
     * Validates all required fields for creating a new user.
     *
     * @param user            User object containing user information
     * @param rawPassword     The plain password entered by the user
     * @param confirmPassword Confirmation password entered by the user
     * @throws ValidationException If any validation constraint fails
     */
    public static void validateNewUser(User user, String rawPassword, String confirmPassword) throws ValidationException {
        if (user == null) {
            throw new ValidationException("User information cannot be null.");
        }

        if (isEmpty(user.getUsername())) {
            throw new ValidationException("Username is required.");
        }
        if (user.getUsername().trim().length() < 3) {
            throw new ValidationException("Username must be at least 3 characters long.");
        }
        if (!user.getUsername().trim().matches("^[a-zA-Z0-9_]+$")) {
            throw new ValidationException("Username can only contain alphanumeric characters and underscores.");
        }

        if (isEmpty(rawPassword)) {
            throw new ValidationException("Password is required for a new user.");
        }
        if (rawPassword.trim().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
        if (confirmPassword != null && !rawPassword.equals(confirmPassword)) {
            throw new ValidationException("Passwords do not match. Please verify.");
        }

        if (isEmpty(user.getFirstName())) {
            throw new ValidationException("First Name is required.");
        }
        if (isEmpty(user.getLastName())) {
            throw new ValidationException("Last Name is required.");
        }

        if (isEmpty(user.getEmail())) {
            throw new ValidationException("Email address is required.");
        }
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            throw new ValidationException("Please enter a valid email address.");
        }

        if (user.getRole() == null || isEmpty(user.getRole().getRoleName())) {
            throw new ValidationException("Please assign a valid role to the user.");
        }
    }

    /**
     * Validates fields for updating an existing user profile.
     *
     * @param user User object containing updated information
     * @throws ValidationException If any validation constraint fails
     */
    public static void validateUserUpdate(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("User data cannot be null.");
        }
        if (user.getUserId() <= 0) {
            throw new ValidationException("Invalid User ID for update.");
        }
        if (isEmpty(user.getFirstName())) {
            throw new ValidationException("First Name cannot be empty.");
        }
        if (isEmpty(user.getLastName())) {
            throw new ValidationException("Last Name cannot be empty.");
        }
        if (isEmpty(user.getEmail())) {
            throw new ValidationException("Email address cannot be empty.");
        }
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            throw new ValidationException("Please enter a valid email address.");
        }
        if (user.getRole() == null || isEmpty(user.getRole().getRoleName())) {
            throw new ValidationException("Please select a valid user role.");
        }
    }
}
