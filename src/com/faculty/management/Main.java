package com.faculty.management;

import com.faculty.management.controller.LoginController;
<<<<<<< HEAD
import com.faculty.management.gui.common.UITheme;
=======
>>>>>>> test

import javax.swing.*;

/**
 * Entry point for the Faculty Management System application.
 */
public class Main {

    public static void main(String[] args) {
<<<<<<< HEAD
        // Initialize modern UI system rendering (Anti-aliasing, system fonts, look & feel)
        UITheme.setupSystemRendering();
=======
        // Set native system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to default Swing Look & Feel if system L&F is unavailable
        }
>>>>>>> test

        // Launch Login Screen
        SwingUtilities.invokeLater(() -> {
            LoginController.showLogin();
        });
    }
}
