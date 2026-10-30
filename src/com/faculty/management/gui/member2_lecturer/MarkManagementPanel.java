package com.faculty.management.gui.member2_lecturer;

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
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initUI();
    }

    private void initUI() {
        // Top Toolbar Panel (Student Lookup & Filters)
        JPanel topToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topToolbar.setOpaque(false);

        txtSearchStudent = new JTextField(10);
        txtSearchStudent.setPreferredSize(new Dimension(110, 32));
        btnSearchStudent = new JButton("Search Student");
        btnSearchStudent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSearchStudent.setBackground(Color.WHITE);
        btnSearchStudent.setFocusPainted(false);
        btnSearchStudent.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(148, 163, 184), 1),
                new EmptyBorder(5, 12, 5, 12)
        ));

        txtSearchEval = new JTextField(8);
        txtSearchEval.setPreferredSize(new Dimension(90, 32));
        btnSearchEval = new JButton("Search Evaluation");
        btnSearchEval.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSearchEval.setBackground(Color.WHITE);
        btnSearchEval.setFocusPainted(false);
        btnSearchEval.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(148, 163, 184), 1),
                new EmptyBorder(5, 12, 5, 12)
        ));

        JLabel lblStudent = new JLabel("Student ID:");
        lblStudent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStudent.setForeground(new Color(51, 65, 85));

        JLabel lblEval = new JLabel("Eval ID:");
        lblEval.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEval.setForeground(new Color(51, 65, 85));

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
        markTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        markTable.setRowHeight(32);
        markTable.setShowGrid(true);
        markTable.setGridColor(new Color(226, 232, 240));
        markTable.setSelectionBackground(new Color(224, 231, 255));
        markTable.setSelectionForeground(new Color(15, 23, 42));

        // Bottom Action Panel per Wireframe 04: [ + Add Marks ] [ Edit Selected ] [ Delete Selected ]
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bottomPanel.setOpaque(false);

        btnAddMark = new JButton("+ Add Marks");
        btnAddMark.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAddMark.setBackground(Color.WHITE);
        btnAddMark.setFocusPainted(false);
        btnAddMark.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(148, 163, 184), 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

        btnEditMark = new JButton("Edit Selected");
        btnEditMark.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnEditMark.setBackground(Color.WHITE);
        btnEditMark.setFocusPainted(false);
        btnEditMark.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(148, 163, 184), 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

        btnDeleteMark = new JButton("Delete Selected");
        btnDeleteMark.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnDeleteMark.setBackground(Color.WHITE);
        btnDeleteMark.setFocusPainted(false);
        btnDeleteMark.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(148, 163, 184), 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

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
        add(new JScrollPane(markTable), BorderLayout.CENTER);
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