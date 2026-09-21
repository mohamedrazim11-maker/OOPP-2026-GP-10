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
 * Course Form Dialog for Administrator Course Management.
 * Strictly manages Course information (View, Add, Update).
 * 
 * Demonstrates:
 * - Classes & Objects: Dialog and component instances
 * - Inheritance: Extends JDialog
 * - Abstraction & Polymorphism: Mode-based polymorphic behavior (VIEW, ADD, UPDATE)
 * - Encapsulation: Private fields with accessors
 * - Error & Exception Handling: Catches ValidationException and DatabaseException
 */
public class CourseFormDialog extends JDialog {

    public enum FormMode {
        VIEW,
        ADD,
        UPDATE
    }

    private final FormMode mode;
    private final AdminController adminController;
    private boolean saved = false;
    private Course courseData;

    // Encapsulated UI components
    private JTextField codeField;
    private JTextField nameField;
    private JSpinner creditSpinner;
    private JSpinner theoryHoursSpinner;
    private JSpinner practicalHoursSpinner;
    private JComboBox<String> departmentComboBox;
    private JComboBox<String> semesterComboBox;
    private JTextArea descriptionArea;
    private JLabel errorLabel;
    private JButton saveButton;
    private JButton cancelButton;

    public CourseFormDialog(JFrame parent, String title, FormMode mode, Course course, AdminController adminController) {
        super(parent, title, true);
        this.mode = mode;
        this.courseData = course;
        this.adminController = (adminController != null) ? adminController : new AdminController();
        initComponents();
        if (course != null) {
            populateFields(course);
        }
        applyModeRestrictions();
    }

    public CourseFormDialog(JFrame parent, String title, FormMode mode, AdminController adminController) {
        this(parent, title, mode, null, adminController);
    }

    public CourseFormDialog(JFrame parent, String title, FormMode mode) {
        this(parent, title, mode, null, new AdminController());
    }

