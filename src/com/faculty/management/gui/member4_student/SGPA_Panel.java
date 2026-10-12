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
 * Modern SGPA Breakdown View:
 * Mathematically transparent demonstration of Semester Grade Point Average:
 *   SGPA = Σ(Credit × Grade Point) / Σ Credits
 * Displays individual course contributions and total weighted points.
 */
public class SGPA_Panel extends JPanel {

    private final UndergraduateController controller;
    private final int studentId;
    private final SemesterSelector semesterSelector;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel sgpaHeroValue;
    private JLabel totalCreditsBadge;
    private JLabel totalWeightedPointsBadge;

    public SGPA_Panel(UndergraduateController controller, int studentId) {
        this.controller = controller;
        this.studentId = studentId;
        this.semesterSelector = new SemesterSelector(controller, studentId);
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(20, 24, 20, 24));
        buildUi();
        loadSgpa();
    }

    private void buildUi() {
        // 1. Top Section (Formula Hero Card + Controls)
        UITheme.ModernCard heroCard = new UITheme.ModernCard(14);
        heroCard.setLayout(new BorderLayout(20, 16));
        heroCard.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel leftBox = new JPanel();
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
        leftBox.setOpaque(false);

        JLabel heading = new JLabel("SGPA — Semester Grade Point Average");
        heading.setFont(UITheme.FONT_PAGE_TITLE);
        heading.setForeground(UITheme.TEXT_MAIN);

        JLabel formula = new JLabel("Formula:  SGPA = Σ (Credit × Grade Point)  /  Σ Total Credits");
        formula.setFont(UITheme.FONT_BODY_BOLD);
        formula.setForeground(UITheme.PRIMARY);
        formula.setBorder(new EmptyBorder(4, 0, 12, 0));

        // Semester Selector Strip
        JPanel filterStrip = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterStrip.setOpaque(false);
        JLabel semLabel = new JLabel("📅  Semester:");
        semLabel.setFont(UITheme.FONT_BODY_BOLD);
        semLabel.setForeground(UITheme.TEXT_MAIN);

        filterStrip.add(semLabel);
        filterStrip.add(semesterSelector.getComboBox());
        semesterSelector.getComboBox().addActionListener(e -> loadSgpa());

        leftBox.add(heading);
        leftBox.add(formula);
        leftBox.add(filterStrip);

        // Right Hero SGPA Box
        JPanel rightHeroBox = new JPanel();
        rightHeroBox.setLayout(new BoxLayout(rightHeroBox, BoxLayout.Y_AXIS));
        rightHeroBox.setBackground(UITheme.PRIMARY_LIGHT);
        rightHeroBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.PRIMARY_TINT, 1),
                new EmptyBorder(12, 24, 12, 24)
        ));

        JLabel heroTitle = new JLabel("SEMESTER SGPA");
        heroTitle.setFont(UITheme.FONT_SMALL_BOLD);
        heroTitle.setForeground(UITheme.PRIMARY);
        heroTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        sgpaHeroValue = new JLabel("0.000");
        sgpaHeroValue.setFont(UITheme.FONT_KPI_HERO);
        sgpaHeroValue.setForeground(UITheme.PRIMARY);
        sgpaHeroValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightHeroBox.add(heroTitle);
        rightHeroBox.add(Box.createVerticalStrut(4));
        rightHeroBox.add(sgpaHeroValue);

        heroCard.add(leftBox, BorderLayout.WEST);
        heroCard.add(rightHeroBox, BorderLayout.EAST);

        // 2. Stat Chips Bar
        JPanel chipsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        chipsBar.setOpaque(false);
        chipsBar.setBorder(new EmptyBorder(14, 0, 14, 0));

        totalCreditsBadge = new JLabel("Σ Credits: 0");
        totalCreditsBadge.setFont(UITheme.FONT_BODY_BOLD);
        totalCreditsBadge.setForeground(UITheme.TEXT_BODY);

        totalWeightedPointsBadge = new JLabel("Σ (Credit × GP): 0.00");
        totalWeightedPointsBadge.setFont(UITheme.FONT_BODY_BOLD);
        totalWeightedPointsBadge.setForeground(UITheme.SUCCESS_DARK);

        chipsBar.add(new UITheme.PillBadge("WEIGHTED CALCULATION BREAKDOWN", UITheme.BG_HEADER, UITheme.TEXT_BODY, UITheme.BORDER_LIGHT));
        chipsBar.add(totalCreditsBadge);
        chipsBar.add(totalWeightedPointsBadge);

        // 3. Table Setup
        String[] columns = {"Course Module", "Credits (Ci)", "Letter Grade", "Grade Point (GPi)", "Weighted Contribution (Ci × GPi)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(2).setCellRenderer(new UITheme.GradeBadgeRenderer());

        JScrollPane scrollPane = UITheme.createModernScrollPane(table);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setBorder(new EmptyBorder(8, 4, 0, 0));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(chipsBar, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(statusLabel, BorderLayout.SOUTH);

        add(heroCard, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadSgpa() {
        if (!semesterSelector.hasSelection()) {
            statusLabel.setText("No enrollment history found.");
            sgpaHeroValue.setText("0.000");
            totalCreditsBadge.setText("Σ Credits: 0");
            totalWeightedPointsBadge.setText("Σ (Credit × GP): 0.00");
            return;
        }
        try {
            int semesterNumber = semesterSelector.getSelectedSemesterNumber();
            String academicYear = semesterSelector.getSelectedAcademicYear();
            List<Grade> grades = controller.getGrades(studentId, semesterNumber, academicYear);
            tableModel.setRowCount(0);

            int totalCredits = 0;
            double totalWeightedPoints = 0.0;

            for (Grade g : grades) {
                int credits = g.getCourse().getCredits();
                totalCredits += credits;
                double points = g.countsTowardGpa() ? (credits * g.getGradePoint()) : 0.0;
                totalWeightedPoints += points;

                String contribution = g.countsTowardGpa() ? String.format("%.2f", points) : "Excluded (NE)";
                tableModel.addRow(new Object[]{
                        g.getCourse().getCourseCode() + " — " + g.getCourse().getCourseName(),
                        credits + " Credits",
                        g.getGradeLetter(),
                        String.format("%.2f", g.getGradePoint()),
                        contribution
                });
            }

            double sgpa = controller.getSgpa(studentId, semesterNumber, academicYear);
            sgpaHeroValue.setText(grades.isEmpty() ? "0.000" : String.format("%.3f", sgpa));
            totalCreditsBadge.setText("Σ Credits: " + totalCredits);
            totalWeightedPointsBadge.setText("Σ (Credit × GP): " + String.format("%.2f", totalWeightedPoints));

            statusLabel.setText(grades.isEmpty()
                    ? "No marks entered for this semester yet."
                    : "Semester " + semesterNumber + " (" + academicYear + ") — " + grades.size() + " graded modules.");
        } catch (DatabaseException e) {
            statusLabel.setForeground(UITheme.DANGER_DARK);
            statusLabel.setText("Failed to load SGPA: " + e.getMessage());
        }
    }
}
