package com.faculty.management.gui.member3_attendance;

import com.faculty.management.controller.TechnicalOfficerController;
import com.faculty.management.model.Course;
import com.faculty.management.model.StudentAttendanceSummary;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * View Attendance Dialog for viewing batch and individual student attendance percentages,
 * supporting Theory, Practical, and Combined calculations with 80% eligibility thresholds.
 * Architecture: Interacts exclusively via TechnicalOfficerController.
 */
public class ViewAttendanceDialog extends JDialog {

    private final TechnicalOfficerController controller;

    private JComboBox<String> cmbCourse;
    private JComboBox<String> cmbComponent;
    private JComboBox<String> cmbEligibilityFilter;
    private JTextField txtSearch;

    private JTable summaryTable;
    private DefaultTableModel tableModel;

    private JLabel lblTotalStudents;
    private JLabel lblEligibleCount;
    private JLabel lblIneligibleCount;
    private JLabel lblBatchAvg;

    private List<StudentAttendanceSummary> allSummaries = new ArrayList<>();

    public ViewAttendanceDialog(JFrame parent) {
        super(parent, "Batch Attendance Summary & Eligibility — Technical Operations", true);
        this.controller = new TechnicalOfficerController();

        initComponents();
        loadCourses();
        loadAttendanceData();
    }

