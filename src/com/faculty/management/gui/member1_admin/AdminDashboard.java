package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.LoginController;
<<<<<<< HEAD
import com.faculty.management.gui.common.UITheme;
=======
import com.faculty.management.gui.common.DangerButton;
>>>>>>> test
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
<<<<<<< HEAD
 * Modern Admin Dashboard GUI.
=======
 * Admin Dashboard GUI.
>>>>>>> test
 * Target redirect frame for Administrator role.
 */
public class AdminDashboard extends JFrame {

    private final User currentUser;

<<<<<<< HEAD
=======
    private JTabbedPane tabbedPane;
    private UserManagementPanel userManagementPanel;
    private CourseManagementPanel courseManagementPanel;

>>>>>>> test
    public AdminDashboard(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Faculty Management System — Administrator Portal");
<<<<<<< HEAD
        setSize(1020, 680);
        setMinimumSize(new Dimension(880, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_PAGE);

        // Standardized Modern Header
        add(UITheme.buildHeader(this, "Administrator Portal", currentUser.getFullName(), "Admin", () -> {
            dispose();
            LoginController.showLogin();
        }), BorderLayout.NORTH);

        // Main Scrollable / Padded Content
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(24, 28, 24, 28));

        // 1. Welcome Hero Banner
        UITheme.ModernCard bannerCard = new UITheme.ModernCard(14);
        bannerCard.setLayout(new BorderLayout(16, 10));
        bannerCard.setCardBackground(Color.WHITE);
        bannerCard.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel bannerText = new JPanel();
        bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        bannerText.setOpaque(false);

        JLabel welcomeLabel = new JLabel(currentUser.getDashboardGreeting());
        welcomeLabel.setFont(UITheme.FONT_PAGE_TITLE);
        welcomeLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel roleDescLabel = new JLabel("You have full administrative privileges: User Profiles, Course Curriculums, Notices, and Timetables.");
        roleDescLabel.setFont(UITheme.FONT_BODY);
        roleDescLabel.setForeground(UITheme.TEXT_MUTED);
        roleDescLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        bannerText.add(welcomeLabel);
        bannerText.add(roleDescLabel);

        JPanel badgeBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badgeBox.setOpaque(false);
        UITheme.PillBadge activeBadge = new UITheme.PillBadge("SYSTEM ACTIVE", UITheme.SUCCESS_LIGHT, UITheme.SUCCESS_DARK, UITheme.SUCCESS_BORDER);
        UITheme.PillBadge roleBadge = new UITheme.PillBadge("ADMINISTRATOR", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, UITheme.PRIMARY_TINT);
        badgeBox.add(activeBadge);
        badgeBox.add(roleBadge);

        bannerCard.add(bannerText, BorderLayout.WEST);
        bannerCard.add(badgeBox, BorderLayout.EAST);

        // 2. Quick KPI Stat Cards
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(18, 0, 18, 0));

        statsGrid.add(UITheme.createStatCard("", "Total Users", "15 Accounts", UITheme.PRIMARY));
        statsGrid.add(UITheme.createStatCard("", "Active Courses", "10 Modules", UITheme.SUCCESS));
        statsGrid.add(UITheme.createStatCard("", "Timetable Entries", "Active Schedule", UITheme.ACCENT_BLUE));
        statsGrid.add(UITheme.createStatCard("", "Notices & Alerts", "4 Published", UITheme.WARNING_DARK));

        // 3. Modules Header
        JLabel sectionTitle = new JLabel("Core Administrative Modules");
        sectionTitle.setFont(UITheme.FONT_SECTION_TITLE);
        sectionTitle.setForeground(UITheme.TEXT_MAIN);
        sectionTitle.setBorder(new EmptyBorder(4, 4, 12, 4));

        // 4. Modules Grid (2x2)
        JPanel modulesGrid = new JPanel(new GridLayout(2, 2, 18, 18));
        modulesGrid.setOpaque(false);

        modulesGrid.add(createModuleCard("User Management",
                "Create, edit, view and manage system users (Lecturers, Technical Officers, Undergraduates). Assign credentials and permissions.",
                UITheme.PRIMARY, "Manage Users"));

        modulesGrid.add(createModuleCard("Course Management",
                "Add courses, assign credits, specify theory and practical session requirements, and organize academic module offerings.",
                UITheme.SUCCESS_DARK, "Configure Courses"));

        modulesGrid.add(createModuleCard("Notice Board",
                "Publish, edit, and broadcast official faculty notices, academic circulars, and departmental memos to staff and students.",
                UITheme.WARNING_DARK, "Publish Notices"));

        modulesGrid.add(createModuleCard("Timetable Management",
                "Create, adjust, and maintain departmental lecture schedules, laboratory sessions, and venue allocations.",
                UITheme.PURPLE, "Manage Timetables"));

