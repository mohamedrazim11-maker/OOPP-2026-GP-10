package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.exception.BusinessRuleException;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Grade;
import com.faculty.management.model.Result;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Modern Results Screen with two sub-tabs:
 *   - Individual Result: Course | Final Mark | Grade | Grade Point | Credit (+ SGPA)
 *   - Batch Result: Student | SGPA | Credits Completed | Credits Attempted, for one semester
 *
 * "Publish / Refresh" recomputes every course grade for the chosen semester
 * and persists the SGPA snapshot into the database results table.
 */
public class ResultPanel extends JPanel {

    private final UndergraduateController controller;
    private final int studentId;

    public ResultPanel(UndergraduateController controller, int studentId) {
        this.controller = controller;
        this.studentId = studentId;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel topBox = new JPanel();
        topBox.setLayout(new BoxLayout(topBox, BoxLayout.Y_AXIS));
        topBox.setOpaque(false);
        topBox.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel heading = new JLabel("Official Examination Results");
        heading.setFont(UITheme.FONT_PAGE_TITLE);
        heading.setForeground(UITheme.TEXT_MAIN);

        JLabel subheading = new JLabel("View individual semester grade transcripts or review batch-wide performance standings.");
        subheading.setFont(UITheme.FONT_BODY);
        subheading.setForeground(UITheme.TEXT_MUTED);

        topBox.add(heading);
        topBox.add(Box.createVerticalStrut(4));
        topBox.add(subheading);

        JTabbedPane innerTabs = new JTabbedPane();
        UITheme.styleTabbedPane(innerTabs);
        innerTabs.addTab("📄  Individual Transcript", buildIndividualTab());
        innerTabs.addTab("👥  Batch Performance Ranking", buildBatchTab());

        add(topBox, BorderLayout.NORTH);
        add(innerTabs, BorderLayout.CENTER);
    }

    // ---------------------------------------------------------------
    // 1. INDIVIDUAL RESULT TAB
    // ---------------------------------------------------------------
    private JPanel buildIndividualTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        SemesterSelector semesterSelector = new SemesterSelector(controller, studentId);

        // Filter Card
        UITheme.ModernCard filterCard = new UITheme.ModernCard(12);
        filterCard.setLayout(new BorderLayout(14, 0));
        filterCard.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel leftFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilter.setOpaque(false);
        JLabel semLabel = new JLabel("📅  Select Semester:");
        semLabel.setFont(UITheme.FONT_BODY_BOLD);
        semLabel.setForeground(UITheme.TEXT_MAIN);
        leftFilter.add(semLabel);
        leftFilter.add(semesterSelector.getComboBox());

        UITheme.ModernButton publishButton = new UITheme.ModernButton("🔄  Publish / Refresh Result", UITheme.ButtonStyle.PRIMARY, 8);
        publishButton.setFont(UITheme.FONT_SMALL_BOLD);

        filterCard.add(leftFilter, BorderLayout.WEST);
        filterCard.add(publishButton, BorderLayout.EAST);

        // Table
        String[] columns = {"Course Module", "Final Exam Mark", "Letter Grade", "Grade Point Value", "Academic Credit"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(2).setCellRenderer(new UITheme.GradeBadgeRenderer());

        JScrollPane scrollPane = UITheme.createModernScrollPane(table);

        // Status & Summary Strip
        JPanel bottomStrip = new JPanel(new BorderLayout());
        bottomStrip.setOpaque(false);
        bottomStrip.setBorder(new EmptyBorder(10, 4, 0, 4));

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_BODY_BOLD);
        statusLabel.setForeground(UITheme.TEXT_MUTED);

        JLabel summaryLabel = new JLabel(" ");
        summaryLabel.setFont(UITheme.FONT_KPI_VALUE);
        summaryLabel.setForeground(UITheme.PRIMARY);

        bottomStrip.add(statusLabel, BorderLayout.WEST);
        bottomStrip.add(summaryLabel, BorderLayout.EAST);

