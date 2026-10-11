package com.faculty.management.gui.common;

import com.faculty.management.controller.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Modern Login GUI for Faculty Management System.
 * Pure Java Swing / Java2D — Elevated UI design with Quick Demo login.
 */
public class LoginFrame extends JFrame {

    private UITheme.ModernTextField usernameField;
    private UITheme.ModernPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JPanel errorBanner;
    private JLabel errorLabel;
    private UITheme.ModernButton loginButton;
    private UITheme.ModernButton resetButton;

    private final LoginController loginController;

    public LoginFrame(LoginController controller) {
        this.loginController = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Faculty Management System — Portal Authentication");
        setSize(880, 560);
        setMinimumSize(new Dimension(820, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainContainer = new JPanel(new GridLayout(1, 2));

        // ---------------------------------------------------------------------
        // 1. UNIVERSITY BRANDING AND STUDENT DEMO ACCESS
        // ---------------------------------------------------------------------
        JPanel brandPanel = new JPanel();
        brandPanel.setBackground(UITheme.PRIMARY_DARK);
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(5, 0, 0, 0, UITheme.PRIMARY),
                new EmptyBorder(35, 35, 35, 35)));

        JLabel logoLabel = new JLabel("FMS");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel facTitle = new JLabel("FACULTY OF TECHNOLOGY");
        facTitle.setFont(UITheme.FONT_SMALL_BOLD);
        facTitle.setForeground(new Color(199, 210, 254));
        facTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appTitleLabel = new JLabel("Faculty Management");
        appTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 23));
        appTitleLabel.setForeground(Color.WHITE);
        appTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appSubTitleLabel = new JLabel("System (FMS)");
        appSubTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appSubTitleLabel.setForeground(new Color(224, 231, 255));
        appSubTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel uniLabel = new JLabel("University of Ruhuna | Sri Lanka");
        uniLabel.setFont(UITheme.FONT_BODY);
        uniLabel.setForeground(new Color(199, 210, 254));
        uniLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Main system areas
        JPanel highlightsPanel = new JPanel();
        highlightsPanel.setLayout(new BoxLayout(highlightsPanel, BoxLayout.Y_AXIS));
        highlightsPanel.setOpaque(false);
        highlightsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        highlightsPanel.setBorder(new EmptyBorder(16, 10, 16, 10));

        addFeatureBullet(highlightsPanel, "Student records and academic progress");
        addFeatureBullet(highlightsPanel, "Results, eligibility and attendance");
        addFeatureBullet(highlightsPanel, "Courses, notices and timetables");

        // Demo Accounts Guide Box with 1-Click Fill Buttons
        JPanel demoBox = new JPanel();
        demoBox.setLayout(new BoxLayout(demoBox, BoxLayout.Y_AXIS));
        demoBox.setBackground(new Color(30, 27, 75));
        demoBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(99, 102, 241), 1),
                new EmptyBorder(12, 14, 12, 14)
        ));
        demoBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel demoTitle = new JLabel("STUDENT DEMO ACCOUNT");
        demoTitle.setFont(UITheme.FONT_SMALL_BOLD);
        demoTitle.setForeground(new Color(199, 210, 254));
        demoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel demoButtons = new JPanel(new GridLayout(1, 1, 6, 6));
        demoButtons.setOpaque(false);
        demoButtons.setBorder(new EmptyBorder(8, 0, 0, 0));

        demoButtons.add(createDemoFillButton("Student", "tg2021001", "std123"));

        demoBox.add(demoTitle);
        demoBox.add(demoButtons);

        brandPanel.add(Box.createVerticalGlue());
        brandPanel.add(logoLabel);
        brandPanel.add(Box.createVerticalStrut(8));
        brandPanel.add(facTitle);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(appTitleLabel);
        brandPanel.add(appSubTitleLabel);
        brandPanel.add(Box.createVerticalStrut(6));
        brandPanel.add(uniLabel);
        brandPanel.add(Box.createVerticalStrut(10));
        brandPanel.add(highlightsPanel);
        brandPanel.add(demoBox);
        brandPanel.add(Box.createVerticalGlue());

        // ---------------------------------------------------------------------
        // 2. RIGHT LOGIN FORM PANEL
        // ---------------------------------------------------------------------
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(40, 45, 40, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.weightx = 1.0;

        JLabel signInLabel = new JLabel("Welcome Back");
        signInLabel.setFont(UITheme.FONT_PAGE_TITLE);
        signInLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel signInSub = new JLabel("Enter your credentials to access your dashboard");
        signInSub.setFont(UITheme.FONT_BODY);
        signInSub.setForeground(UITheme.TEXT_MUTED);

        // Error message banner
        errorBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        errorBanner.setBackground(UITheme.DANGER_LIGHT);
        errorBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.DANGER_BORDER, 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        errorBanner.setVisible(false);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.FONT_SMALL_BOLD);
        errorLabel.setForeground(UITheme.DANGER_DARK);
        errorBanner.add(errorLabel);

        // Username
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(UITheme.FONT_BODY_BOLD);
        usernameLabel.setForeground(UITheme.TEXT_BODY);

        usernameField = new UITheme.ModernTextField("Enter your username", 20);
        usernameField.setPreferredSize(new Dimension(240, 40));

        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(UITheme.FONT_BODY_BOLD);
        passwordLabel.setForeground(UITheme.TEXT_BODY);

        passwordField = new UITheme.ModernPasswordField("Enter your password", 20);
        passwordField.setPreferredSize(new Dimension(240, 40));

        // Show Password Checkbox
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(UITheme.FONT_SMALL);
        showPasswordCheckBox.setForeground(UITheme.TEXT_MUTED);
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
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setOpaque(false);

        loginButton = new UITheme.ModernButton("Sign in", UITheme.ButtonStyle.PRIMARY);
        loginButton.setPreferredSize(new Dimension(120, 42));
        loginButton.addActionListener(e -> performLogin());

        resetButton = new UITheme.ModernButton("Clear", UITheme.ButtonStyle.SECONDARY);
        resetButton.setPreferredSize(new Dimension(100, 42));
        resetButton.addActionListener(e -> resetFields());

        buttonPanel.add(loginButton);
        buttonPanel.add(resetButton);

        // Security / System info
        JLabel footerNote = new JLabel("University account access");
        footerNote.setFont(UITheme.FONT_SMALL);
        footerNote.setForeground(UITheme.TEXT_LIGHT);
        footerNote.setHorizontalAlignment(SwingConstants.CENTER);

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
        int gridY = 0;
        gbc.gridx = 0;

        gbc.gridy = gridY++;
        formPanel.add(signInLabel, gbc);

        gbc.gridy = gridY++;
        formPanel.add(signInSub, gbc);

        gbc.gridy = gridY++;
        gbc.insets = new Insets(8, 0, 8, 0);
        formPanel.add(errorBanner, gbc);

        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridy = gridY++;
        formPanel.add(usernameLabel, gbc);

        gbc.gridy = gridY++;
        formPanel.add(usernameField, gbc);

        gbc.gridy = gridY++;
        formPanel.add(passwordLabel, gbc);

        gbc.gridy = gridY++;
        formPanel.add(passwordField, gbc);

        gbc.gridy = gridY++;
        formPanel.add(showPasswordCheckBox, gbc);

        gbc.gridy = gridY++;
        gbc.insets = new Insets(18, 0, 10, 0);
        formPanel.add(buttonPanel, gbc);

        gbc.gridy = gridY++;
        gbc.insets = new Insets(15, 0, 0, 0);
        formPanel.add(footerNote, gbc);

        mainContainer.add(brandPanel);
        mainContainer.add(formPanel);

        add(mainContainer, BorderLayout.CENTER);
    }

    private void addFeatureBullet(JPanel parent, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(new Color(224, 231, 255));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(2, 0, 2, 0));
        parent.add(lbl);
    }

    private JButton createDemoFillButton(String label, String user, String pass) {
        JButton btn = new JButton(label);
        btn.setFont(UITheme.FONT_SMALL_BOLD);
        btn.setBackground(UITheme.PRIMARY_DARK);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(99, 102, 241), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        btn.addActionListener(e -> {
            usernameField.setText(user);
            passwordField.setText(pass);
            clearErrorMessage();
            usernameField.requestFocus();
        });
        return btn;
    }

    private void performLogin() {
        clearErrorMessage();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        loginController.handleLogin(this, username, password);
    }

    public void showErrorMessage(String message) {
        errorLabel.setText(message);
        errorBanner.setVisible(true);
        errorBanner.revalidate();
        errorBanner.repaint();
    }

    public void clearErrorMessage() {
        errorLabel.setText(" ");
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
}
