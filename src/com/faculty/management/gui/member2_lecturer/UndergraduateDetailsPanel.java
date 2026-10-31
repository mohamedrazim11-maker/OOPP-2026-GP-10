package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.dao.UndergraduateDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.gui.common.PrimaryButton;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Enrollment;
import com.faculty.management.model.Undergraduate;
import com.faculty.management.service.UndergraduateService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Undergraduate Details Panel (Requirement 3).
 * Enables the Lecturer to:
 * - View undergraduate student information
 * - Search students by ID, Name, or Username
 * - Inspect full student details and enrollment records
 */
public class UndergraduateDetailsPanel extends JPanel {
    private JTextField txtSearch;
    private JButton btnSearch, btnRefresh, btnViewDetails;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JLabel lblStatusCount;

    private final UndergraduateService undergraduateService = new UndergraduateService();
    private List<Undergraduate> allStudents = new ArrayList<>();

    public UndergraduateDetailsPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(18, 24, 18, 24));
        initUI();
        loadStudents();
    }

    private void initUI() {
        // Top Toolbar per Requirement 3 (Search students)
        JPanel topToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topToolbar.setOpaque(false);

        JLabel lblSearch = new JLabel("Search Undergraduate:");
        lblSearch.setFont(UITheme.FONT_BODY_BOLD);
        lblSearch.setForeground(UITheme.TEXT_BODY);

        txtSearch = new JTextField(16);
        txtSearch.setFont(UITheme.FONT_BODY);
        txtSearch.setPreferredSize(new Dimension(180, 34));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        txtSearch.addActionListener(e -> filterStudents());

        btnSearch = new JButton("Search");
        btnSearch.setFont(UITheme.FONT_SMALL_BOLD);
        btnSearch.setBackground(Color.WHITE);
        btnSearch.setForeground(UITheme.TEXT_BODY);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btnSearch.addActionListener(e -> filterStudents());

        btnRefresh = new JButton("Show All");
        btnRefresh.setFont(UITheme.FONT_SMALL_BOLD);
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setForeground(UITheme.TEXT_BODY);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadStudents();
        });

        topToolbar.add(lblSearch);
        topToolbar.add(txtSearch);
        topToolbar.add(btnSearch);
        topToolbar.add(btnRefresh);

        // Student Table Setup
        tableModel = new DefaultTableModel(new String[]{
                "Student ID", "Username / Reg No", "Full Name", "Email Address", "Contact No", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        UITheme.styleTable(studentTable);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        studentTable.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        studentTable.getColumnModel().getColumn(1).setCellRenderer(centerRender);
        studentTable.getColumnModel().getColumn(4).setCellRenderer(centerRender);
        studentTable.getColumnModel().getColumn(5).setCellRenderer(centerRender);

        studentTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(210);
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        studentTable.getColumnModel().getColumn(5).setPreferredWidth(90);

        JScrollPane scrollPane = UITheme.createModernScrollPane(studentTable);

        // Bottom Action Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        btnViewDetails = new PrimaryButton("View Student Details");
        btnViewDetails.setFont(UITheme.FONT_SMALL_BOLD);
        btnViewDetails.setPreferredSize(new Dimension(170, 36));
        btnViewDetails.addActionListener(e -> showStudentDetails());

        lblStatusCount = new JLabel("Total Undergraduates: 0");
        lblStatusCount.setFont(UITheme.FONT_SMALL);
        lblStatusCount.setForeground(UITheme.TEXT_MUTED);

        bottomPanel.add(btnViewDetails, BorderLayout.WEST);
        bottomPanel.add(lblStatusCount, BorderLayout.EAST);

        add(topToolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadStudents() {
        try {
            allStudents = undergraduateService.getAllStudents();
            populateTable(allStudents);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Failed to load undergraduate records: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterStudents() {
        String query = txtSearch.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            populateTable(allStudents);
            return;
        }

        List<Undergraduate> filtered = new ArrayList<>();
        for (Undergraduate s : allStudents) {
            String idStr = String.valueOf(s.getUserId());
            String username = s.getUsername() != null ? s.getUsername().toLowerCase() : "";
            String fullName = s.getFullName() != null ? s.getFullName().toLowerCase() : "";
            String email = s.getEmail() != null ? s.getEmail().toLowerCase() : "";

            if (idStr.contains(query) || username.contains(query) || fullName.contains(query) || email.contains(query)) {
                filtered.add(s);
            }
        }
        populateTable(filtered);
    }

    private void populateTable(List<Undergraduate> list) {
        tableModel.setRowCount(0);
        for (Undergraduate s : list) {
            tableModel.addRow(new Object[]{
                    s.getUserId(),
                    s.getUsername(),
                    s.getFullName(),
                    s.getEmail(),
                    s.getContactNo() != null ? s.getContactNo() : "-",
                    s.getStatus() != null ? s.getStatus() : "ACTIVE"
            });
        }
        lblStatusCount.setText("Showing " + list.size() + " of " + allStudents.size() + " Undergraduates");
    }

    private void showStudentDetails() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student row to view details.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int studentId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            Undergraduate student = undergraduateService.getProfile(studentId);
            List<Enrollment> enrollments = undergraduateService.getCourseDetails(studentId);

            showStudentDetailsDialog(student, enrollments);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to fetch student details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStudentDetailsDialog(Undergraduate student, List<Enrollment> enrollments) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Undergraduate Student Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setMinimumSize(new Dimension(520, 420));
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel(student.getFullName());
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel regNo = new JLabel("Registration No: " + student.getUsername() + "  |  User ID: " + student.getUserId());
        regNo.setFont(UITheme.FONT_BODY);
        regNo.setForeground(UITheme.TEXT_MUTED);

        content.add(title);
        content.add(regNo);
        content.add(Box.createVerticalStrut(14));

        // Details Grid
        JPanel grid = new JPanel(new GridLayout(4, 2, 8, 8));
        grid.setOpaque(false);
        grid.add(new JLabel("Email Address:"));
        grid.add(new JLabel(student.getEmail()));
        grid.add(new JLabel("Contact Number:"));
        grid.add(new JLabel(student.getContactNo() != null ? student.getContactNo() : "-"));
        grid.add(new JLabel("Account Status:"));
        grid.add(new JLabel(student.getStatus()));
        grid.add(new JLabel("Profile Picture:"));
        grid.add(new JLabel(student.getProfilePic() != null ? student.getProfilePic() : "Default"));

        content.add(grid);
        content.add(Box.createVerticalStrut(16));

        // Enrolled Courses Sub-section
        JLabel lblEnrolled = new JLabel("Enrolled Academic Modules:");
        lblEnrolled.setFont(UITheme.FONT_BODY_BOLD);
        lblEnrolled.setForeground(UITheme.TEXT_MAIN);
        content.add(lblEnrolled);
        content.add(Box.createVerticalStrut(8));

        DefaultTableModel enrollModel = new DefaultTableModel(new String[]{"Course Code", "Semester", "Academic Year", "Status"}, 0);
        for (Enrollment en : enrollments) {
            String cCode = (en.getCourse() != null) ? en.getCourse().getCourseCode() : ("Course #" + en.getCourseId());
            enrollModel.addRow(new Object[]{cCode, "Semester " + en.getSemesterNumber(), en.getAcademicYear(), en.getStatus()});
        }
        JTable enrollTable = new JTable(enrollModel);
        UITheme.styleTable(enrollTable);
        JScrollPane enrollScroll = UITheme.createModernScrollPane(enrollTable);
        enrollScroll.setPreferredSize(new Dimension(460, 120));
        content.add(enrollScroll);

        // Close button
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setBackground(UITheme.BG_PAGE);
        JButton btnClose = new JButton("Close");
        btnClose.setFont(UITheme.FONT_SMALL_BOLD);
        btnClose.addActionListener(e -> dialog.dispose());
        btnRow.add(btnClose);

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setVisible(true);
    }
}
