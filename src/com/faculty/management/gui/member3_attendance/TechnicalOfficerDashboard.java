package com.faculty.management.gui.member3_attendance;

import com.faculty.management.controller.LoginController;
<<<<<<< HEAD
import com.faculty.management.gui.common.UITheme;
=======
>>>>>>> Aysha
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
<<<<<<< HEAD
 * Modern Technical Officer Dashboard GUI.
 * Integrates all 6 technical officer modules inside a unified, tabbed interface:
 * 1. Attendance Entry
 * 2. Medical Certificates
 * 3. Batch Summary
 * 4. Dept Timetables
 * 5. Notice Board
 * 6. My Profile
=======
 * Technical Officer Dashboard GUI.
 * Target redirect frame for Technical Officer role.
>>>>>>> Aysha
 */
public class TechnicalOfficerDashboard extends JFrame {

    private final User currentUser;
<<<<<<< HEAD
    private JTabbedPane tabbedPane;
    private UITheme.SidebarNavigation sidebarNav;
    private UITheme.HeaderPanel headerPanel;

    private AttendanceEntryPanel attendanceEntryPanel;
    private MedicalManagementPanel medicalManagementPanel;
    private ViewAttendancePanel viewAttendancePanel;
    private DepartmentTimetablePanel departmentTimetablePanel;
    private NoticeViewerPanel noticeViewerPanel;
    private TechnicalOfficerProfilePanel profilePanel;

    private static final String[] TO_NAV_TITLES = {
            "Home Overview",
            "Attendance Entry",
            "Medical Certificates",
            "Batch Summary",
            "Dept Timetables",
            "Notice Board",
            "My Profile"
    };
=======
>>>>>>> Aysha

    public TechnicalOfficerDashboard(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Faculty Management System — Technical Officer Portal");
<<<<<<< HEAD
        setSize(1220, 780);
        setMinimumSize(new Dimension(1020, 660));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_PAGE);

        // 1. Left Sidebar Navigation (Per FMS UI PDF - Solid Burgundy #5C1221, Gold Active Pill)
        sidebarNav = new UITheme.SidebarNavigation("Tech Officer Portal", TO_NAV_TITLES, 0, idx -> {
            if (tabbedPane != null && tabbedPane.getSelectedIndex() != idx) {
                tabbedPane.setSelectedIndex(idx);
            }
            if (headerPanel != null) {
                headerPanel.setTitle(TO_NAV_TITLES[idx]);
            }
        });

        // 2. Right / Center Container
        JPanel mainContentContainer = new JPanel(new BorderLayout());
        mainContentContainer.setOpaque(false);

        // Standardized Modern Header
        headerPanel = new UITheme.HeaderPanel(this, TO_NAV_TITLES[0], currentUser.getFullName(), "Tech Officer", () -> {
            dispose();
            LoginController.showLogin();
        });

        // --- Tabbed Content Area ---
        tabbedPane = new JTabbedPane();
        UITheme.styleTabbedPane(tabbedPane);

        // Tab 0: Welcome Overview
        tabbedPane.addTab("Home Overview", createHomeOverviewPanel());

        // Tab 1: Attendance Entry & Daily Logs
        attendanceEntryPanel = new AttendanceEntryPanel(currentUser);
        tabbedPane.addTab("Attendance Entry", attendanceEntryPanel);

        // Tab 2: Medical Certificates & Verification
        medicalManagementPanel = new MedicalManagementPanel();
        tabbedPane.addTab("Medical Certificates", medicalManagementPanel);

        // Tab 3: Batch Summary & 80% Eligibility
        viewAttendancePanel = new ViewAttendancePanel();
        tabbedPane.addTab("Batch Summary", viewAttendancePanel);

        // Tab 4: Department Timetables & Laboratory Schedules
        departmentTimetablePanel = new DepartmentTimetablePanel();
        tabbedPane.addTab("Dept Timetables", departmentTimetablePanel);

