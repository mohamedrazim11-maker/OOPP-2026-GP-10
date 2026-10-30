package com.faculty.management.gui.common;

import java.awt.*;

/**
 * Primary action button inheriting from CustomButton.
 * Styled with Burgundy theme matching FMS UI PDF specification.
 */
public class PrimaryButton extends CustomButton {

    public PrimaryButton(String text) {
        super(
            text,
            UITheme.PRIMARY,         // Deep Burgundy #6E1624
            UITheme.PRIMARY_HOVER,   // Hover #841B2D
            UITheme.PRIMARY_DARK,    // Pressed #480E1A
            Color.WHITE,             // Crisp white text
            null,                    // No border needed
            8                        // 8px corner radius per UI PDF
        );
        setFont(new Font("Segoe UI", Font.BOLD, 13));
    }
}
