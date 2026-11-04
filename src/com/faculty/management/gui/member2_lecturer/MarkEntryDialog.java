package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.InvalidMarkException;
import com.faculty.management.service.MarkService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern Mark Entry Dialog for Member 2.
 * Strictly fulfills Requirement 4:
 * - Enter / Edit Single Evaluation mark (0 - 100)
 * - Enter / Upload Course Component marks: Quiz, Assessment, Mid-semester, Practical, Theory, Final marks (all strictly out of 100)
 * - Validates marks using InvalidMarkException
 */
public class MarkEntryDialog extends JDialog {

    private final MarkService markService = new MarkService();
    private boolean successful = false;

    // Mode 1: Single Evaluation
    private JTextField txtSingleStudentId;
    private JTextField txtSingleEvaluationId;
    private JTextField txtSingleMarks;

    // Mode 2: Course Component Marks
    private JTextField txtCompStudentId;
    private JTextField txtCompCourseId;
    private JTextField txtQuizMarks;
    private JTextField txtAssessmentMarks;
    private JTextField txtMidMarks;
    private JTextField txtPracticalMarks;
    private JTextField txtTheoryMarks;
    private JTextField txtFinalMarks;

    public MarkEntryDialog(Frame parent, String defaultStudentId, String defaultEvalId, String defaultMarks) {
        super(parent, "Enter / Upload Student Marks", true);
        setLayout(new BorderLayout());
        setSize(480, 480);
        setLocationRelativeTo(parent);

        initUI(defaultStudentId, defaultEvalId, defaultMarks);
    }

