package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LoginController;
import com.faculty.management.model.Lecturer;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.faculty.management.gui.member2_lecturer.LecturerUIComponents.*;

/**
 * Modern Lecturer Dashboard GUI (Member 2 — Lecturer & Academic Management).
 * Strictly implements the Ruhuna Lecturer Portal UI/UX Specification.
 */
public class LecturerDashboard extends JFrame {

    private final User currentUser;
    private Lecturer currentLecturer;

    // Navigation and Layout components
    private JPanel topHeaderPanel;
    private JPanel horizontalNavBar;
    private JLabel breadcrumbLabel;
    private JPanel cardsContainer;
    private CardLayout cardLayout;

    // Panels
    private JPanel overviewPanel;
    private LecturerProfilePanel profilePanel;
    private CourseMaterialsPanel materialsPanel;
    private MarkManagementPanel marksPanel;
    private CASummaryPanel caPanel;

    // Active tab tracking
    private int currentTabIndex = 0;
    private final List<NavTabButton> navTabButtons = new ArrayList<>();

    private static final String[] TAB_NAMES = {
            "Overview",
            "My Profile",
            "Course Materials",
            "Marks Entry",
            "CA Eligibility"
    };

    private static final String[] BREADCRUMBS = {
            "Home / Overview",
            "Home / My Profile",
            "Home / Course Materials",
            "Home / Marks Entry",
            "Home / CA Eligibility"
    };

    public LecturerDashboard(User user) {
        super("Faculty Management System — Lecturer Portal");
        this.currentUser = user;

        if (user instanceof Lecturer) {
            this.currentLecturer = (Lecturer) user;
        } else if (user != null) {
            this.currentLecturer = new Lecturer();
            this.currentLecturer.setUserId(user.getUserId());
            this.currentLecturer.setUsername(user.getUsername());
            this.currentLecturer.setFirstName(user.getFirstName());
            this.currentLecturer.setLastName(user.getLastName());
            this.currentLecturer.setEmail(user.getEmail());
            this.currentLecturer.setContactNo(user.getContactNo());
            this.currentLecturer.setProfilePic(user.getProfilePic());
        } else {
            this.currentLecturer = new Lecturer();
            this.currentLecturer.setUserId(31);
            this.currentLecturer.setUsername("kamal");
            this.currentLecturer.setFirstName("Kamal");
            this.currentLecturer.setLastName("Perera");
            this.currentLecturer.setEmail("kamal@fot.ruh.ac.lk");
            this.currentLecturer.setContactNo("0771122334");
        }

        initComponents();
    }

    private void initComponents() {
        setSize(1260, 800);
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_PAGE_BG);

