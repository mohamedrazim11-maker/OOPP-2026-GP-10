package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.gui.common.CustomButton;
import com.faculty.management.gui.common.DangerButton;
import com.faculty.management.gui.common.PrimaryButton;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Course Management Panel for Administrator (Member 1).
 * Represents the Course Management section in the Admin Dashboard.
 * 
 * Provides strictly the "Add Course", "Update Course", "Delete Course", "View Courses", and "Assign Course Information" options UI for managing academic courses.
 * Strictly avoids adding any other options inside course management as requested.
 * 
 * Demonstrates Core OOP Principles:
 * - Classes & Objects: Instantiation of UI cards, controller, buttons, and models.
 * - Inheritance: Extends JPanel to inherit Swing container behavior.
 * - Abstraction: Controller & service layers abstract backend database logic from UI.
 * - Polymorphism: Swing event listener dispatching and controller delegation.
 * - Encapsulation: Private component fields with controlled access via getters.
 * - Error & Exception Handling: Catches and manages errors through CourseFormDialog and message dialogs.
 * - Database Handling: Connects to MySQL through AdminController, CourseService, and CourseDAO.
 */
public class CourseManagementPanel extends JPanel {

    // Encapsulated Controller & Components
    private final AdminController adminController;
    private PrimaryButton addCourseButton;
    private CustomButton updateCourseButton;
    private CustomButton deleteCourseButton;
    private CustomButton viewCourseButton;
    private CustomButton assignCourseInfoButton;
    private JLabel lastActionCourseLabel;

    private JTable courseTable;
    private DefaultTableModel tableModel;
    private List<Course> currentCoursesList = new ArrayList<>();

    public CourseManagementPanel() {
        this.adminController = new AdminController();
        initComponents();
        loadCourseTable();
    }

    public CourseManagementPanel(AdminController adminController) {
        this.adminController = (adminController != null) ? adminController : new AdminController();
        initComponents();
        loadCourseTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. Header Title & Description (Wireframe 04 Course Management)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel mainTitleLabel = new JLabel("Course Management");
        mainTitleLabel.setFont(UITheme.FONT_PAGE_TITLE);
        mainTitleLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel subTitleLabel = new JLabel("Register and maintain academic course modules");
        subTitleLabel.setFont(UITheme.FONT_BODY);
        subTitleLabel.setForeground(UITheme.TEXT_MUTED);

        headerPanel.add(mainTitleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subTitleLabel);

        // 2. Action Toolbar & Table Container
        JPanel centerContainer = new JPanel(new BorderLayout(0, 12));
        centerContainer.setOpaque(false);

        // Action Toolbar matching Wireframe 04 Course Management
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionToolbar.setOpaque(false);

        addCourseButton = new PrimaryButton("+ Add Course");
        addCourseButton.setPreferredSize(new Dimension(135, 36));
        addCourseButton.setToolTipText("Open form to register a new academic course");
        addCourseButton.addActionListener(e -> handleAddCourseAction());

        updateCourseButton = new CustomButton("Update Course", Color.WHITE, UITheme.TEXT_BODY, UITheme.BORDER_INPUT);
        updateCourseButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        updateCourseButton.setPreferredSize(new Dimension(145, 36));
        updateCourseButton.setToolTipText("Modify selected academic course module");
        updateCourseButton.addActionListener(e -> handleUpdateCourseAction());

        deleteCourseButton = new DangerButton("Delete Course");
        deleteCourseButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteCourseButton.setPreferredSize(new Dimension(135, 36));
        deleteCourseButton.setToolTipText("Remove selected academic course module");
        deleteCourseButton.addActionListener(e -> handleDeleteCourseAction());

        viewCourseButton = new CustomButton("View Courses", Color.WHITE, UITheme.TEXT_BODY, UITheme.BORDER_INPUT);
        viewCourseButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        viewCourseButton.setPreferredSize(new Dimension(135, 36));
        viewCourseButton.setToolTipText("Inspect comprehensive course details from database");
        viewCourseButton.addActionListener(e -> handleViewCoursesAction());

        assignCourseInfoButton = new CustomButton("Assign Course Info", Color.WHITE, UITheme.TEXT_BODY, UITheme.BORDER_INPUT);
        assignCourseInfoButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        assignCourseInfoButton.setPreferredSize(new Dimension(160, 36));
        assignCourseInfoButton.setToolTipText("Assign department, semester, credits, and contact hours");
        assignCourseInfoButton.addActionListener(e -> handleAssignCourseInfoAction());

        actionToolbar.add(addCourseButton);
        actionToolbar.add(updateCourseButton);
        actionToolbar.add(deleteCourseButton);
        actionToolbar.add(viewCourseButton);
        actionToolbar.add(assignCourseInfoButton);

        // Table Component (Columns per Wireframe 04: Course Code | Name | Credits | Theory Hrs | Practical Hrs | Dept | Sem)
        String[] columns = {"Course Code", "Name", "Credits", "Theory Hrs", "Practical Hrs", "Dept", "Sem"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        courseTable = new JTable(tableModel);
        UITheme.styleTable(courseTable);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Column widths
        courseTable.getColumnModel().getColumn(0).setPreferredWidth(110);
        courseTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        courseTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        courseTable.getColumnModel().getColumn(3).setPreferredWidth(95);
        courseTable.getColumnModel().getColumn(4).setPreferredWidth(105);
        courseTable.getColumnModel().getColumn(5).setPreferredWidth(160);
        courseTable.getColumnModel().getColumn(6).setPreferredWidth(80);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        courseTable.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        courseTable.getColumnModel().getColumn(2).setCellRenderer(centerRender);
        courseTable.getColumnModel().getColumn(3).setCellRenderer(centerRender);
        courseTable.getColumnModel().getColumn(4).setCellRenderer(centerRender);
        courseTable.getColumnModel().getColumn(6).setCellRenderer(centerRender);

        JScrollPane scrollPane = UITheme.createModernScrollPane(courseTable);

        // 3. Results area / operation help text banner
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));

