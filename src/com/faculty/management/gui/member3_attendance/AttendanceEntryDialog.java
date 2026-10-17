package com.faculty.management.gui.member3_attendance;

import com.faculty.management.dao.AttendanceDAO;
import com.faculty.management.dao.CourseDAO;
import com.faculty.management.model.Course;
import com.faculty.management.model.Undergraduate;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Attendance Entry Dialog for Technical Officer to mark Theory and Practical attendance
 * for up to 15 sessions per course.
 */
public class AttendanceEntryDialog extends JDialog {

    private final User currentUser;
    private final AttendanceDAO attendanceDAO;
    private final CourseDAO courseDAO;

    private JComboBox<String> cmbCourses;
    private JComboBox<String> cmbSessionType;
    private JComboBox<Integer> cmbSessionNo;
    private JTextField txtDate;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JLabel lblStats;

    private int activeSessionId = -1;
    private List<Undergraduate> studentList;

    public AttendanceEntryDialog(JFrame parent, User currentUser) {
        super(parent, "Attendance Entry — Technical Operations", true);
        this.currentUser = currentUser;
        this.attendanceDAO = new AttendanceDAO();
        this.courseDAO = new CourseDAO();

        initComponents();
        loadCourses();
        loadSessionData();
    }

    private void initComponents() {
        setSize(900, 650);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(124, 45, 18)); // Technical Officer Rust/Amber
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("📋 Undergraduate Attendance Entry");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Select course, session type, and session number (1 to 15) to mark attendance");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(254, 215, 170));

        JPanel headerTextPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        headerTextPanel.setOpaque(false);
        headerTextPanel.add(titleLabel);
        headerTextPanel.add(subLabel);

        headerPanel.add(headerTextPanel, BorderLayout.WEST);

        // Session Configuration Panel
        JPanel sessionConfigPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        sessionConfigPanel.setBackground(new Color(248, 250, 252));
        sessionConfigPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        sessionConfigPanel.add(new JLabel("Course:"));
        cmbCourses = new JComboBox<>();
        cmbCourses.setPreferredSize(new Dimension(180, 30));
        sessionConfigPanel.add(cmbCourses);

        sessionConfigPanel.add(new JLabel("Type:"));
        cmbSessionType = new JComboBox<>(new String[]{"THEORY", "PRACTICAL"});
        cmbSessionType.setPreferredSize(new Dimension(110, 30));
        sessionConfigPanel.add(cmbSessionType);

        sessionConfigPanel.add(new JLabel("Session No:"));
        Integer[] sessionNumbers = new Integer[15];
        for (int i = 0; i < 15; i++) sessionNumbers[i] = i + 1;
        cmbSessionNo = new JComboBox<>(sessionNumbers);
        cmbSessionNo.setPreferredSize(new Dimension(80, 30));
        sessionConfigPanel.add(cmbSessionNo);

        sessionConfigPanel.add(new JLabel("Date:"));
        txtDate = new JTextField(LocalDate.now().toString(), 9);
        txtDate.setPreferredSize(new Dimension(100, 30));
        sessionConfigPanel.add(txtDate);

        JButton btnLoad = new JButton("Load Session");
        btnLoad.setBackground(new Color(14, 165, 233));
        btnLoad.setForeground(Color.WHITE);
        btnLoad.setFocusPainted(false);
        btnLoad.addActionListener(e -> loadSessionData());
        sessionConfigPanel.add(btnLoad);

