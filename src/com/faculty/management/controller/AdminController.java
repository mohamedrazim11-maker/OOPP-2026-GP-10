package com.faculty.management.controller;

import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.*;
import com.faculty.management.service.UserService;

import java.util.List;

/**
 * Controller handling Administrator operations for User Management.
 * Demonstrates Classes & Objects, Inheritance, Abstraction, Polymorphism, 
 * Encapsulation, and Error/Exception Handling.
 */
public class AdminController {

    private final UserService userService;

    public AdminController() {
        this.userService = new UserService();
    }

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles the creation of a new user in the system.
     * Polymorphically instantiates the appropriate User subclass based on role selection.
     *
     * @param roleName        Selected role name (e.g. "Admin", "Lecturer", "Technical Officer", "Undergraduate")
     * @param username        Username
     * @param password        Password
     * @param confirmPassword Confirmation password
     * @param firstName       First name
     * @param lastName        Last name
     * @param email           Email address
     * @param contactNo       Contact number
     * @param status          Status ("ACTIVE", "INACTIVE")
     * @param department      Department (optional)
     * @param batch           Batch (optional for undergraduates)
     * @return Created User object
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public User handleCreateUser(String roleName, String username, String password, String confirmPassword,
                                 String firstName, String lastName, String email, String contactNo,
                                 String status, String department, String batch) 
            throws ValidationException, DatabaseException {

        Role role = new Role();
        role.setRoleName(roleName != null ? roleName.toUpperCase().replace(" ", "_") : "UNDERGRADUATE");

        // Factory creation demonstrating Polymorphism & Inheritance
        User newUser = createUserInstance(roleName, department, batch);

        newUser.setUsername(username != null ? username.trim() : "");
        newUser.setFirstName(firstName != null ? firstName.trim() : "");
        newUser.setLastName(lastName != null ? lastName.trim() : "");
        newUser.setEmail(email != null ? email.trim() : "");
        newUser.setContactNo(contactNo != null ? contactNo.trim() : "");
        newUser.setRole(role);
        newUser.setStatus(status != null ? status : "ACTIVE");

        return userService.createUser(newUser, password, confirmPassword);
    }

    /**
     * Handles updating an existing user's profile with backend database persistence.
     *
     * @param userId     User ID
     * @param roleName   User Role
     * @param username   Username
     * @param firstName  First Name
     * @param lastName   Last Name
     * @param email      Email Address
     * @param contactNo  Contact Number
     * @param status     Account Status
     * @param department Department
     * @param batch      Batch / Intake
     * @return true if updated successfully
     * @throws ValidationException If validation constraints fail
     * @throws DatabaseException   If database error occurs
     */
    public boolean handleUpdateUser(int userId, String roleName, String username,
                                    String firstName, String lastName, String email,
                                    String contactNo, String status, String department, String batch) 
            throws ValidationException, DatabaseException {

        Role role = new Role();
        role.setRoleName(roleName != null ? roleName.toUpperCase().replace(" ", "_") : "UNDERGRADUATE");

        User updatedUser = createUserInstance(roleName, department, batch);
        updatedUser.setUserId(userId);
        updatedUser.setUsername(username != null ? username.trim() : "");
        updatedUser.setFirstName(firstName != null ? firstName.trim() : "");
        updatedUser.setLastName(lastName != null ? lastName.trim() : "");
        updatedUser.setEmail(email != null ? email.trim() : "");
        updatedUser.setContactNo(contactNo != null ? contactNo.trim() : "");
        updatedUser.setRole(role);
        updatedUser.setStatus(status != null ? status : "ACTIVE");

        return userService.updateUser(updatedUser);
    }

    /**
     * Overloaded method to update user directly via User entity.
     */
    public boolean handleUpdateUser(User user) throws ValidationException, DatabaseException {
        return userService.updateUser(user);
    }

    /**
     * Helper factory method to create the appropriate concrete User subclass (Polymorphism & Inheritance).
     */
    private User createUserInstance(String roleName, String department, String batch) {
        String normalizedRole = (roleName != null) ? roleName.toUpperCase().replace(" ", "_") : "";

        if (Role.ADMIN.equals(normalizedRole) || "ADMINISTRATOR".equals(normalizedRole)) {
            return new Admin();
        } else if (Role.LECTURER.equals(normalizedRole)) {
            Lecturer lecturer = new Lecturer();
            lecturer.setDepartment(department);
            return lecturer;
        } else if (Role.TECHNICAL_OFFICER.equals(normalizedRole) || "TECHNICALOFFICER".equals(normalizedRole)) {
            TechnicalOfficer to = new TechnicalOfficer();
            to.setDepartment(department);
            return to;
        } else {
            Undergraduate student = new Undergraduate();
            student.setDepartment(department);
            student.setBatch(batch);
            return student;
        }
    }

    /**
     * Retrieves all users from the backend service.
     *
     * @return List of User objects
     * @throws DatabaseException If database error occurs
     */
    public List<User> loadAllUsers() throws DatabaseException {
        return userService.getAllUsers();
    }

