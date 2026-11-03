package com.faculty.management.gui.common;

import com.faculty.management.controller.LoginController;

<<<<<<< HEAD
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

/**
 * Modern University Login Frame for Faculty Management System (FMS).
 * Faithfully implements the University of Ruhuna Faculty of Technology
 * sunset lake presentation layout:
 * - Left 50%: University lake photo (uni_image.jpeg) with smooth horizontal fade blend into ivory canvas
 * - Bottom-left: Golden & burgundy curve swoosh with motto "Learn • Innovate • Build the Future"
 * - Top-right: Elegant burgundy and gold swoosh ribbons
 * - Bottom-right: Soft botanical leaf watermark motif
 * - Center-right: Two-line title "Faculty Management System", diamond divider,
 *   "University of Ruhuna / Faculty of Technology", "Welcome Back",
 *   rounded input fields with outline vector icons, custom burgundy arrow sign-in button,
 *   and white reset button with reload icon.
 */
public class LoginFrame extends JFrame {

    // --- Color Palette matching UI Design ---
    private static final Color MAROON_PRIMARY = new Color(90, 16, 32);   // #5A1020 Deep University Burgundy
    private static final Color MAROON_HOVER   = new Color(112, 22, 40);  // #701628
    private static final Color MAROON_PRESSED = new Color(68, 10, 22);   // #440A16
    private static final Color GOLD_ACCENT    = new Color(218, 165, 62);  // #DAA53E Rich Gold
    private static final Color GOLD_LIGHT     = new Color(245, 215, 140); // Soft Champagne Gold
    private static final Color GOLD_DARK      = new Color(180, 125, 35);
    private static final Color CREAM_BG       = new Color(250, 245, 237); // #FAF5ED Warm Soft Ivory
    private static final Color BORDER_IDLE    = new Color(218, 206, 194); // #DACEBE
    private static final Color BORDER_FOCUS   = new Color(110, 22, 36);   // Burgundy focus
    private static final Color TEXT_DARK      = new Color(32, 18, 22);
    private static final Color PLACEHOLDER_COLOR = new Color(165, 155, 145);

    // --- GUI Components ---
=======
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Login GUI for Faculty Management System.
 * Pure Java Swing - No third-party frameworks.
 */
public class LoginFrame extends JFrame {

>>>>>>> Aysha
    private JComboBox<String> roleComboBox;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
<<<<<<< HEAD
    private JButton eyeToggleButton;
    private JButton loginButton;
    private JButton resetButton;
    private JLabel errorLabel;

    private boolean isPasswordVisible = false;
    private BufferedImage universityImage;
    private final LoginController loginController;

    public LoginFrame() {
        this(new LoginController());
    }

    public LoginFrame(LoginController controller) {
        this.loginController = (controller != null) ? controller : new LoginController();
        this.universityImage = loadUniversityImage();
        initComponents();
    }

    /**
     * Loads the university lake photo (uni_image.jpeg) from working directory, resources, or classpath.
     */
    private BufferedImage loadUniversityImage() {
        // 1. Working directory
        File file1 = new File("uni_image.jpeg");
        if (file1.exists()) {
            try { return ImageIO.read(file1); } catch (Exception ignored) {}
        }
        // 2. Resources directory
        File file2 = new File("resources/images/uni_image.jpeg");
        if (file2.exists()) {
            try { return ImageIO.read(file2); } catch (Exception ignored) {}
        }
        File file3 = new File("resources/uni_image.jpeg");
        if (file3.exists()) {
            try { return ImageIO.read(file3); } catch (Exception ignored) {}
        }
        // 3. Classloader classpath
        try {
            URL url = getClass().getResource("/images/uni_image.jpeg");
            if (url != null) return ImageIO.read(url);
            url = getClass().getResource("/uni_image.jpeg");
            if (url != null) return ImageIO.read(url);
        } catch (Exception ignored) {}
        return null;
    }

