package com.faculty.management.gui.common;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Unified university-style UI design system for the Faculty Management System.
 * Pure Java Swing / Java 2D - No external dependencies.
 * Provides consistent colors, typography, custom components, tables, and buttons.
 */
public class UITheme {

    // =========================================================================
    // COLOR PALETTE (From "FMS — Faculty Management System UI.pdf")
    // =========================================================================
    public static final Color MAROON_SIDEBAR = new Color(92, 18, 33);        // #5C1221 Solid University Burgundy
    public static final Color PRIMARY        = new Color(110, 22, 36);       // #6E1624 Primary Action Burgundy
    public static final Color PRIMARY_HOVER  = new Color(132, 27, 45);       // #841B2D Hover Burgundy
    public static final Color PRIMARY_DARK   = new Color(72, 14, 26);        // #480E1A Pressed Burgundy
    public static final Color PRIMARY_LIGHT  = new Color(248, 239, 240);     // #F8EFF0
    public static final Color PRIMARY_TINT   = new Color(217, 179, 114);

    // Sidebar Gold Active Pill
    public static final Color GOLD_ACTIVE_BG = new Color(232, 175, 68);      // #E8AF44 Warm Golden-Yellow
    public static final Color GOLD_ACTIVE_FG = new Color(44, 30, 20);        // #2C1E14 Dark Charcoal/Brown
    public static final Color NAV_INACTIVE_FG= new Color(252, 238, 235);     // #FCEEEB Soft Cream White
    public static final Color NAV_INACTIVE_DOT = new Color(209, 165, 158);   // #D1A59E Subtle dot

    // Backgrounds
    public static final Color BG_PAGE        = new Color(246, 239, 230);     // #F6EFE6 Warm Soft Ivory / Cream
    public static final Color BG_CARD        = new Color(255, 255, 255);     // #FFFFFF Pure White
    public static final Color BG_HEADER      = new Color(241, 233, 221);     // #F1E9DD Warm Soft Sand / Beige
    public static final Color BG_ALT_ROW     = new Color(252, 249, 245);

    // Borders & Focus
    public static final Color BORDER_LIGHT   = new Color(236, 229, 216);     // #ECE5D8 Warm subtle border
    public static final Color BORDER_INPUT   = new Color(213, 206, 196);     // #D5CEC4 Input border
    public static final Color BORDER_FOCUS   = new Color(110, 22, 36);

    // Typography
    public static final Color TEXT_MAIN      = new Color(24, 24, 27);        // #18181B Heading Dark
    public static final Color TEXT_BODY      = new Color(55, 65, 81);        // #374151 Body Slate
    public static final Color TEXT_MUTED     = new Color(107, 114, 128);     // #6B7280 Muted Subtitles
    public static final Color TEXT_LIGHT     = new Color(156, 163, 175);     // #9CA3AF Placeholders

    // Status Badges & Accents (Pastel Pills matching UI PDF)
    public static final Color SUCCESS        = new Color(21, 128, 61);       // #15803D
    public static final Color SUCCESS_LIGHT  = new Color(220, 252, 231);     // #DCFCE7 Mint Green
    public static final Color SUCCESS_DARK   = new Color(21, 128, 61);
    public static final Color SUCCESS_BORDER = new Color(187, 247, 208);

    public static final Color WARNING        = new Color(180, 83, 9);        // #B45309 Dark Amber
    public static final Color WARNING_LIGHT  = new Color(254, 243, 199);     // #FEF3C7 Light Amber
    public static final Color WARNING_DARK   = new Color(180, 83, 9);
    public static final Color WARNING_BORDER = new Color(253, 230, 138);

    public static final Color DANGER         = new Color(185, 28, 28);       // #B91C1C Dark Crimson
    public static final Color DANGER_LIGHT   = new Color(254, 226, 226);     // #FEE2E2 Light Rose
    public static final Color DANGER_DARK    = new Color(185, 28, 28);
    public static final Color DANGER_BORDER  = new Color(254, 202, 202);

    public static final Color ACCENT_ROSE    = new Color(190, 18, 60);       // #BE123C
    public static final Color ACCENT_ROSE_LIGHT = new Color(255, 228, 230);  // #FFE4E6 Soft Coral
    public static final Color ACCENT_ROSE_BORDER = new Color(254, 205, 211);

    public static final Color ACCENT_PURPLE  = new Color(109, 40, 217);      // #6D28D9 Dark Violet
    public static final Color ACCENT_PURPLE_LIGHT = new Color(237, 233, 254);// #EDE9FE Light Lavender
    public static final Color ACCENT_PURPLE_BORDER = new Color(221, 214, 254);