    /**
     * Retrieves a single user profile with all details by User ID (Backend View User).
     *
     * @param userId ID of the user
     * @return User object
     * @throws ValidationException If ID is invalid or user not found
     * @throws DatabaseException   If database error occurs
     */
    public User handleViewUser(int userId) throws ValidationException, DatabaseException {
        if (userId <= 0) {
            throw new ValidationException("Invalid User ID specified.");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new ValidationException("User with ID " + userId + " was not found in the database.");
        }
        return user;
    }

    /**
     * Retrieves a single user profile by username.
     *
     * @param username Username of the user
     * @return User object
     * @throws ValidationException If username is invalid or user not found
     * @throws DatabaseException   If database error occurs
     */
    public User handleViewUserByUsername(String username) throws ValidationException, DatabaseException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty.");
        }
        User user = userService.getUserByUsername(username.trim());
        if (user == null) {
            throw new ValidationException("User with username '" + username + "' was not found.");
        }
        return user;
    }

    /**
     * Updates user credentials (username and/or password) with validation and database handling.
     *
     * @param userId          User ID
     * @param username        New username
     * @param newPassword     New password
     * @param confirmPassword Confirmation password
     * @return true if updated
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean handleUpdateCredentials(int userId, String username, String newPassword, String confirmPassword) 
            throws ValidationException, DatabaseException {
        return userService.updateCredentials(userId, username, newPassword, confirmPassword);
    }

    /**
     * Overloaded method to update credentials with username and password.
     */
    public boolean handleUpdateCredentials(int userId, String username, String newPassword) 
            throws ValidationException, DatabaseException {
        return userService.updateCredentials(userId, username, newPassword, newPassword);
    }

    /**
     * Polymorphic overload to update credentials using a User model entity.
     * Demonstrates Polymorphism and Object-Oriented design.
     *
     * @param user            Target User entity
     * @param username        New username
     * @param newPassword     New password
     * @param confirmPassword Confirmation password
     * @return true if updated
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean handleUpdateCredentials(User user, String username, String newPassword, String confirmPassword) 
            throws ValidationException, DatabaseException {
        return userService.updateCredentials(user, username, newPassword, confirmPassword);
    }

    /**
     * Updates only the password for a user.
     *
     * @param userId          User ID
     * @param newPassword     New password
     * @param confirmPassword Confirmation password
     * @return true if updated
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean handleUpdatePassword(int userId, String newPassword, String confirmPassword) 
            throws ValidationException, DatabaseException {
        return userService.updatePassword(userId, newPassword, confirmPassword);
    }

    /**
     * Updates only the username for a user.
     *
     * @param userId      User ID
     * @param newUsername New username
     * @return true if updated
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean handleUpdateUsername(int userId, String newUsername) 
            throws ValidationException, DatabaseException {
        return userService.updateUsername(userId, newUsername);
    }

    /**
     * Updates user active/inactive status.
     *
     * @param userId User ID
     * @param status Status ("ACTIVE" or "INACTIVE")
     * @return true if updated
     * @throws DatabaseException If database error occurs
     */
    public boolean handleUpdateStatus(int userId, String status) throws DatabaseException {
        return userService.updateStatus(userId, status);
    }

    /**
     * Deletes a user by ID with validation and database handling.
     *
     * @param userId User ID
     * @return true if deleted
     * @throws ValidationException If validation fails (e.g. deleting root admin)
     * @throws DatabaseException   If database error occurs
     */
    public boolean handleDeleteUser(int userId) throws ValidationException, DatabaseException {
        return userService.deleteUser(userId);
    }

    /**
     * Polymorphic overload to delete a user by passing a User entity.
     *
     * @param user User object
     * @return true if deleted
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean handleDeleteUser(User user) throws ValidationException, DatabaseException {
        if (user == null) {
            throw new ValidationException("User cannot be null.");
        }
        return userService.deleteUser(user);
    }

    /**
     * Handles assigning a new role to a user.
     *
     * @param userId   User ID
     * @param roleName Role Name (e.g. "Admin", "Lecturer", "Technical Officer", "Undergraduate")
     * @return true if updated
     * @throws ValidationException If validation fails (e.g. demoting root admin)
     * @throws DatabaseException   If database error occurs
     */
    public boolean handleAssignRole(int userId, String roleName) throws ValidationException, DatabaseException {
        return userService.assignRole(userId, roleName);
    }

    /**
     * Polymorphic overload to assign role using User and Role entities.
     *
     * @param user    Target User entity
     * @param newRole New Role entity
     * @return true if updated
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean handleAssignRole(User user, Role newRole) throws ValidationException, DatabaseException {
        return userService.assignRole(user, newRole);
    }

    /**
     * Retrieves all system roles.
     *
     * @return List of roles
     * @throws DatabaseException If database error occurs
     */
    public List<Role> getAllRoles() throws DatabaseException {
        return userService.getAllRoles();
    }
}
