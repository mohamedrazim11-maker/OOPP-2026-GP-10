package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.LoginController;
import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.gui.common.UITheme;
<<<<<<< HEAD
import com.faculty.management.model.AttendanceRecord;
import com.faculty.management.model.Enrollment;
=======
>>>>>>> test
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
<<<<<<< HEAD
import java.util.List;
import java.util.concurrent.ExecutionException;

=======
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modern Undergraduate Dashboard GUI — Member 4 (Undergraduate, Timetable & Results).
 * High-performance tabbed self-service student portal connecting Profiles,
 * Enrolled Courses, Class Timetables, Attendance Monitoring, Medical Excuses,
 * Faculty Notice Board, UGC Letter Grades, Transcripts, SGPA & CGPA.
 */
>>>>>>> test
public class UndergraduateDashboard extends JFrame {

    private final User currentUser;
    private final UndergraduateController controller = new UndergraduateController();
<<<<<<< HEAD
    private JLabel coursesValue;
    private JLabel attendanceValue;
    private JLabel cgpaValue;
    private JLabel noticesValue;
    private JLabel dashboardStatus;
=======
>>>>>>> test

    public UndergraduateDashboard(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
<<<<<<< HEAD
        setTitle("Faculty Management System — Student Portal");
        setSize(1020, 680);
        setMinimumSize(new Dimension(880, 560));
=======
        setTitle("Faculty Management System — Student Portal (" + currentUser.getUsername() + ")");
        setSize(1240, 800);
        setMinimumSize(new Dimension(980, 640));
>>>>>>> test
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_PAGE);

        add(UITheme.buildHeader(this, "Student Portal", currentUser.getFullName(), "Undergraduate", () -> {
            dispose();
            LoginController.showLogin();
        }), BorderLayout.NORTH);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(24, 28, 24, 28));

        UITheme.ModernCard bannerCard = new UITheme.ModernCard(14);
        bannerCard.setLayout(new BorderLayout(16, 10));
        bannerCard.setCardBackground(Color.WHITE);
        bannerCard.setBorder(new EmptyBorder(20, 24, 20, 24));

