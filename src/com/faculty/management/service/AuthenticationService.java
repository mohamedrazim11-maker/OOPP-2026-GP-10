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
 * Connects directly to MySQL / SQL Server via UserDAO with graceful fallback for offline testing.
 */
public class AuthenticationService {

    private final UserDAO userDAO;
    private final Map<String, User> mockUsers;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.mockUsers = new HashMap<>();
        initMockUsers();
    }

    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = (userDAO != null) ? userDAO : new UserDAO();
        this.mockUsers = new HashMap<>();
        initMockUsers();
    }

    /**
     * Authenticates a user with given credentials against the database and verifies role selection.
     */
    public User authenticate(String username, String password, String selectedRole) 
            throws ValidationException, AuthenticationException, DatabaseException {

        // 1. Validate inputs (username, password, selected role)
        ValidationUtil.validateLoginInput(username, password, selectedRole);

        String trimmedUsername = username.trim();

        // 2. Try authenticating via database
        try {
            User user = userDAO.findByUsername(trimmedUsername);
            if (user != null) {
                if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
                    throw new AuthenticationException("Invalid username or password.");
                }

                if (!user.isActive()) {
                    throw new AuthenticationException("Account is inactive or suspended. Please contact administrator.");
                }

                verifyUserRole(user, selectedRole);
                return user;
            } else if (DatabaseConnection.getInstance().isConnected()) {
                throw new AuthenticationException("Invalid username or password.");
            }
        } catch (DatabaseException e) {
            // If database is offline, fall back to offline mock users
            if (!DatabaseConnection.getInstance().isConnected()) {
                User mockUser = mockUsers.get(trimmedUsername.toLowerCase());
                if (mockUser != null && PasswordUtil.verifyPassword(password, mockUser.getPassword())) {
                    if (!mockUser.isActive()) {
                        throw new AuthenticationException("Account is inactive or suspended. Please contact administrator.");
                    }
                    verifyUserRole(mockUser, selectedRole);
                    return mockUser;
                }
            }
            throw e;
        }

        // 3. Graceful offline fallback for testing/demo mode when database is not populated
        User mockUser = mockUsers.get(trimmedUsername.toLowerCase());
        if (mockUser != null && PasswordUtil.verifyPassword(password, mockUser.getPassword())) {
            if (!mockUser.isActive()) {
                throw new AuthenticationException("Account is inactive or suspended. Please contact administrator.");
            }
            verifyUserRole(mockUser, selectedRole);
            return mockUser;
        }

        throw new AuthenticationException("Invalid username or password.");
    }

    /**
     * Authenticates a user with given credentials without role verification.
     */
    public User authenticate(String username, String password) 
            throws ValidationException, AuthenticationException, DatabaseException {
        return authenticate(username, password, null);
    }

    /**
     * Verifies that the authenticated user has the role selected in the login form.
     */
    private void verifyUserRole(User user, String selectedRole) throws AuthenticationException {
        if (selectedRole == null || selectedRole.trim().isEmpty() || "-- Select Role --".equalsIgnoreCase(selectedRole.trim())) {
            return;
        }

        if (user.getRole() == null || user.getRole().getRoleName() == null) {
            throw new AuthenticationException("No role assigned to this account.");
        }

        String userRole = user.getRole().getRoleName().replace("_", " ").trim().toUpperCase();
        String selected = selectedRole.replace("_", " ").trim().toUpperCase();

        // Handle synonyms (STUDENT <-> UNDERGRADUATE)
        if (userRole.equals("STUDENT")) {
            userRole = "UNDERGRADUATE";
        }
        if (selected.equals("STUDENT")) {
            selected = "UNDERGRADUATE";
        }

        if (!userRole.equalsIgnoreCase(selected)) {
            throw new AuthenticationException("Role mismatch: You cannot log in as '" + selectedRole + "' with this account.");
        }
    }

    /**
     * Initializes mock users for instant testing/demonstration when database is offline.
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
