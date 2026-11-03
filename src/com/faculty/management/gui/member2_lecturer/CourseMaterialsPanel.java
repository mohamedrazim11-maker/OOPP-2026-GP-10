package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LecturerController;
import com.faculty.management.model.CourseMaterial;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

import static com.faculty.management.gui.member2_lecturer.LecturerUIComponents.*;

/**
 * Course Materials Management Panel for Member 2.
 * Strictly implements Page 3 of the Ruhuna Lecturer Portal Specification.
 */
public class CourseMaterialsPanel extends JPanel {

    private final LecturerController controller;
    private JTable materialTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // Filters
    private SearchTextField searchCourseField;
    private JComboBox<String> semesterFilterBox;
    private JLabel footerSummaryLabel;
    private JLabel emptyStateLabel;

    public CourseMaterialsPanel() {
        this.controller = new LecturerController();
        initComponents();
        loadMaterials();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(COLOR_PAGE_BG);
        setBorder(new EmptyBorder(0, 28, 20, 28));

        // 1. Header: Title, Subtitle, and "+ Upload material" button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("Course Materials");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(COLOR_TEXT_DARK);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pageTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel pageSub = new JLabel("Upload and review materials for your assigned courses.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);
        pageSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        pageSub.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        BurgundyButton uploadBtn = new BurgundyButton("+ Upload material");
        uploadBtn.setPreferredSize(new Dimension(150, 38));
        uploadBtn.addActionListener(e -> handleUploadMaterial());

        headerPanel.add(titleBox, BorderLayout.CENTER);
        headerPanel.add(uploadBtn, BorderLayout.EAST);

        // 2. Filter Toolbar
        JPanel filterBar = new JPanel(new BorderLayout(10, 0));
        filterBar.setOpaque(false);

        searchCourseField = new SearchTextField("Search by course ID", 24);
        searchCourseField.setPreferredSize(new Dimension(240, 36));
        searchCourseField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadMaterials(); }
            public void removeUpdate(DocumentEvent e) { loadMaterials(); }
            public void changedUpdate(DocumentEvent e) { loadMaterials(); }
        });

        String[] semesters = {"All semesters", "Semester 1", "Semester 2"};
        semesterFilterBox = new JComboBox<>(semesters);
        styleComboBox(semesterFilterBox);
        semesterFilterBox.setPreferredSize(new Dimension(140, 36));
        semesterFilterBox.addActionListener(e -> loadMaterials());

        WhiteOutlineButton refreshBtn = createRefreshButton();
        refreshBtn.setPreferredSize(new Dimension(100, 36));
        refreshBtn.addActionListener(e -> {
            searchCourseField.setText("");
            semesterFilterBox.setSelectedIndex(0);
            loadMaterials();
        });

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftFilters.setOpaque(false);
        leftFilters.add(searchCourseField);
        leftFilters.add(semesterFilterBox);

        filterBar.add(leftFilters, BorderLayout.WEST);
        filterBar.add(refreshBtn, BorderLayout.EAST);

        // 3. Table Container
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setOpaque(false);

        String[] columns = {"ID", "COURSE ID", "TITLE", "FILE PATH", "UPLOADED"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        materialTable = new JTable(tableModel);
        materialTable.setRowHeight(42);
        materialTable.setShowVerticalLines(false);
        materialTable.setGridColor(new Color(243, 244, 246));
        materialTable.setSelectionBackground(new Color(254, 249, 235));
        materialTable.setSelectionForeground(COLOR_TEXT_DARK);
        materialTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        materialTable.setFont(FONT_BODY);

        // Header Styling
        JTableHeader th = materialTable.getTableHeader();
        th.setFont(FONT_SMALL_BOLD);
        th.setForeground(COLOR_TEXT_MUTED);
        th.setBackground(Color.WHITE);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Column widths & renderers
        materialTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        materialTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        materialTable.getColumnModel().getColumn(2).setPreferredWidth(260);
        materialTable.getColumnModel().getColumn(3).setPreferredWidth(320);
        materialTable.getColumnModel().getColumn(4).setPreferredWidth(140);

        rowSorter = new TableRowSorter<>(tableModel);
        materialTable.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(materialTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));

        // Center overlay empty state label
        emptyStateLabel = new JLabel("No materials yet. Search a course or upload a file.", SwingConstants.CENTER);
        emptyStateLabel.setFont(FONT_BODY);
        emptyStateLabel.setForeground(COLOR_TEXT_MUTED);
        emptyStateLabel.setVisible(false);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(scrollPane, BorderLayout.CENTER);

        // Footer Summary
        footerSummaryLabel = new JLabel("Showing 0 of 0 materials · Upload opens a file chooser and saves the path and date");
        footerSummaryLabel.setFont(FONT_SMALL);
        footerSummaryLabel.setForeground(COLOR_TEXT_MUTED);
        footerSummaryLabel.setBorder(new EmptyBorder(4, 4, 4, 4));

        tableContainer.add(filterBar, BorderLayout.NORTH);
        tableContainer.add(centerWrapper, BorderLayout.CENTER);
        tableContainer.add(footerSummaryLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
    }

    public void loadMaterials() {
        tableModel.setRowCount(0);
        String courseSearch = searchCourseField.getText().trim();

        List<CourseMaterial> list = null;
        try {
            if (!courseSearch.isEmpty()) {
                list = controller.getCourseMaterials(courseSearch);
            } else {
                // Try default course modules for this lecturer
                list = controller.getCourseMaterials("1");
                if (list == null || list.isEmpty()) {
                    list = controller.getCourseMaterials("ICT2112");
                }
            }
        } catch (Exception ignored) {
        }

        if (list != null && !list.isEmpty()) {
            for (CourseMaterial cm : list) {
                tableModel.addRow(new Object[]{
                        cm.getMaterialId(),
                        cm.getCourseId(),
                        cm.getTitle(),
                        cm.getFilePath(),
                        cm.getUploadedDate()
                });
            }
        }

        int count = tableModel.getRowCount();
        footerSummaryLabel.setText("Showing " + count + " of " + count + " materials · Upload opens a file chooser and saves the path and date");
    }

    private void handleUploadMaterial() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        MaterialFormDialog dialog = new MaterialFormDialog(parentFrame, controller);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            loadMaterials();
        }
    }
}
