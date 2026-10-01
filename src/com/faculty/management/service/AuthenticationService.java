package com.faculty.management.service;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.dao.UserDAO;
import com.faculty.management.exception.AuthenticationException;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.*;
import com.faculty.management.util.PasswordUtil;
import com.faculty.management.util.ValidationUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Service handling authentication logic, credential validation, and role identification.
 */
public class AuthenticationService {

    private final UserDAO userDAO;
    private final Map<String, User> mockUsers;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.mockUsers = new HashMap<>();
        initMockUsers();
    }

    /**
     * Authenticates a user with given credentials.
     *
     * @param username User-entered username
     * @param password User-entered password
     * @return Authenticated User object (Admin, Lecturer, TechnicalOfficer, or Undergraduate)
     * @throws ValidationException If inputs are invalid or empty
     * @throws AuthenticationException If username/password do not match or account is inactive
     * @throws DatabaseException If database communication fails
     */
    public User authenticate(String username, String password) 
            throws ValidationException, AuthenticationException, DatabaseException {

        // 1. Validate inputs
        ValidationUtil.validateLoginInput(username, password);

        String trimmedUsername = username.trim();

        // 2. Try authenticating via MySQL Database
        boolean isDbConnected = DatabaseConnection.getInstance().isConnected();
        if (isDbConnected) {
            User user = userDAO.findByUsername(trimmedUsername);
            if (user == null) {
                throw new AuthenticationException("Invalid username or password.");
            }

            if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
                throw new AuthenticationException("Invalid username or password.");
            }

            if (!user.isActive()) {
                throw new AuthenticationException("Account is inactive or suspended. Please contact administrator.");
            }

            return user;
        }

        // 3. Graceful offline fallback for testing/demo mode when MySQL is offline
        User mockUser = mockUsers.get(trimmedUsername.toLowerCase());
        if (mockUser != null && PasswordUtil.verifyPassword(password, mockUser.getPassword())) {
            if (!mockUser.isActive()) {
                throw new AuthenticationException("Account is inactive or suspended. Please contact administrator.");
            }
            return mockUser;
        }

        throw new AuthenticationException("Invalid username or password. (Database offline: check credentials or start MySQL)");
    }

    /**
     * Initializes mock users for instant testing/demonstration when MySQL server is not running.
     */
    private void initMockUsers() {
        Role adminRole = new Role(1, Role.ADMIN, "System Administrator");
        Role lecRole = new Role(2, Role.LECTURER, "Lecturer");
        Role toRole = new Role(3, Role.TECHNICAL_OFFICER, "Technical Officer");
        Role stdRole = new Role(4, Role.UNDERGRADUATE, "Undergraduate Student");

        mockUsers.put("admin", new Admin(1, "admin", "admin123", "admin@fot.ruh.ac.lk", 
                "System", "Admin", adminRole, "0711234567", null, "ACTIVE"));

        mockUsers.put("lec_kamal", new Lecturer(2, "lec_kamal", "lec123", "kamal@fot.ruh.ac.lk", 
                "Kamal", "Perera", lecRole, "0771122334", null, "ACTIVE"));

        mockUsers.put("to_bandara", new TechnicalOfficer(7, "to_bandara", "to123", "bandara@fot.ruh.ac.lk", 
                "Bandara", "Herath", toRole, "0761122334", null, "ACTIVE"));

        mockUsers.put("tg2021001", new Undergraduate(11, "tg2021001", "std123", "tg2021001@fot.ruh.ac.lk", 
                "Kasun", "Kalhara", stdRole, "0701122334", null, "ACTIVE"));
    }
}
