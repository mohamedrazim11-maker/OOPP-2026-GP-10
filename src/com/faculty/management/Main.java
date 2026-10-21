package com.faculty.management;

import com.faculty.management.controller.LoginController;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
<<<<<<< HEAD
        SwingUtilities.invokeLater(LoginController::showLogin);
=======
        // Set native system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to default Swing Look & Feel if system L&F is unavailable
        }

        // Launch Login Screen
        SwingUtilities.invokeLater(() -> {
            LoginController.showLogin();
        });
>>>>>>> test
    }
}
