package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Course;
import com.faculty.management.model.TimetableEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static com.faculty.management.gui.member1_admin.AdminUIComponents.*;

/**
 * Timetable Form Dialog for Administrator (Member 1).
 * Supports creating, updating, and viewing timetable schedule entries.
 * 
 * Demonstrates Core OOP Principles:
 * - Classes and Objects: Dialog, Swing controls, and TimetableEntry model instantiation.
 * - Inheritance: Extends JDialog.
 * - Abstraction & Polymorphism: Mode-based polymorphic configuration (ADD, UPDATE, VIEW).
 * - Encapsulation: Private member variables with controlled public accessors.
 * - Error & Exception Handling: Catches ValidationException and DatabaseException with descriptive feedback.
 * - Database Handling: Connects to backend layers for persistent SQL Server / MySQL persistence.
 */
public class TimetableFormDialog extends JDialog {

    public enum FormMode {
        ADD,
        UPDATE,
        VIEW
    }

    private static final DateTimeFormatter DISPLAY_TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter ISO_TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private static final String[] DAYS_OF_WEEK = {
            "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"
    };

    private static final String[] SESSION_TYPES = {
            "THEORY", "PRACTICAL"
    };

    private static final String[] TIME_SLOTS = {
            "08:00 AM", "08:30 AM", "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM",
            "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM", "01:00 PM", "01:30 PM",
            "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM",
            "05:00 PM", "05:30 PM", "06:00 PM"
    };

    private static final String[] VENUES = {
            "Lecture Hall A1", "Lecture Hall A2", "Lecture Hall B1", "Lecture Hall B2",
            "Computer Lab 1", "Computer Lab 2", "Hardware Lab", "Bioprocess Lab", "Engineering Workshop"
    };

    // Encapsulated state
    private final FormMode mode;
    private final AdminController adminController;
    private boolean saved = false;
    private TimetableEntry timetableEntry;
    private int timetableIdToUpdate = 0;

    // UI Controls
    private JComboBox<Course> courseComboBox;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> startTimeComboBox;
    private JComboBox<String> endTimeComboBox;
    private JComboBox<String> sessionTypeComboBox;
    private JTextField lecturerField;
    private JComboBox<String> venueComboBox;
    private JComboBox<String> semesterComboBox;
    private JTextField academicYearField;
    private JLabel errorLabel;
    private BurgundyButton saveButton;
    private WhiteOutlineButton cancelButton;

    public TimetableFormDialog(JFrame parent, AdminController adminController) {
        this(parent, adminController, FormMode.ADD, null);
    }

    public TimetableFormDialog(JFrame parent, AdminController adminController, FormMode mode, TimetableEntry initialEntry) {
        super(parent, getTitleForMode(mode), true);
        this.mode = (mode != null) ? mode : FormMode.ADD;
        this.adminController = (adminController != null) ? adminController : new AdminController();
        this.timetableEntry = initialEntry;
        if (initialEntry != null) {
            this.timetableIdToUpdate = initialEntry.getTimetableId();
        }

        initComponents();
        loadCourses();
        if (initialEntry != null) {
            populateFields(initialEntry);
        }
        applyModeRestrictions();
    }

    private static String getTitleForMode(FormMode mode) {
        switch (mode) {
            case UPDATE:
                return "Edit Timetable Slot";
            case VIEW:
                return "Timetable Slot Details";
            case ADD:
            default:
                return "Create Timetable Slot";
        }
    }