    private void initComponents() {
        setSize(520, 620);
        setMinimumSize(new Dimension(480, 560));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        setResizable(false);

        // 1. Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(16, 185, 129)); // Emerald Green for Course Management
        headerPanel.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel titleLabel = new JLabel(getHeaderTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // 2. Form Content Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Error message label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setForeground(new Color(220, 38, 38));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(errorLabel, gbc);
        gbc.gridwidth = 1;

        // Course Code
        JLabel codeLabel = createFieldLabel("Course Code *");
        codeField = createStyledTextField();
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(codeLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(codeField, gbc);

        // Course Name
        JLabel nameLabel = createFieldLabel("Course Name *");
        nameField = createStyledTextField();
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(nameField, gbc);

        // Credit Value
        JLabel creditLabel = createFieldLabel("Credit Value *");
        creditSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        styleSpinner(creditSpinner);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(creditLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(creditSpinner, gbc);

        // Theory Hours
        JLabel theoryLabel = createFieldLabel("Theory Hours");
        theoryHoursSpinner = new JSpinner(new SpinnerNumberModel(30, 0, 150, 5));
        styleSpinner(theoryHoursSpinner);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(theoryLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(theoryHoursSpinner, gbc);

        // Practical Hours
        JLabel practicalLabel = createFieldLabel("Practical Hours");
        practicalHoursSpinner = new JSpinner(new SpinnerNumberModel(30, 0, 150, 5));
        styleSpinner(practicalHoursSpinner);
        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(practicalLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 5; formPanel.add(practicalHoursSpinner, gbc);

        // Department
        JLabel deptLabel = createFieldLabel("Department *");
        String[] departments = {
                "Department of Information & Communication Technology",
                "Department of Biosystems Technology",
                "Department of Engineering Technology",
                "Multidisciplinary"
        };
        departmentComboBox = new JComboBox<>(departments);
        departmentComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        departmentComboBox.setBackground(Color.WHITE);
        departmentComboBox.setPreferredSize(new Dimension(0, 32));
        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(deptLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 6; formPanel.add(departmentComboBox, gbc);

        // Semester
        JLabel semLabel = createFieldLabel("Semester");
        String[] semesters = {"Semester 1", "Semester 2"};
        semesterComboBox = new JComboBox<>(semesters);
        semesterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        semesterComboBox.setBackground(Color.WHITE);
        semesterComboBox.setSelectedItem("Semester 2");
        semesterComboBox.setPreferredSize(new Dimension(0, 32));
        gbc.gridx = 0; gbc.gridy = 7; formPanel.add(semLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 7; formPanel.add(semesterComboBox, gbc);

        // Description / Syllabus Summary
        JLabel descLabel = createFieldLabel("Description");
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        gbc.gridx = 0; gbc.gridy = 8; formPanel.add(descLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 8; formPanel.add(descScrollPane, gbc);

        // 3. Bottom Action Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(new Color(248, 250, 252));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        cancelButton = new SecondaryButton(mode == FormMode.VIEW ? "Close" : "Cancel");
        cancelButton.setPreferredSize(new Dimension(90, 34));
        cancelButton.addActionListener(e -> dispose());

        saveButton = new PrimaryButton(getSaveButtonText());
        saveButton.setPreferredSize(new Dimension(140, 34));
        saveButton.addActionListener(e -> handleSave());

        buttonPanel.add(cancelButton);
        if (mode != FormMode.VIEW) {
            buttonPanel.add(saveButton);
        }

        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private String getHeaderTitle() {
        switch (mode) {
            case VIEW:
                return "Course Details (Read-Only)";
            case ADD:
                return "Add New Course Module";
            case UPDATE:
                return "Update Course Module";
            default:
                return "Course Form";
        }
    }

    private String getSaveButtonText() {
        switch (mode) {
            case ADD:
                return "Add Course";
            case UPDATE:
                return "Save Changes";
            default:
                return "Save";
        }
    }

    private void applyModeRestrictions() {
        if (mode == FormMode.VIEW) {
            codeField.setEditable(false);
            nameField.setEditable(false);
            creditSpinner.setEnabled(false);
            theoryHoursSpinner.setEnabled(false);
            practicalHoursSpinner.setEnabled(false);
            departmentComboBox.setEnabled(false);
            semesterComboBox.setEnabled(false);
            descriptionArea.setEditable(false);
        }
    }

    private void populateFields(Course c) {
        if (c == null) return;
        codeField.setText(c.getCourseCode() != null ? c.getCourseCode() : "");
        nameField.setText(c.getCourseName() != null ? c.getCourseName() : "");
        creditSpinner.setValue(c.getCreditValue() > 0 ? c.getCreditValue() : 2);
        theoryHoursSpinner.setValue(c.getTheoryHours() >= 0 ? c.getTheoryHours() : 30);
        practicalHoursSpinner.setValue(c.getPracticalHours() >= 0 ? c.getPracticalHours() : 30);
        if (c.getDepartment() != null) departmentComboBox.setSelectedItem(c.getDepartment());
        if (c.getSemester() != null) semesterComboBox.setSelectedItem(c.getSemester());
        descriptionArea.setText(c.getDescription() != null ? c.getDescription() : "");
    }

    private void handleSave() {
        errorLabel.setText(" ");
        String code = codeField.getText().trim().toUpperCase();
        String name = nameField.getText().trim();
        int credits = (int) creditSpinner.getValue();
        int theory = (int) theoryHoursSpinner.getValue();
        int practical = (int) practicalHoursSpinner.getValue();
        String dept = (String) departmentComboBox.getSelectedItem();
        String sem = (String) semesterComboBox.getSelectedItem();
        String desc = descriptionArea.getText().trim();

        try {
            if (mode == FormMode.ADD) {
                Course newCourse = new Course(code, name, credits, theory, practical, dept, sem, desc);
                courseData = adminController.handleAddCourse(newCourse);
            } else if (mode == FormMode.UPDATE) {
                int id = (courseData != null) ? courseData.getCourseId() : 0;
                Course updated = new Course(id, code, name, credits, theory, practical, dept, sem, desc);
                adminController.handleUpdateCourse(updated);
                courseData = updated;
            }

            saved = true;
            dispose();

        } catch (ValidationException ex) {
            errorLabel.setText("⚠️ " + ex.getMessage());
        } catch (DatabaseException ex) {
            errorLabel.setText("⚠️ Database Error: " + ex.getMessage());
        } catch (Exception ex) {
            errorLabel.setText("⚠️ Error: " + ex.getMessage());
        }
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(51, 65, 85));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(0, 32));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinner.setPreferredSize(new Dimension(0, 32));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setHorizontalAlignment(JTextField.LEFT);
        }
    }

    // Encapsulated Getters
    public boolean isSaved() {
        return saved;
    }

    public Course getCourseData() {
        return courseData;
    }
}
