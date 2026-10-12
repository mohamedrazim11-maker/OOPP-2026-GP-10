package com.faculty.management.gui.common;

import com.faculty.management.controller.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
<<<<<<< HEAD
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

/**
 * University-themed login screen for the Faculty Management System.
 */
public class LoginFrame extends JFrame {

    private UITheme.ModernTextField usernameField;
    private UITheme.ModernPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JPanel errorBanner;
    private JLabel errorLabel;
    private UITheme.ModernButton loginButton;
    private UITheme.ModernButton resetButton;
=======

/**
 * Login GUI for Faculty Management System.
 * Pure Java Swing - No third-party frameworks.
 */
public class LoginFrame extends JFrame {

    private JComboBox<String> roleComboBox;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JLabel errorLabel;
    private JButton loginButton;
    private JButton resetButton;
>>>>>>> test

    private final LoginController loginController;

    public LoginFrame(LoginController controller) {
        this.loginController = controller;
        initComponents();
    }

    private void initComponents() {
<<<<<<< HEAD
        setTitle("Faculty Management System — Portal Authentication");
        setSize(1000, 650);
        setMinimumSize(new Dimension(900, 590));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(new Color(239, 240, 255));
        page.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel shell = new JPanel(new GridLayout(1, 2));
        shell.setBackground(Color.WHITE);
        shell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 220, 255), 1),
                new EmptyBorder(4, 4, 4, 4)));

        shell.add(buildBrandPanel());
        shell.add(buildLoginPanel());
        page.add(shell, BorderLayout.CENTER);
        setContentPane(page);
    }

    private JPanel buildBrandPanel() {
        JPanel brandPanel = new BrandPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBorder(new EmptyBorder(36, 38, 30, 38));

        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        logoRow.setOpaque(false);
        JLabel capMark = new JLabel("◆");
        capMark.setFont(new Font("Segoe UI Symbol", Font.BOLD, 29));
        capMark.setForeground(Color.WHITE);
        JLabel logoLabel = new JLabel("FMS");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        logoLabel.setForeground(Color.WHITE);
        logoRow.add(capMark);
        logoRow.add(logoLabel);

        JLabel facultyLabel = new JLabel("FACULTY OF TECHNOLOGY");
        facultyLabel.setFont(UITheme.FONT_BODY_BOLD);
        facultyLabel.setForeground(new Color(211, 207, 255));

        JLabel titleLabel = new JLabel("<html>Faculty Management<br>System (FMS)</html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 27));
        titleLabel.setForeground(Color.WHITE);

        JLabel universityLabel = new JLabel("University of Ruhuna | Sri Lanka");
        universityLabel.setFont(UITheme.FONT_BODY);
        universityLabel.setForeground(new Color(222, 222, 255));

        JPanel accentLine = new JPanel();
        accentLine.setBackground(new Color(177, 151, 255));
        accentLine.setMaximumSize(new Dimension(54, 4));
        accentLine.setPreferredSize(new Dimension(54, 4));
        accentLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel features = new JPanel(new GridLayout(1, 4, 8, 0));
        features.setOpaque(false);
        features.setMaximumSize(new Dimension(Integer.MAX_VALUE, 104));
        features.add(createFeature("S", "Student\nRecords"));
        features.add(createFeature("C", "Courses &\nTimetable"));
        features.add(createFeature("A", "Attendance\n& Grades"));
        features.add(createFeature("N", "Notices &\nUpdates"));

        JLabel quote = new JLabel("<html><i>“ Empowering Education<br>&nbsp;&nbsp; Through Technology ”</i></html>");
        quote.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        quote.setForeground(new Color(226, 222, 255));
        quote.setBorder(new EmptyBorder(14, 0, 0, 0));

        brandPanel.add(logoRow);
        brandPanel.add(Box.createVerticalStrut(12));
        brandPanel.add(leftAligned(facultyLabel));
        brandPanel.add(Box.createVerticalStrut(9));
        brandPanel.add(leftAligned(titleLabel));
        brandPanel.add(Box.createVerticalStrut(7));
        brandPanel.add(leftAligned(universityLabel));
        brandPanel.add(Box.createVerticalStrut(16));
        brandPanel.add(accentLine);
        brandPanel.add(Box.createVerticalStrut(24));
        brandPanel.add(features);
        brandPanel.add(Box.createVerticalStrut(18));
        brandPanel.add(leftAligned(quote));
        brandPanel.add(Box.createVerticalGlue());
        return brandPanel;
    }

    private JPanel createFeature(String mark, String label) {
        JPanel feature = new JPanel();
        feature.setLayout(new BoxLayout(feature, BoxLayout.Y_AXIS));
        feature.setOpaque(false);

        JLabel icon = new JLabel(mark, SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 15));
        icon.setForeground(Color.WHITE);
        icon.setOpaque(true);
        icon.setBackground(new Color(255, 255, 255, 48));
        icon.setPreferredSize(new Dimension(38, 38));
        icon.setMinimumSize(new Dimension(38, 38));
        icon.setMaximumSize(new Dimension(38, 38));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel text = new JLabel("<html><div style='text-align:center;'>"
                + label.replace("\n", "<br>") + "</div></html>", SwingConstants.CENTER);
        text.setFont(UITheme.FONT_SMALL_BOLD);
        text.setForeground(new Color(239, 237, 255));
        text.setAlignmentX(Component.CENTER_ALIGNMENT);

        feature.add(icon);
        feature.add(Box.createVerticalStrut(8));
        feature.add(text);
        return feature;
    }

    private Component leftAligned(JComponent component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        return component;
    }

    private JPanel buildLoginPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(34, 42, 28, 42));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setMaximumSize(new Dimension(440, 520));

        UITheme.PillBadge secureBadge = new UITheme.PillBadge(
                "SECURE LOGIN", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, UITheme.PRIMARY_TINT);
        secureBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel signInLabel = new JLabel("Welcome Back");
        signInLabel.setFont(new Font("Segoe UI", Font.BOLD, 27));
        signInLabel.setForeground(UITheme.PRIMARY_DARK);
        signInLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel signInSub = new JLabel("Enter your credentials to access your dashboard");
        signInSub.setFont(UITheme.FONT_BODY);
        signInSub.setForeground(UITheme.TEXT_MUTED);
        signInSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        errorBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        errorBanner.setBackground(UITheme.DANGER_LIGHT);
        errorBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.DANGER_BORDER, 1),
                new EmptyBorder(4, 8, 4, 8)));
        errorBanner.setVisible(false);
        errorBanner.setAlignmentX(Component.LEFT_ALIGNMENT);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.FONT_SMALL_BOLD);
        errorLabel.setForeground(UITheme.DANGER_DARK);
        errorBanner.add(errorLabel);

        usernameField = new UITheme.ModernTextField("Enter your username", 20);
        usernameField.setPreferredSize(new Dimension(240, 42));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        passwordField = new UITheme.ModernPasswordField("Enter your password", 20);
        passwordField.setPreferredSize(new Dimension(240, 42));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JPanel showPasswordRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        showPasswordRow.setOpaque(false);
        showPasswordRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(UITheme.FONT_SMALL);
        showPasswordCheckBox.setForeground(UITheme.TEXT_BODY);
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.setFocusPainted(false);
        showPasswordCheckBox.addActionListener(e ->
                passwordField.setEchoChar(showPasswordCheckBox.isSelected() ? (char) 0 : '•'));
        showPasswordRow.add(showPasswordCheckBox);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginButton = new UITheme.ModernButton("Sign in", UITheme.ButtonStyle.PRIMARY, 10);
        loginButton.setPreferredSize(new Dimension(120, 44));
        loginButton.addActionListener(e -> performLogin());

        resetButton = new UITheme.ModernButton("Clear", UITheme.ButtonStyle.SECONDARY, 10);
        resetButton.setPreferredSize(new Dimension(100, 44));
        resetButton.addActionListener(e -> resetFields());
        buttonPanel.add(loginButton);
        buttonPanel.add(resetButton);

        JPanel divider = new JPanel(new BorderLayout(10, 0));
        divider.setOpaque(false);
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel dividerText = new JLabel("Or continue with", SwingConstants.CENTER);
        dividerText.setFont(UITheme.FONT_SMALL);
        dividerText.setForeground(UITheme.TEXT_MUTED);
        divider.add(createDividerLine(), BorderLayout.WEST);
        divider.add(dividerText, BorderLayout.CENTER);
        divider.add(createDividerLine(), BorderLayout.EAST);

        UITheme.ModernButton demoButton = new UITheme.ModernButton(
                "Student Demo Account", UITheme.ButtonStyle.OUTLINE, 9);
        demoButton.setPreferredSize(new Dimension(200, 42));
        demoButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        demoButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        demoButton.addActionListener(e -> fillDemoCredentials());

        JLabel footerNote = new JLabel("University account access", SwingConstants.CENTER);
        footerNote.setFont(UITheme.FONT_SMALL);
        footerNote.setForeground(UITheme.TEXT_LIGHT);
        footerNote.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(secureBadge);
        form.add(Box.createVerticalStrut(16));
        form.add(signInLabel);
        form.add(Box.createVerticalStrut(5));
        form.add(signInSub);
        form.add(Box.createVerticalStrut(18));
        form.add(errorBanner);
        form.add(Box.createVerticalStrut(8));
        form.add(createInputLabel("Username"));
        form.add(Box.createVerticalStrut(7));
        form.add(usernameField);
        form.add(Box.createVerticalStrut(16));
        form.add(createInputLabel("Password"));
        form.add(Box.createVerticalStrut(7));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(5));
        form.add(showPasswordRow);
        form.add(Box.createVerticalStrut(14));
        form.add(buttonPanel);
        form.add(Box.createVerticalStrut(20));
        form.add(divider);
        form.add(Box.createVerticalStrut(14));
        form.add(demoButton);
        form.add(Box.createVerticalStrut(22));
        form.add(footerNote);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        formPanel.add(form, constraints);

