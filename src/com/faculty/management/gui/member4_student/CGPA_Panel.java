package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Result;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Modern CGPA View:
 * Cumulative Grade Point Average pooled across all completed academic semesters:
 *   CGPA = Σ(Credit × Grade Point) / Σ Credits
 * Displays degree honours classification, semester trends, and cumulative credit stats.
 */
public class CGPA_Panel extends JPanel {

    private final UndergraduateController controller;
    private final int studentId;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel cgpaHeroValue;
    private UITheme.PillBadge honoursClassBadge;

    public CGPA_Panel(UndergraduateController controller, int studentId) {
        this.controller = controller;
        this.studentId = studentId;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(20, 24, 20, 24));
        buildUi();
        loadCgpa();
    }

    private void buildUi() {
        // 1. Hero CGPA Card
        UITheme.ModernCard heroCard = new UITheme.ModernCard(14);
        heroCard.setLayout(new BorderLayout(20, 16));
        heroCard.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel leftBox = new JPanel();
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
        leftBox.setOpaque(false);

        JLabel heading = new JLabel("CGPA — Cumulative Grade Point Average");
        heading.setFont(UITheme.FONT_PAGE_TITLE);
        heading.setForeground(UITheme.TEXT_MAIN);

        JLabel formula = new JLabel("Cumulative Formula:  CGPA = Σ_all (Credit × Grade Point)  /  Σ_all Credits");
        formula.setFont(UITheme.FONT_BODY_BOLD);
        formula.setForeground(UITheme.PRIMARY);
        formula.setBorder(new EmptyBorder(4, 0, 10, 0));

        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        badgeRow.setOpaque(false);

        honoursClassBadge = new UITheme.PillBadge("EVALUATING CLASSIFICATION", UITheme.BG_HEADER, UITheme.TEXT_BODY, UITheme.BORDER_LIGHT);
        honoursClassBadge.setFont(UITheme.FONT_BODY_BOLD);

        UITheme.ModernButton refreshButton = new UITheme.ModernButton("🔄  Refresh CGPA", UITheme.ButtonStyle.SECONDARY, 8);
        refreshButton.setFont(UITheme.FONT_SMALL_BOLD);
        refreshButton.addActionListener(e -> loadCgpa());

        badgeRow.add(honoursClassBadge);
        badgeRow.add(Box.createHorizontalStrut(8));
        badgeRow.add(refreshButton);

        leftBox.add(heading);
        leftBox.add(formula);
        leftBox.add(badgeRow);

        // Right Big CGPA Display
        JPanel rightHeroBox = new JPanel();
        rightHeroBox.setLayout(new BoxLayout(rightHeroBox, BoxLayout.Y_AXIS));
        rightHeroBox.setBackground(UITheme.SUCCESS_LIGHT);
        rightHeroBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.SUCCESS_BORDER, 1),
                new EmptyBorder(12, 28, 12, 28)
        ));

        JLabel heroTitle = new JLabel("CUMULATIVE CGPA");
        heroTitle.setFont(UITheme.FONT_SMALL_BOLD);
        heroTitle.setForeground(UITheme.SUCCESS_DARK);
        heroTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        cgpaHeroValue = new JLabel("0.000");
        cgpaHeroValue.setFont(UITheme.FONT_KPI_HERO);
        cgpaHeroValue.setForeground(UITheme.SUCCESS_DARK);
        cgpaHeroValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightHeroBox.add(heroTitle);
        rightHeroBox.add(Box.createVerticalStrut(4));
        rightHeroBox.add(cgpaHeroValue);

        heroCard.add(leftBox, BorderLayout.WEST);
        heroCard.add(rightHeroBox, BorderLayout.EAST);

        // 2. Semester History Table
        String[] columns = {"Academic Year", "Academic Semester", "Semester SGPA", "Credits Completed", "Credits Attempted", "Academic Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(5).setCellRenderer(new UITheme.StatusBadgeRenderer());

        JScrollPane scrollPane = UITheme.createModernScrollPane(table);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setBorder(new EmptyBorder(8, 4, 0, 0));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(statusLabel, BorderLayout.SOUTH);

        add(heroCard, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadCgpa() {
        try {
            List<Result> history = controller.getResultHistory(studentId);
            tableModel.setRowCount(0);

            for (Result r : history) {
                String standing = r.getSgpa() >= 2.0 ? "GOOD STANDING" : "PROBATION";
                tableModel.addRow(new Object[]{
                        r.getAcademicYear(),
                        "Semester " + r.getSemesterNumber(),
                        String.format("%.3f", r.getSgpa()),
                        r.getCreditsCompleted() + " Credits",
                        r.getCreditsAttempted() + " Credits",
                        standing
                });
            }

            double cgpa = controller.getCgpa(studentId);
            cgpaHeroValue.setText(history.isEmpty() ? "0.000" : String.format("%.3f", cgpa));
            updateHonoursBadge(cgpa, history.isEmpty());

            statusLabel.setText(history.isEmpty()
                    ? "No published results found yet. Publish a semester result from the Results tab first."
                    : "Cumulative standing calculated from " + history.size() + " published semester results.");
        } catch (DatabaseException e) {
            statusLabel.setForeground(UITheme.DANGER_DARK);
            statusLabel.setText("Failed to load CGPA: " + e.getMessage());
        }
    }

    private void updateHonoursBadge(double cgpa, boolean empty) {
        if (empty) {
            honoursClassBadge.setText("NO RESULTS PUBLISHED");
            honoursClassBadge.setColors(UITheme.BG_HEADER, UITheme.TEXT_MUTED, UITheme.BORDER_LIGHT);
            return;
        }

        if (cgpa >= 3.70) {
            honoursClassBadge.setText("🏆  FIRST CLASS HONOURS (CGPA >= 3.70)");
            honoursClassBadge.setColors(UITheme.SUCCESS_LIGHT, UITheme.SUCCESS_DARK, UITheme.SUCCESS_BORDER);
        } else if (cgpa >= 3.30) {
            honoursClassBadge.setText("🎖️  SECOND CLASS UPPER DIVISION (CGPA >= 3.30)");
            honoursClassBadge.setColors(UITheme.ACCENT_BLUE_LIGHT, UITheme.ACCENT_BLUE, new Color(191, 219, 254));
        } else if (cgpa >= 3.00) {
            honoursClassBadge.setText("🎖️  SECOND CLASS LOWER DIVISION (CGPA >= 3.00)");
            honoursClassBadge.setColors(UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, UITheme.PRIMARY_TINT);
        } else if (cgpa >= 2.00) {
            honoursClassBadge.setText("🎓  GENERAL PASS (CGPA >= 2.00)");
            honoursClassBadge.setColors(UITheme.BG_HEADER, UITheme.TEXT_MAIN, UITheme.BORDER_LIGHT);
        } else {
            honoursClassBadge.setText("⚠️  ACADEMIC PROBATION (CGPA < 2.00)");
            honoursClassBadge.setColors(UITheme.DANGER_LIGHT, UITheme.DANGER_DARK, UITheme.DANGER_BORDER);
        }
    }
}
