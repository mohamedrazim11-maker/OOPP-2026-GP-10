package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.model.Eligibility;
import com.faculty.management.service.EligibilityService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EligibilityPanel extends JPanel {
    private JTextField txtCourseFilter;
    private JComboBox<String> cmbStatusFilter;
    private JButton btnFilter;
    private JTable batchTable;
    private DefaultTableModel tableModel;

    private final EligibilityService eligibilityService = new EligibilityService();

    public EligibilityPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initUI();
        loadBatchData();
    }

    private void initUI() {
        // Filter Bar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(new Color(241, 245, 249));

        txtCourseFilter = new JTextField(10);
        cmbStatusFilter = new JComboBox<>(new String[]{"All", "Eligible", "Not Eligible"});
        btnFilter = new JButton("Apply Filters");
        btnFilter.setBackground(new Color(15, 118, 110));
        btnFilter.setForeground(Color.WHITE);

        filterPanel.add(new JLabel("Course ID (Numeric):"));
        filterPanel.add(txtCourseFilter);
        filterPanel.add(new JLabel("CA Status:"));
        filterPanel.add(cmbStatusFilter);
        filterPanel.add(btnFilter);

        // Whole-Batch Table Model
        tableModel = new DefaultTableModel(new String[]{"Student ID", "Course ID", "CA Percentage", "Final Exam Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        batchTable = new JTable(tableModel);
        batchTable.setRowHeight(25);

        btnFilter.addActionListener(e -> loadBatchData());

        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(batchTable), BorderLayout.CENTER);
    }

    private void loadBatchData() {
        String courseStr = txtCourseFilter.getText().trim();
        String status = (String) cmbStatusFilter.getSelectedItem();

        Integer courseFilter = null;
        if (!courseStr.isEmpty()) {
            try {
                courseFilter = Integer.parseInt(courseStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Course ID must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            List<Eligibility> batchList = eligibilityService.getBatchEligibility(courseFilter, status);
            tableModel.setRowCount(0);

            for (Eligibility e : batchList) {
                tableModel.addRow(new Object[]{
                        e.getStudentId(),
                        e.getCourseId(),
                        String.format("%.2f%%", e.getCaPercentage()),
                        e.getStatusString()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load batch summary: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