    public static final Color ACCENT_BLUE    = new Color(3, 105, 161);       // #0369A1 Deep Blue
    public static final Color ACCENT_BLUE_LIGHT = new Color(224, 242, 254);  // #E0F2FE Light Sky
    public static final Color ACCENT_BLUE_BORDER = new Color(186, 230, 253);

    public static final Color PURPLE_BRIGHT      = PRIMARY;
    public static final Color PURPLE_GRADIENT_END= PRIMARY_HOVER;
    public static final Color LAVENDER_SURFACE   = BG_PAGE;
    public static final Color LAVENDER_BORDER    = BORDER_LIGHT;
    public static final Color PURPLE             = ACCENT_PURPLE;
    public static final Color PURPLE_LIGHT       = ACCENT_PURPLE_LIGHT;
    public static final Color TEAL               = PRIMARY;
    public static final Color TEAL_LIGHT         = BG_PAGE;

    // =========================================================================
    // TYPOGRAPHY (Segoe UI on Windows, with robust fallbacks)
    // =========================================================================
    private static final String FONT_NAME = "Segoe UI";

    public static final Font FONT_HEADER_TITLE = new Font(FONT_NAME, Font.BOLD, 20);
    public static final Font FONT_PAGE_TITLE = new Font(FONT_NAME, Font.BOLD, 22);
    public static final Font FONT_SECTION_TITLE = new Font(FONT_NAME, Font.BOLD, 16);
    public static final Font FONT_CARD_TITLE = new Font(FONT_NAME, Font.BOLD, 15);
    public static final Font FONT_BODY_BOLD = new Font(FONT_NAME, Font.BOLD, 13);
    public static final Font FONT_BODY = new Font(FONT_NAME, Font.PLAIN, 13);
    public static final Font FONT_SMALL_BOLD = new Font(FONT_NAME, Font.BOLD, 11);
    public static final Font FONT_SMALL = new Font(FONT_NAME, Font.PLAIN, 11);
    public static final Font FONT_KPI_VALUE = new Font(FONT_NAME, Font.BOLD, 26);
    public static final Font FONT_KPI_HERO = new Font(FONT_NAME, Font.BOLD, 32);

