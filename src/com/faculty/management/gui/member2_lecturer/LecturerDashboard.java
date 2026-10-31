package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LoginController;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.gui.member3_attendance.MedicalManagementDialog;
import com.faculty.management.gui.member3_attendance.ViewAttendanceDialog;
import com.faculty.management.model.Lecturer;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern Lecturer Dashboard GUI (Member 2 — Lecturer & Academic Management).
 * Supports:
 * 1. Lecturer Profile Management (locked credentials, editable contact & profile pic)
 * 2. Course Materials (Assigned courses, Add, Modify, View materials)
 * 3. Undergraduate Details (View information, Search students, View student details)
 * 4. Marks Management (Upload Quiz, Assessment, Mid-term, Practical, Theory, Final marks; all out of 100)
 * 5. CA Eligibility (CA >= 40% calculation, Eligible / Not Eligible for Individual & Whole-batch)
 * 6. Marks Summary (Screens for Individual marks, Whole-batch marks, Marks by subject, CA summary, Final marks)
 */
public class LecturerDashboard extends JFrame {

    private final User currentUser;
    private Lecturer currentLecturer;
    private JTabbedPane tabbedPane;
    private UITheme.SidebarNavigation sidebarNav;
    private UITheme.HeaderPanel headerPanel;

    private static final String[] NAV_TITLES = {
            "Home Overview",
            "My Profile",
            "Course Materials",
            "Undergraduate Details",
            "Marks Management",
            "CA Eligibility",
            "Attendance & Medical"
    };

    public LecturerDashboard(User user) {
        this.currentUser = user;

        // Safely map User to Lecturer model
        if (user instanceof Lecturer) {
            this.currentLecturer = (Lecturer) user;
        } else {
            this.currentLecturer = new Lecturer();
            this.currentLecturer.setUserId(user.getUserId());
            this.currentLecturer.setUsername(user.getUsername());
            this.currentLecturer.setFirstName(user.getFirstName());
            this.currentLecturer.setLastName(user.getLastName());
            this.currentLecturer.setEmail(user.getEmail());
            this.currentLecturer.setContactNo(user.getContactNo());
            this.currentLecturer.setProfilePic(user.getProfilePic());
            this.currentLecturer.setStatus(user.getStatus());
        }

        initComponents();
    }

