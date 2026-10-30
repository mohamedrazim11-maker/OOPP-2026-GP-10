package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.LoginController;
import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modern Undergraduate Dashboard GUI — Member 4 (Undergraduate, Timetable & Results).
 * High-performance tabbed self-service student portal connecting Profiles,
 * Enrolled Courses, Class Timetables, Attendance Monitoring, Medical Excuses,
 * Faculty Notice Board, UGC Letter Grades, Transcripts, SGPA & CGPA.
 */
public class UndergraduateDashboard extends JFrame {

    private final User currentUser;
    private final UndergraduateController controller = new UndergraduateController();
    private JTabbedPane tabs;
    private UITheme.SidebarNavigation sidebarNav;
    private UITheme.HeaderPanel headerPanel;

    private static final String[] STUDENT_TABS = {
            "Profile",
            "Courses",
            "Timetable",
            "Attendance",
            "Medical",
            "Notices",
            "Grades",
            "Results",
            "SGPA",
            "CGPA"
    };

    public UndergraduateDashboard(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Faculty Management System — Student Portal (" + currentUser.getUsername() + ")");
        setSize(1240, 800);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_PAGE);

        // 1. Left Sidebar Navigation (Per FMS UI PDF - Solid Burgundy #5C1221, Gold Active Pill)
        sidebarNav = new UITheme.SidebarNavigation("Student Portal", STUDENT_TABS, 0, idx -> {
            if (tabs != null && tabs.getSelectedIndex() != idx) {
                tabs.setSelectedIndex(idx);
            }
            if (headerPanel != null) {
                headerPanel.setTitle(STUDENT_TABS[idx]);
            }
        });

        // 2. Right / Center Container
        JPanel mainContentContainer = new JPanel(new BorderLayout());
        mainContentContainer.setOpaque(false);

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // Unified Brand Header
        headerPanel = new UITheme.HeaderPanel(this, STUDENT_TABS[0],
                currentUser.getFullName() + " (" + currentUser.getUsername() + ")", "Student", () -> {
            dispose();
            LoginController.showLogin();
        });
        topContainer.add(headerPanel);

        // Student Identity & Quick Status Banner
        JPanel subBanner = new JPanel(new BorderLayout());
        subBanner.setBackground(Color.WHITE);
        subBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                        new EmptyBorder(10, 16, 10, 16)),
                new EmptyBorder(6, 20, 6, 20)
        ));

        // Left: Avatar + Greeting Stack
        JPanel leftIdentityBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftIdentityBox.setOpaque(false);

        UITheme.CircularAvatar avatarThumb = new UITheme.CircularAvatar(34, UITheme.PRIMARY);
        avatarThumb.setRingStroke(2.0f);
        if (currentUser.getProfilePic() != null && !currentUser.getProfilePic().isBlank()) {
            avatarThumb.setImagePath(currentUser.getProfilePic());
        }
        avatarThumb.setPlaceholder("S");

        JPanel greetStack = new JPanel();
        greetStack.setLayout(new BoxLayout(greetStack, BoxLayout.Y_AXIS));
        greetStack.setOpaque(false);

        JLabel welcomeGreeting = new JLabel(currentUser.getDashboardGreeting());
        welcomeGreeting.setFont(UITheme.FONT_BODY_BOLD);
        welcomeGreeting.setForeground(UITheme.TEXT_MAIN);

        JLabel dateInfo = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
                + " | Student portal");
        dateInfo.setFont(UITheme.FONT_SMALL);
        dateInfo.setForeground(UITheme.TEXT_MUTED);

        greetStack.add(welcomeGreeting);
        greetStack.add(Box.createVerticalStrut(1));
        greetStack.add(dateInfo);

        leftIdentityBox.add(avatarThumb);
        leftIdentityBox.add(greetStack);

        // Right: Status Badges
        JPanel pillsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pillsPanel.setOpaque(false);

        UITheme.PillBadge idBadge = new UITheme.PillBadge("REG: " + currentUser.getUsername(), UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, UITheme.PRIMARY_TINT);
        UITheme.PillBadge statusBadge = new UITheme.PillBadge(currentUser.getStatus() != null ? currentUser.getStatus() : "ACTIVE", UITheme.SUCCESS_LIGHT, UITheme.SUCCESS_DARK, UITheme.SUCCESS_BORDER);

        pillsPanel.add(idBadge);
        pillsPanel.add(statusBadge);

        subBanner.add(leftIdentityBox, BorderLayout.WEST);
        subBanner.add(pillsPanel, BorderLayout.EAST);

        JPanel bannerMargin = new JPanel(new BorderLayout());
        bannerMargin.setBackground(UITheme.BG_PAGE);
        bannerMargin.setBorder(new EmptyBorder(10, 20, 6, 20));
        bannerMargin.add(subBanner, BorderLayout.CENTER);
        topContainer.add(bannerMargin);

        tabs = buildTabs();
        tabs.addChangeListener(e -> {
            int selectedIndex = tabs.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < STUDENT_TABS.length) {
                sidebarNav.setSelectedIndex(selectedIndex);
                headerPanel.setTitle(STUDENT_TABS[selectedIndex]);
            }
        });

        mainContentContainer.add(topContainer, BorderLayout.NORTH);
        mainContentContainer.add(tabs, BorderLayout.CENTER);

        add(sidebarNav, BorderLayout.WEST);
        add(mainContentContainer, BorderLayout.CENTER);
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        UITheme.styleTabbedPane(tabs);

        int studentId = currentUser.getUserId();

        tabs.addTab("Profile", new StudentProfilePanel(controller, studentId));
        tabs.addTab("Courses", new StudentCoursePanel(controller, studentId));
        tabs.addTab("Timetable", new TimetablePanel(controller, studentId));
        tabs.addTab("Attendance", new AttendancePanel(controller, studentId));
        tabs.addTab("Medical", new MedicalPanel(controller, studentId));
        tabs.addTab("Notices", new StudentNoticePanel(controller, studentId));
        tabs.addTab("Grades", new GradePanel(controller, studentId));
        tabs.addTab("Results", new ResultPanel(controller, studentId));
        tabs.addTab("SGPA", new SGPA_Panel(controller, studentId));
        tabs.addTab("CGPA", new CGPA_Panel(controller, studentId));

        return tabs;
    }
}
