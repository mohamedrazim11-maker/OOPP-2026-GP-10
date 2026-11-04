package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Batch;
import com.faculty.management.model.Undergraduate;

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
import java.util.ArrayList;
import java.util.List;

import static com.faculty.management.gui.member1_admin.AdminUIComponents.*;

/**
 * Batch Management Panel for Administrator (Member 1).
 * Allows Administrator to view, create, edit, delete, and inspect undergraduate batches.
 *
 * Demonstrates:
 * - Classes & Objects: Batch domain model, table models, and controller encapsulation.
 * - Inheritance: Extends JPanel.
 * - Abstraction: Controller and DAO layer abstractions.
 * - Polymorphism: Custom table cell renderers for status pills and student counts.
 * - Error & Exception Handling: Catches DatabaseException and ValidationException with UI feedback.
 */
public class BatchManagementPanel extends JPanel {

    private final AdminController adminController;
    private List<Batch> allBatches = new ArrayList<>();
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTable batchTable;

    // Filters & Search
    private SearchTextField searchField;
    private JComboBox<String> statusFilterBox;

    // KPI Stat values
    private JLabel totalBatchesVal;
    private JLabel activeBatchesVal;
    private JLabel completedBatchesVal;
    private JLabel totalStudentsVal;
    private JLabel summaryFooterLabel;

    // Action buttons
    private WhiteOutlineButton viewStudentsBtn;
    private WhiteOutlineButton editBatchBtn;
    private DangerOutlineButton deleteBatchBtn;

    public BatchManagementPanel() {
        this(new AdminController());
    }

    public BatchManagementPanel(AdminController adminController) {
        this.adminController = (adminController != null) ? adminController : new AdminController();
        initComponents();
        loadBatchData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(COLOR_PAGE_BG);
        setBorder(new EmptyBorder(0, 28, 20, 28));

        // 1. Header: Title, Subtitle, and Top Action Buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("Undergraduate Batches");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(COLOR_TEXT_DARK);

        JLabel pageSub = new JLabel("Create, review and manage undergraduate cohorts, intakes, and student allocations.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBtns.setOpaque(false);

        WhiteOutlineButton refreshBtn = new WhiteOutlineButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(100, 38));
        refreshBtn.addActionListener(e -> loadBatchData());

        BurgundyButton createBatchBtn = new BurgundyButton("+ Add Batch");
        createBatchBtn.setPreferredSize(new Dimension(145, 38));
        createBatchBtn.addActionListener(e -> handleCreateBatchAction());

        actionBtns.add(refreshBtn);
        actionBtns.add(createBatchBtn);

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(actionBtns, BorderLayout.EAST);

        // 2. Main Body Container (KPI Cards + Card with Filter Bar & Table)
        JPanel bodyContainer = new JPanel();
        bodyContainer.setLayout(new BoxLayout(bodyContainer, BoxLayout.Y_AXIS));
        bodyContainer.setOpaque(false);

        bodyContainer.add(createKpiCardsRow());
        bodyContainer.add(Box.createVerticalStrut(14));

        CardPanel tableCard = new CardPanel();
        tableCard.setLayout(new BorderLayout(0, 12));
        tableCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        tableCard.add(createFilterBar(), BorderLayout.NORTH);
        tableCard.add(createTableScrollPane(), BorderLayout.CENTER);
        tableCard.add(createTableFooter(), BorderLayout.SOUTH);

        bodyContainer.add(tableCard);

        add(headerPanel, BorderLayout.NORTH);
        add(bodyContainer, BorderLayout.CENTER);
    }

    private JPanel createKpiCardsRow() {
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);

        totalBatchesVal = new JLabel("0");
        activeBatchesVal = new JLabel("0");
        completedBatchesVal = new JLabel("0");
        totalStudentsVal = new JLabel("0");

        statsRow.add(createStatCard("Total Batches", totalBatchesVal, "All Registered Batches", new BookIcon(20, COLOR_BURGUNDY)));
        statsRow.add(createStatCard("Active Batches", activeBatchesVal, "Current Academic Cohorts", new GradCapIcon(20, new Color(16, 185, 129))));
        statsRow.add(createStatCard("Completed / Other", completedBatchesVal, "Graduated / Upcoming", new CalendarIcon(20, new Color(59, 130, 246))));
        statsRow.add(createStatCard("Total Undergraduates", totalStudentsVal, "Allocated to Batches", new UserGroupIcon(20, COLOR_BURGUNDY)));