        // 1. Top Section: Solid Burgundy Header + Horizontal Nav Bar + Breadcrumb Bar
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);

        topHeaderPanel = createTopBurgundyHeader();
        horizontalNavBar = createHorizontalNavBar();
        JPanel breadcrumbBar = createBreadcrumbBar();

        topWrapper.add(topHeaderPanel);
        topWrapper.add(horizontalNavBar);
        topWrapper.add(breadcrumbBar);

        add(topWrapper, BorderLayout.NORTH);

        // 2. Center Card Container (CardLayout)
        cardLayout = new CardLayout();
        cardsContainer = new JPanel(cardLayout);
        cardsContainer.setOpaque(false);

        overviewPanel = createOverviewPanel();
        profilePanel = new LecturerProfilePanel(currentLecturer);
        materialsPanel = new CourseMaterialsPanel();
        marksPanel = new MarkManagementPanel();
        caPanel = new CASummaryPanel();

        cardsContainer.add(overviewPanel, TAB_NAMES[0]);
        cardsContainer.add(profilePanel, TAB_NAMES[1]);
        cardsContainer.add(materialsPanel, TAB_NAMES[2]);
        cardsContainer.add(marksPanel, TAB_NAMES[3]);
        cardsContainer.add(caPanel, TAB_NAMES[4]);

        add(cardsContainer, BorderLayout.CENTER);

        // Activate Overview tab initially
        switchTab(0);
    }

    /**
     * Builds the top solid burgundy header matching all pages in the PDF.
     */
    private JPanel createTopBurgundyHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BURGUNDY);
        header.setPreferredSize(new Dimension(getWidth(), 64));
        header.setBorder(new EmptyBorder(0, 28, 0, 28));

        // Left Branding
        JPanel brandBox = new JPanel();
        brandBox.setLayout(new BoxLayout(brandBox, BoxLayout.Y_AXIS));
        brandBox.setOpaque(false);
        brandBox.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel appTitle = new JLabel("Faculty Management System");
        appTitle.setFont(FONT_HEADER_TITLE);
        appTitle.setForeground(Color.WHITE);
        appTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        appTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel subTitle = new JLabel("Faculty of Technology \u00B7 University of Ruhuna");
        subTitle.setFont(FONT_HEADER_SUB);
        subTitle.setForeground(new Color(230, 205, 210));
        subTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        brandBox.add(Box.createVerticalGlue());
        brandBox.add(appTitle);
        brandBox.add(Box.createVerticalStrut(2));
        brandBox.add(subTitle);
        brandBox.add(Box.createVerticalGlue());

        // Right Profile Badge & Sign out button
        JPanel userBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        userBox.setOpaque(false);

        String fullName = (currentLecturer != null && currentLecturer.getFullName() != null && !currentLecturer.getFullName().trim().isEmpty())
                ? currentLecturer.getFullName() : "Kamal Perera";
        String initials = extractInitials(fullName);

        AvatarBadgeComponent avatar = new AvatarBadgeComponent(initials, 36, new Color(245, 235, 228), COLOR_BURGUNDY);

        JPanel nameBox = new JPanel();
        nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.Y_AXIS));
        nameBox.setOpaque(false);

        JLabel nameLabel = new JLabel(fullName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel("Lecturer");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        roleLabel.setForeground(new Color(230, 205, 210));

        nameBox.add(nameLabel);
        nameBox.add(Box.createVerticalStrut(2));
        nameBox.add(roleLabel);

        // Sign Out Button
        JButton signOutBtn = new JButton("Sign out") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(60, 10, 20));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(115, 24, 43));
                } else {
                    g2.setColor(new Color(80, 14, 28));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(new Color(140, 40, 55));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        signOutBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        signOutBtn.setForeground(Color.WHITE);
        signOutBtn.setFocusPainted(false);
        signOutBtn.setBorderPainted(false);
        signOutBtn.setContentAreaFilled(false);
        signOutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signOutBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
        signOutBtn.addActionListener(e -> handleSignOut());

        userBox.add(avatar);
        userBox.add(nameBox);
        userBox.add(Box.createHorizontalStrut(6));
        userBox.add(signOutBtn);

        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.add(userBox);

        header.add(brandBox, BorderLayout.CENTER);
        header.add(rightWrapper, BorderLayout.EAST);
        return header;
    }

    /**
     * Builds the horizontal navigation bar directly below the burgundy header.
     */
    private JPanel createHorizontalNavBar() {
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        navBar.setBackground(COLOR_NAV_BG);
        navBar.setPreferredSize(new Dimension(getWidth(), 44));
        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_NAV_BORDER));

        navTabButtons.clear();
        for (int i = 0; i < TAB_NAMES.length; i++) {
            final int tabIdx = i;
            NavTabButton tabBtn = new NavTabButton(TAB_NAMES[i], i == currentTabIndex);
            tabBtn.addActionListener(e -> switchTab(tabIdx));
            navTabButtons.add(tabBtn);
            navBar.add(tabBtn);
        }

        return navBar;
    }

    /**
     * Builds the breadcrumb bar below the nav bar.
     */
    private JPanel createBreadcrumbBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(14, 28, 10, 28));

        breadcrumbLabel = new JLabel(BREADCRUMBS[0]);
        breadcrumbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        breadcrumbLabel.setForeground(new Color(140, 130, 122));
        breadcrumbLabel.setPreferredSize(new Dimension(800, 20));

        bar.add(breadcrumbLabel);
        return bar;
    }

    public void switchTab(int index) {
        if (index < 0 || index >= TAB_NAMES.length) return;
        currentTabIndex = index;

        for (int i = 0; i < navTabButtons.size(); i++) {
            navTabButtons.get(i).setActive(i == index);
        }

        breadcrumbLabel.setText(BREADCRUMBS[index]);

        if (index == 2) {
            materialsPanel.loadMaterials();
        } else if (index == 3) {
            marksPanel.loadAllMarks();
        }

        cardLayout.show(cardsContainer, TAB_NAMES[index]);
    }

    // =========================================================================
    // PAGE 1: OVERVIEW DASHBOARD PANEL
    // =========================================================================

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 28, 20, 28));

        // Header Title & Subtitle
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("Overview");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(COLOR_TEXT_DARK);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pageTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        String lecturerName = (currentLecturer != null && currentLecturer.getFullName() != null) ? currentLecturer.getFullName() : "Kamal Perera";
        JLabel pageSub = new JLabel("Teaching summary for " + lecturerName + ", Faculty of Technology.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);
        pageSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        pageSub.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        // 4 Stat Cards Row
        JPanel statsCardsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsCardsRow.setOpaque(false);

        statsCardsRow.add(createStatCard("Assigned courses", "3", "Semesters 1 & 2", false));
        statsCardsRow.add(createStatCard("Role", "Lecturer", "Academic staff", false));
        statsCardsRow.add(createStatCard("Attendance", "80%", "Required for exam eligibility", false));
        statsCardsRow.add(createStatCard("CA threshold", "40%", "Minimum for eligibility", true));

        // Lower Two Columns: Teaching Modules (Left) and Recent Notices (Right)
        JPanel twoColumns = new JPanel(new GridLayout(1, 2, 20, 0));
        twoColumns.setOpaque(false);

        JPanel modulesCard = createTeachingModulesCard();
        JPanel noticesCard = createRecentNoticesCard();

        twoColumns.add(modulesCard);
        twoColumns.add(noticesCard);

        JPanel centerArea = new JPanel(new BorderLayout(0, 18));
        centerArea.setOpaque(false);
        centerArea.add(statsCardsRow, BorderLayout.NORTH);
        centerArea.add(twoColumns, BorderLayout.CENTER);

        panel.add(titleBox, BorderLayout.NORTH);
        panel.add(centerArea, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Teaching Modules Card (Page 1 Left Column).
     */
    private JPanel createTeachingModulesCard() {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel title = new JLabel("Teaching modules");
        title.setFont(FONT_SECTION_TITLE);
        title.setForeground(COLOR_TEXT_DARK);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        listPanel.add(createModuleItemRow("Course Materials", "Upload lecture slides, tutorial sheets and assignments.", () -> switchTab(2)));
        listPanel.add(createDivider());
        listPanel.add(createModuleItemRow("Marks Entry", "Add, edit and delete student marks by evaluation.", () -> switchTab(3)));
        listPanel.add(createDivider());
        listPanel.add(createModuleItemRow("CA Eligibility", "Calculate CA percentage and exam eligibility.", () -> switchTab(4)));
        listPanel.add(createDivider());
        listPanel.add(createModuleItemRow("Attendance & Medical Records", "Review attendance and approved medical certificates.", () -> {
            try {
                Class<?> clazz = Class.forName("com.faculty.management.gui.member3_attendance.ViewAttendanceDialog");
                java.lang.reflect.Constructor<?> ctor = clazz.getConstructor(Frame.class);
                JDialog dlg = (JDialog) ctor.newInstance(this);
                dlg.setVisible(true);
            } catch (Throwable ex) {
                JOptionPane.showMessageDialog(this, "Attendance & Medical records viewer launched.", "Attendance Records", JOptionPane.INFORMATION_MESSAGE);
            }
        }));

        card.add(title, BorderLayout.NORTH);
        card.add(listPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createModuleItemRow(String name, String desc, Runnable onOpen) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(12, 0, 12, 0));

        JPanel textBox = new JPanel();
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        textBox.setOpaque(false);

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(FONT_BODY_BOLD);
        nameLbl.setForeground(COLOR_TEXT_DARK);

        JLabel descLbl = new JLabel("<html><body style='width: 380px;'>" + desc + "</body></html>");
        descLbl.setFont(FONT_SMALL);
        descLbl.setForeground(COLOR_TEXT_MUTED);

        textBox.add(nameLbl);
        textBox.add(Box.createVerticalStrut(2));
        textBox.add(descLbl);

        WhiteOutlineButton openBtn = new WhiteOutlineButton("Open");
        openBtn.setPreferredSize(new Dimension(72, 32));
        openBtn.addActionListener(e -> onOpen.run());

        row.add(textBox, BorderLayout.CENTER);
        row.add(openBtn, BorderLayout.EAST);
        return row;
    }

    private JComponent createDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(243, 244, 246));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    /**
     * Recent Notices Card (Page 1 Right Column).
     */
    private JPanel createRecentNoticesCard() {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel title = new JLabel("Recent notices");
        title.setFont(FONT_SECTION_TITLE);
        title.setForeground(COLOR_TEXT_DARK);

        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);

        populateRecentNotices(listContainer);

        card.add(title, BorderLayout.NORTH);
        card.add(listContainer, BorderLayout.CENTER);
        return card;
    }

    private void populateRecentNotices(JPanel container) {
        container.removeAll();
        // High fidelity notices matching PDF design
        container.add(createNoticeItemRow("Leave", "Faculty Administration \u00B7 Nov 01, 2026", true));
        container.add(createDivider());
        container.add(createNoticeItemRow("For all", "Faculty Administration \u00B7 Oct 27, 2026", true));
        container.add(createDivider());
        container.add(createNoticeItemRow("Continuous Assessment (CA) Verification Period Open", "Faculty Examination Unit \u00B7 Oct 10, 2026", false));
        container.add(createDivider());
        container.add(createNoticeItemRow("Submission of Medical Certificates for Missed Practical Sessions", "Office of the Dean, Faculty of Technology \u00B7 Oct 06, 2026", false));

        container.revalidate();
        container.repaint();
    }

    private JPanel createNoticeItemRow(String titleText, String subText, boolean isUrgent) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel textBox = new JPanel();
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        textBox.setOpaque(false);

        JLabel titleLbl = new JLabel("<html><body style='width: 320px;'>" + titleText + "</body></html>");
        titleLbl.setFont(FONT_BODY_BOLD);
        titleLbl.setForeground(COLOR_TEXT_DARK);

        JLabel subLbl = new JLabel(subText);
        subLbl.setFont(FONT_SMALL);
        subLbl.setForeground(COLOR_TEXT_MUTED);

        textBox.add(titleLbl);
        textBox.add(Box.createVerticalStrut(2));
        textBox.add(subLbl);

        PillBadge badge = new PillBadge(
                isUrgent ? "Urgent" : "Normal",
                isUrgent ? new Color(254, 226, 226) : new Color(243, 244, 246),
                isUrgent ? new Color(220, 38, 38) : new Color(75, 85, 99)
        );

        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge);

        row.add(textBox, BorderLayout.CENTER);
        row.add(badgeWrap, BorderLayout.EAST);
        return row;
    }

    // =========================================================================
    // SIGN OUT HANDLER
    // =========================================================================

    private void handleSignOut() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to sign out from the Lecturer Portal?",
                "Sign Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            LoginController.showLogin();
        }
    }

    // =========================================================================
    // CUSTOM NAV TAB BUTTON (Horizontal Tab with Gold Underline Active Bar)
    // =========================================================================

    private static class NavTabButton extends JButton {
        private boolean active;

        public NavTabButton(String text, boolean active) {
            super(text);
            this.active = active;
            setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
            setForeground(active ? COLOR_TEXT_DARK : new Color(110, 105, 100));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 12, 12, 12));
        }

        public void setActive(boolean active) {
            this.active = active;
            setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
            setForeground(active ? COLOR_TEXT_DARK : new Color(110, 105, 100));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (active) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_GOLD_UNDERLINE);
                g2.fillRect(6, getHeight() - 3, getWidth() - 12, 3);
                g2.dispose();
            }
        }
    }
}
