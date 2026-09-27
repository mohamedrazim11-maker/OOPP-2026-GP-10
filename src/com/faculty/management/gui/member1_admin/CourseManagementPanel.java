package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.gui.common.PrimaryButton;
import com.faculty.management.model.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Course Management Panel for Administrator (Member 1).
 * Represents the Course Management section in the Admin Dashboard.
 * 
 * Provides strictly the "Add Course" option UI for creating new academic courses.
 * Strictly avoids other options inside course management as requested.
 * 
 * Demonstrates Core OOP Principles:
 * - Classes & Objects: Instantiation of UI cards, controller, buttons, and models.
 * - Inheritance: Extends JPanel to inherit Swing container behavior.
 * - Abstraction: Controller & service layers abstract backend database logic from UI.
 * - Polymorphism: Swing event listener dispatching and controller delegation.
 * - Encapsulation: Private component fields with controlled access.
 * - Error & Exception Handling: Catches and manages errors through CourseFormDialog.
 * - Database Handling: Connects to MySQL through AdminController and CourseDAO.
 */
public class CourseManagementPanel extends JPanel {

    // Encapsulated Controller & Components
    private final AdminController adminController;
    private PrimaryButton addCourseButton;
    private JLabel lastAddedCourseLabel;

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
        setBorder(new EmptyBorder(30, 35, 30, 35));

        // 1. Header Section
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Course Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(15, 23, 42));

        JLabel subTitleLabel = new JLabel("Faculty of Technology — Course & Academic Module Management");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subTitleLabel.setForeground(new Color(100, 116, 139));

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subTitleLabel);

        // 2. Main Content Area with strictly the "Add Course" Option UI
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false);

        // Action Toolbar (Strictly only "Add Course" option)
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionToolbar.setOpaque(false);

        addCourseButton = new PrimaryButton("➕  Add Course");
        addCourseButton.setPreferredSize(new Dimension(160, 42));
        addCourseButton.setToolTipText("Open form to register a new academic course");
        addCourseButton.addActionListener(e -> handleAddCourseAction());

        actionToolbar.add(addCourseButton);

        // Add Course Feature Card (Clean presentation of the Add Course option)
        JPanel featureCard = new JPanel(new BorderLayout(0, 15));
        featureCard.setBackground(Color.WHITE);
        featureCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(30, 35, 30, 35)
        ));

        JPanel cardHeader = new JPanel();
        cardHeader.setLayout(new BoxLayout(cardHeader, BoxLayout.Y_AXIS));
        cardHeader.setOpaque(false);

        JLabel cardTitle = new JLabel("Add Courses Option");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cardTitle.setForeground(new Color(30, 41, 59));

        JLabel cardDescription = new JLabel("<html>" +
                "Register new course modules into the Faculty curriculum with complete academic details:<br>" +
                "• Unique Course Code (e.g. <b>ICT2132</b>, <b>BST2113</b>, <b>ENG2113</b>)<br>" +
                "• Course Module Name and Credit Value (1 to 10)<br>" +
                "• Theory & Practical Lecture Hours<br>" +
                "• Academic Department and Semester<br>" +
                "• Course Description and syllabus guidelines" +
                "</html>");
        cardDescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardDescription.setForeground(new Color(100, 116, 139));
        cardDescription.setBorder(new EmptyBorder(10, 0, 15, 0));

        cardHeader.add(cardTitle);
        cardHeader.add(cardDescription);

        // Status banner indicating the result of the last course added
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        statusPanel.setBackground(new Color(241, 245, 249));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(5, 12, 5, 12)
        ));

        lastAddedCourseLabel = new JLabel("Click 'Add Course' above to register a new module to MySQL database.");
        lastAddedCourseLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lastAddedCourseLabel.setForeground(new Color(71, 85, 105));

        statusPanel.add(lastAddedCourseLabel);

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
        CourseFormDialog dialog = new CourseFormDialog(parentFrame, adminController);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Course course = dialog.getCreatedCourse();
            if (course != null) {
                lastAddedCourseLabel.setText("✓ Successfully added: " + course.getCourseCode() + " — " +
                        course.getCourseName() + " (" + course.getCreditValue() + " Credits)");
                lastAddedCourseLabel.setForeground(new Color(22, 101, 52)); // Success green
            }
        }
    }
}
