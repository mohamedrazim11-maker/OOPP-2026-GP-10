package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LecturerController;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Course;
import com.faculty.management.model.CourseMaterial;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

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
        }
    }
}
