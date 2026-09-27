package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.gui.common.PrimaryButton;
import com.faculty.management.gui.common.SecondaryButton;
import com.faculty.management.model.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Course Form Dialog for Administrator (Member 1).
 * Allows adding new courses to the Faculty Management System.
 * 
 * Demonstrates Core OOP Principles:
 * - Classes and Objects: Dialog, Swing components, and Course model instantiation.
 * - Inheritance: Extends JDialog to inherit standard modal window behaviors.
 * - Abstraction: Interacts with business logic through AdminController.
 * - Polymorphism: Event dispatching and overloaded controller calls.
 * - Encapsulation: Private UI controls and data fields with public accessors.
 * - Error and Exception Handling: Catches ValidationException and DatabaseException.
 * - Database Handling: Connects to MySQL database through backend layers.
 */
public class CourseFormDialog extends JDialog {

    private final AdminController adminController;
    private boolean saved = false;
    private Course createdCourse;

    // Encapsulated UI Components
    private JTextField courseCodeField;
    private JTextField courseNameField;
    private JSpinner creditSpinner;
    private JSpinner theoryHoursSpinner;
    private JSpinner practicalHoursSpinner;
    private JComboBox<String> departmentComboBox;
    private JComboBox<String> semesterComboBox;
    private JTextArea descriptionArea;
    private JLabel errorLabel;
    private JButton saveButton;
    private JButton cancelButton;

    public CourseFormDialog(JFrame parent, AdminController adminController) {
        super(parent, "Add New Course — Faculty Management System", true);
        this.adminController = (adminController != null) ? adminController : new AdminController();
        initComponents();
    }

    private void initComponents() {
        setSize(560, 680);
        setMinimumSize(new Dimension(500, 600));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        // 1. Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 58, 138)); // Deep Navy Blue
        headerPanel.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel titleLabel = new JLabel("Add New Course");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subTitleLabel = new JLabel("Enter academic course details to register a new module in the curriculum");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subTitleLabel.setForeground(new Color(226, 232, 240));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subTitleLabel, BorderLayout.SOUTH);

        // 2. Form Body (Scrollable)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);
        int gridY = 0;

