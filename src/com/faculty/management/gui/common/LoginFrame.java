package com.faculty.management.gui.common;

import com.faculty.management.controller.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Modern University Login GUI for Faculty Management System (FMS).
 * Built with pure Java Swing and Java2D - No external libraries.
 * Features:
 * - Two-column responsive layout (Burgundy Branding Panel + Cream Form Panel)
 * - Academic branding with neoclassical building art and golden ribbon accents
 * - Sleek rounded input cards with vector icons and focus glow
 * - Password visibility eye-toggle & checkbox synchronization
 * - Full backward compatibility with LoginController and existing APIs
 */
public class LoginFrame extends JFrame {

    // --- Color Palette (Per FMS UI PDF) ---
    private static final Color MAROON_DARK = new Color(72, 14, 26);    // #480E1A
    private static final Color MAROON_PRIMARY = new Color(92, 18, 33); // #5C1221 Solid University Burgundy
    private static final Color MAROON_LIGHT = new Color(110, 22, 36);  // #6E1624 Primary Action
    private static final Color MAROON_HOVER = new Color(132, 27, 45);  // #841B2D
    private static final Color MAROON_PRESSED = new Color(48, 8, 16);  // #300810

    private static final Color GOLD_LIGHT = new Color(245, 215, 140);
    private static final Color GOLD_PRIMARY = new Color(232, 175, 68); // #E8AF44 Golden Accent
    private static final Color GOLD_DARK = new Color(180, 115, 30);

    private static final Color CREAM_BG = new Color(246, 239, 230);   // #F6EFE6 Warm Soft Ivory
    private static final Color TEXT_DARK = new Color(24, 24, 27);      // #18181B Heading Dark
    private static final Color TEXT_MUTED = new Color(107, 114, 128);  // #6B7280 Muted
    private static final Color BORDER_NORMAL = new Color(213, 206, 196); // #D5CEC4 Subtle Input Border
    private static final Color BORDER_ACTIVE = new Color(110, 22, 36); // #6E1624

    // --- GUI Components ---
    private JComboBox<String> roleComboBox;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JButton eyeToggleButton;
    private JLabel errorLabel;
    private JButton loginButton;
    private JButton resetButton;

    private boolean isPasswordVisible = false;
    private final LoginController loginController;