        lastActionCourseLabel = new JLabel("Results area / operation help text");
        lastActionCourseLabel.setFont(UITheme.FONT_SMALL);
        lastActionCourseLabel.setForeground(UITheme.TEXT_MUTED);
        statusPanel.add(lastActionCourseLabel);

        centerContainer.add(actionToolbar, BorderLayout.NORTH);
        centerContainer.add(scrollPane, BorderLayout.CENTER);
        centerContainer.add(statusPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(centerContainer, BorderLayout.CENTER);
    }

    /**
     * Loads courses from database into table.
     */
    public void loadCourseTable() {
        try {
            currentCoursesList = adminController.loadAllCourses();
            tableModel.setRowCount(0);
            if (currentCoursesList != null) {
                for (Course c : currentCoursesList) {
                    tableModel.addRow(new Object[]{
                            c.getCourseCode(),
                            c.getCourseName(),
                            c.getCreditValue(),
                            c.getTheoryHours(),
                            c.getPracticalHours(),
                            c.getDepartment() != null ? c.getDepartment() : "General",
                            c.getSemester() != null ? c.getSemester() : "Semester 1"
                    });
                }
                lastActionCourseLabel.setText("Loaded " + currentCoursesList.size() + " academic course module(s) from database.");
                lastActionCourseLabel.setForeground(new Color(51, 65, 85));
            }
        } catch (DatabaseException ex) {
            lastActionCourseLabel.setText("Error loading courses: " + ex.getMessage());
            lastActionCourseLabel.setForeground(new Color(220, 38, 38));
        }
    }