        // Tab 5: Faculty Notice Board & Circulars
        noticeViewerPanel = new NoticeViewerPanel();
        tabbedPane.addTab("Notice Board", noticeViewerPanel);

        // Tab 6: Staff Profile & Account Settings
        profilePanel = new TechnicalOfficerProfilePanel(currentUser);
        tabbedPane.addTab("My Profile", profilePanel);

        // Synchronize tabbed pane selection with sidebar and header
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < TO_NAV_TITLES.length) {
                sidebarNav.setSelectedIndex(selectedIndex);
                headerPanel.setTitle(TO_NAV_TITLES[selectedIndex]);
            }
        });

        mainContentContainer.add(headerPanel, BorderLayout.NORTH);
        mainContentContainer.add(tabbedPane, BorderLayout.CENTER);

        add(sidebarNav, BorderLayout.WEST);
        add(mainContentContainer, BorderLayout.CENTER);
    }

    private JComponent createHomeOverviewPanel() {
        // Main Content Container
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

        JLabel roleDescLabel = new JLabel("Technical Officer Responsibilities: Daily Attendance, Medical Submissions, and Lab Schedules.");
        roleDescLabel.setFont(UITheme.FONT_BODY);
        roleDescLabel.setForeground(UITheme.TEXT_MUTED);
        roleDescLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        bannerText.add(welcomeLabel);
        bannerText.add(roleDescLabel);

        // Hero Badges & Quick Action Buttons
        JPanel badgeBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        badgeBox.setOpaque(false);

        UITheme.ModernButton noticesBtn = new UITheme.ModernButton("📢 Notices", UITheme.ButtonStyle.OUTLINE, 6);
        noticesBtn.setFont(UITheme.FONT_SMALL_BOLD);
        noticesBtn.addActionListener(e -> tabbedPane.setSelectedIndex(5));

        UITheme.ModernButton profileBtn = new UITheme.ModernButton("👤 My Profile", UITheme.ButtonStyle.SECONDARY, 6);
        profileBtn.setFont(UITheme.FONT_SMALL_BOLD);
        profileBtn.addActionListener(e -> tabbedPane.setSelectedIndex(6));

        UITheme.PillBadge toBadge = new UITheme.PillBadge("OPERATIONS STAFF", UITheme.WARNING_LIGHT, UITheme.WARNING_DARK, UITheme.WARNING_BORDER);
        UITheme.PillBadge labBadge = new UITheme.PillBadge("LABS & SESSIONS", UITheme.SUCCESS_LIGHT, UITheme.SUCCESS_DARK, UITheme.SUCCESS_BORDER);

        badgeBox.add(noticesBtn);
        badgeBox.add(profileBtn);
        badgeBox.add(toBadge);
        badgeBox.add(labBadge);

        bannerCard.add(bannerText, BorderLayout.WEST);
        bannerCard.add(badgeBox, BorderLayout.EAST);

        // 2. Stat Cards
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(18, 0, 18, 0));

        statsGrid.add(UITheme.createStatCard("🗓️", "Today's Sessions", "4 Active Labs", UITheme.WARNING_DARK));
        statsGrid.add(UITheme.createStatCard("🏥", "Medical Requests", "3 Pending Review", UITheme.DANGER));
        statsGrid.add(UITheme.createStatCard("📊", "Batch Attendance", "Overall 84.5%", UITheme.PRIMARY));
        statsGrid.add(UITheme.createStatCard("🔬", "Lab Venues", "5 Department Labs", UITheme.SUCCESS));

        // 3. Section Title
        JLabel sectionTitle = new JLabel("Technical & Attendance Operations");
        sectionTitle.setFont(UITheme.FONT_SECTION_TITLE);
        sectionTitle.setForeground(UITheme.TEXT_MAIN);
        sectionTitle.setBorder(new EmptyBorder(4, 4, 12, 4));

        // 4. Modules Grid (3x2)
        JPanel modulesGrid = new JPanel(new GridLayout(3, 2, 18, 18));
        modulesGrid.setOpaque(false);

        // Module 1: Attendance Entry
        modulesGrid.add(createModuleCard("Attendance Entry",
                "Record and update daily undergraduate attendance for theory lectures and laboratory practical sessions.",
                UITheme.WARNING_DARK, "Log Attendance", () -> tabbedPane.setSelectedIndex(1)));

        // Module 2: Medical Certificates
        modulesGrid.add(createModuleCard("Medical Certificates",
                "Log medical submissions, inspect doctor certs, and update verification/approval status for excused absences.",
                UITheme.DANGER, "Review Medicals", () -> tabbedPane.setSelectedIndex(2)));

        // Module 3: Batch Attendance Summary
        modulesGrid.add(createModuleCard("Batch Attendance Summary",
                "View and compute aggregate semester attendance percentages by course, batch, and academic specialization.",
                UITheme.PRIMARY, "View Summaries", () -> tabbedPane.setSelectedIndex(3)));

        // Module 4: Department Timetables
        modulesGrid.add(createModuleCard("Department Timetables",
                "View departmental lab time slots, equipment allocation, and lecture timetable schedules.",
                UITheme.SUCCESS_DARK, "Check Schedules", () -> tabbedPane.setSelectedIndex(4)));

        // Module 5: Faculty Notice Board
        modulesGrid.add(createModuleCard("Faculty Notice Board",
                "Browse official academic announcements, circulars, exam memos, and timetable notices.",
                UITheme.ACCENT_BLUE, "Browse Notices", () -> tabbedPane.setSelectedIndex(5)));

        // Module 6: Technical Officer Profile
        modulesGrid.add(createModuleCard("Staff Profile & Account",
                "Manage contact phone numbers, email address, and personal staff details.",
                UITheme.PRIMARY, "Edit Profile", () -> tabbedPane.setSelectedIndex(6)));

        mainContent.add(bannerCard);
        mainContent.add(statsGrid);
        mainContent.add(sectionTitle);
        mainContent.add(modulesGrid);

        return UITheme.createModernScrollPane(mainContent);
    }

    private JPanel createModuleCard(String title, String desc, Color accentColor, String actionText, Runnable onClick) {
        UITheme.ModernCard card = new UITheme.ModernCard(12);
        card.setLayout(new BorderLayout(12, 12));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(16, 18, 16, 18)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (onClick != null) onClick.run();
            }
        });

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
            if (onClick != null) onClick.run();
        });
        bottomRow.add(actionBtn);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(descLbl, BorderLayout.CENTER);
        card.add(bottomRow, BorderLayout.SOUTH);
