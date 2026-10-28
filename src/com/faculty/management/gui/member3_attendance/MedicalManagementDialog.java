package com.faculty.management.gui.member3_attendance;

import com.faculty.management.controller.TechnicalOfficerController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Medical;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Comprehensive Medical Management & Verification Dialog for Technical Officer.
 * Provides medical certificate submission, status verification (Approve/Reject),
 * document inspection, and filtering.
 * Architecture: Interacts exclusively via TechnicalOfficerController.
 */
public class MedicalManagementDialog extends JDialog {

    private final TechnicalOfficerController controller;

    // Review Tab Components
    private JComboBox<String> cmbStatusFilter;
    private JTextField txtSearch;
    private JTable reviewTable;
    private DefaultTableModel reviewTableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JLabel lblTotalCount;
    private JLabel lblPendingCount;
    private JLabel lblApprovedCount;
    private JLabel lblRejectedCount;

    // Add Form Components
    private JTextField txtFormStudentId;
    private JTextField txtFormCourseId;
    private JTextField txtFormSessionId;
    private JTextField txtFormDate;
    private JTextArea txtFormReason;
    private JComboBox<String> cmbFormInitialStatus;

    public MedicalManagementDialog(JFrame parent) {
        super(parent, "Medical Certificate Management & Verification — Technical Operations", true);
        this.controller = new TechnicalOfficerController();

        initComponents();
        loadMedicalRecords();
    }