    private void initComponents() {
        setSize(560, 680);
        setMinimumSize(new Dimension(500, 600));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_PAGE_BG);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BURGUNDY);
        headerPanel.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel titleLabel = new JLabel(getTitleForMode(mode));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Schedule lecture sessions, practical labs, and allocate venues.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(COLOR_TEXT_SUBHEADER);

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.add(titleLabel);
        headerText.add(Box.createVerticalStrut(3));
        headerText.add(subtitleLabel);

        headerPanel.add(headerText, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Form Fields Container
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);
        formContainer.setBorder(new EmptyBorder(20, 24, 16, 24));

        // 1. Course selection
        courseComboBox = new JComboBox<>();
        courseComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) {
                    Course c = (Course) value;
                    setText(c.getCourseCode() + " - " + c.getCourseName());
                } else if (value == null) {
                    setText("-- Select Course Module --");
                }
                return this;
            }
        });
        styleControl(courseComboBox);
        formContainer.add(createFieldWrapper("Course Module *", courseComboBox));
        formContainer.add(Box.createVerticalStrut(12));

        // 2. Day of Week & Session Type (Side by Side)
        JPanel dayTypeRow = new JPanel(new GridLayout(1, 2, 14, 0));
        dayTypeRow.setOpaque(false);

        dayComboBox = new JComboBox<>(DAYS_OF_WEEK);
        styleControl(dayComboBox);
        dayTypeRow.add(createFieldWrapper("Day of Week *", dayComboBox));

        sessionTypeComboBox = new JComboBox<>(SESSION_TYPES);
        styleControl(sessionTypeComboBox);
        dayTypeRow.add(createFieldWrapper("Session Type *", sessionTypeComboBox));

        formContainer.add(dayTypeRow);
        formContainer.add(Box.createVerticalStrut(12));

        // 3. Start Time & End Time (Side by Side)
        JPanel timeRow = new JPanel(new GridLayout(1, 2, 14, 0));
        timeRow.setOpaque(false);

        startTimeComboBox = new JComboBox<>(TIME_SLOTS);
        startTimeComboBox.setEditable(true);
        styleControl(startTimeComboBox);
        startTimeComboBox.setSelectedItem("08:00 AM");
        timeRow.add(createFieldWrapper("Start Time *", startTimeComboBox));

        endTimeComboBox = new JComboBox<>(TIME_SLOTS);
        endTimeComboBox.setEditable(true);
        styleControl(endTimeComboBox);
        endTimeComboBox.setSelectedItem("10:00 AM");
        timeRow.add(createFieldWrapper("End Time *", endTimeComboBox));

        formContainer.add(timeRow);
        formContainer.add(Box.createVerticalStrut(12));

        // 4. Lecturer / Instructor
        lecturerField = new JTextField();
        styleControl(lecturerField);
        formContainer.add(createFieldWrapper("Lecturer / Instructor Name", lecturerField));
        formContainer.add(Box.createVerticalStrut(12));

        // 5. Venue / Lab
        venueComboBox = new JComboBox<>(VENUES);
        venueComboBox.setEditable(true);
        styleControl(venueComboBox);
        formContainer.add(createFieldWrapper("Venue / Room / Lab *", venueComboBox));
        formContainer.add(Box.createVerticalStrut(12));

        // 6. Semester Number & Academic Year (Side by Side)
        JPanel semYearRow = new JPanel(new GridLayout(1, 2, 14, 0));
        semYearRow.setOpaque(false);

        String[] semesters = {"Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5", "Semester 6", "Semester 7", "Semester 8"};
        semesterComboBox = new JComboBox<>(semesters);
        semesterComboBox.setSelectedIndex(1); // Default Semester 2
        styleControl(semesterComboBox);
        semYearRow.add(createFieldWrapper("Semester *", semesterComboBox));

        academicYearField = new JTextField("2025/2026");
        styleControl(academicYearField);
        semYearRow.add(createFieldWrapper("Academic Year *", academicYearField));

        formContainer.add(semYearRow);
        formContainer.add(Box.createVerticalStrut(12));

        // Error message label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setForeground(new Color(185, 28, 28));
        formContainer.add(errorLabel);

        JScrollPane scrollPane = new JScrollPane(formContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        add(scrollPane, BorderLayout.CENTER);

        // Footer Action Buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER));

        cancelButton = new WhiteOutlineButton(mode == FormMode.VIEW ? "Close" : "Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 36));
        cancelButton.addActionListener(e -> dispose());
        footerPanel.add(cancelButton);

        if (mode != FormMode.VIEW) {
            saveButton = new BurgundyButton(mode == FormMode.UPDATE ? "Save Changes" : "Create Slot");
            saveButton.setPreferredSize(new Dimension(130, 36));
            saveButton.addActionListener(e -> handleSaveAction());
            footerPanel.add(saveButton);
        }

        add(footerPanel, BorderLayout.SOUTH);
    }

    private void styleControl(JComponent c) {
        c.setFont(FONT_BODY);
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, 36));
        c.setBackground(Color.WHITE);
        if (c instanceof JTextField) {
            ((JTextField) c).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_BORDER_INPUT, 1, true),
                    new EmptyBorder(4, 10, 4, 10)
            ));
        }
    }

    private JPanel createFieldWrapper(String labelText, JComponent field) {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FONT_BODY_BOLD);
        lbl.setForeground(COLOR_TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        wrap.add(lbl);
        wrap.add(Box.createVerticalStrut(4));
        wrap.add(field);
        return wrap;
    }

    private void loadCourses() {
        try {
            List<Course> courses = adminController.loadAllCourses();
            courseComboBox.removeAllItems();
            for (Course c : courses) {
                courseComboBox.addItem(c);
            }
        } catch (DatabaseException e) {
            errorLabel.setText("Failed to load courses: " + e.getMessage());
        }
    }

    private void populateFields(TimetableEntry entry) {
        if (entry.getCourse() != null) {
            for (int i = 0; i < courseComboBox.getItemCount(); i++) {
                Course c = courseComboBox.getItemAt(i);
                if (c.getCourseId() == entry.getCourse().getCourseId()) {
                    courseComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (entry.getDayOfWeek() != null) {
            dayComboBox.setSelectedItem(entry.getDayOfWeek().toUpperCase());
        }

        if (entry.getSessionType() != null) {
            sessionTypeComboBox.setSelectedItem(entry.getSessionType().toUpperCase());
        }

        if (entry.getStartTime() != null) {
            startTimeComboBox.setSelectedItem(entry.getStartTime().format(DISPLAY_TIME_FMT));
        }

        if (entry.getEndTime() != null) {
            endTimeComboBox.setSelectedItem(entry.getEndTime().format(DISPLAY_TIME_FMT));
        }

        if (entry.getLecturerName() != null) {
            lecturerField.setText(entry.getLecturerName());
        }

        if (entry.getVenue() != null) {
            venueComboBox.setSelectedItem(entry.getVenue());
        }

        int semIdx = entry.getSemesterNumber() - 1;
        if (semIdx >= 0 && semIdx < semesterComboBox.getItemCount()) {
            semesterComboBox.setSelectedIndex(semIdx);
        }

        if (entry.getAcademicYear() != null) {
            academicYearField.setText(entry.getAcademicYear());
        }
    }

    private void applyModeRestrictions() {
        if (mode == FormMode.VIEW) {
            courseComboBox.setEnabled(false);
            dayComboBox.setEnabled(false);
            startTimeComboBox.setEnabled(false);
            endTimeComboBox.setEnabled(false);
            sessionTypeComboBox.setEnabled(false);
            lecturerField.setEditable(false);
            venueComboBox.setEnabled(false);
            semesterComboBox.setEnabled(false);
            academicYearField.setEditable(false);
        }
    }

    private void handleSaveAction() {
        errorLabel.setText(" ");

        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        if (selectedCourse == null) {
            errorLabel.setText("Please select an academic course module.");
            return;
        }

        String dayOfWeek = (String) dayComboBox.getSelectedItem();
        String sessionType = (String) sessionTypeComboBox.getSelectedItem();

        LocalTime startTime = parseTime(startTimeComboBox.getSelectedItem());
        if (startTime == null) {
            errorLabel.setText("Invalid start time format. Use e.g. 08:00 AM or 08:00.");
            return;
        }

        LocalTime endTime = parseTime(endTimeComboBox.getSelectedItem());
        if (endTime == null) {
            errorLabel.setText("Invalid end time format. Use e.g. 10:00 AM or 10:00.");
            return;
        }

        if (!endTime.isAfter(startTime)) {
            errorLabel.setText("End time must be after start time.");
            return;
        }

        String lecturerName = lecturerField.getText().trim();
        String venue = venueComboBox.getSelectedItem() != null ? venueComboBox.getSelectedItem().toString().trim() : "";
        if (venue.isEmpty()) {
            errorLabel.setText("Please enter or select a lecture hall or laboratory venue.");
            return;
        }

        int semesterNumber = semesterComboBox.getSelectedIndex() + 1;
        String academicYear = academicYearField.getText().trim();
        if (academicYear.isEmpty()) {
            academicYear = "2025/2026";
        }

        try {
            boolean success;
            if (mode == FormMode.ADD) {
                success = adminController.handleCreateTimetable(
                        selectedCourse.getCourseId(), dayOfWeek, startTime, endTime,
                        sessionType, lecturerName, venue, semesterNumber, academicYear);
            } else {
                success = adminController.handleUpdateTimetable(
                        timetableIdToUpdate, selectedCourse.getCourseId(), dayOfWeek, startTime, endTime,
                        sessionType, lecturerName, venue, semesterNumber, academicYear);
            }

            if (success) {
                this.saved = true;
                JOptionPane.showMessageDialog(this,
                        (mode == FormMode.ADD ? "New timetable slot scheduled successfully." : "Timetable slot updated successfully."),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                errorLabel.setText("Unable to save timetable slot to database.");
            }
        } catch (ValidationException | DatabaseException ex) {
            errorLabel.setText(ex.getMessage());
        }
    }

    private LocalTime parseTime(Object val) {
        if (val == null) return null;
        String text = val.toString().trim();
        if (text.isEmpty()) return null;

        // Try standard 12-hour format "08:00 AM"
        try {
            return LocalTime.parse(text.toUpperCase(), DISPLAY_TIME_FMT);
        } catch (DateTimeParseException ignored) {
        }

        // Try standard 24-hour format "08:00" or "8:00"
        try {
            if (text.length() == 4 && text.charAt(1) == ':') {
                text = "0" + text;
            }
            return LocalTime.parse(text, ISO_TIME_FMT);
        } catch (DateTimeParseException ignored) {
        }

        return null;
    }

    public boolean isSaved() {
        return saved;
    }
}

