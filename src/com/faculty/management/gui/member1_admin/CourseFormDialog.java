package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.gui.common.CustomButton;
import com.faculty.management.gui.common.PrimaryButton;
import com.faculty.management.gui.common.SecondaryButton;
import com.faculty.management.model.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Course Form Dialog for Administrator (Member 1).
 * Supports "Add Course", "Update Course", "Delete Course", and "View Course" operations for Faculty Management System.
 * 
 * Demonstrates Core OOP Principles:
 * - Classes and Objects: Dialog, Swing components, and Course model instantiation.
 * - Inheritance: Extends JDialog to inherit standard modal window behaviors.
 * - Abstraction: Interacts with business logic through AdminController and CourseService layers.
 * - Polymorphism: Overloaded constructors, mode-based polymorphic form behavior (ADD, UPDATE, DELETE, VIEW).
 * - Encapsulation: Private UI controls, state variables, and data fields with public accessors.
 * - Error and Exception Handling: Catches ValidationException and DatabaseException with descriptive feedback.
 * - Database Handling: Connects to MySQL database through backend layers with offline fallback support.
 */
public class CourseFormDialog extends JDialog {

    /**
     * Enumeration defining the operational mode of the dialog.
     */
    public enum FormMode {
        ADD,
        UPDATE,
        DELETE,
        VIEW,
        ASSIGN_INFO
    }

    // Encapsulated Fields
    private final FormMode mode;
    private final AdminController adminController;
    private boolean saved = false;
    private Course course;
    private int courseIdToUpdate = 0;

    // Encapsulated UI Components
    private JComboBox<Course> courseSelectorComboBox;
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

    /**
     * Default constructor for Add Course mode.
     */
    public CourseFormDialog(JFrame parent, AdminController adminController) {
        this(parent, adminController, FormMode.ADD, null);
    }

    /**
     * Constructor specifying the operational mode (ADD, UPDATE, DELETE, VIEW, or ASSIGN_INFO).
     */
    public CourseFormDialog(JFrame parent, AdminController adminController, FormMode mode) {
        this(parent, adminController, mode, null);
    }

    /**
     * Overloaded constructor for Add, Update, Delete, View, or Assign Info with an initial course entity.
     */
    public CourseFormDialog(JFrame parent, AdminController adminController, FormMode mode, Course initialCourse) {
        super(parent, getDialogTitle(mode), true);
        this.mode = (mode != null) ? mode : FormMode.ADD;
        this.adminController = (adminController != null) ? adminController : new AdminController();
        this.course = initialCourse;
        if (initialCourse != null) {
            this.courseIdToUpdate = initialCourse.getCourseId();
        }

        initComponents();

        if (this.mode == FormMode.UPDATE || this.mode == FormMode.DELETE || this.mode == FormMode.VIEW || this.mode == FormMode.ASSIGN_INFO) {
            loadExistingCourses(initialCourse);
        }
    }

    private static String getDialogTitle(FormMode mode) {
        if (mode == FormMode.DELETE) {
            return "Delete Course — Faculty Management System";
        } else if (mode == FormMode.UPDATE) {
            return "Update Course — Faculty Management System";
        } else if (mode == FormMode.VIEW) {
            return "View Course Details — Faculty Management System";
        } else if (mode == FormMode.ASSIGN_INFO) {
            return "Assign Course Information — Faculty Management System";
        }
        return "Add New Course — Faculty Management System";
    }

