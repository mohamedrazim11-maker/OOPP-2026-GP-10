package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LoginController;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern Lecturer Dashboard GUI.
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

        statsGrid.add(UITheme.createStatCard("📖", "Assigned Courses", "3 Modules", UITheme.TEAL));
        statsGrid.add(UITheme.createStatCard("📝", "Marks Submitted", "Continuous Assess.", UITheme.ACCENT_BLUE));
        statsGrid.add(UITheme.createStatCard("🎯", "Eligibility Status", "40% CA Threshold", UITheme.PURPLE));
        statsGrid.add(UITheme.createStatCard("📊", "Attendance Ratio", "80% Required", UITheme.WARNING_DARK));

        // 3. Section Title
        JLabel sectionTitle = new JLabel("Academic & Teaching Operations");
        sectionTitle.setFont(UITheme.FONT_SECTION_TITLE);
        sectionTitle.setForeground(UITheme.TEXT_MAIN);
        sectionTitle.setBorder(new EmptyBorder(4, 4, 12, 4));

        // 4. Modules Grid (2x2)
        JPanel modulesGrid = new JPanel(new GridLayout(2, 2, 18, 18));
        modulesGrid.setOpaque(false);

        modulesGrid.add(createModuleCard("📄 Course Materials",
                "Upload lecture slides, tutorial sheets, assignment specifications, and syllabus guidelines for assigned courses.",
                UITheme.TEAL, "Upload Materials"));

        modulesGrid.add(createModuleCard("📝 Assessment & Exam Marks",
                "Enter, edit, and calculate Continuous Assessment (CA) components, quizzes, midterms, and End-Semester final marks.",
                UITheme.ACCENT_BLUE, "Enter Marks"));

        modulesGrid.add(createModuleCard("🎯 Student Eligibility & GPA",
                "Verify exam eligibility based on CA >= 40% and 80% attendance. Review automated UGC circular grade points and SGPA.",
                UITheme.PURPLE, "Review Eligibility"));

        modulesGrid.add(createModuleCard("📊 Attendance & Medical Records",
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
        UITheme.ModernButton actionBtn = new UITheme.ModernButton(actionText + " →", UITheme.ButtonStyle.OUTLINE, 8);
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

        return card;
    }
}