        mainContent.add(bannerCard);
        mainContent.add(statsGrid);
        mainContent.add(sectionTitle);
        mainContent.add(modulesGrid);

        add(UITheme.createModernScrollPane(mainContent), BorderLayout.CENTER);
    }

    private JPanel createModuleCard(String title, String desc, Color accentColor, String actionText) {
        UITheme.ModernCard card = new UITheme.ModernCard(12);
        card.setLayout(new BorderLayout(12, 12));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_CARD_TITLE);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JLabel descLbl = new JLabel("<html><p style='width: 320px; color: #64748B;'>" + desc + "</p></html>");
        descLbl.setFont(UITheme.FONT_BODY);

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomRow.setOpaque(false);
        UITheme.ModernButton actionBtn = new UITheme.ModernButton(actionText, UITheme.ButtonStyle.OUTLINE, 4);
        actionBtn.setFont(UITheme.FONT_SMALL_BOLD);
        actionBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Admin module [" + title + "] is configured and ready.",
                    title,
                    JOptionPane.INFORMATION_MESSAGE);
        });
        bottomRow.add(actionBtn);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(descLbl, BorderLayout.CENTER);
        card.add(bottomRow, BorderLayout.SOUTH);
=======
        setSize(1080, 680);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 58, 138)); // Deep Navy Blue
        headerPanel.setBorder(new EmptyBorder(12, 25, 12, 25));

        JLabel titleLabel = new JLabel("Faculty Management System (FMS)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel(currentUser.getFullName() + " (Admin)");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(Color.WHITE);

        DangerButton logoutButton = new DangerButton("Logout");
        logoutButton.setPreferredSize(new Dimension(85, 30));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                LoginController.showLogin();
            }
        });

        userPanel.add(userLabel);
        userPanel.add(logoutButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        // Tabbed Navigation
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);

        // Tab 1: Overview Dashboard
        JPanel overviewPanel = createOverviewPanel();

        // Tab 2: User Profile Management
        userManagementPanel = new UserManagementPanel();

        // Tab 3: Course Management
        courseManagementPanel = new CourseManagementPanel();

        tabbedPane.addTab("  Overview  ", overviewPanel);
        tabbedPane.addTab("  User Profiles  ", userManagementPanel);
        tabbedPane.addTab("  Course Management  ", courseManagementPanel);

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createOverviewPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(248, 250, 252));
        centerPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel welcomeCard = new JPanel();
        welcomeCard.setLayout(new BoxLayout(welcomeCard, BoxLayout.Y_AXIS));
        welcomeCard.setBackground(Color.WHITE);
        welcomeCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel welcomeLabel = new JLabel(currentUser.getDashboardGreeting());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(15, 23, 42));

        JLabel roleDescLabel = new JLabel("You have full administrative privileges: User Profiles, Courses, Notices, and Timetables.");
        roleDescLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleDescLabel.setForeground(new Color(100, 116, 139));
        roleDescLabel.setBorder(new EmptyBorder(10, 0, 20, 0));

        // Quick Stats / Modules Cards
        JPanel modulesGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        modulesGrid.setOpaque(false);

        JPanel userCard = createModuleCard("👥 User Management", "Create, update, and maintain credentials for system users (Lecturers, TOs, Students). Click to manage.", new Color(37, 99, 235));
        userCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userCard.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                tabbedPane.setSelectedIndex(1); // Switch to User Profiles tab
            }
        });

        JPanel courseCard = createModuleCard("📚 Course Management", "Add, update, delete, view courses, and assign course details. Click to manage.", new Color(16, 185, 129));
        courseCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        courseCard.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                tabbedPane.setSelectedIndex(2); // Switch to Course Management tab
            }
        });

        modulesGrid.add(userCard);
        modulesGrid.add(courseCard);
        modulesGrid.add(createModuleCard("📢 Notice Board", "Publish and maintain official faculty notices and circulars.", new Color(245, 158, 11)));
        modulesGrid.add(createModuleCard("🗓️ Timetable Management", "Create and maintain lecture and lab timetables.", new Color(139, 92, 246)));

        welcomeCard.add(welcomeLabel);
        welcomeCard.add(roleDescLabel);
        welcomeCard.add(modulesGrid);

        centerPanel.add(welcomeCard, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createModuleCard(String title, String desc, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(241, 245, 249));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(new Color(30, 41, 59));

        JLabel descLbl = new JLabel("<html>" + desc + "</html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLbl.setForeground(new Color(100, 116, 139));
        descLbl.setBorder(new EmptyBorder(5, 0, 0, 0));

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(descLbl, BorderLayout.CENTER);
>>>>>>> test

        return card;
    }
}
