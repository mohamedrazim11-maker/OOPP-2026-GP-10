package com.faculty.management.gui.common;

import java.awt.*;

/**
 * Primary action button inheriting from CustomButton.
 * 
 * - Inheritance: Subclass of CustomButton.
 * - Encapsulation: Provides pre-configured brand theme styling for primary actions.
 */
public class PrimaryButton extends CustomButton {

    public PrimaryButton(String text) {
        super(
            text,
            new Color(37, 99, 235), // Primary Blue
            new Color(29, 78, 216), // Hover Darker Blue
            new Color(30, 64, 175), // Pressed Deep Blue
            Color.WHITE,            // Crisp white text
            null,                   // No border needed
            8                       // 8px corner radius
        );
        setFont(new Font("Segoe UI", Font.BOLD, 14));
    }
}
