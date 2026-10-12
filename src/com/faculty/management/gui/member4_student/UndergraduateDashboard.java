package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.LoginController;
<<<<<<< HEAD
import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.gui.common.UITheme;
=======
>>>>>>> test
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
<<<<<<< HEAD
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modern Undergraduate Dashboard GUI — Member 4 (Undergraduate, Timetable & Results).
 * High-performance tabbed self-service student portal connecting Profiles,
 * Enrolled Courses, Class Timetables, Attendance Monitoring, Medical Excuses,
 * Faculty Notice Board, UGC Letter Grades, Transcripts, SGPA & CGPA.
=======

/**
 * Undergraduate Dashboard GUI.
 * Target redirect frame for Undergraduate (Student) role.
>>>>>>> test
 */
public class UndergraduateDashboard extends JFrame {

    private final User currentUser;
<<<<<<< HEAD
    private final UndergraduateController controller = new UndergraduateController();
=======
>>>>>>> test

    public UndergraduateDashboard(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
<<<<<<< HEAD
        setTitle("Faculty Management System — Student Portal (" + currentUser.getUsername() + ")");
        setSize(1240, 800);
        setMinimumSize(new Dimension(980, 640));
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
        subBanner.setBackground(UITheme.LAVENDER_SURFACE);
        subBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.LAVENDER_BORDER, 1),
                        new EmptyBorder(10, 16, 10, 16)),
                new EmptyBorder(8, 24, 8, 24)
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
        bannerMargin.setBorder(new EmptyBorder(12, 24, 8, 24));
        bannerMargin.add(subBanner, BorderLayout.CENTER);
        topContainer.add(bannerMargin);

        add(topContainer, BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
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
=======
        setTitle("Faculty Management System — Student Portal");
        setSize(950, 600);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(67, 56, 202)); // Royal Indigo
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("Faculty of Technology — Student Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("🎓 " + currentUser.getFullName() + " (" + currentUser.getUsername() + ")");
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

        JLabel roleDescLabel = new JLabel("Your Student Space: View Courses, Attendance, Exam Results, SGPA/CGPA, and Timetables.");
        roleDescLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleDescLabel.setForeground(new Color(100, 116, 139));
        roleDescLabel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JPanel modulesGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        modulesGrid.setOpaque(false);

        modulesGrid.add(createModuleCard("📖 My Courses & Materials", "Access course syllabi, lecture notes, and learning resources.", new Color(79, 70, 229)));
        modulesGrid.add(createModuleCard("📈 Attendance & Medicals", "Check Theory/Practical attendance % and medical submissions.", new Color(13, 148, 136)));
        modulesGrid.add(createModuleCard("🏆 Grades, GPA & Results", "View course grades, UGC grade points, SGPA, and CGPA.", new Color(225, 29, 72)));
        modulesGrid.add(createModuleCard("📅 Academic Timetable", "View current semester lecture and laboratory timetable.", new Color(202, 138, 4)));

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

        return card;
>>>>>>> test
    }
}
