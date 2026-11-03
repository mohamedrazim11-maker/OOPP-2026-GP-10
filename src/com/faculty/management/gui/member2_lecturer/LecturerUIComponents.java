package com.faculty.management.gui.member2_lecturer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Reusable UI Components and Styling Utilities strictly for Member 2 (Lecturer Portal).
 * Implements pure Java Swing 2D graphics matching the Ruhuna FMS Lecturer UI Specification.
 */
public class LecturerUIComponents {

    // Theme Color Constants matching the provided UI/UX Specification
    public static final Color COLOR_BURGUNDY = new Color(92, 18, 33);       // #5C1221 Header & Primary Button
    public static final Color COLOR_BURGUNDY_HOVER = new Color(115, 24, 43); // #73182B Hover
    public static final Color COLOR_BURGUNDY_DARK = new Color(72, 14, 26);   // #480E1A Active
    public static final Color COLOR_GOLD_UNDERLINE = new Color(217, 119, 6); // #D97706 Tab Active Underline
    public static final Color COLOR_PAGE_BG = new Color(251, 249, 245);     // #FBF9F5 Warm Canvas Page
    public static final Color COLOR_CARD_BG = Color.WHITE;                  // #FFFFFF Pure White Card
    public static final Color COLOR_BORDER = new Color(229, 231, 235);      // #E5E7EB Subtle 1px Border
    public static final Color COLOR_BORDER_INPUT = new Color(209, 213, 219);// #D1D5DB Input Border
    public static final Color COLOR_NAV_BG = new Color(250, 247, 242);      // #FAF7F2 Nav Bar
    public static final Color COLOR_NAV_BORDER = new Color(230, 223, 213);  // #E6DFD5 Nav Border

    // Typography Colors
    public static final Color COLOR_TEXT_DARK = new Color(17, 24, 39);      // #111827 Headings
    public static final Color COLOR_TEXT_BODY = new Color(55, 65, 81);      // #374151 Body
    public static final Color COLOR_TEXT_MUTED = new Color(107, 114, 128);  // #6B7280 Subtitles
    public static final Color COLOR_TEXT_LIGHT = new Color(156, 163, 175);  // #9CA3AF Placeholders