    private void initUI(String defaultStudentId, String defaultEvalId, String defaultMarks) {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Single Evaluation
        JPanel singlePanel = new JPanel(new BorderLayout());
        singlePanel.setBackground(Color.WHITE);

        JPanel singleForm = new JPanel(new GridLayout(3, 2, 10, 14));
        singleForm.setBorder(new EmptyBorder(20, 24, 20, 24));
        singleForm.setOpaque(false);

        txtSingleStudentId = new JTextField(defaultStudentId != null ? defaultStudentId : "");
        txtSingleEvaluationId = new JTextField(defaultEvalId != null ? defaultEvalId : "");
        txtSingleMarks = new JTextField(defaultMarks != null ? defaultMarks : "");

        if (defaultStudentId != null && !defaultStudentId.isEmpty() && defaultEvalId != null && !defaultEvalId.isEmpty()) {
            txtSingleStudentId.setEditable(false);
            txtSingleEvaluationId.setEditable(false);
        }

        singleForm.add(createBoldLabel("Student ID / Reg No:"));
        singleForm.add(txtSingleStudentId);
        singleForm.add(createBoldLabel("Evaluation ID (1-8):"));
        singleForm.add(txtSingleEvaluationId);
        singleForm.add(createBoldLabel("Marks Obtained (0 - 100):"));
        singleForm.add(txtSingleMarks);

        JPanel singleBtnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        singleBtnRow.setBackground(new Color(249, 250, 251));
        singleBtnRow.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));

        JButton btnSingleCancel = new JButton("Cancel");
        btnSingleCancel.addActionListener(e -> dispose());

        JButton btnSingleSave = new JButton("Save Evaluation Mark");
        btnSingleSave.setBackground(new Color(114, 28, 36));
        btnSingleSave.setForeground(Color.WHITE);
        btnSingleSave.addActionListener(e -> saveSingleEvaluation());

        singleBtnRow.add(btnSingleCancel);
        singleBtnRow.add(btnSingleSave);

        singlePanel.add(singleForm, BorderLayout.CENTER);
        singlePanel.add(singleBtnRow, BorderLayout.SOUTH);

        // Tab 2: Course Component Marks (Requirement 4)
        JPanel compPanel = new JPanel(new BorderLayout());
        compPanel.setBackground(Color.WHITE);

        JPanel compForm = new JPanel(new GridLayout(8, 2, 10, 8));
        compForm.setBorder(new EmptyBorder(16, 24, 16, 24));
        compForm.setOpaque(false);

        txtCompStudentId = new JTextField(defaultStudentId != null ? defaultStudentId : "32");
        txtCompCourseId = new JTextField("6");
        txtQuizMarks = new JTextField();
        txtAssessmentMarks = new JTextField();
        txtMidMarks = new JTextField();
        txtPracticalMarks = new JTextField();
        txtTheoryMarks = new JTextField();
        txtFinalMarks = new JTextField();

        compForm.add(createBoldLabel("Student ID:"));
        compForm.add(txtCompStudentId);
        compForm.add(createBoldLabel("Course ID / Code:"));
        compForm.add(txtCompCourseId);
        compForm.add(createBoldLabel("Quiz Marks (0-100):"));
        compForm.add(txtQuizMarks);
        compForm.add(createBoldLabel("Assessment Marks (0-100):"));
        compForm.add(txtAssessmentMarks);
        compForm.add(createBoldLabel("Mid-Semester Marks (0-100):"));
        compForm.add(txtMidMarks);
        compForm.add(createBoldLabel("Practical Marks (0-100):"));
        compForm.add(txtPracticalMarks);
        compForm.add(createBoldLabel("Theory Marks (0-100):"));
        compForm.add(txtTheoryMarks);
        compForm.add(createBoldLabel("Final Marks (0-100):"));
        compForm.add(txtFinalMarks);

        JPanel compBtnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        compBtnRow.setBackground(new Color(249, 250, 251));
        compBtnRow.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));

        JButton btnCompCancel = new JButton("Cancel");
        btnCompCancel.addActionListener(e -> dispose());

        JButton btnCompSave = new JButton("Save Course Component Marks");
        btnCompSave.setBackground(new Color(114, 28, 36));
        btnCompSave.setForeground(Color.WHITE);
        btnCompSave.addActionListener(e -> saveCourseComponents());

        compBtnRow.add(btnCompCancel);
        compBtnRow.add(btnCompSave);

        compPanel.add(compForm, BorderLayout.CENTER);
        compPanel.add(compBtnRow, BorderLayout.SOUTH);

        tabbedPane.addTab("Single Evaluation", singlePanel);
        tabbedPane.addTab("Course Components (Quiz, Mid, Practical, Theory, Final)", compPanel);

        // If not editing an existing evaluation, default to course components
        if (defaultEvalId == null || defaultEvalId.isEmpty()) {
            tabbedPane.setSelectedIndex(1);
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JLabel createBoldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return lbl;
    }

    private void saveSingleEvaluation() {
        String studentId = txtSingleStudentId.getText().trim();
        String evalIdStr = txtSingleEvaluationId.getText().trim();
        String marksStr = txtSingleMarks.getText().trim();

        if (studentId.isEmpty() || evalIdStr.isEmpty() || marksStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int evaluationId = Integer.parseInt(evalIdStr);
            double marks = Double.parseDouble(marksStr);

            markService.saveOrUpdateMark(studentId, evaluationId, marks);
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

    private void saveCourseComponents() {
        String studentId = txtCompStudentId.getText().trim();
        String courseIdStr = txtCompCourseId.getText().trim();

        if (studentId.isEmpty() || courseIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please specify Student ID and Course ID/Code.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int courseId = Integer.parseInt(courseIdStr);
            Double quiz = parseOptionalDouble(txtQuizMarks.getText().trim());
            Double assessment = parseOptionalDouble(txtAssessmentMarks.getText().trim());
            Double mid = parseOptionalDouble(txtMidMarks.getText().trim());
            Double practical = parseOptionalDouble(txtPracticalMarks.getText().trim());
            Double theory = parseOptionalDouble(txtTheoryMarks.getText().trim());
            Double finalExam = parseOptionalDouble(txtFinalMarks.getText().trim());

            markService.saveCourseComponentMarks(studentId, courseId, quiz, assessment, mid, practical, theory, finalExam);
            successful = true;

            JOptionPane.showMessageDialog(this, "Course component marks saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Number Format! Course ID and Marks must be numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidMarkException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Range Error (0-100)", JOptionPane.ERROR_MESSAGE);
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, "Database Failure: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving component marks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Double parseOptionalDouble(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        return Double.parseDouble(text.trim());
    }

    public boolean isSuccessful() {
        return successful;
    }
}
