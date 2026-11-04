package com.faculty.management.gui.member1_admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;

/**
 * Reusable UI Components, Vector Icons, and Styling Utilities strictly for Member 1 (Admin Dashboard).
 * Implements pure Java Swing 2D graphics matching the University of Ruhuna FMS Admin Specification.
 */
public class AdminUIComponents {

    // Theme Color Constants matching the Ruhuna FMS Specification & Design Image
    public static final Color COLOR_BURGUNDY = new Color(92, 15, 33);             // #5C0F21 Header & Primary Action
    public static final Color COLOR_BURGUNDY_HOVER = new Color(115, 24, 43);       // #73182B Hover state
    public static final Color COLOR_BURGUNDY_DARK = new Color(72, 14, 26);         // #480E1A Active/Pressed state
    public static final Color COLOR_BURGUNDY_MODULE_HEADER = new Color(99, 16, 36);// #631024 Card Header
    public static final Color COLOR_FOOTER_BG = new Color(78, 12, 26);             // #4E0C1A Bottom footer
    public static final Color COLOR_GOLD_UNDERLINE = new Color(217, 119, 6);       // #D97706 Underline accent
    public static final Color COLOR_GOLD_ACCENT = new Color(225, 151, 47);         // #E1972F University Gold
    public static final Color COLOR_GOLD_MUTED = new Color(212, 154, 50);          // #D49A32 Muted Gold
    public static final Color COLOR_PAGE_BG = new Color(251, 249, 245);           // #FBF9F5 Warm Canvas Page
    public static final Color COLOR_CARD_BG = Color.WHITE;                        // #FFFFFF Pure White Card
    public static final Color COLOR_BORDER = new Color(229, 231, 235);            // #E5E7EB Subtle 1px Border
    public static final Color COLOR_BORDER_INPUT = new Color(209, 213, 219);      // #D1D5DB Input Border
    public static final Color COLOR_NAV_BG = new Color(245, 240, 234);            // #F5F0EA Nav Bar
    public static final Color COLOR_NAV_BORDER = new Color(229, 222, 206);        // #E5DECE Nav Border
    public static final Color COLOR_NAV_PILL = new Color(94, 15, 36);             // #5E0F24 Active Nav Pill
    public static final Color COLOR_NAV_TEXT = new Color(43, 37, 32);             // #2B2520 Nav Tab Text
    public static final Color COLOR_NAV_SEP = new Color(220, 213, 201);           // #DCD5C9 Nav Separator

    // Typography Colors
    public static final Color COLOR_TEXT_DARK = new Color(17, 24, 39);            // #111827 Headings
    public static final Color COLOR_TEXT_BODY = new Color(55, 65, 81);            // #374151 Body
    public static final Color COLOR_TEXT_MUTED = new Color(107, 114, 128);        // #6B7280 Subtitles
    public static final Color COLOR_TEXT_LIGHT = new Color(156, 163, 175);        // #9CA3AF Placeholders
    public static final Color COLOR_TEXT_SUBHEADER = new Color(230, 204, 210);    // #E6CCD2 Header Subtitle

