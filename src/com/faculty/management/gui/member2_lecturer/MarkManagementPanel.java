package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.model.Mark;
import com.faculty.management.service.MarkService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.faculty.management.gui.member2_lecturer.LecturerUIComponents.*;

/**
 * Marks Entry & Management Panel for Member 2.
 * Strictly implements Page 4 of the Ruhuna Lecturer Portal Specification.
 */
public class MarkManagementPanel extends JPanel {

    private final MarkService markService = new MarkService();

    private SearchTextField searchStudentField;
    private SearchTextField searchEvalField;
    private JTable markTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel footerSummaryLabel;

    public MarkManagementPanel() {
        initComponents();
        loadAllMarks();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(COLOR_PAGE_BG);
        setBorder(new EmptyBorder(0, 28, 20, 28));

        // 1. Header: Title, Subtitle, and "+ Add marks" button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("Marks Entry");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(COLOR_TEXT_DARK);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pageTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel pageSub = new JLabel("Add, edit and remove student marks by evaluation.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);
        pageSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        pageSub.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        BurgundyButton addMarksBtn = new BurgundyButton("+ Add marks");
        addMarksBtn.setPreferredSize(new Dimension(135, 38));
        addMarksBtn.addActionListener(e -> openAddDialog());

        headerPanel.add(titleBox, BorderLayout.CENTER);
        headerPanel.add(addMarksBtn, BorderLayout.EAST);

        // 2. Filter Toolbar
        JPanel filterBar = new JPanel(new BorderLayout(10, 0));
        filterBar.setOpaque(false);

        searchStudentField = new SearchTextField("Search by student ID", 18);
        searchStudentField.setPreferredSize(new Dimension(200, 36));
        searchStudentField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        searchEvalField = new SearchTextField("Search by evaluation ID", 18);
        searchEvalField.setPreferredSize(new Dimension(200, 36));
        searchEvalField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        WhiteOutlineButton refreshBtn = createRefreshButton();
        refreshBtn.setPreferredSize(new Dimension(100, 36));
        refreshBtn.addActionListener(e -> {
            searchStudentField.setText("");
            searchEvalField.setText("");
            loadAllMarks();
        });

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftFilters.setOpaque(false);
        leftFilters.add(searchStudentField);
        leftFilters.add(searchEvalField);

        filterBar.add(leftFilters, BorderLayout.WEST);
        filterBar.add(refreshBtn, BorderLayout.EAST);

        // 3. Table Container
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setOpaque(false);

        String[] columns = {"STUDENT ID", "EVALUATION ID", "MARKS OBTAINED", "ACTIONS", "_DATA_"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        markTable = new JTable(tableModel);
        markTable.setRowHeight(42);
        markTable.setShowVerticalLines(false);
        markTable.setGridColor(new Color(243, 244, 246));
        markTable.setSelectionBackground(new Color(254, 249, 235));
        markTable.setSelectionForeground(COLOR_TEXT_DARK);
        markTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        markTable.setFont(FONT_BODY);

        // Header Styling
        JTableHeader th = markTable.getTableHeader();
        th.setFont(FONT_SMALL_BOLD);
        th.setForeground(COLOR_TEXT_MUTED);
        th.setBackground(Color.WHITE);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Renderers
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(JLabel.LEFT);
        centerRender.setFont(FONT_BODY);

        markTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        markTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setFont(FONT_BODY_BOLD);
                setForeground(COLOR_TEXT_DARK);
                setBorder(new EmptyBorder(0, 12, 0, 0));
                return c;
            }
        });

        markTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        markTable.getColumnModel().getColumn(1).setCellRenderer(centerRender);

        markTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        markTable.getColumnModel().getColumn(2).setCellRenderer(centerRender);

        markTable.getColumnModel().getColumn(3).setPreferredWidth(140);
        markTable.getColumnModel().getColumn(3).setCellRenderer(new ActionsCellRenderer());

        // Hide raw mark data column
        markTable.getColumnModel().getColumn(4).setMinWidth(0);
        markTable.getColumnModel().getColumn(4).setMaxWidth(0);
        markTable.getColumnModel().getColumn(4).setPreferredWidth(0);

        // Actions click listener
        markTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = markTable.columnAtPoint(e.getPoint());
                int row = markTable.rowAtPoint(e.getPoint());
                if (row >= 0 && col == 3) {
                    int modelRow = markTable.convertRowIndexToModel(row);
                    Mark m = (Mark) tableModel.getValueAt(modelRow, 4);
                    if (m == null) return;

                    Rectangle cellRect = markTable.getCellRect(row, col, false);
                    int clickX = e.getX() - cellRect.x;

                    if (clickX < 55) {
                        openEditDialogForMark(m);
                    } else {
                        deleteSelectedMark(m);
                    }
                }
            }
        });

        rowSorter = new TableRowSorter<>(tableModel);
        markTable.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(markTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));

        // Footer Summary
        footerSummaryLabel = new JLabel("Showing 0 of 0 records · Rows offer Edit and Delete");
        footerSummaryLabel.setFont(FONT_SMALL);
        footerSummaryLabel.setForeground(COLOR_TEXT_MUTED);
        footerSummaryLabel.setBorder(new EmptyBorder(4, 4, 4, 4));

        tableContainer.add(filterBar, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        tableContainer.add(footerSummaryLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
    }

    public void loadAllMarks() {
        tableModel.setRowCount(0);
        try {
            // Load sample/database marks
            List<Mark> list = markService.getMarksForStudent("11");
            if (list == null || list.isEmpty()) {
                list = markService.getMarksForStudent("1");
            }

            if (list != null) {
                for (Mark m : list) {
                    tableModel.addRow(new Object[]{
                            m.getStudentId(),
                            String.valueOf(m.getEvaluationId()),
                            String.valueOf(m.getMarksObtained()),
                            "Edit   Delete",
                            m
                    });
                }
            }
        } catch (Exception ignored) {
        }
        updateFooterCount();
    }

    private void applyFilters() {
        String sStudent = searchStudentField.getText().trim().toLowerCase();
        String sEval = searchEvalField.getText().trim().toLowerCase();

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                if (!sStudent.isEmpty()) {
                    String std = entry.getStringValue(0).toLowerCase();
                    if (!std.contains(sStudent)) return false;
                }
                if (!sEval.isEmpty()) {
                    String ev = entry.getStringValue(1).toLowerCase();
                    if (!ev.contains(sEval)) return false;
                }
                return true;
            }
        };

        rowSorter.setRowFilter(rf);
        updateFooterCount();
    }

    private void updateFooterCount() {
        int count = markTable.getRowCount();
        footerSummaryLabel.setText("Showing " + count + " of " + count + " records · Rows offer Edit and Delete");
    }

    private void openAddDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        MarkEntryDialog dialog = new MarkEntryDialog(parentFrame, "", "", "");
        dialog.setVisible(true);

        if (dialog.isSuccessful()) {
            loadAllMarks();
        }
    }

    private void openEditDialogForMark(Mark m) {
        if (m == null) return;
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        MarkEntryDialog dialog = new MarkEntryDialog(parentFrame, m.getStudentId(), String.valueOf(m.getEvaluationId()), String.valueOf(m.getMarksObtained()));
        dialog.setVisible(true);

        if (dialog.isSuccessful()) {
            loadAllMarks();
        }
    }

    private void deleteSelectedMark(Mark m) {
        if (m == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete mark for Student: " + m.getStudentId() + " (Eval ID: " + m.getEvaluationId() + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (markService.removeMark(m.getStudentId(), m.getEvaluationId())) {
                    loadAllMarks();
                    JOptionPane.showMessageDialog(this, "Mark deleted successfully.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting mark: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class ActionsCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JLabel editLink = new JLabel("Edit");
        private final JLabel deleteLink = new JLabel("Delete");

        public ActionsCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
            setOpaque(true);

            editLink.setFont(FONT_BODY_BOLD);
            editLink.setForeground(COLOR_BURGUNDY);
            editLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

            deleteLink.setFont(FONT_BODY_BOLD);
            deleteLink.setForeground(new Color(220, 38, 38));
            deleteLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

            add(editLink);
            add(deleteLink);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? new Color(254, 249, 235) : Color.WHITE);
            return this;
        }
    }
}