    private void initComponents() {
        setTitle("Faculty Management System — University of Ruhuna");
        setSize(1060, 640);
        setMinimumSize(new Dimension(960, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(createWindowIcon());

        // Master Background Canvas Panel
        BackgroundCanvasPanel canvas = new BackgroundCanvasPanel();
        canvas.setLayout(new GridBagLayout());
        setContentPane(canvas);

        // Two-column layout container: Left filler (44%) + Right form card (56%)
        JPanel rootContainer = new JPanel(new GridBagLayout());
        rootContainer.setOpaque(false);

        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.weightx = 0.44;
        leftGbc.weighty = 1.0;
        leftGbc.fill = GridBagConstraints.BOTH;
        JPanel leftSpacer = new JPanel();
        leftSpacer.setOpaque(false);
        rootContainer.add(leftSpacer, leftGbc);

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 1;
        rightGbc.gridy = 0;
        rightGbc.weightx = 0.56;
        rightGbc.weighty = 1.0;
        rightGbc.fill = GridBagConstraints.BOTH;
        rightGbc.insets = new Insets(10, 10, 10, 30);

        JPanel formWrapper = buildFormWrapper();
        rootContainer.add(formWrapper, rightGbc);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        canvas.add(rootContainer, gbc);
    }

    /**
     * Builds the right-side form card containing titles, inputs, buttons, and security footer.
     */
    private JPanel buildFormWrapper() {
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(10, 20, 10, 25));

        // 1. Main Header Title: "Faculty Management System"
        JLabel titleLine1 = new JLabel("Faculty Management");
        titleLine1.setFont(new Font("Georgia", Font.BOLD, 28));
        titleLine1.setForeground(MAROON_PRIMARY);
        titleLine1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLine2 = new JLabel("System");
        titleLine2.setFont(new Font("Georgia", Font.BOLD, 28));
        titleLine2.setForeground(MAROON_PRIMARY);
        titleLine2.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 2. Decorative Golden Divider Line with Diamond
        DiamondDividerPanel divider = new DiamondDividerPanel();
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. Subtitles
        JLabel uniLabel = new JLabel("University of Ruhuna");
        uniLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        uniLabel.setForeground(MAROON_PRIMARY);
        uniLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel facLabel = new JLabel("Faculty of Technology");
        facLabel.setFont(new Font("Georgia", Font.ITALIC, 14));
        facLabel.setForeground(new Color(110, 26, 42));
        facLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 4. "Welcome Back" Heading
        JLabel welcomeBack = new JLabel("Welcome Back");
        welcomeBack.setFont(new Font("Georgia", Font.BOLD, 26));
        welcomeBack.setForeground(MAROON_PRIMARY);
        welcomeBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeBack.setBorder(new EmptyBorder(12, 0, 8, 0));

        // Error message banner
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(185, 28, 28));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form Fields (fixed comfortable width of 370px)
        int formWidth = 370;

        // Field 1: Select Role
        JPanel roleCard = createRoleCard(formWidth);
        roleCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Field 2: Username
        JPanel userCard = createUsernameCard(formWidth);
        userCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Field 3: Password
        JPanel passCard = createPasswordCard(formWidth);
        passCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Show Password Checkbox
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkPanel.setOpaque(false);
        checkPanel.setMaximumSize(new Dimension(formWidth, 22));
        checkPanel.setPreferredSize(new Dimension(formWidth, 22));
        checkPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheckBox.setForeground(TEXT_DARK);
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.setFocusPainted(false);
        showPasswordCheckBox.addActionListener(e -> setPasswordVisibility(showPasswordCheckBox.isSelected()));
        checkPanel.add(showPasswordCheckBox);

        // Buttons Row: Login (Arrow) + Reset
        JPanel buttonRow = createButtonRow(formWidth);
        buttonRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Footer: Golden diamond + Secure Access
        JPanel footerPanel = createFooterPanel(formWidth);
        footerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Assembly
        formPanel.add(Box.createVerticalGlue());
        formPanel.add(titleLine1);
        formPanel.add(titleLine2);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(divider);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(uniLabel);
        formPanel.add(facLabel);
        formPanel.add(welcomeBack);
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(2));
        formPanel.add(roleCard);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(userCard);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(passCard);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(checkPanel);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(buttonRow);
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(footerPanel);
        formPanel.add(Box.createVerticalGlue());

        return formPanel;
    }

    // =========================================================================
    // FIELD CARDS (Rounded white input containers with outline vector icons)
    // =========================================================================

