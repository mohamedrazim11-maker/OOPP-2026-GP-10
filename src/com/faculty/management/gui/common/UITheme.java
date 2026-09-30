package com.faculty.management.gui.common;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
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
 * Unified Modern UI Design System for Faculty Management System.
 * Pure Java Swing / Java 2D - No external dependencies.
 * Provides consistent colors, typography, custom components, tables, and buttons.
 */
public class UITheme {

    // =========================================================================
    // COLOR PALETTE (Modern Slate & Indigo Enterprise Palette)
    // =========================================================================
    public static final Color PRIMARY = new Color(67, 56, 202);         // Indigo 700
    public static final Color PRIMARY_HOVER = new Color(79, 70, 229);   // Indigo 600
    public static final Color PRIMARY_DARK = new Color(49, 46, 129);    // Indigo 900
    public static final Color PRIMARY_LIGHT = new Color(238, 242, 255); // Indigo 50
    public static final Color PRIMARY_TINT = new Color(224, 231, 255);  // Indigo 100

    public static final Color ACCENT_BLUE = new Color(37, 99, 235);    // Blue 600
    public static final Color ACCENT_BLUE_LIGHT = new Color(239, 246, 255);

    public static final Color SUCCESS = new Color(16, 185, 129);       // Emerald 500
    public static final Color SUCCESS_DARK = new Color(5, 150, 105);
    public static final Color SUCCESS_LIGHT = new Color(236, 253, 245);
    public static final Color SUCCESS_BORDER = new Color(167, 243, 208);

    public static final Color WARNING = new Color(245, 158, 11);       // Amber 500
    public static final Color WARNING_DARK = new Color(217, 119, 6);
    public static final Color WARNING_LIGHT = new Color(254, 243, 199);
    public static final Color WARNING_BORDER = new Color(253, 230, 138);

    public static final Color DANGER = new Color(239, 68, 68);         // Red 500
    public static final Color DANGER_DARK = new Color(220, 38, 38);    // Red 600
    public static final Color DANGER_LIGHT = new Color(254, 242, 242);
    public static final Color DANGER_BORDER = new Color(254, 202, 202);

    public static final Color PURPLE = new Color(139, 92, 246);        // Purple 500
    public static final Color PURPLE_LIGHT = new Color(245, 243, 255);

    public static final Color TEAL = new Color(13, 148, 136);          // Teal 600
    public static final Color TEAL_LIGHT = new Color(240, 253, 250);

    public static final Color BG_PAGE = new Color(248, 250, 252);      // Slate 50
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BG_ALT_ROW = new Color(248, 250, 252);
    public static final Color BG_HEADER = new Color(241, 245, 249);    // Slate 100

    public static final Color BORDER_LIGHT = new Color(226, 232, 240); // Slate 200
    public static final Color BORDER_FOCUS = new Color(99, 102, 241);  // Indigo 500

