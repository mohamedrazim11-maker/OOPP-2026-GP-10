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
 */
public class AuthenticationService {

    private final UserDAO userDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
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

        ValidationUtil.validateLoginInput(username, password);

        String trimmedUsername = username.trim();

        User user = userDAO.findByUsername(trimmedUsername);
        if (user == null || !PasswordUtil.verifyPassword(password, user.getPassword())) {
            throw new AuthenticationException("Invalid username or password.");
        }

        if (!user.isActive()) {
            throw new AuthenticationException("Account is inactive or suspended. Please contact administrator.");
        }

        return user;
    }
}
