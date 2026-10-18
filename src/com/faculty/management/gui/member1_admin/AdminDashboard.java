package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.LoginController;
import com.faculty.management.gui.common.DangerButton;
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
    private UserManagementPanel userManagementPanel;
    private CourseManagementPanel courseManagementPanel;
    private NoticeManagementPanel noticeManagementPanel;

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
        setSize(1080, 680);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 58, 138)); // Deep Navy Blue
        headerPanel.setBorder(new EmptyBorder(12, 25, 12, 25));

        JLabel titleLabel = new JLabel("Faculty Management System (FMS)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        // Dynamic Binding / Polymorphism: invokes currentUser.getRoleTitle() at runtime
        JLabel userLabel = new JLabel(currentUser.getFullName() + " (" + currentUser.getRoleTitle() + ")");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(Color.WHITE);

        DangerButton logoutButton = new DangerButton("Logout");
        logoutButton.setPreferredSize(new Dimension(85, 30));
        logoutButton.addActionListener(e -> {
            try {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    LoginController.showLogin();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error during logout: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        userPanel.add(userLabel);
        userPanel.add(logoutButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        // Tabbed Navigation
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);

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

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createOverviewPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(248, 250, 252));
        centerPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

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

        JLabel roleDescLabel = new JLabel("You have full administrative privileges: User Profiles, Courses, Notices, and Timetables.");
        roleDescLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleDescLabel.setForeground(new Color(100, 116, 139));
        roleDescLabel.setBorder(new EmptyBorder(10, 0, 20, 0));

        // Quick Stats / Modules Cards
        JPanel modulesGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        modulesGrid.setOpaque(false);

        JPanel userCard = createModuleCard("👥 User Management", "Create, update, and maintain credentials for system users (Lecturers, TOs, Students). Click to manage.", new Color(37, 99, 235));
        userCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tabbedPane.setSelectedIndex(1); // Switch to User Profiles tab
            }
        });

        JPanel courseCard = createModuleCard("📚 Course Management", "Add, update, delete, view courses, and assign course details. Click to manage.", new Color(16, 185, 129));
        courseCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        courseCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tabbedPane.setSelectedIndex(2); // Switch to Course Management tab
            }
        });

        JPanel noticeCard = createModuleCard("📢 Notice Board", "Create, edit, delete, and view official faculty notices and circulars. Click to manage.", new Color(245, 158, 11));
        noticeCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        noticeCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tabbedPane.setSelectedIndex(3); // Switch to Notice Board tab
            }
        });

        modulesGrid.add(userCard);
        modulesGrid.add(courseCard);
        modulesGrid.add(noticeCard);
        modulesGrid.add(createModuleCard("🗓️ Timetable Management", "Create and maintain lecture and lab timetables.", new Color(139, 92, 246)));

        welcomeCard.add(welcomeLabel);
        welcomeCard.add(roleDescLabel);
        welcomeCard.add(modulesGrid);

        centerPanel.add(welcomeCard, BorderLayout.CENTER);
        return centerPanel;
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

