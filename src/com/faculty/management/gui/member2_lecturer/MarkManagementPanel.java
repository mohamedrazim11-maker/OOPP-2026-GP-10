package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.gui.common.DangerButton;
import com.faculty.management.gui.common.PrimaryButton;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Mark;
import com.faculty.management.service.MarkService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MarkManagementPanel extends JPanel {
    private JTextField txtSearchStudent;
    private JTextField txtSearchEval;
    private JButton btnSearchStudent;
    private JButton btnSearchEval;
    private JButton btnAddMark;
    private JButton btnEditMark;
    private JButton btnDeleteMark;

    private JTable markTable;
    private DefaultTableModel tableModel;

    private final MarkService markService = new MarkService();

    public MarkManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initUI();
    }

    private void initUI() {
        // Top Toolbar Panel (Student Lookup & Filters)
        JPanel topToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topToolbar.setOpaque(false);

        txtSearchStudent = new JTextField(10);
        txtSearchStudent.setFont(UITheme.FONT_BODY);
        txtSearchStudent.setPreferredSize(new Dimension(110, 32));
        txtSearchStudent.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(2, 6, 2, 6)
        ));

        btnSearchStudent = new JButton("Search Student");
        btnSearchStudent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearchStudent.setBackground(Color.WHITE);
        btnSearchStudent.setForeground(UITheme.TEXT_BODY);
        btnSearchStudent.setFocusPainted(false);
        btnSearchStudent.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearchStudent.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(5, 12, 5, 12)
        ));

        txtSearchEval = new JTextField(8);
        txtSearchEval.setFont(UITheme.FONT_BODY);
        txtSearchEval.setPreferredSize(new Dimension(90, 32));
        txtSearchEval.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(2, 6, 2, 6)
        ));

        btnSearchEval = new JButton("Search Evaluation");
        btnSearchEval.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearchEval.setBackground(Color.WHITE);
        btnSearchEval.setForeground(UITheme.TEXT_BODY);
        btnSearchEval.setFocusPainted(false);
        btnSearchEval.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearchEval.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(5, 12, 5, 12)
        ));

        JLabel lblStudent = new JLabel("Student ID:");
        lblStudent.setFont(UITheme.FONT_SMALL_BOLD);
        lblStudent.setForeground(UITheme.TEXT_BODY);

        JLabel lblEval = new JLabel("Eval ID:");
        lblEval.setFont(UITheme.FONT_SMALL_BOLD);
        lblEval.setForeground(UITheme.TEXT_BODY);

        topToolbar.add(lblStudent);
        topToolbar.add(txtSearchStudent);
        topToolbar.add(btnSearchStudent);
        topToolbar.add(Box.createHorizontalStrut(8));
        topToolbar.add(lblEval);
        topToolbar.add(txtSearchEval);
        topToolbar.add(btnSearchEval);

        // Table Model Setup
        tableModel = new DefaultTableModel(new String[]{"Student ID", "Evaluation ID", "Marks Obtained"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        markTable = new JTable(tableModel);
        UITheme.styleTable(markTable);

        // Bottom Action Panel per Wireframe 04: [ + Add Marks ] [ Edit Selected ] [ Delete Selected ]
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bottomPanel.setOpaque(false);

        btnAddMark = new PrimaryButton("+ Add Marks");
        btnAddMark.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddMark.setPreferredSize(new Dimension(130, 34));

        btnEditMark = new JButton("Edit Selected");
        btnEditMark.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEditMark.setBackground(Color.WHITE);
        btnEditMark.setForeground(UITheme.TEXT_BODY);
        btnEditMark.setFocusPainted(false);
        btnEditMark.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditMark.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

        btnDeleteMark = new DangerButton("Delete Selected");
        btnDeleteMark.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDeleteMark.setPreferredSize(new Dimension(140, 34));

        bottomPanel.add(btnAddMark);
        bottomPanel.add(btnEditMark);
        bottomPanel.add(btnDeleteMark);

        // Event Listeners
        btnSearchStudent.addActionListener(e -> loadStudentMarks());
        btnSearchEval.addActionListener(e -> loadEvaluationMarks());
        btnAddMark.addActionListener(e -> openAddDialog());
        btnEditMark.addActionListener(e -> openEditDialog());
        btnDeleteMark.addActionListener(e -> deleteSelectedMark());

        add(topToolbar, BorderLayout.NORTH);
        add(UITheme.createModernScrollPane(markTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadStudentMarks() {
        String studentId = txtSearchStudent.getText().trim();
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID to search.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            List<Mark> list = markService.getMarksForStudent(studentId);
            populateTable(list);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load marks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEvaluationMarks() {
        String evalStr = txtSearchEval.getText().trim();
        if (evalStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Evaluation ID.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            int evalId = Integer.parseInt(evalStr);
            List<Mark> list = markService.getMarksForEvaluation(evalId);
            populateTable(list);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Evaluation ID must be an integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load marks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Mark> marks) {
        tableModel.setRowCount(0);
        for (Mark m : marks) {
            tableModel.addRow(new Object[]{m.getStudentId(), m.getEvaluationId(), m.getMarksObtained()});
        }
    }

    private void openAddDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        MarkEntryDialog dialog = new MarkEntryDialog(parentFrame, "", "", "");
        dialog.setVisible(true);

        if (dialog.isSuccessful()) {
            if (!txtSearchStudent.getText().trim().isEmpty()) {
                loadStudentMarks();
            }
        }
    }

    private void openEditDialog() {
        int selectedRow = markTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a mark row to edit.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = tableModel.getValueAt(selectedRow, 0).toString();
        String evalId = tableModel.getValueAt(selectedRow, 1).toString();
        String currentMarks = tableModel.getValueAt(selectedRow, 2).toString();

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        MarkEntryDialog dialog = new MarkEntryDialog(parentFrame, studentId, evalId, currentMarks);
        dialog.setVisible(true);

        if (dialog.isSuccessful()) {
            loadStudentMarks();
        }
    }

    private void deleteSelectedMark() {
        int selectedRow = markTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = tableModel.getValueAt(selectedRow, 0).toString();
        int evalId = Integer.parseInt(tableModel.getValueAt(selectedRow, 1).toString());

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete mark for Student: " + studentId + " (Eval ID: " + evalId + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (markService.removeMark(studentId, evalId)) {
                    JOptionPane.showMessageDialog(this, "Mark deleted successfully.");
                    loadStudentMarks();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting mark: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}