    private JPanel createRoleCard(int width) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(width, 68));
        container.setPreferredSize(new Dimension(width, 68));

        JLabel label = new JLabel("Select Role");
        label.setFont(new Font("Georgia", Font.BOLD, 13));
        label.setForeground(MAROON_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setPreferredSize(new Dimension(width, 20));
        label.setMaximumSize(new Dimension(width, 20));
        label.setBorder(new EmptyBorder(0, 2, 4, 0));
        container.add(label);

        RoundedBoxPanel box = new RoundedBoxPanel(width, 42);
        box.setLayout(new BorderLayout(8, 0));
        box.setBorder(new EmptyBorder(2, 10, 2, 8));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(new UserOutlineIcon(MAROON_PRIMARY));
        box.add(iconLabel, BorderLayout.WEST);

        String[] roles = {"Admin", "Lecturer", "Technical Officer", "Undergraduate"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleComboBox.setForeground(TEXT_DARK);
        roleComboBox.setOpaque(false);
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        roleComboBox.setFocusable(false);

        // Custom UI for clean Chevron arrow
        roleComboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(TEXT_DARK);
                        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
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
                // Keep clean transparent/white background
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

        box.add(roleComboBox, BorderLayout.CENTER);
        container.add(box);
        return container;
    }

    private JPanel createUsernameCard(int width) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(width, 68));
        container.setPreferredSize(new Dimension(width, 68));

        JLabel label = new JLabel("Username");
        label.setFont(new Font("Georgia", Font.BOLD, 13));
        label.setForeground(MAROON_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setPreferredSize(new Dimension(width, 20));
        label.setMaximumSize(new Dimension(width, 20));
        label.setBorder(new EmptyBorder(0, 2, 4, 0));
        container.add(label);

        RoundedBoxPanel box = new RoundedBoxPanel(width, 42);
        box.setLayout(new BorderLayout(8, 0));
        box.setBorder(new EmptyBorder(2, 10, 2, 10));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(new UserOutlineIcon(MAROON_PRIMARY));
        box.add(iconLabel, BorderLayout.WEST);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameField.setForeground(TEXT_DARK);
        usernameField.setCaretColor(MAROON_PRIMARY);
        usernameField.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
        usernameField.setOpaque(false);
        installPlaceholder(usernameField, "adm");

        usernameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { box.setFocused(true); }
            @Override
            public void focusLost(FocusEvent e) { box.setFocused(false); }
        });

        usernameField.addActionListener(e -> performLogin());

        box.add(usernameField, BorderLayout.CENTER);
        container.add(box);
        return container;
    }

    private JPanel createPasswordCard(int width) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(width, 68));
        container.setPreferredSize(new Dimension(width, 68));

        JLabel label = new JLabel("Password");
        label.setFont(new Font("Georgia", Font.BOLD, 13));
        label.setForeground(MAROON_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setPreferredSize(new Dimension(width, 20));
        label.setMaximumSize(new Dimension(width, 20));
        label.setBorder(new EmptyBorder(0, 2, 4, 0));
        container.add(label);

        RoundedBoxPanel box = new RoundedBoxPanel(width, 42);
        box.setLayout(new BorderLayout(8, 0));
        box.setBorder(new EmptyBorder(2, 10, 2, 6));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(new LockOutlineIcon(MAROON_PRIMARY));
        box.add(iconLabel, BorderLayout.WEST);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setForeground(TEXT_DARK);
        passwordField.setCaretColor(MAROON_PRIMARY);
        passwordField.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
        passwordField.setOpaque(false);
        passwordField.setEchoChar('•');
        installPasswordPlaceholder(passwordField, "Enter your password");

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { box.setFocused(true); }
            @Override
            public void focusLost(FocusEvent e) { box.setFocused(false); }
        });

        passwordField.addActionListener(e -> performLogin());
        box.add(passwordField, BorderLayout.CENTER);

        eyeToggleButton = new JButton(new EyeToggleIcon(false));
        eyeToggleButton.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        eyeToggleButton.setContentAreaFilled(false);
        eyeToggleButton.setFocusPainted(false);
        eyeToggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eyeToggleButton.setToolTipText("Toggle password visibility");
        eyeToggleButton.addActionListener(e -> togglePasswordVisibility());
        box.add(eyeToggleButton, BorderLayout.EAST);

        container.add(box);
        return container;
    }

    private JPanel createButtonRow(int width) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(width, 42));
        row.setPreferredSize(new Dimension(width, 42));

        // 1. Login Button with Sign-In Arrow: [ →| ]
        loginButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = MAROON_PRIMARY;
                if (getModel().isPressed()) {
                    bg = MAROON_PRESSED;
                } else if (getModel().isRollover()) {
                    bg = MAROON_HOVER;
                }
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Draw crisp Sign-In Arrow Glyphs (→|) in white/gold
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Horizontal line
                g2.drawLine(cx - 10, cy, cx + 5, cy);
                // Arrow head
                g2.drawLine(cx + 1, cy - 5, cx + 6, cy);
                g2.drawLine(cx + 1, cy + 5, cx + 6, cy);
                // Vertical stop bar
                g2.drawLine(cx + 10, cy - 7, cx + 10, cy + 7);

                g2.dispose();
            }
        };
        loginButton.setPreferredSize(new Dimension((int) (width * 0.60), 42));
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setContentAreaFilled(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setToolTipText("Sign In to Portal");
        loginButton.addActionListener(e -> performLogin());

        // 2. Reset Button with circular reload icon: [ ↻  Reset ]
        resetButton = new JButton("Reset") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                Color bg = getModel().isRollover() ? new Color(245, 238, 230) : Color.WHITE;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Border
                g2.setColor(MAROON_PRIMARY);
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 9, 9);

                // Reload icon (circular arrow ↻)
                int iconX = 22;
                int iconY = getHeight() / 2;
                g2.setColor(MAROON_PRIMARY);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // 270 degree arc
                g2.drawArc(iconX - 7, iconY - 7, 14, 14, 45, 280);
                // Arrow tip at top
                g2.drawLine(iconX + 3, iconY - 7, iconX + 8, iconY - 7);
                g2.drawLine(iconX + 8, iconY - 7, iconX + 8, iconY - 2);

                // Label Text
                g2.setFont(new Font("Georgia", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                int textX = iconX + 16;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), textX, textY);

                g2.dispose();
            }
        };
        resetButton.setPreferredSize(new Dimension((int) (width * 0.38), 42));
        resetButton.setBorder(BorderFactory.createEmptyBorder());
        resetButton.setContentAreaFilled(false);
        resetButton.setFocusPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.setToolTipText("Clear all fields");
        resetButton.addActionListener(e -> resetFields());

        row.add(loginButton, BorderLayout.WEST);
        row.add(resetButton, BorderLayout.EAST);
        return row;
    }

    private JPanel createFooterPanel(int width) {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);
        footer.setMaximumSize(new Dimension(width, 36));
        footer.setPreferredSize(new Dimension(width, 36));

        // Tiny golden diamond
        JLabel diamondLabel = new JLabel("◆");
        diamondLabel.setFont(new Font("Segoe UI", Font.PLAIN, 8));
        diamondLabel.setForeground(GOLD_ACCENT);
        diamondLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Security row with vector lock icon
        JPanel lockRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        lockRow.setOpaque(false);
        JLabel lockIcon = new JLabel(new LockOutlineIcon(new Color(110, 100, 95)));
        JLabel secureLabel = new JLabel("Secure access for authorized users");
        secureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        secureLabel.setForeground(new Color(110, 100, 95));

        lockRow.add(lockIcon);
        lockRow.add(secureLabel);

        footer.add(diamondLabel);
        footer.add(Box.createVerticalStrut(2));
        footer.add(lockRow);
        return footer;
    }

    // =========================================================================
    // BACKGROUND CANVAS PANEL (Paints Lake Photo, Fade Gradient & Flourishes)
    // =========================================================================
    private class BackgroundCanvasPanel extends JPanel {

        public BackgroundCanvasPanel() {
            setOpaque(true);
            setBackground(CREAM_BG);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // 1. Base Ivory Background
            g2.setColor(CREAM_BG);
            g2.fillRect(0, 0, w, h);

            // 2. Draw Lake Photo (uni_image.jpeg) on Left ~48%
            int photoWidth = (int) (w * 0.48);
            if (universityImage != null) {
                int imgW = universityImage.getWidth();
                int imgH = universityImage.getHeight();
                double scale = Math.max((double) photoWidth / imgW, (double) h / imgH);
                int scaledW = (int) (imgW * scale);
                int scaledH = (int) (imgH * scale);
                int drawX = 0;
                int drawY = (h - scaledH) / 2;

                Shape oldClip = g2.getClip();
                g2.clipRect(0, 0, photoWidth, h);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(universityImage, drawX, drawY, scaledW, scaledH, null);
                g2.setClip(oldClip);

                // 3. Smooth Fade Transition from Lake Photo to Ivory Canvas
                int blendStart = (int) (w * 0.32);
                int blendLen = photoWidth - blendStart + 6;
                LinearGradientPaint blendGrad = new LinearGradientPaint(
                        blendStart, 0, blendStart + blendLen, 0,
                        new float[]{0.0f, 0.35f, 0.70f, 1.0f},
                        new Color[]{
                                new Color(CREAM_BG.getRed(), CREAM_BG.getGreen(), CREAM_BG.getBlue(), 0),
                                new Color(CREAM_BG.getRed(), CREAM_BG.getGreen(), CREAM_BG.getBlue(), 100),
                                new Color(CREAM_BG.getRed(), CREAM_BG.getGreen(), CREAM_BG.getBlue(), 220),
                                CREAM_BG
                        }
                );
                g2.setPaint(blendGrad);
                g2.fillRect(blendStart, 0, blendLen, h);
            }

            // 4. Bottom-Left Golden & Burgundy Ribbon Waves
            drawBottomLeftRibbons(g2, w, h);

            // 5. Motto under Photo: "─── Learn • Innovate • Build the Future ───"
            drawMottoLine(g2, photoWidth, h);

            // 6. Top-Right Burgundy & Gold Swirl Curve
            drawTopRightSwirl(g2, w, h);

            // 7. Bottom-Right Delicate Botanical Leaf Watermark
            drawBotanicalWatermark(g2, w - 15, h - 15);

            g2.dispose();
        }

        private void drawBottomLeftRibbons(Graphics2D g2, int w, int h) {
            // Dark burgundy corner wave
            Path2D.Double maroonWave = new Path2D.Double();
            maroonWave.moveTo(0, h * 0.88);
            maroonWave.curveTo(w * 0.08, h * 0.89, w * 0.16, h * 0.95, w * 0.22, h);
            maroonWave.lineTo(0, h);
            maroonWave.closePath();
            g2.setColor(new Color(90, 16, 32));
            g2.fill(maroonWave);

            // Elegant sweeping gold ribbon
            Path2D.Double goldRibbon = new Path2D.Double();
            goldRibbon.moveTo(0, h * 0.83);
            goldRibbon.curveTo(w * 0.08, h * 0.84, w * 0.17, h * 0.93, w * 0.25, h);
            goldRibbon.lineTo(w * 0.21, h);
            goldRibbon.curveTo(w * 0.14, h * 0.93, w * 0.06, h * 0.86, 0, h * 0.86);
            goldRibbon.closePath();

            GradientPaint goldGrad = new GradientPaint(
                    0, (float) (h * 0.83), GOLD_LIGHT,
                    (float) (w * 0.25), (float) h, GOLD_DARK
            );
            g2.setPaint(goldGrad);
            g2.fill(goldRibbon);
        }

        private void drawMottoLine(Graphics2D g2, int photoWidth, int h) {
            int lineY = (int) (h * 0.93);
            int centerX = (int) (photoWidth * 0.45);

            String motto = "Learn  •  Innovate  •  Build the Future";
            Font font = new Font("Georgia", Font.ITALIC, 13);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();
            int strW = fm.stringWidth(motto);

            int textX = centerX - strW / 2;
            int textY = lineY + fm.getAscent() / 2 - 2;

            // Flanking golden lines
            g2.setColor(new Color(230, 195, 120, 190));
            g2.setStroke(new BasicStroke(1.0f));
            int gap = 14;
            int lineLen = 38;
            g2.drawLine(textX - gap - lineLen, lineY, textX - gap, lineY);
            g2.drawLine(textX + strW + gap, lineY, textX + strW + gap + lineLen, lineY);

            // Shadow & Motto text in crisp white with subtle golden tint
            g2.setColor(new Color(10, 5, 5, 180));
            g2.drawString(motto, textX + 1, textY + 1);
            g2.setColor(Color.WHITE);
            g2.drawString(motto, textX, textY);
        }

        private void drawTopRightSwirl(Graphics2D g2, int w, int h) {
            // Main Burgundy Curve from top to right
            Path2D.Double maroonWave = new Path2D.Double();
            maroonWave.moveTo(w * 0.84, 0);
            maroonWave.curveTo(w * 0.87, h * 0.08, w * 0.93, h * 0.15, w, h * 0.18);
            maroonWave.lineTo(w, 0);
            maroonWave.closePath();

            g2.setColor(new Color(90, 16, 32));
            g2.fill(maroonWave);

            // Golden Accent Ribbon
            Path2D.Double goldWave = new Path2D.Double();
            goldWave.moveTo(w * 0.82, 0);
            goldWave.curveTo(w * 0.85, h * 0.10, w * 0.92, h * 0.18, w, h * 0.22);
            goldWave.lineTo(w, h * 0.19);
            goldWave.curveTo(w * 0.93, h * 0.16, w * 0.87, h * 0.08, w * 0.84, 0);
            goldWave.closePath();

            GradientPaint goldGrad = new GradientPaint(
                    (float) (w * 0.82), 0, GOLD_LIGHT,
                    (float) w, (float) (h * 0.22), GOLD_DARK
            );
            g2.setPaint(goldGrad);
            g2.fill(goldWave);
        }

        private void drawBotanicalWatermark(Graphics2D g2, int originX, int originY) {
            g2.setColor(new Color(215, 195, 165, 75));
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Main Vine Branch arching up and to the left
            Path2D.Double stem = new Path2D.Double();
            stem.moveTo(originX, originY);
            stem.curveTo(originX - 35, originY - 45, originX - 65, originY - 110, originX - 70, originY - 180);
            g2.draw(stem);

            // Alternating Leaves
            drawWatermarkLeaf(g2, originX - 18, originY - 30, -35, 24, 11);
            drawWatermarkLeaf(g2, originX - 38, originY - 65, 30, 26, 12);
            drawWatermarkLeaf(g2, originX - 52, originY - 105, -25, 28, 13);
            drawWatermarkLeaf(g2, originX - 64, originY - 145, 25, 26, 12);
            drawWatermarkLeaf(g2, originX - 70, originY - 180, -10, 22, 10);
        }

        private void drawWatermarkLeaf(Graphics2D g2, int x, int y, double angleDeg, int length, int width) {
            AffineTransform old = g2.getTransform();
            g2.translate(x, y);
            g2.rotate(Math.toRadians(angleDeg));

            Path2D.Double leaf = new Path2D.Double();
            leaf.moveTo(0, 0);
            leaf.curveTo(length * 0.35, -width, length * 0.75, -width * 0.6, length, 0);
            leaf.curveTo(length * 0.75, width * 0.6, length * 0.35, width, 0, 0);
            leaf.closePath();

            g2.setColor(new Color(225, 205, 175, 55));
            g2.fill(leaf);
            g2.setColor(new Color(205, 180, 145, 95));
            g2.draw(leaf);

            g2.setTransform(old);
        }
    }

    // =========================================================================
    // UI HELPER PANELS & CUSTOM VECTOR ICONS
    // =========================================================================

    /**
     * White container box with rounded corners and focus-tracking outline border.
     */
    private static class RoundedBoxPanel extends JPanel {
        private boolean isFocused = false;

        public RoundedBoxPanel(int width, int height) {
            setOpaque(false);
            setPreferredSize(new Dimension(width, height));
            setMaximumSize(new Dimension(width, height));
            setMinimumSize(new Dimension(280, height));
        }

        public void setFocused(boolean focused) {
            this.isFocused = focused;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Background White Pill
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(1, 1, w - 2, h - 2, 10, 10);

            // Border (Burgundy on focus, soft beige idle)
            if (isFocused) {
                g2.setColor(BORDER_FOCUS);
                g2.setStroke(new BasicStroke(1.8f));
            } else {
                g2.setColor(BORDER_IDLE);
                g2.setStroke(new BasicStroke(1.2f));
            }
            g2.drawRoundRect(1, 1, w - 3, h - 3, 10, 10);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Decorative Golden Divider with Center Diamond: ──── ◆ ────
     */
    private static class DiamondDividerPanel extends JPanel {
        public DiamondDividerPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(240, 16));
            setMaximumSize(new Dimension(240, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int lineLen = 70;

            g2.setColor(GOLD_ACCENT);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(cx - lineLen, cy, cx - 10, cy);
            g2.drawLine(cx + 10, cy, cx + lineLen, cy);

            // Diamond
            Path2D.Double diamond = new Path2D.Double();
            diamond.moveTo(cx, cy - 4);
            diamond.lineTo(cx + 4, cy);
            diamond.lineTo(cx, cy + 4);
            diamond.lineTo(cx - 4, cy);
            diamond.closePath();
            g2.fill(diamond);

            g2.dispose();
        }
    }

    /**
     * Vector icon for outline user silhouette: 👤
     */
    private static class UserOutlineIcon implements Icon {
        private final Color color;
        public UserOutlineIcon(Color color) { this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Head
            g2.drawOval(x + 5, y + 2, 10, 10);

            // Shoulders
            Path2D.Double shoulders = new Path2D.Double();
            shoulders.moveTo(x + 2, y + 18);
            shoulders.curveTo(x + 4, y + 14, x + 7, y + 13, x + 10, y + 13);
            shoulders.curveTo(x + 13, y + 13, x + 16, y + 14, x + 18, y + 18);
            g2.draw(shoulders);

            g2.dispose();
        }

        @Override public int getIconWidth() { return 20; }
        @Override public int getIconHeight() { return 20; }
    }

    /**
     * Vector icon for outline padlock: 🔒
     */
    private static class LockOutlineIcon implements Icon {
        private final Color color;
        public LockOutlineIcon(Color color) { this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Shackle
            g2.drawArc(x + 4, y + 2, 8, 8, 0, 180);
            g2.drawLine(x + 4, y + 6, x + 4, y + 8);
            g2.drawLine(x + 12, y + 6, x + 12, y + 8);

            // Body
            g2.drawRoundRect(x + 2, y + 8, 12, 9, 2, 2);

            // Keyhole
            g2.fillOval(x + 7, y + 10, 2, 2);
            g2.drawLine(x + 8, y + 12, x + 8, y + 14);

            g2.dispose();
        }

        @Override public int getIconWidth() { return 18; }
        @Override public int getIconHeight() { return 18; }
    }

    /**
     * Vector icon for password visibility toggle eye: 👁 / ⊘
     */
    private static class EyeToggleIcon implements Icon {
        private final boolean isVisible;
        public EyeToggleIcon(boolean isVisible) { this.isVisible = isVisible; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(110, 95, 85));
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

            // Slash line if hidden
            if (!isVisible) {
                g2.drawLine(x + 3, y + 3, x + 15, y + 15);
            }

            g2.dispose();
        }

        @Override public int getIconWidth() { return 18; }
        @Override public int getIconHeight() { return 18; }
    }

    /**
     * Creates an academic crest image for the window icon.
     */
    private Image createWindowIcon() {
        int s = 32;
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(MAROON_PRIMARY);
        g2.fillRoundRect(0, 0, s, s, 8, 8);

        g2.setColor(GOLD_ACCENT);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(6, 12, 16, 6);
        g2.drawLine(16, 6, 26, 12);
        g2.drawLine(6, 12, 26, 12);
        g2.drawLine(9, 13, 9, 23);
        g2.drawLine(16, 13, 16, 23);
        g2.drawLine(23, 13, 23, 23);
        g2.drawLine(6, 24, 26, 24);

        g2.dispose();
        return img;
    }

    // =========================================================================
    // PLACEHOLDER HANDLERS & INTERACTIONS
    // =========================================================================

    private void installPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(PLACEHOLDER_COLOR);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_DARK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });
    }

    private void installPasswordPlaceholder(JPasswordField field, String placeholder) {
        field.setEchoChar((char) 0);
        field.setText(placeholder);
        field.setForeground(PLACEHOLDER_COLOR);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String pwd = new String(field.getPassword());
                if (pwd.equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_DARK);
                    if (!isPasswordVisible) {
                        field.setEchoChar('•');
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                String pwd = new String(field.getPassword());
                if (pwd.trim().isEmpty()) {
                    field.setEchoChar((char) 0);
                    field.setText(placeholder);
                    field.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });
    }

    private void togglePasswordVisibility() {
        setPasswordVisibility(!isPasswordVisible);
    }

    private void setPasswordVisibility(boolean visible) {
        this.isPasswordVisible = visible;
        String current = new String(passwordField.getPassword());
        if (!current.equals("Enter your password")) {
            if (visible) {
=======
    private JLabel errorLabel;
    private JButton loginButton;
    private JButton resetButton;

    private final LoginController loginController;

    public LoginFrame(LoginController controller) {
        this.loginController = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Faculty Management System — Login");
        setSize(780, 560);
        setMinimumSize(new Dimension(720, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main Container Split into Left (Branding) and Right (Login Form)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // 1. Left Branding Panel
        JPanel brandPanel = new JPanel();
        brandPanel.setBackground(new Color(30, 58, 138)); // Deep Navy
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBorder(new EmptyBorder(40, 30, 40, 30));

        JLabel logoIconLabel = new JLabel("🏛️");
        logoIconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        logoIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appTitleLabel = new JLabel("Faculty Management");
        appTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        appTitleLabel.setForeground(Color.WHITE);
        appTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appSubTitleLabel = new JLabel("System (FMS)");
        appSubTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appSubTitleLabel.setForeground(new Color(191, 219, 254));
        appSubTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel uniLabel = new JLabel("University of Ruhuna");
        uniLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        uniLabel.setForeground(new Color(226, 232, 240));
        uniLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel facLabel = new JLabel("Faculty of Technology");
        facLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        facLabel.setForeground(new Color(148, 163, 184));
        facLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Demo Accounts Guide Box
        JPanel demoBox = new JPanel();
        demoBox.setLayout(new BoxLayout(demoBox, BoxLayout.Y_AXIS));
        demoBox.setBackground(new Color(23, 37, 84));
        demoBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(59, 130, 246), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        demoBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel demoTitle = new JLabel("Demo Accounts:");
        demoTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        demoTitle.setForeground(new Color(147, 197, 253));

        JLabel demoAdmin = new JLabel("• Admin: admin / admin123");
        demoAdmin.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        demoAdmin.setForeground(new Color(203, 213, 225));

        JLabel demoLec = new JLabel("• Lecturer: lec_kamal / lec123");
        demoLec.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        demoLec.setForeground(new Color(203, 213, 225));

        JLabel demoTO = new JLabel("• TO: to_bandara / to123");
        demoTO.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        demoTO.setForeground(new Color(203, 213, 225));

        JLabel demoStd = new JLabel("• Student: tg2021001 / std123");
        demoStd.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        demoStd.setForeground(new Color(203, 213, 225));

        demoBox.add(demoTitle);
        demoBox.add(Box.createVerticalStrut(4));
        demoBox.add(demoAdmin);
        demoBox.add(demoLec);
        demoBox.add(demoTO);
        demoBox.add(demoStd);

        brandPanel.add(Box.createVerticalGlue());
        brandPanel.add(logoIconLabel);
        brandPanel.add(Box.createVerticalStrut(15));
        brandPanel.add(appTitleLabel);
        brandPanel.add(appSubTitleLabel);
        brandPanel.add(Box.createVerticalStrut(10));
        brandPanel.add(uniLabel);
        brandPanel.add(facLabel);
        brandPanel.add(Box.createVerticalStrut(20));
        brandPanel.add(demoBox);
        brandPanel.add(Box.createVerticalGlue());

        // 2. Right Login Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(25, 35, 25, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.weightx = 1.0;

        // Form Title
        JLabel signInLabel = new JLabel("Welcome Back");
        signInLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        signInLabel.setForeground(new Color(15, 23, 42));

        JLabel signInSub = new JLabel("Please select your role and enter credentials");
        signInSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signInSub.setForeground(new Color(100, 116, 139));

        // Error message label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setForeground(new Color(220, 38, 38));

        // Role Selection Dropdown
        JLabel roleLabel = new JLabel("Select Role");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleLabel.setForeground(new Color(51, 65, 85));

        String[] roles = {"-- Select Role --", "Admin", "Lecturer", "Technical Officer", "Undergraduate"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setPreferredSize(new Dimension(200, 36));
        roleComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(3, 5, 3, 5)
        ));

        // Username
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        usernameLabel.setForeground(new Color(51, 65, 85));

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(200, 36));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));

        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passwordLabel.setForeground(new Color(51, 65, 85));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 36));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));

        // Show Password Checkbox
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheckBox.setBackground(Color.WHITE);
        showPasswordCheckBox.setFocusPainted(false);
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
>>>>>>> Aysha
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
<<<<<<< HEAD
        }
        showPasswordCheckBox.setSelected(visible);
        eyeToggleButton.setIcon(new EyeToggleIcon(visible));
