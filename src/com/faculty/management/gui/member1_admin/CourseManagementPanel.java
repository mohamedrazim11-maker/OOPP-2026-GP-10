package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.gui.common.CustomButton;
import com.faculty.management.gui.common.PrimaryButton;
import com.faculty.management.model.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Course Management Panel for Administrator (Member 1).
 * Represents the Course Management section in the Admin Dashboard.
 * 
 * Provides strictly the "Add Course", "Update Course", "Delete Course", and "View Courses" options UI for managing academic courses.
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
    private JLabel lastActionCourseLabel;

    public CourseManagementPanel() {
        this.adminController = new AdminController();
        initComponents();
    }

    public CourseManagementPanel(AdminController adminController) {
        this.adminController = (adminController != null) ? adminController : new AdminController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // 1. Header Title & Description
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel mainTitleLabel = new JLabel("Course Management");
        mainTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mainTitleLabel.setForeground(new Color(15, 23, 42));

        JLabel subTitleLabel = new JLabel("Register and maintain academic course modules for Faculty of Technology degree programmes.");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subTitleLabel.setForeground(new Color(100, 116, 139));

        headerPanel.add(mainTitleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subTitleLabel);

        // 2. Main Content Area with strictly "Add Course", "Update Course", "Delete Course", and "View Courses" Options UI
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false);

        // Action Toolbar (Strictly only "Add Course", "Update Course", "Delete Course", and "View Courses" options)
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actionToolbar.setOpaque(false);

        addCourseButton = new PrimaryButton("➕  Add Course");
        addCourseButton.setPreferredSize(new Dimension(160, 42));
        addCourseButton.setToolTipText("Open form to register a new academic course");
        addCourseButton.addActionListener(e -> handleAddCourseAction());

        updateCourseButton = new CustomButton(
                "✏️  Update Course",
                new Color(15, 118, 110), // Teal background
                new Color(13, 148, 136), // Hover Teal
                new Color(17, 94, 89),   // Pressed Dark Teal
                Color.WHITE,             // White text
                null,
                8                        // Corner radius
        );
        updateCourseButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateCourseButton.setPreferredSize(new Dimension(170, 42));
        updateCourseButton.setToolTipText("Select and modify an existing academic course module");
        updateCourseButton.addActionListener(e -> handleUpdateCourseAction());

        deleteCourseButton = new CustomButton(
                "🗑️  Delete Course",
                new Color(220, 38, 38), // Red background
                new Color(185, 28, 28), // Hover Red
                new Color(153, 27, 27), // Pressed Dark Red
                Color.WHITE,            // White text
                null,
                8                       // Corner radius
        );
        deleteCourseButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteCourseButton.setPreferredSize(new Dimension(165, 42));
        deleteCourseButton.setToolTipText("Select and permanently remove an existing academic course module");
        deleteCourseButton.addActionListener(e -> handleDeleteCourseAction());

        viewCourseButton = new CustomButton(
                "👁️  View Courses",
                new Color(37, 99, 235), // Royal Blue 600
                new Color(29, 78, 216), // Hover Blue 700
                new Color(30, 58, 138), // Pressed Dark Navy
                Color.WHITE,            // White text
                null,
                8                       // Corner radius
        );
        viewCourseButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewCourseButton.setPreferredSize(new Dimension(165, 42));
        viewCourseButton.setToolTipText("Browse and inspect comprehensive details of academic course modules");
        viewCourseButton.addActionListener(e -> handleViewCoursesAction());

        actionToolbar.add(addCourseButton);
        actionToolbar.add(updateCourseButton);
        actionToolbar.add(deleteCourseButton);
        actionToolbar.add(viewCourseButton);

        // Feature Card (Clean presentation of the Course Management options)
        JPanel featureCard = new JPanel(new BorderLayout(0, 15));
        featureCard.setBackground(Color.WHITE);
        featureCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(30, 35, 30, 35)
        ));

        JPanel cardHeader = new JPanel();
        cardHeader.setLayout(new BoxLayout(cardHeader, BoxLayout.Y_AXIS));
        cardHeader.setOpaque(false);

        JLabel cardTitle = new JLabel("Course Management Operations");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cardTitle.setForeground(new Color(30, 41, 59));

        JLabel cardDescription = new JLabel("<html>" +
                "Manage academic course modules in the Faculty curriculum:<br><br>" +
                "• <b>Add Courses Option:</b> Register new course modules with unique Course Code (e.g. <b>ICT2132</b>, <b>BST2113</b>, <b>ENG2113</b>), " +
                "Course Name, Credit Value (1 to 10), Theory & Practical Hours, Department, and Semester.<br><br>" +
                "• <b>Update Courses Option:</b> Select an existing course module from the database and update its course code, module name, credit value, " +
                "lecture/practical contact hours, academic department, semester, and syllabus description.<br><br>" +
                "• <b>Delete Courses Option:</b> Select an existing course module from the database, review its curriculum details, and permanently delete it with validation and database integrity checks.<br><br>" +
                "• <b>View Courses Option:</b> Browse and inspect complete academic course details including module codes, titles, credits, contact hours, department allocations, and syllabus descriptions from MySQL database.<br><br>" +
                "<i>(Note: This section strictly contains only the Add Course, Update Course, Delete Course, and View Courses management options).</i>" +
                "</html>");
        cardDescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardDescription.setForeground(new Color(100, 116, 139));
        cardDescription.setBorder(new EmptyBorder(10, 0, 15, 0));

        cardHeader.add(cardTitle);
        cardHeader.add(cardDescription);

        // Status banner indicating the result of the last course added, updated, deleted, or viewed
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        statusPanel.setBackground(new Color(241, 245, 249));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(5, 12, 5, 12)
        ));

        lastActionCourseLabel = new JLabel("Click 'Add Course', 'Update Course', 'Delete Course', or 'View Courses' to manage academic modules in MySQL database.");
        lastActionCourseLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lastActionCourseLabel.setForeground(new Color(71, 85, 105));

        statusPanel.add(lastActionCourseLabel);

        featureCard.add(cardHeader, BorderLayout.NORTH);
        featureCard.add(statusPanel, BorderLayout.CENTER);

        mainContentPanel.add(actionToolbar);
        mainContentPanel.add(Box.createVerticalStrut(18));
        mainContentPanel.add(featureCard);

        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
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
            Course course = dialog.getCreatedCourse();
            if (course != null) {
                lastActionCourseLabel.setText("✓ Successfully added: " + course.getCourseCode() + " — " +
                        course.getCourseName() + " (" + course.getCreditValue() + " Credits)");
                lastActionCourseLabel.setForeground(new Color(22, 101, 52)); // Success green
            }
        }
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

    public JLabel getLastActionCourseLabel() {
        return lastActionCourseLabel;
    }
}
