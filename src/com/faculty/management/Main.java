package com.faculty.management;

import com.faculty.management.controller.LoginController;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginController::showLogin);
    }
}
