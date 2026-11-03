package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.InvalidMarkException;
import com.faculty.management.service.MarkService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MarkEntryDialog extends JDialog {
    private JTextField txtStudentId;
    private JTextField txtEvaluationId;
    private JTextField txtMarks;
    private JButton btnSave;
    private JButton btnCancel;

    private final MarkService markService = new MarkService();
    private boolean successful = false;

    public MarkEntryDialog(Frame parent, String defaultStudentId, String defaultEvalId, String defaultMarks) {
        super(parent, "Enter / Edit Student Marks", true);
        setLayout(new BorderLayout());
        setSize(400, 280);
        setLocationRelativeTo(parent);

        initUI(defaultStudentId, defaultEvalId, defaultMarks);
    }

    private void initUI(String defaultStudentId, String defaultEvalId, String defaultMarks) {
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        txtStudentId = new JTextField(defaultStudentId);
        txtEvaluationId = new JTextField(defaultEvalId);
        txtMarks = new JTextField(defaultMarks);

        // Lock Student ID and Evaluation ID if we are editing an existing row
        if (defaultStudentId != null && !defaultStudentId.isEmpty()) {
            txtStudentId.setEditable(false);
            txtEvaluationId.setEditable(false);
        }

        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(txtStudentId);
        formPanel.add(new JLabel("Evaluation ID:"));
        formPanel.add(txtEvaluationId);
        formPanel.add(new JLabel("Marks (0 - 100):"));
        formPanel.add(txtMarks);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnSave = new JButton("Save Mark");
        btnSave.setBackground(new Color(13, 148, 136));
        btnSave.setForeground(Color.WHITE);

        btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> saveAction());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveAction() {
        String studentId = txtStudentId.getText().trim();
        String evalIdStr = txtEvaluationId.getText().trim();
        String marksStr = txtMarks.getText().trim();

        if (studentId.isEmpty() || evalIdStr.isEmpty() || marksStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int evaluationId = Integer.parseInt(evalIdStr);
            double marks = Double.parseDouble(marksStr);

            // Calls Service layer for range validation & database persistence
            markService.saveOrUpdateMark(studentId, evaluationId, marks);
            successful = true;

            JOptionPane.showMessageDialog(this, "Mark saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Numeric Input! Please enter numbers for Evaluation ID and Marks.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidMarkException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Range Error (0-100)", JOptionPane.ERROR_MESSAGE);
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, "Database Failure: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving mark: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccessful() {
        return successful;
    }
}