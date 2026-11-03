package com.faculty.management.gui.common;

import java.awt.*;

/**
 * Danger action button (Delete, Logout) inheriting from CustomButton.
 * Styled with soft crimson theme matching FMS UI PDF specification.
 */
public class DangerButton extends CustomButton {

    public DangerButton(String text) {
        super(
            text,
            UITheme.DANGER,         // Crimson #B91C1C
            new Color(153, 27, 27), // Hover Dark Crimson
            new Color(127, 29, 29), // Pressed Deep Crimson
            Color.WHITE,            // Crisp white text
            null,                   // Border
            8                       // 8px corner radius per UI PDF
        );
        setFont(new Font("Segoe UI", Font.BOLD, 12));
    }
}
