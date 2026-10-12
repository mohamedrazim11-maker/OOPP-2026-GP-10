package com.faculty.management.gui.member3_attendance;

import com.faculty.management.dao.MedicalDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Medical;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MedicalManagementDialog extends JDialog {

    private MedicalDAO medicalDAO;
    private JTextField txtStudentIdSearch;
    private JTable medicalTable;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField txtCourseId;
    private JTextField txtSessionId;
    private JTextField txtMedicalDate;
    private JTextField txtReason;

    public MedicalManagementDialog(JFrame parent) {
        super(parent, "Medical Management", true);
        this.medicalDAO = new MedicalDAO();
        initComponents();
    }

    private void initComponents() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Top Panel for Search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topPanel.setBackground(new Color(241, 245, 249));
        topPanel.add(new JLabel("Enter Student ID:"));
        txtStudentIdSearch = new JTextField(15);
        JButton btnSearch = new JButton("Search / View Medicals");
        btnSearch.setBackground(new Color(14, 165, 233));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> loadMedicals());

        topPanel.add(txtStudentIdSearch);
        topPanel.add(btnSearch);

        // Center Panel for Table
        tableModel = new DefaultTableModel(new String[]{"Medical ID", "Course ID", "Session ID", "Date", "Reason", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        medicalTable = new JTable(tableModel);
        medicalTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(medicalTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Student Medical Records", TitledBorder.LEFT, TitledBorder.TOP));

        // Bottom Panel for Adding Medical
        JPanel bottomPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        bottomPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Add New Medical Record", TitledBorder.LEFT, TitledBorder.TOP));
        bottomPanel.setBackground(Color.WHITE);

        bottomPanel.add(new JLabel("Course ID:"));
        txtCourseId = new JTextField();
        bottomPanel.add(txtCourseId);

        bottomPanel.add(new JLabel("Session ID (Optional):"));
        txtSessionId = new JTextField();
        bottomPanel.add(txtSessionId);

        bottomPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        txtMedicalDate = new JTextField(LocalDate.now().toString());
        bottomPanel.add(txtMedicalDate);

        bottomPanel.add(new JLabel("Reason:"));
        txtReason = new JTextField();
        bottomPanel.add(txtReason);

        JButton btnAdd = new JButton("Add Medical");
        btnAdd.setBackground(new Color(16, 185, 129));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addMedical());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.add(btnAdd);

        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.add(bottomPanel, BorderLayout.CENTER);
        formWrapper.add(actionPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(formWrapper, BorderLayout.SOUTH);
    }

    private void loadMedicals() {
        String studentIdStr = txtStudentIdSearch.getText().trim();
        if (studentIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID to search.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdStr);
            List<Medical> medicals = medicalDAO.getMedicalsByStudent(studentId);
            tableModel.setRowCount(0); // Clear table

            for (Medical m : medicals) {
                tableModel.addRow(new Object[]{
                        m.getMedicalId(),
                        m.getCourseId(),
                        m.getSessionId() == null ? "N/A" : m.getSessionId(),
                        m.getMedicalDate().toString(),
                        m.getReason(),
                        m.getStatus()
                });
            }

            if (medicals.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No medical records found for Student ID: " + studentId, "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student ID must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addMedical() {
        String studentIdStr = txtStudentIdSearch.getText().trim();
        if (studentIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please search for a Student ID first before adding a medical record.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdStr);
            String courseId = txtCourseId.getText().trim();
            String sessionIdStr = txtSessionId.getText().trim();
            String dateStr = txtMedicalDate.getText().trim();
            String reason = txtReason.getText().trim();

            if (courseId.isEmpty() || dateStr.isEmpty() || reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course ID, Date, and Reason are required.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer sessionId = null;
            if (!sessionIdStr.isEmpty()) {
                sessionId = Integer.parseInt(sessionIdStr);
            }

            LocalDate medicalDate = LocalDate.parse(dateStr);

            Medical medical = new Medical(0, studentId, courseId, sessionId, medicalDate, reason, "PENDING");
            
            boolean success = medicalDAO.addMedical(medical);
            if (success) {
                JOptionPane.showMessageDialog(this, "Medical record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Clear fields
                txtCourseId.setText("");
                txtSessionId.setText("");
                txtReason.setText("");
                // Reload table
                loadMedicals();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add medical record.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format in Student ID or Session ID.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
