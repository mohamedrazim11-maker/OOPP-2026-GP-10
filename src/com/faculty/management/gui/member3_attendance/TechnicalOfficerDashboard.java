package com.faculty.management.gui.member3_attendance;

import com.faculty.management.controller.LoginController;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern Technical Officer Dashboard GUI.
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
        setSize(1020, 680);
        setMinimumSize(new Dimension(880, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_PAGE);

        // Standardized Modern Header
        add(UITheme.buildHeader(this, "Technical Operations Portal", currentUser.getFullName(), "Tech Officer", () -> {
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

        JLabel roleDescLabel = new JLabel("Technical Officer Responsibilities: Daily Attendance, Medical Submissions, and Lab Schedules.");
        roleDescLabel.setFont(UITheme.FONT_BODY);
        roleDescLabel.setForeground(UITheme.TEXT_MUTED);
        roleDescLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        bannerText.add(welcomeLabel);
        bannerText.add(roleDescLabel);

        JPanel badgeBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badgeBox.setOpaque(false);
        UITheme.PillBadge toBadge = new UITheme.PillBadge("OPERATIONS STAFF", UITheme.WARNING_LIGHT, UITheme.WARNING_DARK, UITheme.WARNING_BORDER);
        UITheme.PillBadge labBadge = new UITheme.PillBadge("LABS & SESSIONS", UITheme.SUCCESS_LIGHT, UITheme.SUCCESS_DARK, UITheme.SUCCESS_BORDER);
        badgeBox.add(toBadge);
        badgeBox.add(labBadge);

        bannerCard.add(bannerText, BorderLayout.WEST);
        bannerCard.add(badgeBox, BorderLayout.EAST);

        // 2. Stat Cards
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(18, 0, 18, 0));

        statsGrid.add(UITheme.createStatCard("📋", "Today's Sessions", "4 Active Labs", UITheme.WARNING_DARK));
        statsGrid.add(UITheme.createStatCard("🏥", "Medical Requests", "3 Pending Review", UITheme.DANGER));
        statsGrid.add(UITheme.createStatCard("📊", "Batch Attendance", "Overall 84.5%", UITheme.PRIMARY));
        statsGrid.add(UITheme.createStatCard("🗓️", "Lab Venues", "5 Department Labs", UITheme.SUCCESS));

        // 3. Section Title
        JLabel sectionTitle = new JLabel("Technical & Attendance Operations");
        sectionTitle.setFont(UITheme.FONT_SECTION_TITLE);
        sectionTitle.setForeground(UITheme.TEXT_MAIN);
        sectionTitle.setBorder(new EmptyBorder(4, 4, 12, 4));

        // 4. Modules Grid (2x2)
        JPanel modulesGrid = new JPanel(new GridLayout(2, 2, 18, 18));
        modulesGrid.setOpaque(false);

        modulesGrid.add(createModuleCard("📋 Attendance Entry",
                "Record and update daily undergraduate attendance for theory lectures and laboratory practical sessions.",
                UITheme.WARNING_DARK, "Log Attendance"));

        modulesGrid.add(createModuleCard("🏥 Medical Certificates",
                "Log medical submissions, inspect doctor certs, and update verification/approval status for excused absences.",
                UITheme.DANGER, "Review Medicals"));

        modulesGrid.add(createModuleCard("📊 Batch Attendance Summary",
                "View and compute aggregate semester attendance percentages by course, batch, and academic specialization.",
                UITheme.PRIMARY, "View Summaries"));

        modulesGrid.add(createModuleCard("🗓️ Department Timetables",
                "View departmental lab time slots, equipment allocation, and lecture timetable schedules.",
                UITheme.SUCCESS_DARK, "Check Schedules"));

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
                    "Technical Officer module [" + title + "] is configured and ready.",
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
