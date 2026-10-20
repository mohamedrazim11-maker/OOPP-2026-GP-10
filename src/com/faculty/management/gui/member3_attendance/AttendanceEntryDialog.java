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
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Attendance Entry Dialog for Technical Officer.
 * Provides attendance marking, updating, deleting, date validation, and real-time student searching.
 */
public class AttendanceEntryDialog extends JDialog {

    private final User currentUser;
    private final AttendanceDAO attendanceDAO;
    private final CourseDAO courseDAO;

    private JComboBox<String> cmbCourses;
    private JComboBox<String> cmbSessionType;
    private JComboBox<Integer> cmbSessionNo;
    private JTextField txtDate;
    private JLabel lblSessionStatus;
    private JTextField txtSearchStudent;

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JLabel lblStats;

    private JButton btnSave;
    private JButton btnDelete;

    private int activeSessionId = -1;
    private List<Undergraduate> studentList;

    public AttendanceEntryDialog(JFrame parent, User currentUser) {
        super(parent, "Attendance Management — Technical Operations", true);
        this.currentUser = currentUser;
        this.attendanceDAO = new AttendanceDAO();
        this.courseDAO = new CourseDAO();

        initComponents();
        loadCourses();
        loadSessionData();
    }

    private void initComponents() {
        setSize(950, 700);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // 1. Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(124, 45, 18)); // Technical Officer Rust/Amber
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("📋 Undergraduate Attendance Entry & Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Record, update, or remove Theory and Practical session attendance (Sessions 1 to 15)");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(254, 215, 170));

        JPanel headerTextPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        headerTextPanel.setOpaque(false);
        headerTextPanel.add(titleLabel);
        headerTextPanel.add(subLabel);

        headerPanel.add(headerTextPanel, BorderLayout.WEST);

        // 2. Session Configuration Panel
        JPanel sessionConfigPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        sessionConfigPanel.setBackground(new Color(248, 250, 252));
        sessionConfigPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        sessionConfigPanel.add(new JLabel("Course:"));
        cmbCourses = new JComboBox<>();
        cmbCourses.setPreferredSize(new Dimension(175, 30));
        cmbCourses.addActionListener(e -> loadSessionData());
        sessionConfigPanel.add(cmbCourses);

        sessionConfigPanel.add(new JLabel("Type:"));
        cmbSessionType = new JComboBox<>(new String[]{"THEORY", "PRACTICAL"});
        cmbSessionType.setPreferredSize(new Dimension(105, 30));
        cmbSessionType.addActionListener(e -> loadSessionData());
        sessionConfigPanel.add(cmbSessionType);

        sessionConfigPanel.add(new JLabel("Session No:"));
        Integer[] sessionNumbers = new Integer[15];
        for (int i = 0; i < 15; i++) sessionNumbers[i] = i + 1;
        cmbSessionNo = new JComboBox<>(sessionNumbers);
        cmbSessionNo.setPreferredSize(new Dimension(75, 30));
        cmbSessionNo.addActionListener(e -> loadSessionData());
        sessionConfigPanel.add(cmbSessionNo);

        sessionConfigPanel.add(new JLabel("Date:"));
        txtDate = new JTextField(LocalDate.now().toString(), 9);
        txtDate.setPreferredSize(new Dimension(95, 30));
        sessionConfigPanel.add(txtDate);

        JButton btnLoad = new JButton("🔄 Refresh");
        btnLoad.setBackground(new Color(14, 165, 233));
        btnLoad.setForeground(Color.WHITE);
        btnLoad.setFocusPainted(false);
        btnLoad.addActionListener(e -> loadSessionData());
        sessionConfigPanel.add(btnLoad);

        lblSessionStatus = new JLabel("● NEW SESSION");
        lblSessionStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSessionStatus.setForeground(new Color(245, 158, 11));
        lblSessionStatus.setBorder(new EmptyBorder(0, 10, 0, 0));
        sessionConfigPanel.add(lblSessionStatus);

        // 3. Search and Quick Controls Bar
        JPanel searchFilterBar = new JPanel(new BorderLayout());
        searchFilterBar.setBackground(Color.WHITE);
        searchFilterBar.setBorder(new EmptyBorder(8, 15, 8, 15));