=======
        });

        // Buttons Panel (Using OOP CustomButton components: PrimaryButton & SecondaryButton)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);

        loginButton = new PrimaryButton("Sign In");
        loginButton.setPreferredSize(new Dimension(100, 38));
        loginButton.addActionListener(e -> performLogin());

        resetButton = new SecondaryButton("Reset");
        resetButton.setPreferredSize(new Dimension(100, 38));
        resetButton.addActionListener(e -> resetFields());

        buttonPanel.add(loginButton);
        buttonPanel.add(resetButton);

        // Enter key listeners
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        roleComboBox.addKeyListener(enterKeyListener);
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        // Adding components to Form Panel
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(signInLabel, gbc);

        gbc.gridy = 1;
        formPanel.add(signInSub, gbc);

        gbc.gridy = 2;
        formPanel.add(errorLabel, gbc);

        gbc.gridy = 3;
        formPanel.add(roleLabel, gbc);

        gbc.gridy = 4;
        formPanel.add(roleComboBox, gbc);

        gbc.gridy = 5;
        formPanel.add(usernameLabel, gbc);

        gbc.gridy = 6;
        formPanel.add(usernameField, gbc);

        gbc.gridy = 7;
        formPanel.add(passwordLabel, gbc);

        gbc.gridy = 8;
        formPanel.add(passwordField, gbc);

        gbc.gridy = 9;
        formPanel.add(showPasswordCheckBox, gbc);

        gbc.gridy = 10;
        gbc.insets = new Insets(12, 0, 0, 0);
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(brandPanel);
        mainPanel.add(formPanel);

        add(mainPanel, BorderLayout.CENTER);
