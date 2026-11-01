package com.faculty.management.gui.member3_attendance;

import com.faculty.management.controller.TechnicalOfficerController;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Course;
import com.faculty.management.model.StudentAttendanceSummary;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
 * View Attendance Panel for viewing batch and individual student attendance percentages,
 * supporting Theory, Practical, and Combined calculations with 80% eligibility thresholds.
 * Embedded directly inside TechnicalOfficerDashboard tabbed pane.
 * Architecture: Interacts exclusively via TechnicalOfficerController.
 */
public class ViewAttendancePanel extends JPanel {

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

    public ViewAttendancePanel() {
        this.controller = new TechnicalOfficerController();
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);

        initComponents();
        loadCourses();
        loadAttendanceData();
    }

    private void initComponents() {
        // 1. Filter Control Bar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(4, 16, 4, 16)
        ));

        JLabel lblCourse = new JLabel("Course:");
        lblCourse.setFont(UITheme.FONT_BODY_BOLD);
        filterPanel.add(lblCourse);

        cmbCourse = new JComboBox<>();
        cmbCourse.setPreferredSize(new Dimension(200, 32));
        cmbCourse.addActionListener(e -> loadAttendanceData());
        filterPanel.add(cmbCourse);

        JLabel lblComp = new JLabel("Component:");
        lblComp.setFont(UITheme.FONT_BODY_BOLD);
        filterPanel.add(lblComp);

        cmbComponent = new JComboBox<>(new String[]{"COMBINED", "THEORY", "PRACTICAL"});
        cmbComponent.setPreferredSize(new Dimension(130, 32));
        cmbComponent.addActionListener(e -> loadAttendanceData());
        filterPanel.add(cmbComponent);

        JLabel lblFilter = new JLabel("Filter:");
        lblFilter.setFont(UITheme.FONT_BODY_BOLD);
        filterPanel.add(lblFilter);

        cmbEligibilityFilter = new JComboBox<>(new String[]{"All Students", "Eligible (>= 80%)", "Not Eligible (< 80%)"});
        cmbEligibilityFilter.setPreferredSize(new Dimension(160, 32));
        cmbEligibilityFilter.addActionListener(e -> applyFilter());
        filterPanel.add(cmbEligibilityFilter);

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(UITheme.FONT_BODY_BOLD);
        filterPanel.add(lblSearch);

        txtSearch = new JTextField(10);
        txtSearch.setPreferredSize(new Dimension(110, 32));
        txtSearch.addActionListener(e -> applyFilter());
        filterPanel.add(txtSearch);

        JButton btnSearch = new JButton("Search");
        btnSearch.setFont(UITheme.FONT_SMALL_BOLD);
        btnSearch.addActionListener(e -> applyFilter());
        filterPanel.add(btnSearch);

        // 2. KPI Summary Cards
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiPanel.setBorder(new EmptyBorder(14, 20, 10, 20));
        kpiPanel.setOpaque(false);

        lblTotalStudents = new JLabel("0", SwingConstants.CENTER);
        lblEligibleCount = new JLabel("0", SwingConstants.CENTER);
        lblIneligibleCount = new JLabel("0", SwingConstants.CENTER);
        lblBatchAvg = new JLabel("0.0%", SwingConstants.CENTER);

        kpiPanel.add(createKpiCard("Total Batch Students", lblTotalStudents, new Color(30, 41, 59)));
        kpiPanel.add(createKpiCard("Eligible (>= 80%)", lblEligibleCount, new Color(16, 185, 129)));
        kpiPanel.add(createKpiCard("Not Eligible (< 80%)", lblIneligibleCount, new Color(225, 29, 72)));
        kpiPanel.add(createKpiCard("Batch Average %", lblBatchAvg, new Color(14, 165, 233)));

        // 3. Table Panel
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
        summaryTable.setRowHeight(30);
        summaryTable.setFont(UITheme.FONT_BODY);
        summaryTable.getTableHeader().setFont(UITheme.FONT_BODY_BOLD);

        // Custom Cell Renderer for Eligibility Column
        summaryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row % 2 == 0) c.setBackground(Color.WHITE);
                    else c.setBackground(new Color(248, 250, 252));

                    if (column == 7) { // Eligibility status column
                        String status = String.valueOf(value);
                        if ("ELIGIBLE".equalsIgnoreCase(status)) {
                            c.setForeground(new Color(16, 185, 129));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else {
                            c.setForeground(new Color(225, 29, 72));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        }
                    } else if (column == 6) { // Percentage column
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                        c.setForeground(new Color(30, 41, 59));
                    } else {
                        c.setForeground(new Color(30, 41, 59));
                        c.setFont(c.getFont().deriveFont(Font.PLAIN));
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(summaryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(new EmptyBorder(0, 20, 10, 20));
        mainContentPanel.add(kpiPanel, BorderLayout.NORTH);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        // 4. Bottom Controls Bar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnExportCsv = new JButton("📥 Export to CSV");
        btnExportCsv.setBackground(new Color(16, 185, 129));
        btnExportCsv.setForeground(Color.WHITE);
        btnExportCsv.setFont(UITheme.FONT_SMALL_BOLD);
        btnExportCsv.setFocusPainted(false);
        btnExportCsv.addActionListener(e -> exportToCsv());

        JButton btnRefresh = new JButton("🔄 Refresh Data");
        btnRefresh.setFont(UITheme.FONT_SMALL_BOLD);
        btnRefresh.addActionListener(e -> loadAttendanceData());

        bottomPanel.add(btnExportCsv);
        bottomPanel.add(btnRefresh);

        add(filterPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createKpiCard(String label, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel titleLbl = new JLabel(label);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void loadCourses() {
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

    public void loadAttendanceData() {
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
            writer.println("Registration No,Student Name,Component,Sessions Held,Attended Sessions,Approved Medicals,Attendance Percentage,Eligibility Status");

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
