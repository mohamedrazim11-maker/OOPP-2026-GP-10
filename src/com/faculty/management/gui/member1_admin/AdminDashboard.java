package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.LoginController;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Admin Dashboard GUI.
 * Target redirect frame for Administrator role.
 */
public class AdminDashboard extends JFrame {

    private final User currentUser;

    private JTabbedPane tabbedPane;
    private UserManagementPanel userManagementPanel;

    public AdminDashboard(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Faculty Management System — Administrator Portal");
        setSize(1080, 680);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 58, 138)); // Deep Navy Blue
        headerPanel.setBorder(new EmptyBorder(12, 25, 12, 25));

        JLabel titleLabel = new JLabel("🏛️ Faculty Management System (FMS)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("👤 " + currentUser.getFullName() + " (Admin)");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setBackground(new Color(220, 38, 38)); // Red
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

        // Tabbed Navigation
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);

        // Tab 1: Overview Dashboard
        JPanel overviewPanel = createOverviewPanel();

        // Tab 2: User Profile Management
        userManagementPanel = new UserManagementPanel();

        tabbedPane.addTab("  🏠 Overview  ", overviewPanel);
        tabbedPane.addTab("  👥 User Profiles  ", userManagementPanel);

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
        userCard.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                tabbedPane.setSelectedIndex(1); // Switch to User Profiles tab
            }
        });

        modulesGrid.add(userCard);
        modulesGrid.add(createModuleCard("📚 Course Management", "Add courses, assign credits, manage course modules.", new Color(16, 185, 129)));
        modulesGrid.add(createModuleCard("📢 Notice Board", "Publish and maintain official faculty notices and circulars.", new Color(245, 158, 11)));
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
}