    // Fonts
    public static final Font FONT_HEADER_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_HEADER_SUB = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_PAGE_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_PAGE_SUB = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SECTION_TITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);

    // =========================================================================
    // BUTTONS & WIDGETS
    // =========================================================================

    /**
     * Solid Burgundy Action Button (e.g. "+ Create user", "+ Add course", "+ Post notice").
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
     * White Outline Action Button (e.g. "Update user", "Refresh", "Assign course info", "Open").
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
     * Danger Outline Button (Red text and subtle red border for "Delete user", "Delete").
     */
    public static class DangerOutlineButton extends WhiteOutlineButton {
        public DangerOutlineButton(String text) {
            super(text, new Color(248, 113, 113), new Color(220, 38, 38));
        }
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
            this(initials, size, new Color(250, 245, 236), COLOR_BURGUNDY);
        }

        public AvatarIcon(String initials, int size, Color bgColor, Color fgColor) {
            this.initials = (initials != null && !initials.trim().isEmpty()) ? initials.trim().toUpperCase() : "U";
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

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
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
            this.initials = (initials != null && !initials.trim().isEmpty()) ? initials.trim().toUpperCase() : "U";
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

    public static String extractInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "AU";
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

    public static String toTitleCase(String str) {
        if (str == null || str.trim().isEmpty()) return "";
        String s = str.trim().toLowerCase();
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

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
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            if (border != null) {
                g2.setColor(border);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static JPanel createStatCard(String label, String bigValue, String subtext, boolean isAccent) {
        CardPanel card = new CardPanel(
                isAccent ? COLOR_BURGUNDY : COLOR_CARD_BG,
                isAccent ? null : COLOR_BORDER
        );
        card.setLayout(new BorderLayout(0, 4));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLabel.setForeground(isAccent ? new Color(243, 209, 215) : COLOR_TEXT_MUTED);

        JLabel lblValue = new JLabel(bigValue);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValue.setForeground(isAccent ? Color.WHITE : COLOR_TEXT_DARK);

        JLabel lblSub = new JLabel(subtext);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(isAccent ? new Color(229, 168, 178) : COLOR_TEXT_LIGHT);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(lblLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(lblValue);
        content.add(Box.createVerticalStrut(4));
        content.add(lblSub);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    public static void styleComboBox(JComboBox<?> box) {
        box.setFont(FONT_BODY);
        box.setBackground(Color.WHITE);
        box.setForeground(COLOR_TEXT_DARK);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER_INPUT, 1, true),
                new EmptyBorder(4, 6, 4, 6)
        ));
    }

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

            int ax = x + size - 2;
            int ay = y + size / 2 - 2;
            Polygon arrow = new Polygon();
            arrow.addPoint(ax - 3, ay - 4);
            arrow.addPoint(ax + 2, ay);
            arrow.addPoint(ax - 3, ay + 4);
            g2.fill(arrow);

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public static WhiteOutlineButton createRefreshButton() {
        WhiteOutlineButton btn = new WhiteOutlineButton("Refresh");
        btn.setIcon(new RefreshIcon(13, COLOR_TEXT_BODY));
        btn.setIconTextGap(6);
        return btn;
    }

    // =========================================================================
    // MODERN JAVA 2D VECTOR ICONS (EXACTLY MATCHING TARGET UI IMAGE)
    // =========================================================================

    public static class HomeIcon implements Icon {
        private final int size;
        private final Color color;
        public HomeIcon(int size, Color color) { this.size = size; this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            int cx = x + size / 2;
            int cy = y + size / 2;

            Path2D.Double roof = new Path2D.Double();
            roof.moveTo(cx, y + 2);
            roof.lineTo(x + size - 2, cy);
            roof.lineTo(x + size - 4, cy);
            roof.lineTo(x + size - 4, y + size - 2);
            roof.lineTo(x + 4, y + size - 2);
            roof.lineTo(x + 4, cy);
            roof.lineTo(x + 2, cy);
            roof.closePath();
            g2.fill(roof);

            // Door cutout
            Color bg = (c != null && c.getBackground() != null) ? c.getBackground() : COLOR_BURGUNDY;
            g2.setColor(bg);
            g2.fillRect(cx - 2, y + size - 6, 4, 5);

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public static class UserGroupIcon implements Icon {
        private final int size;
        private final Color color;
        public UserGroupIcon(int size, Color color) { this.size = size; this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            int cx = x + size / 2;
            int cy = y + size / 2;

            // Center person head & torso
            g2.fillOval(cx - 3, cy - 6, 6, 6);
            Path2D.Double centerTorso = new Path2D.Double();
            centerTorso.moveTo(cx - 5, y + size - 3);
            centerTorso.curveTo(cx - 5, cy + 1, cx + 5, cy + 1, cx + 5, y + size - 3);
            centerTorso.closePath();
            g2.fill(centerTorso);

            // Left person
            g2.fillOval(cx - 7, cy - 4, 4, 4);
            Path2D.Double leftTorso = new Path2D.Double();
            leftTorso.moveTo(cx - 9, y + size - 3);
            leftTorso.curveTo(cx - 9, cy + 2, cx - 4, cy + 2, cx - 4, y + size - 3);
            leftTorso.closePath();
            g2.fill(leftTorso);

            // Right person
            g2.fillOval(cx + 3, cy - 4, 4, 4);
            Path2D.Double rightTorso = new Path2D.Double();
            rightTorso.moveTo(cx + 4, y + size - 3);
            rightTorso.curveTo(cx + 4, cy + 2, cx + 9, cy + 2, cx + 9, y + size - 3);
            rightTorso.closePath();
            g2.fill(rightTorso);

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public static class BookIcon implements Icon {
        private final int size;
        private final Color color;
        public BookIcon(int size, Color color) { this.size = size; this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            int cx = x + size / 2;

            // Left page
            Path2D.Double leftPage = new Path2D.Double();
            leftPage.moveTo(cx - 1, y + 3);
            leftPage.curveTo(cx - 4, y + 2, x + 2, y + 2, x + 2, y + 4);
            leftPage.lineTo(x + 2, y + size - 3);
            leftPage.curveTo(cx - 4, y + size - 4, cx - 1, y + size - 3, cx - 1, y + size - 2);
            leftPage.closePath();
            g2.fill(leftPage);

            // Right page
            Path2D.Double rightPage = new Path2D.Double();
            rightPage.moveTo(cx + 1, y + 3);
            rightPage.curveTo(cx + 4, y + 2, x + size - 2, y + 2, x + size - 2, y + 4);
            rightPage.lineTo(x + size - 2, y + size - 3);
            rightPage.curveTo(cx + 4, y + size - 4, cx + 1, y + size - 3, cx + 1, y + size - 2);
            rightPage.closePath();
            g2.fill(rightPage);

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public static class MegaphoneIcon implements Icon {
        private final int size;
        private final Color color;
        public MegaphoneIcon(int size, Color color) { this.size = size; this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            int cy = y + size / 2;

            // Cone
            Path2D.Double cone = new Path2D.Double();
            cone.moveTo(x + 4, cy - 2);
            cone.lineTo(x + size - 4, cy - 6);
            cone.lineTo(x + size - 4, cy + 6);
            cone.lineTo(x + 4, cy + 2);
            cone.closePath();
            g2.fill(cone);

            // Handle
            Path2D.Double handle = new Path2D.Double();
            handle.moveTo(x + 6, cy + 2);
            handle.lineTo(x + 4, y + size - 3);
            handle.lineTo(x + 7, y + size - 3);
            handle.lineTo(x + 9, cy + 2);
            handle.closePath();
            g2.fill(handle);

            // Mouth ring
            g2.fillRoundRect(x + size - 4, cy - 7, 2, 14, 2, 2);

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public static class CalendarIcon implements Icon {
        private final int size;
        private final Color color;
        public CalendarIcon(int size, Color color) { this.size = size; this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(x + 2, y + 4, size - 4, size - 6, 3, 3);
            g2.fillRect(x + 2, y + 4, size - 4, 3);

            // Binder rings
            g2.fillRect(x + 4, y + 2, 2, 3);
            g2.fillRect(x + size - 6, y + 2, 2, 3);

            // Dots
            int dotY = y + 9;
            g2.fillRect(x + 5, dotY, 2, 2);
            g2.fillRect(x + 9, dotY, 2, 2);
            g2.fillRect(x + size - 7, dotY, 2, 2);

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public static class AlertTriangleIcon implements Icon {
        private final int size;
        private final Color color;
        public AlertTriangleIcon(int size, Color color) { this.size = size; this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            int cx = x + size / 2;
            Path2D.Double tri = new Path2D.Double();
            tri.moveTo(cx, y + 2);
            tri.lineTo(x + size - 2, y + size - 2);
            tri.lineTo(x + 2, y + size - 2);
            tri.closePath();
            g2.fill(tri);

            // Exclamation cutout
            Color cut = (c != null && c.getBackground() != null) ? c.getBackground() : new Color(122, 19, 39);
            g2.setColor(cut);
            g2.fillRect(cx - 1, y + 6, 2, 5);
            g2.fillRect(cx - 1, y + 12, 2, 2);

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public static class GearIcon implements Icon {
        private final int size;
        private final Color color;
        public GearIcon(int size, Color color) { this.size = size; this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            int cx = x + size / 2;
            int cy = y + size / 2;

            g2.setStroke(new BasicStroke(2.2f));
            g2.drawOval(cx - 4, cy - 4, 8, 8);

            for (int i = 0; i < 6; i++) {
                double rad = Math.toRadians(i * 60);
                int tx1 = cx + (int)(3.5 * Math.cos(rad));
                int ty1 = cy + (int)(3.5 * Math.sin(rad));
                int tx2 = cx + (int)(7 * Math.cos(rad));
                int ty2 = cy + (int)(7 * Math.sin(rad));
                g2.drawLine(tx1, ty1, tx2, ty2);
            }

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public static class BellIcon implements Icon {
        private final int size;
        private final Color color;
        public BellIcon(int size, Color color) { this.size = size; this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            int cx = x + size / 2;
            Path2D.Double bell = new Path2D.Double();
            bell.moveTo(cx, y + 2);
            bell.curveTo(cx - 5, y + 5, cx - 6, y + size - 5, x + 2, y + size - 3);
            bell.lineTo(x + size - 2, y + size - 3);
            bell.curveTo(cx + 6, y + size - 5, cx + 5, y + 5, cx, y + 2);
            bell.closePath();
            g2.fill(bell);

            g2.fillOval(cx - 2, y + size - 2, 4, 2);
            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public static class GradCapIcon implements Icon {
        private final int size;
        private final Color color;
        public GradCapIcon(int size, Color color) { this.size = size; this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            int cx = x + size / 2;
            int cy = y + size / 2;

            // Diamond cap
            Path2D.Double cap = new Path2D.Double();
            cap.moveTo(cx, y + 3);
            cap.lineTo(x + size - 2, cy - 2);
            cap.lineTo(cx, cy + 2);
            cap.lineTo(x + 2, cy - 2);
            cap.closePath();
            g2.fill(cap);

            // Skullcap base
            Path2D.Double base = new Path2D.Double();
            base.moveTo(cx - 5, cy + 1);
            base.lineTo(cx - 5, cy + 5);
            base.curveTo(cx - 5, y + size - 2, cx + 5, y + size - 2, cx + 5, cy + 5);
            base.lineTo(cx + 5, cy + 1);
            base.closePath();
            g2.fill(base);

            // Tassel
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(x + size - 3, cy - 1, x + size - 3, y + size - 3);

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public static class PushpinIcon implements Icon {
        private final int size;
        private final Color color;
        public PushpinIcon(int size, Color color) { this.size = size; this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            int cx = x + size / 2;

            // Pin head
            g2.fillOval(cx - 3, y + 2, 6, 5);
            // Pin neck
            g2.fillRect(cx - 4, y + 7, 8, 3);
            // Pin rim
            g2.fillRect(cx - 5, y + 10, 10, 2);
            // Pin needle
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(cx, y + 12, cx, y + size - 2);

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public static class ArrowRightIcon implements Icon {
        private final int size;
        private final Color color;
        public ArrowRightIcon(int size, Color color) { this.size = size; this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cy = y + size / 2;
            int x1 = x + 3;
            int x2 = x + size - 3;
            g2.drawLine(x1, cy, x2, cy);

            // Arrow head
            g2.drawLine(x2 - 4, cy - 4, x2, cy);
            g2.drawLine(x2 - 4, cy + 4, x2, cy);

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public static class SignOutIcon implements Icon {
        private final int size;
        private final Color color;
        public SignOutIcon(int size, Color color) { this.size = size; this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cy = y + size / 2;
            // Bracket
            Path2D.Double bracket = new Path2D.Double();
            bracket.moveTo(x + 7, y + 3);
            bracket.lineTo(x + 3, y + 3);
            bracket.lineTo(x + 3, y + size - 3);
            bracket.lineTo(x + 7, y + size - 3);
            g2.draw(bracket);

            // Arrow
            int ax1 = x + 6;
            int ax2 = x + size - 2;
            g2.drawLine(ax1, cy, ax2, cy);
            g2.drawLine(ax2 - 3, cy - 3, ax2, cy);
            g2.drawLine(ax2 - 3, cy + 3, ax2, cy);

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public static class InfoIcon implements Icon {
        private final int size;
        private final Color color;
        public InfoIcon(int size, Color color) { this.size = size; this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            int cx = x + size / 2;
            int cy = y + size / 2;

            g2.fillOval(cx - 1, cy - 4, 2, 2);
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawLine(cx, cy - 1, cx, cy + 4);

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    // =========================================================================
    // MODERN STAT CARD WITH ICON BADGE & BOTTOM-RIGHT DECORATIVE WAVE
    // =========================================================================

    public static class ModernStatCard extends JPanel {
        private final String label;
        private final String bigValue;
        private final String subtext;
        private final Color subtextColor;
        private final Icon icon;
        private final Color badgeBg;
        private final Color waveColor;

        public ModernStatCard(String label, String bigValue, String subtext, Color subtextColor,
                              Icon icon, Color badgeBg, Color waveColor) {
            this.label = label;
            this.bigValue = bigValue;
            this.subtext = subtext;
            this.subtextColor = subtextColor;
            this.icon = icon;
            this.badgeBg = badgeBg;
            this.waveColor = waveColor;
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(14, 16, 14, 16));

            JPanel inner = new JPanel(new BorderLayout(14, 0));
            inner.setOpaque(false);

            // Left circular icon badge
            JPanel badgePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(badgeBg);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    if (icon != null) {
                        int ix = (getWidth() - icon.getIconWidth()) / 2;
                        int iy = (getHeight() - icon.getIconHeight()) / 2;
                        icon.paintIcon(this, g2, ix, iy);
                    }
                    g2.dispose();
                }
            };
            badgePanel.setPreferredSize(new Dimension(42, 42));
            badgePanel.setOpaque(false);

            JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
            badgeWrapper.setOpaque(false);
            badgeWrapper.add(badgePanel);

            // Right text stack
            JPanel textStack = new JPanel();
            textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
            textStack.setOpaque(false);

            JLabel lblLabel = new JLabel(label);
            lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblLabel.setForeground(COLOR_TEXT_MUTED);

            JLabel lblVal = new JLabel(bigValue);
            lblVal.setFont(new Font("Segoe UI", Font.BOLD, 26));
            lblVal.setForeground(new Color(27, 36, 59));

            JLabel lblSub = new JLabel(subtext);
            lblSub.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblSub.setForeground(subtextColor);

            textStack.add(lblLabel);
            textStack.add(Box.createVerticalStrut(2));
            textStack.add(lblVal);
            textStack.add(Box.createVerticalStrut(2));
            textStack.add(lblSub);

            inner.add(badgeWrapper, BorderLayout.WEST);
            inner.add(textStack, BorderLayout.CENTER);

            add(inner, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // White rounded container
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 10, 10));

            // Bottom-right soft decorative wave
            if (waveColor != null) {
                Area clipArea = new Area(new RoundRectangle2D.Float(0, 0, w, h, 10, 10));
                g2.setClip(clipArea);

                Path2D.Double wave = new Path2D.Double();
                wave.moveTo(w * 0.55, h);
                wave.curveTo(w * 0.70, h * 0.65, w * 0.82, h * 0.85, w, h * 0.35);
                wave.lineTo(w, h);
                wave.closePath();

                g2.setColor(new Color(waveColor.getRed(), waveColor.getGreen(), waveColor.getBlue(), 70));
                g2.fill(wave);

                // Second softer inner wave
                Path2D.Double wave2 = new Path2D.Double();
                wave2.moveTo(w * 0.72, h);
                wave2.curveTo(w * 0.80, h * 0.80, w * 0.88, h * 0.90, w, h * 0.60);
                wave2.lineTo(w, h);
                wave2.closePath();

                g2.setColor(new Color(waveColor.getRed(), waveColor.getGreen(), waveColor.getBlue(), 110));
                g2.fill(wave2);

                g2.setClip(null);
            }

            // Subtle 1px border
            g2.setColor(COLOR_BORDER);
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, 10, 10));

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
