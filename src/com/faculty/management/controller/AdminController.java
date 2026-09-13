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
     * Polymorphically instantiates the appropriate User subclass (Admin, Lecturer, TechnicalOfficer, Undergraduate)
     * based on role selection.
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

        // 1. Resolve Role object
        Role role = new Role();
        role.setRoleName(roleName != null ? roleName.toUpperCase().replace(" ", "_") : "UNDERGRADUATE");

        // 2. Factory creation demonstrating Polymorphism & Inheritance
        User newUser;
        String normalizedRole = (roleName != null) ? roleName.toUpperCase().replace(" ", "_") : "";

        if (Role.ADMIN.equals(normalizedRole) || "ADMINISTRATOR".equals(normalizedRole)) {
            newUser = new Admin();
        } else if (Role.LECTURER.equals(normalizedRole)) {
            Lecturer lecturer = new Lecturer();
            lecturer.setDepartment(department);
            newUser = lecturer;
        } else if (Role.TECHNICAL_OFFICER.equals(normalizedRole) || "TECHNICALOFFICER".equals(normalizedRole)) {
            TechnicalOfficer to = new TechnicalOfficer();
            to.setDepartment(department);
            newUser = to;
        } else {
            Undergraduate student = new Undergraduate();
            student.setDepartment(department);
            student.setBatch(batch);
            newUser = student;
        }

        // 3. Populate encapsulated properties
        newUser.setUsername(username != null ? username.trim() : "");
        newUser.setFirstName(firstName != null ? firstName.trim() : "");
        newUser.setLastName(lastName != null ? lastName.trim() : "");
        newUser.setEmail(email != null ? email.trim() : "");
        newUser.setContactNo(contactNo != null ? contactNo.trim() : "");
        newUser.setRole(role);
        newUser.setStatus(status != null ? status : "ACTIVE");

        // 4. Delegate to business service layer
        return userService.createUser(newUser, password, confirmPassword);
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
     * Updates an existing user's profile.
     *
     * @param user Updated User object
     * @return true if updated
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean handleUpdateUser(User user) throws ValidationException, DatabaseException {
        return userService.updateUser(user);
    }

    /**
     * Updates user credentials (username and password).
     *
     * @param userId      User ID
     * @param username    New username
     * @param newPassword New password
     * @return true if updated
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean handleUpdateCredentials(int userId, String username, String newPassword) 
            throws ValidationException, DatabaseException {
        return userService.updateCredentials(userId, username, newPassword);
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
     * Deletes a user by ID.
     *
     * @param userId User ID
     * @return true if deleted
     * @throws DatabaseException If database error occurs
     */
    public boolean handleDeleteUser(int userId) throws DatabaseException {
        return userService.deleteUser(userId);
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
