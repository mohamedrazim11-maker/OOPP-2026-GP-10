package com.faculty.management;

import com.faculty.management.controller.LoginController;
import com.faculty.management.gui.common.UITheme;

import javax.swing.*;

/**
 * Entry point for the Faculty Management System application.
 */
public class Main {

    public static void main(String[] args) {
        // Initialize modern UI system rendering (Anti-aliasing, system fonts, look & feel)
        UITheme.setupSystemRendering();

        // Launch Login Screen
        SwingUtilities.invokeLater(() -> {
            LoginController.showLogin();
        });
    }
}
