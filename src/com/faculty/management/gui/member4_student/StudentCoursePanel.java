package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Enrollment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Modern Course Enrolment View:
 * Shows every course a student is enrolled in across academic semesters,
 * with search filter, stat chips, and custom table renderers.
 */
public class StudentCoursePanel extends JPanel {

    private final UndergraduateController controller;
    private final int studentId;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel totalCoursesLbl;
    private JLabel totalCreditsLbl;
    private JLabel practicalCoursesLbl;
    private JLabel statusLabel;
    private UITheme.ModernTextField searchField;

    public StudentCoursePanel(UndergraduateController controller, int studentId) {
        this.controller = controller;
        this.studentId = studentId;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(20, 24, 20, 24));
        buildUi();
        loadCourses();
    }

    private void buildUi() {
        // 1. Heading & Stats
        JPanel topBox = new JPanel();
        topBox.setLayout(new BoxLayout(topBox, BoxLayout.Y_AXIS));
        topBox.setOpaque(false);
        topBox.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel heading = new JLabel("Enrolled Course Modules");
        heading.setFont(UITheme.FONT_PAGE_TITLE);
        heading.setForeground(UITheme.TEXT_MAIN);

        JLabel subheading = new JLabel("Comprehensive curriculum catalog of all course modules registered across semesters.");
        subheading.setFont(UITheme.FONT_BODY);
        subheading.setForeground(UITheme.TEXT_MUTED);

        topBox.add(heading);
        topBox.add(Box.createVerticalStrut(4));
        topBox.add(subheading);

        // Stat Cards Bar
        JPanel statsBar = new JPanel(new GridLayout(1, 3, 16, 0));
        statsBar.setOpaque(false);
        statsBar.setBorder(new EmptyBorder(12, 0, 14, 0));

        totalCoursesLbl = new JLabel("0 Modules");
        totalCreditsLbl = new JLabel("0 Credits");
        practicalCoursesLbl = new JLabel("0 Labs");

        statsBar.add(UITheme.createStatCard("📚", "Enrolled Courses", totalCoursesLbl, UITheme.PRIMARY));
        statsBar.add(UITheme.createStatCard("🎓", "Cumulative Credits", totalCreditsLbl, UITheme.SUCCESS_DARK));
        statsBar.add(UITheme.createStatCard("🔬", "Practical Courses", practicalCoursesLbl, UITheme.PURPLE));

        // 2. Search & Filter Bar
        JPanel searchBar = new JPanel(new BorderLayout(12, 0));
        searchBar.setOpaque(false);
        searchBar.setBorder(new EmptyBorder(0, 0, 12, 0));

        searchField = new UITheme.ModernTextField("🔍  Quick search course code, title, or semester...", 25);
        searchField.setPreferredSize(new Dimension(320, 38));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });

        statusLabel = new JLabel("Loading course catalog...");
        statusLabel.setFont(UITheme.FONT_SMALL_BOLD);
        statusLabel.setForeground(UITheme.TEXT_MUTED);

        searchBar.add(searchField, BorderLayout.WEST);
        searchBar.add(statusLabel, BorderLayout.EAST);

        topBox.add(statsBar);
        topBox.add(searchBar);

        // 3. Table Setup
        String[] columns = {"Course Code", "Course Name", "Credits", "Theory Hours", "Practical Hours", "Semester", "Academic Year", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Column Renderers
        table.getColumnModel().getColumn(7).setCellRenderer(new UITheme.StatusBadgeRenderer());

        JScrollPane scrollPane = UITheme.createModernScrollPane(table);

        add(topBox, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void applyFilter() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void loadCourses() {
        try {
            List<Enrollment> enrollments = controller.getCourseDetails(studentId);
            tableModel.setRowCount(0);

            int totalCredits = 0;
            int practicalCount = 0;

            for (Enrollment e : enrollments) {
                int credits = e.getCourse().getCredits();
                totalCredits += credits;
                if (e.getCourse().hasPractical()) {
                    practicalCount++;
                }

                tableModel.addRow(new Object[]{
                        e.getCourse().getCourseCode(),
                        e.getCourse().getCourseName(),
                        credits + " Credits",
                        e.getCourse().getTheorySessions() + " hrs",
                        e.getCourse().hasPractical() ? (e.getCourse().getPracticalSessions() + " hrs") : "None",
                        "Semester " + e.getSemesterNumber(),
                        e.getAcademicYear(),
                        e.getStatus()
                });
            }

            totalCoursesLbl.setText(enrollments.size() + " Modules");
            totalCreditsLbl.setText(totalCredits + " Credits");
            practicalCoursesLbl.setText(practicalCount + " Labs");
            statusLabel.setText("Showing " + enrollments.size() + " active course enrolment(s)");
        } catch (DatabaseException e) {
            statusLabel.setForeground(UITheme.DANGER_DARK);
            statusLabel.setText("Failed to load courses: " + e.getMessage());
        }
    }
}