>>>>>>> Aysha
    }

    private void performLogin() {
        clearErrorMessage();
        String selectedRole = (String) roleComboBox.getSelectedItem();
<<<<<<< HEAD
        String username = usernameField.getText().trim();
        if (username.equals("adm") && usernameField.getForeground().equals(PLACEHOLDER_COLOR)) {
            username = "";
        }
        String password = new String(passwordField.getPassword());
        if (password.equals("Enter your password") && passwordField.getForeground().equals(PLACEHOLDER_COLOR)) {
            password = "";
        }
=======
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
>>>>>>> Aysha
        loginController.handleLogin(this, username, password, selectedRole);
    }

    public void showErrorMessage(String message) {
<<<<<<< HEAD
        errorLabel.setText("<html><body style='text-align: center; width: 340px; color: #B91C1C;'>⚠️ " + message + "</body></html>");
=======
        errorLabel.setText("⚠️ " + message);
>>>>>>> Aysha
    }

    public void clearErrorMessage() {
        errorLabel.setText(" ");
    }

    public void resetFields() {
        roleComboBox.setSelectedIndex(0);
<<<<<<< HEAD
        usernameField.setText("adm");
        usernameField.setForeground(PLACEHOLDER_COLOR);
        passwordField.setEchoChar((char) 0);
        passwordField.setText("Enter your password");
        passwordField.setForeground(PLACEHOLDER_COLOR);
        setPasswordVisibility(false);
        clearErrorMessage();
        usernameField.requestFocus();
    }

    // =========================================================================
    // ENCAPSULATION ACCESSORS (Preserves 100% backward compatibility)
    // =========================================================================