    public LoginFrame(LoginController controller) {
        this.loginController = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Faculty Management System — Login");
        setSize(960, 620);
        setMinimumSize(new Dimension(880, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Window Icon (Academic Crest)
        setIconImage(createWindowIcon());

        // Main Container with 2 Columns: Left (Branding 45%) and Right (Login Form 55%)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CREAM_BG);

        LeftBrandingPanel leftPanel = new LeftBrandingPanel();
        leftPanel.setPreferredSize(new Dimension(420, 0));
        mainPanel.add(leftPanel, BorderLayout.WEST);

        RightFormPanel rightPanel = new RightFormPanel();
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    // =========================================================================
    // 1. LEFT BRANDING PANEL (Burgundy Gradient, Gold Ribbons, Building Art)
    // =========================================================================
    private class LeftBrandingPanel extends JPanel {

        public LeftBrandingPanel() {
            setOpaque(false);
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // 1. Deep Burgundy Background Gradient
            GradientPaint bgGradient = new GradientPaint(
                    0, 0, MAROON_DARK,
                    w, h, MAROON_PRIMARY
            );
            g2.setPaint(bgGradient);
            g2.fillRect(0, 0, w, h);

            // 2. Neoclassical Building & Driveway Art (Drawn in subtle monochrome burgundy)
            drawUniversityFacade(g2, w, h);

            // 3. Top-Left Elegant Golden Swirl Ribbon
            drawTopGoldenRibbon(g2, w, h);

            // 4. Bottom-Right Elegant Golden Swirl Ribbon
            drawBottomGoldenRibbon(g2, w, h);

            // 5. Central Branding Texts & Badges
            drawBrandingText(g2, w, h);

            g2.dispose();
        }

        private void drawTopGoldenRibbon(Graphics2D g2, int w, int h) {
            // Main Top Ribbon
            Path2D.Double path = new Path2D.Double();
            path.moveTo(0, 0);
            path.curveTo(w * 0.15, h * 0.01, w * 0.45, h * 0.05, 0, h * 0.22);
            path.closePath();

            GradientPaint goldGrad = new GradientPaint(
                    0, 0, GOLD_LIGHT,
                    w * 0.35f, h * 0.15f, GOLD_DARK
            );
            g2.setPaint(goldGrad);
            g2.fill(path);

            // Thinner highlight ribbon alongside
            Path2D.Double path2 = new Path2D.Double();
            path2.moveTo(0, h * 0.04);
            path2.curveTo(w * 0.25, h * 0.04, w * 0.52, h * 0.11, 0, h * 0.25);
            path2.curveTo(w * 0.45, h * 0.08, w * 0.20, h * 0.03, 0, h * 0.02);
            path2.closePath();

            g2.setColor(new Color(250, 230, 175, 120));
            g2.fill(path2);
        }

        private void drawBottomGoldenRibbon(Graphics2D g2, int w, int h) {
            // Bottom-Right sweeping gold ribbon
            Path2D.Double path = new Path2D.Double();
            path.moveTo(w * 0.35, h);
            path.curveTo(w * 0.65, h * 0.96, w * 0.90, h * 0.88, w, h * 0.75);
            path.lineTo(w, h);
            path.closePath();

            GradientPaint goldGrad = new GradientPaint(
                    w * 0.35f, h, GOLD_DARK,
                    w, h * 0.75f, GOLD_LIGHT
            );
            g2.setPaint(goldGrad);
            g2.fill(path);

            // Thinner inner accent wave
            Path2D.Double accent = new Path2D.Double();
            accent.moveTo(w * 0.25, h);
            accent.curveTo(w * 0.55, h * 0.94, w * 0.85, h * 0.84, w, h * 0.70);
            accent.curveTo(w * 0.88, h * 0.86, w * 0.58, h * 0.96, w * 0.32, h);
            accent.closePath();

            g2.setColor(new Color(250, 230, 175, 130));
            g2.fill(accent);
        }

        private void drawUniversityFacade(Graphics2D g2, int w, int h) {
            int baseY = (int) (h * 0.78);
            int buildingW = (int) (w * 0.86);
            int buildingH = (int) (h * 0.26);
            int buildingX = (w - buildingW) / 2;
            int buildingY = baseY - buildingH;

            // Soft building shadow & silhouette
            g2.setColor(new Color(40, 6, 12, 190));
            g2.fillRect(buildingX, buildingY, buildingW, buildingH);

            // Architectural horizontal floor tiers
            g2.setColor(new Color(95, 22, 34, 180));
            for (int i = 1; i <= 3; i++) {
                int tierY = buildingY + (buildingH / 4) * i;
                g2.fillRect(buildingX, tierY, buildingW, 3);
            }

            // Window grids (Soft warm glow in windows)
            Color windowDark = new Color(28, 4, 8);
            Color windowGlow = new Color(225, 180, 120, 25);
            int cols = 12;
            int rows = 3;
            int winW = buildingW / (cols * 2);
            int winH = (buildingH / rows) - 12;

            for (int r = 0; r < rows; r++) {
                int wy = buildingY + 8 + r * (winH + 8);
                for (int c = 0; c < cols; c++) {
                    int wx = buildingX + 10 + c * (winW + 12);
                    g2.setColor(windowDark);
                    g2.fillRect(wx, wy, winW, winH);
                    g2.setColor(windowGlow);
                    g2.fillRect(wx + 1, wy + 1, winW - 2, winH - 2);
                }
            }

            // Central Pediment (Triangular portico with columns)
            int porticoW = (int) (buildingW * 0.28);
            int porticoX = buildingX + (buildingW - porticoW) / 2;
            int pedimentH = 22;

            Path2D.Double pediment = new Path2D.Double();
            pediment.moveTo(porticoX - 4, buildingY);
            pediment.lineTo(porticoX + porticoW / 2, buildingY - pedimentH);
            pediment.lineTo(porticoX + porticoW + 4, buildingY);
            pediment.closePath();

            g2.setColor(new Color(60, 10, 18));
            g2.fill(pediment);
            g2.setColor(new Color(190, 145, 75, 160));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(pediment);

            // Columns under pediment
            g2.setColor(new Color(110, 26, 40));
            int colCount = 4;
            int colGap = porticoW / (colCount + 1);
            for (int c = 1; c <= colCount; c++) {
                int cx = porticoX + c * colGap - 3;
                g2.fillRect(cx, buildingY, 6, buildingH);
            }

            // Foreground Driveway / Pathway leading to the entrance
            Path2D.Double road = new Path2D.Double();
            road.moveTo(w * 0.15, h);
            road.curveTo(w * 0.32, h * 0.88, w * 0.44, h * 0.82, w * 0.48, baseY);
            road.lineTo(w * 0.52, baseY);
            road.curveTo(w * 0.56, h * 0.82, w * 0.68, h * 0.88, w * 0.85, h);
            road.closePath();

            GradientPaint roadGrad = new GradientPaint(
                    w * 0.5f, baseY, new Color(48, 7, 14),
                    w * 0.5f, h, new Color(30, 4, 8)
            );
            g2.setPaint(roadGrad);
            g2.fill(road);

            // Flanking lush foliage / trees on left & right
            drawFoliage(g2, buildingX - 15, baseY + 10, 48, 60);
            drawFoliage(g2, buildingX + buildingW - 35, baseY + 10, 48, 60);
        }

        private void drawFoliage(Graphics2D g2, int x, int y, int fw, int fh) {
            g2.setColor(new Color(25, 4, 7, 210));
            g2.fillOval(x, y - fh, fw, fh);
            g2.fillOval(x - 10, y - (int) (fh * 0.8), (int) (fw * 0.9), (int) (fh * 0.9));
            g2.fillOval(x + 12, y - (int) (fh * 0.85), (int) (fw * 0.85), (int) (fh * 0.9));
        }

        private void drawBrandingText(Graphics2D g2, int w, int h) {
            Font serifFont = new Font("Georgia", Font.BOLD, 24);
            Font smallSerif = new Font("Georgia", Font.BOLD, 17);
            Font subSerif = new Font("Georgia", Font.PLAIN, 14);
            Font italicMotto = new Font("Georgia", Font.ITALIC, 12);

            // 1. "Faculty Management System (FMS)"
            g2.setFont(serifFont);
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();

            String line1 = "Faculty Management";
            String line2 = "System (FMS)";
            int startY = (int) (h * 0.23);

            g2.drawString(line1, (w - fm.stringWidth(line1)) / 2, startY);
            g2.drawString(line2, (w - fm.stringWidth(line2)) / 2, startY + 30);

            // 2. Golden Diamond Divider Line: --- ⬥ ---
            int dividerY = startY + 48;
            int centerX = w / 2;
            int lineLen = 70;

            g2.setColor(GOLD_PRIMARY);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(centerX - lineLen, dividerY, centerX - 12, dividerY);
            g2.drawLine(centerX + 12, dividerY, centerX + lineLen, dividerY);

            // Central Diamond
            Path2D.Double diamond = new Path2D.Double();
            diamond.moveTo(centerX, dividerY - 5);
            diamond.lineTo(centerX + 5, dividerY);
            diamond.lineTo(centerX, dividerY + 5);
            diamond.lineTo(centerX - 5, dividerY);
            diamond.closePath();
            g2.fill(diamond);

            // 3. "University of Ruhuna"
            g2.setFont(smallSerif);
            g2.setColor(GOLD_LIGHT);
            fm = g2.getFontMetrics();
            String uni = "University of Ruhuna";
            g2.drawString(uni, (w - fm.stringWidth(uni)) / 2, dividerY + 32);

            // 4. "Faculty of Technology"
            g2.setFont(subSerif);
            g2.setColor(new Color(245, 235, 225));
            fm = g2.getFontMetrics();
            String fac = "Faculty of Technology";
            g2.drawString(fac, (w - fm.stringWidth(fac)) / 2, dividerY + 54);

            // 5. Bottom Motto: "Learn  •  Innovate  •  Build the Future"
            g2.setFont(italicMotto);
            g2.setColor(new Color(225, 195, 150));
            fm = g2.getFontMetrics();
            String motto = "Learn  •  Innovate  •  Build the Future";
            g2.drawString(motto, (w - fm.stringWidth(motto)) / 2, (int) (h * 0.88));
        }
    }

    // =========================================================================
    // 2. RIGHT LOGIN FORM PANEL (Warm Cream, Botanical Motif, Modern Form Cards)
    // =========================================================================
    private class RightFormPanel extends JPanel {

        public RightFormPanel() {
            setOpaque(false);
            setLayout(new GridBagLayout());
            buildFormLayout();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Background Fill
            g2.setColor(CREAM_BG);
            g2.fillRect(0, 0, w, h);

            // Top-Right Floral/Leaf Motif Watermark
            drawBotanicalMotif(g2, w - 20, 20);

            // Bottom-Right Soft Swirl Curve
            Path2D.Double bottomCurve = new Path2D.Double();
            bottomCurve.moveTo(w * 0.70, h);
            bottomCurve.curveTo(w * 0.85, h * 0.94, w * 0.95, h * 0.88, w, h * 0.78);
            bottomCurve.lineTo(w, h);
            bottomCurve.closePath();
            g2.setColor(new Color(215, 195, 165, 45));
            g2.fill(bottomCurve);

            g2.dispose();
        }

        /**
         * Draws delicate botanical vines and leaves in the top right corner.
         */
        private void drawBotanicalMotif(Graphics2D g2, int originX, int originY) {
            g2.setColor(new Color(195, 165, 125, 60));
            g2.setStroke(new BasicStroke(1.5f));

            // Main Vine Branch
            Path2D.Double vine = new Path2D.Double();
            vine.moveTo(originX, originY + 5);
            vine.curveTo(originX - 40, originY + 25, originX - 60, originY + 70, originX - 85, originY + 120);
            g2.draw(vine);

            // Leaves along the vine
            drawLeaf(g2, originX - 25, originY + 20, -35, 18, 9);
            drawLeaf(g2, originX - 50, originY + 45, 45, 20, 10);
            drawLeaf(g2, originX - 58, originY + 75, -25, 22, 10);
            drawLeaf(g2, originX - 75, originY + 100, 30, 20, 9);
            drawLeaf(g2, originX - 88, originY + 125, -15, 18, 8);
        }

        private void drawLeaf(Graphics2D g2, int x, int y, double angleDeg, int length, int width) {
            AffineTransform old = g2.getTransform();
            g2.translate(x, y);
            g2.rotate(Math.toRadians(angleDeg));

            Path2D.Double leaf = new Path2D.Double();
            leaf.moveTo(0, 0);
            leaf.curveTo(length * 0.4, -width, length * 0.8, -width * 0.5, length, 0);
            leaf.curveTo(length * 0.8, width * 0.5, length * 0.4, width, 0, 0);
            leaf.closePath();

            g2.setColor(new Color(205, 175, 135, 75));
            g2.fill(leaf);
            g2.setColor(new Color(185, 150, 110, 90));
            g2.draw(leaf);

            g2.setTransform(old);
        }

        private void buildFormLayout() {
            // Inner content panel fixed to comfortable form width (~420px)
            JPanel content = new JPanel();
            content.setOpaque(false);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(new EmptyBorder(10, 10, 10, 10));
            content.setPreferredSize(new Dimension(430, 530));
            content.setMaximumSize(new Dimension(430, 530));

            // 1. "Welcome Back" Header
            JLabel welcomeLabel = new JLabel("Welcome Back");
            welcomeLabel.setFont(new Font("Georgia", Font.BOLD, 30));
            welcomeLabel.setForeground(MAROON_PRIMARY);
            welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Subtitle
            JLabel subLabel = new JLabel("Please sign in to access your faculty portal");
            subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            subLabel.setForeground(TEXT_MUTED);
            subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            subLabel.setPreferredSize(new Dimension(410, 20));
            subLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

            // Gold underline bar
            JPanel goldBar = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(GOLD_PRIMARY);
                    g2.fillRoundRect(0, 1, 52, 3, 2, 2);
                    g2.dispose();
                }
            };
            goldBar.setOpaque(false);
            goldBar.setPreferredSize(new Dimension(55, 6));
            goldBar.setMaximumSize(new Dimension(55, 6));
            goldBar.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Error Label
            errorLabel = new JLabel(" ");
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            errorLabel.setForeground(new Color(185, 28, 28));
            errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // 2. Select Role Field
            JLabel roleTitle = createFieldLabel("Select Role");
            String[] roles = {"-- Select Role --", "Admin", "Lecturer", "Technical Officer", "Undergraduate"};
            roleComboBox = new JComboBox<>(roles);
            roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            roleComboBox.setBackground(Color.WHITE);
            roleComboBox.setForeground(TEXT_DARK);
            roleComboBox.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            roleComboBox.setOpaque(false);
            roleComboBox.setFocusable(false);
            roleComboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    JButton btn = new JButton() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(new Color(90, 80, 75));
                            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                            int cx = getWidth() / 2;
                            int cy = getHeight() / 2;
                            // Clean down chevron: ⌵
                            g2.drawLine(cx - 5, cy - 2, cx, cy + 3);
                            g2.drawLine(cx, cy + 3, cx + 5, cy - 2);
                            g2.dispose();
                        }
                    };
                    btn.setBorder(BorderFactory.createEmptyBorder());
                    btn.setContentAreaFilled(false);
                    btn.setFocusPainted(false);
                    btn.setOpaque(false);
                    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    return btn;
                }

