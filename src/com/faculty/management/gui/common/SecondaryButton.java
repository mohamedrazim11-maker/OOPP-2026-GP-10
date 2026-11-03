package com.faculty.management.gui.common;

import java.awt.*;

/**
 * Secondary action button inheriting from CustomButton.
 * Styled with clean white surface and soft border matching FMS UI PDF specification.
 */
public class SecondaryButton extends CustomButton {

    public SecondaryButton(String text) {
        super(
            text,
            Color.WHITE,                             // Clean white background
            UITheme.BG_PAGE,                         // Hover soft cream #F6EFE6
            UITheme.BORDER_LIGHT,                    // Pressed subtle tint
            UITheme.TEXT_BODY,                       // Slate charcoal #374151
            UITheme.BORDER_INPUT,                    // Border #D5CEC4
            8                                        // 8px corner radius per UI PDF
        );
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }
}
