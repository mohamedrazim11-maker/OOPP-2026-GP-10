package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LecturerController;
<<<<<<< HEAD
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Course;
=======
>>>>>>> Aysha
import com.faculty.management.model.CourseMaterial;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
<<<<<<< HEAD
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Course Materials Panel.
 * Implements Wireframe 03 Course Materials with Assigned Courses dropdown,
 * upload, modify, view file, and delete support.
 */
public class CourseMaterialsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbAssignedCourses;
    private JTextField txtCourseSearch;
    private JButton btnSearch, btnUpload, btnModify, btnViewFile, btnDelete;
    private JLabel subtextLabel;
    private final LecturerController controller = new LecturerController();
    private List<CourseMaterial> currentMaterialsList = new ArrayList<>();
    private List<Course> assignedCoursesList = new ArrayList<>();

    public CourseMaterialsPanel() {
        this(2); // default lecturerId
    }

    public CourseMaterialsPanel(int lecturerId) {
        setLayout(new BorderLayout(0, 14));
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(18, 24, 18, 24));
        initUI(lecturerId);
        loadAssignedCourses(lecturerId);
        loadMaterials();
    }

    private void initUI(int lecturerId) {
        // Top Toolbar per Wireframe 03: Assigned Course dropdown | Course Search | Upload Material
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setOpaque(false);

        JLabel lblAssigned = new JLabel("Assigned Course:");
        lblAssigned.setFont(UITheme.FONT_BODY_BOLD);
        lblAssigned.setForeground(UITheme.TEXT_BODY);

        cmbAssignedCourses = new JComboBox<>();
        cmbAssignedCourses.setFont(UITheme.FONT_BODY);
        cmbAssignedCourses.setPreferredSize(new Dimension(240, 34));
        cmbAssignedCourses.addItem("All Assigned Courses");
        cmbAssignedCourses.addActionListener(e -> onCourseSelected());

        JLabel lblSearch = new JLabel("Filter/Search:");
        lblSearch.setFont(UITheme.FONT_BODY_BOLD);
        lblSearch.setForeground(UITheme.TEXT_BODY);

        txtCourseSearch = new JTextField(10);
        txtCourseSearch.setFont(UITheme.FONT_BODY);
        txtCourseSearch.setPreferredSize(new Dimension(110, 34));
        txtCourseSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(4, 8, 4, 8)
        ));

        btnSearch = new JButton("Filter");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.setBackground(Color.WHITE);
        btnSearch.setForeground(UITheme.TEXT_BODY);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

        btnUpload = new JButton("+ Upload Material");
        btnUpload.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUpload.setBackground(UITheme.PRIMARY);
        btnUpload.setForeground(Color.WHITE);
        btnUpload.setFocusPainted(false);
        btnUpload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpload.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.PRIMARY, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

        topPanel.add(lblAssigned);
        topPanel.add(cmbAssignedCourses);
        topPanel.add(lblSearch);
        topPanel.add(txtCourseSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnUpload);

        // Table per Wireframe 03: ID | Course ID | Title | File Path | Uploaded Date
        tableModel = new DefaultTableModel(new String[]{"ID", "Course Code", "Title", "File Path", "Uploaded Date"}, 0) {
=======
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
>>>>>>> Aysha
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

<<<<<<< HEAD
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(320);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRender);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRender);

        JScrollPane scrollPane = UITheme.createModernScrollPane(table);

        // Bottom Action Panel: Modify Material, View File, Delete Material
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        actionButtons.setOpaque(false);

        btnModify = new JButton("Modify Selected");
        btnModify.setFont(UITheme.FONT_SMALL_BOLD);
        btnModify.setBackground(Color.WHITE);
        btnModify.setForeground(UITheme.TEXT_BODY);
        btnModify.setFocusPainted(false);
        btnModify.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModify.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

        btnViewFile = new JButton("Open / View Document");
        btnViewFile.setFont(UITheme.FONT_SMALL_BOLD);
        btnViewFile.setBackground(Color.WHITE);
        btnViewFile.setForeground(UITheme.TEXT_BODY);
        btnViewFile.setFocusPainted(false);
        btnViewFile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewFile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

        btnDelete = new JButton("Delete Selected");
        btnDelete.setFont(UITheme.FONT_SMALL_BOLD);
        btnDelete.setBackground(new Color(239, 68, 68));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(239, 68, 68), 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

        actionButtons.add(btnModify);
        actionButtons.add(btnViewFile);
        actionButtons.add(btnDelete);

        subtextLabel = new JLabel("Select an assigned course to view or update materials. Modify/Upload opens file dialog.");
        subtextLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subtextLabel.setForeground(new Color(100, 116, 139));
        subtextLabel.setBorder(new EmptyBorder(4, 4, 0, 0));

        bottomPanel.add(actionButtons, BorderLayout.NORTH);
        bottomPanel.add(subtextLabel, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> loadMaterials());
        btnUpload.addActionListener(e -> openUploadDialog());
        btnModify.addActionListener(e -> openModifyDialog());
        btnViewFile.addActionListener(e -> openSelectedFile());
        btnDelete.addActionListener(e -> deleteSelectedMaterial());

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadAssignedCourses(int lecturerId) {
        assignedCoursesList = controller.getAssignedCourses(lecturerId);
        cmbAssignedCourses.removeAllItems();
        cmbAssignedCourses.addItem("All Assigned Courses");
        for (Course c : assignedCoursesList) {
            cmbAssignedCourses.addItem(c.getCourseCode() + " - " + c.getCourseName());
        }
    }

    private void onCourseSelected() {
        int idx = cmbAssignedCourses.getSelectedIndex();
        if (idx > 0 && idx - 1 < assignedCoursesList.size()) {
            txtCourseSearch.setText(assignedCoursesList.get(idx - 1).getCourseCode());
        } else {
            txtCourseSearch.setText("");
        }
        loadMaterials();
    }

    private void loadMaterials() {
        tableModel.setRowCount(0);
        String filter = txtCourseSearch.getText().trim();
        currentMaterialsList = controller.getCourseMaterials(filter.isEmpty() ? "ALL" : filter);

        for (CourseMaterial cm : currentMaterialsList) {
            tableModel.addRow(new Object[]{
                    cm.getMaterialId(),
                    cm.getCourseId(),
                    cm.getTitle(),
                    cm.getFilePath(),
                    cm.getUploadedDate() != null ? cm.getUploadedDate().toString() : "-"
            });
        }
    }

    private void openUploadDialog() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        MaterialFormDialog dialog = new MaterialFormDialog(parent, controller);

        // Prepopulate course code if selected in combo
        int idx = cmbAssignedCourses.getSelectedIndex();
        if (idx > 0 && idx - 1 < assignedCoursesList.size()) {
            // Can pass selected course
        }

        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadMaterials();
        }
    }

    private void openModifyDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a material row to modify.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int materialId = (int) tableModel.getValueAt(row, 0);
        CourseMaterial target = null;
        for (CourseMaterial cm : currentMaterialsList) {
            if (cm.getMaterialId() == materialId) {
                target = cm;
                break;
            }
        }

        if (target == null) {
            target = new CourseMaterial(
                    materialId,
                    (String) tableModel.getValueAt(row, 1),
                    (String) tableModel.getValueAt(row, 2),
                    (String) tableModel.getValueAt(row, 3),
                    null
            );
        }

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        MaterialFormDialog dialog = new MaterialFormDialog(parent, controller, target);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadMaterials();
        }
    }

    private void openSelectedFile() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a material row to open.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String path = (String) tableModel.getValueAt(row, 3);
        if (path == null || path.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No file path recorded for this material.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File file = new File(path);
        if (file.exists() && Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "File path: " + file.getAbsolutePath() + "\n(Could not open default viewer: " + ex.getMessage() + ")", "Material Document", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "File located at:\n" + file.getAbsolutePath(), "Material Document Path", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteSelectedMaterial() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a material row to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int materialId = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete material: " + title + " (ID: " + materialId + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteCourseMaterial(materialId)) {
                JOptionPane.showMessageDialog(this, "Material deleted successfully.");
                loadMaterials();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete material.", "Error", JOptionPane.ERROR_MESSAGE);
            }
=======
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
>>>>>>> Aysha
        }
    }
}