    public static final Color TEXT_MAIN = new Color(15, 23, 42);       // Slate 900
    public static final Color TEXT_BODY = new Color(51, 65, 85);       // Slate 700
    public static final Color TEXT_MUTED = new Color(100, 116, 139);   // Slate 500
    public static final Color TEXT_LIGHT = new Color(148, 163, 184);   // Slate 400

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
            this.cornerRadius = cornerRadius;
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
                    bg = isPressed ? SUCCESS_DARK : (isHovered ? new Color(16, 185, 129) : SUCCESS_DARK);
                    fg = Color.WHITE;
                    break;
                case DANGER:
                    bg = isPressed ? new Color(185, 28, 28) : (isHovered ? DANGER_DARK : DANGER);
                    fg = Color.WHITE;
                    break;
                case SECONDARY:
                    bg = isPressed ? new Color(203, 213, 225) : (isHovered ? new Color(226, 232, 240) : new Color(241, 245, 249));
                    fg = TEXT_BODY;
                    border = BORDER_LIGHT;
                    break;
                case OUTLINE:
                    bg = isHovered ? PRIMARY_LIGHT : Color.WHITE;
                    fg = PRIMARY;
                    border = isHovered ? PRIMARY : BORDER_LIGHT;
                    break;
                case GHOST:
                default:
                    bg = isHovered ? new Color(241, 245, 249) : new Color(0, 0, 0, 0);
                    fg = isHovered ? PRIMARY : TEXT_BODY;
                    break;
            }

            setForeground(fg);

            // Fill background
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

            // Paint border if applicable
            if (border != null) {
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, cornerRadius, cornerRadius));
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
        private int cornerRadius = 10;

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
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

            // Border
            if (isFocused) {
                g2.setColor(BORDER_FOCUS);
                g2.setStroke(new BasicStroke(1.8f));
            } else {
                g2.setColor(BORDER_LIGHT);
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, cornerRadius, cornerRadius));

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
        private int cornerRadius = 10;

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
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

            if (isFocused) {
                g2.setColor(BORDER_FOCUS);
                g2.setStroke(new BasicStroke(1.8f));
            } else {
                g2.setColor(BORDER_LIGHT);
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, cornerRadius, cornerRadius));

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
            this.cornerRadius = cornerRadius;
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
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

            // Card border
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, cornerRadius, cornerRadius));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================================
    // KPI STAT CARD (Metric display with icon, value, label)
    // =========================================================================
    public static JPanel createStatCard(String icon, String label, String value, Color accentColor) {
        ModernCard card = new ModernCard(12);
        card.setLayout(new BorderLayout(12, 6));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Top Row: Icon badge + Label
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topRow.setOpaque(false);

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        JLabel titleLbl = new JLabel(label.toUpperCase());
        titleLbl.setFont(FONT_SMALL_BOLD);
        titleLbl.setForeground(TEXT_MUTED);

        topRow.add(iconLbl);
        topRow.add(titleLbl);

        // Value
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(FONT_KPI_VALUE);
        valueLbl.setForeground(accentColor != null ? accentColor : TEXT_MAIN);
        valueLbl.setBorder(new EmptyBorder(4, 4, 0, 0));

        card.add(topRow, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);

        return card;
    }

    // =========================================================================
    // PILL BADGE (Status / Category chip)
    // =========================================================================
    public static class PillBadge extends JLabel {
        private Color bgColor;
        private Color textColor;
        private Color borderColor;

        public PillBadge(String text, Color bg, Color textCol) {
            this(text, bg, textCol, null);
        }

        public PillBadge(String text, Color bg, Color textCol, Color borderCol) {
            super(text);
            this.bgColor = bg;
            this.textColor = textCol;
            this.borderColor = borderCol;
            setFont(FONT_SMALL_BOLD);
            setForeground(textCol);
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(new EmptyBorder(3, 10, 3, 10));
            setOpaque(false);
        }

        public void setColors(Color bg, Color textCol, Color borderCol) {
            this.bgColor = bg;
            this.textColor = textCol;
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
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));

            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, arc, arc));
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
        private int diameter;
        private Color ringColor = PRIMARY;
        private float ringStroke = 3.0f;
        private Color bgColor = PRIMARY_LIGHT;
        private Color textColor = PRIMARY;

        public CircularAvatar(int diameter) {
            this(diameter, PRIMARY);
        }

        public CircularAvatar(int diameter, Color ringColor) {
            this.diameter = diameter;
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
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(PRIMARY_DARK);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_SMALL_BOLD);
        header.setForeground(TEXT_BODY);
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
                    c.setBackground(PRIMARY_LIGHT);
                    c.setForeground(PRIMARY_DARK);
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
        tabbedPane.setBackground(BG_PAGE);
        tabbedPane.setFocusable(false);
        tabbedPane.setUI(new ModernTabbedPaneUI());
    }

    public static class ModernTabbedPaneUI extends BasicTabbedPaneUI {
        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabInsets = new Insets(10, 18, 10, 18);
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
                g2.setColor(Color.WHITE);
                g2.fillRect(x, y, w, h);
                // Active indicator line at bottom
                g2.setColor(PRIMARY);
                g2.fillRect(x + 4, y + h - 3, w - 8, 3);
            } else {
                g2.setColor(new Color(241, 245, 249));
                g2.fillRect(x, y, w, h);
            }
            g2.dispose();
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(BORDER_LIGHT);
            g2.drawLine(0, 0, tabPane.getWidth(), 0);
            g2.dispose();
        }

        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(font);
            g2.setColor(isSelected ? PRIMARY : TEXT_MUTED);
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
    // STANDARDIZED DASHBOARD HEADER BUILDER
    // =========================================================================
    public static JPanel buildHeader(JFrame frame, String portalTitle, String userName, String roleLabel, Runnable onLogout) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY);
        headerPanel.setBorder(new EmptyBorder(14, 24, 14, 24));

        // Left Branding
        JPanel brandBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandBox.setOpaque(false);

        JLabel logoLabel = new JLabel("🏛️");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel appTitle = new JLabel("Faculty Management System");
        appTitle.setFont(new Font(FONT_NAME, Font.BOLD, 17));
        appTitle.setForeground(Color.WHITE);

        JLabel portalSub = new JLabel(portalTitle + " • Faculty of Technology");
        portalSub.setFont(new Font(FONT_NAME, Font.PLAIN, 12));
        portalSub.setForeground(new Color(199, 210, 254));

        titleBox.add(appTitle);
        titleBox.add(portalSub);

        brandBox.add(logoLabel);
        brandBox.add(titleBox);

        // Right User Pill + Logout Button
        JPanel userBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        userBox.setOpaque(false);

        JPanel userCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        userCard.setBackground(new Color(49, 46, 129, 160));
        userCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(99, 102, 241, 100), 1),
                new EmptyBorder(2, 10, 2, 10)
        ));

        CircularAvatar userAvatar = new CircularAvatar(26, new Color(129, 140, 248));
        userAvatar.setRingStroke(1.5f);
        userAvatar.setBgColor(new Color(67, 56, 202));
        userAvatar.setTextColor(Color.WHITE);
        userAvatar.setPlaceholder("👤");

        JLabel userText = new JLabel(userName);
        userText.setFont(FONT_BODY_BOLD);
        userText.setForeground(Color.WHITE);

        JLabel rolePill = new JLabel(" " + roleLabel + " ");
        rolePill.setFont(FONT_SMALL_BOLD);
        rolePill.setOpaque(true);
        rolePill.setBackground(new Color(99, 102, 241));
        rolePill.setForeground(Color.WHITE);
        rolePill.setBorder(new EmptyBorder(1, 4, 1, 4));

        userCard.add(userAvatar);
        userCard.add(userText);
        userCard.add(rolePill);

        ModernButton logoutBtn = new ModernButton("Sign Out", ButtonStyle.DANGER, 8);
        logoutBtn.setFont(FONT_SMALL_BOLD);
        logoutBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
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

        userBox.add(userCard);
        userBox.add(logoutBtn);

        headerPanel.add(brandBox, BorderLayout.WEST);
        headerPanel.add(userBox, BorderLayout.EAST);

        return headerPanel;
    }
}