        Runnable loadAction = () -> {
            if (!semesterSelector.hasSelection()) {
                statusLabel.setText("No enrollment history found.");
                summaryLabel.setText("");
                return;
            }
            try {
                int semesterNumber = semesterSelector.getSelectedSemesterNumber();
                String academicYear = semesterSelector.getSelectedAcademicYear();
                List<Grade> grades = controller.getGrades(studentId, semesterNumber, academicYear);
                tableModel.setRowCount(0);

                for (Grade g : grades) {
                    tableModel.addRow(new Object[]{
                            g.getCourse().getCourseCode() + " — " + g.getCourse().getCourseName(),
                            g.isCaEligible() ? String.format("%.1f", g.getFinalTotalMarks()) : "N/A",
                            g.getGradeLetter(),
                            String.format("%.2f", g.getGradePoint()),
                            g.getCourse().getCredits() + " Credits"
                    });
                }

                double sgpa = controller.getSgpa(studentId, semesterNumber, academicYear);
                summaryLabel.setText(grades.isEmpty() ? "" : String.format("Semester SGPA: %.3f", sgpa));
                statusLabel.setText(grades.size() + " module(s) evaluated for Semester " + semesterNumber);
            } catch (DatabaseException e) {
                statusLabel.setForeground(UITheme.DANGER_DARK);
                statusLabel.setText("Failed to load result: " + e.getMessage());
            }
        };

        semesterSelector.getComboBox().addActionListener(e -> loadAction.run());
        publishButton.addActionListener(e -> {
            if (!semesterSelector.hasSelection()) return;
            try {
                Result result = controller.publishResult(studentId, semesterSelector.getSelectedSemesterNumber(), semesterSelector.getSelectedAcademicYear());
                statusLabel.setForeground(UITheme.SUCCESS_DARK);
                statusLabel.setText("✓ Result successfully published! Stored SGPA = " + String.format("%.3f", result.getSgpa()));
                loadAction.run();
            } catch (DatabaseException | BusinessRuleException ex) {
                statusLabel.setForeground(UITheme.DANGER_DARK);
                statusLabel.setText("Failed to publish result: " + ex.getMessage());
            }
        });

        loadAction.run();

        panel.add(filterCard, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomStrip, BorderLayout.SOUTH);

        return panel;
    }

    // ---------------------------------------------------------------
    // 2. BATCH RESULT TAB
    // ---------------------------------------------------------------
    private JPanel buildBatchTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        SemesterSelector semesterSelector = new SemesterSelector(controller, studentId);

        UITheme.ModernCard filterCard = new UITheme.ModernCard(12);
        filterCard.setLayout(new BorderLayout(14, 0));
        filterCard.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel leftFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilter.setOpaque(false);
        JLabel semLabel = new JLabel("📅  Select Semester:");
        semLabel.setFont(UITheme.FONT_BODY_BOLD);
        semLabel.setForeground(UITheme.TEXT_MAIN);
        leftFilter.add(semLabel);
        leftFilter.add(semesterSelector.getComboBox());

        UITheme.PillBadge noteBadge = new UITheme.PillBadge("BATCH-WIDE PERFORMANCE", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, UITheme.PRIMARY_TINT);
        filterCard.add(leftFilter, BorderLayout.WEST);
        filterCard.add(noteBadge, BorderLayout.EAST);

        String[] columns = {"Rank / Student Name", "Semester SGPA", "Credits Completed", "Credits Attempted", "Academic Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(4).setCellRenderer(new UITheme.StatusBadgeRenderer());

        JScrollPane scrollPane = UITheme.createModernScrollPane(table);

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setBorder(new EmptyBorder(8, 4, 0, 0));

        Runnable loadBatch = () -> {
            if (!semesterSelector.hasSelection()) {
                statusLabel.setText("No enrollment history found.");
                return;
            }
            try {
                int semesterNumber = semesterSelector.getSelectedSemesterNumber();
                String academicYear = semesterSelector.getSelectedAcademicYear();
                List<Result> batch = controller.getBatchResults(semesterNumber, academicYear);
                tableModel.setRowCount(0);

                int rank = 1;
                for (Result r : batch) {
                    String studentName = r.getStudentName() != null ? r.getStudentName() : ("Student #" + r.getStudentId());
                    String statusText = r.getSgpa() >= 2.0 ? "PASSED" : "ACADEMIC WARNING";

                    tableModel.addRow(new Object[]{
                            "#" + rank + "  " + studentName,
                            String.format("%.3f", r.getSgpa()),
                            r.getCreditsCompleted() + " Credits",
                            r.getCreditsAttempted() + " Credits",
                            statusText
                    });
                    rank++;
                }

                statusLabel.setText(batch.isEmpty()
                        ? "No results published yet for Semester " + semesterNumber + ". Use 'Publish / Refresh Result' on the Individual Transcript tab first."
                        : "Showing " + batch.size() + " published student record(s) for Semester " + semesterNumber + " (" + academicYear + ").");
            } catch (DatabaseException e) {
                statusLabel.setForeground(UITheme.DANGER_DARK);
                statusLabel.setText("Failed to load batch results: " + e.getMessage());
            }
        };

        semesterSelector.getComboBox().addActionListener(e -> loadBatch.run());
        loadBatch.run();

        panel.add(filterCard, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }
}