    // =========================================================================
    // SYSTEM SETUP & ANTIALIASING
    // =========================================================================
    public static void setupSystemRendering() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        // Set standard tooltip styling
        UIManager.put("ToolTip.background", TEXT_MAIN);
        UIManager.put("ToolTip.foreground", Color.WHITE);
        UIManager.put("ToolTip.font", FONT_SMALL);
        UIManager.put("ToolTip.border", BorderFactory.createEmptyBorder(4, 8, 4, 8));
    }

    // =========================================================================
    // CUSTOM ROUNDED BUTTON
    // =========================================================================
    public enum ButtonStyle {
        PRIMARY, SECONDARY, SUCCESS, DANGER, OUTLINE, GHOST
    }

    public static class ModernButton extends JButton {
        private final ButtonStyle style;
        private final int cornerRadius;
        private boolean isHovered = false;
        private boolean isPressed = false;

        public ModernButton(String text, ButtonStyle style) {
            this(text, style, 10);
        }

        public ModernButton(String text, ButtonStyle style, int cornerRadius) {
            super(text);
            this.style = style;
            this.cornerRadius = Math.min(cornerRadius, 12);
            setFont(FONT_BODY_BOLD);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(9, 18, 9, 18));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg;
            Color fg;
            Color border = null;

            switch (style) {
                case PRIMARY:
                    bg = isPressed ? PRIMARY_DARK : (isHovered ? PRIMARY_HOVER : PRIMARY);
                    fg = Color.WHITE;
                    break;
                case SUCCESS:
                    bg = isPressed ? SUCCESS_DARK : (isHovered ? SUCCESS : SUCCESS_DARK);
                    fg = Color.WHITE;
                    break;
                case DANGER:
                    bg = isPressed ? new Color(185, 28, 28) : (isHovered ? DANGER_DARK : DANGER);
                    fg = Color.WHITE;
                    break;
                case SECONDARY:
                    bg = isPressed ? new Color(234, 227, 217) : (isHovered ? new Color(244, 239, 234) : Color.WHITE);
                    fg = TEXT_BODY;
                    border = BORDER_INPUT;
                    break;
                case OUTLINE:
                    bg = isHovered ? PRIMARY_LIGHT : Color.WHITE;
                    fg = PRIMARY;
                    border = isHovered ? PRIMARY : BORDER_INPUT;
                    break;
                case GHOST:
                default:
                    bg = isHovered ? new Color(244, 239, 234) : new Color(0, 0, 0, 0);
                    fg = isHovered ? PRIMARY : TEXT_BODY;
                    break;
            }

            setForeground(fg);

            // Fill background
            Shape buttonShape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                    cornerRadius, cornerRadius);
            g2.setColor(bg);
            g2.fill(buttonShape);

            // Paint border if applicable
            if (border != null) {
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f,
                        getHeight() - 1f, cornerRadius, cornerRadius));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================================
    // CUSTOM ROUNDED TEXT FIELD & PASSWORD FIELD
    // =========================================================================
    public static class ModernTextField extends JTextField {
        private String placeholder = "";
        private boolean isFocused = false;
        private int cornerRadius = 7;

        public ModernTextField() {
            this("");
        }

        public ModernTextField(String placeholder) {
            this(placeholder, 20);
        }

        public ModernTextField(String placeholder, int columns) {
            super(columns);
            this.placeholder = placeholder;
            setFont(FONT_BODY);
            setForeground(TEXT_MAIN);
            setCaretColor(PRIMARY);
            setBackground(Color.WHITE);
            setOpaque(false);
            setBorder(new EmptyBorder(8, 12, 8, 12));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    isFocused = true;
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    isFocused = false;
                    repaint();
                }
            });
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background
            g2.setColor(isEditable() ? Color.WHITE : new Color(248, 250, 252));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                    cornerRadius, cornerRadius));

            // Border
            if (isFocused) {
                g2.setColor(BORDER_FOCUS);
                g2.setStroke(new BasicStroke(1.8f));
            } else {
                g2.setColor(BORDER_LIGHT);
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f,
                    getHeight() - 1f, cornerRadius, cornerRadius));

            g2.dispose();
            super.paintComponent(g);

            // Placeholder
            if (getText().isEmpty() && !placeholder.isEmpty() && !isFocused) {
                Graphics2D gPlaceholder = (Graphics2D) g.create();
                gPlaceholder.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                gPlaceholder.setColor(TEXT_LIGHT);
                gPlaceholder.setFont(getFont());
                Insets insets = getInsets();
                FontMetrics fm = gPlaceholder.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                gPlaceholder.drawString(placeholder, insets.left, y);
                gPlaceholder.dispose();
            }
        }
    }

    public static class ModernPasswordField extends JPasswordField {
        private String placeholder = "";
        private boolean isFocused = false;
        private int cornerRadius = 7;

        public ModernPasswordField() {
            this("");
        }

        public ModernPasswordField(String placeholder) {
            this(placeholder, 20);
        }

        public ModernPasswordField(String placeholder, int columns) {
            super(columns);
            this.placeholder = placeholder;
            setFont(FONT_BODY);
            setForeground(TEXT_MAIN);
            setCaretColor(PRIMARY);
            setBackground(Color.WHITE);
            setOpaque(false);
            setBorder(new EmptyBorder(8, 12, 8, 12));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    isFocused = true;
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    isFocused = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(isEditable() ? Color.WHITE : new Color(248, 250, 252));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                    cornerRadius, cornerRadius));

            if (isFocused) {
                g2.setColor(BORDER_FOCUS);
                g2.setStroke(new BasicStroke(1.8f));
            } else {
                g2.setColor(BORDER_LIGHT);
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f,
                    getHeight() - 1f, cornerRadius, cornerRadius));

            g2.dispose();
            super.paintComponent(g);

            if (getPassword().length == 0 && !placeholder.isEmpty() && !isFocused) {
                Graphics2D gPlaceholder = (Graphics2D) g.create();
                gPlaceholder.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                gPlaceholder.setColor(TEXT_LIGHT);
                gPlaceholder.setFont(getFont());
                Insets insets = getInsets();
                FontMetrics fm = gPlaceholder.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                gPlaceholder.drawString(placeholder, insets.left, y);
                gPlaceholder.dispose();
            }
        }
    }

    // =========================================================================
    // MODERN CARD (Rounded container panel with border)
    // =========================================================================
    public static class ModernCard extends JPanel {
        private final int cornerRadius;
        private Color cardBackground = BG_CARD;
        private Color borderColor = BORDER_LIGHT;

        public ModernCard() {
            this(14);
        }

        public ModernCard(int cornerRadius) {
            super();
            this.cornerRadius = Math.min(cornerRadius, 14);
            setOpaque(false);
            setBorder(new EmptyBorder(16, 20, 16, 20));
        }

        public void setCardBackground(Color c) {
            this.cardBackground = c;
            repaint();
        }

        public void setBorderColor(Color c) {
            this.borderColor = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Card surface
            g2.setColor(cardBackground);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                    cornerRadius, cornerRadius));

            // Card border
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f,
                        getHeight() - 1f, cornerRadius, cornerRadius));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================================
    // KPI STAT CARD (Metric display with icon, value, label)
    // =========================================================================
    public static JPanel createStatCard(String icon, String label, String value, Color accentColor) {
        JLabel valueLbl = new JLabel(value);
        return createStatCard(icon, label, valueLbl, accentColor);
    }

    public static JPanel createStatCard(String icon, String label, JLabel valueLbl, Color accentColor) {
        ModernCard card = new ModernCard(12);
        card.setLayout(new BorderLayout(12, 6));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Top row: plain metric label
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);

        JLabel titleLbl = new JLabel(label);
        titleLbl.setFont(FONT_SMALL_BOLD);
        titleLbl.setForeground(TEXT_MUTED);

        topRow.add(titleLbl);

        // Value
        valueLbl.setFont(new Font(FONT_NAME, Font.BOLD, 23));
        valueLbl.setForeground(accentColor != null ? accentColor : TEXT_MAIN);
        valueLbl.setBorder(new EmptyBorder(4, 4, 0, 0));

        card.add(topRow, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);

        return card;
    }

    // =========================================================================
    // COMPACT BADGE (Status / Category label)
    // =========================================================================
    public static class PillBadge extends JLabel {
        private Color bgColor;
        private Color borderColor;

        public PillBadge(String text, Color bg, Color textCol) {
            this(text, bg, textCol, null);
        }

        public PillBadge(String text, Color bg, Color textCol, Color borderCol) {
            super(text);
            this.bgColor = bg;
            this.borderColor = borderCol;
            setFont(FONT_SMALL_BOLD);
            setForeground(textCol);
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(new EmptyBorder(3, 10, 3, 10));
            setOpaque(false);
        }

        public void setColors(Color bg, Color textCol, Color borderCol) {
            this.bgColor = bg;
            this.borderColor = borderCol;
            setForeground(textCol);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = getHeight();
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                    arc, arc));

            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f,
                        getHeight() - 1f, arc, arc));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================================
    // CIRCULAR AVATAR (Circle Shape Profile Photo / Avatar)
    // =========================================================================
    public static class CircularAvatar extends JComponent {
        private BufferedImage image;
        private String placeholder = "👤";
        private Color ringColor = PRIMARY;
        private float ringStroke = 3.0f;
        private Color bgColor = PRIMARY_LIGHT;
        private Color textColor = PRIMARY;

        public CircularAvatar(int diameter) {
            this(diameter, PRIMARY);
        }

        public CircularAvatar(int diameter, Color ringColor) {
            this.ringColor = ringColor;
            setPreferredSize(new Dimension(diameter, diameter));
            setMinimumSize(new Dimension(diameter, diameter));
            setMaximumSize(new Dimension(diameter, diameter));
            setOpaque(false);
        }

        public void setImage(BufferedImage img) {
            this.image = img;
            repaint();
        }

        public void setImagePath(String path) {
            if (path == null || path.trim().isEmpty()) {
                this.image = null;
                repaint();
                return;
            }
            File file = new File(path.trim());
            if (file.exists() && file.isFile()) {
                try {
                    this.image = ImageIO.read(file);
                } catch (Exception e) {
                    this.image = null;
                }
            } else {
                this.image = null;
            }
            repaint();
        }

        public void setPlaceholder(String text) {
            this.placeholder = text;
            repaint();
        }

        public void setRingColor(Color c) {
            this.ringColor = c;
            repaint();
        }

        public void setRingStroke(float stroke) {
            this.ringStroke = stroke;
            repaint();
        }

        public void setBgColor(Color c) {
            this.bgColor = c;
            repaint();
        }

        public void setTextColor(Color c) {
            this.textColor = c;
            repaint();
        }

        public boolean hasImage() {
            return this.image != null;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w, h) - (int) Math.ceil(ringStroke * 2) - 2;
            if (size <= 0) size = 10;
            int x = (w - size) / 2;
            int y = (h - size) / 2;

            Ellipse2D.Double circle = new Ellipse2D.Double(x, y, size, size);

            if (image != null) {
                Shape oldClip = g2.getClip();
                g2.clip(circle);

                int imgW = image.getWidth();
                int imgH = image.getHeight();
                double scale = Math.max((double) size / imgW, (double) size / imgH);
                int drawW = (int) (imgW * scale);
                int drawH = (int) (imgH * scale);
                int drawX = x + (size - drawW) / 2;
                int drawY = y + (size - drawH) / 2;

                g2.drawImage(image, drawX, drawY, drawW, drawH, null);
                g2.setClip(oldClip);
            } else {
                // Background circle
                g2.setColor(bgColor);
                g2.fill(circle);

                // Placeholder icon or text in center
                g2.setColor(textColor);
                int fontSize = Math.max(14, size / 3);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, fontSize));
                FontMetrics fm = g2.getFontMetrics();
                int textX = x + (size - fm.stringWidth(placeholder)) / 2;
                int textY = y + (size - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, textX, textY);
            }

            // Outer decorative ring
            if (ringColor != null && ringStroke > 0) {
                g2.setColor(ringColor);
                g2.setStroke(new BasicStroke(ringStroke));
                g2.draw(circle);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================================
    // MODERN TABLE STYLING
    // =========================================================================
    public static void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_MAIN);
        table.setGridColor(BORDER_LIGHT);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setSelectionBackground(new Color(245, 238, 227));
        table.setSelectionForeground(TEXT_MAIN);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_SMALL_BOLD);
        header.setForeground(new Color(102, 92, 80));
        header.setBackground(BG_HEADER);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT));

        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Default cell renderer with zebra striping and padding
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : BG_ALT_ROW);
                    c.setForeground(TEXT_MAIN);
                } else {
                    c.setBackground(new Color(245, 238, 227));
                    c.setForeground(TEXT_MAIN);
                }
                return c;
            }
        });
    }

    // =========================================================================
    // MODERN SCROLL PANE
    // =========================================================================
    public static JScrollPane createModernScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);

        scrollPane.getVerticalScrollBar().setUI(new CleanScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new CleanScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));

        return scrollPane;
    }

    private static class CleanScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(203, 213, 225);
            this.trackColor = new Color(248, 250, 252);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fill(new RoundRectangle2D.Float(thumbBounds.x + 1, thumbBounds.y + 1, thumbBounds.width - 2, thumbBounds.height - 2, 6, 6));
            g2.dispose();
        }
    }

    // =========================================================================
    // MODERN TABBED PANE STYLER
    // =========================================================================
    public static void styleTabbedPane(JTabbedPane tabbedPane) {
        tabbedPane.setFont(FONT_BODY_BOLD);
        tabbedPane.setBackground(PRIMARY_LIGHT);
        tabbedPane.setFocusable(false);
        tabbedPane.setBorder(new EmptyBorder(10, 20, 0, 20));
        tabbedPane.setUI(new ModernTabbedPaneUI());
    }

    public static class ModernTabbedPaneUI extends BasicTabbedPaneUI {
        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabInsets = new Insets(10, 12, 10, 12);
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
        }

        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            // Flat, borderless tab borders
        }

        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isSelected) {
                Shape activeTab = new RoundRectangle2D.Float(x + 3, y + 4, w - 6, h - 8, 12, 12);
                g2.setColor(PRIMARY);
                g2.fill(activeTab);
            } else {
                g2.setColor(BG_HEADER);
                g2.fill(new RoundRectangle2D.Float(x + 3, y + 4, w - 6, h - 8, 12, 12));
                g2.setColor(BORDER_LIGHT);
                g2.draw(new RoundRectangle2D.Float(x + 3.5f, y + 4.5f, w - 7f, h - 9f, 12, 12));
            }
            g2.dispose();
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
        }

        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(font);
            g2.setColor(isSelected ? Color.WHITE : new Color(85, 75, 62));
            g2.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            g2.dispose();
        }
    }

    // =========================================================================
    // CELL RENDERERS FOR BADGES
    // =========================================================================
    public static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        private final PillBadge badge = new PillBadge("", SUCCESS_LIGHT, SUCCESS_DARK, SUCCESS_BORDER);
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));

        public StatusBadgeRenderer() {
            panel.setOpaque(true);
            panel.add(badge);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            String text = value != null ? value.toString() : "-";
            badge.setText(text);

            Color rowBg = isSelected ? PRIMARY_LIGHT : (row % 2 == 0 ? Color.WHITE : BG_ALT_ROW);
            panel.setBackground(rowBg);

            String upper = text.toUpperCase();
            if (upper.contains("ELIGIBLE") && !upper.contains("NOT")) {
                badge.setColors(SUCCESS_LIGHT, SUCCESS_DARK, SUCCESS_BORDER);
            } else if (upper.contains("NOT ELIGIBLE") || upper.contains("FAILED") || upper.contains("FAIL")) {
                badge.setColors(DANGER_LIGHT, DANGER_DARK, DANGER_BORDER);
            } else if (upper.contains("ENROLLED") || upper.contains("ACTIVE")) {
                badge.setColors(PRIMARY_LIGHT, PRIMARY, PRIMARY_TINT);
            } else if (upper.contains("PRACTICAL")) {
                badge.setColors(PURPLE_LIGHT, PURPLE, new Color(221, 214, 254));
            } else if (upper.contains("THEORY")) {
                badge.setColors(ACCENT_BLUE_LIGHT, ACCENT_BLUE, new Color(191, 219, 254));
            } else {
                badge.setColors(BG_HEADER, TEXT_BODY, BORDER_LIGHT);
            }

            return panel;
        }
    }

    public static class GradeBadgeRenderer extends DefaultTableCellRenderer {
        private final PillBadge badge = new PillBadge("", SUCCESS_LIGHT, SUCCESS_DARK, SUCCESS_BORDER);
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));

        public GradeBadgeRenderer() {
            panel.setOpaque(true);
            panel.add(badge);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            String text = value != null ? value.toString() : "-";
            badge.setText(text);

            Color rowBg = isSelected ? PRIMARY_LIGHT : (row % 2 == 0 ? Color.WHITE : BG_ALT_ROW);
            panel.setBackground(rowBg);

            if (text.startsWith("A")) {
                badge.setColors(SUCCESS_LIGHT, SUCCESS_DARK, SUCCESS_BORDER);
            } else if (text.startsWith("B")) {
                badge.setColors(ACCENT_BLUE_LIGHT, ACCENT_BLUE, new Color(191, 219, 254));
            } else if (text.startsWith("C")) {
                badge.setColors(WARNING_LIGHT, WARNING_DARK, WARNING_BORDER);
            } else if (text.startsWith("D") || text.startsWith("E") || text.startsWith("F") || text.equals("NE")) {
                badge.setColors(DANGER_LIGHT, DANGER_DARK, DANGER_BORDER);
            } else {
                badge.setColors(BG_HEADER, TEXT_BODY, BORDER_LIGHT);
            }

            return panel;
        }
    }

    // =========================================================================
    // STANDARDIZED DASHBOARD HEADER & SIDEBAR (Per FMS UI PDF)
    // =========================================================================
    public static class HeaderPanel extends JPanel {
        private final JLabel titleLabel;
        private final JLabel subLabel;
        private final JLabel userLabel;
        private final JLabel roleLabel;

        public HeaderPanel(JFrame frame, String initialTitle, String userName, String role, Runnable onLogout) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
                    new EmptyBorder(12, 24, 12, 24)
            ));

            // Left: Title & Subtitle Stack
            JPanel leftBox = new JPanel();
            leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
            leftBox.setOpaque(false);

            titleLabel = new JLabel(initialTitle);
            titleLabel.setFont(FONT_PAGE_TITLE);
            titleLabel.setForeground(TEXT_MAIN);

            subLabel = new JLabel("Faculty of Technology · University of Ruhuna");
            subLabel.setFont(FONT_SMALL);
            subLabel.setForeground(TEXT_MUTED);

            leftBox.add(titleLabel);
            leftBox.add(Box.createVerticalStrut(2));
            leftBox.add(subLabel);

            // Right: User Pill (Card) + Sign Out Button
            JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
            rightBox.setOpaque(false);

            // User Pill Card
            ModernCard userPill = new ModernCard(8);
            userPill.setCardBackground(BG_PAGE);
            userPill.setBorderColor(BORDER_LIGHT);
            userPill.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
            userPill.setBorder(new EmptyBorder(4, 12, 4, 12));

            userLabel = new JLabel(userName);
            userLabel.setFont(FONT_BODY_BOLD);
            userLabel.setForeground(TEXT_MAIN);

            JLabel dotSep = new JLabel("·");
            dotSep.setFont(FONT_BODY_BOLD);
            dotSep.setForeground(TEXT_MUTED);

            roleLabel = new JLabel(role != null ? role.toUpperCase() : "");
            roleLabel.setFont(FONT_SMALL_BOLD);
            roleLabel.setForeground(PRIMARY);

            userPill.add(userLabel);
            userPill.add(dotSep);
            userPill.add(roleLabel);

            // Sign Out Button
            ModernButton logoutBtn = new ModernButton("Sign out", ButtonStyle.SECONDARY, 8);
            logoutBtn.setFont(FONT_SMALL_BOLD);
            logoutBtn.setBorder(new EmptyBorder(7, 14, 7, 14));
            logoutBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to sign out?",
                        "Confirm Sign Out",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (onLogout != null) {
                        onLogout.run();
                    }
                }
            });

            rightBox.add(userPill);
            rightBox.add(logoutBtn);

            add(leftBox, BorderLayout.WEST);
            add(rightBox, BorderLayout.EAST);
        }

        public void setTitle(String title) {
            titleLabel.setText(title);
        }

        public void setSubtitle(String subtitle) {
            subLabel.setText(subtitle);
        }
    }

    public static JPanel buildHeader(JFrame frame, String portalTitle, String userName, String roleLabel, Runnable onLogout) {
        return new HeaderPanel(frame, portalTitle, userName, roleLabel, onLogout);
    }

    public static class SidebarNavigation extends JPanel {
        private final String[] navItems;
        private int selectedIndex = 0;
        private final java.util.function.Consumer<Integer> onSelectionChanged;
        private final JPanel itemsContainer;

        public SidebarNavigation(String portalSubtitle, String[] items, int initialIndex, java.util.function.Consumer<Integer> onSelectionChanged) {
            this.navItems = items;
            this.selectedIndex = initialIndex;
            this.onSelectionChanged = onSelectionChanged;

            setPreferredSize(new Dimension(225, 0));
            setMinimumSize(new Dimension(225, 0));
            setBackground(MAROON_SIDEBAR);
            setLayout(new BorderLayout());

            // 1. Top University Header in Sidebar
            JPanel topBranding = new JPanel();
            topBranding.setLayout(new BoxLayout(topBranding, BoxLayout.Y_AXIS));
            topBranding.setOpaque(false);
            topBranding.setBorder(new EmptyBorder(22, 20, 20, 20));

            JLabel title1 = new JLabel("Faculty of");
            title1.setFont(new Font(FONT_NAME, Font.BOLD, 17));
            title1.setForeground(Color.WHITE);

            JLabel title2 = new JLabel("Technology");
            title2.setFont(new Font(FONT_NAME, Font.BOLD, 17));
            title2.setForeground(Color.WHITE);

            JLabel subTitle = new JLabel("University of Ruhuna");
            subTitle.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
            subTitle.setForeground(new Color(226, 194, 186));
            subTitle.setBorder(new EmptyBorder(3, 0, 0, 0));

            topBranding.add(title1);
            topBranding.add(title2);
            topBranding.add(subTitle);

            // Subtle divider line
            JPanel divider = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(new Color(125, 30, 48));
                    g.fillRect(20, 0, getWidth() - 40, 1);
                }
            };
            divider.setPreferredSize(new Dimension(225, 1));
            divider.setOpaque(false);

            JPanel northBox = new JPanel(new BorderLayout());
            northBox.setOpaque(false);
            northBox.add(topBranding, BorderLayout.CENTER);
            northBox.add(divider, BorderLayout.SOUTH);

            add(northBox, BorderLayout.NORTH);

            // 2. Middle Navigation Items
            itemsContainer = new JPanel();
            itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
            itemsContainer.setOpaque(false);
            itemsContainer.setBorder(new EmptyBorder(15, 12, 15, 12));

            rebuildItems();

            JScrollPane scrollNav = new JScrollPane(itemsContainer);
            scrollNav.setOpaque(false);
            scrollNav.getViewport().setOpaque(false);
            scrollNav.setBorder(BorderFactory.createEmptyBorder());
            scrollNav.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollNav.getVerticalScrollBar().setUI(new CleanScrollBarUI());
            scrollNav.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));

            add(scrollNav, BorderLayout.CENTER);

            // 3. Bottom Footer
            JPanel bottomFooter = new JPanel(new BorderLayout());
            bottomFooter.setOpaque(false);
            bottomFooter.setBorder(new EmptyBorder(16, 20, 18, 20));

            JLabel footerText = new JLabel("FMS v1.0 • Ruhuna");
            footerText.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
            footerText.setForeground(new Color(209, 165, 158));

            bottomFooter.add(footerText, BorderLayout.WEST);
            add(bottomFooter, BorderLayout.SOUTH);
        }

        public void setSelectedIndex(int index) {
            if (index >= 0 && index < navItems.length && index != selectedIndex) {
                this.selectedIndex = index;
                rebuildItems();
            }
        }

        public int getSelectedIndex() {
            return selectedIndex;
        }

        private void rebuildItems() {
            itemsContainer.removeAll();
            for (int i = 0; i < navItems.length; i++) {
                final int index = i;
                final boolean isActive = (i == selectedIndex);
                String itemName = navItems[i];

                JPanel itemBtn = new JPanel(new BorderLayout(10, 0)) {
                    private boolean isHovered = false;
                    {
                        addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseEntered(MouseEvent e) {
                                isHovered = true;
                                repaint();
                            }
                            @Override
                            public void mouseExited(MouseEvent e) {
                                isHovered = false;
                                repaint();
                            }
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                selectedIndex = index;
                                rebuildItems();
                                if (onSelectionChanged != null) {
                                    onSelectionChanged.accept(index);
                                }
                            }
                        });
                    }

                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        int arc = 10;
                        if (isActive) {
                            g2.setColor(GOLD_ACTIVE_BG);
                            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
                        } else if (isHovered) {
                            g2.setColor(new Color(110, 26, 42));
                            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
                        }
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };

                itemBtn.setOpaque(false);
                itemBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                itemBtn.setBorder(new EmptyBorder(8, 14, 8, 14));
                itemBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                JLabel dot = new JLabel(isActive ? "●" : "•");
                dot.setFont(new Font(FONT_NAME, Font.PLAIN, isActive ? 10 : 14));
                dot.setForeground(isActive ? GOLD_ACTIVE_FG : NAV_INACTIVE_DOT);

                JLabel label = new JLabel(itemName);
                label.setFont(isActive ? FONT_BODY_BOLD : FONT_BODY);
                label.setForeground(isActive ? GOLD_ACTIVE_FG : NAV_INACTIVE_FG);

                itemBtn.add(dot, BorderLayout.WEST);
                itemBtn.add(label, BorderLayout.CENTER);

                itemsContainer.add(itemBtn);
                itemsContainer.add(Box.createVerticalStrut(4));
            }
            itemsContainer.revalidate();
            itemsContainer.repaint();
        }
    }

    // =========================================================================
    // MODERN ROUNDED PROGRESS BAR
    // =========================================================================
    public static class ModernProgressBar extends JComponent {
        private double percentage = 0.0;
        private String customText = null;
        private Color customFillColor = null;
        private int cornerRadius = 8;
        private boolean showText = true;

        public ModernProgressBar() {
            this(0.0);
        }

        public ModernProgressBar(double percentage) {
            this.percentage = Math.max(0.0, Math.min(100.0, percentage));
            setPreferredSize(new Dimension(140, 20));
            setOpaque(false);
        }

        public void setPercentage(double p) {
            this.percentage = Math.max(0.0, Math.min(100.0, p));
            repaint();
        }

        public double getPercentage() {
            return percentage;
        }

        public void setCustomText(String text) {
            this.customText = text;
            repaint();
        }

        public void setCustomFillColor(Color c) {
            this.customFillColor = c;
            repaint();
        }

        public void setShowText(boolean show) {
            this.showText = show;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Background track
            g2.setColor(new Color(241, 245, 249));
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
            g2.setColor(BORDER_LIGHT);
            g2.setStroke(new BasicStroke(1.0f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, cornerRadius, cornerRadius));

            // Fill color
            Color fill;
            if (customFillColor != null) {
                fill = customFillColor;
            } else if (percentage >= 80.0) {
                fill = SUCCESS;
            } else if (percentage >= 75.0) {
                fill = WARNING;
            } else {
                fill = DANGER;
            }

            // Fill bar
            int fillW = (int) Math.round((w * percentage) / 100.0);
            if (fillW > 0) {
                g2.setColor(fill);
                g2.fill(new RoundRectangle2D.Float(0, 0, fillW, h, cornerRadius, cornerRadius));
            }

            // Text
            if (showText) {
                String text = customText != null ? customText : String.format("%.1f%%", percentage);
                g2.setFont(FONT_SMALL_BOLD);
                FontMetrics fm = g2.getFontMetrics();
                int textX = (w - fm.stringWidth(text)) / 2;
                int textY = (h - fm.getHeight()) / 2 + fm.getAscent();

                    g2.setColor(fillW > (textX + fm.stringWidth(text) / 2) ? Color.WHITE : TEXT_MAIN);
                g2.drawString(text, textX, textY);
            }

            g2.dispose();
        }
    }

    public static class ProgressBarTableCellRenderer extends DefaultTableCellRenderer {
        private final ModernProgressBar progressBar = new ModernProgressBar();
        private final JPanel panel = new JPanel(new GridBagLayout());

        public ProgressBarTableCellRenderer() {
            panel.setOpaque(true);
            progressBar.setPreferredSize(new Dimension(130, 18));
            panel.add(progressBar);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Color rowBg = isSelected ? PRIMARY_LIGHT : (row % 2 == 0 ? Color.WHITE : BG_ALT_ROW);
            panel.setBackground(rowBg);

            if (value instanceof Number) {
                double pct = ((Number) value).doubleValue();
                progressBar.setPercentage(pct);
                progressBar.setCustomText(String.format("%.1f%%", pct));
            } else if (value != null) {
                String s = value.toString().replace("%", "").trim();
                try {
                    double pct = Double.parseDouble(s);
                    progressBar.setPercentage(pct);
                    progressBar.setCustomText(String.format("%.1f%%", pct));
                } catch (NumberFormatException e) {
                    progressBar.setPercentage(0);
                    progressBar.setCustomText(value.toString());
                }
            } else {
                progressBar.setPercentage(0);
                progressBar.setCustomText("-");
            }
            return panel;
        }
    }
}
