package com.faculty.management.gui.common;

import com.faculty.management.controller.LoginController;

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
 * - Left 52%: University lake photo (uni_image.jpeg) with smooth horizontal fade blend into ivory canvas
 * - Bottom-left: Golden & burgundy curve swoosh with motto "Learn • Innovate • Build the Future"
 * - Top-right: Elegant burgundy and gold swoosh ribbons
 * - Bottom-right: Soft botanical leaf watermark motif
 * - Center-right: Two-line title "Faculty Management System", diamond divider,
 *   "University of Ruhuna / Faculty of Technology", "Welcome Back",
 *   rounded input fields with outline vector icons, custom burgundy arrow sign-in button,
 *   and white reset button with reload icon.
 * - 100% responsive for any window size with enhanced university color palette.
 */
public class LoginFrame extends JFrame {

    // --- Enhanced Color Palette matching "University Faculty Login Portal.png" ---
    private static final Color MAROON_DARK    = new Color(74, 5, 17);     // #4A0511 Deepest Title Maroon
    private static final Color MAROON_PRIMARY = new Color(115, 10, 29);   // #730A1D Primary Action & Focus
    private static final Color MAROON_HOVER   = new Color(140, 18, 40);   // #8C1228 Rollover State
    private static final Color MAROON_PRESSED = new Color(78, 4, 18);     // #4E0412 Pressed State
    private static final Color MAROON_SUBTITLE= new Color(110, 22, 38);   // #6E1626 Subtitle

    private static final Color GOLD_ACCENT    = new Color(225, 151, 47);  // #E1972F Ruhuna Rich Gold
    private static final Color GOLD_LIGHT     = new Color(248, 222, 150); // #F8DE96 Champagne Gold
    private static final Color GOLD_DARK      = new Color(185, 120, 28);  // #B9781C Deep Bronze Gold

    private static final Color CREAM_BG       = new Color(252, 245, 237); // #FCF5ED Exact Ivory from design
    private static final Color CREAM_BG_SHADOW= new Color(246, 237, 227); // #F6EDE3 Subtle Vignette Edge
    private static final Color BORDER_IDLE    = new Color(222, 207, 194); // #DECFC2 Subtle idle border
    private static final Color BORDER_FOCUS   = new Color(115, 10, 29);   // #730A1D Active Focus
    private static final Color BORDER_GLOW    = new Color(115, 10, 29, 36);

    private static final Color TEXT_DARK      = new Color(34, 20, 24);    // #221418 Main Text
    private static final Color TEXT_MUTED     = new Color(122, 106, 96);  // #7A6A60 Security Notice
    private static final Color PLACEHOLDER_COLOR = new Color(157, 142, 133); // #9D8E85 Placeholder

    // --- GUI Components ---
    private JComboBox<String> roleComboBox;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JButton eyeToggleButton;
    private JButton loginButton;
    private JButton resetButton;
    private JLabel errorLabel;
    private JPanel errorContainer;

