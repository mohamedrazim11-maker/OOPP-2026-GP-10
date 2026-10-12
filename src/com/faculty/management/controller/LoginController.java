package com.faculty.management.controller;

import com.faculty.management.exception.AuthenticationException;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.gui.common.LoginFrame;
<<<<<<< HEAD
=======
import com.faculty.management.gui.member1_admin.AdminDashboard;
import com.faculty.management.gui.member2_lecturer.LecturerDashboard;
import com.faculty.management.gui.member3_attendance.TechnicalOfficerDashboard;
>>>>>>> test
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
<<<<<<< HEAD
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
=======
     * Handles the login action submitted from the Login GUI with role verification.
     *
     * @param loginFrame   The active LoginFrame instance
     * @param username     Entered username
     * @param password     Entered password
     * @param selectedRole Selected role from dropdown
     */
    public void handleLogin(LoginFrame loginFrame, String username, String password, String selectedRole) {
        try {
            // 1. Authenticate & verify user role
            User user = authService.authenticate(username, password, selectedRole);

            // 2. Redirect based on role identification (Polymorphism & Abstraction)
>>>>>>> test
            redirectToRoleDashboard(user);

            // 3. Close the login window
            loginFrame.dispose();

<<<<<<< HEAD
        } catch (ValidationException e) {
            loginFrame.showErrorMessage(e.getMessage());
        } catch (AuthenticationException e) {
=======
        } catch (ValidationException | AuthenticationException e) {
>>>>>>> test
            loginFrame.showErrorMessage(e.getMessage());
        } catch (DatabaseException e) {
            loginFrame.showErrorMessage("Database error: " + e.getMessage());
        } catch (Exception e) {
            loginFrame.showErrorMessage("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
<<<<<<< HEAD
     * This build exposes the student portal only.
=======
     * Overloaded login handler for backward compatibility.
     */
    public void handleLogin(LoginFrame loginFrame, String username, String password) {
        handleLogin(loginFrame, username, password, null);
    }

    /**
     * Identifies the role and redirects the user to the corresponding dashboard.
>>>>>>> test
     *
     * @param user Authenticated user
     */
    private void redirectToRoleDashboard(User user) {
        String roleName = (user.getRole() != null) ? user.getRole().getRoleName() : "";

        SwingUtilities.invokeLater(() -> {
<<<<<<< HEAD
            if (Role.UNDERGRADUATE.equalsIgnoreCase(roleName) || "STUDENT".equalsIgnoreCase(roleName)) {
                new UndergraduateDashboard(user).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null,
                        "This version is available only for undergraduate student accounts.",
                        "Student Portal Only", JOptionPane.WARNING_MESSAGE);
                showLogin();
=======
            switch (roleName.toUpperCase()) {
                case Role.ADMIN:
                    new AdminDashboard(user).setVisible(true);
                    break;
                case Role.LECTURER:
                    new LecturerDashboard(user).setVisible(true);
                    break;
                case Role.TECHNICAL_OFFICER:
                    new TechnicalOfficerDashboard(user).setVisible(true);
                    break;
                case Role.UNDERGRADUATE:
                case "STUDENT":
                    new UndergraduateDashboard(user).setVisible(true);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, 
                            "Unrecognized user role: " + roleName, 
                            "Role Error", 
                            JOptionPane.ERROR_MESSAGE);
                    showLogin();
                    break;
>>>>>>> test
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