    // Fonts
    public static final Font FONT_HEADER_TITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_HEADER_SUB = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_PAGE_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_PAGE_SUB = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SECTION_TITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);

    /**
     * Solid Burgundy Action Button (e.g. "+ Upload material", "+ Add marks", "Calculate", "Save changes").
     */
    public static class BurgundyButton extends JButton {
        public BurgundyButton(String text) {
            super(text);
            setFont(FONT_BODY_BOLD);
            setForeground(Color.WHITE);
            setBackground(COLOR_BURGUNDY);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 16, 8, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isPressed()) {
                g2.setColor(COLOR_BURGUNDY_DARK);
            } else if (getModel().isRollover()) {
                g2.setColor(COLOR_BURGUNDY_HOVER);
            } else {
                g2.setColor(COLOR_BURGUNDY);
            }
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * White Outline Action Button (e.g. "Refresh", "Update profile", "Open").
     */
    public static class WhiteOutlineButton extends JButton {
        private final Color borderColor;
        private final Color textColor;

        public WhiteOutlineButton(String text) {
            this(text, COLOR_BORDER_INPUT, COLOR_TEXT_BODY);
        }

        public WhiteOutlineButton(String text, Color borderColor, Color textColor) {
            super(text);
            this.borderColor = borderColor;
            this.textColor = textColor;
            setFont(FONT_BODY_BOLD);
            setForeground(textColor);
            setBackground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(7, 14, 7, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isPressed()) {
                g2.setColor(new Color(243, 244, 246));
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(249, 250, 251));
            } else {
                g2.setColor(Color.WHITE);
            }
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
            g2.setColor(borderColor);
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 6, 6));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Pure Java 2D circular refresh icon so it renders flawlessly across all OS/fonts.
     */
    public static class RefreshIcon implements Icon {
        private final int size;
        private final Color color;

        public RefreshIcon(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int pad = 2;
            int d = size - pad * 2;
            g2.draw(new Arc2D.Float(x + pad, y + pad, d, d, 45, 270, Arc2D.OPEN));

            // Arrow head
            int ax = x + size - 2;
            int ay = y + size / 2 - 2;
            Polygon arrow = new Polygon();
            arrow.addPoint(ax - 3, ay - 4);
            arrow.addPoint(ax + 2, ay);
            arrow.addPoint(ax - 3, ay + 4);
            g2.fill(arrow);

            g2.dispose();
        }

        @Override
        public int getIconWidth() { return size; }

        @Override
        public int getIconHeight() { return size; }
    }

    public static WhiteOutlineButton createRefreshButton() {
        WhiteOutlineButton btn = new WhiteOutlineButton("Refresh");
        btn.setIcon(new RefreshIcon(13, COLOR_TEXT_BODY));
        btn.setIconTextGap(6);
        return btn;
    }

    /**
     * Pill Badge Component (Draws rounded pill with background color and text).
     */
    public static class PillBadge extends JLabel {
        private Color bgColor;
        private Color fgColor;

        public PillBadge(String text, Color bgColor, Color fgColor) {
            super(text, SwingConstants.CENTER);
            this.bgColor = bgColor;
            this.fgColor = fgColor;
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(fgColor);
            setOpaque(false);
            setBorder(new EmptyBorder(3, 10, 3, 10));
        }

        public void setColors(Color bg, Color fg) {
            this.bgColor = bg;
            this.fgColor = fg;
            setForeground(fg);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Circular Avatar Badge Icon.
     */
    public static class AvatarIcon implements Icon {
        private final String initials;
        private final int size;
        private final Color bgColor;
        private final Color fgColor;

        public AvatarIcon(String initials, int size) {
            this(initials, size, new Color(254, 243, 199), new Color(146, 64, 14));
        }

        public AvatarIcon(String initials, int size, Color bgColor, Color fgColor) {
            this.initials = (initials != null && !initials.trim().isEmpty()) ? initials.trim().toUpperCase() : "KP";
            this.size = size;
            this.bgColor = bgColor;
            this.fgColor = fgColor;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(bgColor);
            g2.fill(new Ellipse2D.Float(x, y, size, size));

            g2.setColor(fgColor);
            int fontSize = Math.max(10, size / 3 + 1);
            g2.setFont(new Font("Segoe UI", Font.BOLD, fontSize));

            FontMetrics fm = g2.getFontMetrics();
            int strW = fm.stringWidth(initials);
            int strH = fm.getAscent();
            int tx = x + (size - strW) / 2;
            int ty = y + (size + strH) / 2 - 2;

            g2.drawString(initials, tx, ty);
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return size; }

        @Override
        public int getIconHeight() { return size; }
    }

    /**
     * Circular Avatar JComponent for Headers and Cards.
     */
    public static class AvatarBadgeComponent extends JComponent {
        private final String initials;
        private final int size;
        private final Color bgColor;
        private final Color fgColor;

        public AvatarBadgeComponent(String initials, int size, Color bgColor, Color fgColor) {
            this.initials = (initials != null && !initials.trim().isEmpty()) ? initials.trim().toUpperCase() : "KP";
            this.size = size;
            this.bgColor = bgColor;
            this.fgColor = fgColor;
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(bgColor);
            g2.fill(new Ellipse2D.Float(0, 0, size, size));

            g2.setColor(fgColor);
            int fontSize = Math.max(10, size / 3 + 1);
            g2.setFont(new Font("Segoe UI", Font.BOLD, fontSize));

            FontMetrics fm = g2.getFontMetrics();
            int strW = fm.stringWidth(initials);
            int strH = fm.getAscent();
            int tx = (size - strW) / 2;
            int ty = (size + strH) / 2 - 2;

            g2.drawString(initials, tx, ty);
            g2.dispose();
        }
    }

    /**
     * Extracts 2-letter uppercase initials from a full name (e.g. "Kamal Perera" -> "KP").
     */
    public static String extractInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "KP";
        }
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        } else if (parts[0].length() >= 2) {
            return parts[0].substring(0, 2).toUpperCase();
        } else {
            return parts[0].toUpperCase();
        }
    }

    /**
     * Search Input Text Field with placeholder and clean border.
     */
    public static class SearchTextField extends JTextField {
        private final String placeholder;

        public SearchTextField(String placeholder, int columns) {
            super(columns);
            this.placeholder = placeholder;
            setFont(FONT_BODY);
            setForeground(COLOR_TEXT_DARK);
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_BORDER_INPUT, 1, true),
                    new EmptyBorder(6, 12, 6, 12)
            ));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !(isFocusOwner())) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(COLOR_TEXT_LIGHT);
                g2.setFont(getFont());
                Insets insets = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(placeholder, insets.left, getHeight() / 2 + fm.getAscent() / 2 - 2);
                g2.dispose();
            }
        }
    }

    /**
     * Standardized Card Panel with rounded corners and subtle border.
     */
    public static class CardPanel extends JPanel {
        private final Color bg;
        private final Color border;

        public CardPanel() {
            this(COLOR_CARD_BG, COLOR_BORDER);
        }

        public CardPanel(Color bg, Color border) {
            super();
            this.bg = bg;
            this.border = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            if (border != null) {
                g2.setColor(border);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Stat Card component for Overview page (White card or Accent Burgundy Card).
     */
    public static JPanel createStatCard(String label, String bigValue, String subtext, boolean isAccent) {
        CardPanel card = new CardPanel(
                isAccent ? COLOR_BURGUNDY : COLOR_CARD_BG,
                isAccent ? null : COLOR_BORDER
        );
        card.setLayout(new BorderLayout(0, 4));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLabel.setForeground(isAccent ? new Color(243, 209, 215) : COLOR_TEXT_MUTED);

        JLabel lblValue = new JLabel(bigValue);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setForeground(isAccent ? Color.WHITE : COLOR_TEXT_DARK);

        JLabel lblSub = new JLabel(subtext);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(isAccent ? new Color(229, 168, 178) : COLOR_TEXT_LIGHT);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(lblLabel);
        content.add(Box.createVerticalStrut(3));
        content.add(lblValue);
        content.add(Box.createVerticalStrut(3));
        content.add(lblSub);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    /**
     * Stylizes standard JComboBox with white background and clean font.
     */
    public static void styleComboBox(JComboBox<?> box) {
        box.setFont(FONT_BODY);
        box.setBackground(Color.WHITE);
        box.setForeground(COLOR_TEXT_DARK);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER_INPUT, 1, true),
                new EmptyBorder(4, 6, 4, 6)
        ));
    }
}