=======
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
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
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
>>>>>>> test
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
<<<<<<< HEAD
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        return formPanel;
    }

    private JLabel createInputLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_BODY_BOLD);
        label.setForeground(UITheme.TEXT_BODY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JComponent createDividerLine() {
        JPanel line = new JPanel();
        line.setBackground(UITheme.BORDER_LIGHT);
        line.setPreferredSize(new Dimension(20, 1));
        return line;
    }

    private void fillDemoCredentials() {
        usernameField.setText("tg2021001");
        passwordField.setText("std123");
        clearErrorMessage();
        usernameField.requestFocus();
=======
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
>>>>>>> test
    }

    private void performLogin() {
        clearErrorMessage();
<<<<<<< HEAD
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        loginController.handleLogin(this, username, password);
    }

    public void showErrorMessage(String message) {
        errorLabel.setText(message);
        errorBanner.setVisible(true);
        errorBanner.revalidate();
        errorBanner.repaint();
=======
        String selectedRole = (String) roleComboBox.getSelectedItem();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        loginController.handleLogin(this, username, password, selectedRole);
    }

    public void showErrorMessage(String message) {
        errorLabel.setText("⚠️ " + message);
>>>>>>> test
    }

    public void clearErrorMessage() {
        errorLabel.setText(" ");
<<<<<<< HEAD
        errorBanner.setVisible(false);
        errorBanner.revalidate();
        errorBanner.repaint();
    }

    public void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
        clearErrorMessage();
        usernameField.requestFocus();
    }

    private static class BrandPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, new Color(60, 47, 194),
                    getWidth(), getHeight(), new Color(89, 61, 222)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(255, 255, 255, 22));
            g2.fill(new Ellipse2D.Double(getWidth() - 150, -110, 260, 260));
            g2.fill(new Ellipse2D.Double(-120, getHeight() - 185, 320, 250));

            Path2D wave = new Path2D.Double();
            wave.moveTo(0, getHeight() * 0.68);
            wave.curveTo(getWidth() * 0.27, getHeight() * 0.58,
                    getWidth() * 0.61, getHeight() * 0.84,
                    getWidth(), getHeight() * 0.64);
            wave.lineTo(getWidth(), getHeight());
            wave.lineTo(0, getHeight());
            wave.closePath();
            g2.setColor(new Color(255, 255, 255, 20));
            g2.fill(wave);

            g2.setColor(new Color(233, 227, 255, 100));
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 5; col++) {
                    g2.fill(new Ellipse2D.Double(getWidth() - 70 + col * 10,
                            24 + row * 10, 3, 3));
                }
            }

            paintCampusSilhouette(g2);
            g2.dispose();
        }

        private void paintCampusSilhouette(Graphics2D g2) {
            int baseY = getHeight() - 22;
            int buildingWidth = Math.min(getWidth() - 56, 300);
            int left = (getWidth() - buildingWidth) / 2;
            int height = 58;

            g2.setColor(new Color(34, 35, 128, 70));
            g2.fillRect(left, baseY - height, buildingWidth, height);
            g2.fillRect(left + 18, baseY - height - 11, buildingWidth - 36, 11);
            g2.fillRect(left + buildingWidth / 2 - 22, baseY - height - 25, 44, 25);

            g2.setColor(new Color(236, 231, 255, 80));
            for (int x = left + 14; x < left + buildingWidth - 8; x += 25) {
                g2.fillRect(x, baseY - height + 13, 10, 11);
                g2.fillRect(x, baseY - height + 34, 10, 11);
            }
            g2.fillRect(left + buildingWidth / 2 - 8, baseY - 28, 16, 28);
            g2.setColor(new Color(236, 231, 255, 90));
            g2.fillRect(left - 18, baseY, buildingWidth + 36, 2);
        }
=======
    }

    public void resetFields() {
        roleComboBox.setSelectedIndex(0);
        usernameField.setText("");
        passwordField.setText("");
        clearErrorMessage();
        roleComboBox.requestFocus();
    }

    // Encapsulation: Getters and Setters for GUI components
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
>>>>>>> test
    }
}