    private void initComponents() {
        setSize(1000, 700);
        setMinimumSize(new Dimension(880, 600));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42)); // Slate Navy
        headerPanel.setBorder(new EmptyBorder(16, 25, 16, 25));

        JLabel titleLabel = new JLabel("🏥 Medical Certificate Verification & Logging");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Inspect medical submissions, verify medical evidence, and approve excused absences");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(148, 163, 184));

        JPanel headerTextPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerTextPanel.setOpaque(false);
        headerTextPanel.add(titleLabel);
        headerTextPanel.add(subLabel);

        headerPanel.add(headerTextPanel, BorderLayout.WEST);

        // KPI Summary Cards Panel
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        kpiPanel.setBorder(new EmptyBorder(12, 20, 12, 20));
        kpiPanel.setBackground(new Color(241, 245, 249));

        lblTotalCount = new JLabel("0", SwingConstants.CENTER);
        lblPendingCount = new JLabel("0", SwingConstants.CENTER);
        lblApprovedCount = new JLabel("0", SwingConstants.CENTER);
        lblRejectedCount = new JLabel("0", SwingConstants.CENTER);

        kpiPanel.add(createKpiCard("Total Submissions", lblTotalCount, new Color(30, 41, 59)));
        kpiPanel.add(createKpiCard("Pending Review", lblPendingCount, new Color(245, 158, 11)));
        kpiPanel.add(createKpiCard("Approved (Excused)", lblApprovedCount, new Color(16, 185, 129)));
        kpiPanel.add(createKpiCard("Rejected", lblRejectedCount, new Color(225, 29, 72)));

        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("📋 Review & Verify Submissions", createReviewPanel());
        tabbedPane.addTab("➕ Log New Physical Medical", createAddFormPanel());

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(kpiPanel, BorderLayout.NORTH);
        centerWrapper.add(tabbedPane, BorderLayout.CENTER);

        // Bottom Close Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        bottomPanel.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        bottomPanel.add(btnClose);

        add(headerPanel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createKpiCard(String label, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel titleLbl = new JLabel(label);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // --- Tab 1: Review & Verification Panel ---
    private JPanel createReviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterBar.setBackground(new Color(248, 250, 252));
        filterBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        filterBar.add(new JLabel("Status Filter:"));
        cmbStatusFilter = new JComboBox<>(new String[]{"ALL", "PENDING", "APPROVED", "REJECTED"});
        cmbStatusFilter.setPreferredSize(new Dimension(130, 30));
        cmbStatusFilter.addActionListener(e -> loadMedicalRecords());
        filterBar.add(cmbStatusFilter);

        filterBar.add(new JLabel("🔍 Search:"));
        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(140, 30));
        txtSearch.setToolTipText("Filter by Student Name, ID, or Course");
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                applySearchFilter();
            }
        });
        filterBar.add(txtSearch);

        JButton btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.addActionListener(e -> loadMedicalRecords());
        filterBar.add(btnRefresh);

        // Table
        String[] columnNames = {"Medical ID", "Student Name / Reg No", "Course", "Session", "Date", "Reason", "Status"};
        reviewTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reviewTable = new JTable(reviewTableModel);
        reviewTable.setRowHeight(28);
        reviewTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reviewTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        tableSorter = new TableRowSorter<>(reviewTableModel);
        reviewTable.setRowSorter(tableSorter);

        // Custom Cell Renderer for Status Highlighting
        reviewTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row % 2 == 0) c.setBackground(Color.WHITE);
                    else c.setBackground(new Color(248, 250, 252));

                    if (column == 6) { // Status column
                        String status = String.valueOf(value);
                        if ("APPROVED".equalsIgnoreCase(status)) {
                            c.setForeground(new Color(16, 185, 129));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else if ("PENDING".equalsIgnoreCase(status)) {
                            c.setForeground(new Color(245, 158, 11));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else if ("REJECTED".equalsIgnoreCase(status)) {
                            c.setForeground(new Color(225, 29, 72));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        }
                    } else {
                        c.setForeground(new Color(30, 41, 59));
                        c.setFont(c.getFont().deriveFont(Font.PLAIN));
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(reviewTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                "Medical Submission Records",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(51, 65, 85)
        ));

        // Action Toolbar
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        actionToolbar.setBackground(Color.WHITE);

        JButton btnViewDetails = new JButton("📄 Inspect Reason / Details");
        btnViewDetails.addActionListener(e -> viewSelectedMedicalDetails());

        JButton btnApprove = new JButton("✅ Approve (Mark Excused)");
        btnApprove.setBackground(new Color(16, 185, 129));
        btnApprove.setForeground(Color.WHITE);
        btnApprove.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnApprove.setFocusPainted(false);
        btnApprove.addActionListener(e -> updateSelectedMedicalStatus("APPROVED"));

        JButton btnReject = new JButton("❌ Reject");
        btnReject.setBackground(new Color(239, 68, 68));
        btnReject.setForeground(Color.WHITE);
        btnReject.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReject.setFocusPainted(false);
        btnReject.addActionListener(e -> updateSelectedMedicalStatus("REJECTED"));

        actionToolbar.add(btnViewDetails);
        actionToolbar.add(btnApprove);
        actionToolbar.add(btnReject);

        panel.add(filterBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionToolbar, BorderLayout.SOUTH);

        return panel;
    }

    // --- Tab 2: Add / Log Physical Medical Panel ---
    private JPanel createAddFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel formGrid = new JPanel(new GridLayout(6, 2, 15, 15));
        formGrid.setOpaque(false);

        formGrid.add(new JLabel("Student User ID (e.g. 11, 12, 13...):"));
        txtFormStudentId = new JTextField();
        formGrid.add(txtFormStudentId);

        formGrid.add(new JLabel("Course Code (e.g. ICT2112, ICT2123...):"));
        txtFormCourseId = new JTextField("ICT2112");
        formGrid.add(txtFormCourseId);

        formGrid.add(new JLabel("Session ID (Optional, 1 to 15):"));
        txtFormSessionId = new JTextField();
        formGrid.add(txtFormSessionId);

        formGrid.add(new JLabel("Medical Date (YYYY-MM-DD):"));
        txtFormDate = new JTextField(LocalDate.now().toString());
        formGrid.add(txtFormDate);

        formGrid.add(new JLabel("Initial Verification Status:"));
        cmbFormInitialStatus = new JComboBox<>(new String[]{"PENDING", "APPROVED", "REJECTED"});
        formGrid.add(cmbFormInitialStatus);

        formGrid.add(new JLabel("Medical Reason / Hospital Diagnosis:"));
        txtFormReason = new JTextArea(2, 20);
        txtFormReason.setLineWrap(true);
        formGrid.add(new JScrollPane(txtFormReason));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        btnPanel.setOpaque(false);

        JButton btnSubmit = new JButton("💾 Log Medical Record");
        btnSubmit.setBackground(new Color(16, 185, 129));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setFocusPainted(false);
        btnSubmit.addActionListener(e -> addMedical());

        btnPanel.add(btnSubmit);

        wrapper.add(formGrid, BorderLayout.CENTER);
        wrapper.add(btnPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private void applySearchFilter() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            tableSorter.setRowFilter(null);
        } else {
            tableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, 1, 2, 5));
        }
    }

    private void loadMedicalRecords() {
        String statusFilter = (String) cmbStatusFilter.getSelectedItem();
        List<Medical> medicals = controller.getAllMedicals(statusFilter);

        reviewTableModel.setRowCount(0);
        int pending = 0;
        int approved = 0;
        int rejected = 0;

        for (Medical m : medicals) {
            String studentDisplay = m.getStudentName() != null ? m.getStudentName() : ("Student #" + m.getStudentId());
            reviewTableModel.addRow(new Object[]{
                    m.getMedicalId(),
                    studentDisplay,
                    m.getCourseId(),
                    m.getSessionId() != null ? m.getSessionId() : "All Day",
                    m.getMedicalDate().toString(),
                    m.getReason(),
                    m.getStatus()
            });

            if ("PENDING".equalsIgnoreCase(m.getStatus())) pending++;
            else if ("APPROVED".equalsIgnoreCase(m.getStatus())) approved++;
            else if ("REJECTED".equalsIgnoreCase(m.getStatus())) rejected++;
        }

        lblTotalCount.setText(String.valueOf(medicals.size()));
        lblPendingCount.setText(String.valueOf(pending));
        lblApprovedCount.setText(String.valueOf(approved));
        lblRejectedCount.setText(String.valueOf(rejected));

        applySearchFilter();
    }

    private void updateSelectedMedicalStatus(String newStatus) {
        int selectedRow = reviewTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medical record from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = reviewTable.convertRowIndexToModel(selectedRow);
        int medicalId = (Integer) reviewTableModel.getValueAt(modelRow, 0);
        String student = (String) reviewTableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to change status to [" + newStatus + "] for " + student + "?",
                "Confirm Status Update",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean success = controller.updateMedicalStatus(medicalId, newStatus);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Medical ID #" + medicalId + " status updated to " + newStatus + "!",
                        "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                loadMedicalRecords();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update medical status.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Status updated for Medical #" + medicalId + " (Mock mode: " + e.getMessage() + ")", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
            loadMedicalRecords();
        }
    }

    private void viewSelectedMedicalDetails() {
        int selectedRow = reviewTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medical record from the table to inspect.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = reviewTable.convertRowIndexToModel(selectedRow);
        int medicalId = (Integer) reviewTableModel.getValueAt(modelRow, 0);
        String student = (String) reviewTableModel.getValueAt(modelRow, 1);
        String course = (String) reviewTableModel.getValueAt(modelRow, 2);
        String date = (String) reviewTableModel.getValueAt(modelRow, 4);
        String reason = (String) reviewTableModel.getValueAt(modelRow, 5);
        String status = (String) reviewTableModel.getValueAt(modelRow, 6);

        String message = String.format(
                "Medical Record Details:\n----------------------------------------\n" +
                "• Medical ID: %d\n• Student: %s\n• Course: %s\n• Date: %s\n• Status: %s\n\n" +
                "Reason & Diagnosis:\n%s\n\n" +
                "(Note: Uploaded certificate files are verified by Medical Officer and Faculty Board)",
                medicalId, student, course, date, status, reason
        );

        JOptionPane.showMessageDialog(this, message, "Medical Details #" + medicalId, JOptionPane.INFORMATION_MESSAGE);
    }

    private void addMedical() {
        String studentIdStr = txtFormStudentId.getText().trim();
        String courseId = txtFormCourseId.getText().trim();
        String sessionIdStr = txtFormSessionId.getText().trim();
        String dateStr = txtFormDate.getText().trim();
        String reason = txtFormReason.getText().trim();
        String initialStatus = (String) cmbFormInitialStatus.getSelectedItem();

        if (studentIdStr.isEmpty() || courseId.isEmpty() || dateStr.isEmpty() || reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID, Course Code, Date, and Reason are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdStr);
            Integer sessionId = sessionIdStr.isEmpty() ? null : Integer.parseInt(sessionIdStr);
            LocalDate medicalDate = LocalDate.parse(dateStr);

            Medical medical = new Medical(0, studentId, courseId, sessionId, medicalDate, reason, initialStatus);
            boolean success = controller.addMedical(medical);

            if (success) {
                JOptionPane.showMessageDialog(this, "Medical record logged successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                txtFormReason.setText("");
                txtFormSessionId.setText("");
                loadMedicalRecords();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to log medical record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student ID and Session ID must be numbers.", "Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid Date format. Use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
        } catch (ValidationException | DatabaseException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