=======
        usernameField.setText("");
        passwordField.setText("");
        clearErrorMessage();
        roleComboBox.requestFocus();
    }

    // Encapsulation: Getters and Setters for GUI components
>>>>>>> Aysha
    public String getSelectedRole() {
        return (String) roleComboBox.getSelectedItem();
    }

    public void setSelectedRole(String role) {
        roleComboBox.setSelectedItem(role);
    }

    public String getUsername() {
<<<<<<< HEAD
        String u = usernameField.getText().trim();
        return u.equals("adm") && usernameField.getForeground().equals(PLACEHOLDER_COLOR) ? "" : u;
=======
        return usernameField.getText();
>>>>>>> Aysha
    }

    public void setUsername(String username) {
        usernameField.setText(username);
<<<<<<< HEAD
        usernameField.setForeground(TEXT_DARK);
=======
>>>>>>> Aysha
    }

    public void setPassword(String password) {
        passwordField.setText(password);
<<<<<<< HEAD
        passwordField.setForeground(TEXT_DARK);
        if (!isPasswordVisible) {
            passwordField.setEchoChar('•');
        }
    }

    public JComboBox<String> getRoleComboBox() { return roleComboBox; }
    public JTextField getUsernameField() { return usernameField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
    public JButton getResetButton() { return resetButton; }
    public JLabel getErrorLabel() { return errorLabel; }
    public JCheckBox getShowPasswordCheckBox() { return showPasswordCheckBox; }
=======
    }
>>>>>>> Aysha
}