    /**
     * Handles opening the Add Course Dialog.
     * Demonstrates Abstraction, Polymorphism, and Error Handling.
     */
    private void handleAddCourseAction() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        CourseFormDialog dialog = new CourseFormDialog(parentFrame, adminController, CourseFormDialog.FormMode.ADD);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadCourseTable();
            Course course = dialog.getCreatedCourse();
            if (course != null) {
                lastActionCourseLabel.setText("✓ Successfully added: " + course.getCourseCode() + " — " +
                        course.getCourseName() + " (" + course.getCreditValue() + " Credits)");
                lastActionCourseLabel.setForeground(new Color(22, 101, 52)); // Success green
            }
        }
    }

    /**
     * Programmatic backend method to add a new course via the panel's controller.
     * Demonstrates Abstraction, Encapsulation, and Error/Exception Handling.
     *
     * @param course Course entity to register
     * @return Registered Course entity with generated ID
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database insertion fails
     */
    public Course handleAddCourse(Course course) throws ValidationException, DatabaseException {
        return adminController.handleAddCourse(course);
    }

    /**
     * Programmatic backend method to update an existing course via the panel's controller.
     * Demonstrates Abstraction, Encapsulation, and Error/Exception Handling.
     */
    public boolean handleUpdateCourse(Course course) throws ValidationException, DatabaseException {
        return adminController.handleUpdateCourse(course);
    }

    /**
     * Programmatic backend method to delete a course by ID via the panel's controller.
     * Demonstrates Abstraction, Encapsulation, and Database Handling.
     */
    public boolean handleDeleteCourse(int courseId) throws ValidationException, DatabaseException {
        return adminController.handleDeleteCourse(courseId);
    }

    /**
     * Polymorphic overload to delete a course using a Course entity.
     */
    public boolean handleDeleteCourse(Course course) throws ValidationException, DatabaseException {
        return adminController.handleDeleteCourse(course);
    }

    /**
     * Programmatic backend method to load all courses from the database.
     */
    public List<Course> handleLoadAllCourses() throws DatabaseException {
        return adminController.loadAllCourses();
    }

    /**
     * Programmatic backend method to view a specific course by ID.
     */
    public Course handleViewCourse(int courseId) throws ValidationException, DatabaseException {
        return adminController.handleViewCourse(courseId);
    }

    /**
     * Programmatic backend method to view a specific course by Code.
     */
    public Course handleViewCourseByCode(String courseCode) throws ValidationException, DatabaseException {
        return adminController.handleViewCourseByCode(courseCode);
    }

    /**
     * Programmatic backend method to search courses by query.
     */
    public List<Course> handleSearchCourses(String query) throws DatabaseException {
        return adminController.handleSearchCourses(query);
    }

    /**
     * Handles opening the Update Course Dialog.
     * Demonstrates Abstraction, Polymorphism, Database Handling, and Error & Exception Handling.
     */
    private void handleUpdateCourseAction() {
        try {
            List<Course> courses = adminController.loadAllCourses();
            if (courses == null || courses.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No courses found in database to update. Please add a course first.",
                        "No Courses Available",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            CourseFormDialog dialog = new CourseFormDialog(parentFrame, adminController, CourseFormDialog.FormMode.UPDATE);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                loadCourseTable();
                Course course = dialog.getCreatedCourse();
                if (course != null) {
                    lastActionCourseLabel.setText("✓ Successfully updated: " + course.getCourseCode() + " — " +
                            course.getCourseName() + " (" + course.getCreditValue() + " Credits)");
                    lastActionCourseLabel.setForeground(new Color(22, 101, 52)); // Success green
                }
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to retrieve courses from database: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles opening the Delete Course Dialog.
     * Demonstrates Abstraction, Polymorphism, Database Handling, and Error & Exception Handling.
     */
    private void handleDeleteCourseAction() {
        try {
            List<Course> courses = adminController.loadAllCourses();
            if (courses == null || courses.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No courses found in database to delete. Please add a course first.",
                        "No Courses Available",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            CourseFormDialog dialog = new CourseFormDialog(parentFrame, adminController, CourseFormDialog.FormMode.DELETE);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                loadCourseTable();
                Course course = dialog.getCreatedCourse();
                if (course != null) {
                    lastActionCourseLabel.setText("✓ Successfully deleted: " + course.getCourseCode() + " — " +
                            course.getCourseName());
                    lastActionCourseLabel.setForeground(new Color(220, 38, 38)); // Delete red
                }
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to retrieve courses from database: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles opening the View Courses Dialog.
     * Demonstrates Abstraction, Polymorphism, Database Handling, and Error & Exception Handling.
     */
    private void handleViewCoursesAction() {
        loadCourseTable();
        try {
            List<Course> courses = adminController.loadAllCourses();
            if (courses == null || courses.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No courses found in database to view. Please add a course first.",
                        "No Courses Available",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            CourseFormDialog dialog = new CourseFormDialog(parentFrame, adminController, CourseFormDialog.FormMode.VIEW);
            dialog.setVisible(true);

            Course selected = dialog.getSelectedCourse();
            if (selected != null) {
                lastActionCourseLabel.setText("Currently viewing: " + selected.getCourseCode() + " — " +
                        selected.getCourseName() + " (" + selected.getCreditValue() + " Credits)");
                lastActionCourseLabel.setForeground(new Color(37, 99, 235)); // Primary blue
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to retrieve courses from database: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles opening the Assign Course Information Dialog.
     * Demonstrates Abstraction, Polymorphism, Database Handling, and Error & Exception Handling.
     */
    private void handleAssignCourseInfoAction() {
        try {
            List<Course> courses = adminController.loadAllCourses();
            if (courses == null || courses.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No courses found in database to assign information. Please add a course first.",
                        "No Courses Available",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            CourseFormDialog dialog = new CourseFormDialog(parentFrame, adminController, CourseFormDialog.FormMode.ASSIGN_INFO);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                loadCourseTable();
                Course course = dialog.getCreatedCourse();
                if (course != null) {
                    lastActionCourseLabel.setText("✓ Successfully assigned information for: " + course.getCourseCode() + " — " +
                            course.getCourseName() + " (" + course.getDepartment() + " | " + course.getSemester() + ")");
                    lastActionCourseLabel.setForeground(new Color(109, 40, 217)); // Purple
                }
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to retrieve courses from database: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Encapsulated Getters
    public AdminController getAdminController() {
        return adminController;
    }

    public PrimaryButton getAddCourseButton() {
        return addCourseButton;
    }

    public CustomButton getUpdateCourseButton() {
        return updateCourseButton;
    }

    public CustomButton getDeleteCourseButton() {
        return deleteCourseButton;
    }

    public CustomButton getViewCourseButton() {
        return viewCourseButton;
    }

    public CustomButton getAssignCourseInfoButton() {
        return assignCourseInfoButton;
    }

    public JLabel getLastActionCourseLabel() {
        return lastActionCourseLabel;
    }
}
