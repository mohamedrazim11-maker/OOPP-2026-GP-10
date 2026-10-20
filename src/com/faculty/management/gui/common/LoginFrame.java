package com.faculty.management.gui.common;

import com.faculty.management.controller.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

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

    private final LoginController loginController;

    public LoginFrame(LoginController controller) {
        this.loginController = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Faculty Management System — Portal Authentication");
        setUndecorated(true);
        setSize(1000, 650);
        setMinimumSize(new Dimension(900, 590));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(37, 8, 11));
        root.setBorder(BorderFactory.createLineBorder(new Color(202, 154, 74), 2));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(70, 11, 20));
        titleBar.setPreferredSize(new Dimension(0, 36));
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(202, 154, 74)));

        JPanel leftTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        leftTitle.setOpaque(false);
        JLabel icon = new JLabel("◫");
        icon.setFont(new Font("Segoe UI Symbol", Font.BOLD, 17));
        icon.setForeground(new Color(245, 219, 168));
        JLabel appTitle = new JLabel("Faculty Management System — Login");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        appTitle.setForeground(new Color(247, 235, 218));
        leftTitle.add(icon);
        leftTitle.add(appTitle);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 7));
        rightButtons.setOpaque(false);
        JButton minimize = new JButton("—");
        JButton close = new JButton("✕");
        styleWindowButton(minimize);
        styleWindowButton(close);
        minimize.setPreferredSize(new Dimension(24, 18));
        close.setPreferredSize(new Dimension(24, 18));
        minimize.addActionListener(e -> setState(JFrame.ICONIFIED));
        close.addActionListener(e -> System.exit(0));
        rightButtons.add(minimize);
        rightButtons.add(close);

        titleBar.add(leftTitle, BorderLayout.WEST);
        titleBar.add(rightButtons, BorderLayout.EAST);

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(new Color(243, 236, 228));
        page.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel shell = new JPanel(new GridLayout(1, 2));
        shell.setBackground(new Color(252, 247, 241));
        shell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 186, 140), 1),
                new EmptyBorder(4, 4, 4, 4)));

        shell.add(buildBrandPanel());
        shell.add(buildLoginPanel());
        page.add(shell, BorderLayout.CENTER);

        root.add(titleBar, BorderLayout.NORTH);
        root.add(page, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void styleWindowButton(JButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setForeground(new Color(242, 231, 210));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel buildBrandPanel() {
        JPanel brandPanel = new BrandPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBorder(new EmptyBorder(30, 38, 28, 38));

        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoRow.setOpaque(false);
        JLabel capMark = new JLabel("◈");
        capMark.setFont(new Font("Segoe UI Symbol", Font.BOLD, 28));
        capMark.setForeground(new Color(240, 205, 125));
        JLabel logoLabel = new JLabel("FMS");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        logoRow.add(capMark);
        logoRow.add(logoLabel);

        JLabel facultyLabel = new JLabel("FACULTY OF TECHNOLOGY");
        facultyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        facultyLabel.setForeground(new Color(244, 230, 194));
        facultyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("<html>Faculty Management<br>System (FMS)</html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(255, 248, 243));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel universityLabel = new JLabel("University of Ruhuna");
        universityLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        universityLabel.setForeground(new Color(255, 241, 221));
        universityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel facultySubLabel = new JLabel("Faculty of Technology");
        facultySubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        facultySubLabel.setForeground(new Color(247, 229, 199));
        facultySubLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel accentLine = new JPanel();
        accentLine.setBackground(new Color(215, 177, 94));
        accentLine.setMaximumSize(new Dimension(160, 4));
        accentLine.setPreferredSize(new Dimension(160, 4));
        accentLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel features = new JPanel(new GridLayout(1, 4, 8, 0));
        features.setOpaque(false);
        features.setMaximumSize(new Dimension(Integer.MAX_VALUE, 94));
        features.add(createFeature("S", "Student\nRecords"));
        features.add(createFeature("C", "Courses &\nTimetable"));
        features.add(createFeature("A", "Attendance\n& Grades"));
        features.add(createFeature("N", "Notices &\nUpdates"));

        JLabel quote = new JLabel("<html><i>Learn • Innovate • Build the Future</i></html>");
        quote.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        quote.setForeground(new Color(245, 230, 205));
        quote.setBorder(new EmptyBorder(18, 0, 0, 0));
        quote.setAlignmentX(Component.LEFT_ALIGNMENT);

        brandPanel.add(logoRow);
        brandPanel.add(Box.createVerticalStrut(18));
        brandPanel.add(facultyLabel);
        brandPanel.add(Box.createVerticalStrut(10));
        brandPanel.add(titleLabel);
        brandPanel.add(Box.createVerticalStrut(12));
        brandPanel.add(universityLabel);
        brandPanel.add(facultySubLabel);
        brandPanel.add(Box.createVerticalStrut(14));
        brandPanel.add(accentLine);
        brandPanel.add(Box.createVerticalStrut(20));
        brandPanel.add(features);
        brandPanel.add(Box.createVerticalGlue());
        brandPanel.add(quote);
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
        text.setForeground(new Color(246, 230, 197));
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
        formPanel.setBackground(UITheme.BG_CARD);
        formPanel.setBorder(new EmptyBorder(34, 42, 28, 42));
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setMaximumSize(new Dimension(440, 520));

        JLabel signInLabel = new JLabel("Welcome Back");
        signInLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        signInLabel.setForeground(new Color(79, 17, 24));
        signInLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel signInSub = new JLabel("Please sign in to access your faculty portal");
        signInSub.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        signInSub.setForeground(new Color(94, 70, 62));
        signInSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel subtitleLine = new JPanel();
        subtitleLine.setBackground(new Color(185, 142, 66));
        subtitleLine.setPreferredSize(new Dimension(120, 3));
        subtitleLine.setMaximumSize(new Dimension(120, 3));
        subtitleLine.setAlignmentX(Component.LEFT_ALIGNMENT);

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

        JLabel roleLabel = new JLabel("Select Role");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        roleLabel.setForeground(new Color(77, 36, 40));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"-- Select Role --"});
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        roleCombo.setForeground(new Color(89, 72, 64));
        roleCombo.setBackground(new Color(247, 240, 232));
        roleCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(188, 154, 121), 1),
                new EmptyBorder(6, 10, 6, 10)));
        roleCombo.setPreferredSize(new Dimension(240, 48));
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        usernameField = new UITheme.ModernTextField("Enter your username", 20);
        usernameField.setPreferredSize(new Dimension(240, 46));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        passwordField = new UITheme.ModernPasswordField("Enter your password", 20);
        passwordField.setPreferredSize(new Dimension(240, 46));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel showPasswordRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        showPasswordRow.setOpaque(false);
        showPasswordRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        showPasswordCheckBox.setForeground(new Color(79, 52, 43));
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.setFocusPainted(false);
        showPasswordCheckBox.addActionListener(e ->
                passwordField.setEchoChar(showPasswordCheckBox.isSelected() ? (char) 0 : '•'));
        showPasswordRow.add(showPasswordCheckBox);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginButton = new UITheme.ModernButton("Sign In", UITheme.ButtonStyle.PRIMARY, 10);
        loginButton.setPreferredSize(new Dimension(150, 48));
        loginButton.addActionListener(e -> performLogin());

        resetButton = new UITheme.ModernButton("Reset", UITheme.ButtonStyle.SECONDARY, 10);
        resetButton.setPreferredSize(new Dimension(120, 48));
        resetButton.addActionListener(e -> resetFields());
        buttonPanel.add(loginButton);
        buttonPanel.add(resetButton);

        JLabel footerNote = new JLabel("Secure access for authorized users");
        footerNote.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        footerNote.setForeground(new Color(85, 63, 56));
        footerNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerNote.setIcon(new ImageIcon(createLockIcon()));

        form.add(signInLabel);
        form.add(Box.createVerticalStrut(8));
        form.add(signInSub);
        form.add(Box.createVerticalStrut(10));
        form.add(subtitleLine);
        form.add(Box.createVerticalStrut(22));
        form.add(errorBanner);
        form.add(Box.createVerticalStrut(12));
        form.add(roleLabel);
        form.add(Box.createVerticalStrut(8));
        form.add(roleCombo);
        form.add(Box.createVerticalStrut(16));
        form.add(createInputLabel("Username"));
        form.add(Box.createVerticalStrut(8));
        form.add(usernameField);
        form.add(Box.createVerticalStrut(16));
        form.add(createInputLabel("Password"));
        form.add(Box.createVerticalStrut(8));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(8));
        form.add(showPasswordRow);
        form.add(Box.createVerticalStrut(18));
        form.add(buttonPanel);
        form.add(Box.createVerticalStrut(28));
        form.add(footerNote);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        formPanel.add(form, constraints);

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
        return formPanel;
    }

    private JLabel createInputLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(78, 34, 38));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private BufferedImage createLockIcon() {
        BufferedImage img = new BufferedImage(18, 18, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1.8f));
        g2.setColor(new Color(98, 63, 47));
        g2.drawRoundRect(5, 9, 8, 7, 2, 2);
        g2.drawArc(4, 4, 10, 7, 180, 180);
        g2.dispose();
        return img;
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

    private static class BrandPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, new Color(80, 12, 26),
                    getWidth(), getHeight(), new Color(120, 21, 39)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(255, 255, 255, 18));
            g2.fill(new Ellipse2D.Double(getWidth() - 160, -110, 260, 260));
            g2.fill(new Ellipse2D.Double(-125, getHeight() - 185, 320, 250));

            Path2D wave = new Path2D.Double();
            wave.moveTo(0, getHeight() * 0.68);
            wave.curveTo(getWidth() * 0.24, getHeight() * 0.58,
                    getWidth() * 0.62, getHeight() * 0.82,
                    getWidth(), getHeight() * 0.64);
            wave.lineTo(getWidth(), getHeight());
            wave.lineTo(0, getHeight());
            wave.closePath();
            g2.setColor(new Color(245, 214, 168, 40));
            g2.fill(wave);

            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(new Color(211, 168, 87));
            g2.draw(new RoundRectangle2D.Double(18, 18, getWidth() - 36, getHeight() - 36, 26, 26));

            g2.setColor(new Color(245, 228, 197, 90));
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
    }
}
