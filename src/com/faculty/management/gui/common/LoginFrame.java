package com.faculty.management.gui.common;

import com.faculty.management.controller.LoginController;

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

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
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
        setSize(780, 520);
        setMinimumSize(new Dimension(720, 480));
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
        formPanel.setBorder(new EmptyBorder(30, 35, 30, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.weightx = 1.0;

        // Form Title
        JLabel signInLabel = new JLabel("Welcome Back");
        signInLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        signInLabel.setForeground(new Color(15, 23, 42));

        JLabel signInSub = new JLabel("Please enter your credentials to login");
        signInSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signInSub.setForeground(new Color(100, 116, 139));

        // Error message label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setForeground(new Color(220, 38, 38));

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

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);

        loginButton = new JButton("Sign In");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(37, 99, 235)); // Primary Blue
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(100, 38));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> performLogin());

        resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resetButton.setBackground(new Color(241, 245, 249));
        resetButton.setForeground(new Color(71, 85, 105));
        resetButton.setPreferredSize(new Dimension(100, 38));
        resetButton.setFocusPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        formPanel.add(usernameLabel, gbc);

        gbc.gridy = 4;
        formPanel.add(usernameField, gbc);

        gbc.gridy = 5;
        formPanel.add(passwordLabel, gbc);

        gbc.gridy = 6;
        formPanel.add(passwordField, gbc);

        gbc.gridy = 7;
        formPanel.add(showPasswordCheckBox, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(15, 0, 0, 0);
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(brandPanel);
        mainPanel.add(formPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void performLogin() {
        clearErrorMessage();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        loginController.handleLogin(this, username, password);
    }

    public void showErrorMessage(String message) {
        errorLabel.setText("⚠️ " + message);
    }

    public void clearErrorMessage() {
        errorLabel.setText(" ");
    }

    public void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
        clearErrorMessage();
        usernameField.requestFocus();
    }
}