<<<<<<< HEAD
        JPanel bannerText = new JPanel();
        bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        bannerText.setOpaque(false);

        JLabel welcomeLabel = new JLabel(currentUser.getDashboardGreeting());
        welcomeLabel.setFont(UITheme.FONT_PAGE_TITLE);
        welcomeLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel roleDescLabel = new JLabel("Academic status, course records, attendance, notices, and timetable information for the current semester.");
        roleDescLabel.setFont(UITheme.FONT_BODY);
        roleDescLabel.setForeground(UITheme.TEXT_MUTED);
        roleDescLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        bannerText.add(welcomeLabel);
        bannerText.add(roleDescLabel);

        JPanel badgeBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badgeBox.setOpaque(false);
        UITheme.PillBadge activeBadge = new UITheme.PillBadge("ENROLLED", UITheme.SUCCESS_LIGHT, UITheme.SUCCESS_DARK, UITheme.SUCCESS_BORDER);
        UITheme.PillBadge roleBadge = new UITheme.PillBadge("STUDENT", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, UITheme.PRIMARY_TINT);
        badgeBox.add(activeBadge);
        badgeBox.add(roleBadge);

        bannerCard.add(bannerText, BorderLayout.WEST);
        bannerCard.add(badgeBox, BorderLayout.EAST);

        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(18, 0, 18, 0));

        coursesValue = new JLabel("Loading...");
        attendanceValue = new JLabel("Loading...");
        cgpaValue = new JLabel("Loading...");
        noticesValue = new JLabel("Loading...");
        statsGrid.add(UITheme.createStatCard("", "Current Courses", coursesValue, UITheme.PRIMARY));
        statsGrid.add(UITheme.createStatCard("", "Attendance", attendanceValue, UITheme.SUCCESS));
        statsGrid.add(UITheme.createStatCard("", "Cumulative GPA", cgpaValue, UITheme.ACCENT_BLUE));
        statsGrid.add(UITheme.createStatCard("", "Notices", noticesValue, UITheme.WARNING_DARK));

        JLabel sectionTitle = new JLabel("Student Services");
        sectionTitle.setFont(UITheme.FONT_SECTION_TITLE);
        sectionTitle.setForeground(UITheme.TEXT_MAIN);
        sectionTitle.setBorder(new EmptyBorder(4, 4, 12, 4));

        JPanel modulesGrid = new JPanel(new GridLayout(2, 2, 18, 18));
        modulesGrid.setOpaque(false);

        modulesGrid.add(createModuleCard("My Courses", "Review enrolled courses and academic timetable details.", UITheme.PRIMARY, "Open"));
        modulesGrid.add(createModuleCard("Attendance", "Inspect attendance records and medical submissions.", UITheme.SUCCESS, "View"));
        modulesGrid.add(createModuleCard("Results", "Check grades, GPA trends, and overall performance.", UITheme.ACCENT_BLUE, "Review"));
        modulesGrid.add(createModuleCard("Notices", "Read official announcements and faculty updates.", UITheme.WARNING_DARK, "Browse"));

        mainContent.add(bannerCard);
        mainContent.add(statsGrid);
        mainContent.add(sectionTitle);
        mainContent.add(modulesGrid);

        add(UITheme.createModernScrollPane(mainContent), BorderLayout.CENTER);

        dashboardStatus = new JLabel("Loading your student records...");
        dashboardStatus.setFont(UITheme.FONT_SMALL);
        dashboardStatus.setForeground(UITheme.TEXT_MUTED);
        dashboardStatus.setBorder(new EmptyBorder(4, 28, 10, 28));
        add(dashboardStatus, BorderLayout.SOUTH);

        loadDashboardSummary();
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
        actionBtn.addActionListener(e -> openModule(title));
        bottomRow.add(actionBtn);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(descLbl, BorderLayout.CENTER);
        card.add(bottomRow, BorderLayout.SOUTH);

        return card;
    }

    private void loadDashboardSummary() {
        SwingWorker<DashboardSummary, Void> worker = new SwingWorker<DashboardSummary, Void>() {
            @Override
            protected DashboardSummary doInBackground() throws DatabaseException {
                List<Enrollment> enrollments = controller.getCourseDetails(currentUser.getUserId());
                List<AttendanceRecord> attendance = controller.getAttendance(currentUser.getUserId());
                double cgpa = controller.getCgpa(currentUser.getUserId());
                int noticeCount = controller.getNotices().size();

                long currentCourseCount = enrollments.stream()
                        .filter(enrollment -> Enrollment.ONGOING.equalsIgnoreCase(enrollment.getStatus())
                                || Enrollment.REPEAT.equalsIgnoreCase(enrollment.getStatus()))
                        .count();
                long attendedSessions = attendance.stream()
                        .mapToLong(record -> record.getTheoryAttended() + record.getPracticalAttended())
                        .sum();
                long totalSessions = attendance.stream()
                        .mapToLong(record -> record.getTheoryTotal() + record.getPracticalTotal())
                        .sum();

                String attendanceText = totalSessions == 0
                        ? "No records"
                        : String.format("%.1f%%", attendedSessions * 100.0 / totalSessions);
                return new DashboardSummary(currentCourseCount, attendanceText, cgpa, noticeCount);
            }

            @Override
            protected void done() {
                try {
                    DashboardSummary summary = get();
                    coursesValue.setText(summary.currentCourses + " Subjects");
                    attendanceValue.setText(summary.attendance);
                    cgpaValue.setText(String.format("%.3f", summary.cgpa));
                    noticesValue.setText(summary.noticeCount + " Notices");
                    dashboardStatus.setText("Dashboard data loaded for " + currentUser.getUsername() + ".");
                    dashboardStatus.setForeground(UITheme.TEXT_MUTED);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    showSummaryError("Loading dashboard data was interrupted.");
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    String message = cause == null ? e.getMessage() : cause.getMessage();
                    showSummaryError("Could not load dashboard data: " + message);
                }
            }
        };
        worker.execute();
    }

    private void showSummaryError(String message) {
        coursesValue.setText("Unavailable");
        attendanceValue.setText("Unavailable");
        cgpaValue.setText("Unavailable");
        noticesValue.setText("Unavailable");
        dashboardStatus.setText(message);
        dashboardStatus.setForeground(UITheme.DANGER_DARK);
    }

    private void openModule(String title) {
        JTabbedPane tabs = new JTabbedPane();
        UITheme.styleTabbedPane(tabs);
        int studentId = currentUser.getUserId();

        switch (title) {
            case "My Courses":
                tabs.addTab("Enrolled Courses", new StudentCoursePanel(controller, studentId));
                tabs.addTab("Timetable", new TimetablePanel(controller, studentId));
                break;
            case "Attendance":
                tabs.addTab("Attendance", new AttendancePanel(controller, studentId));
                tabs.addTab("Medical Submissions", new MedicalPanel(controller, studentId));
                break;
            case "Results":
                tabs.addTab("Grades", new GradePanel(controller, studentId));
                tabs.addTab("Official Results", new ResultPanel(controller, studentId));
                tabs.addTab("Cumulative GPA", new CGPA_Panel(controller, studentId));
                break;
            case "Notices":
                tabs.addTab("Faculty Notices", new StudentNoticePanel(controller, studentId));
                break;
            default:
                throw new IllegalArgumentException("Unknown student module: " + title);
        }

        JDialog dialog = new JDialog(this, title, true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.add(tabs, BorderLayout.CENTER);
        dialog.setSize(1120, 720);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private static final class DashboardSummary {
        private final long currentCourses;
        private final String attendance;
        private final double cgpa;
        private final int noticeCount;

        private DashboardSummary(long currentCourses, String attendance, double cgpa, int noticeCount) {
            this.currentCourses = currentCourses;
            this.attendance = attendance;
            this.cgpa = cgpa;
            this.noticeCount = noticeCount;
        }
=======
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
>>>>>>> test
    }
}
