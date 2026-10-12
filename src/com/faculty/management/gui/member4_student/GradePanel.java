package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Grade;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Modern Grade Calculation View:
 * Marks -> CA Eligibility (>= 40%) -> Final Exam -> Letter Grade -> Grade Point.
 * Implements UGC Commission Circular No. 12-2024 with KPI stat cards.
 */
public class GradePanel extends JPanel {

    private final UndergraduateController controller;
    private final int studentId;
    private final SemesterSelector semesterSelector;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel sgpaValueCard;
    private JLabel creditsValueCard;
    private JLabel eligibleCountCard;

    public GradePanel(UndergraduateController controller, int studentId) {
        this.controller = controller;
        this.studentId = studentId;
        this.semesterSelector = new SemesterSelector(controller, studentId);
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(20, 24, 20, 24));
        buildUi();
        loadGrades();
    }

    private void buildUi() {
        // 1. Heading & Controls Card
        UITheme.ModernCard topCard = new UITheme.ModernCard(14);
        topCard.setLayout(new BorderLayout(16, 12));
        topCard.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel heading = new JLabel("Academic Grades & Assessment Scores");
        heading.setFont(UITheme.FONT_PAGE_TITLE);
        heading.setForeground(UITheme.TEXT_MAIN);

        JLabel subheading = new JLabel("Continuous Assessment (CA >= 40%) eligibility and letter grades per UGC Commission Circular No. 12-2024.");
        subheading.setFont(UITheme.FONT_BODY);
        subheading.setForeground(UITheme.TEXT_MUTED);

        titleBox.add(heading);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(subheading);

        // Filter Strip
        JPanel filterStrip = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterStrip.setOpaque(false);
        filterStrip.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel semLabel = new JLabel("📅  Select Semester:");
        semLabel.setFont(UITheme.FONT_BODY_BOLD);
        semLabel.setForeground(UITheme.TEXT_MAIN);

        filterStrip.add(semLabel);
        filterStrip.add(semesterSelector.getComboBox());
        semesterSelector.getComboBox().addActionListener(e -> loadGrades());

        UITheme.PillBadge ruleBadge = new UITheme.PillBadge("UGC CIRCULAR NO. 12-2024", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, UITheme.PRIMARY_TINT);
        filterStrip.add(Box.createHorizontalStrut(10));
        filterStrip.add(ruleBadge);

        topCard.add(titleBox, BorderLayout.NORTH);
        topCard.add(filterStrip, BorderLayout.SOUTH);

        // 2. KPI Summary Cards (SGPA, Credits, Eligibility)
        JPanel kpiGrid = new JPanel(new GridLayout(1, 3, 16, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.setBorder(new EmptyBorder(16, 0, 16, 0));

        sgpaValueCard = new JLabel("0.000");
        creditsValueCard = new JLabel("0 Credits");
        eligibleCountCard = new JLabel("0 Modules");

        kpiGrid.add(UITheme.createStatCard("🏆", "Semester SGPA", sgpaValueCard, UITheme.PRIMARY));
        kpiGrid.add(UITheme.createStatCard("🎓", "Semester Credits", creditsValueCard, UITheme.SUCCESS_DARK));
        kpiGrid.add(UITheme.createStatCard("✅", "Eligible Courses", eligibleCountCard, UITheme.ACCENT_BLUE));

        // 3. Table Setup
        String[] columns = {"Course Module", "Credits", "CA %", "Final Exam", "Total Mark", "CA Eligibility", "Letter Grade", "Grade Point"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);

        // Custom Cell Renderers for Badges
        table.getColumnModel().getColumn(5).setCellRenderer(new UITheme.StatusBadgeRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new UITheme.GradeBadgeRenderer());

        JScrollPane scrollPane = UITheme.createModernScrollPane(table);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setBorder(new EmptyBorder(8, 4, 0, 0));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(statusLabel, BorderLayout.SOUTH);

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setOpaque(false);
        contentWrapper.add(topCard);
        contentWrapper.add(kpiGrid);
        contentWrapper.add(centerPanel);

        add(contentWrapper, BorderLayout.CENTER);
    }

    private void loadGrades() {
        if (!semesterSelector.hasSelection()) {
            statusLabel.setText("No enrollment history found.");
            return;
        }
        try {
            int semesterNumber = semesterSelector.getSelectedSemesterNumber();
            String academicYear = semesterSelector.getSelectedAcademicYear();
            List<Grade> grades = controller.getGrades(studentId, semesterNumber, academicYear);
            tableModel.setRowCount(0);

            int totalCredits = 0;
            int eligibleCount = 0;

            for (Grade g : grades) {
                totalCredits += g.getCourse().getCredits();
                if (g.isCaEligible()) {
                    eligibleCount++;
                }

                tableModel.addRow(new Object[]{
                        g.getCourse().getCourseCode() + " — " + g.getCourse().getCourseName(),
                        g.getCourse().getCredits() + " Credits",
                        String.format("%.1f%%", g.getCaMarks()),
                        g.isCaEligible() ? String.format("%.1f", g.getFinalExamMarks()) : "N/A",
                        g.isCaEligible() ? String.format("%.1f", g.getFinalTotalMarks()) : "N/A",
                        g.isCaEligible() ? "Eligible" : "Not Eligible",
                        g.getGradeLetter(),
                        String.format("%.2f", g.getGradePoint())
                });
            }

            double sgpa = controller.getSgpa(studentId, semesterNumber, academicYear);
            sgpaValueCard.setText(String.format("%.3f", sgpa));
            creditsValueCard.setText(totalCredits + " Credits");
            eligibleCountCard.setText(eligibleCount + " / " + grades.size() + " Eligible");

            statusLabel.setText(grades.isEmpty()
                    ? "No marks entered for this semester yet."
                    : grades.size() + " course grade(s) computed for Semester " + semesterNumber + " (" + academicYear + ").");

        } catch (DatabaseException e) {
            statusLabel.setForeground(UITheme.DANGER_DARK);
            statusLabel.setText("Failed to load grades: " + e.getMessage());
        }
    }
}
