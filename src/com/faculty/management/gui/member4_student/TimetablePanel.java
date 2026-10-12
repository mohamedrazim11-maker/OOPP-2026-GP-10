package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.TimetableEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Modern Student Timetable View:
 * Weekly lecture and laboratory schedule filtered to enrolled courses
 * with session badges, venue indicators, and semester picker.
 */
public class TimetablePanel extends JPanel {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a");

    private final UndergraduateController controller;
    private final int studentId;
    private final SemesterSelector semesterSelector;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel sessionCountBadge;

    public TimetablePanel(UndergraduateController controller, int studentId) {
        this.controller = controller;
        this.studentId = studentId;
        this.semesterSelector = new SemesterSelector(controller, studentId);
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(20, 24, 20, 24));
        buildUi();
        loadTimetable();
    }

    private void buildUi() {
        // 1. Heading & Controls Card
        UITheme.ModernCard topCard = new UITheme.ModernCard(14);
        topCard.setLayout(new BorderLayout(16, 12));
        topCard.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel heading = new JLabel("Class & Laboratory Timetable");
        heading.setFont(UITheme.FONT_PAGE_TITLE);
        heading.setForeground(UITheme.TEXT_MAIN);

        JLabel subheading = new JLabel("Weekly timetable schedule mapped to your enrolled modules for the chosen semester.");
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
        semesterSelector.getComboBox().addActionListener(e -> loadTimetable());

        sessionCountBadge = new JLabel("0 sessions scheduled");
        sessionCountBadge.setFont(UITheme.FONT_SMALL_BOLD);
        sessionCountBadge.setForeground(UITheme.PRIMARY);
        filterStrip.add(Box.createHorizontalStrut(12));
        filterStrip.add(sessionCountBadge);

        topCard.add(titleBox, BorderLayout.NORTH);
        topCard.add(filterStrip, BorderLayout.SOUTH);

        // 2. Timetable Table Setup
        String[] columns = {"Day of Week", "Time Slot", "Course Module", "Lecturer / Instructor", "Session Type", "Allocated Venue"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);

        // Session Type badge renderer
        table.getColumnModel().getColumn(4).setCellRenderer(new UITheme.StatusBadgeRenderer());

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

        add(topCard, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadTimetable() {
        if (!semesterSelector.hasSelection()) {
            statusLabel.setText("No enrollment history found — nothing to schedule yet.");
            sessionCountBadge.setText("No sessions");
            return;
        }
        try {
            int semesterNumber = semesterSelector.getSelectedSemesterNumber();
            String academicYear = semesterSelector.getSelectedAcademicYear();
            List<TimetableEntry> entries = controller.getTimetable(studentId, semesterNumber, academicYear);
            tableModel.setRowCount(0);

            for (TimetableEntry entry : entries) {
                tableModel.addRow(new Object[]{
                        capitalize(entry.getDayOfWeek()),
                        formatTime(entry),
                        entry.getCourse().getCourseCode() + " — " + entry.getCourse().getCourseName(),
                        entry.getLecturerName() != null ? entry.getLecturerName() : "TBA",
                        entry.isPractical() ? "Practical" : "Theory",
                        "📍 " + entry.getVenue()
                });
            }

            sessionCountBadge.setText(entries.size() + " session(s) this semester");
            statusLabel.setText(entries.isEmpty()
                    ? "No timetable entries published yet for Semester " + semesterNumber + " (" + academicYear + ")."
                    : "Showing " + entries.size() + " published session(s) for Semester " + semesterNumber + " (" + academicYear + ").");
        } catch (DatabaseException e) {
            statusLabel.setForeground(UITheme.DANGER_DARK);
            statusLabel.setText("Failed to load timetable: " + e.getMessage());
        }
    }

    private String formatTime(TimetableEntry entry) {
        if (entry.getStartTime() == null || entry.getEndTime() == null) {
            return "-";
        }
        return entry.getStartTime().format(TIME_FORMAT) + " - " + entry.getEndTime().format(TIME_FORMAT);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}
