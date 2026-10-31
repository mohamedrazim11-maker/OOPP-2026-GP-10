package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.gui.common.PrimaryButton;
import com.faculty.management.gui.common.UITheme;
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
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initUI();
    }

    private void initUI() {
        // Controls Header
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(6, 12, 6, 12)
        ));

        txtStudentId = new JTextField(8);
        txtStudentId.setFont(UITheme.FONT_BODY);
        txtStudentId.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));

        txtCourseId = new JTextField(8);
        txtCourseId.setFont(UITheme.FONT_BODY);
        txtCourseId.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));

        txtMaxCAMarks = new JTextField("100", 5);
        txtMaxCAMarks.setFont(UITheme.FONT_BODY);
        txtMaxCAMarks.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));

        btnCalculate = new PrimaryButton("Calculate");
        btnCalculate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCalculate.setPreferredSize(new Dimension(110, 32));

        JLabel lblStd = new JLabel("Student ID:");
        lblStd.setFont(UITheme.FONT_SMALL_BOLD);
        lblStd.setForeground(UITheme.TEXT_BODY);

        JLabel lblCrs = new JLabel("Course ID:");
        lblCrs.setFont(UITheme.FONT_SMALL_BOLD);
        lblCrs.setForeground(UITheme.TEXT_BODY);

        JLabel lblMax = new JLabel("Max CA Marks:");
        lblMax.setFont(UITheme.FONT_SMALL_BOLD);
        lblMax.setForeground(UITheme.TEXT_BODY);

        inputPanel.add(lblStd);
        inputPanel.add(txtStudentId);
        inputPanel.add(lblCrs);
        inputPanel.add(txtCourseId);
        inputPanel.add(lblMax);
        inputPanel.add(txtMaxCAMarks);
        inputPanel.add(btnCalculate);

        // Marks Breakdown Table
        tableModel = new DefaultTableModel(new String[]{"Evaluation ID", "Marks Obtained"}, 0);
        marksTable = new JTable(tableModel);
        UITheme.styleTable(marksTable);

        // Result Summary Card
        JPanel resultCard = new JPanel(new GridLayout(3, 1, 6, 6));
        resultCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(14, 18, 14, 18)
        ));
        resultCard.setBackground(Color.WHITE);

        lblTotalObtained = new JLabel("Total Obtained CA Marks: -");
        lblCAPercentage = new JLabel("Calculated CA Percentage: - %");
        lblStatus = new JLabel("Exam Eligibility Status: -");

        lblTotalObtained.setFont(UITheme.FONT_BODY_BOLD);
        lblTotalObtained.setForeground(UITheme.TEXT_MAIN);
        lblCAPercentage.setFont(UITheme.FONT_BODY_BOLD);
        lblCAPercentage.setForeground(UITheme.TEXT_MAIN);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblStatus.setForeground(UITheme.PRIMARY);

        resultCard.add(lblTotalObtained);
        resultCard.add(lblCAPercentage);
        resultCard.add(lblStatus);

        btnCalculate.addActionListener(e -> processCACalculation());

        add(inputPanel, BorderLayout.NORTH);
        add(UITheme.createModernScrollPane(marksTable), BorderLayout.CENTER);
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

            List<Mark> studentMarks = eligibilityService.getStudentMarks(studentIdStr, courseIdStr);

            tableModel.setRowCount(0);
            double sum = 0.0;
            int count = 0;

            for (Mark m : studentMarks) {
                tableModel.addRow(new Object[]{m.getEvaluationId(), m.getMarksObtained()});
                sum += m.getMarksObtained();
                count++;
            }

            // Average of all evaluation components, scaled to maxMarks
            double totalObtained = (count == 0) ? 0.0 : (sum / count) * (maxMarks / 100.0);

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