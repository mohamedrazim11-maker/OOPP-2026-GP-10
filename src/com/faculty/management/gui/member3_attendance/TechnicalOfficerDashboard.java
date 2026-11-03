package com.faculty.management.gui.member3_attendance;

import com.faculty.management.controller.LoginController;
import com.faculty.management.controller.TechnicalOfficerController;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Notice;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern Technical Officer Dashboard GUI.
 * Top navigation bar layout matching Faculty Management System UI specifications:
 * 1. Overview
 * 2. Attendance Entry
 * 3. Medical Certificates
 * 4. Batch Summary
 * 5. Dept Timetables
 * 6. Notice Board
 * 7. My Profile
 */
public class TechnicalOfficerDashboard extends JFrame {

    private final User currentUser;
    private final TechnicalOfficerController controller;

    private JTabbedPane tabbedPane;
    private JPanel navBarPanel;
    private JLabel breadcrumbLabel;
    private final List<NavTabButton> tabButtons = new ArrayList<>();

    private AttendanceEntryPanel attendanceEntryPanel;
    private MedicalManagementPanel medicalManagementPanel;
    private ViewAttendancePanel viewAttendancePanel;
    private DepartmentTimetablePanel departmentTimetablePanel;
    private NoticeViewerPanel noticeViewerPanel;
    private TechnicalOfficerProfilePanel profilePanel;

    private static final String[] NAV_TITLES = {
            "Overview",
            "Attendance Entry",
            "Medical Certificates",
            "Batch Summary",
            "Dept Timetables",
            "Notice Board",
            "My Profile"
    };

    public TechnicalOfficerDashboard(User user) {
        this.currentUser = user;
        this.controller = new TechnicalOfficerController();
        initComponents();
    }

