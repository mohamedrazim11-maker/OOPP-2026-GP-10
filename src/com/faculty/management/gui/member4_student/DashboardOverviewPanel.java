package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Enrollment;
import com.faculty.management.model.Notice;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

/**
 * Data-backed landing page for the undergraduate portal.
 */
public class DashboardOverviewPanel extends JPanel {

    private final UndergraduateController controller;
    private final User currentUser;
    private final IntConsumer onNavigate;

    private final JLabel courseCount = new JLabel("—");
    private final JLabel creditCount = new JLabel("—");
    private final JLabel practicalCount = new JLabel("—");
    private final JLabel courseDataStatus = new JLabel(" ");
    private JPanel noticesList;

    public DashboardOverviewPanel(UndergraduateController controller, User currentUser, IntConsumer onNavigate) {
        this.controller = controller;
        this.currentUser = currentUser;
        this.onNavigate = onNavigate;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);

        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(UITheme.BG_PAGE);
        page.setBorder(new EmptyBorder(24, 28, 28, 28));
        page.add(buildWelcomeCard());
        page.add(Box.createVerticalStrut(18));
        page.add(buildMetrics());
        courseDataStatus.setFont(UITheme.FONT_SMALL);
        courseDataStatus.setForeground(UITheme.DANGER_DARK);
        page.add(courseDataStatus);
        page.add(Box.createVerticalStrut(18));
        page.add(buildLowerContent());

