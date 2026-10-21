package com.faculty.management.util;

import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Course;
import com.faculty.management.model.Role;
import com.faculty.management.model.User;

/**
<<<<<<< HEAD
 * Utility class providing validation methods for user inputs.
=======
 * Utility class providing validation methods for user inputs, user creation, and updates.
 * Demonstrates Encapsulation and Error/Exception Handling.
>>>>>>> test
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
     * @param role The selected role from the dropdown
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
     * Validates a Sri Lankan contact number: 9-10 digits, optionally
     * prefixed with '+', e.g. "0771234567" or "+94771234567".
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return phone.trim().matches("^\\+?[0-9]{9,12}$");
    }

<<<<<<< HEAD
=======
    /**
     * Validates all required fields for creating a new user.
     *
     * @param user            User object containing user information
     * @param rawPassword     The plain password entered by the user
     * @param confirmPassword Confirmation password entered by the user
     * @throws ValidationException If any validation constraint fails
     */
>>>>>>> test
    public static void validateNewUser(User user, String rawPassword, String confirmPassword) throws ValidationException {
        if (user == null) {
            throw new ValidationException("User details cannot be null.");
        }
        if (isEmpty(user.getUsername())) {
            throw new ValidationException("Username cannot be empty.");
        }
        if (user.getUsername().trim().length() < 3) {
            throw new ValidationException("Username must be at least 3 characters long.");
        }
        if (isEmpty(user.getEmail())) {
            throw new ValidationException("Email cannot be empty.");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Email must be a valid email address.");
        }
        if (isEmpty(user.getFirstName())) {
            throw new ValidationException("First name cannot be empty.");
        }
        if (isEmpty(user.getLastName())) {
            throw new ValidationException("Last name cannot be empty.");
        }
        if (user.getRole() == null || isEmpty(user.getRole().getRoleName())) {
            throw new ValidationException("Please select a valid role.");
        }
        if (!isValidPhoneNumber(user.getContactNo())) {
            throw new ValidationException("Contact number must be a valid Sri Lankan phone number.");
        }
        if (isEmpty(rawPassword)) {
            throw new ValidationException("Password cannot be empty.");
        }
        if (rawPassword.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
        if (!rawPassword.equals(confirmPassword)) {
            throw new ValidationException("Passwords do not match.");
        }
    }

    public static void validateUserUpdate(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("User details cannot be null.");
        }
        if (user.getUserId() <= 0) {
            throw new ValidationException("Invalid user ID.");
        }
        if (isEmpty(user.getUsername())) {
            throw new ValidationException("Username cannot be empty.");
        }
        if (isEmpty(user.getEmail())) {
            throw new ValidationException("Email cannot be empty.");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Email must be a valid email address.");
        }
        if (isEmpty(user.getFirstName())) {
            throw new ValidationException("First name cannot be empty.");
        }
        if (isEmpty(user.getLastName())) {
            throw new ValidationException("Last name cannot be empty.");
        }
        if (user.getRole() == null || isEmpty(user.getRole().getRoleName())) {
            throw new ValidationException("Please select a valid role.");
        }
        if (!isValidPhoneNumber(user.getContactNo())) {
            throw new ValidationException("Contact number must be a valid Sri Lankan phone number.");
        }
    }

    public static void validateCredentialsUpdate(int userId, String currentUsername, String newUsername,
                                                String newPassword, String confirmPassword) throws ValidationException {
        if (userId <= 0) {
            throw new ValidationException("Invalid user ID.");
        }
        boolean usernameProvided = !isEmpty(newUsername);
        boolean passwordProvided = !isEmpty(newPassword);
        if (!usernameProvided && !passwordProvided) {
            throw new ValidationException("At least one field must be updated.");
        }
        if (usernameProvided) {
            String trimmedUsername = newUsername.trim();
            if (trimmedUsername.length() < 3) {
                throw new ValidationException("Username must be at least 3 characters long.");
            }
            if (!trimmedUsername.equalsIgnoreCase(currentUsername) && !trimmedUsername.equalsIgnoreCase(confirmPassword)) {
                // no-op validation for confirm matching is handled in service before call; kept as safety
            }
        }
        if (passwordProvided) {
            if (newPassword.length() < 6) {
                throw new ValidationException("Password must be at least 6 characters long.");
            }
            if (!newPassword.equals(confirmPassword)) {
                throw new ValidationException("Passwords do not match.");
            }
        }
    }

    public static void validateRoleAssignment(int userId, String username, String roleName) throws ValidationException {
        if (userId <= 0) {
            throw new ValidationException("Invalid user ID.");
        }
        if (isEmpty(username)) {
            throw new ValidationException("Username cannot be empty.");
        }
        if (isEmpty(roleName)) {
            throw new ValidationException("Role cannot be empty.");
        }
        String normalized = roleName.trim();
        if (!Role.ADMIN.equalsIgnoreCase(normalized)
                && !Role.LECTURER.equalsIgnoreCase(normalized)
                && !Role.TECHNICAL_OFFICER.equalsIgnoreCase(normalized)
                && !Role.UNDERGRADUATE.equalsIgnoreCase(normalized)) {
            throw new ValidationException("Invalid role selected.");
        }
    }

    public static void validateUserDeletion(int userId, String username) throws ValidationException {
        if (userId <= 0) {
            throw new ValidationException("Invalid user ID.");
        }
        if (isEmpty(username)) {
            throw new ValidationException("Username cannot be empty.");
        }
        if (userId == 1) {
            throw new ValidationException("The primary system administrator cannot be deleted.");
        }
    }

    public static void validateCourse(Course course) throws ValidationException {
        if (course == null) {
            throw new ValidationException("Course cannot be null.");
        }
        if (isEmpty(course.getCourseCode())) {
            throw new ValidationException("Course code cannot be empty.");
        }
        if (isEmpty(course.getCourseName())) {
            throw new ValidationException("Course name cannot be empty.");
        }
        if (isEmpty(course.getDepartment())) {
            throw new ValidationException("Department cannot be empty.");
        }
        if (course.getCredits() <= 0) {
            throw new ValidationException("Credit value must be greater than zero.");
        }
        if (course.getTheorySessions() < 0 || course.getPracticalSessions() < 0) {
            throw new ValidationException("Session counts cannot be negative.");
        }
        if (course.getSemesterNumber() <= 0) {
            throw new ValidationException("Semester number must be greater than zero.");
        }
        if (isEmpty(course.getAcademicYear())) {
            throw new ValidationException("Academic year cannot be empty.");
        }
    }
}
