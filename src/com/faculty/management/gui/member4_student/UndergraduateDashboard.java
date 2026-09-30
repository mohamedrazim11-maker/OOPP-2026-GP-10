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
 * Tabbed shell hosting: Profile, Courses, Timetable, Attendance (pending
 * Member 3), Medical (pending Member 3), Grades, Results, SGPA, and CGPA.
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
        setSize(1180, 740);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_PAGE);

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));

        // 1. Header
        topContainer.add(UITheme.buildHeader(this, "Student Portal",
                currentUser.getFullName() + " (" + currentUser.getUsername() + ")", "Student", () -> {
            dispose();
            LoginController.showLogin();
        }));

        // 2. Student Info Quick Strip
        JPanel subBanner = new JPanel(new BorderLayout());
        subBanner.setBackground(Color.WHITE);
        subBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT),
                new EmptyBorder(10, 24, 10, 24)
        ));

        JLabel welcomeGreeting = new JLabel("👋 Welcome back, " + currentUser.getFullName());
        welcomeGreeting.setFont(UITheme.FONT_BODY_BOLD);
        welcomeGreeting.setForeground(UITheme.TEXT_MAIN);

        JPanel pillsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pillsPanel.setOpaque(false);

        UITheme.PillBadge idBadge = new UITheme.PillBadge("REG: " + currentUser.getUsername(), UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, UITheme.PRIMARY_TINT);
        UITheme.PillBadge facBadge = new UITheme.PillBadge("FACULTY OF TECHNOLOGY", UITheme.ACCENT_BLUE_LIGHT, UITheme.ACCENT_BLUE, new Color(191, 219, 254));
        UITheme.PillBadge statusBadge = new UITheme.PillBadge(currentUser.getStatus() != null ? currentUser.getStatus() : "ACTIVE", UITheme.SUCCESS_LIGHT, UITheme.SUCCESS_DARK, UITheme.SUCCESS_BORDER);

        pillsPanel.add(idBadge);
        pillsPanel.add(facBadge);
        pillsPanel.add(statusBadge);

        subBanner.add(welcomeGreeting, BorderLayout.WEST);
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
        tabs.addTab("📊  Attendance", new PendingModulePanel("📊", "Attendance Records",
                "Attendance percentages are recorded and maintained by the Technical Officer module (Member 3). " +
                "This tab will display Theory %, Practical %, and Combined % once that module is merged in."));
        tabs.addTab("🏥  Medical", new PendingModulePanel("🏥", "Medical Submissions",
                "Medical certificates and verified excused absences are maintained by the Technical Officer " +
                "module (Member 3). This tab will display submissions and approval status once merged in."));
        tabs.addTab("🏆  Grades", new GradePanel(controller, studentId));
        tabs.addTab("📋  Results", new ResultPanel(controller, studentId));
        tabs.addTab("Σ  SGPA", new SGPA_Panel(controller, studentId));
        tabs.addTab("ΣΣ  CGPA", new CGPA_Panel(controller, studentId));

        return tabs;
    }
}