        return statsRow;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, String subtitle, Icon icon) {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(12, 4));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_SMALL_BOLD);
        titleLbl.setForeground(COLOR_TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(COLOR_TEXT_DARK);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLbl.setForeground(COLOR_TEXT_MUTED);

        textPanel.add(titleLbl);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(subLbl);

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setVerticalAlignment(JLabel.TOP);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(iconLbl, BorderLayout.EAST);
        return card;
    }

    private JPanel createFilterBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);

        searchField = new SearchTextField("Search by batch name, year, or intake description...", 25);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightControls.setOpaque(false);

        statusFilterBox = new JComboBox<>(new String[]{"All Statuses", "ACTIVE", "UPCOMING", "COMPLETED"});
        statusFilterBox.setFont(FONT_BODY);
        statusFilterBox.setPreferredSize(new Dimension(135, 36));
        statusFilterBox.setBackground(Color.WHITE);
        statusFilterBox.addActionListener(e -> applyFilters());

        viewStudentsBtn = new WhiteOutlineButton("View Students");
        viewStudentsBtn.setPreferredSize(new Dimension(125, 36));
        viewStudentsBtn.setEnabled(false);
        viewStudentsBtn.addActionListener(e -> handleViewStudentsAction());

        editBatchBtn = new WhiteOutlineButton("Edit Batch");
        editBatchBtn.setPreferredSize(new Dimension(105, 36));
        editBatchBtn.setEnabled(false);
        editBatchBtn.addActionListener(e -> handleEditBatchAction());

        deleteBatchBtn = new DangerOutlineButton("Delete");
        deleteBatchBtn.setPreferredSize(new Dimension(85, 36));
        deleteBatchBtn.setEnabled(false);
        deleteBatchBtn.addActionListener(e -> handleDeleteBatchAction());

        rightControls.add(new JLabel("Status:"));
        rightControls.add(statusFilterBox);
        rightControls.add(Box.createHorizontalStrut(6));
        rightControls.add(viewStudentsBtn);
        rightControls.add(editBatchBtn);
        rightControls.add(deleteBatchBtn);

        bar.add(searchField, BorderLayout.CENTER);
        bar.add(rightControls, BorderLayout.EAST);
        return bar;
    }

    private JScrollPane createTableScrollPane() {
        String[] columnNames = {"ID", "BATCH NAME", "ACADEMIC YEAR", "DESCRIPTION", "UNDERGRADUATES", "STATUS", "CREATED AT"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        batchTable = new JTable(tableModel);
        batchTable.setFont(FONT_BODY);
        batchTable.setRowHeight(44);
        batchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        batchTable.setGridColor(new Color(241, 245, 249));
        batchTable.setShowVerticalLines(false);
        batchTable.setSelectionBackground(new Color(243, 230, 233));
        batchTable.setSelectionForeground(COLOR_TEXT_DARK);

        JTableHeader th = batchTable.getTableHeader();
        th.setFont(FONT_SMALL_BOLD);
        th.setForeground(COLOR_TEXT_MUTED);
        th.setBackground(Color.WHITE);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 38));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Column widths
        batchTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        batchTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        batchTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        batchTable.getColumnModel().getColumn(3).setPreferredWidth(260);
        batchTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        batchTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        batchTable.getColumnModel().getColumn(6).setPreferredWidth(130);

        // Custom Renderers
        batchTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setForeground(isSelected ? table.getSelectionForeground() : COLOR_BURGUNDY);
                return l;
            }
        });

        // Undergraduates count pill renderer
        batchTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(JLabel.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                int count = 0;
                try {
                    count = Integer.parseInt(value.toString().replaceAll("[^0-9]", ""));
                } catch (Exception ignored) {}
                l.setText(count + (count == 1 ? " Student" : " Students"));
                l.setForeground(new Color(15, 23, 42));
                return l;
            }
        });

        // Status pill renderer
        batchTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(JLabel.CENTER);
                String st = (value != null) ? value.toString().toUpperCase() : "ACTIVE";
                l.setText("● " + st);
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                if ("ACTIVE".equals(st)) {
                    l.setForeground(new Color(16, 185, 129));
                } else if ("UPCOMING".equals(st)) {
                    l.setForeground(new Color(59, 130, 246));
                } else {
                    l.setForeground(new Color(100, 116, 139));
                }
                return l;
            }
        });

        // Selection listener to toggle buttons
        batchTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSel = batchTable.getSelectedRow() != -1;
            viewStudentsBtn.setEnabled(hasSel);
            editBatchBtn.setEnabled(hasSel);
            deleteBatchBtn.setEnabled(hasSel);
        });

        // Double click to edit
        batchTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && batchTable.getSelectedRow() != -1) {
                    handleEditBatchAction();
                }
            }
        });

        rowSorter = new TableRowSorter<>(tableModel);
        batchTable.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(batchTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    private JPanel createTableFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 4, 4, 4));

        summaryFooterLabel = new JLabel("Showing 0 batches");
        summaryFooterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        summaryFooterLabel.setForeground(COLOR_TEXT_MUTED);

        footer.add(summaryFooterLabel, BorderLayout.WEST);
        return footer;
    }

    /**
     * Loads batches directly from the database and updates KPIs and table.
     */
    public void loadBatchData() {
        try {
            allBatches = adminController.loadAllBatches();
            populateBatchTable(allBatches);
            updateKpiStats();
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load batch records: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateBatchTable(List<Batch> batches) {
        tableModel.setRowCount(0);
        for (Batch b : batches) {
            tableModel.addRow(new Object[]{
                    b.getBatchId(),
                    b.getBatchName(),
                    b.getAcademicYear() != null ? b.getAcademicYear() : b.getBatchName(),
                    b.getDescription() != null ? b.getDescription() : "—",
                    b.getStudentCount(),
                    b.getStatus() != null ? b.getStatus() : "ACTIVE",
                    b.getCreatedAt() != null ? b.getCreatedAt().substring(0, Math.min(10, b.getCreatedAt().length())) : "—"
            });
        }
        applyFilters();
    }

    private void updateKpiStats() {
        int total = allBatches.size();
        int active = 0;
        int completed = 0;
        int totalStudents = 0;

        for (Batch b : allBatches) {
            if ("ACTIVE".equalsIgnoreCase(b.getStatus())) {
                active++;
            } else {
                completed++;
            }
            totalStudents += b.getStudentCount();
        }

        totalBatchesVal.setText(String.valueOf(total));
        activeBatchesVal.setText(String.valueOf(active));
        completedBatchesVal.setText(String.valueOf(completed));
        totalStudentsVal.setText(String.valueOf(totalStudents));

        summaryFooterLabel.setText("Showing " + batchTable.getRowCount() + " of " + total + " batches  (" + totalStudents + " total undergraduates enrolled)");
    }

    private void applyFilters() {
        if (rowSorter == null) return;

        String query = searchField.getText().trim().toLowerCase();
        String statusFilter = (String) statusFilterBox.getSelectedItem();

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                // Status Filter (Column 5)
                if (statusFilter != null && !statusFilter.equals("All Statuses")) {
                    String st = entry.getStringValue(5);
                    if (!statusFilter.equalsIgnoreCase(st)) {
                        return false;
                    }
                }

                // Text query (Columns 1: Batch Name, 2: Academic Year, 3: Description)
                if (!query.isEmpty() && !query.equals("search by batch name, year, or intake description...")) {
                    String name = entry.getStringValue(1).toLowerCase();
                    String year = entry.getStringValue(2).toLowerCase();
                    String desc = entry.getStringValue(3).toLowerCase();
                    return name.contains(query) || year.contains(query) || desc.contains(query);
                }
                return true;
            }
        };

        rowSorter.setRowFilter(rf);
        int count = batchTable.getRowCount();
        summaryFooterLabel.setText("Showing " + count + " of " + allBatches.size() + " batches");
    }

    private Batch getSelectedBatch() {
        int selectedRow = batchTable.getSelectedRow();
        if (selectedRow == -1) return null;

        int modelRow = batchTable.convertRowIndexToModel(selectedRow);
        int batchId = (int) tableModel.getValueAt(modelRow, 0);

        for (Batch b : allBatches) {
            if (b.getBatchId() == batchId) {
                return b;
            }
        }
        return null;
    }

    private void handleCreateBatchAction() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        BatchFormDialog dialog = new BatchFormDialog(parent, BatchFormDialog.Mode.CREATE, null, adminController);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadBatchData();
            JOptionPane.showMessageDialog(this,
                    "Undergraduate batch created successfully!",
                    "Batch Created", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleEditBatchAction() {
        Batch selected = getSelectedBatch();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a batch from the table to edit.", "No Batch Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Window parent = SwingUtilities.getWindowAncestor(this);
        BatchFormDialog dialog = new BatchFormDialog(parent, BatchFormDialog.Mode.UPDATE, selected, adminController);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadBatchData();
            JOptionPane.showMessageDialog(this,
                    "Batch '" + selected.getBatchName() + "' updated successfully!",
                    "Batch Updated", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleDeleteBatchAction() {
        Batch selected = getSelectedBatch();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a batch to delete.", "No Batch Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selected.getStudentCount() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete batch '" + selected.getBatchName() + "' because " + selected.getStudentCount() + " undergraduate students are assigned to it.\nPlease reassign these students first or set the batch status to COMPLETED.",
                    "Active Students In Batch", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete batch '" + selected.getBatchName() + "'?\nThis action cannot be undone.",
                "Confirm Delete Batch", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                adminController.handleDeleteBatch(selected.getBatchId());
                loadBatchData();
                JOptionPane.showMessageDialog(this, "Batch deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            } catch (ValidationException | DatabaseException ex) {
                JOptionPane.showMessageDialog(this, "Failed to delete batch: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleViewStudentsAction() {
        Batch selected = getSelectedBatch();
        if (selected == null) return;

        try {
            List<Undergraduate> students = adminController.getStudentsInBatch(selected.getBatchName());
            showBatchStudentsDialog(selected, students);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Failed to load students for batch: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBatchStudentsDialog(Batch batch, List<Undergraduate> students) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(parent, "Undergraduates in Batch " + batch.getBatchName(), Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(750, 480);
        dlg.setLocationRelativeTo(parent);
        dlg.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BURGUNDY);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Batch: " + batch.getBatchName() + " — Enrolled Undergraduates");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel(students.size() + " students allocated to this intake (" + (batch.getDescription() != null ? batch.getDescription() : "") + ")");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(230, 204, 210));

        JPanel headerBox = new JPanel();
        headerBox.setLayout(new BoxLayout(headerBox, BoxLayout.Y_AXIS));
        headerBox.setOpaque(false);
        headerBox.add(title);
        headerBox.add(Box.createVerticalStrut(3));
        headerBox.add(sub);
        header.add(headerBox, BorderLayout.CENTER);

        String[] cols = {"USER ID", "STUDENT ID / USERNAME", "FULL NAME", "DEPARTMENT", "EMAIL", "CONTACT NO", "STATUS"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        for (Undergraduate u : students) {
            tm.addRow(new Object[]{
                    u.getUserId(),
                    u.getUsername(),
                    u.getFullName(),
                    u.getDepartment() != null ? u.getDepartment() : "General",
                    u.getEmail(),
                    u.getContactNo() != null ? u.getContactNo() : "—",
                    u.getStatus() != null ? u.getStatus() : "ACTIVE"
            });
        }

        JTable table = new JTable(tm);
        table.setRowHeight(36);
        table.setFont(FONT_BODY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(FONT_SMALL_BOLD);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new EmptyBorder(12, 16, 12, 16));
        sp.getViewport().setBackground(Color.WHITE);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        bottom.setBackground(new Color(248, 250, 252));
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER));

        WhiteOutlineButton closeBtn = new WhiteOutlineButton("Close");
        closeBtn.setPreferredSize(new Dimension(90, 32));
        closeBtn.addActionListener(e -> dlg.dispose());
        bottom.add(closeBtn);

        dlg.add(header, BorderLayout.NORTH);
        dlg.add(sp, BorderLayout.CENTER);
        dlg.add(bottom, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
