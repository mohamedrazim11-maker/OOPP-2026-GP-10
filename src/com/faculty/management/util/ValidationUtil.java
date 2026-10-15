package com.faculty.management.util;

import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Course;
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
     * Validates a Sri Lankan contact number: 9-10 digits, optionally
     * prefixed with '+', e.g. "0771234567" or "+94771234567".
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return phone.trim().matches("^\\+?[0-9]{9,12}$");
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

    /**
     * Validates deletion of a user.
     * Prevents deletion of the root system administrator and invalid user IDs.
     *
     * @param userId   ID of the user to be deleted
     * @param username Username of the user to be deleted (optional)
     * @throws ValidationException If deletion rules are violated
     */
    public static void validateUserDeletion(int userId, String username) throws ValidationException {
        if (userId <= 0) {
            throw new ValidationException("Invalid user ID specified for deletion.");
        }
        if (userId == 1 || (username != null && "admin".equalsIgnoreCase(username.trim()))) {
            throw new ValidationException("The primary system administrator ('admin') account cannot be deleted.");
        }
    }

    /**
     * Validates role assignment parameters.
     * Prevents removing admin role from primary admin and validates role name.
     *
     * @param userId   ID of user
     * @param username Username of user
     * @param newRole  Target role name
     * @throws ValidationException If validation constraints are violated
     */
    public static void validateRoleAssignment(int userId, String username, String newRole) throws ValidationException {
        if (userId <= 0) {
            throw new ValidationException("Invalid user ID for role assignment.");
        }
        if (isEmpty(newRole) || "-- Select Role --".equalsIgnoreCase(newRole.trim())) {
            throw new ValidationException("Please select a valid role to assign.");
        }
        if (userId == 1 && !"ADMIN".equalsIgnoreCase(newRole.trim()) && !"SYSTEM_ADMINISTRATOR".equalsIgnoreCase(newRole.trim())) {
            throw new ValidationException("Cannot demote the primary system administrator ('admin') from the Admin role.");
        }
    }

    /**
     * Validates input fields for maintaining and updating user credentials (username and password).
     * Demonstrates Encapsulation and Error/Exception Handling.
     *
     * @param userId          ID of the user being modified
     * @param currentUsername Existing username before edit (optional, for change detection)
     * @param newUsername     New or existing username
     * @param newPassword     New password (optional if only updating username)
     * @param confirmPassword Confirmation password
     * @throws ValidationException If any validation rule fails
     */
    public static void validateCredentialsUpdate(int userId, String currentUsername, String newUsername,
                                                 String newPassword, String confirmPassword) throws ValidationException {
        if (userId <= 0) {
            throw new ValidationException("Invalid user ID specified for maintaining credentials.");
        }

        if (isEmpty(newUsername)) {
            throw new ValidationException("Username cannot be empty.");
        }

        if (newUsername.trim().length() < 3) {
            throw new ValidationException("Username must be at least 3 characters long.");
        }

        if (!newUsername.trim().matches("^[a-zA-Z0-9_]+$")) {
            throw new ValidationException("Username can only contain alphanumeric characters and underscores.");
        }

        // Protect root system administrator username
        if (userId == 1 && !"admin".equalsIgnoreCase(newUsername.trim())) {
            throw new ValidationException("The username for the primary system administrator ('admin') cannot be changed.");
        }

        boolean hasPassword = !isEmpty(newPassword);
        boolean usernameChanged = (currentUsername != null) && !currentUsername.trim().equalsIgnoreCase(newUsername.trim());

        // Ensure at least one credential field is being modified
        if (!hasPassword && !usernameChanged && currentUsername != null) {
            throw new ValidationException("No changes detected. Please enter a new username or a new password.");
        }

        // Validate password when provided
        if (hasPassword) {
            if (newPassword.trim().length() < 6) {
                throw new ValidationException("New password must be at least 6 characters long.");
            }
            if (confirmPassword != null && !newPassword.equals(confirmPassword)) {
                throw new ValidationException("New password and confirmation password do not match.");
            }
        }
    }

    /**
     * Overloaded validation method for maintaining credentials without current username comparison.
     */
    public static void validateCredentialsUpdate(int userId, String newUsername, String newPassword, String confirmPassword)
            throws ValidationException {
        validateCredentialsUpdate(userId, null, newUsername, newPassword, confirmPassword);
    }

    /**
     * Validates fields of a Course object.
     * Demonstrates Encapsulation and Error/Exception Handling.
     *
     * @param course Course entity to validate
     * @throws ValidationException If any validation constraint fails
     */
    public static void validateCourse(Course course) throws ValidationException {
        if (course == null) {
            throw new ValidationException("Course information cannot be null.");
        }
        if (isEmpty(course.getCourseCode())) {
            throw new ValidationException("Course Code is required (e.g. ICT2132).");
        }
        String trimmedCode = course.getCourseCode().trim();
        if (trimmedCode.length() < 4) {
            throw new ValidationException("Course Code must be at least 4 characters long.");
        }
        if (!trimmedCode.matches("^[a-zA-Z]{2,5}[0-9]{3,4}$")) {
            throw new ValidationException("Invalid Course Code format. Example valid formats: ICT2132, BST2113, ENG2113.");
        }
        if (isEmpty(course.getCourseName())) {
            throw new ValidationException("Course Name is required.");
        }
        if (course.getCourseName().trim().length() < 3) {
            throw new ValidationException("Course Name must be at least 3 characters long.");
        }
        if (course.getCreditValue() <= 0 || course.getCreditValue() > 10) {
            throw new ValidationException("Credit Value must be between 1 and 10.");
        }
        if (course.getTheoryHours() < 0) {
            throw new ValidationException("Theory hours cannot be negative.");
        }
        if (course.getPracticalHours() < 0) {
            throw new ValidationException("Practical hours cannot be negative.");
        }
        if (course.getTheoryHours() == 0 && course.getPracticalHours() == 0) {
            throw new ValidationException("A course module must have at least one of theory hours or practical hours greater than 0.");
        }
        if (isEmpty(course.getDepartment()) || "-- Select Department --".equals(course.getDepartment().trim())) {
            throw new ValidationException("Please select an academic Department.");
        }
        if (isEmpty(course.getSemester())) {
            throw new ValidationException("Please select an academic Semester.");
        }
    }

    /**
     * Polymorphic overload to validate course fields directly from parameters.
     * Demonstrates Polymorphism, Encapsulation, and Error/Exception Handling.
     */
    public static void validateCourse(String courseCode, String courseName, int creditValue,
                                      int theoryHours, int practicalHours, String department, String semester)
            throws ValidationException {
        Course c = new Course();
        c.setCourseCode(courseCode);
        c.setCourseName(courseName);
        c.setCreditValue(creditValue);
        c.setTheoryHours(theoryHours);
        c.setPracticalHours(practicalHours);
        c.setDepartment(department);
        c.setSemester(semester);
        validateCourse(c);
    }

    /**
     * Validates course assignment information fields.
     * Demonstrates Encapsulation and Error/Exception Handling.
     *
     * @param course Course entity containing assigned information
     * @throws ValidationException If validation fails
     */
    public static void validateCourseAssignment(Course course) throws ValidationException {
        if (course == null) {
            throw new ValidationException("Course information cannot be null.");
        }
        if (course.getCourseId() <= 0 && isEmpty(course.getCourseCode())) {
            throw new ValidationException("Please select a valid course to assign information.");
        }
        if (isEmpty(course.getDepartment()) || "-- Select Department --".equals(course.getDepartment().trim())) {
            throw new ValidationException("Please assign a valid academic Department.");
        }
        if (isEmpty(course.getSemester())) {
            throw new ValidationException("Please assign a valid Semester.");
        }
        if (course.getCreditValue() <= 0 || course.getCreditValue() > 10) {
            throw new ValidationException("Credit value must be between 1 and 10.");
        }
        if (course.getTheoryHours() < 0) {
            throw new ValidationException("Theory hours cannot be negative.");
        }
        if (course.getPracticalHours() < 0) {
            throw new ValidationException("Practical hours cannot be negative.");
        }
    }

    /**
     * Overloaded validation method for course assignment parameters.
     */
    public static void validateCourseAssignment(int courseId, String department, String semester,
                                                int creditValue, int theoryHours, int practicalHours) throws ValidationException {
        if (courseId <= 0) {
            throw new ValidationException("Please select a valid course to assign information.");
        }
        if (isEmpty(department) || "-- Select Department --".equals(department.trim())) {
            throw new ValidationException("Please assign a valid academic Department.");
        }
        if (isEmpty(semester)) {
            throw new ValidationException("Please assign a valid Semester.");
        }
        if (creditValue <= 0 || creditValue > 10) {
            throw new ValidationException("Assigned Credit Value must be between 1 and 10.");
        }
        if (theoryHours < 0) {
            throw new ValidationException("Theory hours cannot be negative.");
        }
        if (practicalHours < 0) {
            throw new ValidationException("Practical hours cannot be negative.");
        }
    }

    /**
     * Validates an existing course object for update operations.
     * Demonstrates Encapsulation and Error/Exception Handling.
     */
    public static void validateCourseUpdate(Course course) throws ValidationException {
        if (course == null) {
            throw new ValidationException("Course entity cannot be null for update.");
        }
        if (course.getCourseId() <= 0) {
            throw new ValidationException("Invalid Course ID specified for update.");
        }
        validateCourse(course);
    }

    /**
     * Validates course deletion parameters.
     * Demonstrates Error and Exception Handling.
     */
    public static void validateCourseDeletion(int courseId) throws ValidationException {
        if (courseId <= 0) {
            throw new ValidationException("Invalid Course ID specified for deletion.");
        }
    }

    /**
     * Polymorphic overload to validate course deletion by course code string.
     */
    public static void validateCourseDeletion(String courseCode) throws ValidationException {
        if (isEmpty(courseCode)) {
            throw new ValidationException("Course Code cannot be empty for deletion.");
        }
    }

    /**
     * Validates course lookup parameters for View Courses.
     */
    public static void validateCourseLookup(int courseId) throws ValidationException {
        if (courseId <= 0) {
            throw new ValidationException("Invalid Course ID specified for lookup.");
        }
    }

    /**
     * Polymorphic overload to validate course lookup by course code.
     */
    public static void validateCourseLookup(String courseCode) throws ValidationException {
        if (isEmpty(courseCode)) {
            throw new ValidationException("Course Code cannot be empty for lookup.");
        }
    }
}