        JScrollPane scroll = new JScrollPane(page);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.BG_PAGE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        loadOverviewData();
    }

    private JComponent buildWelcomeCard() {
        UITheme.ModernCard welcome = new UITheme.ModernCard(12);
        welcome.setCardBackground(UITheme.PRIMARY);
        welcome.setBorderColor(UITheme.PRIMARY);
        welcome.setLayout(new BorderLayout(16, 8));
        welcome.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel welcomeText = new JPanel();
        welcomeText.setOpaque(false);
        welcomeText.setLayout(new BoxLayout(welcomeText, BoxLayout.Y_AXIS));

        JLabel greeting = new JLabel(currentUser.getDashboardGreeting());
        greeting.setFont(UITheme.FONT_PAGE_TITLE);
        greeting.setForeground(Color.WHITE);
        JLabel description = new JLabel("Your academic information and student services, all in one place.");
        description.setFont(UITheme.FONT_BODY);
        description.setForeground(new Color(249, 235, 219));
        welcomeText.add(greeting);
        welcomeText.add(Box.createVerticalStrut(6));
        welcomeText.add(description);
        welcome.add(welcomeText, BorderLayout.CENTER);

        JLabel date = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));
        date.setFont(UITheme.FONT_BODY_BOLD);
        date.setForeground(new Color(249, 235, 219));
        welcome.add(date, BorderLayout.EAST);
        return welcome;
    }

    private JPanel buildMetrics() {
        JPanel metrics = new JPanel(new GridLayout(1, 3, 14, 0));
        metrics.setOpaque(false);
        metrics.add(buildMetricCard("Enrolled modules", courseCount, "Across all semesters"));
        metrics.add(buildMetricCard("Cumulative credits", creditCount, "From your registered courses"));
        metrics.add(buildMetricCard("Practical courses", practicalCount, "Modules with practical sessions"));
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 112));
        return metrics;
    }

    private JPanel buildMetricCard(String label, JLabel value, String detail) {
        UITheme.ModernCard card = new UITheme.ModernCard(12);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 18, 14, 18));

        JLabel title = new JLabel(label);
        title.setFont(UITheme.FONT_SMALL_BOLD);
        title.setForeground(UITheme.TEXT_MUTED);
        value.setFont(UITheme.FONT_KPI_VALUE);
        value.setForeground(UITheme.TEXT_MAIN);
        JLabel description = new JLabel(detail);
        description.setFont(UITheme.FONT_SMALL);
        description.setForeground(UITheme.TEXT_MUTED);
        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(value);
        card.add(Box.createVerticalStrut(3));
        card.add(description);
        return card;
    }

    private JPanel buildLowerContent() {
        JPanel lower = new JPanel(new GridLayout(1, 2, 16, 0));
        lower.setOpaque(false);
        lower.add(buildNoticesCard());
        lower.add(buildQuickLinksCard());
        lower.setPreferredSize(new Dimension(900, 260));
        lower.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));
        return lower;
    }

    private JPanel buildNoticesCard() {
        UITheme.ModernCard card = new UITheme.ModernCard(12);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = new JLabel("Recent notices");
        title.setFont(UITheme.FONT_SECTION_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);
        UITheme.ModernButton openNotices = new UITheme.ModernButton(
                "View all", UITheme.ButtonStyle.GHOST, 6);
        openNotices.setFont(UITheme.FONT_SMALL_BOLD);
        openNotices.addActionListener(e -> onNavigate.accept(5));
        heading.add(title, BorderLayout.WEST);
        heading.add(openNotices, BorderLayout.EAST);
        card.add(heading, BorderLayout.NORTH);

        noticesList = new JPanel();
        noticesList.setOpaque(false);
        noticesList.setLayout(new BoxLayout(noticesList, BoxLayout.Y_AXIS));
        noticesList.add(messageLabel("Loading notices…", UITheme.TEXT_MUTED));
        card.add(noticesList, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildQuickLinksCard() {
        UITheme.ModernCard card = new UITheme.ModernCard(12);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel title = new JLabel("Quick access");
        title.setFont(UITheme.FONT_SECTION_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);
        card.add(title, BorderLayout.NORTH);

        JPanel links = new JPanel(new GridLayout(2, 2, 10, 10));
        links.setOpaque(false);
        links.add(createLinkButton("Courses", 1));
        links.add(createLinkButton("Timetable", 2));
        links.add(createLinkButton("Attendance", 3));
        links.add(createLinkButton("Results", 7));
        card.add(links, BorderLayout.CENTER);
        return card;
    }

    private JButton createLinkButton(String label, int section) {
        UITheme.ModernButton button = new UITheme.ModernButton(
                label, UITheme.ButtonStyle.OUTLINE, 8);
        button.setFont(UITheme.FONT_BODY_BOLD);
        button.addActionListener(e -> onNavigate.accept(section));
        return button;
    }

    private void loadOverviewData() {
        try {
            List<Enrollment> enrollments = controller.getCourseDetails(currentUser.getUserId());
            int credits = 0;
            int practicalCourses = 0;
            for (Enrollment enrollment : enrollments) {
                if (enrollment.getCourse() != null) {
                    credits += enrollment.getCourse().getCredits();
                    if (enrollment.getCourse().hasPractical()) {
                        practicalCourses++;
                    }
                }
            }
            courseCount.setText(String.valueOf(enrollments.size()));
            creditCount.setText(String.valueOf(credits));
            practicalCount.setText(String.valueOf(practicalCourses));
        } catch (DatabaseException e) {
            courseCount.setText("Unavailable");
            creditCount.setText("Unavailable");
            practicalCount.setText("Unavailable");
            courseCount.setForeground(UITheme.DANGER_DARK);
            creditCount.setForeground(UITheme.DANGER_DARK);
            practicalCount.setForeground(UITheme.DANGER_DARK);
            courseDataStatus.setText("Unable to load course summary: " + e.getMessage());
        }

        try {
            List<Notice> notices = controller.getNotices().stream()
                    .sorted(Comparator.comparing(
                            Notice::getPostedDate,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(4)
                    .collect(Collectors.toList());
            showNotices(notices);
        } catch (DatabaseException e) {
            showNoticeError("Unable to load notices: " + e.getMessage());
        }
    }

    private void showNotices(List<Notice> notices) {
        noticesList.removeAll();
        if (notices.isEmpty()) {
            noticesList.add(messageLabel("No recent notices available.", UITheme.TEXT_MUTED));
            noticesList.revalidate();
            noticesList.repaint();
            return;
        }
        for (int i = 0; i < notices.size(); i++) {
            Notice notice = notices.get(i);
            noticesList.add(createNoticeRow(notice));
            if (i < notices.size() - 1) {
                noticesList.add(Box.createVerticalStrut(8));
            }
        }
        noticesList.revalidate();
        noticesList.repaint();
    }

    private JComponent createNoticeRow(Notice notice) {
        JPanel row = new JPanel(new BorderLayout(8, 3));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(3, 0, 7, 0));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(notice.getTitle());
        title.setFont(UITheme.FONT_BODY_BOLD);
        title.setForeground(UITheme.TEXT_MAIN);
        JLabel category = new JLabel(notice.getCategory() + "  ·  " + notice.getFormattedDate());
        category.setFont(UITheme.FONT_SMALL);
        category.setForeground(UITheme.TEXT_MUTED);
        text.add(title);
        text.add(Box.createVerticalStrut(2));
        text.add(category);

        UITheme.PillBadge priority = new UITheme.PillBadge(
                notice.getPriority(), UITheme.BG_HEADER, UITheme.TEXT_BODY, UITheme.BORDER_LIGHT);
        row.add(text, BorderLayout.CENTER);
        row.add(priority, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        return row;
    }

    private void showNoticeError(String message) {
        noticesList.removeAll();
        noticesList.add(messageLabel(message, UITheme.DANGER_DARK));
        noticesList.revalidate();
        noticesList.repaint();
    }

    private JLabel messageLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_BODY);
        label.setForeground(color);
        return label;
    }
}