    // Card containers for responsive resizing
    private JPanel formCard;
    private JPanel rootContainer;
    private JPanel leftSpacer;
    private JPanel rightColumnPanel;
    private JPanel roleCardContainer;
    private JPanel userCardContainer;
    private JPanel passCardContainer;
    private RoundedBoxPanel roleBox;
    private RoundedBoxPanel userBox;
    private RoundedBoxPanel passBox;
    private JPanel checkRow;
    private JPanel buttonRow;
    private JPanel footerPanel;
    private JLabel titleLine1;
    private JLabel titleLine2;
    private DiamondDividerPanel divider;
    private JLabel uniLabel;
    private JLabel facLabel;
    private JLabel welcomeBack;

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
        setMinimumSize(new Dimension(880, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(createWindowIcon());

        // Master Background Canvas Panel
        BackgroundCanvasPanel canvas = new BackgroundCanvasPanel();
        canvas.setLayout(new GridBagLayout());
        setContentPane(canvas);

        // Responsive Two-Column Layout Container
        rootContainer = new JPanel(new GridBagLayout()) {
            @Override
            public void doLayout() {
                applyResponsiveLayout();
                super.doLayout();
            }
        };
        rootContainer.setOpaque(false);

        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.weightx = 0.50;
        leftGbc.weighty = 1.0;
        leftGbc.fill = GridBagConstraints.BOTH;
        leftSpacer = new JPanel();
        leftSpacer.setOpaque(false);
        rootContainer.add(leftSpacer, leftGbc);

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 1;
        rightGbc.gridy = 0;
        rightGbc.weightx = 0.50;
        rightGbc.weighty = 1.0;
        rightGbc.fill = GridBagConstraints.BOTH;
        rightGbc.insets = new Insets(10, 20, 10, 30);

        rightColumnPanel = buildRightWrapper();
        rootContainer.add(rightColumnPanel, rightGbc);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        canvas.add(rootContainer, gbc);

        // Component listener for live window resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyResponsiveLayout();
            }
        });
    }

    private JPanel buildRightWrapper() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        formCard = buildFormCard();

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;

        wrapper.add(formCard, c);
        return wrapper;
    }

    private JPanel buildFormCard() {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // 1. Main Header Title: "Faculty Management System"
        titleLine1 = new JLabel("Faculty Management");
        titleLine1.setFont(new Font("Georgia", Font.BOLD, 28));
        titleLine1.setForeground(MAROON_DARK);
        titleLine1.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleLine2 = new JLabel("System");
        titleLine2.setFont(new Font("Georgia", Font.BOLD, 28));
        titleLine2.setForeground(MAROON_DARK);
        titleLine2.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 2. Decorative Golden Divider Line with Center Diamond
        divider = new DiamondDividerPanel();
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. Subtitles
        uniLabel = new JLabel("University of Ruhuna");
        uniLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        uniLabel.setForeground(MAROON_DARK);
        uniLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        facLabel = new JLabel("Faculty of Technology");
        facLabel.setFont(new Font("Georgia", Font.ITALIC, 14));
        facLabel.setForeground(MAROON_SUBTITLE);
        facLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 4. "Welcome Back" Heading
        welcomeBack = new JLabel("Welcome Back");
        welcomeBack.setFont(new Font("Georgia", Font.BOLD, 26));
        welcomeBack.setForeground(MAROON_DARK);
        welcomeBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeBack.setBorder(new EmptyBorder(8, 0, 4, 0));

        // 5. Error Container (reserves space to avoid layout jump)
        errorContainer = new JPanel(new BorderLayout());
        errorContainer.setOpaque(false);
        errorContainer.setPreferredSize(new Dimension(380, 24));
        errorContainer.setMaximumSize(new Dimension(500, 24));
        errorContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorLabel = new JLabel(" ", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setForeground(new Color(185, 28, 28));
        errorContainer.add(errorLabel, BorderLayout.CENTER);

        int formWidth = 380;

        // Field 1: Role
        roleCardContainer = createRoleCard(formWidth);
        roleCardContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Field 2: Username
        userCardContainer = createUsernameCard(formWidth);
        userCardContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Field 3: Password
        passCardContainer = createPasswordCard(formWidth);
        passCardContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Show Password Checkbox
        checkRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkRow.setOpaque(false);
        checkRow.setMaximumSize(new Dimension(formWidth, 22));
        checkRow.setPreferredSize(new Dimension(formWidth, 22));
        checkRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheckBox.setForeground(TEXT_DARK);
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.setFocusPainted(false);
        showPasswordCheckBox.addActionListener(e -> setPasswordVisibility(showPasswordCheckBox.isSelected()));
        checkRow.add(showPasswordCheckBox);

        // Buttons Row: Login (Arrow) + Reset
        buttonRow = createButtonRow(formWidth);
        buttonRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Footer: Golden diamond + Secure Access
        footerPanel = createFooterPanel(formWidth);
        footerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Assembly
        card.add(titleLine1);
        card.add(titleLine2);
        card.add(Box.createVerticalStrut(4));
        card.add(divider);
        card.add(Box.createVerticalStrut(4));
        card.add(uniLabel);
        card.add(facLabel);
        card.add(welcomeBack);
        card.add(errorContainer);
        card.add(Box.createVerticalStrut(2));
        card.add(roleCardContainer);
        card.add(Box.createVerticalStrut(8));
        card.add(userCardContainer);
        card.add(Box.createVerticalStrut(8));
        card.add(passCardContainer);
        card.add(Box.createVerticalStrut(6));
        card.add(checkRow);
        card.add(Box.createVerticalStrut(10));
        card.add(buttonRow);
        card.add(Box.createVerticalStrut(12));
        card.add(footerPanel);

        return card;
    }

    public void applyResponsiveLayout() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        // Dynamic responsive width based on screen width
        int formWidth = Math.min(480, Math.max(340, (int) (w * 0.28)));
        int fieldHeight = 45;

        // Responsive left/right split
        double photoRatio = (w >= 1400) ? 0.54 : ((w >= 1100) ? 0.50 : 0.46);
        if (rootContainer != null && rootContainer.getLayout() instanceof GridBagLayout) {
            GridBagLayout gbl = (GridBagLayout) rootContainer.getLayout();
            GridBagConstraints gbcLeft = gbl.getConstraints(leftSpacer);
            GridBagConstraints gbcRight = gbl.getConstraints(rightColumnPanel);
            gbcLeft.weightx = photoRatio;
            gbcRight.weightx = 1.0 - photoRatio;
            gbl.setConstraints(leftSpacer, gbcLeft);
            gbl.setConstraints(rightColumnPanel, gbcRight);
        }

        if (roleBox != null && roleCardContainer != null) {
            Dimension boxDim = new Dimension(formWidth, fieldHeight);
            roleBox.setPreferredSize(boxDim);
            roleBox.setMaximumSize(boxDim);
            userBox.setPreferredSize(boxDim);
            userBox.setMaximumSize(boxDim);
            passBox.setPreferredSize(boxDim);
            passBox.setMaximumSize(boxDim);

            int containerH = fieldHeight + 24;
            Dimension containerDim = new Dimension(formWidth, containerH);
            roleCardContainer.setPreferredSize(containerDim);
            roleCardContainer.setMaximumSize(containerDim);
            userCardContainer.setPreferredSize(containerDim);
            userCardContainer.setMaximumSize(containerDim);
            passCardContainer.setPreferredSize(containerDim);
            passCardContainer.setMaximumSize(containerDim);

            checkRow.setPreferredSize(new Dimension(formWidth, 22));
            checkRow.setMaximumSize(new Dimension(formWidth, 22));

            buttonRow.setPreferredSize(new Dimension(formWidth, fieldHeight));
            buttonRow.setMaximumSize(new Dimension(formWidth, fieldHeight));

            footerPanel.setPreferredSize(new Dimension(formWidth, 34));
            footerPanel.setMaximumSize(new Dimension(formWidth, 34));
            errorContainer.setPreferredSize(new Dimension(formWidth, 24));
            errorContainer.setMaximumSize(new Dimension(formWidth, 24));

            int dividerWidth = (int) (formWidth * 0.65);
            divider.setPreferredSize(new Dimension(dividerWidth, 16));
            divider.setMaximumSize(new Dimension(dividerWidth, 16));
        }
    }

    private JPanel createRoleCard(int width) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(width, 70));
        container.setPreferredSize(new Dimension(width, 70));

        JLabel label = new JLabel("Select Role");
        label.setFont(new Font("Georgia", Font.BOLD, 13));
        label.setForeground(MAROON_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setPreferredSize(new Dimension(width, 20));
        label.setMaximumSize(new Dimension(width, 20));
        label.setBorder(new EmptyBorder(0, 2, 4, 0));
        container.add(label);

        roleBox = new RoundedBoxPanel(width, 45);
        roleBox.setLayout(new BorderLayout(8, 0));
        roleBox.setBorder(new EmptyBorder(2, 12, 2, 8));
        roleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(new UserOutlineIcon(MAROON_PRIMARY));
        roleBox.add(iconLabel, BorderLayout.WEST);

        String[] roles = {"Admin", "Lecturer", "Technical Officer", "Undergraduate"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleComboBox.setForeground(TEXT_DARK);
        roleComboBox.setOpaque(false);
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        roleComboBox.setFocusable(false);

        roleComboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(MAROON_DARK);
                        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
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
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {}
        });

        roleComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setBorder(new EmptyBorder(6, 12, 6, 12));
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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

        roleBox.add(roleComboBox, BorderLayout.CENTER);
        container.add(roleBox);
        return container;
    }

    private JPanel createUsernameCard(int width) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(width, 70));
        container.setPreferredSize(new Dimension(width, 70));

        JLabel label = new JLabel("Username");
        label.setFont(new Font("Georgia", Font.BOLD, 13));
        label.setForeground(MAROON_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setPreferredSize(new Dimension(width, 20));
        label.setMaximumSize(new Dimension(width, 20));
        label.setBorder(new EmptyBorder(0, 2, 4, 0));
        container.add(label);

        userBox = new RoundedBoxPanel(width, 45);
        userBox.setLayout(new BorderLayout(8, 0));
        userBox.setBorder(new EmptyBorder(2, 12, 2, 10));
        userBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(new UserOutlineIcon(MAROON_PRIMARY));
        userBox.add(iconLabel, BorderLayout.WEST);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameField.setForeground(TEXT_DARK);
        usernameField.setCaretColor(MAROON_PRIMARY);
        usernameField.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 4));
        usernameField.setOpaque(false);
        installPlaceholder(usernameField, "adm");

        usernameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { userBox.setFocused(true); }
            @Override
            public void focusLost(FocusEvent e) { userBox.setFocused(false); }
        });

        usernameField.addActionListener(e -> performLogin());

        userBox.add(usernameField, BorderLayout.CENTER);
        container.add(userBox);
        return container;
    }

    private JPanel createPasswordCard(int width) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(width, 70));
        container.setPreferredSize(new Dimension(width, 70));

        JLabel label = new JLabel("Password");
        label.setFont(new Font("Georgia", Font.BOLD, 13));
        label.setForeground(MAROON_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setPreferredSize(new Dimension(width, 20));
        label.setMaximumSize(new Dimension(width, 20));
        label.setBorder(new EmptyBorder(0, 2, 4, 0));
        container.add(label);

        passBox = new RoundedBoxPanel(width, 45);
        passBox.setLayout(new BorderLayout(8, 0));
        passBox.setBorder(new EmptyBorder(2, 12, 2, 6));
        passBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(new LockOutlineIcon(MAROON_PRIMARY));
        passBox.add(iconLabel, BorderLayout.WEST);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setForeground(TEXT_DARK);
        passwordField.setCaretColor(MAROON_PRIMARY);
        passwordField.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 4));
        passwordField.setOpaque(false);
        passwordField.setEchoChar('•');
        installPasswordPlaceholder(passwordField, "Enter your password");

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { passBox.setFocused(true); }
            @Override
            public void focusLost(FocusEvent e) { passBox.setFocused(false); }
        });

        passwordField.addActionListener(e -> performLogin());
        passBox.add(passwordField, BorderLayout.CENTER);

        eyeToggleButton = new JButton(new EyeToggleIcon(false));
        eyeToggleButton.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        eyeToggleButton.setContentAreaFilled(false);
        eyeToggleButton.setFocusPainted(false);
        eyeToggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eyeToggleButton.setToolTipText("Toggle password visibility");
        eyeToggleButton.addActionListener(e -> togglePasswordVisibility());
        passBox.add(eyeToggleButton, BorderLayout.EAST);

        container.add(passBox);
        return container;
    }

    private JPanel createButtonRow(int width) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(width, 45));
        row.setPreferredSize(new Dimension(width, 45));

        // 1. Login Button with Sign-In Arrow [ →| ]
        loginButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color top = MAROON_HOVER;
                Color btm = MAROON_PRIMARY;
                if (getModel().isPressed()) {
                    top = MAROON_PRESSED;
                    btm = MAROON_PRESSED;
                } else if (!getModel().isRollover()) {
                    top = new Color(125, 12, 33);
                    btm = MAROON_PRIMARY;
                }
                GradientPaint gp = new GradientPaint(0, 0, top, 0, getHeight(), btm);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 9, 9);

                // Draw Sign-In Arrow (→|) in white
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx - 11, cy, cx + 5, cy);
                g2.drawLine(cx + 1, cy - 5, cx + 6, cy);
                g2.drawLine(cx + 1, cy + 5, cx + 6, cy);
                g2.drawLine(cx + 11, cy - 7, cx + 11, cy + 7);

                g2.dispose();
            }
        };
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setContentAreaFilled(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setToolTipText("Sign In to Portal");
        loginButton.addActionListener(e -> performLogin());

        // 2. Reset Button with reload icon [ ↻  Reset ]
        resetButton = new JButton("Reset") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = getModel().isRollover() ? new Color(250, 242, 234) : Color.WHITE;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(MAROON_PRIMARY);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 9, 9);

                int iconX = 22;
                int iconY = getHeight() / 2;
                g2.setColor(MAROON_PRIMARY);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(iconX - 7, iconY - 7, 14, 14, 45, 280);
                g2.drawLine(iconX + 3, iconY - 7, iconX + 8, iconY - 7);
                g2.drawLine(iconX + 8, iconY - 7, iconX + 8, iconY - 2);

                g2.setFont(new Font("Georgia", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                int textX = iconX + 16;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), textX, textY);

                g2.dispose();
            }
        };
        resetButton.setBorder(BorderFactory.createEmptyBorder());
        resetButton.setContentAreaFilled(false);
        resetButton.setFocusPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.setToolTipText("Clear all fields");
        resetButton.addActionListener(e -> resetFields());

        GridBagConstraints gbcLogin = new GridBagConstraints();
        gbcLogin.gridx = 0;
        gbcLogin.gridy = 0;
        gbcLogin.weightx = 0.62;
        gbcLogin.weighty = 1.0;
        gbcLogin.fill = GridBagConstraints.BOTH;
        gbcLogin.insets = new Insets(0, 0, 0, 10);
        row.add(loginButton, gbcLogin);

        GridBagConstraints gbcReset = new GridBagConstraints();
        gbcReset.gridx = 1;
        gbcReset.gridy = 0;
        gbcReset.weightx = 0.38;
        gbcReset.weighty = 1.0;
        gbcReset.fill = GridBagConstraints.BOTH;
        row.add(resetButton, gbcReset);

        return row;
    }

    private JPanel createFooterPanel(int width) {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);
        footer.setMaximumSize(new Dimension(width, 34));
        footer.setPreferredSize(new Dimension(width, 34));

        JPanel diamondPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                Path2D.Double dia = new Path2D.Double();
                dia.moveTo(cx, cy - 3.5);
                dia.lineTo(cx + 3.5, cy);
                dia.lineTo(cx, cy + 3.5);
                dia.lineTo(cx - 3.5, cy);
                dia.closePath();
                g2.setColor(GOLD_ACCENT);
                g2.fill(dia);
                g2.dispose();
            }
        };
        diamondPanel.setOpaque(false);
        diamondPanel.setPreferredSize(new Dimension(20, 10));
        diamondPanel.setMaximumSize(new Dimension(20, 10));
        diamondPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel lockRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        lockRow.setOpaque(false);
        JLabel lockIcon = new JLabel(new LockOutlineIcon(TEXT_MUTED));
        JLabel secureLabel = new JLabel("Secure access for authorized users");
        secureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        secureLabel.setForeground(TEXT_MUTED);

        lockRow.add(lockIcon);
        lockRow.add(secureLabel);

        footer.add(diamondPanel);
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
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            // 1. Base Ivory Background
            GradientPaint canvasGrad = new GradientPaint(
                    0, 0, CREAM_BG,
                    w, h, CREAM_BG_SHADOW
            );
            g2.setPaint(canvasGrad);
            g2.fillRect(0, 0, w, h);

            // 2. Responsive photo boundary
            double photoRatio = (w >= 1400) ? 0.54 : ((w >= 1100) ? 0.50 : 0.46);
            int photoWidth = (int) (w * photoRatio);

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
                g2.drawImage(universityImage, drawX, drawY, scaledW, scaledH, null);
                g2.setClip(oldClip);

                // Smooth Fade Transition to Ivory Canvas
                int blendStart = (int) (photoWidth * 0.80);
                int blendLen = photoWidth - blendStart + 4;
                LinearGradientPaint blendGrad = new LinearGradientPaint(
                        blendStart, 0, blendStart + blendLen, 0,
                        new float[]{0.0f, 0.25f, 0.60f, 1.0f},
                        new Color[]{
                                new Color(CREAM_BG.getRed(), CREAM_BG.getGreen(), CREAM_BG.getBlue(), 0),
                                new Color(CREAM_BG.getRed(), CREAM_BG.getGreen(), CREAM_BG.getBlue(), 80),
                                new Color(CREAM_BG.getRed(), CREAM_BG.getGreen(), CREAM_BG.getBlue(), 210),
                                CREAM_BG
                        }
                );
                g2.setPaint(blendGrad);
                g2.fillRect(blendStart, 0, blendLen, h);
            }

            // 3. Bottom-Left Golden & Burgundy Ribbon Waves
            drawBottomLeftRibbons(g2, w, h);

            // 4. Motto under Photo: "─── Learn • Innovate • Build the Future ───"
            drawMottoLine(g2, photoWidth, h);

            // 5. Top-Right Burgundy & Gold Swirl Curve
            drawTopRightSwirl(g2, w, h);

            // 6. Bottom-Right Delicate Botanical Leaf Watermark
            drawBotanicalWatermark(g2, w - 18, h - 18);

            g2.dispose();
        }

        private void drawBottomLeftRibbons(Graphics2D g2, int w, int h) {
            Path2D.Double maroonWave = new Path2D.Double();
            maroonWave.moveTo(0, h * 0.86);
            maroonWave.curveTo(w * 0.08, h * 0.88, w * 0.16, h * 0.94, w * 0.24, h);
            maroonWave.lineTo(0, h);
            maroonWave.closePath();
            g2.setColor(MAROON_DARK);
            g2.fill(maroonWave);

            Path2D.Double goldRibbon = new Path2D.Double();
            goldRibbon.moveTo(0, h * 0.81);
            goldRibbon.curveTo(w * 0.08, h * 0.83, w * 0.18, h * 0.92, w * 0.27, h);
            goldRibbon.lineTo(w * 0.23, h);
            goldRibbon.curveTo(w * 0.15, h * 0.92, w * 0.07, h * 0.85, 0, h * 0.84);
            goldRibbon.closePath();

            GradientPaint goldGrad = new GradientPaint(
                    0, (float) (h * 0.81), GOLD_LIGHT,
                    (float) (w * 0.27), (float) h, GOLD_DARK
            );
            g2.setPaint(goldGrad);
            g2.fill(goldRibbon);
        }

        private void drawMottoLine(Graphics2D g2, int photoWidth, int h) {
            int lineY = (int) (h * 0.925);
            int centerX = (int) (photoWidth * 0.44);

            String motto = "Learn  •  Innovate  •  Build the Future";
            Font font = new Font("Georgia", Font.ITALIC, 13);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();
            int strW = fm.stringWidth(motto);

            int textX = centerX - strW / 2;
            int textY = lineY + fm.getAscent() / 2 - 2;

            g2.setColor(new Color(235, 195, 120, 210));
            g2.setStroke(new BasicStroke(1.2f));
            int gap = 14;
            int lineLen = 42;
            g2.drawLine(textX - gap - lineLen, lineY, textX - gap, lineY);
            g2.drawLine(textX + strW + gap, lineY, textX + strW + gap + lineLen, lineY);

            g2.setColor(new Color(15, 8, 8, 200));
            g2.drawString(motto, textX + 1, textY + 1);
            g2.setColor(Color.WHITE);
            g2.drawString(motto, textX, textY);
        }

        private void drawTopRightSwirl(Graphics2D g2, int w, int h) {
            Path2D.Double maroonWave = new Path2D.Double();
            maroonWave.moveTo(w * 0.80, 0);
            maroonWave.curveTo(w * 0.84, h * 0.10, w * 0.90, h * 0.18, w, h * 0.22);
            maroonWave.lineTo(w, 0);
            maroonWave.closePath();

            GradientPaint maroonGrad = new GradientPaint(
                    (float) (w * 0.80), 0, MAROON_DARK,
                    (float) w, (float) (h * 0.22), MAROON_PRIMARY
            );
            g2.setPaint(maroonGrad);
            g2.fill(maroonWave);

            Path2D.Double goldWave = new Path2D.Double();
            goldWave.moveTo(w * 0.78, 0);
            goldWave.curveTo(w * 0.82, h * 0.12, w * 0.89, h * 0.21, w, h * 0.26);
            goldWave.lineTo(w, h * 0.23);
            goldWave.curveTo(w * 0.90, h * 0.19, w * 0.84, h * 0.10, w * 0.80, 0);
            goldWave.closePath();

            GradientPaint goldGrad = new GradientPaint(
                    (float) (w * 0.78), 0, GOLD_LIGHT,
                    (float) w, (float) (h * 0.26), GOLD_DARK
            );
            g2.setPaint(goldGrad);
            g2.fill(goldWave);
        }

        private void drawBotanicalWatermark(Graphics2D g2, int originX, int originY) {
            g2.setColor(new Color(215, 190, 160, 85));
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            Path2D.Double stem = new Path2D.Double();
            stem.moveTo(originX, originY);
            stem.curveTo(originX - 35, originY - 50, originX - 68, originY - 115, originX - 72, originY - 190);
            g2.draw(stem);

            drawWatermarkLeaf(g2, originX - 18, originY - 32, -35, 25, 12);
            drawWatermarkLeaf(g2, originX - 38, originY - 70, 30, 27, 13);
            drawWatermarkLeaf(g2, originX - 54, originY - 112, -25, 29, 14);
            drawWatermarkLeaf(g2, originX - 66, originY - 152, 25, 27, 13);
            drawWatermarkLeaf(g2, originX - 72, originY - 190, -10, 23, 11);
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

            g2.setColor(new Color(225, 205, 175, 60));
            g2.fill(leaf);
            g2.setColor(new Color(205, 175, 140, 100));
            g2.draw(leaf);

            g2.setTransform(old);
        }
    }

    // =========================================================================
    // ROUNDED BOX PANEL WITH ACTIVE FOCUS GLOW
    // =========================================================================
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

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(1, 1, w - 2, h - 2, 10, 10);

            if (isFocused) {
                g2.setColor(BORDER_GLOW);
                g2.setStroke(new BasicStroke(3.0f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, 10, 10);

                g2.setColor(BORDER_FOCUS);
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, 10, 10);
            } else {
                g2.setColor(BORDER_IDLE);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, 10, 10);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class DiamondDividerPanel extends JPanel {
        public DiamondDividerPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(260, 16));
            setMaximumSize(new Dimension(260, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int lineLen = (int) (getWidth() * 0.38);

            g2.setColor(GOLD_ACCENT);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(cx - lineLen, cy, cx - 10, cy);
            g2.drawLine(cx + 10, cy, cx + lineLen, cy);

            Path2D.Double diamond = new Path2D.Double();
            diamond.moveTo(cx, cy - 4.5);
            diamond.lineTo(cx + 4.5, cy);
            diamond.lineTo(cx, cy + 4.5);
            diamond.lineTo(cx - 4.5, cy);
            diamond.closePath();
            g2.fill(diamond);

            g2.dispose();
        }
    }

    private static class UserOutlineIcon implements Icon {
        private final Color color;
        public UserOutlineIcon(Color color) { this.color = color; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

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

    private static class EyeToggleIcon implements Icon {
        private final boolean isVisible;
        public EyeToggleIcon(boolean isVisible) { this.isVisible = isVisible; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(110, 95, 85));
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            Path2D.Double eye = new Path2D.Double();
            eye.moveTo(x + 2, y + 9);
            eye.curveTo(x + 6, y + 3, x + 12, y + 3, x + 16, y + 9);
            eye.curveTo(x + 12, y + 15, x + 6, y + 15, x + 2, y + 9);
            eye.closePath();
            g2.draw(eye);

            g2.fillOval(x + 7, y + 7, 4, 4);

            if (!isVisible) {
                g2.drawLine(x + 3, y + 3, x + 15, y + 15);
            }

            g2.dispose();
        }

        @Override public int getIconWidth() { return 18; }
        @Override public int getIconHeight() { return 18; }
    }

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
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        }
        showPasswordCheckBox.setSelected(visible);
        eyeToggleButton.setIcon(new EyeToggleIcon(visible));
    }

    private void performLogin() {
        clearErrorMessage();
        String selectedRole = (String) roleComboBox.getSelectedItem();
        String username = usernameField.getText().trim();
        if (username.equals("adm") && usernameField.getForeground().equals(PLACEHOLDER_COLOR)) {
            username = "";
        }
        String password = new String(passwordField.getPassword());
        if (password.equals("Enter your password") && passwordField.getForeground().equals(PLACEHOLDER_COLOR)) {
            password = "";
        }
        loginController.handleLogin(this, username, password, selectedRole);
    }

    public void showErrorMessage(String message) {
        errorLabel.setText("⚠️ " + message);
        errorContainer.setOpaque(true);
        errorContainer.setBackground(new Color(254, 226, 226));
        errorContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(248, 113, 113), 1, true),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        errorContainer.revalidate();
        errorContainer.repaint();
    }

    public void clearErrorMessage() {
        errorLabel.setText(" ");
        errorContainer.setOpaque(false);
        errorContainer.setBorder(null);
        errorContainer.revalidate();
        errorContainer.repaint();
    }

    public void resetFields() {
        roleComboBox.setSelectedIndex(0);
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

    public String getSelectedRole() { return (String) roleComboBox.getSelectedItem(); }
    public void setSelectedRole(String role) { roleComboBox.setSelectedItem(role); }
    public String getUsername() {
        String u = usernameField.getText().trim();
        return u.equals("adm") && usernameField.getForeground().equals(PLACEHOLDER_COLOR) ? "" : u;
    }
    public void setUsername(String username) {
        usernameField.setText(username);
        usernameField.setForeground(TEXT_DARK);
    }
    public void setPassword(String password) {
        passwordField.setText(password);
        passwordField.setForeground(TEXT_DARK);
        if (!isPasswordVisible) passwordField.setEchoChar('•');
    }
    public JComboBox<String> getRoleComboBox() { return roleComboBox; }
    public JTextField getUsernameField() { return usernameField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
    public JButton getResetButton() { return resetButton; }
    public JLabel getErrorLabel() { return errorLabel; }
    public JCheckBox getShowPasswordCheckBox() { return showPasswordCheckBox; }
}
