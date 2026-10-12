package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LoginController;
<<<<<<< HEAD
import com.faculty.management.gui.common.UITheme;
=======
>>>>>>> test
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
<<<<<<< HEAD
 * Modern Lecturer Dashboard GUI.
=======
 * Lecturer Dashboard GUI.
>>>>>>> test
 * Target redirect frame for Lecturer role.
 */
public class LecturerDashboard extends JFrame {

    private final User currentUser;

    public LecturerDashboard(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Faculty Management System — Lecturer Portal");
<<<<<<< HEAD
        setSize(1020, 680);
        setMinimumSize(new Dimension(880, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_PAGE);

        // Standardized Modern Header
        add(UITheme.buildHeader(this, "Academic Staff Portal", currentUser.getFullName(), "Lecturer", () -> {
            dispose();
            LoginController.showLogin();
        }), BorderLayout.NORTH);

        // Main Content
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(24, 28, 24, 28));

        // 1. Welcome Hero Banner
        UITheme.ModernCard bannerCard = new UITheme.ModernCard(14);
        bannerCard.setLayout(new BorderLayout(16, 10));
        bannerCard.setCardBackground(Color.WHITE);
        bannerCard.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel bannerText = new JPanel();
        bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        bannerText.setOpaque(false);

        JLabel welcomeLabel = new JLabel(currentUser.getDashboardGreeting());
        welcomeLabel.setFont(UITheme.FONT_PAGE_TITLE);
        welcomeLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel roleDescLabel = new JLabel("Academic Access: Course Materials, Mark Management, Student Eligibility, and Attendance Review.");
        roleDescLabel.setFont(UITheme.FONT_BODY);
        roleDescLabel.setForeground(UITheme.TEXT_MUTED);
        roleDescLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        bannerText.add(welcomeLabel);
        bannerText.add(roleDescLabel);

        JPanel badgeBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badgeBox.setOpaque(false);
        UITheme.PillBadge staffBadge = new UITheme.PillBadge("ACADEMIC STAFF", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, UITheme.PRIMARY_TINT);
        UITheme.PillBadge semBadge = new UITheme.PillBadge("SEMESTER 1 & 2", UITheme.ACCENT_BLUE_LIGHT, UITheme.ACCENT_BLUE, new Color(191, 219, 254));
        badgeBox.add(staffBadge);
        badgeBox.add(semBadge);

        bannerCard.add(bannerText, BorderLayout.WEST);
        bannerCard.add(badgeBox, BorderLayout.EAST);

        // 2. Stat Cards
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(18, 0, 18, 0));

        statsGrid.add(UITheme.createStatCard("", "Assigned Courses", "3 Modules", UITheme.TEAL));
        statsGrid.add(UITheme.createStatCard("", "Marks Submitted", "Continuous Assess.", UITheme.ACCENT_BLUE));
        statsGrid.add(UITheme.createStatCard("", "Eligibility Status", "40% CA Threshold", UITheme.PURPLE));
        statsGrid.add(UITheme.createStatCard("", "Attendance Ratio", "80% Required", UITheme.WARNING_DARK));

        // 3. Section Title
        JLabel sectionTitle = new JLabel("Academic & Teaching Operations");
        sectionTitle.setFont(UITheme.FONT_SECTION_TITLE);
        sectionTitle.setForeground(UITheme.TEXT_MAIN);
        sectionTitle.setBorder(new EmptyBorder(4, 4, 12, 4));

        // 4. Modules Grid (2x2)
        JPanel modulesGrid = new JPanel(new GridLayout(2, 2, 18, 18));
        modulesGrid.setOpaque(false);

        modulesGrid.add(createModuleCard("Course Materials",
                "Upload lecture slides, tutorial sheets, assignment specifications, and syllabus guidelines for assigned courses.",
                UITheme.TEAL, "Upload Materials"));

        modulesGrid.add(createModuleCard("Assessment & Exam Marks",
                "Enter, edit, and calculate Continuous Assessment (CA) components, quizzes, midterms, and End-Semester final marks.",
                UITheme.ACCENT_BLUE, "Enter Marks"));

        modulesGrid.add(createModuleCard("Student Eligibility & GPA",
                "Verify exam eligibility based on CA >= 40% and 80% attendance. Review automated UGC circular grade points and SGPA.",
                UITheme.PURPLE, "Review Eligibility"));

        modulesGrid.add(createModuleCard("Attendance & Medical Records",
                "Audit undergraduate student attendance percentages and inspect approved medical certificates for missed sessions.",
                UITheme.WARNING_DARK, "View Attendance"));

        mainContent.add(bannerCard);
        mainContent.add(statsGrid);
        mainContent.add(sectionTitle);
        mainContent.add(modulesGrid);

        add(UITheme.createModernScrollPane(mainContent), BorderLayout.CENTER);
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
        actionBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Lecturer module [" + title + "] is configured and ready.",
                    title,
                    JOptionPane.INFORMATION_MESSAGE);
        });
        bottomRow.add(actionBtn);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(descLbl, BorderLayout.CENTER);
        card.add(bottomRow, BorderLayout.SOUTH);
=======
        setSize(950, 600);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
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
>>>>>>> test

        return card;
    }
}