                @Override
                public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                    // Keep clean white background
                }
            });
            roleComboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    lbl.setBorder(new EmptyBorder(6, 10, 6, 10));
                    if (isSelected) {
                        lbl.setBackground(MAROON_PRIMARY);
                        lbl.setForeground(Color.WHITE);
                    } else {
                        lbl.setBackground(Color.WHITE);
                        lbl.setForeground(TEXT_DARK);
                    }
                    return lbl;
                }
            });

            InputCard roleCard = new InputCard(new RoleIcon(), roleComboBox, null);

            // 3. Username Field
            JLabel usernameTitle = createFieldLabel("Username");
            usernameField = new PlaceholderTextField("Enter your username");
            InputCard usernameCard = new InputCard(new UserIcon(), usernameField, null);

            // 4. Password Field with Eye Toggle
            JLabel passwordTitle = createFieldLabel("Password");
            passwordField = new PlaceholderPasswordField("Enter your password");

            eyeToggleButton = new JButton();
            eyeToggleButton.setOpaque(false);
            eyeToggleButton.setContentAreaFilled(false);
            eyeToggleButton.setBorderPainted(false);
            eyeToggleButton.setFocusPainted(false);
            eyeToggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            eyeToggleButton.setPreferredSize(new Dimension(28, 28));
            eyeToggleButton.setIcon(new EyeIcon(false));
            eyeToggleButton.addActionListener(e -> togglePasswordVisibility());

            InputCard passwordCard = new InputCard(new LockIcon(), passwordField, eyeToggleButton);

            // 5. "Show Password" Checkbox
            showPasswordCheckBox = new JCheckBox("Show Password");
            showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            showPasswordCheckBox.setForeground(TEXT_DARK);
            showPasswordCheckBox.setOpaque(false);
            showPasswordCheckBox.setFocusPainted(false);
            showPasswordCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            showPasswordCheckBox.addActionListener(e -> {
                setPasswordVisibility(showPasswordCheckBox.isSelected());
            });

            // 6. Action Buttons: "Sign In" (Filled Maroon) & "Reset" (Outlined Maroon)
            JPanel buttonsRow = new JPanel(new GridLayout(1, 2, 12, 0));
            buttonsRow.setOpaque(false);
            buttonsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            buttonsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            loginButton = new ModernButton("Sign In", true);
            loginButton.addActionListener(e -> performLogin());

            resetButton = new ModernButton("Reset", false);
            resetButton.addActionListener(e -> resetFields());

            buttonsRow.add(loginButton);
            buttonsRow.add(resetButton);

            // Enter Key Listener to trigger Login
            KeyAdapter enterListener = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        performLogin();
                    }
                }
            };
            roleComboBox.addKeyListener(enterListener);
            usernameField.addKeyListener(enterListener);
            passwordField.addKeyListener(enterListener);

            // 7. Golden Diamond Divider: ────── ⬥ ──────
            JPanel footerDivider = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int cy = getHeight() / 2;
                    int cx = w / 2;
                    int halfLen = 110;

                    g2.setColor(new Color(225, 212, 195));
                    g2.drawLine(cx - halfLen, cy, cx - 10, cy);
                    g2.drawLine(cx + 10, cy, cx + halfLen, cy);

                    // Diamond
                    g2.setColor(GOLD_PRIMARY);
                    Path2D.Double d = new Path2D.Double();
                    d.moveTo(cx, cy - 4);
                    d.lineTo(cx + 4, cy);
                    d.lineTo(cx, cy + 4);
                    d.lineTo(cx - 4, cy);
                    d.closePath();
                    g2.fill(d);

                    g2.dispose();
                }
            };
            footerDivider.setOpaque(false);
            footerDivider.setPreferredSize(new Dimension(410, 16));
            footerDivider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
            footerDivider.setAlignmentX(Component.LEFT_ALIGNMENT);

            // 8. Security Badge: 🔒 Secure access for authorized users
            JPanel securityBadge = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
            securityBadge.setOpaque(false);
            securityBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
            securityBadge.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

            JLabel lockIconBadge = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(MAROON_PRIMARY);
                    g2.fillRoundRect(2, 6, 10, 8, 2, 2);
                    g2.setStroke(new BasicStroke(1.4f));
                    g2.drawArc(4, 2, 6, 7, 0, 180);
                    g2.dispose();
                }

                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(14, 16);
                }
            };

            JLabel securityText = new JLabel("Secure access for authorized users");
            securityText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            securityText.setForeground(TEXT_MUTED);
            securityText.setPreferredSize(new Dimension(220, 18));

            securityBadge.add(lockIconBadge);
            securityBadge.add(securityText);

            // Assemble into Form Container with harmonious vertical spacing
            content.add(welcomeLabel);
            content.add(Box.createVerticalStrut(4));
            content.add(subLabel);
            content.add(Box.createVerticalStrut(8));
            content.add(goldBar);
            content.add(Box.createVerticalStrut(4));
            content.add(errorLabel);
            content.add(Box.createVerticalStrut(4));

            content.add(roleTitle);
            content.add(Box.createVerticalStrut(4));
            content.add(roleCard);
            content.add(Box.createVerticalStrut(10));

            content.add(usernameTitle);
            content.add(Box.createVerticalStrut(4));
            content.add(usernameCard);
            content.add(Box.createVerticalStrut(10));

            content.add(passwordTitle);
            content.add(Box.createVerticalStrut(4));
            content.add(passwordCard);
            content.add(Box.createVerticalStrut(6));

            content.add(showPasswordCheckBox);
            content.add(Box.createVerticalStrut(14));

            content.add(buttonsRow);
            content.add(Box.createVerticalStrut(18));

            content.add(footerDivider);
            content.add(Box.createVerticalStrut(8));
            content.add(securityBadge);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.CENTER;
            add(content, gbc);
        }

        private JLabel createFieldLabel(String text) {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setForeground(TEXT_DARK);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            return label;
        }
    }

    // =========================================================================
    // 3. REUSABLE CUSTOM UI CONTROLS & CARDS (Simple, Clean, Pure Swing)
    // =========================================================================

    /**
     * Card container with rounded border and focus-highlight for input fields.
     */
    private static class InputCard extends JPanel {
        private boolean isFocused = false;

        public InputCard(JComponent leadingIcon, JComponent inputComp, JComponent trailingComp) {
            setLayout(new BorderLayout(10, 0));
            setOpaque(false);
            setBorder(new EmptyBorder(6, 14, 6, 14));
            setPreferredSize(new Dimension(410, 44));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setAlignmentX(Component.LEFT_ALIGNMENT);

            if (leadingIcon != null) {
                add(leadingIcon, BorderLayout.WEST);
            }
            if (inputComp != null) {
                add(inputComp, BorderLayout.CENTER);
                inputComp.addFocusListener(new FocusAdapter() {
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
            if (trailingComp != null) {
                add(trailingComp, BorderLayout.EAST);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // White rounded background
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(1, 1, w - 2, h - 2, 10, 10);

            // Subtle border (Maroon on focus, soft slate when idle)
            g2.setColor(isFocused ? BORDER_ACTIVE : BORDER_NORMAL);
            g2.setStroke(new BasicStroke(isFocused ? 1.5f : 1.0f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, 10, 10);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Clean TextField with built-in placeholder support.
     */
    private static class PlaceholderTextField extends JTextField {
        private final String placeholder;

        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(TEXT_DARK);
            setCaretColor(TEXT_DARK);
            setBorder(null);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(165, 155, 148));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, getInsets().left, y);
                g2.dispose();
            }
        }
    }

    /**
     * Clean JPasswordField with built-in placeholder support.
     */
    private static class PlaceholderPasswordField extends JPasswordField {
        private final String placeholder;

        public PlaceholderPasswordField(String placeholder) {
            this.placeholder = placeholder;
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(TEXT_DARK);
            setCaretColor(TEXT_DARK);
            setEchoChar('•');
            setBorder(null);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getPassword().length == 0 && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(165, 155, 148));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, getInsets().left, y);
                g2.dispose();
            }
        }
    }

    /**
     * Modern custom button supporting filled primary (Maroon) and outlined secondary (Reset).
     */
    private static class ModernButton extends JButton {
        private final boolean isPrimary;

        public ModernButton(String text, boolean isPrimary) {
            super(text);
            this.isPrimary = isPrimary;
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int radius = 8;

            if (isPrimary) {
                // Filled Primary Button (Sign In - Burgundy #6E1624)
                Color bg = MAROON_LIGHT;
                if (getModel().isPressed()) {
                    bg = MAROON_PRESSED;
                } else if (getModel().isRollover()) {
                    bg = MAROON_HOVER;
                }
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, radius, radius);

                // Draw leading Sign-In Arrow icon: →]
                drawSignInIcon(g2, 28, h / 2, Color.WHITE);

                // Center Text
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(getText())) / 2 + 8;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);

            } else {
                // Outlined Secondary Button (Reset - White with #D5CEC4 border)
                Color bg = Color.WHITE;
                if (getModel().isPressed()) {
                    bg = new Color(234, 227, 217);
                } else if (getModel().isRollover()) {
                    bg = new Color(244, 239, 234);
                }
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, radius, radius);

                // Subtle Border #D5CEC4
                g2.setColor(BORDER_NORMAL);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, w - 2, h - 2, radius, radius);

                // Draw Reset circular arrow icon: ↻
                drawResetIcon(g2, 30, h / 2, TEXT_DARK);

                // Center Text
                g2.setColor(TEXT_DARK);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(getText())) / 2 + 8;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
            }

            g2.dispose();
        }

        private void drawSignInIcon(Graphics2D g2, int cx, int cy, Color color) {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Arrow line & head pointing into door
            g2.drawLine(cx - 8, cy, cx + 2, cy);
            g2.drawLine(cx - 1, cy - 4, cx + 3, cy);
            g2.drawLine(cx - 1, cy + 4, cx + 3, cy);
            // Right door bracket
            g2.drawLine(cx + 6, cy - 7, cx + 6, cy + 7);
        }

        private void drawResetIcon(Graphics2D g2, int cx, int cy, Color color) {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // 270 degree circular arc
            g2.drawArc(cx - 7, cy - 7, 14, 14, 45, 270);
            // Arrowhead at the end
            g2.drawLine(cx + 1, cy - 8, cx + 6, cy - 7);
            g2.drawLine(cx + 4, cy - 3, cx + 6, cy - 7);
        }
    }

    // =========================================================================
    // 4. VECTOR ICONS (Sharp, High-DPI, No External Images Needed)
    // =========================================================================

    /**
     * Vector icon for "Select Role" field.
     */
    private static class RoleIcon extends JComponent {
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(20, 20);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(110, 95, 88));
            g2.setStroke(new BasicStroke(1.5f));

            // Head
            g2.drawOval(6, 2, 8, 8);
            // Shoulders
            g2.drawArc(2, 11, 16, 9, 0, 180);
            g2.dispose();
        }
    }

    /**
     * Vector icon for "Username" field.
     */
    private static class UserIcon extends JComponent {
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(20, 20);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(110, 95, 88));
            g2.setStroke(new BasicStroke(1.5f));

            // Head
            g2.drawOval(6, 2, 8, 8);
            // Torso curve
            g2.drawArc(3, 11, 14, 8, 0, 180);
            g2.dispose();
        }
    }

    /**
     * Vector icon for "Password" field.
     */
    private static class LockIcon extends JComponent {
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(20, 20);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(110, 95, 88));
            g2.setStroke(new BasicStroke(1.5f));

            // Shackle
            g2.drawArc(5, 2, 10, 10, 0, 180);
            // Padlock Body
            g2.drawRoundRect(3, 7, 14, 10, 3, 3);
            // Keyhole
            g2.fillOval(9, 10, 2, 2);
            g2.drawLine(10, 11, 10, 14);
            g2.dispose();
        }
    }

    /**
     * Vector icon for Eye Toggle Button.
     */
    private static class EyeIcon implements Icon {
        private final boolean isVisible;

        public EyeIcon(boolean isVisible) {
            this.isVisible = isVisible;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(125, 110, 102));
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Almond eye outline
            Path2D.Double eye = new Path2D.Double();
            eye.moveTo(x + 2, y + 9);
            eye.curveTo(x + 6, y + 3, x + 12, y + 3, x + 16, y + 9);
            eye.curveTo(x + 12, y + 15, x + 6, y + 15, x + 2, y + 9);
            eye.closePath();
            g2.draw(eye);

            // Pupil
            g2.fillOval(x + 7, y + 7, 4, 4);

            // Diagonal strike-through line if password is hidden
            if (!isVisible) {
                g2.drawLine(x + 3, y + 3, x + 15, y + 15);
            }

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 18;
        }

        @Override
        public int getIconHeight() {
            return 18;
        }
    }

    /**
     * Generates an academic crest image for the window icon.
     */
    private Image createWindowIcon() {
        int s = 32;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(s, s, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Maroon rounded background
        g2.setColor(MAROON_PRIMARY);
        g2.fillRoundRect(0, 0, s, s, 8, 8);

        // Golden crest / pillars
        g2.setColor(GOLD_PRIMARY);
        g2.setStroke(new BasicStroke(1.5f));
        // Pediment
        g2.drawLine(6, 12, 16, 6);
        g2.drawLine(16, 6, 26, 12);
        g2.drawLine(6, 12, 26, 12);
        // Pillars
        g2.drawLine(9, 13, 9, 23);
        g2.drawLine(16, 13, 16, 23);
        g2.drawLine(23, 13, 23, 23);
        // Base
        g2.drawLine(6, 24, 26, 24);

        g2.dispose();
        return img;
    }

    // =========================================================================
    // 5. EVENT HANDLERS & BACKWARD COMPATIBLE API
    // =========================================================================

    private void togglePasswordVisibility() {
        setPasswordVisibility(!isPasswordVisible);
    }

    private void setPasswordVisibility(boolean visible) {
        this.isPasswordVisible = visible;
        if (visible) {
            passwordField.setEchoChar((char) 0);
        } else {
            passwordField.setEchoChar('•');
        }
        showPasswordCheckBox.setSelected(visible);
        eyeToggleButton.setIcon(new EyeIcon(visible));
    }

    private void performLogin() {
        clearErrorMessage();
        String selectedRole = (String) roleComboBox.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        loginController.handleLogin(this, username, password, selectedRole);
    }

    public void showErrorMessage(String message) {
        errorLabel.setText("<html><body style='width: 320px; color: #B91C1C;'>⚠️ " + message + "</body></html>");
    }

    public void clearErrorMessage() {
        errorLabel.setText(" ");
    }

    public void resetFields() {
        roleComboBox.setSelectedIndex(0);
        usernameField.setText("");
        passwordField.setText("");
        setPasswordVisibility(false);
        clearErrorMessage();
        roleComboBox.requestFocus();
    }

    // Encapsulation: Getters and Setters
    public String getSelectedRole() {
        return (String) roleComboBox.getSelectedItem();
    }

    public void setSelectedRole(String role) {
        roleComboBox.setSelectedItem(role);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public void setUsername(String username) {
        usernameField.setText(username);
    }

    public void setPassword(String password) {
        passwordField.setText(password);
    }
}
