package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.InvalidMarkException;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.service.MarkService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog for entering and editing student marks across all required evaluation types:
 * Quiz, Assessment, Mid-semester, Practical, Theory, and Final marks.
 * Enforces rule: All marks must be out of 100.
 */
public class MarkEntryDialog extends JDialog {
    private JTextField txtStudentId;
    private JTextField txtCourseId;
    private JComboBox<String> cmbEvaluationType;
    private JTextField txtEvaluationId;
    private JTextField txtMarks;
    private JButton btnSave;
    private JButton btnCancel;

    private final MarkService markService = new MarkService();
    private boolean successful = false;

    // Component evaluation mappings per Requirement 4
    public static final String[] EVAL_TYPES = {
            "Quiz Marks (Component: Quiz)",
            "Assessment Marks (Component: Assignment/CA)",
            "Mid-Semester Marks (Component: Mid-term Exam)",
            "Practical Marks (Component: Lab/Practical)",
            "Theory Marks (Component: Theory Exam)",
            "Final Marks (Component: End-Semester Exam)"
    };

    public MarkEntryDialog(Frame parent, String defaultStudentId, String defaultEvalId, String defaultMarks) {
        super(parent, "Enter / Edit Student Marks", true);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(460, 360));
        setResizable(false);
        setLocationRelativeTo(parent);

        initUI(defaultStudentId, defaultEvalId, defaultMarks);
    }

    private void initUI(String defaultStudentId, String defaultEvalId, String defaultMarks) {
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 12, 14));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(24, 24, 18, 24));

        txtStudentId = new JTextField(defaultStudentId != null ? defaultStudentId : "");
        txtStudentId.setFont(UITheme.FONT_BODY);

        txtCourseId = new JTextField("1");
        txtCourseId.setFont(UITheme.FONT_BODY);

        cmbEvaluationType = new JComboBox<>(EVAL_TYPES);
        cmbEvaluationType.setFont(UITheme.FONT_BODY);

        txtEvaluationId = new JTextField(defaultEvalId != null && !defaultEvalId.isEmpty() ? defaultEvalId : "1");
        txtEvaluationId.setFont(UITheme.FONT_BODY);

        cmbEvaluationType.addActionListener(e -> {
            int selectedIdx = cmbEvaluationType.getSelectedIndex();
            txtEvaluationId.setText(String.valueOf(selectedIdx + 1));
        });

        // Set matching combo index if editing existing eval
        if (defaultEvalId != null && !defaultEvalId.isEmpty()) {
            try {
                int id = Integer.parseInt(defaultEvalId);
                if (id >= 1 && id <= EVAL_TYPES.length) {
                    cmbEvaluationType.setSelectedIndex(id - 1);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        txtMarks = new JTextField(defaultMarks != null ? defaultMarks : "");
        txtMarks.setFont(UITheme.FONT_BODY);

        // Lock Student ID if we are editing an existing row
        if (defaultStudentId != null && !defaultStudentId.isEmpty()) {
            txtStudentId.setEditable(false);
            txtStudentId.setBackground(new Color(241, 245, 249));
        }

        JLabel lblStd = new JLabel("Student ID / User ID:");
        lblStd.setFont(UITheme.FONT_SMALL_BOLD);
        lblStd.setForeground(UITheme.TEXT_BODY);

        JLabel lblCrs = new JLabel("Course ID (e.g. 1):");
        lblCrs.setFont(UITheme.FONT_SMALL_BOLD);
        lblCrs.setForeground(UITheme.TEXT_BODY);

        JLabel lblType = new JLabel("Evaluation Type:");
        lblType.setFont(UITheme.FONT_SMALL_BOLD);
        lblType.setForeground(UITheme.TEXT_BODY);

        JLabel lblId = new JLabel("Evaluation ID:");
        lblId.setFont(UITheme.FONT_SMALL_BOLD);
        lblId.setForeground(UITheme.TEXT_BODY);

        JLabel lblMark = new JLabel("Marks Obtained (0 - 100):");
        lblMark.setFont(UITheme.FONT_SMALL_BOLD);
        lblMark.setForeground(UITheme.TEXT_BODY);

        formPanel.add(lblStd);
        formPanel.add(txtStudentId);
        formPanel.add(lblCrs);
        formPanel.add(txtCourseId);
        formPanel.add(lblType);
        formPanel.add(cmbEvaluationType);
        formPanel.add(lblId);
        formPanel.add(txtEvaluationId);
        formPanel.add(lblMark);
        formPanel.add(txtMarks);

        // Bottom button row
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBackground(UITheme.BG_PAGE);

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(UITheme.FONT_SMALL_BOLD);
        btnCancel.addActionListener(e -> dispose());

        btnSave = new JButton("Save Mark");
        btnSave.setFont(UITheme.FONT_SMALL_BOLD);
        btnSave.setBackground(new Color(13, 148, 136)); // Teal
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(13, 148, 136), 1),
                new EmptyBorder(6, 16, 6, 16)
        ));
        btnSave.addActionListener(e -> saveAction());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
    }

    private void saveAction() {
        String studentId = txtStudentId.getText().trim();
        String evalIdStr = txtEvaluationId.getText().trim();
        String marksStr = txtMarks.getText().trim();
        String courseStr = txtCourseId.getText().trim();

        if (studentId.isEmpty() || evalIdStr.isEmpty() || marksStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int evaluationId = Integer.parseInt(evalIdStr);
            double marks = Double.parseDouble(marksStr);
            int courseId = courseStr.isEmpty() ? 1 : Integer.parseInt(courseStr);

            // Domain rule validation (Strictly 0 - 100)
            if (marks < 0.0 || marks > 100.0) {
                throw new InvalidMarkException("Invalid Mark: " + marks + ". Marks must be between 0 and 100.");
            }

            // Calls Service layer for range validation & database persistence
            markService.saveOrUpdateMark(studentId, evaluationId, marks);

            // Also synchronize with component course marks for Member 4 & Grade calculation
            int studentIdInt = 0;
            try {
                studentIdInt = Integer.parseInt(studentId);
            } catch (NumberFormatException ignored) {
            }

            if (studentIdInt > 0) {
                int evalIdx = cmbEvaluationType.getSelectedIndex();
                Double q = (evalIdx == 0) ? marks : null;
                Double a = (evalIdx == 1) ? marks : null;
                Double m = (evalIdx == 2) ? marks : null;
                Double p = (evalIdx == 3) ? marks : null;
                Double f = (evalIdx == 5) ? marks : null;
                try {
                    markService.saveCourseComponentMarks(studentIdInt, courseId, q, a, m, p, f);
                } catch (Exception ignored) {
                }
            }

            successful = true;
            JOptionPane.showMessageDialog(this, "Mark saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Numeric Input! Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
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