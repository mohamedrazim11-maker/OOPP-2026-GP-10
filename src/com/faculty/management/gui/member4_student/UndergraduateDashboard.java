package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.LoginController;
import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern Undergraduate Dashboard GUI — Member 4 (Undergraduate, Timetable & Results).
 * High-performance tabbed self-service student portal connecting Profiles,
 * Enrolled Courses, Class Timetables, Attendance Monitoring, Medical Excuses,
 * Faculty Notice Board, UGC Letter Grades, Transcripts, SGPA & CGPA.
 */
public class UndergraduateDashboard extends JFrame {

    private final User currentUser;
    private final UndergraduateController controller = new UndergraduateController();

    public UndergraduateDashboard(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Faculty Management System — Student Portal (" + currentUser.getUsername() + ")");
        setSize(1220, 780);
        setMinimumSize(new Dimension(1020, 660));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_PAGE);

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));

        // 1. Unified Brand Header
        topContainer.add(UITheme.buildHeader(this, "Student Academic Portal",
                currentUser.getFullName() + " (" + currentUser.getUsername() + ")", "Student", () -> {
            dispose();
            LoginController.showLogin();
        }));

        // 2. Student Identity & Quick Status Banner
        JPanel subBanner = new JPanel(new BorderLayout());
        subBanner.setBackground(Color.WHITE);
        subBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT),
                new EmptyBorder(10, 24, 10, 24)
        ));

        // Left: Avatar + Greeting Stack
        JPanel leftIdentityBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftIdentityBox.setOpaque(false);

        UITheme.CircularAvatar avatarThumb = new UITheme.CircularAvatar(34, UITheme.PRIMARY);
        avatarThumb.setRingStroke(2.0f);
        if (currentUser.getProfilePic() != null && !currentUser.getProfilePic().isBlank()) {
            avatarThumb.setImagePath(currentUser.getProfilePic());
        }
        avatarThumb.setPlaceholder("👤");

        JPanel greetStack = new JPanel();
        greetStack.setLayout(new BoxLayout(greetStack, BoxLayout.Y_AXIS));
        greetStack.setOpaque(false);

        JLabel welcomeGreeting = new JLabel("👋 Welcome back, " + currentUser.getFullName());
        welcomeGreeting.setFont(UITheme.FONT_BODY_BOLD);
        welcomeGreeting.setForeground(UITheme.TEXT_MAIN);

        JLabel degreeInfo = new JLabel("Bachelor of Information and Communication Technology (BICT Hons) • Level II");
        degreeInfo.setFont(UITheme.FONT_SMALL);
        degreeInfo.setForeground(UITheme.TEXT_MUTED);

        greetStack.add(welcomeGreeting);
        greetStack.add(Box.createVerticalStrut(1));
        greetStack.add(degreeInfo);

        leftIdentityBox.add(avatarThumb);
        leftIdentityBox.add(greetStack);

        // Right: Status Badges
        JPanel pillsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pillsPanel.setOpaque(false);

        UITheme.PillBadge idBadge = new UITheme.PillBadge("REG: " + currentUser.getUsername(), UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, UITheme.PRIMARY_TINT);
        UITheme.PillBadge semBadge = new UITheme.PillBadge("SEMESTER 2 (2025/2026)", UITheme.PURPLE_LIGHT, UITheme.PURPLE, new Color(221, 214, 254));
        UITheme.PillBadge facBadge = new UITheme.PillBadge("FACULTY OF TECHNOLOGY", UITheme.ACCENT_BLUE_LIGHT, UITheme.ACCENT_BLUE, new Color(191, 219, 254));
        UITheme.PillBadge statusBadge = new UITheme.PillBadge(currentUser.getStatus() != null ? currentUser.getStatus() : "ACTIVE", UITheme.SUCCESS_LIGHT, UITheme.SUCCESS_DARK, UITheme.SUCCESS_BORDER);

        pillsPanel.add(idBadge);
        pillsPanel.add(semBadge);
        pillsPanel.add(facBadge);
        pillsPanel.add(statusBadge);

        subBanner.add(leftIdentityBox, BorderLayout.WEST);
        subBanner.add(pillsPanel, BorderLayout.EAST);

        topContainer.add(subBanner);

        add(topContainer, BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        UITheme.styleTabbedPane(tabs);

        int studentId = currentUser.getUserId();

        tabs.addTab("👤  Profile", new StudentProfilePanel(controller, studentId));
        tabs.addTab("📚  My Courses", new StudentCoursePanel(controller, studentId));
        tabs.addTab("🗓️  Timetable", new TimetablePanel(controller, studentId));
        tabs.addTab("📊  Attendance", new AttendancePanel(controller, studentId));
        tabs.addTab("🏥  Medical", new MedicalPanel(controller, studentId));
        tabs.addTab("📢  Notices", new StudentNoticePanel(controller, studentId));
        tabs.addTab("🏆  Grades", new GradePanel(controller, studentId));
        tabs.addTab("📋  Results", new ResultPanel(controller, studentId));
        tabs.addTab("Σ  SGPA", new SGPA_Panel(controller, studentId));
        tabs.addTab("ΣΣ  CGPA", new CGPA_Panel(controller, studentId));

        return tabs;
    }
}