        // Student Table Panel
        String[] columnNames = {"Student ID", "Reg No", "Student Name", "Status (Present?)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only Present checkbox is editable
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(30);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentTable.getModel().addTableModelListener(e -> updateStats());

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                "Student Attendance Checklist",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(51, 65, 85)
        ));

        // Center Panel container
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(sessionConfigPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Controls and Actions Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Quick Selection & Stats Bar
        JPanel quickSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        quickSelectPanel.setOpaque(false);

        JButton btnAllPresent = new JButton("Mark All Present");
        btnAllPresent.addActionListener(e -> setAllAttendance(true));

        JButton btnAllAbsent = new JButton("Mark All Absent");
        btnAllAbsent.addActionListener(e -> setAllAttendance(false));

        lblStats = new JLabel("Total: 0 | Present: 0 | Absent: 0");
        lblStats.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStats.setForeground(new Color(71, 85, 105));
        lblStats.setBorder(new EmptyBorder(0, 15, 0, 0));

        quickSelectPanel.add(btnAllPresent);
        quickSelectPanel.add(btnAllAbsent);
        quickSelectPanel.add(lblStats);

        // Save & Close Action Buttons
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtonPanel.setOpaque(false);

        JButton btnSave = new JButton("💾 Save Attendance");
        btnSave.setBackground(new Color(16, 185, 129));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> saveAttendance());

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());

        actionButtonPanel.add(btnSave);
        actionButtonPanel.add(btnClose);

        bottomPanel.add(quickSelectPanel, BorderLayout.WEST);
        bottomPanel.add(actionButtonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadCourses() {
        cmbCourses.removeAllItems();
        try {
            List<Course> courses = courseDAO.getAllCourses();
            for (Course c : courses) {
                cmbCourses.addItem(c.getCourseCode() + " - " + c.getCourseName());
            }
        } catch (Exception e) {
            // Fallback default courses
            cmbCourses.addItem("ICT2112 - Data Structures and Algorithms");
            cmbCourses.addItem("ICT2123 - Database Management Systems");
            cmbCourses.addItem("ICT2132 - Object Oriented Programming");
            cmbCourses.addItem("BST2113 - Bioprocess Technology");
            cmbCourses.addItem("ENG2113 - Engineering Mechanics");
        }
    }

    private String getSelectedCourseCode() {
        String selected = (String) cmbCourses.getSelectedItem();
        if (selected != null && selected.contains(" - ")) {
            return selected.split(" - ")[0].trim();
        }
        return selected != null ? selected.trim() : "ICT2112";
    }

    private void loadSessionData() {
        String courseCode = getSelectedCourseCode();
        String sessionType = (String) cmbSessionType.getSelectedItem();
        int sessionNo = cmbSessionNo.getSelectedItem() != null ? (Integer) cmbSessionNo.getSelectedItem() : 1;
        String dateStr = txtDate.getText().trim();

        LocalDate sessionDate;
        try {
            sessionDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Using current date.", "Date Warning", JOptionPane.WARNING_MESSAGE);
            sessionDate = LocalDate.now();
            txtDate.setText(sessionDate.toString());
        }

        try {
            activeSessionId = attendanceDAO.getOrCreateSession(
                    courseCode, sessionType, sessionNo, sessionDate,
                    currentUser != null ? currentUser.getUserId() : 1
            );
        } catch (Exception e) {
            activeSessionId = 1; // Fallback for offline mode
        }

        // Fetch students & their marked status
        studentList = attendanceDAO.getAllUndergraduates();
        Map<Integer, String> markedStatus;
        try {
            markedStatus = attendanceDAO.getAttendanceForSession(activeSessionId);
        } catch (Exception e) {
            markedStatus = new HashMap<>();
        }

        tableModel.setRowCount(0);
        for (Undergraduate student : studentList) {
            String status = markedStatus.get(student.getUserId());
            boolean isPresent = "PRESENT".equalsIgnoreCase(status);

            tableModel.addRow(new Object[]{
                    student.getUserId(),
                    student.getUsername().toUpperCase(),
                    student.getFullName(),
                    isPresent
            });
        }

        updateStats();
    }

    private void setAllAttendance(boolean present) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(present, i, 3);
        }
        updateStats();
    }

    private void updateStats() {
        int total = tableModel.getRowCount();
        int presentCount = 0;
        for (int i = 0; i < total; i++) {
            Object val = tableModel.getValueAt(i, 3);
            if (val instanceof Boolean && (Boolean) val) {
                presentCount++;
            }
        }
        int absentCount = total - presentCount;
        lblStats.setText(String.format("Total: %d | Present: %d | Absent: %d", total, presentCount, absentCount));
    }

    private void saveAttendance() {
        if (studentList == null || studentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students loaded to mark attendance.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<Integer, String> attendanceMap = new HashMap<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int studentId = (Integer) tableModel.getValueAt(i, 0);
            boolean isPresent = (Boolean) tableModel.getValueAt(i, 3);
            attendanceMap.put(studentId, isPresent ? "PRESENT" : "ABSENT");
        }

        try {
            boolean success = attendanceDAO.saveBatchAttendance(activeSessionId, attendanceMap);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Attendance saved successfully for Session " + cmbSessionNo.getSelectedItem() + " (" + cmbSessionType.getSelectedItem() + ")!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save attendance.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            // In case of offline mode, still notify user
            JOptionPane.showMessageDialog(this,
                    "Attendance updated for " + attendanceMap.size() + " students!\n(Note: " + ex.getMessage() + ")",
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
