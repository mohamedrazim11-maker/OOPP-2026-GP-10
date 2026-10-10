package com.faculty.management.gui.member3_attendance;

import com.faculty.management.controller.LoginController;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Technical Officer Dashboard GUI.
 * Target redirect frame for Technical Officer role.
 */
public class TechnicalOfficerDashboard extends JFrame {

    private final User currentUser;

    public TechnicalOfficerDashboard(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Faculty Management System — Technical Officer Portal");
        setSize(950, 600);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(124, 45, 18)); // Rust Amber / Bronze
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("Faculty Management System — Technical Operations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("🛠️ " + currentUser.getFullName() + " (TO)");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        JButton profileButton = new JButton("My Profile");
        profileButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        profileButton.setBackground(new Color(14, 165, 233));
        profileButton.setForeground(Color.WHITE);
        profileButton.setFocusPainted(false);
        profileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileButton.addActionListener(e -> {
            new TechnicalOfficerProfileDialog(this, currentUser).setVisible(true);
        });

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
        userPanel.add(profileButton);
        userPanel.add(logoutButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(248, 250, 252));
        centerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

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

        JLabel roleDescLabel = new JLabel("Technical Officer Responsibilities: Daily Attendance, Medical Submissions, and Lab Schedules.");
        roleDescLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleDescLabel.setForeground(new Color(100, 116, 139));
        roleDescLabel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JPanel modulesGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        modulesGrid.setOpaque(false);

        modulesGrid.add(createModuleCard("📋 Attendance Entry", "Record and update undergraduate attendance for theory and practical sessions.", new Color(217, 119, 6)));
        modulesGrid.add(createModuleCard("🏥 Medical Certificates", "Log medical certificates and manage verification/approval status.", new Color(225, 29, 72)));
        modulesGrid.add(createModuleCard("📊 Batch Attendance Summary", "View semester attendance percentages by course and batch.", new Color(79, 70, 229)));
        modulesGrid.add(createModuleCard("🗓️ Department Timetables", "View departmental lab and lecture timetable schedules.", new Color(5, 150, 105)));

        welcomeCard.add(welcomeLabel);
        welcomeCard.add(roleDescLabel);
        welcomeCard.add(modulesGrid);

        centerPanel.add(welcomeCard, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
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
