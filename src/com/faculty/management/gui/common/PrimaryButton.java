package com.faculty.management.gui.common;

import java.awt.*;

/**
 * Primary action button inheriting from CustomButton.
<<<<<<< HEAD
 * Styled with Burgundy theme matching FMS UI PDF specification.
=======
 * 
 * - Inheritance: Subclass of CustomButton.
 * - Encapsulation: Provides pre-configured brand theme styling for primary actions.
>>>>>>> Aysha
 */
public class PrimaryButton extends CustomButton {

    public PrimaryButton(String text) {
        super(
            text,
<<<<<<< HEAD
            UITheme.PRIMARY,         // Deep Burgundy #6E1624
            UITheme.PRIMARY_HOVER,   // Hover #841B2D
            UITheme.PRIMARY_DARK,    // Pressed #480E1A
            Color.WHITE,             // Crisp white text
            null,                    // No border needed
            8                        // 8px corner radius per UI PDF
        );
        setFont(new Font("Segoe UI", Font.BOLD, 13));
=======
            new Color(37, 99, 235), // Primary Blue
            new Color(29, 78, 216), // Hover Darker Blue
            new Color(30, 64, 175), // Pressed Deep Blue
            Color.WHITE,            // Crisp white text
            null,                   // No border needed
            8                       // 8px corner radius
        );
        setFont(new Font("Segoe UI", Font.BOLD, 14));
>>>>>>> Aysha
    }
}
