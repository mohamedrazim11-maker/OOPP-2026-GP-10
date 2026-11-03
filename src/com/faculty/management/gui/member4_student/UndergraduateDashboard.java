package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.LoginController;
import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Undergraduate self-service portal for profiles, courses, attendance and results.
 */
public class UndergraduateDashboard extends JFrame {

    private static final String[] STUDENT_SECTIONS = {
            "Overview", "Courses", "Timetable", "Attendance", "Medical",
            "Notices", "Grades", "Results", "SGPA", "CGPA", "Profile"
    };

    private final User currentUser;
    private final UndergraduateController controller = new UndergraduateController();
<<<<<<< Updated upstream
=======
    private final List<JButton> navigationButtons = new ArrayList<>();
    private CardLayout contentLayout;
    private JPanel contentCards;
>>>>>>> Stashed changes

    public UndergraduateDashboard(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Faculty Management System — Undergraduate Portal");
        setSize(1360, 860);
        setMinimumSize(new Dimension(1050, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_PAGE);

<<<<<<< Updated upstream
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
=======
        add(buildBrandHeader(), BorderLayout.NORTH);

        JPanel portal = new JPanel(new BorderLayout());
        portal.setBackground(UITheme.BG_PAGE);
        portal.add(buildNavigation(), BorderLayout.NORTH);
        portal.add(buildContent(), BorderLayout.CENTER);
        add(portal, BorderLayout.CENTER);

        showSection(0);
    }

    private JPanel buildBrandHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(12, 24, 12, 22));

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Faculty Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 19));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Faculty of Technology  ·  University of Ruhuna");
        subtitle.setFont(UITheme.FONT_SMALL);
        subtitle.setForeground(new Color(245, 220, 184));

        brand.add(title);
        brand.add(Box.createVerticalStrut(3));
        brand.add(subtitle);
        header.add(brand, BorderLayout.WEST);

        JPanel account = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        account.setOpaque(false);

        UITheme.CircularAvatar avatar = new UITheme.CircularAvatar(38, UITheme.PRIMARY_TINT);
        avatar.setRingColor(new Color(255, 242, 220));
        avatar.setRingStroke(1.5f);
        avatar.setTextColor(UITheme.PRIMARY);
        avatar.setPlaceholder(getInitials(currentUser.getFullName()));
        if (currentUser.getProfilePic() != null && !currentUser.getProfilePic().isBlank()) {
            avatar.setImagePath(currentUser.getProfilePic());
        }

        JPanel identity = new JPanel();
        identity.setOpaque(false);
        identity.setLayout(new BoxLayout(identity, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(currentUser.getFullName().trim());
        name.setFont(UITheme.FONT_BODY_BOLD);
        name.setForeground(Color.WHITE);
        JLabel roleAndId = new JLabel(currentUser.getRoleTitle() + "  ·  " + currentUser.getUsername());
        roleAndId.setFont(UITheme.FONT_SMALL);
        roleAndId.setForeground(new Color(245, 220, 184));
        identity.add(name);
        identity.add(Box.createVerticalStrut(2));
        identity.add(roleAndId);

        UITheme.ModernButton signOut = new UITheme.ModernButton(
                "Sign out", UITheme.ButtonStyle.SECONDARY, 8);
        signOut.setFont(UITheme.FONT_SMALL_BOLD);
        signOut.setBorder(new EmptyBorder(8, 14, 8, 14));
        signOut.addActionListener(e -> confirmSignOut());

        account.add(avatar);
        account.add(identity);
        account.add(signOut);
        header.add(account, BorderLayout.EAST);
        return header;
    }

    private JPanel buildNavigation() {
        JPanel navigation = new JPanel(new BorderLayout());
        navigation.setBackground(Color.WHITE);
        navigation.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT));

        JPanel items = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        items.setBackground(Color.WHITE);
        for (int i = 0; i < STUDENT_SECTIONS.length; i++) {
            final int sectionIndex = i;
            JButton button = new SectionButton(STUDENT_SECTIONS[i]);
            button.addActionListener(e -> showSection(sectionIndex));
            navigationButtons.add(button);
            items.add(button);
        }

        JScrollPane scroll = new JScrollPane(items);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 5));
        scroll.setPreferredSize(new Dimension(0, 52));
        navigation.add(scroll, BorderLayout.CENTER);
        return navigation;
    }

    private JPanel buildContent() {
        contentLayout = new CardLayout();
        contentCards = new JPanel(contentLayout);
        contentCards.setBackground(UITheme.BG_PAGE);

        contentCards.add(new DashboardOverviewPanel(controller, currentUser, this::showSection),
                STUDENT_SECTIONS[0]);
        int studentId = currentUser.getUserId();
        contentCards.add(new StudentCoursePanel(controller, studentId), STUDENT_SECTIONS[1]);
        contentCards.add(new TimetablePanel(controller, studentId), STUDENT_SECTIONS[2]);
        contentCards.add(new AttendancePanel(controller, studentId), STUDENT_SECTIONS[3]);
        contentCards.add(new MedicalPanel(controller, studentId), STUDENT_SECTIONS[4]);
        contentCards.add(new StudentNoticePanel(controller, studentId), STUDENT_SECTIONS[5]);
        contentCards.add(new GradePanel(controller, studentId), STUDENT_SECTIONS[6]);
        contentCards.add(new ResultPanel(controller, studentId), STUDENT_SECTIONS[7]);
        contentCards.add(new SGPA_Panel(controller, studentId), STUDENT_SECTIONS[8]);
        contentCards.add(new CGPA_Panel(controller, studentId), STUDENT_SECTIONS[9]);
        contentCards.add(new StudentProfilePanel(controller, studentId), STUDENT_SECTIONS[10]);
        return contentCards;
    }

    private void showSection(int index) {
        if (index < 0 || index >= STUDENT_SECTIONS.length || contentLayout == null) {
            return;
        }
        contentLayout.show(contentCards, STUDENT_SECTIONS[index]);
        for (int i = 0; i < navigationButtons.size(); i++) {
            ((SectionButton) navigationButtons.get(i)).setActive(i == index);
        }
        navigationButtons.get(index).scrollRectToVisible(navigationButtons.get(index).getBounds());
    }

    private void confirmSignOut() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to sign out?",
                "Confirm Sign Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            LoginController.showLogin();
        }
>>>>>>> Stashed changes
    }

    private static String getInitials(String name) {
        if (name == null || name.isBlank()) {
            return "U";
        }
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private static final class SectionButton extends JButton {
        private boolean selected;

        private SectionButton(String text) {
            super(text);
            setFont(UITheme.FONT_BODY);
            setForeground(UITheme.TEXT_BODY);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(15, 15, 12, 15));
            setPreferredSize(new Dimension(getPreferredSize().width + 30, 52));
        }

        private void setActive(boolean selected) {
            this.selected = selected;
            setFont(selected ? UITheme.FONT_BODY_BOLD : UITheme.FONT_BODY);
            setForeground(selected ? UITheme.PRIMARY : UITheme.TEXT_BODY);
            setBorder(selected
                    ? new MatteBorder(0, 0, 3, 0, UITheme.PRIMARY)
                    : new EmptyBorder(15, 15, 12, 15));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (getModel().isRollover() && !selected) {
                g.setColor(UITheme.PRIMARY_LIGHT);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            super.paintComponent(g);
        }
    }
}
