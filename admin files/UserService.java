package com.faculty.management.service;

import com.faculty.management.dao.RoleDAO;
import com.faculty.management.dao.UserDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.*;
import com.faculty.management.util.ValidationUtil;

import java.util.List;

/**
 * Service layer handling User business logic, validations, and real MySQL database persistence.
 * 
 * Directly connected to MySQL via UserDAO & RoleDAO with pure JDBC.
 * Completely free of mock or hardcoded data.
 * 
 * OOP Principles Applied:
 * - Classes & Objects: Instantiates User domain models (Admin, Lecturer, TechnicalOfficer, Undergraduate) and DAOs.
 * - Inheritance: Operates on subclasses extending the abstract/base User class.
 * - Abstraction: Encapsulates database queries behind business service methods.
 * - Polymorphism: Overloaded methods for entity-based and ID-based operations, plus polymorphic role mapping.
 * - Encapsulation: Private DAOs and input validation rules protecting domain state.
 * - Error and Exception Handling: Throws and handles ValidationException and DatabaseException.
 * - Database Handling: Full SQL transactions via JDBC PreparedStatements.
 */
public class UserService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;

    public UserService() {
        this.userDAO = new UserDAO();
        this.roleDAO = new RoleDAO();
    }

    public UserService(UserDAO userDAO, RoleDAO roleDAO) {
        this.userDAO = (userDAO != null) ? userDAO : new UserDAO();
        this.roleDAO = (roleDAO != null) ? roleDAO : new RoleDAO();
    }

    /**
     * Creates a new user profile with input validation and direct MySQL database persistence.
     *
     * @param user            The concrete User subclass instance (Admin, Lecturer, TechnicalOfficer, Undergraduate)
     * @param rawPassword     Plain password string
     * @param confirmPassword Confirmation password string
     * @return The created User instance with its generated database user_id
     * @throws ValidationException If validation fails (empty fields, format, duplicate username/email)
     * @throws DatabaseException   If database insertion fails
     */
    public User createUser(User user, String rawPassword, String confirmPassword) 
            throws ValidationException, DatabaseException {

        // 1. Validation (Encapsulation & Exception Handling)
        ValidationUtil.validateNewUser(user, rawPassword, confirmPassword);

        user.setPassword(rawPassword);

        // 2. Resolve Role ID from database
        if (user.getRole() != null) {
            String roleName = user.getRole().getRoleName();
            int resolvedRoleId = mapRoleNameToId(roleName);
            user.getRole().setRoleId(resolvedRoleId);
        }

        // 3. Check username uniqueness in MySQL database
        if (userDAO.existsByUsername(user.getUsername())) {
            throw new ValidationException("Username '" + user.getUsername() + "' is already taken in the database. Please choose another.");
        }

        // 4. Check email uniqueness in MySQL database
        if (userDAO.existsByEmail(user.getEmail())) {
            throw new ValidationException("Email '" + user.getEmail() + "' is already registered in the database. Please choose another.");
        }

        // 5. Direct Database Persistence using SQL INSERT
        return userDAO.createUser(user);
    }

    /**
     * Retrieves all user profiles polymorphically from the real MySQL database.
     *
     * @return List of polymorphic User objects
     * @throws DatabaseException If database query fails
     */
    public List<User> getAllUsers() throws DatabaseException {
        return userDAO.getAllUsers();
    }

    /**
     * Finds a user by their unique database ID.
     *
     * @param userId Primary key user_id
     * @return User object or null if not found
     * @throws DatabaseException If database query fails
     */
    public User getUserById(int userId) throws DatabaseException {
        if (userId <= 0) {
            return null;
        }
        return userDAO.findById(userId);
    }

    /**
     * Finds a user by their unique username from the database.
     *
     * @param username Username to search
     * @return User object or null if not found
     * @throws DatabaseException If database query fails
     */
    public User getUserByUsername(String username) throws DatabaseException {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userDAO.findByUsername(username.trim());
    }

    /**
     * Updates an existing user's details in MySQL with business rule validations.
     *
     * @param user User object containing updated details
     * @return true if updated successfully in MySQL
     * @throws ValidationException If validation fails (empty fields, format, duplicate email)
     * @throws DatabaseException   If database update fails
     */
    public boolean updateUser(User user) throws ValidationException, DatabaseException {
        // 1. Validate fields
        ValidationUtil.validateUserUpdate(user);

        // 2. Resolve Role ID
        if (user.getRole() != null) {
            String roleName = user.getRole().getRoleName();
            int resolvedRoleId = mapRoleNameToId(roleName);
            user.getRole().setRoleId(resolvedRoleId);
        }

        // 3. Check if updated email is already taken by another account in MySQL
        if (userDAO.existsByEmailExcludingUser(user.getEmail(), user.getUserId())) {
            throw new ValidationException("Email '" + user.getEmail() + "' is already in use by another database account.");
        }

        // 4. Direct Database Update using SQL UPDATE
        return userDAO.updateUser(user);
    }

    /**
     * Updates user credentials (username and/or password) directly in MySQL database.
     * Demonstrates Encapsulation, Abstraction, Error/Exception Handling, and Database Handling.
     *
     * @param userId          ID of the user
     * @param newUsername     New username
     * @param newPassword     New password (optional if only updating username)
     * @param confirmPassword Confirmation password
     * @return true if updated successfully
     * @throws ValidationException If validation constraints fail
     * @throws DatabaseException   If database update fails
     */
    public boolean updateCredentials(int userId, String newUsername, String newPassword, String confirmPassword) 
            throws ValidationException, DatabaseException {

        User existingUser = getUserById(userId);
        if (existingUser == null) {
            throw new ValidationException("User with ID " + userId + " was not found in the database.");
        }

        String currentUsername = existingUser.getUsername();
        ValidationUtil.validateCredentialsUpdate(userId, currentUsername, newUsername, newPassword, confirmPassword);

        String trimmedUsername = newUsername != null ? newUsername.trim() : "";
        String trimmedPassword = newPassword != null ? newPassword.trim() : "";
        boolean usernameChanged = !currentUsername.equalsIgnoreCase(trimmedUsername);
        boolean passwordProvided = !trimmedPassword.isEmpty();

        // Check username uniqueness if changed
        if (usernameChanged && userDAO.existsByUsernameExcludingUser(trimmedUsername, userId)) {
            throw new ValidationException("Username '" + trimmedUsername + "' is already taken in the database. Please choose another.");
        }

        // Direct SQL update via UserDAO
        if (usernameChanged && passwordProvided) {
            return userDAO.updateCredentials(userId, trimmedUsername, trimmedPassword);
        } else if (usernameChanged) {
            return userDAO.updateUsername(userId, trimmedUsername);
        } else if (passwordProvided) {
            return userDAO.updatePassword(userId, trimmedPassword);
        }

        return false;
    }

    /**
     * Overloaded method to update credentials with username and password.
     */
    public boolean updateCredentials(int userId, String newUsername, String newPassword) 
            throws ValidationException, DatabaseException {
        return updateCredentials(userId, newUsername, newPassword, newPassword);
    }

    /**
     * Polymorphic overload to update credentials using a User entity.
     * Demonstrates Polymorphism and Object-Oriented design.
     */
    public boolean updateCredentials(User user, String newUsername, String newPassword, String confirmPassword)
            throws ValidationException, DatabaseException {
        if (user == null) {
            throw new ValidationException("User entity cannot be null.");
        }
        return updateCredentials(user.getUserId(), newUsername, newPassword, confirmPassword);
    }

    /**
     * Updates only the password for a user in the database.
     */
    public boolean updatePassword(int userId, String newPassword, String confirmPassword) 
            throws ValidationException, DatabaseException {
        User existingUser = getUserById(userId);
        if (existingUser == null) {
            throw new ValidationException("User with ID " + userId + " was not found in the database.");
        }
        return updateCredentials(userId, existingUser.getUsername(), newPassword, confirmPassword);
    }

    /**
     * Updates only the username for a user in the database.
     */
    public boolean updateUsername(int userId, String newUsername) throws ValidationException, DatabaseException {
        return updateCredentials(userId, newUsername, null, null);
    }

    /**
     * Updates the active/inactive status of a user directly in the database.
     */
    public boolean updateStatus(int userId, String status) throws DatabaseException {
        return userDAO.updateStatus(userId, status);
    }

    /**
     * Assigns a new role to an existing user directly in the database.
     *
     * @param userId   ID of the user
     * @param roleName Target role name (Admin, Lecturer, Technical Officer, Undergraduate)
     * @return true if updated in MySQL
     * @throws ValidationException If validation fails (e.g. demoting root admin)
     * @throws DatabaseException   If database update fails
     */
    public boolean assignRole(int userId, String roleName) throws ValidationException, DatabaseException {
        User user = getUserById(userId);
        if (user == null) {
            throw new ValidationException("User with ID " + userId + " was not found in the database.");
        }

        ValidationUtil.validateRoleAssignment(userId, user.getUsername(), roleName);

        int resolvedRoleId = mapRoleNameToId(roleName);
        return userDAO.updateUserRole(userId, resolvedRoleId);
    }

    /**
     * Polymorphic overload to assign role using User and Role entities.
     */
    public boolean assignRole(User user, Role newRole) throws ValidationException, DatabaseException {
        if (user == null) {
            throw new ValidationException("User entity cannot be null.");
        }
        if (newRole == null || ValidationUtil.isEmpty(newRole.getRoleName())) {
            throw new ValidationException("Role entity must contain a valid role name.");
        }
        return assignRole(user.getUserId(), newRole.getRoleName());
    }

    /**
     * Deletes a user by ID from MySQL after verifying business rules and authorization.
     * Prevents deletion of the primary system administrator and invalid IDs.
     *
     * @param userId ID of the user to delete
     * @return true if deleted from database
     * @throws ValidationException If deletion rules fail
     * @throws DatabaseException   If database deletion fails
     */
    public boolean deleteUser(int userId) throws ValidationException, DatabaseException {
        User user = getUserById(userId);
        if (user == null) {
            throw new ValidationException("User with ID " + userId + " does not exist in the database.");
        }

        ValidationUtil.validateUserDeletion(userId, user.getUsername());
        return userDAO.deleteUser(userId);
    }

    /**
     * Polymorphic overload to delete a user directly using a User entity.
     */
    public boolean deleteUser(User user) throws ValidationException, DatabaseException {
        if (user == null) {
            throw new ValidationException("User entity cannot be null.");
        }
        return deleteUser(user.getUserId());
    }

    /**
     * Retrieves all roles available in the real MySQL database.
     *
     * @return List of Role objects from MySQL roles table
     * @throws DatabaseException If database query fails
     */
    public List<Role> getAllRoles() throws DatabaseException {
        return roleDAO.getAllRoles();
    }

    private int mapRoleNameToId(String roleName) {
        if (roleName == null) return 4;
        String normalized = roleName.trim().toUpperCase().replace(" ", "_");
        switch (normalized) {
            case "ADMIN":
            case "SYSTEM_ADMINISTRATOR":
                return 1;
            case "LECTURER":
                return 2;
            case "TECHNICAL_OFFICER":
            case "TECHNICALOFFICER":
                return 3;
            case "UNDERGRADUATE":
            case "STUDENT":
            default:
                return 4;
        }
    }
}