    /**
     * Initializes all UI components and layouts.
     */
    private void initComponents() {
        setSize(580, 720);
        setMinimumSize(new Dimension(520, 640));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        // 1. Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        Color headerBg;
        if (mode == FormMode.DELETE) {
            headerBg = new Color(220, 38, 38); // Red for Delete
        } else if (mode == FormMode.UPDATE) {
            headerBg = new Color(15, 118, 110); // Teal for Update
        } else if (mode == FormMode.VIEW) {
            headerBg = new Color(37, 99, 235); // Royal Blue for View
        } else if (mode == FormMode.ASSIGN_INFO) {
            headerBg = new Color(109, 40, 217); // Purple for Assign Info
        } else {
            headerBg = new Color(30, 58, 138); // Navy for Add
        }
        headerPanel.setBackground(headerBg);
        headerPanel.setBorder(new EmptyBorder(18, 24, 18, 24));

        String titleText;
        if (mode == FormMode.DELETE) {
            titleText = "Delete Academic Course";
        } else if (mode == FormMode.UPDATE) {
            titleText = "Update Academic Course";
        } else if (mode == FormMode.VIEW) {
            titleText = "View Academic Course Details";
        } else if (mode == FormMode.ASSIGN_INFO) {
            titleText = "Assign Course Information";
        } else {
            titleText = "Add New Course";
        }
        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        String subTitleText;
        if (mode == FormMode.DELETE) {
            subTitleText = "Select an existing course module to permanently delete from MySQL database";
        } else if (mode == FormMode.UPDATE) {
            subTitleText = "Select an existing course module and modify its academic details in MySQL database";
        } else if (mode == FormMode.VIEW) {
            subTitleText = "Select an existing course module to inspect comprehensive curriculum details from MySQL database";
        } else if (mode == FormMode.ASSIGN_INFO) {
            subTitleText = "Select a course module and assign its academic department, semester, credits, and contact hours";
        } else {
            subTitleText = "Enter academic course details to register a new module in the curriculum";
        }
        JLabel subTitleLabel = new JLabel(subTitleText);
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

        // Course Selector (Displayed in UPDATE, DELETE, VIEW, and ASSIGN_INFO modes)
        if (mode == FormMode.UPDATE || mode == FormMode.DELETE || mode == FormMode.VIEW || mode == FormMode.ASSIGN_INFO) {
            gbc.gridx = 0;
            gbc.gridy = gridY;
            gbc.weightx = 0.35;
            String selectorLabel;
            if (mode == FormMode.DELETE) {
                selectorLabel = "Select Course to Delete *";
            } else if (mode == FormMode.VIEW) {
                selectorLabel = "Select Course to View *";
            } else if (mode == FormMode.ASSIGN_INFO) {
                selectorLabel = "Select Course to Assign Info *";
            } else {
                selectorLabel = "Select Course *";
            }
            formPanel.add(createFieldLabel(selectorLabel), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.65;
            courseSelectorComboBox = new JComboBox<>();
            courseSelectorComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            courseSelectorComboBox.setBackground(Color.WHITE);
            courseSelectorComboBox.addActionListener(e -> handleCourseSelectionChanged());
            formPanel.add(courseSelectorComboBox, gbc);
            gridY++;
        }

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

        // Set fields read-only in DELETE and VIEW modes, or Course Code & Name in ASSIGN_INFO mode
        if (mode == FormMode.DELETE || mode == FormMode.VIEW) {
            courseCodeField.setEditable(false);
            courseCodeField.setBackground(new Color(241, 245, 249));
            courseNameField.setEditable(false);
            courseNameField.setBackground(new Color(241, 245, 249));
            creditSpinner.setEnabled(false);
            theoryHoursSpinner.setEnabled(false);
            practicalHoursSpinner.setEnabled(false);
            departmentComboBox.setEnabled(false);
            semesterComboBox.setEnabled(false);
            descriptionArea.setEditable(false);
            descriptionArea.setBackground(new Color(241, 245, 249));
        } else if (mode == FormMode.ASSIGN_INFO) {
            courseCodeField.setEditable(false);
            courseCodeField.setBackground(new Color(241, 245, 249));
            courseNameField.setEditable(false);
            courseNameField.setBackground(new Color(241, 245, 249));
        }

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

        if (mode == FormMode.VIEW) {
            cancelButton = new PrimaryButton("Close");
            cancelButton.setPreferredSize(new Dimension(110, 38));
            cancelButton.addActionListener(e -> dispose());
            buttonPanel.add(cancelButton);
        } else {
            cancelButton = new SecondaryButton("Cancel");
            cancelButton.setPreferredSize(new Dimension(100, 38));
            cancelButton.addActionListener(e -> dispose());

            if (mode == FormMode.DELETE) {
                saveButton = new CustomButton(
                        "🗑️  Delete Course",
                        new Color(220, 38, 38), // Red 600
                        new Color(185, 28, 28), // Hover Red 700
                        new Color(153, 27, 27), // Pressed Red 800
                        Color.WHITE,
                        null,
                        8
                );
                saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
                saveButton.setPreferredSize(new Dimension(150, 38));
            } else if (mode == FormMode.UPDATE) {
                saveButton = new PrimaryButton("Update Course");
                saveButton.setPreferredSize(new Dimension(135, 38));
            } else if (mode == FormMode.ASSIGN_INFO) {
                saveButton = new CustomButton(
                        "📋  Assign Course Info",
                        new Color(109, 40, 217), // Purple 700
                        new Color(124, 58, 237), // Hover Purple 600
                        new Color(91, 33, 182),  // Pressed Purple 800
                        Color.WHITE,
                        null,
                        8
                );
                saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
                saveButton.setPreferredSize(new Dimension(185, 38));
            } else {
                saveButton = new PrimaryButton("Add Course");
                saveButton.setPreferredSize(new Dimension(135, 38));
            }
            saveButton.addActionListener(e -> handleFormSubmission());

            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);
        }

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
     * Loads existing courses from backend into the selector dropdown for UPDATE, DELETE, or VIEW mode.
     */
    private void loadExistingCourses(Course initialSelection) {
        try {
            List<Course> courses = adminController.loadAllCourses();
            courseSelectorComboBox.removeAllItems();

            if (courses == null || courses.isEmpty()) {
                String emptyMsg;
                if (mode == FormMode.DELETE) {
                    emptyMsg = "No courses found in database to delete.";
                } else if (mode == FormMode.VIEW) {
                    emptyMsg = "No courses found in database to view.";
                } else if (mode == FormMode.ASSIGN_INFO) {
                    emptyMsg = "No courses found in database to assign information.";
                } else {
                    emptyMsg = "No courses found in database to update.";
                }
                errorLabel.setText(emptyMsg);
                if (saveButton != null) {
                    saveButton.setEnabled(false);
                }
                return;
            }

            for (Course c : courses) {
                courseSelectorComboBox.addItem(c);
            }

            if (initialSelection != null) {
                for (int i = 0; i < courseSelectorComboBox.getItemCount(); i++) {
                    Course c = courseSelectorComboBox.getItemAt(i);
                    if (c.getCourseId() == initialSelection.getCourseId()) {
                        courseSelectorComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else if (courseSelectorComboBox.getItemCount() > 0) {
                courseSelectorComboBox.setSelectedIndex(0);
            }

            Course current = (Course) courseSelectorComboBox.getSelectedItem();
            if (current != null) {
                populateFields(current);
            }

        } catch (DatabaseException ex) {
            errorLabel.setText("Failed to load courses: " + ex.getMessage());
        }
    }

    /**
     * Handles course selection changes in UPDATE and DELETE modes.
     */
    private void handleCourseSelectionChanged() {
        Course selected = (Course) courseSelectorComboBox.getSelectedItem();
        if (selected != null) {
            populateFields(selected);
        }
    }

    /**
     * Populates form fields from a Course object.
     */
    private void populateFields(Course c) {
        if (c == null) return;
        this.courseIdToUpdate = c.getCourseId();
        courseCodeField.setText(c.getCourseCode());
        courseNameField.setText(c.getCourseName());
        creditSpinner.setValue(c.getCreditValue());
        theoryHoursSpinner.setValue(c.getTheoryHours());
        practicalHoursSpinner.setValue(c.getPracticalHours());

        if (c.getDepartment() != null) {
            departmentComboBox.setSelectedItem(c.getDepartment());
        } else {
            departmentComboBox.setSelectedIndex(0);
        }

        if (c.getSemester() != null) {
            semesterComboBox.setSelectedItem(c.getSemester());
        } else {
            semesterComboBox.setSelectedIndex(0);
        }

        descriptionArea.setText(c.getDescription() != null ? c.getDescription() : "");
        errorLabel.setText(" ");
    }

    /**
     * Handles course submission (Add, Update, or Delete) with input validation and database persistence.
     * Demonstrates Error & Exception Handling, Encapsulation, and Database Handling.
     */
    private void handleFormSubmission() {
        errorLabel.setText(" ");

        try {
            // If in DELETE mode, perform delete confirmation and execution
            if (mode == FormMode.DELETE) {
                if (courseIdToUpdate <= 0) {
                    throw new ValidationException("Please select a valid course to delete.");
                }

                Course selectedCourse = (Course) courseSelectorComboBox.getSelectedItem();
                String courseInfo = (selectedCourse != null)
                        ? selectedCourse.getCourseCode() + " - " + selectedCourse.getCourseName()
                        : "ID #" + courseIdToUpdate;

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to permanently delete course '" + courseInfo + "'?\nThis action cannot be undone!",
                        "Confirm Course Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                boolean success = adminController.handleDeleteCourse(courseIdToUpdate);
                if (success) {
                    this.course = selectedCourse;
                    this.saved = true;

                    JOptionPane.showMessageDialog(
                            this,
                            "Course '" + courseInfo + "' was successfully deleted from the system!",
                            "Course Deleted Successfully",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    dispose();
                }
                return;
            }

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
            Course targetCourse = new Course();
            targetCourse.setCourseCode(courseCode);
            targetCourse.setCourseName(courseName);
            targetCourse.setCreditValue(creditValue);
            targetCourse.setTheoryHours(theoryHours);
            targetCourse.setPracticalHours(practicalHours);
            targetCourse.setDepartment(selectedDept);
            targetCourse.setSemester(selectedSem);
            targetCourse.setDescription(description);

            if (mode == FormMode.ASSIGN_INFO) {
                if (courseIdToUpdate <= 0) {
                    throw new ValidationException("Please select a valid course to assign information.");
                }
                targetCourse.setCourseId(courseIdToUpdate);

                // Perform assignment in database via controller
                boolean success = adminController.handleAssignCourseInformation(
                        courseIdToUpdate, selectedDept, selectedSem, creditValue, theoryHours, practicalHours, description);
                if (success) {
                    this.course = targetCourse;
                    this.saved = true;

                    JOptionPane.showMessageDialog(
                            this,
                            "Course information for '" + targetCourse.getCourseCode() + " - " + targetCourse.getCourseName() +
                            "' was successfully assigned and updated in the system!",
                            "Course Information Assigned",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    dispose();
                }
            } else if (mode == FormMode.UPDATE) {
                if (courseIdToUpdate <= 0) {
                    throw new ValidationException("Please select a course to update.");
                }
                targetCourse.setCourseId(courseIdToUpdate);

                // Perform update in database via controller
                boolean success = adminController.handleUpdateCourse(targetCourse);
                if (success) {
                    this.course = targetCourse;
                    this.saved = true;

                    JOptionPane.showMessageDialog(
                            this,
                            "Course '" + targetCourse.getCourseCode() + " - " + targetCourse.getCourseName() +
                            "' was successfully updated in the system!",
                            "Course Updated Successfully",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    dispose();
                }
            } else {
                // Perform addition in database via controller
                Course created = adminController.handleAddCourse(targetCourse);
                this.course = created;
                this.saved = true;

                JOptionPane.showMessageDialog(
                        this,
                        "Course '" + created.getCourseCode() + " - " + created.getCourseName() +
                        "' was successfully registered in the system!",
                        "Course Added Successfully",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                dispose();
            }

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
        return course;
    }

    public Course getCourse() {
        return course;
    }

    public Course getSelectedCourse() {
        if (courseSelectorComboBox != null && courseSelectorComboBox.getSelectedItem() != null) {
            return (Course) courseSelectorComboBox.getSelectedItem();
        }
        return course;
    }

    public FormMode getMode() {
        return mode;
    }
}
