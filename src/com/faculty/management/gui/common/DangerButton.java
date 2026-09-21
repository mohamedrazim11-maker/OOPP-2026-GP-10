package com.faculty.management.gui.common;

import java.awt.*;

/**
 * Danger action button (Delete, Logout) inheriting from CustomButton.
 * 
 * - Inheritance: Subclass of CustomButton.
 * - Encapsulation: Pre-configured danger red styling.
 */
public class DangerButton extends CustomButton {

    public DangerButton(String text) {
        super(
            text,
            new Color(220, 38, 38), // Red 600
            new Color(185, 28, 28), // Hover Red 700
            new Color(153, 27, 27), // Pressed Red 800
            Color.WHITE,            // Crisp white text
            null,                   // Border
            6                       // Corner radius
        );
        setFont(new Font("Segoe UI", Font.BOLD, 12));
    }
}
