package com.faculty.management.gui.common;

import javax.swing.*;
import java.awt.*;

/**
 * Reusable custom button component built using OOP principles:
 * 
 * - Inheritance: Extends javax.swing.JButton to inherit all standard button capabilities.
 * - Encapsulation: State properties (colors, border, corner radius) are private and accessed via getters/setters.
 * - Polymorphism: Overrides paintComponent(Graphics g) to render custom background and borders across all Look & Feels.
 */
public class CustomButton extends JButton {

    private Color backgroundColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color borderColor;
    private int cornerRadius;

    /**
     * Constructor taking text, background, and foreground colors.
     */
    public CustomButton(String text, Color bg, Color fg) {
        this(text, bg, fg, null);
    }

    /**
     * Constructor taking text, background, foreground, and border colors.
     */
    public CustomButton(String text, Color bg, Color fg, Color border) {
        this(text, bg, computeHoverColor(bg), computePressedColor(bg), fg, border, 6);
    }

    /**
     * Full constructor with customizable theme colors, states, border, and corner radius.
     */
    public CustomButton(String text, Color bg, Color hover, Color pressed, Color fg, Color border, int radius) {
        super(text);
        this.backgroundColor = bg;
        this.hoverColor = hover;
        this.pressedColor = pressed;
        this.borderColor = border;
        this.cornerRadius = radius;

        setForeground(fg);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Computes a slightly darker or lighter shade for hover state.
     */
    private static Color computeHoverColor(Color color) {
        if (color == null) return null;
        int r = Math.max(0, Math.min(255, color.getRed() - 15));
        int g = Math.max(0, Math.min(255, color.getGreen() - 15));
        int b = Math.max(0, Math.min(255, color.getBlue() - 15));
        return new Color(r, g, b);
    }

    /**
     * Computes a deeper shade for pressed state.
     */
    private static Color computePressedColor(Color color) {
        if (color == null) return null;
        int r = Math.max(0, Math.min(255, color.getRed() - 30));
        int g = Math.max(0, Math.min(255, color.getGreen() - 30));
        int b = Math.max(0, Math.min(255, color.getBlue() - 30));
        return new Color(r, g, b);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // State-based background color selection
        if (getModel().isPressed()) {
            g2.setColor(pressedColor != null ? pressedColor : backgroundColor);
        } else if (getModel().isRollover()) {
            g2.setColor(hoverColor != null ? hoverColor : backgroundColor);
        } else {
            g2.setColor(backgroundColor != null ? backgroundColor : getBackground());
        }

        // Draw rounded button background
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        // Draw border if defined
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();

        // Let superclass paint text and icon cleanly
        super.paintComponent(g);
    }

    // Encapsulation: Getters and Setters
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaint();
    }

    public Color getHoverColor() {
        return hoverColor;
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    public Color getPressedColor() {
        return pressedColor;
    }

    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        repaint();
    }
}
