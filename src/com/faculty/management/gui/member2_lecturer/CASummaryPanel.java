package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.model.Eligibility;
import com.faculty.management.model.Mark;
import com.faculty.management.service.EligibilityService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CASummaryPanel extends JPanel {
    private JTextField txtStudentId, txtCourseId, txtMaxCAMarks;
    private JButton btnCalculate;
    private JLabel lblTotalObtained, lblCAPercentage, lblStatus;
    private JTable marksTable;
    private DefaultTableModel tableModel;

    private final EligibilityService eligibilityService = new EligibilityService();

    public CASummaryPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initUI();
    }

    private void initUI() {
        // Controls Header
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBackground(new Color(241, 245, 249));

        txtStudentId = new JTextField(8);
        txtCourseId = new JTextField(8);
        txtMaxCAMarks = new JTextField("100", 5);
        btnCalculate = new JButton("Calculate");
        btnCalculate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCalculate.setBackground(Color.WHITE);
        btnCalculate.setFocusPainted(false);
        btnCalculate.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(148, 163, 184), 1),
                new EmptyBorder(5, 14, 5, 14)
        ));

        inputPanel.add(new JLabel("Student ID:"));
        inputPanel.add(txtStudentId);
        inputPanel.add(new JLabel("Course ID:"));
        inputPanel.add(txtCourseId);
        inputPanel.add(new JLabel("Max CA Marks:"));
        inputPanel.add(txtMaxCAMarks);
        inputPanel.add(btnCalculate);

        // Marks Breakdown Table
        tableModel = new DefaultTableModel(new String[]{"Evaluation ID", "Marks Obtained"}, 0);
        marksTable = new JTable(tableModel);

        // Result Summary Card
        JPanel resultCard = new JPanel(new GridLayout(3, 1, 5, 5));
        resultCard.setBorder(BorderFactory.createTitledBorder("CA Summary Result"));
        resultCard.setBackground(Color.WHITE);

        lblTotalObtained = new JLabel("Total Obtained CA Marks: -");
        lblCAPercentage = new JLabel("Calculated CA Percentage: - %");
        lblStatus = new JLabel("Exam Eligibility Status: -");

        lblTotalObtained.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCAPercentage.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));

        resultCard.add(lblTotalObtained);
        resultCard.add(lblCAPercentage);
        resultCard.add(lblStatus);

        btnCalculate.addActionListener(e -> processCACalculation());

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(marksTable), BorderLayout.CENTER);
        add(resultCard, BorderLayout.SOUTH);
    }

    private void processCACalculation() {
        String studentIdStr = txtStudentId.getText().trim();
        String courseIdStr = txtCourseId.getText().trim();
        String maxStr = txtMaxCAMarks.getText().trim();

        if (studentIdStr.isEmpty() || courseIdStr.isEmpty() || maxStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Student ID, Course ID, and Max CA Marks.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdStr);
            int courseId = Integer.parseInt(courseIdStr);
            double maxMarks = Double.parseDouble(maxStr);

            List<Mark> studentMarks = eligibilityService.getStudentMarks(studentIdStr);

            tableModel.setRowCount(0);
            double totalObtained = 0.0;

            for (Mark m : studentMarks) {
                tableModel.addRow(new Object[]{m.getEvaluationId(), m.getMarksObtained()});
                totalObtained += m.getMarksObtained();
            }

            // Calls updated service accepting (int, int, double, double)
            Eligibility result = eligibilityService.calculateAndSaveEligibility(studentId, courseId, totalObtained, maxMarks);

            lblTotalObtained.setText(String.format("Total Obtained CA Marks: %.2f / %.2f", totalObtained, maxMarks));
            lblCAPercentage.setText(String.format("Calculated CA Percentage: %.2f%%", result.getCaPercentage()));
            lblStatus.setText("Exam Eligibility Status: " + result.getStatusString().toUpperCase());

            if (result.isEligible()) {
                lblStatus.setForeground(new Color(22, 101, 52)); // Dark Green
            } else {
                lblStatus.setForeground(new Color(185, 28, 28)); // Dark Red
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID, Course ID, and Max Marks must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error processing CA: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}