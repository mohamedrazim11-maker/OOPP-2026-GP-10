package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Course Management Panel for Administrator (Member 1).
 * Strictly handles Course Management operations:
 * - View Courses
 * - Add Courses
 * - Update Courses
 * - Delete Courses
 * - Filter and Search Courses
 * 
 * Demonstrates:
 * - Classes & Objects: Panel, tables, models, dialogs, renderers, controller
 * - Inheritance: Extends JPanel
 * - Abstraction: Controller layer abstracts database operations from presentation
 * - Polymorphism: Overloaded methods and custom table cell renderers
 * - Encapsulation: Private members with public accessors
 * - Error and Exception Handling: Handles ValidationException and DatabaseException
 * - Database Handling: Connects to MySQL via AdminController/CourseService with offline fallback
 */
public class CourseManagementPanel extends JPanel {

    private final AdminController adminController;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private JComboBox<String> deptFilterComboBox;

    // Action Buttons (Strictly Course Management only)
    private JButton viewButton;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;

    public CourseManagementPanel() {
        this.adminController = new AdminController();
        initComponents();
        loadCourseDataFromBackend();
    }

    public CourseManagementPanel(AdminController adminController) {
        this.adminController = (adminController != null) ? adminController : new AdminController();
        initComponents();
        loadCourseDataFromBackend();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. Top Section: Header & Action Toolbar
        JPanel topSection = new JPanel(new BorderLayout(0, 14));
        topSection.setOpaque(false);

        // Header Title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel mainTitle = new JLabel("📚 Course Management");
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mainTitle.setForeground(new Color(15, 23, 42));

        JLabel subTitle = new JLabel("Add, update, delete, view courses, and assign course details for the Faculty of Technology.");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subTitle.setForeground(new Color(100, 116, 139));

        titlePanel.add(mainTitle);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subTitle);

        // Controls: Actions (Left) and Filter/Search (Right)
        JPanel controlsPanel = new JPanel(new BorderLayout(10, 10));
        controlsPanel.setOpaque(false);

        // Left Action Buttons
        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionButtons.setOpaque(false);

        viewButton = createActionButton("👁️ View Course", new Color(241, 245, 249), new Color(30, 41, 59));
        viewButton.addActionListener(e -> handleViewCourse());

        addButton = createActionButton("➕ Add Course", new Color(16, 185, 129), Color.WHITE);
        addButton.addActionListener(e -> handleAddCourse());

        updateButton = createActionButton("✏️ Update Course", new Color(241, 245, 249), new Color(30, 41, 59));
        updateButton.addActionListener(e -> handleUpdateCourse());

        deleteButton = createActionButton("🗑️ Delete Course", new Color(254, 242, 242), new Color(220, 38, 38));
        deleteButton.addActionListener(e -> handleDeleteCourse());

        refreshButton = createActionButton("🔄 Refresh", new Color(241, 245, 249), new Color(71, 85, 105));
        refreshButton.addActionListener(e -> {
            resetFilters();
            loadCourseDataFromBackend();
        });

        actionButtons.add(viewButton);
        actionButtons.add(addButton);
        actionButtons.add(updateButton);
        actionButtons.add(deleteButton);
        actionButtons.add(refreshButton);

        // Right Search & Filter Bar
        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchFilterPanel.setOpaque(false);

