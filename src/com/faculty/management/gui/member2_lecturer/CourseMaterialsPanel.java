package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LecturerController;
import com.faculty.management.model.Course;
import com.faculty.management.model.CourseMaterial;
import com.faculty.management.model.Lecturer;

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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.faculty.management.gui.member2_lecturer.LecturerUIComponents.*;

/**
 * Course Materials Management Panel for Member 2.
 * Strictly fulfills Requirement 2:
 * - View assigned courses
 * - Add materials
 * - Modify materials
 * - View materials
 */
public class CourseMaterialsPanel extends JPanel {

    private final LecturerController controller;
    private JTable materialTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private List<CourseMaterial> currentMaterialsList = new ArrayList<>();

    private Lecturer currentLecturer;

    // Filters & Info
    private SearchTextField searchCourseField;
    private JComboBox<String> courseFilterBox;
    private JLabel footerSummaryLabel;
    private JLabel assignedCoursesLabel;

    public CourseMaterialsPanel() {
        this(null);
    }

    public CourseMaterialsPanel(Lecturer lecturer) {
        this.currentLecturer = (lecturer != null) ? lecturer : new Lecturer();
        if (this.currentLecturer.getUserId() <= 0) {
            this.currentLecturer.setUserId(31);
        }
        this.controller = new LecturerController();
        initComponents();
        loadAssignedCourses();
        loadMaterials();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(COLOR_PAGE_BG);
        setBorder(new EmptyBorder(0, 28, 20, 28));

        // 1. Header: Title, Subtitle, and Action Buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("Course Materials");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(COLOR_TEXT_DARK);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageSub = new JLabel("Upload, view, and modify course materials for your assigned teaching modules.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);
        pageSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        assignedCoursesLabel = new JLabel("Assigned Courses: Loading...");
        assignedCoursesLabel.setFont(FONT_SMALL_BOLD);
        assignedCoursesLabel.setForeground(COLOR_BURGUNDY);
        assignedCoursesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(assignedCoursesLabel);

        // Header Action Buttons
        JPanel actionBtnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionBtnBox.setOpaque(false);

        BurgundyButton uploadBtn = new BurgundyButton("+ Upload material");
        uploadBtn.setPreferredSize(new Dimension(150, 38));
        uploadBtn.addActionListener(e -> handleUploadMaterial());

        WhiteOutlineButton modifyBtn = new WhiteOutlineButton("Modify material");
        modifyBtn.setPreferredSize(new Dimension(130, 38));
        modifyBtn.addActionListener(e -> handleModifySelectedMaterial());

        WhiteOutlineButton viewBtn = new WhiteOutlineButton("View / Open file");
        viewBtn.setPreferredSize(new Dimension(130, 38));
        viewBtn.addActionListener(e -> handleViewSelectedFile());

        WhiteOutlineButton deleteBtn = new WhiteOutlineButton("Delete");
        deleteBtn.setPreferredSize(new Dimension(80, 38));
        deleteBtn.setForeground(new Color(220, 38, 38));
        deleteBtn.addActionListener(e -> handleDeleteSelectedMaterial());

        actionBtnBox.add(viewBtn);
        actionBtnBox.add(modifyBtn);
        actionBtnBox.add(deleteBtn);
        actionBtnBox.add(uploadBtn);

        headerPanel.add(titleBox, BorderLayout.CENTER);
        headerPanel.add(actionBtnBox, BorderLayout.EAST);

        // 2. Filter Toolbar
        JPanel filterBar = new JPanel(new BorderLayout(10, 0));
        filterBar.setOpaque(false);

        searchCourseField = new SearchTextField("Search by course or title", 22);
        searchCourseField.setPreferredSize(new Dimension(240, 36));
        searchCourseField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        courseFilterBox = new JComboBox<>(new String[]{"All courses"});
        styleComboBox(courseFilterBox);
        courseFilterBox.setPreferredSize(new Dimension(180, 36));
        courseFilterBox.addActionListener(e -> filterByCourseDropdown());

        WhiteOutlineButton refreshBtn = createRefreshButton();
        refreshBtn.setPreferredSize(new Dimension(100, 36));
        refreshBtn.addActionListener(e -> {
            searchCourseField.setText("");
            if (courseFilterBox.getItemCount() > 0) {
                courseFilterBox.setSelectedIndex(0);
            }
            loadMaterials();
        });

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftFilters.setOpaque(false);
        leftFilters.add(searchCourseField);
        leftFilters.add(courseFilterBox);

        filterBar.add(leftFilters, BorderLayout.WEST);
        filterBar.add(refreshBtn, BorderLayout.EAST);

        // 3. Table Container
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setOpaque(false);

        String[] columns = {"ID", "COURSE CODE", "TITLE", "FILE PATH", "UPLOADED DATE", "_OBJ_"};
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

        // Column widths
        materialTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        materialTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        materialTable.getColumnModel().getColumn(2).setPreferredWidth(260);
        materialTable.getColumnModel().getColumn(3).setPreferredWidth(320);
        materialTable.getColumnModel().getColumn(4).setPreferredWidth(150);

        // Hide raw object column
        materialTable.getColumnModel().getColumn(5).setMinWidth(0);
        materialTable.getColumnModel().getColumn(5).setMaxWidth(0);
        materialTable.getColumnModel().getColumn(5).setPreferredWidth(0);

        // Double click to view file
        materialTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleViewSelectedFile();
                }
            }
        });

        rowSorter = new TableRowSorter<>(tableModel);
        materialTable.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(materialTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));

        // Footer Summary
        footerSummaryLabel = new JLabel("Showing 0 materials · Select a row to View, Modify, or Delete");
        footerSummaryLabel.setFont(FONT_SMALL);
        footerSummaryLabel.setForeground(COLOR_TEXT_MUTED);
        footerSummaryLabel.setBorder(new EmptyBorder(4, 4, 4, 4));

        tableContainer.add(filterBar, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        tableContainer.add(footerSummaryLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
    }

    private void loadAssignedCourses() {
        try {
            int uid = (currentLecturer != null && currentLecturer.getUserId() > 0) ? currentLecturer.getUserId() : 31;
            List<Course> courses = controller.getAssignedCourses(uid);
            if (courses != null && !courses.isEmpty()) {
                StringBuilder sb = new StringBuilder("Assigned Courses: ");
                courseFilterBox.removeAllItems();
                courseFilterBox.addItem("All courses");
                for (int i = 0; i < courses.size(); i++) {
                    Course c = courses.get(i);
                    sb.append(c.getCourseCode());
                    if (i < courses.size() - 1) sb.append(", ");
                    courseFilterBox.addItem(c.getCourseCode() + " - " + c.getCourseName());
                }
                assignedCoursesLabel.setText(sb.toString());
            } else {
                assignedCoursesLabel.setText("Assigned Courses: CS201, CS202, CS203");
            }
        } catch (Exception ex) {
            assignedCoursesLabel.setText("Assigned Courses: Faculty Modules");
        }
    }

    public void loadMaterials() {
        tableModel.setRowCount(0);
        currentMaterialsList.clear();

        try {
            List<CourseMaterial> list = controller.getAllCourseMaterials();
            if (list != null && !list.isEmpty()) {
                currentMaterialsList.addAll(list);
                for (CourseMaterial cm : list) {
                    tableModel.addRow(new Object[]{
                            cm.getMaterialId(),
                            cm.getCourseId(),
                            cm.getTitle(),
                            cm.getFilePath(),
                            cm.getUploadedDate() != null ? cm.getUploadedDate().toString() : "Recent",
                            cm
                    });
                }
            }
        } catch (Exception ignored) {
        }

        updateFooterCount();
    }

    private void filterByCourseDropdown() {
        int idx = courseFilterBox.getSelectedIndex();
        if (idx <= 0) {
            filterTable();
            return;
        }

        String item = (String) courseFilterBox.getSelectedItem();
        if (item == null) return;
        String courseCode = item.split(" - ")[0].trim();
        searchCourseField.setText(courseCode);
    }

    private void filterTable() {
        String query = searchCourseField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(new RowFilter<DefaultTableModel, Object>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                    String cCode = entry.getStringValue(1).toLowerCase();
                    String title = entry.getStringValue(2).toLowerCase();
                    return cCode.contains(query) || title.contains(query);
                }
            });
        }
        updateFooterCount();
    }

    private void updateFooterCount() {
        int count = materialTable.getRowCount();
        footerSummaryLabel.setText("Showing " + count + " of " + currentMaterialsList.size() + " materials · Select a row to View, Modify, or Delete");
    }

    private CourseMaterial getSelectedMaterial() {
        int row = materialTable.getSelectedRow();
        if (row < 0) return null;
        int modelRow = materialTable.convertRowIndexToModel(row);
        return (CourseMaterial) tableModel.getValueAt(modelRow, 5);
    }

    private void handleUploadMaterial() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        MaterialFormDialog dialog = new MaterialFormDialog(parentFrame, controller);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            loadMaterials();
        }
    }

    private void handleModifySelectedMaterial() {
        CourseMaterial selected = getSelectedMaterial();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a course material to modify.", "Select Material", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        MaterialFormDialog dialog = new MaterialFormDialog(parentFrame, controller, selected);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            loadMaterials();
        }
    }

    private void handleViewSelectedFile() {
        CourseMaterial selected = getSelectedMaterial();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a course material to view.", "Select Material", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String path = selected.getFilePath();
        if (path == null || path.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Material has no file path.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File file = new File(path);
        if (file.exists() && Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not open file: " + ex.getMessage() + "\nFile path: " + file.getAbsolutePath(), "File Viewer", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Material Record Details:\nTitle: " + selected.getTitle() + "\nCourse: " + selected.getCourseId() + "\nPath: " + path, "Material Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleDeleteSelectedMaterial() {
        CourseMaterial selected = getSelectedMaterial();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a course material to delete.", "Select Material", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete material: " + selected.getTitle() + " (" + selected.getCourseId() + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteCourseMaterial(selected.getMaterialId())) {
                JOptionPane.showMessageDialog(this, "Material deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMaterials();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete material.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