    private void initComponents() {
        setTitle("Faculty Management System — Faculty of Technology · University of Ruhuna");
        setSize(1240, 800);
        setMinimumSize(new Dimension(1060, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 246, 240));

        // 1. Top Shell Header & Horizontal Navigation
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        topContainer.add(createUniversityHeader());
        topContainer.add(createHorizontalNavBar());

        add(topContainer, BorderLayout.NORTH);

        // 2. Hidden Tabbed Pane Container for Content Switching
        tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                return 0; // Hides default Swing tabs
            }
            @Override
            protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
            }
        });
        tabbedPane.setBorder(null);
        tabbedPane.setOpaque(false);

        // Tab 0: Overview (Page 1)
        tabbedPane.addTab("Overview", createHomeOverviewPanel());

        // Tab 1: Attendance Entry
        attendanceEntryPanel = new AttendanceEntryPanel(currentUser);
        tabbedPane.addTab("Attendance Entry", attendanceEntryPanel);

        // Tab 2: Medical Certificates
        medicalManagementPanel = new MedicalManagementPanel();
        tabbedPane.addTab("Medical Certificates", medicalManagementPanel);

        // Tab 3: Batch Summary
        viewAttendancePanel = new ViewAttendancePanel();
        tabbedPane.addTab("Batch Summary", viewAttendancePanel);

        // Tab 4: Dept Timetables
        departmentTimetablePanel = new DepartmentTimetablePanel();
        tabbedPane.addTab("Dept Timetables", departmentTimetablePanel);

        // Tab 5: Notice Board
        noticeViewerPanel = new NoticeViewerPanel();
        tabbedPane.addTab("Notice Board", noticeViewerPanel);

        // Tab 6: My Profile
        profilePanel = new TechnicalOfficerProfilePanel(currentUser);
        tabbedPane.addTab("My Profile", profilePanel);

        // Synchronize tab state with nav buttons and breadcrumb
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < tabButtons.size()) {
                for (int i = 0; i < tabButtons.size(); i++) {
                    tabButtons.get(i).setActive(i == selectedIndex);
                }
                if (breadcrumbLabel != null) {
                    breadcrumbLabel.setText("Home / " + NAV_TITLES[selectedIndex]);
                }
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * University Maroon Header Bar (#5C1221 / Solid Ruhuna Burgundy).
     * Contains Branding on left, and User Info Avatar & Sign Out on right.
     */
    private JPanel createUniversityHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(92, 18, 33)); // Solid Ruhuna Burgundy
        header.setPreferredSize(new Dimension(1240, 64));
        header.setBorder(new EmptyBorder(10, 24, 10, 24));

        // Left Branding Stack
        JPanel leftBrand = new JPanel();
        leftBrand.setLayout(new BoxLayout(leftBrand, BoxLayout.Y_AXIS));
        leftBrand.setOpaque(false);

        JLabel lblBrandTitle = new JLabel("Faculty Management System");
        lblBrandTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBrandTitle.setForeground(Color.WHITE);

        JLabel lblBrandSub = new JLabel("Faculty of Technology · University of Ruhuna");
        lblBrandSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBrandSub.setForeground(new Color(230, 205, 210));

        leftBrand.add(lblBrandTitle);
        leftBrand.add(Box.createVerticalStrut(2));
        leftBrand.add(lblBrandSub);

        // Right User Avatar, Name & Sign Out
        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 2));
        rightBox.setOpaque(false);

        // Initials Avatar
        String userFullName = (currentUser != null && currentUser.getFullName() != null)
                ? currentUser.getFullName() : "Razim Razim";
        String initials = extractInitials(userFullName);

        JPanel avatarCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(216, 180, 166)); // Warm peach beige
                g2.fill(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));
                g2.setColor(new Color(92, 18, 33)); // Maroon initials
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };
        avatarCircle.setPreferredSize(new Dimension(36, 36));
        avatarCircle.setOpaque(false);

        // User Name & Role text
        JPanel userMeta = new JPanel();
        userMeta.setLayout(new BoxLayout(userMeta, BoxLayout.Y_AXIS));
        userMeta.setOpaque(false);

        JLabel lblUserName = new JLabel(userFullName);
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserName.setForeground(Color.WHITE);

        String roleStr = (currentUser != null && currentUser.getRoleTitle() != null)
                ? currentUser.getRoleTitle() : "Technical Officer";
        JLabel lblUserRole = new JLabel(roleStr);
        lblUserRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblUserRole.setForeground(new Color(230, 205, 210));

        userMeta.add(lblUserName);
        userMeta.add(lblUserRole);

        // Sign Out Outline Button
        JButton btnSignOut = new JButton("Sign out") {
            private boolean hovered = false;
            {
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setForeground(Color.WHITE);
                setFocusPainted(false);
                setBorderPainted(false);
                setContentAreaFilled(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setBorder(new EmptyBorder(6, 14, 6, 14));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hovered) {
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                }
                g2.setColor(new Color(255, 255, 255, 140));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Double(0.6, 0.6, getWidth() - 1.2, getHeight() - 1.2, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btnSignOut.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to sign out?",
                    "Confirm Sign Out",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                LoginController.showLogin();
            }
        });

        rightBox.add(avatarCircle);
        rightBox.add(userMeta);
        rightBox.add(Box.createHorizontalStrut(6));
        rightBox.add(btnSignOut);

        header.add(leftBrand, BorderLayout.WEST);
        header.add(rightBox, BorderLayout.EAST);
        return header;
    }

    /**
     * Horizontal Navigation Bar (Top Tabs).
     * Pure white background with active amber/orange underline indicator (#D97706).
     */
    private JPanel createHorizontalNavBar() {
        navBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        navBarPanel.setBackground(Color.WHITE);
        navBarPanel.setPreferredSize(new Dimension(1240, 46));
        navBarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));

        tabButtons.clear();
        for (int i = 0; i < NAV_TITLES.length; i++) {
            final int tabIndex = i;
            NavTabButton tabBtn = new NavTabButton(NAV_TITLES[i], i == 0);
            tabBtn.addActionListener(e -> tabbedPane.setSelectedIndex(tabIndex));
            tabButtons.add(tabBtn);
            navBarPanel.add(tabBtn);
        }

        return navBarPanel;
    }

    /**
     * Page 1: Overview Panel.
     * Replaces previous layout with the exact FMS UI PDF design:
     * - Breadcrumb: Home / Overview
     * - Title: Overview
     * - Subtitle: Operational summary for the Technical Officer, Faculty of Technology.
     * - 4 KPI Stat Cards (Today's sessions, Batch attendance, Lab venues, Medical requests in maroon)
     * - Left Column: Technical & attendance operations with Open action buttons
     * - Right Column: Recent notices with priority pills (Urgent, Normal)
     */
    private JComponent createHomeOverviewPanel() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(16, 28, 24, 28));

        // 1. Breadcrumbs
        breadcrumbLabel = new JLabel("Home / Overview");
        breadcrumbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        breadcrumbLabel.setForeground(new Color(107, 114, 128));
        breadcrumbLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 2. Page Header
        JLabel pageTitle = new JLabel("Overview");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pageTitle.setForeground(new Color(17, 24, 39));
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageSub = new JLabel("Operational summary for the Technical Officer, Faculty of Technology.");
        pageSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pageSub.setForeground(new Color(107, 114, 128));
        pageSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 3. Stat Cards Row (4 Cards)
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsGrid.setMaximumSize(new Dimension(2000, 110));

        statsGrid.add(createStatCard("Today's sessions", "4", "Active labs", false));
        statsGrid.add(createStatCard("Batch attendance", "84.5%", "Overall", false));
        statsGrid.add(createStatCard("Lab venues", "5", "Department labs", false));
        statsGrid.add(createStatCard("Medical requests", "3", "Pending review", true)); // Maroon Card

        // 4. Two-Column Split (Operations vs Recent Notices)
        JPanel twoColumnPanel = new JPanel(new GridBagLayout());
        twoColumnPanel.setOpaque(false);
        twoColumnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 1.0;

        // Left Operations Card (~58% width)
        gbc.gridx = 0;
        gbc.weightx = 0.58;
        gbc.insets = new Insets(0, 0, 0, 16);
        twoColumnPanel.add(createOperationsCard(), gbc);

        // Right Recent Notices Card (~42% width)
        gbc.gridx = 1;
        gbc.weightx = 0.42;
        gbc.insets = new Insets(0, 0, 0, 0);
        twoColumnPanel.add(createRecentNoticesCard(), gbc);

        // Assembly
        content.add(breadcrumbLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(pageTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(pageSub);
        content.add(Box.createVerticalStrut(18));
        content.add(statsGrid);
        content.add(Box.createVerticalStrut(18));
        content.add(twoColumnPanel);

        return UITheme.createModernScrollPane(content);
    }

    /**
     * Metric KPI Card matching FMS UI PDF.
     * @param isMaroon if true, renders with solid Ruhuna Burgundy background (#5C1221).
     */
    private JPanel createStatCard(String topLabel, String value, String bottomLabel, boolean isMaroon) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isMaroon) {
                    g2.setColor(new Color(92, 18, 33)); // #5C1221 Solid Burgundy
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.setColor(new Color(229, 231, 235)); // Subtle light border
                    g2.setStroke(new BasicStroke(1.0f));
                    g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 10, 10));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTop = new JLabel(topLabel);
        lblTop.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTop.setForeground(isMaroon ? new Color(243, 216, 221) : new Color(107, 114, 128));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblVal.setForeground(isMaroon ? new Color(245, 158, 11) : new Color(17, 24, 39)); // Warm amber if maroon

        JLabel lblBottom = new JLabel(bottomLabel);
        lblBottom.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBottom.setForeground(isMaroon ? new Color(243, 216, 221) : new Color(107, 114, 128));

        card.add(lblTop);
        card.add(Box.createVerticalStrut(4));
        card.add(lblVal);
        card.add(Box.createVerticalStrut(4));
        card.add(lblBottom);

        return card;
    }

    /**
     * Left Column: Technical & attendance operations with Open action buttons.
     */
    private JPanel createOperationsCard() {
        JPanel card = createWhiteCardPanel();
        card.setLayout(new BorderLayout());

        // Header
        JPanel cardHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 14));
        cardHeader.setOpaque(false);
        JLabel lblHeader = new JLabel("Technical & attendance operations");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHeader.setForeground(new Color(17, 24, 39));
        cardHeader.add(lblHeader);

        card.add(cardHeader, BorderLayout.NORTH);

        // List Container
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(0, 16, 12, 16));

        listPanel.add(createOperationRow("Attendance Entry",
                "Record daily attendance for theory lectures and lab sessions.", 1));
        listPanel.add(createOperationRow("Medical Certificates",
                "Log submissions and verify excused absences.", 2));
        listPanel.add(createOperationRow("Batch Attendance Summary",
                "Compute semester attendance and 80% eligibility by course.", 3));
        listPanel.add(createOperationRow("Department Timetables",
                "View lab time slots, venues and lecture schedules.", 4));
        listPanel.add(createOperationRow("Faculty Notice Board",
                "Browse announcements, circulars and exam memos.", 5));
        listPanel.add(createOperationRow("Staff Profile & Account",
                "Manage contact number, email and personal details.", 6));

        card.add(listPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createOperationRow(String title, String desc, int targetTabIndex) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(243, 244, 246)),
                new EmptyBorder(12, 4, 12, 4)
        ));

        // Texts
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(17, 24, 39));

        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(107, 114, 128));

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(lblDesc);

        // Open Button
        JButton btnOpen = new JButton("Open") {
            private boolean hovered = false;
            {
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setForeground(new Color(55, 65, 81));
                setFocusPainted(false);
                setBorderPainted(false);
                setContentAreaFilled(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setBorder(new EmptyBorder(6, 16, 6, 16));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? new Color(243, 244, 246) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 6, 6));
                g2.setColor(new Color(209, 213, 219));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 6, 6));
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btnOpen.addActionListener(e -> tabbedPane.setSelectedIndex(targetTabIndex));

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        btnWrapper.setOpaque(false);
        btnWrapper.add(btnOpen);

        row.add(textPanel, BorderLayout.CENTER);
        row.add(btnWrapper, BorderLayout.EAST);
        return row;
    }

    /**
     * Right Column: Recent notices card with status badge pills (Urgent / Normal).
     */
    private JPanel createRecentNoticesCard() {
        JPanel card = createWhiteCardPanel();
        card.setLayout(new BorderLayout());

        // Header
        JPanel cardHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 14));
        cardHeader.setOpaque(false);
        JLabel lblHeader = new JLabel("Recent notices");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHeader.setForeground(new Color(17, 24, 39));
        cardHeader.add(lblHeader);

        card.add(cardHeader, BorderLayout.NORTH);

        // List Container
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(0, 16, 12, 16));

        // Sample / dynamic notice entries matching FMS UI PDF
        listPanel.add(createNoticeRow("Leave",
                "Faculty Administration · Nov 01, 2026", "Urgent", true));
        listPanel.add(createNoticeRow("For all",
                "Faculty Administration · Oct 27, 2026", "Urgent", true));
        listPanel.add(createNoticeRow("Continuous Assessment (CA) Verification Period Open",
                "Faculty Examination Unit · Oct 10, 2026", "Normal", false));
        listPanel.add(createNoticeRow("Submission of Medical Certificates for Missed Practical Sessions",
                "Office of the Dean, Faculty of Technology · Oct 06, 2026", "Normal", false));

        card.add(listPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createNoticeRow(String title, String meta, String badgeText, boolean isUrgent) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(243, 244, 246)),
                new EmptyBorder(12, 4, 12, 4)
        ));

        // Texts
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("<html><p style='width: 250px; color: #111827; font-weight: bold; font-family: Segoe UI;'>" + title + "</p></html>");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblMeta = new JLabel(meta);
        lblMeta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMeta.setForeground(new Color(107, 114, 128));

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(lblMeta);

        // Badge Pill
        Color bgPill = isUrgent ? new Color(254, 226, 226) : new Color(254, 243, 199); // Soft Red / Soft Amber
        Color fgPill = isUrgent ? new Color(220, 38, 38) : new Color(180, 83, 9);

        JLabel lblBadge = new JLabel(badgeText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgPill);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBadge.setForeground(fgPill);
        lblBadge.setBorder(new EmptyBorder(3, 10, 3, 10));
        lblBadge.setOpaque(false);

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(lblBadge);

        row.add(textPanel, BorderLayout.CENTER);
        row.add(badgeWrapper, BorderLayout.EAST);
        return row;
    }

    private JPanel createWhiteCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(229, 231, 235));
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private String extractInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "TO";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    private String formatRole(String role) {
        if (role == null) return "Technical Officer";
        return role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
    }

    /**
     * Custom Navigation Tab Button with Amber/Orange underline indicator (#D97706).
     */
    private static class NavTabButton extends JButton {
        private boolean active;
        private boolean hovered;

        public NavTabButton(String title, boolean active) {
            super(title);
            this.active = active;
            setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
            setForeground(active ? new Color(17, 24, 39) : new Color(107, 114, 128));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 16, 12, 16));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
            setForeground(active ? new Color(17, 24, 39) : new Color(107, 114, 128));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (hovered && !active) {
                g2.setColor(new Color(249, 250, 251));
                g2.fill(new RoundRectangle2D.Double(2, 4, getWidth() - 4, getHeight() - 8, 6, 6));
            }

            super.paintComponent(g);

            if (active) {
                // Amber/Orange underline indicator (#D97706)
                g2.setColor(new Color(217, 119, 6));
                g2.fillRect(14, getHeight() - 3, getWidth() - 28, 3);
            }
            g2.dispose();
        }
    }
}