        JLabel filterLabel = new JLabel("Department:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterLabel.setForeground(new Color(71, 85, 105));

        String[] departments = {
                "All Departments",
                "Department of Information & Communication Technology",
                "Department of Biosystems Technology",
                "Department of Engineering Technology",
                "Multidisciplinary"
        };
        deptFilterComboBox = new JComboBox<>(departments);
        deptFilterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deptFilterComboBox.setPreferredSize(new Dimension(180, 32));
        deptFilterComboBox.setBackground(Color.WHITE);
        deptFilterComboBox.addActionListener(e -> applyFilter());

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setPreferredSize(new Dimension(160, 32));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(2, 8, 2, 8)
        ));
        searchField.addActionListener(e -> applyFilter());

        JButton searchBtn = createActionButton("Search", new Color(71, 85, 105), Color.WHITE);
        searchBtn.addActionListener(e -> applyFilter());

        searchFilterPanel.add(filterLabel);
        searchFilterPanel.add(deptFilterComboBox);
        searchFilterPanel.add(searchField);
        searchFilterPanel.add(searchBtn);

        controlsPanel.add(actionButtons, BorderLayout.WEST);
        controlsPanel.add(searchFilterPanel, BorderLayout.EAST);

        topSection.add(titlePanel, BorderLayout.NORTH);
        topSection.add(controlsPanel, BorderLayout.SOUTH);

        // 2. Center Section: Course Table
        String[] columnNames = {"Course ID", "Course Code", "Course Name", "Credits", "Theory (hrs)", "Practical (hrs)", "Department", "Semester"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Encapsulation: non-editable table cells
            }
        };

        courseTable = new JTable(tableModel);
        courseTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseTable.setRowHeight(36);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.setShowGrid(false);
        courseTable.setShowHorizontalLines(true);
        courseTable.setGridColor(new Color(241, 245, 249));
        courseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        courseTable.getTableHeader().setBackground(new Color(241, 245, 249));
        courseTable.getTableHeader().setForeground(new Color(51, 65, 85));
        courseTable.getTableHeader().setPreferredSize(new Dimension(0, 38));

        // Center align ID, Code, Credits, Theory, Practical, Semester
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        courseTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        courseTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        courseTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        courseTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        courseTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        courseTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        // Set column widths
        courseTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        courseTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        courseTable.getColumnModel().getColumn(2).setPreferredWidth(220);
        courseTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        courseTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        courseTable.getColumnModel().getColumn(5).setPreferredWidth(85);
        courseTable.getColumnModel().getColumn(6).setPreferredWidth(200);
        courseTable.getColumnModel().getColumn(7).setPreferredWidth(90);

        // Row Sorter for filtering and sorting
        rowSorter = new TableRowSorter<>(tableModel);
        courseTable.setRowSorter(rowSorter);

        // Double-click listener to view course details
        courseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleViewCourse();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createActionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 10, 32));
        return btn;
    }

    /**
     * Loads course data from backend controller/service into the table.
     */
    private void loadCourseDataFromBackend() {
        try {
            tableModel.setRowCount(0);
            List<Course> courses = adminController.loadAllCourses();
            for (Course c : courses) {
                tableModel.addRow(new Object[]{
                        c.getCourseId(),
                        c.getCourseCode(),
                        c.getCourseName(),
                        c.getCreditValue(),
                        c.getTheoryHours(),
                        c.getPracticalHours(),
                        c.getDepartment(),
                        c.getSemester()
                });
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Failed to load courses from database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 1. View Course Details (Read-Only)
     */
    private void handleViewCourse() {
        try {
            int modelRow = getSelectedModelRow();
            int courseId = (int) tableModel.getValueAt(modelRow, 0);

            Course course = adminController.handleViewCourse(courseId);

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            CourseFormDialog dialog = new CourseFormDialog(parentFrame, "Course Details - " + course.getCourseCode(), CourseFormDialog.FormMode.VIEW, course, adminController);
            dialog.setVisible(true);

        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Selection Required", JOptionPane.WARNING_MESSAGE);
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, "Failed to fetch course details: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 2. Add New Course
     */
    private void handleAddCourse() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        CourseFormDialog dialog = new CourseFormDialog(parentFrame, "Add New Course Module", CourseFormDialog.FormMode.ADD, adminController);
        dialog.setVisible(true);

        if (dialog.isSaved() && dialog.getCourseData() != null) {
            Course c = dialog.getCourseData();
            tableModel.addRow(new Object[]{
                    c.getCourseId(),
                    c.getCourseCode(),
                    c.getCourseName(),
                    c.getCreditValue(),
                    c.getTheoryHours(),
                    c.getPracticalHours(),
                    c.getDepartment(),
                    c.getSemester()
            });
            JOptionPane.showMessageDialog(this,
                    "Course '" + c.getCourseCode() + " - " + c.getCourseName() + "' added successfully!",
                    "Course Added",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 3. Update Existing Course
     */
    private void handleUpdateCourse() {
        try {
            int modelRow = getSelectedModelRow();
            int courseId = (int) tableModel.getValueAt(modelRow, 0);

            Course course = adminController.handleViewCourse(courseId);

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            CourseFormDialog dialog = new CourseFormDialog(parentFrame, "Update Course - " + course.getCourseCode(), CourseFormDialog.FormMode.UPDATE, course, adminController);
            dialog.setVisible(true);

            if (dialog.isSaved() && dialog.getCourseData() != null) {
                Course c = dialog.getCourseData();
                tableModel.setValueAt(c.getCourseCode(), modelRow, 1);
                tableModel.setValueAt(c.getCourseName(), modelRow, 2);
                tableModel.setValueAt(c.getCreditValue(), modelRow, 3);
                tableModel.setValueAt(c.getTheoryHours(), modelRow, 4);
                tableModel.setValueAt(c.getPracticalHours(), modelRow, 5);
                tableModel.setValueAt(c.getDepartment(), modelRow, 6);
                tableModel.setValueAt(c.getSemester(), modelRow, 7);

                JOptionPane.showMessageDialog(this, "Course '" + c.getCourseCode() + "' updated successfully!", "Course Updated", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Selection Required", JOptionPane.WARNING_MESSAGE);
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, "Failed to update course: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 4. Delete Course
     */
    private void handleDeleteCourse() {
        try {
            int modelRow = getSelectedModelRow();
            int courseId = (int) tableModel.getValueAt(modelRow, 0);
            String courseCode = (String) tableModel.getValueAt(modelRow, 1);
            String courseName = (String) tableModel.getValueAt(modelRow, 2);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete course module:\n'" + courseCode + " - " + courseName + "'?\nThis action cannot be undone.",
                    "Confirm Course Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                adminController.handleDeleteCourse(courseId);
                tableModel.removeRow(modelRow);
                JOptionPane.showMessageDialog(this, "Course '" + courseCode + "' was deleted successfully.", "Course Deleted", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Selection Required", JOptionPane.WARNING_MESSAGE);
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete course: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getSelectedModelRow() throws ValidationException {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            throw new ValidationException("Please select a course from the table first.");
        }
        return courseTable.convertRowIndexToModel(selectedRow);
    }

    private void applyFilter() {
        String dept = (String) deptFilterComboBox.getSelectedItem();
        String searchText = searchField.getText().trim();

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                if (dept != null && !"All Departments".equals(dept)) {
                    String rowDept = entry.getStringValue(6);
                    if (!dept.equalsIgnoreCase(rowDept)) {
                        return false;
                    }
                }
                if (!searchText.isEmpty()) {
                    boolean match = false;
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        if (entry.getStringValue(i).toLowerCase().contains(searchText.toLowerCase())) {
                            match = true;
                            break;
                        }
                    }
                    return match;
                }
                return true;
            }
        };
        rowSorter.setRowFilter(rf);
    }

    private void resetFilters() {
        deptFilterComboBox.setSelectedIndex(0);
        searchField.setText("");
        rowSorter.setRowFilter(null);
    }

    // Encapsulated Getters
    public JTable getCourseTable() {
        return courseTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public AdminController getAdminController() {
        return adminController;
    }
}
