package com.faculty.management.service;

import com.faculty.management.dao.UserDAO;
import com.faculty.management.exception.AuthenticationException;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.User;
import com.faculty.management.util.PasswordUtil;
import com.faculty.management.util.ValidationUtil;

/**
 * Service handling authentication logic, credential validation, and role identification.
 * 
 * Directly connected to MySQL via UserDAO with pure JDBC.
 * Free of mock or hardcoded data.
 * 
 * Demonstrates:
 * - Classes & Objects: Instantiates UserDAO and processes User objects.
 * - Inheritance: Handles concrete User subclasses.
 * - Abstraction: Abstracts SQL authentication queries from the Controller/GUI.
 * - Polymorphism: Overloaded authenticate methods and polymorphic User model returns.
 * - Encapsulation: Private DAO and role verification logic.
 * - Error & Exception Handling: Throws ValidationException, AuthenticationException, and DatabaseException.
 * - Database Handling: Real-time queries via pure JDBC.
 */
public class AuthenticationService {

    private final UserDAO userDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = (userDAO != null) ? userDAO : new UserDAO();
    }

    /**
     * Authenticates a user with given credentials against the MySQL database and verifies role selection.
     *
     * @param username     User-entered username
     * @param password     User-entered password
     * @param selectedRole User-selected role from login dropdown
     * @return Authenticated concrete User object (Admin, Lecturer, TechnicalOfficer, or Undergraduate)
     * @throws ValidationException     If inputs are invalid or empty
     * @throws AuthenticationException If username/password do not match, account is inactive, or role mismatches
     * @throws DatabaseException       If database communication fails
     */
    public User authenticate(String username, String password, String selectedRole) 
            throws ValidationException, AuthenticationException, DatabaseException {

        // 1. Validate inputs (username, password, selected role)
        ValidationUtil.validateLoginInput(username, password, selectedRole);

        String trimmedUsername = username.trim();

        // 2. Query MySQL database directly via UserDAO
        User user = userDAO.findByUsername(trimmedUsername);
        if (user == null) {
            throw new AuthenticationException("Invalid username or password.");
        }

        // 3. Verify password hash or plain text using PasswordUtil
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            throw new AuthenticationException("Invalid username or password.");
        }

        // 4. Verify account active status
        if (!user.isActive()) {
            throw new AuthenticationException("Account is inactive or suspended. Please contact administrator.");
        }

        // 5. Verify user's assigned role matches the selected role
        verifyUserRole(user, selectedRole);

        return user;
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
     * Demonstrates Encapsulation and Polymorphism.
     *
     * @param user         The authenticated user object
     * @param selectedRole The role chosen in the GUI dropdown
     * @throws AuthenticationException If selected role does not match user's actual role
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
}