    private void initComponents() {
        setTitle("Faculty Management System — Lecturer Portal");
        setSize(1220, 780);
        setMinimumSize(new Dimension(1020, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_PAGE);

        // 1. Left Sidebar Navigation
        sidebarNav = new UITheme.SidebarNavigation("Lecturer Portal", NAV_TITLES, 0, idx -> {
            if (tabbedPane != null && tabbedPane.getSelectedIndex() != idx) {
                tabbedPane.setSelectedIndex(idx);
            }
            if (headerPanel != null) {
                headerPanel.setTitle(NAV_TITLES[idx]);
            }
        });

        // 2. Right / Center Container
        JPanel mainContentContainer = new JPanel(new BorderLayout());
        mainContentContainer.setOpaque(false);

        // Standardized Header
        headerPanel = new UITheme.HeaderPanel(this, NAV_TITLES[0], currentUser.getFullName(), "Lecturer", () -> {
            dispose();
            LoginController.showLogin();
        });

        // --- Tabbed Content Area ---
        tabbedPane = new JTabbedPane();
        UITheme.styleTabbedPane(tabbedPane);

        // Tab 0: Welcome Overview
        tabbedPane.addTab("Home Overview", createHomeOverviewPanel());

        // Tab 1: Profile Management (Requirement 1)
        tabbedPane.addTab("My Profile", new LecturerProfilePanel(currentLecturer));

        // Tab 2: Course Materials (Requirement 2)
        tabbedPane.addTab("Course Materials", new CourseMaterialsPanel(currentLecturer.getUserId()));

        // Tab 3: Undergraduate Details (Requirement 3)
        tabbedPane.addTab("Undergraduate Details", new UndergraduateDetailsPanel());

        // Tab 4: Marks Management & Summary (Requirements 4 & 6)
        tabbedPane.addTab("Marks Management", new MarkManagementPanel());

        // Tab 5: CA Summary & Eligibility (Requirement 5)
        tabbedPane.addTab("CA Eligibility", new CAEligibilityWorkspacePanel());

        // Tab 6: Attendance & Medical Records (Requirement 7)
        tabbedPane.addTab("Attendance & Medical", createAttendanceMedicalPanel());

        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < NAV_TITLES.length) {
                sidebarNav.setSelectedIndex(selectedIndex);
                headerPanel.setTitle(NAV_TITLES[selectedIndex]);
            }
        });

        mainContentContainer.add(headerPanel, BorderLayout.NORTH);
        mainContentContainer.add(tabbedPane, BorderLayout.CENTER);

        add(sidebarNav, BorderLayout.WEST);
        add(mainContentContainer, BorderLayout.CENTER);
    }

    private JComponent createHomeOverviewPanel() {
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

        JLabel roleDescLabel = new JLabel("Academic Access: Course Materials, Undergraduate Tracking, Mark Upload, and CA Eligibility Analysis.");
        roleDescLabel.setFont(UITheme.FONT_BODY);
        roleDescLabel.setForeground(UITheme.TEXT_MUTED);
        roleDescLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        bannerText.add(welcomeLabel);
        bannerText.add(roleDescLabel);

        JPanel badgeBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badgeBox.setOpaque(false);
        UITheme.PillBadge staffBadge = new UITheme.PillBadge("ACADEMIC STAFF", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, UITheme.PRIMARY_TINT);
        UITheme.PillBadge semBadge = new UITheme.PillBadge("SEMESTER 1 & 2", UITheme.ACCENT_BLUE_LIGHT, UITheme.ACCENT_BLUE, new Color(191, 219, 254));
        badgeBox.add(staffBadge);
        badgeBox.add(semBadge);

        bannerCard.add(bannerText, BorderLayout.WEST);
        bannerCard.add(badgeBox, BorderLayout.EAST);

        // 2. Stat Cards
        JPanel statsGrid = new JPanel(new GridLayout(1, 5, 14, 0));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(18, 0, 18, 0));

        statsGrid.add(UITheme.createStatCard("", "Assigned Courses", "Assigned Modules", UITheme.TEAL));
        statsGrid.add(UITheme.createStatCard("", "Marks Management", "Quizzes & Finals", UITheme.ACCENT_BLUE));
        statsGrid.add(UITheme.createStatCard("", "Eligibility Status", "CA >= 40% Rule", UITheme.PURPLE));
        statsGrid.add(UITheme.createStatCard("", "Undergraduates", "Enrolled Students", UITheme.WARNING_DARK));
        statsGrid.add(UITheme.createStatCard("", "Attendance", "& Medical Records", UITheme.TEAL));

        // 3. Section Title
        JLabel sectionTitle = new JLabel("Academic & Teaching Operations");
        sectionTitle.setFont(UITheme.FONT_SECTION_TITLE);
        sectionTitle.setForeground(UITheme.TEXT_MAIN);
        sectionTitle.setBorder(new EmptyBorder(4, 4, 12, 4));

        // 4. Modules: Row 1 (3 cards), Row 2 (2 cards centred)
        JPanel modulesGrid = new JPanel();
        modulesGrid.setLayout(new BoxLayout(modulesGrid, BoxLayout.Y_AXIS));
        modulesGrid.setOpaque(false);

        // Row 1 — 3 cards
        JPanel row1 = new JPanel(new GridLayout(1, 3, 18, 0));
        row1.setOpaque(false);
        row1.add(createModuleCard("Course Materials",
                "View assigned courses, upload lecture slides, modify documents, and download teaching syllabus files.",
                UITheme.TEAL, "Upload Materials", () -> tabbedPane.setSelectedIndex(2)));
        row1.add(createModuleCard("Undergraduate Information",
                "View registered undergraduate student records, search by registration number/name, and review enrolled courses.",
                UITheme.WARNING_DARK, "View Students", () -> tabbedPane.setSelectedIndex(3)));
        row1.add(createModuleCard("Assessment & Exam Marks",
                "Enter, edit, and summarize Continuous Assessment (CA) components, quizzes, midterms, and End-Semester final marks.",
                UITheme.ACCENT_BLUE, "Enter Marks", () -> tabbedPane.setSelectedIndex(4)));

        // Row 2 — 2 cards centred
        JPanel row2 = new JPanel(new GridLayout(1, 2, 18, 0));
        row2.setOpaque(false);
        row2.add(createModuleCard("CA Eligibility & Summary",
                "Verify final exam eligibility based on CA >= 40% for individual students and entire batches with detailed status breakdown.",
                UITheme.PURPLE, "Review Eligibility", () -> tabbedPane.setSelectedIndex(5)));
        row2.add(createModuleCard("Attendance & Medical Records",
                "View undergraduate attendance summaries and medical certificate submissions. Monitor 80% attendance eligibility thresholds.",
                new Color(20, 184, 166), "View Attendance", () -> tabbedPane.setSelectedIndex(6)));

        modulesGrid.add(row1);
        modulesGrid.add(Box.createVerticalStrut(18));
        modulesGrid.add(row2);


        mainContent.add(bannerCard);
        mainContent.add(statsGrid);
        mainContent.add(sectionTitle);
        mainContent.add(modulesGrid);

        return UITheme.createModernScrollPane(mainContent);
    }

    private JPanel createModuleCard(String title, String desc, Color accentColor, String actionText, Runnable onAction) {
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
            if (onAction != null) {
                onAction.run();
            }
        });
        bottomRow.add(actionBtn);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(descLbl, BorderLayout.CENTER);
        card.add(bottomRow, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Tab 6: Attendance & Medical Records panel.
     * Provides quick-launch buttons for the existing ViewAttendanceDialog and MedicalManagementDialog.
     */
    private JComponent createAttendanceMedicalPanel() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(28, 32, 28, 32));

        // Section header
        JLabel title = new JLabel("Attendance & Medical Records");
        title.setFont(UITheme.FONT_PAGE_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("View undergraduate attendance summaries and manage medical certificate records.");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setBorder(new EmptyBorder(4, 0, 24, 0));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Card grid
        JPanel cardGrid = new JPanel(new GridLayout(1, 2, 24, 0));
        cardGrid.setOpaque(false);
        cardGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        cardGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Attendance Card ---
        UITheme.ModernCard attendCard = new UITheme.ModernCard(12);
        attendCard.setLayout(new BorderLayout(12, 12));
        attendCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(20, 184, 166)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel attendTitle = new JLabel("Undergraduate Attendance");
        attendTitle.setFont(UITheme.FONT_CARD_TITLE);
        attendTitle.setForeground(UITheme.TEXT_MAIN);

        JLabel attendDesc = new JLabel("<html><p style='width:260px;color:#64748B;'>View batch and individual student attendance percentages across Theory and Practical sessions. Highlights students below the 80% eligibility threshold.</p></html>");
        attendDesc.setFont(UITheme.FONT_BODY);

        JPanel attendBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        attendBtn.setOpaque(false);
        UITheme.ModernButton btnAttend = new UITheme.ModernButton("View Attendance", UITheme.ButtonStyle.OUTLINE, 4);
        btnAttend.setFont(UITheme.FONT_SMALL_BOLD);
        btnAttend.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(wrapper);
            new ViewAttendanceDialog(topFrame).setVisible(true);
        });
        attendBtn.add(btnAttend);

        attendCard.add(attendTitle, BorderLayout.NORTH);
        attendCard.add(attendDesc, BorderLayout.CENTER);
        attendCard.add(attendBtn, BorderLayout.SOUTH);

        // --- Medical Records Card ---
        UITheme.ModernCard medCard = new UITheme.ModernCard(12);
        medCard.setLayout(new BorderLayout(12, 12));
        medCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(239, 68, 68)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel medTitle = new JLabel("Medical Certificate Records");
        medTitle.setFont(UITheme.FONT_CARD_TITLE);
        medTitle.setForeground(UITheme.TEXT_MAIN);

        JLabel medDesc = new JLabel("<html><p style='width:260px;color:#64748B;'>View, approve, or review student medical certificate submissions. Approved medicals count towards the effective attendance percentage.</p></html>");
        medDesc.setFont(UITheme.FONT_BODY);

        JPanel medBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        medBtn.setOpaque(false);
        UITheme.ModernButton btnMed = new UITheme.ModernButton("View Medical Records", UITheme.ButtonStyle.OUTLINE, 4);
        btnMed.setFont(UITheme.FONT_SMALL_BOLD);
        btnMed.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(wrapper);
            new MedicalManagementDialog(topFrame).setVisible(true);
        });
        medBtn.add(btnMed);

        medCard.add(medTitle, BorderLayout.NORTH);
        medCard.add(medDesc, BorderLayout.CENTER);
        medCard.add(medBtn, BorderLayout.SOUTH);

        cardGrid.add(attendCard);
        cardGrid.add(medCard);

        wrapper.add(title);
        wrapper.add(subtitle);
        wrapper.add(cardGrid);

        return UITheme.createModernScrollPane(wrapper);
    }
}