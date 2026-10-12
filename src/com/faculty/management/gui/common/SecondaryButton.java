package com.faculty.management.gui.common;

import java.awt.*;

/**
 * Secondary action button inheriting from CustomButton.
 * 
 * - Inheritance: Subclass of CustomButton.
 * - Encapsulation: Provides pre-configured neutral styling with subtle border for secondary actions.
 */
public class SecondaryButton extends CustomButton {

    public SecondaryButton(String text) {
        super(
            text,
            new Color(248, 250, 252), // Light Slate Background
            new Color(241, 245, 249), // Hover Slate
            new Color(226, 232, 240), // Pressed Slate
            new Color(71, 85, 105),   // Slate Text Color
            new Color(203, 213, 225), // Slate Border
            8                         // 8px corner radius
        );
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }
}