        // Course Code
        gbc.gridx = 0;
        gbc.gridy = gridY;
        gbc.weightx = 0.35;
        formPanel.add(createFieldLabel("Course Code *"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        courseCodeField = new JTextField();
        courseCodeField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseCodeField.setToolTipText("e.g. ICT2132, BST2113, ENG2113");
        formPanel.add(courseCodeField, gbc);

        // Course Name
        gridY++;
        gbc.gridx = 0;
        gbc.gridy = gridY;
        gbc.weightx = 0.35;
        formPanel.add(createFieldLabel("Course Name *"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        courseNameField = new JTextField();
        courseNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseNameField.setToolTipText("e.g. Object Oriented Programming Practicum");
        formPanel.add(courseNameField, gbc);

        // Credit Value
        gridY++;
        gbc.gridx = 0;
        gbc.gridy = gridY;
        formPanel.add(createFieldLabel("Credit Value *"), gbc);

        gbc.gridx = 1;
        creditSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        creditSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(creditSpinner, gbc);

        // Theory Hours
        gridY++;
        gbc.gridx = 0;
        gbc.gridy = gridY;
        formPanel.add(createFieldLabel("Theory Hours (Total)"), gbc);

        gbc.gridx = 1;
        theoryHoursSpinner = new JSpinner(new SpinnerNumberModel(30, 0, 150, 5));
        theoryHoursSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(theoryHoursSpinner, gbc);

        // Practical Hours
        gridY++;
        gbc.gridx = 0;
        gbc.gridy = gridY;
        formPanel.add(createFieldLabel("Practical Hours (Total)"), gbc);

        gbc.gridx = 1;
        practicalHoursSpinner = new JSpinner(new SpinnerNumberModel(30, 0, 150, 5));
        practicalHoursSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(practicalHoursSpinner, gbc);

        // Department
        gridY++;
        gbc.gridx = 0;
        gbc.gridy = gridY;
        formPanel.add(createFieldLabel("Department *"), gbc);

        gbc.gridx = 1;
        String[] departments = {
                "-- Select Department --",
                "Department of Information & Communication Technology",
                "Department of Biosystems Technology",
                "Department of Engineering Technology",
                "Department of Multidisciplinary Studies"
        };
        departmentComboBox = new JComboBox<>(departments);
        departmentComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        departmentComboBox.setBackground(Color.WHITE);
        formPanel.add(departmentComboBox, gbc);

        // Semester
        gridY++;
        gbc.gridx = 0;
        gbc.gridy = gridY;
        formPanel.add(createFieldLabel("Semester *"), gbc);

        gbc.gridx = 1;
        String[] semesters = {"Semester 1", "Semester 2"};
        semesterComboBox = new JComboBox<>(semesters);
        semesterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        semesterComboBox.setBackground(Color.WHITE);
        formPanel.add(semesterComboBox, gbc);

        // Description
        gridY++;
        gbc.gridx = 0;
        gbc.gridy = gridY;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createFieldLabel("Description"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formPanel.add(descScroll, gbc);

        // Error message banner
        gridY++;
        gbc.gridx = 0;
        gbc.gridy = gridY;
        gbc.gridwidth = 2;
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setForeground(new Color(220, 38, 38));
        formPanel.add(errorLabel, gbc);

        JScrollPane centerScroll = new JScrollPane(formPanel);
        centerScroll.setBorder(null);

        // 3. Bottom Button Bar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        buttonPanel.setBackground(new Color(241, 245, 249));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        cancelButton = new SecondaryButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 38));
        cancelButton.addActionListener(e -> dispose());

        saveButton = new PrimaryButton("Add Course");
        saveButton.setPreferredSize(new Dimension(130, 38));
        saveButton.addActionListener(e -> handleSaveCourse());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(headerPanel, BorderLayout.NORTH);
        add(centerScroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(30, 41, 59));
        return label;
    }

    /**
     * Handles course submission, input validation, and database persistence.
     * Demonstrates Error & Exception Handling, Encapsulation, and Database Handling.
     */
    private void handleSaveCourse() {
        errorLabel.setText(" ");

        try {
            String courseCode = courseCodeField.getText().trim();
            String courseName = courseNameField.getText().trim();
            int creditValue = (Integer) creditSpinner.getValue();
            int theoryHours = (Integer) theoryHoursSpinner.getValue();
            int practicalHours = (Integer) practicalHoursSpinner.getValue();
            String selectedDept = (String) departmentComboBox.getSelectedItem();
            String selectedSem = (String) semesterComboBox.getSelectedItem();
            String description = descriptionArea.getText().trim();

            if (selectedDept == null || "-- Select Department --".equals(selectedDept)) {
                selectedDept = "";
            }

            // Create course model (Classes and Objects, Encapsulation)
            Course course = new Course();
            course.setCourseCode(courseCode);
            course.setCourseName(courseName);
            course.setCreditValue(creditValue);
            course.setTheoryHours(theoryHours);
            course.setPracticalHours(practicalHours);
            course.setDepartment(selectedDept);
            course.setSemester(selectedSem);
            course.setDescription(description);

            // Delegate to AdminController for validation and database persistence
            createdCourse = adminController.handleAddCourse(course);
            saved = true;

            JOptionPane.showMessageDialog(
                    this,
                    "Course '" + createdCourse.getCourseCode() + " - " + createdCourse.getCourseName() +
                    "' was successfully registered in the system!",
                    "Course Added Successfully",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (ValidationException ve) {
            errorLabel.setText(ve.getMessage());
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (DatabaseException de) {
            errorLabel.setText("Database error: " + de.getMessage());
            JOptionPane.showMessageDialog(this, de.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            errorLabel.setText("Unexpected error: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Encapsulated Accessors
    public boolean isSaved() {
        return saved;
    }

    public Course getCreatedCourse() {
        return createdCourse;
    }
}