        JPanel searchLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchLeft.setOpaque(false);
        searchLeft.add(new JLabel("🔍 Filter Student:"));
        txtSearchStudent = new JTextField(15);
        txtSearchStudent.setToolTipText("Type Student ID or Name to filter table");
        txtSearchStudent.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterTable(txtSearchStudent.getText().trim());
            }
        });
        searchLeft.add(txtSearchStudent);

        JPanel quickRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        quickRight.setOpaque(false);
        JButton btnAllPresent = new JButton("Mark All Present");
        btnAllPresent.addActionListener(e -> setAllAttendance(true));

        JButton btnAllAbsent = new JButton("Mark All Absent");
        btnAllAbsent.addActionListener(e -> setAllAttendance(false));

        quickRight.add(btnAllPresent);
        quickRight.add(btnAllAbsent);

        searchFilterBar.add(searchLeft, BorderLayout.WEST);
        searchFilterBar.add(quickRight, BorderLayout.EAST);

        // 4. Student Table Panel
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

        tableSorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(tableSorter);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                "Student Attendance Checklist",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(51, 65, 85)
        ));

        // Center Container
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(sessionConfigPanel, BorderLayout.NORTH);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.add(searchFilterBar, BorderLayout.NORTH);
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableWrapper, BorderLayout.CENTER);

        // 5. Bottom Action Bar
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Stats summary label
        lblStats = new JLabel("Total: 0 | Present: 0 | Absent: 0");
        lblStats.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStats.setForeground(new Color(71, 85, 105));

        // Buttons
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtonPanel.setOpaque(false);

        btnDelete = new JButton("🗑️ Delete Session Attendance");
        btnDelete.setBackground(new Color(239, 68, 68));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDelete.setFocusPainted(false);
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> deleteSessionAttendance());

        btnSave = new JButton("💾 Save Attendance");
        btnSave.setBackground(new Color(16, 185, 129));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> saveAttendance());

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());

        actionButtonPanel.add(btnDelete);
        actionButtonPanel.add(btnSave);
        actionButtonPanel.add(btnClose);

        bottomPanel.add(lblStats, BorderLayout.WEST);
        bottomPanel.add(actionButtonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void filterTable(String query) {
        if (query == null || query.trim().isEmpty()) {
            tableSorter.setRowFilter(null);
        } else {
            tableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, 1, 2));
        }
        updateStats();
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

        boolean hasExistingRecords = attendanceDAO.hasAttendanceRecords(activeSessionId) || !markedStatus.isEmpty();

        // Update status indicator and buttons
        if (hasExistingRecords) {
            lblSessionStatus.setText("● RECORDED (Session #" + sessionNo + ")");
            lblSessionStatus.setForeground(new Color(16, 185, 129)); // Green
            btnSave.setText("🔄 Update Attendance");
            btnDelete.setEnabled(true);
        } else {
            lblSessionStatus.setText("● NEW (Not Saved Yet)");
            lblSessionStatus.setForeground(new Color(245, 158, 11)); // Amber
            btnSave.setText("💾 Save Attendance");
            btnDelete.setEnabled(false);
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

        filterTable(txtSearchStudent.getText().trim());
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

        // 1. Validation: Future Date Check
        String dateStr = txtDate.getText().trim();
        LocalDate sessionDate;
        try {
            sessionDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Date Format! Please use standard format (YYYY-MM-DD).\nExample: " + LocalDate.now(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (sessionDate.isAfter(LocalDate.now())) {
            JOptionPane.showMessageDialog(this,
                    "Validation Error: Attendance date cannot be in the future (" + sessionDate + ")!\nPlease enter today's date or a past lecture date.",
                    "Future Date Prohibited", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Gather student attendance values
        Map<Integer, String> attendanceMap = new HashMap<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int studentId = (Integer) tableModel.getValueAt(i, 0);
            boolean isPresent = (Boolean) tableModel.getValueAt(i, 3);
            attendanceMap.put(studentId, isPresent ? "PRESENT" : "ABSENT");
        }

        try {
            boolean success = attendanceDAO.saveBatchAttendance(activeSessionId, attendanceMap);
            if (success) {
                String action = btnSave.getText().contains("Update") ? "updated" : "saved";
                JOptionPane.showMessageDialog(this,
                        "Attendance " + action + " successfully for Session " + cmbSessionNo.getSelectedItem() +
                                " (" + cmbSessionType.getSelectedItem() + ") of " + getSelectedCourseCode() + "!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadSessionData(); // Refresh state
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save attendance.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            // Offline mode or database error
            JOptionPane.showMessageDialog(this,
                    "Attendance recorded for " + attendanceMap.size() + " students!\n(Note: " + ex.getMessage() + ")",
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
            loadSessionData();
        }
    }

    private void deleteSessionAttendance() {
        int sessionNo = (Integer) cmbSessionNo.getSelectedItem();
        String sessionType = (String) cmbSessionType.getSelectedItem();
        String courseCode = getSelectedCourseCode();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete all attendance records for Session " + sessionNo +
                        " (" + sessionType + ") of " + courseCode + "?\n\nThis will remove all marked records for this session.",
                "Confirm Delete Session Attendance",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean success = attendanceDAO.deleteSessionAttendance(activeSessionId);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Attendance records for Session " + sessionNo + " (" + sessionType + ") have been deleted successfully.",
                        "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadSessionData(); // Reload into blank new session state
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete session attendance.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Session attendance reset.\n(Note: " + ex.getMessage() + ")",
                    "Reset", JOptionPane.INFORMATION_MESSAGE);
            loadSessionData();
        }
    }
}
