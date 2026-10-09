package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LoginController;
import com.faculty.management.model.Lecturer;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Lecturer Dashboard GUI.
 * Redirect frame for Lecturer role supporting Profile & Material Management tabs.
 */
public class LecturerDashboard extends JFrame {

    private final User currentUser;
    private Lecturer currentLecturer;
    private JTabbedPane tabbedPane;

    public LecturerDashboard(User user) {
        this.currentUser = user;

        // Safely map User to Lecturer model
        if (user instanceof Lecturer) {
            this.currentLecturer = (Lecturer) user;
        } else {
            this.currentLecturer = new Lecturer();
            this.currentLecturer.setUserId(user.getUserId());
            this.currentLecturer.setUsername(user.getUsername());
            this.currentLecturer.setFirstName(user.getFirstName());
            this.currentLecturer.setLastName(user.getLastName());
            this.currentLecturer.setEmail(user.getEmail());
            this.currentLecturer.setContactNo(user.getContactNo());
            this.currentLecturer.setProfilePic(user.getProfilePic());
        }

        initComponents();
    }

    private void initComponents() {
        setTitle("Faculty Management System — Lecturer Portal");
        setSize(1000, 700);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. Header (Preserved Leader Style) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 118, 110)); // Deep Teal
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("Faculty Management System — Academic Staff");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("👨‍🏫 " + currentUser.getFullName() + " (Lecturer)");
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

        // --- 2. Main Tabbed Content Area ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Tab 1: Welcome Overview
        tabbedPane.addTab("🏠 Home Overview", createHomeOverviewPanel());

        // Tab 2: Profile Management (Week 2 Requirement)
        tabbedPane.addTab("👤 My Profile", new LecturerProfilePanel(currentLecturer));

        // Tab 3: Course Materials (Week 2 Requirement)
        tabbedPane.addTab("📄 Course Materials", new CourseMaterialsPanel());

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHomeOverviewPanel() {
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

        JLabel roleDescLabel = new JLabel("Academic Access: Course Materials, Mark Management, Student Eligibility, and Attendance Review.");
        roleDescLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleDescLabel.setForeground(new Color(100, 116, 139));
        roleDescLabel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JPanel modulesGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        modulesGrid.setOpaque(false);

        modulesGrid.add(createModuleCard("📄 Course Materials", "Upload and manage course handouts and lecture slides.", new Color(13, 148, 136)));
        modulesGrid.add(createModuleCard("📝 Assessment & Exam Marks", "Enter and update CA and End-Semester exam marks.", new Color(2, 132, 199)));
        modulesGrid.add(createModuleCard("🎯 Student Eligibility & GPA", "Check exam eligibility, calculate GPV, SGPA, and CGPA.", new Color(124, 58, 237)));
        modulesGrid.add(createModuleCard("📊 Attendance & Medical Records", "Review undergraduate attendance percentages and medical submissions.", new Color(217, 119, 6)));

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