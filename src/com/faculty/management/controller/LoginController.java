package com.faculty.management.controller;

import com.faculty.management.exception.AuthenticationException;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.gui.common.LoginFrame;
import com.faculty.management.gui.member4_student.UndergraduateDashboard;
import com.faculty.management.model.Role;
import com.faculty.management.model.User;
import com.faculty.management.service.AuthenticationService;

import javax.swing.*;

/**
 * Controller handling user login actions and role-based redirection.
 */
public class LoginController {

    private final AuthenticationService authService;

    public LoginController() {
        this.authService = new AuthenticationService();
    }

    /**
     * Handles the login action submitted from the Login GUI.
     *
     * @param loginFrame The active LoginFrame instance
     * @param username   Entered username
     * @param password   Entered password
     */
    public void handleLogin(LoginFrame loginFrame, String username, String password) {
        try {
            // 1. Authenticate & identify user role
            User user = authService.authenticate(username, password);

            // 2. Redirect based on role identification
            redirectToRoleDashboard(user);

            // 3. Close the login window
            loginFrame.dispose();

        } catch (ValidationException e) {
            loginFrame.showErrorMessage(e.getMessage());
        } catch (AuthenticationException e) {
            loginFrame.showErrorMessage(e.getMessage());
        } catch (DatabaseException e) {
            loginFrame.showErrorMessage("Database error: " + e.getMessage());
        } catch (Exception e) {
            loginFrame.showErrorMessage("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * This build exposes the student portal only.
     *
     * @param user Authenticated user
     */
    private void redirectToRoleDashboard(User user) {
        String roleName = (user.getRole() != null) ? user.getRole().getRoleName() : "";

        SwingUtilities.invokeLater(() -> {
            if (Role.UNDERGRADUATE.equalsIgnoreCase(roleName) || "STUDENT".equalsIgnoreCase(roleName)) {
                new UndergraduateDashboard(user).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null,
                        "This version is available only for undergraduate student accounts.",
                        "Student Portal Only", JOptionPane.WARNING_MESSAGE);
                showLogin();
            }
        });
    }

    /**
     * Launches the Login window.
     */
    public static void showLogin() {
        SwingUtilities.invokeLater(() -> {
            LoginController controller = new LoginController();
            LoginFrame frame = new LoginFrame(controller);
            frame.setVisible(true);
        });
    }
}