    private void initComponents() {
        setSize(950, 700);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42)); // Slate Navy
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("📊 Undergraduate Attendance Summary & Eligibility");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Evaluate 80% eligibility across Theory, Practical, and Combined sessions");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(148, 163, 184));

        JPanel headerTextPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        headerTextPanel.setOpaque(false);
        headerTextPanel.add(titleLabel);
        headerTextPanel.add(subLabel);

        headerPanel.add(headerTextPanel, BorderLayout.WEST);

        // Filter Control Bar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        filterPanel.setBackground(new Color(248, 250, 252));
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        filterPanel.add(new JLabel("Course:"));
        cmbCourse = new JComboBox<>();
        cmbCourse.setPreferredSize(new Dimension(170, 30));
        cmbCourse.addActionListener(e -> loadAttendanceData());
        filterPanel.add(cmbCourse);

        filterPanel.add(new JLabel("Component:"));
        cmbComponent = new JComboBox<>(new String[]{"COMBINED", "THEORY", "PRACTICAL"});
        cmbComponent.setPreferredSize(new Dimension(130, 30));
        cmbComponent.addActionListener(e -> loadAttendanceData());
        filterPanel.add(cmbComponent);

        filterPanel.add(new JLabel("Filter:"));
        cmbEligibilityFilter = new JComboBox<>(new String[]{"All Students", "Eligible (>= 80%)", "Not Eligible (< 80%)"});
        cmbEligibilityFilter.setPreferredSize(new Dimension(150, 30));
        cmbEligibilityFilter.addActionListener(e -> applyFilter());
        filterPanel.add(cmbEligibilityFilter);

        filterPanel.add(new JLabel("Search:"));
        txtSearch = new JTextField(10);
        txtSearch.setPreferredSize(new Dimension(110, 30));
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> applyFilter());
        filterPanel.add(txtSearch);
        filterPanel.add(btnSearch);

        // KPI Summary Cards
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        kpiPanel.setBorder(new EmptyBorder(12, 20, 12, 20));
        kpiPanel.setBackground(new Color(241, 245, 249));

        lblTotalStudents = new JLabel("0", SwingConstants.CENTER);
        lblEligibleCount = new JLabel("0", SwingConstants.CENTER);
        lblIneligibleCount = new JLabel("0", SwingConstants.CENTER);
        lblBatchAvg = new JLabel("0.0%", SwingConstants.CENTER);

        kpiPanel.add(createKpiCard("Total Batch Students", lblTotalStudents, new Color(30, 41, 59)));
        kpiPanel.add(createKpiCard("Eligible (>= 80%)", lblEligibleCount, new Color(16, 185, 129)));
        kpiPanel.add(createKpiCard("Not Eligible (< 80%)", lblIneligibleCount, new Color(225, 29, 72)));
        kpiPanel.add(createKpiCard("Batch Average %", lblBatchAvg, new Color(14, 165, 233)));

        // Table Panel
        String[] columnNames = {
                "Reg No", "Student Name", "Component", "Sessions Held",
                "Attended", "Medicals Approved", "Attendance %", "Eligibility Status"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        summaryTable = new JTable(tableModel);
        summaryTable.setRowHeight(28);
        summaryTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        summaryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Custom Cell Renderer to highlight Eligibility and Percentages
        summaryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 250, 252));
                    }

                    // Highlight Status column
                    if (column == 7) {
                        String status = String.valueOf(value);
                        if (status.contains("ELIGIBLE") && !status.contains("NOT")) {
                            c.setForeground(new Color(16, 185, 129));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else {
                            c.setForeground(new Color(225, 29, 72));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        }
                    } else if (column == 6) {
                        // Attendance %
                        c.setForeground(new Color(15, 23, 42));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(51, 65, 85));
                        c.setFont(c.getFont().deriveFont(Font.PLAIN));
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(summaryTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                "Attendance Records & Examination Eligibility Details",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(51, 65, 85)
        ));

        // Center Container
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(kpiPanel, BorderLayout.CENTER);

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(centerPanel, BorderLayout.NORTH);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        bottomPanel.setBackground(Color.WHITE);

        JButton btnExportCsv = new JButton("📥 Export to CSV");
        btnExportCsv.setBackground(new Color(16, 185, 129));
        btnExportCsv.setForeground(Color.WHITE);
        btnExportCsv.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExportCsv.setFocusPainted(false);
        btnExportCsv.addActionListener(e -> exportToCsv());

        JButton btnRefresh = new JButton("🔄 Refresh Data");
        btnRefresh.addActionListener(e -> loadAttendanceData());

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnExportCsv);
        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnClose);

        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
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

    private void loadCourses() {
        cmbCourse.removeAllItems();
        try {
            List<Course> courses = controller.getAllCourses();
            for (Course c : courses) {
                cmbCourse.addItem(c.getCourseCode() + " - " + c.getCourseName());
            }
        } catch (Exception e) {
            cmbCourse.addItem("ICT2112 - Data Structures and Algorithms");
            cmbCourse.addItem("ICT2123 - Database Management Systems");
            cmbCourse.addItem("ICT2132 - Object Oriented Programming");
            cmbCourse.addItem("BST2113 - Bioprocess Technology");
            cmbCourse.addItem("ENG2113 - Engineering Mechanics");
        }
    }

    private String getSelectedCourseCode() {
        String selected = (String) cmbCourse.getSelectedItem();
        if (selected != null && selected.contains(" - ")) {
            return selected.split(" - ")[0].trim();
        }
        return selected != null ? selected.trim() : "ICT2112";
    }

    private void loadAttendanceData() {
        String courseCode = getSelectedCourseCode();
        String component = (String) cmbComponent.getSelectedItem();
        if (component == null) component = "COMBINED";

        allSummaries = controller.getBatchAttendanceSummary(courseCode, component);
        applyFilter();
    }

    private void applyFilter() {
        if (allSummaries == null) return;

        String eligibilityFilter = (String) cmbEligibilityFilter.getSelectedItem();
        String search = txtSearch.getText().trim().toLowerCase();

        tableModel.setRowCount(0);
        int eligibleCount = 0;
        int ineligibleCount = 0;
        double totalPercentage = 0.0;

        for (StudentAttendanceSummary s : allSummaries) {
            // Apply Eligibility Filter
            if ("Eligible (>= 80%)".equals(eligibilityFilter) && !s.isEligible()) {
                continue;
            }
            if ("Not Eligible (< 80%)".equals(eligibilityFilter) && s.isEligible()) {
                continue;
            }

            // Apply Search Filter
            if (!search.isEmpty()) {
                boolean matchReg = s.getRegNo().toLowerCase().contains(search);
                boolean matchName = s.getStudentName().toLowerCase().contains(search);
                if (!matchReg && !matchName) {
                    continue;
                }
            }

            tableModel.addRow(new Object[]{
                    s.getRegNo(),
                    s.getStudentName(),
                    s.getSessionType(),
                    s.getTotalSessions(),
                    s.getAttendedSessions(),
                    s.getApprovedMedicals(),
                    String.format("%.1f%%", s.getPercentage()),
                    s.getEligibilityStatus()
            });

            if (s.isEligible()) {
                eligibleCount++;
            } else {
                ineligibleCount++;
            }
            totalPercentage += s.getPercentage();
        }

        int displayedCount = tableModel.getRowCount();
        lblTotalStudents.setText(String.valueOf(allSummaries.size()));
        lblEligibleCount.setText(String.valueOf(eligibleCount));
        lblIneligibleCount.setText(String.valueOf(ineligibleCount));
        double avg = displayedCount > 0 ? (totalPercentage / displayedCount) : 0.0;
        lblBatchAvg.setText(String.format("%.1f%%", avg));
    }

    /**
     * Exports the currently displayed attendance table and eligibility statuses to a CSV file.
     */
    private void exportToCsv() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No attendance data available to export.",
                    "Export Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseCode = getSelectedCourseCode();
        String component = (String) cmbComponent.getSelectedItem();
        if (component == null) component = "COMBINED";

        String defaultFileName = "Attendance_Report_" + courseCode + "_" + component + ".csv";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Attendance Report (CSV)");
        fileChooser.setSelectedFile(new File(defaultFileName));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Spreadsheet (*.csv)", "csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File targetFile = fileChooser.getSelectedFile();
        if (!targetFile.getName().toLowerCase().endsWith(".csv")) {
            targetFile = new File(targetFile.getParentFile(), targetFile.getName() + ".csv");
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(targetFile))) {
            // Write CSV Header
            writer.println("Registration No,Student Name,Component,Sessions Held,Attended Sessions,Approved Medicals,Attendance Percentage,Eligibility Status");

            // Write Table Data Rows
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                StringBuilder row = new StringBuilder();
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    Object val = tableModel.getValueAt(r, c);
                    String strVal = val != null ? val.toString().trim() : "";
                    strVal = strVal.replace("\"", "\"\"");
                    row.append("\"").append(strVal).append("\"");
                    if (c < tableModel.getColumnCount() - 1) {
                        row.append(",");
                    }
                }
                writer.println(row.toString());
            }

            int choice = JOptionPane.showOptionDialog(this,
                    "Attendance report successfully exported to:\n" + targetFile.getAbsolutePath() +
                    "\n\nWould you like to open the file now?",
                    "Export Successful",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Open File", "Close"},
                    "Open File");

            if (choice == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(targetFile);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Could not open file automatically: " + ex.getMessage(),
                            "Notice", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to export CSV report: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