=======
        setSize(950, 600);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(124, 45, 18)); // Rust Amber / Bronze
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("Faculty Management System — Technical Operations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("🛠️ " + currentUser.getFullName() + " (TO)");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setBackground(new Color(220, 38, 38));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(248, 250, 252));
        centerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

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

        JLabel roleDescLabel = new JLabel("Technical Officer Responsibilities: Daily Attendance, Medical Submissions, and Lab Schedules.");
        roleDescLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleDescLabel.setForeground(new Color(100, 116, 139));
        roleDescLabel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JPanel modulesGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        modulesGrid.setOpaque(false);

        modulesGrid.add(createModuleCard("📋 Attendance Entry", "Record and update undergraduate attendance for theory and practical sessions.", new Color(217, 119, 6)));
        modulesGrid.add(createModuleCard("🏥 Medical Certificates", "Log medical certificates and manage verification/approval status.", new Color(225, 29, 72)));
        modulesGrid.add(createModuleCard("📊 Batch Attendance Summary", "View semester attendance percentages by course and batch.", new Color(79, 70, 229)));
        modulesGrid.add(createModuleCard("🗓️ Department Timetables", "View departmental lab and lecture timetable schedules.", new Color(5, 150, 105)));

        welcomeCard.add(welcomeLabel);
        welcomeCard.add(roleDescLabel);
        welcomeCard.add(modulesGrid);

        centerPanel.add(welcomeCard, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
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
>>>>>>> Aysha

        return card;
    }
}
