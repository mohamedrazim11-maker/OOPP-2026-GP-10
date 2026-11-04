package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.gui.common.DangerButton;
import com.faculty.management.gui.common.PrimaryButton;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Mark;
import com.faculty.management.service.MarkService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Marks Entry & Management Panel for Member 2.
 * Strictly implements the exact UI and functions shown in the specification:
 * - Search by Student ID (Search Student)
 * - Search by Evaluation ID (Search Evaluation)
 * - Show All / Refresh
 * - Table: Student ID, Evaluation ID, Marks Obtained (fetches and displays real database data)
 * - Actions: [+ Add Marks], [Edit Selected], [Delete Selected]
 */
public class MarkManagementPanel extends JPanel {

    private JTextField txtSearchStudent;
    private JTextField txtSearchEval;
    private JButton btnSearchStudent;
    private JButton btnSearchEval;
    private JButton btnShowAll;

    private JTable markTable;
    private DefaultTableModel tableModel;

    private JButton btnAddMark;
    private JButton btnEditMark;
    private JButton btnDeleteMark;

    private final MarkService markService = new MarkService();

    public MarkManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
        loadAllMarks();
    }

    private void initUI() {
        // 1. Top Toolbar Panel (Student Lookup & Filters)
        JPanel topToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topToolbar.setOpaque(false);

        JLabel lblStudent = new JLabel("Student ID:");
        lblStudent.setFont(UITheme.FONT_SMALL_BOLD);
        lblStudent.setForeground(UITheme.TEXT_BODY);

        txtSearchStudent = new JTextField(10);
        txtSearchStudent.setFont(UITheme.FONT_BODY);
        txtSearchStudent.setPreferredSize(new Dimension(110, 32));
        txtSearchStudent.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        txtSearchStudent.addActionListener(e -> loadStudentMarks());

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
        btnSearchStudent.addActionListener(e -> loadStudentMarks());

        JLabel lblEval = new JLabel("Eval ID:");
        lblEval.setFont(UITheme.FONT_SMALL_BOLD);
        lblEval.setForeground(UITheme.TEXT_BODY);

        txtSearchEval = new JTextField(8);
        txtSearchEval.setFont(UITheme.FONT_BODY);
        txtSearchEval.setPreferredSize(new Dimension(90, 32));
        txtSearchEval.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        txtSearchEval.addActionListener(e -> loadEvaluationMarks());

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
        btnSearchEval.addActionListener(e -> loadEvaluationMarks());

        btnShowAll = new JButton("Show All Marks");
        btnShowAll.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnShowAll.setBackground(Color.WHITE);
        btnShowAll.setForeground(UITheme.TEXT_BODY);
        btnShowAll.setFocusPainted(false);
        btnShowAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnShowAll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(5, 12, 5, 12)
        ));
        btnShowAll.addActionListener(e -> {
            txtSearchStudent.setText("");
            txtSearchEval.setText("");
            loadAllMarks();
        });

        topToolbar.add(lblStudent);
        topToolbar.add(txtSearchStudent);
        topToolbar.add(btnSearchStudent);
        topToolbar.add(Box.createHorizontalStrut(8));
        topToolbar.add(lblEval);
        topToolbar.add(txtSearchEval);
        topToolbar.add(btnSearchEval);
        topToolbar.add(Box.createHorizontalStrut(8));
        topToolbar.add(btnShowAll);

        // 2. Table Model Setup
        tableModel = new DefaultTableModel(new String[]{"Student ID", "Evaluation ID", "Marks Obtained"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        markTable = new JTable(tableModel);
        UITheme.styleTable(markTable);
        markTable.setRowHeight(32);

        DefaultTableCellRenderer leftAlign = new DefaultTableCellRenderer();
        leftAlign.setHorizontalAlignment(SwingConstants.LEFT);
        leftAlign.setBorder(new EmptyBorder(0, 8, 0, 0));
        markTable.getColumnModel().getColumn(0).setCellRenderer(leftAlign);
        markTable.getColumnModel().getColumn(1).setCellRenderer(leftAlign);
        markTable.getColumnModel().getColumn(2).setCellRenderer(leftAlign);

        // 3. Bottom Action Panel matching the image: [ + Add Marks ] [ Edit Selected ] [ Delete Selected ]
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bottomPanel.setOpaque(false);

        btnAddMark = new PrimaryButton("+ Add Marks");
        btnAddMark.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddMark.setPreferredSize(new Dimension(130, 34));
        btnAddMark.addActionListener(e -> openAddDialog());

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
        btnEditMark.addActionListener(e -> openEditDialog());

        btnDeleteMark = new DangerButton("Delete Selected");
        btnDeleteMark.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDeleteMark.setPreferredSize(new Dimension(140, 34));
        btnDeleteMark.addActionListener(e -> deleteSelectedMark());

        bottomPanel.add(btnAddMark);
        bottomPanel.add(btnEditMark);
        bottomPanel.add(btnDeleteMark);

        add(topToolbar, BorderLayout.NORTH);
        add(UITheme.createModernScrollPane(markTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads all marks from the database and populates the table.
     */
    public void loadAllMarks() {
        try {
            List<Mark> marks = markService.getAllMarks();
            populateTable(marks);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load marks from database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudentMarks() {
        String studentId = txtSearchStudent.getText().trim();
        if (studentId.isEmpty()) {
            loadAllMarks();
            return;
        }

        try {
            List<Mark> list = markService.getMarksForStudent(studentId);
            populateTable(list);
            if (list == null || list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No marks found for Student ID: " + studentId, "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load student marks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEvaluationMarks() {
        String evalStr = txtSearchEval.getText().trim();
        if (evalStr.isEmpty()) {
            loadAllMarks();
            return;
        }

        try {
            int evalId = Integer.parseInt(evalStr);
            List<Mark> list = markService.getMarksForEvaluation(evalId);
            populateTable(list);
            if (list == null || list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No marks found for Evaluation ID: " + evalId, "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Evaluation ID must be an integer number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load evaluation marks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Mark> marks) {
        tableModel.setRowCount(0);
        if (marks != null) {
            for (Mark m : marks) {
                tableModel.addRow(new Object[]{
                        m.getStudentId(),
                        m.getEvaluationId(),
                        String.format("%.1f", m.getMarksObtained())
                });
            }
        }
    }

    private void openAddDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        MarkEntryDialog dialog = new MarkEntryDialog(parentFrame, "", "", "");
        dialog.setVisible(true);

        if (dialog.isSuccessful()) {
            loadAllMarks();
        }
    }

    private void openEditDialog() {
        int selectedRow = markTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a mark row from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = tableModel.getValueAt(selectedRow, 0).toString();
        String evalId = tableModel.getValueAt(selectedRow, 1).toString();
        String currentMarks = tableModel.getValueAt(selectedRow, 2).toString();

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        MarkEntryDialog dialog = new MarkEntryDialog(parentFrame, studentId, evalId, currentMarks);
        dialog.setVisible(true);

        if (dialog.isSuccessful()) {
            loadAllMarks();
        }
    }

    private void deleteSelectedMark() {
        int selectedRow = markTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a mark row from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = tableModel.getValueAt(selectedRow, 0).toString();
        String evalIdStr = tableModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete mark for Student: " + studentId + " (Evaluation ID: " + evalIdStr + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int evalId = Integer.parseInt(evalIdStr);
                if (markService.removeMark(studentId, evalId)) {
                    JOptionPane.showMessageDialog(this, "Mark deleted successfully from database.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAllMarks();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete mark.", "Notice", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting mark: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
