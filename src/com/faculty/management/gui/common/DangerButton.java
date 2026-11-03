package com.faculty.management.gui.common;

import java.awt.*;

/**
 * Danger action button (Delete, Logout) inheriting from CustomButton.
<<<<<<< HEAD
 * Styled with soft crimson theme matching FMS UI PDF specification.
=======
 * 
 * - Inheritance: Subclass of CustomButton.
 * - Encapsulation: Pre-configured danger red styling.
>>>>>>> Aysha
 */
public class DangerButton extends CustomButton {

    public DangerButton(String text) {
        super(
            text,
<<<<<<< HEAD
            UITheme.DANGER,         // Crimson #B91C1C
            new Color(153, 27, 27), // Hover Dark Crimson
            new Color(127, 29, 29), // Pressed Deep Crimson
            Color.WHITE,            // Crisp white text
            null,                   // Border
            8                       // 8px corner radius per UI PDF
=======
            new Color(220, 38, 38), // Red 600
            new Color(185, 28, 28), // Hover Red 700
            new Color(153, 27, 27), // Pressed Red 800
            Color.WHITE,            // Crisp white text
            null,                   // Border
            6                       // Corner radius
>>>>>>> Aysha
        );
        setFont(new Font("Segoe UI", Font.BOLD, 12));
    }
}
