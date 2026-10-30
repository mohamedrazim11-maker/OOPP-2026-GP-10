package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.LoginController;
import com.faculty.management.gui.common.DangerButton;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Admin;
import com.faculty.management.model.Role;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Admin Dashboard GUI.
 * Target redirect frame for Administrator role.
 *
 * Demonstrates Core OOP Concepts from Lecture Notes:
 * 1. Classes & Objects: Blueprint for Administrator Portal window.
 * 2. Inheritance: Extends javax.swing.JFrame and explicitly invokes super(title).
 * 3. Constructor Chaining & Overloading: Default and parameterized constructors via this(...).
 * 4. Abstraction & Encapsulation: Private instance fields with public accessors.
 * 5. Polymorphism & Dynamic Binding: Runtime method dispatch via currentUser.getRoleTitle()
 *    and currentUser.getDashboardGreeting().
 * 6. Exception Handling: Structured try-catch blocks for robust operation.
 */
public class AdminDashboard extends JFrame {

    // Encapsulated instance fields (Encapsulation / Information Hiding)
    private final User currentUser;
    private JTabbedPane tabbedPane;
    private UITheme.SidebarNavigation sidebarNav;
    private UITheme.HeaderPanel headerPanel;
    private UserManagementPanel userManagementPanel;
    private CourseManagementPanel courseManagementPanel;
    private NoticeManagementPanel noticeManagementPanel;

    private static final String[] NAV_TITLES = {
            "Overview",
            "User Profiles",
            "Course Management",
            "Notice Board"
    };

    /**
     * Default constructor (no-arg).
     * Demonstrates Constructor Overloading and Constructor Chaining using this(...).
     * (OOP Lesson 02 Part 02 & Lesson 03 Part 01)
     */
    public AdminDashboard() {
        this(null);
    }

    /**
     * Parameterized constructor.
     * Explicitly calls superclass constructor super(...) as first statement.
     * Provides null-safety fallback to Admin object to prevent NullPointerException.
     *
     * @param user The authenticated user instance
     */
    public AdminDashboard(User user) {
        super("Faculty Management System — Administrator Portal");
        this.currentUser = (user != null) ? user : new Admin(1, "admin", "admin123", "admin@faculty.edu", "System", "Admin", new Role(1, Role.ADMIN, "Administrator"), "0771234567", null, "ACTIVE");
        initComponents();
    }

    private void initComponents() {
        setSize(1180, 740);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_PAGE);

        String roleTitle = (currentUser.getRole() != null) ? currentUser.getRole().getRoleName() : "Administrator";

        // 1. Left Sidebar Navigation (Per FMS UI PDF - Solid Burgundy #5C1221, Gold Active Pill)
        sidebarNav = new UITheme.SidebarNavigation("Administrator Portal", NAV_TITLES, 0, idx -> {
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

        // Standardized Modern Header (Sand / Beige Header per PDF)
        headerPanel = new UITheme.HeaderPanel(this, NAV_TITLES[0], currentUser.getFullName(), roleTitle, () -> {
            try {
                dispose();
                LoginController.showLogin();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error during sign out: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Tabbed Navigation
        tabbedPane = new JTabbedPane();
        UITheme.styleTabbedPane(tabbedPane);

        // Tab 1: Overview Dashboard
        JPanel overviewPanel = createOverviewPanel();

        // Tab 2: User Profile Management
        userManagementPanel = new UserManagementPanel();

        // Tab 3: Course Management
        courseManagementPanel = new CourseManagementPanel();

        // Tab 4: Notice Board Management
        noticeManagementPanel = new NoticeManagementPanel();

        tabbedPane.addTab("  Overview  ", overviewPanel);
        tabbedPane.addTab("  User Profiles  ", userManagementPanel);
        tabbedPane.addTab("  Course Management  ", courseManagementPanel);
        tabbedPane.addTab("  Notice Board  ", noticeManagementPanel);

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

    private JPanel createOverviewPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(UITheme.BG_PAGE);
        centerPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel welcomeCard = new JPanel();
        welcomeCard.setLayout(new BoxLayout(welcomeCard, BoxLayout.Y_AXIS));
        welcomeCard.setBackground(Color.WHITE);
        welcomeCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel welcomeLabel = new JLabel("Welcome to Admin Portal, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel roleDescLabel = new JLabel("Full administrative privileges · System User Accounts, Modules & Announcements");
        roleDescLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleDescLabel.setForeground(UITheme.TEXT_MUTED);
        roleDescLabel.setBorder(new EmptyBorder(6, 0, 20, 0));

        // Quick Modules Cards (2x2 Grid per Wireframe 01 Overview)
        JPanel modulesGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        modulesGrid.setOpaque(false);

        JPanel userCard = createModuleCard("User Management", "Create, update, maintain credentials", () -> {
            tabbedPane.setSelectedIndex(1);
        });

        JPanel courseCard = createModuleCard("Course Management", "Add, update, delete, view courses", () -> {
            tabbedPane.setSelectedIndex(2);
        });

        JPanel noticeCard = createModuleCard("Notice Board", "Create, edit, delete notices", () -> {
            tabbedPane.setSelectedIndex(3);
        });

        JPanel timetableCard = createModuleCard("Timetable Management", "Create lecture and lab timetables", () -> {
            JOptionPane.showMessageDialog(this, "Timetable module is managed per semester schedules.", "Timetable Management", JOptionPane.INFORMATION_MESSAGE);
        });

        modulesGrid.add(userCard);
        modulesGrid.add(courseCard);
        modulesGrid.add(noticeCard);
        modulesGrid.add(timetableCard);

        welcomeCard.add(welcomeLabel);
        welcomeCard.add(roleDescLabel);
        welcomeCard.add(modulesGrid);

        centerPanel.add(welcomeCard, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createModuleCard(String title, String desc, Runnable onManage) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLbl.setForeground(UITheme.TEXT_MUTED);
        descLbl.setBorder(new EmptyBorder(6, 0, 0, 0));

        textPanel.add(titleLbl);
        textPanel.add(descLbl);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setOpaque(false);

        JButton manageBtn = new JButton("Click to manage");
        manageBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        manageBtn.setBackground(Color.WHITE);
        manageBtn.setForeground(UITheme.PRIMARY);
        manageBtn.setFocusPainted(false);
        manageBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        manageBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));
        if (onManage != null) {
            manageBtn.addActionListener(e -> onManage.run());
        }

        btnPanel.add(manageBtn);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    public UserManagementPanel getUserManagementPanel() {
        return userManagementPanel;
    }

    public CourseManagementPanel getCourseManagementPanel() {
        return courseManagementPanel;
    }

    public NoticeManagementPanel getNoticeManagementPanel() {
        return noticeManagementPanel;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    /**
     * Getter for currentUser (Encapsulation).
     *
     * @return The active User (Admin)
     */
    public User getCurrentUser() {
        return currentUser;
    }
}

