package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.dao.CourseDAO;
import com.faculty.management.dao.UndergraduateDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.InvalidMarkException;
import com.faculty.management.gui.common.DangerButton;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Course;
import com.faculty.management.model.Lecturer;
import com.faculty.management.model.Mark;
import com.faculty.management.model.Undergraduate;
import com.faculty.management.service.MarkService;

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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.faculty.management.gui.member2_lecturer.LecturerUIComponents.*;

/**
 * Modern Marks Entry & Management Panel for Member 2 (Lecturer Module).
 * Implements:
 * - Batch dropdown (displays available batches)
 * - Semester dropdown (displays semesters)
 * - Course dropdown (dynamically updates based on selected semester/batch)
 * - Table displays: Student ID number, Student Name, Course Code, Course Name, Semester, Batch, Assessment, Marks
 * - Full capability for lecturer to Enter, Edit, and Update student marks with real-time database persistence.
 */
public class MarkManagementPanel extends JPanel {

    private final MarkService markService = new MarkService();
    private final CourseDAO courseDAO = new CourseDAO();
    private final UndergraduateDAO undergraduateDAO = new UndergraduateDAO();

    private Lecturer currentLecturer;
    private List<Course> allCourses = new ArrayList<>();
    private List<Undergraduate> allStudents = new ArrayList<>();
    private List<Mark> allMarks = new ArrayList<>();

    // Filter controls
    private JComboBox<String> cmbBatch;
    private JComboBox<String> cmbSemester;
    private JComboBox<String> cmbCourse;
    private SearchTextField txtSearch;
    private WhiteOutlineButton btnResetFilters;

    // Table
    private JTable markTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel lblFooterSummary;

    // Action buttons
    private BurgundyButton btnEnterMarks;
    private WhiteOutlineButton btnEditMark;
    private WhiteOutlineButton btnDeleteMark;

    private static final String[] SEMESTERS = {
            "All Semesters",
            "Semester 1",
            "Semester 2",
            "Semester 3",
            "Semester 4",
            "Semester 5",
            "Semester 6",
            "Semester 7",
            "Semester 8"
    };

    public MarkManagementPanel() {
        this(null);
    }

    public MarkManagementPanel(Lecturer lecturer) {
        this.currentLecturer = lecturer;
        setLayout(new BorderLayout(0, 14));
        setBackground(COLOR_PAGE_BG);
        setBorder(new EmptyBorder(0, 20, 16, 20));

        loadReferenceData();
        initUI();
        loadAllMarks();
    }

    private void loadReferenceData() {
        try {
            allCourses = courseDAO.getAllCourses();
        } catch (Exception e) {
            allCourses = new ArrayList<>();
        }

        try {
            allStudents = undergraduateDAO.findAll();
        } catch (Exception e) {
            allStudents = new ArrayList<>();
        }
    }

    private void initUI() {
        // 1. Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("Marks Entry & Grading");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(COLOR_TEXT_DARK);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageSub = new JLabel("Filter and manage student marks and evaluations.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);
        pageSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(pageSub);

        // Header Actions
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        headerActions.setOpaque(false);

        btnEnterMarks = new BurgundyButton("+ Enter Marks");
        btnEnterMarks.setPreferredSize(new Dimension(115, 34));
        btnEnterMarks.addActionListener(e -> openEnterMarksDialog());

        btnEditMark = new WhiteOutlineButton("Edit Mark");
        btnEditMark.setPreferredSize(new Dimension(95, 34));
        btnEditMark.addActionListener(e -> openEditSelectedDialog());

        btnDeleteMark = new WhiteOutlineButton("Delete");
        btnDeleteMark.setPreferredSize(new Dimension(75, 34));
        btnDeleteMark.setForeground(new Color(220, 38, 38));
        btnDeleteMark.addActionListener(e -> deleteSelectedMark());

        headerActions.add(btnEnterMarks);
        headerActions.add(btnEditMark);
        headerActions.add(btnDeleteMark);

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(headerActions, BorderLayout.EAST);

        // 2. Filter Toolbar Panel (Batch, Semester, Course, Search)
        JPanel filterToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterToolbar.setBackground(Color.WHITE);
        filterToolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        // Batch Dropdown
        JLabel lblBatch = new JLabel("Batch:");
        lblBatch.setFont(FONT_BODY_BOLD);
        lblBatch.setForeground(COLOR_TEXT_DARK);

        List<String> batchesList = markService.getAvailableBatches();
        cmbBatch = new JComboBox<>(batchesList.toArray(new String[0]));
        styleComboBox(cmbBatch);
        cmbBatch.setPreferredSize(new Dimension(120, 32));
        cmbBatch.addActionListener(e -> applyFilter());

        // Semester Dropdown
        JLabel lblSemester = new JLabel("Semester:");
        lblSemester.setFont(FONT_BODY_BOLD);
        lblSemester.setForeground(COLOR_TEXT_DARK);

        cmbSemester = new JComboBox<>(SEMESTERS);
        styleComboBox(cmbSemester);
        cmbSemester.setPreferredSize(new Dimension(115, 32));
        cmbSemester.addActionListener(e -> {
            updateCoursesDropdownForSelectedSemester();
            applyFilter();
        });

        // Course Dropdown (dynamically filtered by semester)
        JLabel lblCourse = new JLabel("Course:");
        lblCourse.setFont(FONT_BODY_BOLD);
        lblCourse.setForeground(COLOR_TEXT_DARK);

        cmbCourse = new JComboBox<>();
        styleComboBox(cmbCourse);
        cmbCourse.setPreferredSize(new Dimension(200, 32));
        updateCoursesDropdownForSelectedSemester();
        cmbCourse.addActionListener(e -> applyFilter());

        // Quick Search Field
        txtSearch = new SearchTextField("Search ID / Name...", 14);
        txtSearch.setPreferredSize(new Dimension(160, 32));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });

        // Reset Button
        btnResetFilters = createRefreshButton();
        btnResetFilters.setPreferredSize(new Dimension(75, 32));
        btnResetFilters.setText("Reset");
        btnResetFilters.addActionListener(e -> resetFilters());

        filterToolbar.add(lblBatch);
        filterToolbar.add(cmbBatch);
        filterToolbar.add(Box.createHorizontalStrut(6));
        filterToolbar.add(lblSemester);
        filterToolbar.add(cmbSemester);
        filterToolbar.add(Box.createHorizontalStrut(6));
        filterToolbar.add(lblCourse);
        filterToolbar.add(cmbCourse);
        filterToolbar.add(Box.createHorizontalStrut(8));
        filterToolbar.add(txtSearch);
        filterToolbar.add(btnResetFilters);

        // 3. Marks Data Table
        String[] columns = {
                "Student ID", "Student Name", "Course Code", "Course Name",
                "Semester", "Batch", "Assessment Component", "Marks", "_MARK_OBJ_"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        markTable = new JTable(tableModel);
        markTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        markTable.setPreferredScrollableViewportSize(new Dimension(800, 450));
        markTable.setRowHeight(38);
        markTable.setShowVerticalLines(false);
        markTable.setGridColor(new Color(243, 244, 246));
        markTable.setSelectionBackground(new Color(254, 249, 235));
        markTable.setSelectionForeground(COLOR_TEXT_DARK);
        markTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        markTable.setFont(FONT_BODY);

        // Table Header Styling
        JTableHeader th = markTable.getTableHeader();
        th.setFont(FONT_SMALL_BOLD);
        th.setForeground(COLOR_TEXT_MUTED);
        th.setBackground(Color.WHITE);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Alignments & Widths
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer rightRender = new DefaultTableCellRenderer();
        rightRender.setHorizontalAlignment(SwingConstants.RIGHT);
        rightRender.setBorder(new EmptyBorder(0, 0, 0, 12));

        DefaultTableCellRenderer leftPadding = new DefaultTableCellRenderer();
        leftPadding.setBorder(new EmptyBorder(0, 8, 0, 0));

        markTable.getColumnModel().getColumn(0).setPreferredWidth(90);  // Student ID
        markTable.getColumnModel().getColumn(0).setCellRenderer(centerRender);

        markTable.getColumnModel().getColumn(1).setPreferredWidth(140); // Student Name
        markTable.getColumnModel().getColumn(1).setCellRenderer(leftPadding);

        markTable.getColumnModel().getColumn(2).setPreferredWidth(85);  // Course Code
        markTable.getColumnModel().getColumn(2).setCellRenderer(centerRender);

        markTable.getColumnModel().getColumn(3).setPreferredWidth(180); // Course Name
        markTable.getColumnModel().getColumn(3).setCellRenderer(leftPadding);

        markTable.getColumnModel().getColumn(4).setPreferredWidth(85);  // Semester
        markTable.getColumnModel().getColumn(4).setCellRenderer(centerRender);

        markTable.getColumnModel().getColumn(5).setPreferredWidth(90);  // Batch
        markTable.getColumnModel().getColumn(5).setCellRenderer(centerRender);

        markTable.getColumnModel().getColumn(6).setPreferredWidth(190); // Assessment
        markTable.getColumnModel().getColumn(6).setCellRenderer(leftPadding);

        markTable.getColumnModel().getColumn(7).setPreferredWidth(70);  // Marks
        markTable.getColumnModel().getColumn(7).setCellRenderer(centerRender);

        // Hide raw Mark object column
        markTable.getColumnModel().getColumn(8).setMinWidth(0);
        markTable.getColumnModel().getColumn(8).setMaxWidth(0);
        markTable.getColumnModel().getColumn(8).setPreferredWidth(0);

        // Double click on any row to open Edit Dialog
        markTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && markTable.getSelectedRow() != -1) {
                    openEditSelectedDialog();
                }
            }
        });

        rowSorter = new TableRowSorter<>(tableModel);
        markTable.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(markTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));

        // 4. Footer Summary
        lblFooterSummary = new JLabel("Showing 0 marks · Double-click a row or click 'Edit Selected Mark' to update marks.");
        lblFooterSummary.setFont(FONT_SMALL);
        lblFooterSummary.setForeground(COLOR_TEXT_MUTED);
        lblFooterSummary.setBorder(new EmptyBorder(4, 4, 4, 4));

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(filterToolbar, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(lblFooterSummary, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Dynamically updates the Course dropdown based on the selected semester.
     */
    private void updateCoursesDropdownForSelectedSemester() {
        if (cmbCourse == null) return;

        Object prevSelected = cmbCourse.getSelectedItem();
        cmbCourse.removeAllItems();
        cmbCourse.addItem("All Courses");

        int selectedSemIndex = (cmbSemester != null) ? cmbSemester.getSelectedIndex() : 0;

        for (Course c : allCourses) {
            if (selectedSemIndex == 0) {
                // All Semesters
                cmbCourse.addItem(c.getCourseCode() + " - " + c.getCourseName());
            } else {
                // Filter by selected semester number (1 to 8)
                boolean match = (c.getSemesterNumber() == selectedSemIndex) ||
                        (c.getSemester() != null && c.getSemester().toLowerCase().contains("semester " + selectedSemIndex));
                if (match) {
                    cmbCourse.addItem(c.getCourseCode() + " - " + c.getCourseName());
                }
            }
        }

        // Restore previously selected if valid
        if (prevSelected != null) {
            for (int i = 0; i < cmbCourse.getItemCount(); i++) {
                if (cmbCourse.getItemAt(i).equals(prevSelected)) {
                    cmbCourse.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    /**
     * Loads all marks from the database and updates the table.
     */
    public void loadAllMarks() {
        tableModel.setRowCount(0);
        allMarks.clear();

        try {
            allMarks = markService.getAllMarks();
            for (Mark m : allMarks) {
                tableModel.addRow(new Object[]{
                        m.getStudentUsername(),
                        m.getStudentName(),
                        m.getCourseCode() != null ? m.getCourseCode() : "-",
                        m.getCourseName() != null ? m.getCourseName() : "-",
                        m.getSemesterNumber() > 0 ? "Semester " + m.getSemesterNumber() : "-",
                        m.getBatch() != null ? m.getBatch() : "2025/2026",
                        m.getEvaluationTitle() != null ? m.getEvaluationTitle() : "General Assessment",
                        String.format("%.1f", m.getMarksObtained()),
                        m
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load marks from database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        applyFilter();
    }

    /**
     * Applies the Batch, Semester, Course, and Search filters across table rows.
     */
    private void applyFilter() {
        if (rowSorter == null) return;

        String selectedBatch = cmbBatch != null ? (String) cmbBatch.getSelectedItem() : "All Batches";
        String selectedSemester = cmbSemester != null ? (String) cmbSemester.getSelectedItem() : "All Semesters";
        String selectedCourseItem = cmbCourse != null ? (String) cmbCourse.getSelectedItem() : "All Courses";
        String query = txtSearch != null ? txtSearch.getText().trim().toLowerCase() : "";

        String targetCourseCode = "";
        if (selectedCourseItem != null && !selectedCourseItem.equals("All Courses")) {
            targetCourseCode = selectedCourseItem.split(" - ")[0].trim();
        }

        final String fBatch = selectedBatch != null ? selectedBatch : "All Batches";
        final String fSem = selectedSemester != null ? selectedSemester : "All Semesters";
        final String fCourseCode = targetCourseCode;
        final String fQuery = query;

        rowSorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String stdId = entry.getStringValue(0).toLowerCase();
                String stdName = entry.getStringValue(1).toLowerCase();
                String cCode = entry.getStringValue(2);
                String cName = entry.getStringValue(3).toLowerCase();
                String sem = entry.getStringValue(4);
                String batch = entry.getStringValue(5);
                String eval = entry.getStringValue(6).toLowerCase();

                // 1. Batch Filter
                if (!fBatch.equals("All Batches") && !batch.equalsIgnoreCase(fBatch)) {
                    return false;
                }

                // 2. Semester Filter
                if (!fSem.equals("All Semesters") && !sem.equalsIgnoreCase(fSem)) {
                    return false;
                }

                // 3. Course Filter
                if (!fCourseCode.isEmpty() && !cCode.equalsIgnoreCase(fCourseCode)) {
                    return false;
                }

                // 4. Text Query Filter
                if (!fQuery.isEmpty()) {
                    boolean match = stdId.contains(fQuery) ||
                            stdName.contains(fQuery) ||
                            cCode.toLowerCase().contains(fQuery) ||
                            cName.contains(fQuery) ||
                            eval.contains(fQuery);
                    if (!match) return false;
                }

                return true;
            }
        });

        int visibleCount = markTable.getRowCount();
        lblFooterSummary.setText("Showing " + visibleCount + " of " + allMarks.size() + " marks · Double-click a row or click 'Edit Selected Mark' to update marks.");
    }

    private void resetFilters() {
        if (cmbBatch != null && cmbBatch.getItemCount() > 0) cmbBatch.setSelectedIndex(0);
        if (cmbSemester != null && cmbSemester.getItemCount() > 0) cmbSemester.setSelectedIndex(0);
        if (cmbCourse != null && cmbCourse.getItemCount() > 0) cmbCourse.setSelectedIndex(0);
        if (txtSearch != null) txtSearch.setText("");
        applyFilter();
    }

    /**
     * Opens Enter Marks dialog to enter new marks for a student.
     */
    private void openEnterMarksDialog() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        EnterMarksDialog dialog = new EnterMarksDialog(parent, allCourses, allStudents,
                (String) cmbBatch.getSelectedItem(),
                (String) cmbSemester.getSelectedItem(),
                (String) cmbCourse.getSelectedItem());
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadAllMarks();
        }
    }

    /**
     * Opens Edit Mark dialog for the selected row to update marks in the database.
     */
    private void openEditSelectedDialog() {
        int selectedViewRow = markTable.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a mark record from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = markTable.convertRowIndexToModel(selectedViewRow);
        Mark mark = (Mark) tableModel.getValueAt(modelRow, 8);

        if (mark == null) {
            JOptionPane.showMessageDialog(this, "Unable to resolve selected mark record.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        EditMarkDialog dialog = new EditMarkDialog(parent, mark);
        dialog.setVisible(true);

        if (dialog.isUpdated()) {
            loadAllMarks();
        }
    }

    /**
     * Deletes the selected mark record from database after user confirmation.
     */
    private void deleteSelectedMark() {
        int selectedViewRow = markTable.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a mark record from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = markTable.convertRowIndexToModel(selectedViewRow);
        Mark mark = (Mark) tableModel.getValueAt(modelRow, 8);
        if (mark == null) return;

        String std = mark.getStudentUsername() + " (" + mark.getStudentName() + ")";
        String course = mark.getCourseCode() + " - " + mark.getEvaluationTitle();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the mark for:\n" + std + "\nCourse & Evaluation: " + course + "\nScore: " + mark.getMarksObtained(),
                "Confirm Delete Mark",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted;
                if (mark.getMarkId() > 0) {
                    deleted = markService.deleteMarkById(mark.getMarkId());
                } else {
                    deleted = markService.removeMark(mark.getStudentId(), mark.getEvaluationId());
                }

                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Mark deleted successfully from database.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAllMarks();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete mark from database.", "Notice", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error deleting mark: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // INNER DIALOG: ENTER NEW MARKS
    // =========================================================================
    private class EnterMarksDialog extends JDialog {
        private boolean saved = false;

        private JComboBox<String> dlgBatch;
        private JComboBox<String> dlgSemester;
        private JComboBox<String> dlgCourse;
        private JComboBox<String> dlgStudent;
        private JComboBox<String> dlgEvaluation;
        private JTextField dlgMarks;

        private static final String[] DEFAULT_EVALUATIONS = {
                "Quiz 1 - Fundamental Concepts",
                "Continuous Assessment 1 - Assignment / Problem Set",
                "Mid-Semester Theory Examination",
                "Practical Lab Test & Viva",
                "End-Semester Final Examination"
        };

        public EnterMarksDialog(Frame parent, List<Course> courses, List<Undergraduate> students,
                                String initialBatch, String initialSem, String initialCourse) {
            super(parent, "Enter Student Marks", true);
            setSize(520, 500);
            setLocationRelativeTo(parent);
            setResizable(false);
            setLayout(new BorderLayout());
            getContentPane().setBackground(COLOR_PAGE_BG);

            // Header
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(COLOR_BURGUNDY);
            header.setBorder(new EmptyBorder(16, 20, 16, 20));

            JLabel title = new JLabel("Enter Student Marks");
            title.setFont(new Font("Segoe UI", Font.BOLD, 17));
            title.setForeground(Color.WHITE);

            JLabel sub = new JLabel("Record student evaluation scores and assessment marks directly into the database.");
            sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            sub.setForeground(new Color(254, 205, 211));

            JPanel headerText = new JPanel();
            headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
            headerText.setOpaque(false);
            headerText.add(title);
            headerText.add(Box.createVerticalStrut(3));
            headerText.add(sub);
            header.add(headerText, BorderLayout.CENTER);
            add(header, BorderLayout.NORTH);

            // Form
            JPanel form = new JPanel(new GridLayout(6, 2, 12, 14));
            form.setOpaque(false);
            form.setBorder(new EmptyBorder(20, 24, 20, 24));

            // 1. Batch
            List<String> bList = markService.getAvailableBatches();
            dlgBatch = new JComboBox<>(bList.toArray(new String[0]));
            if (initialBatch != null && !initialBatch.equals("All Batches")) dlgBatch.setSelectedItem(initialBatch);
            else if (dlgBatch.getItemCount() > 1) dlgBatch.setSelectedIndex(1); // default 2025/2026

            // 2. Semester
            dlgSemester = new JComboBox<>(new String[]{
                    "Semester 1", "Semester 2", "Semester 3", "Semester 4",
                    "Semester 5", "Semester 6", "Semester 7", "Semester 8"
            });
            if (initialSem != null && !initialSem.equals("All Semesters")) {
                dlgSemester.setSelectedItem(initialSem);
            } else {
                dlgSemester.setSelectedIndex(1); // default Semester 2
            }
            dlgSemester.addActionListener(e -> updateDlgCourses(courses));

            // 3. Course
            dlgCourse = new JComboBox<>();
            updateDlgCourses(courses);
            if (initialCourse != null && !initialCourse.equals("All Courses")) {
                dlgCourse.setSelectedItem(initialCourse);
            }

            // 4. Student Selector
            dlgStudent = new JComboBox<>();
            for (Undergraduate u : students) {
                dlgStudent.addItem(u.getUsername() + " - " + u.getFullName());
            }
            dlgStudent.setEditable(true);

            // 5. Evaluation Component
            dlgEvaluation = new JComboBox<>(DEFAULT_EVALUATIONS);
            dlgEvaluation.setEditable(true);

            // 6. Marks
            dlgMarks = new JTextField();
            dlgMarks.setFont(FONT_BODY);

            form.add(createLabel("Batch / Academic Year *"));
            form.add(dlgBatch);
            form.add(createLabel("Semester *"));
            form.add(dlgSemester);
            form.add(createLabel("Course Module *"));
            form.add(dlgCourse);
            form.add(createLabel("Student (Reg No / Name) *"));
            form.add(dlgStudent);
            form.add(createLabel("Evaluation / Component *"));
            form.add(dlgEvaluation);
            form.add(createLabel("Marks Obtained (0 - 100) *"));
            form.add(dlgMarks);

            add(form, BorderLayout.CENTER);

            // Footer
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
            footer.setBackground(Color.WHITE);
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER));

            WhiteOutlineButton btnCancel = new WhiteOutlineButton("Cancel");
            btnCancel.setPreferredSize(new Dimension(90, 36));
            btnCancel.addActionListener(e -> dispose());

            BurgundyButton btnSave = new BurgundyButton("Save Mark");
            btnSave.setPreferredSize(new Dimension(120, 36));
            btnSave.addActionListener(e -> handleSave(courses));

            footer.add(btnCancel);
            footer.add(btnSave);
            add(footer, BorderLayout.SOUTH);
        }

        private void updateDlgCourses(List<Course> courses) {
            dlgCourse.removeAllItems();
            int semNo = dlgSemester.getSelectedIndex() + 1;
            for (Course c : courses) {
                if (c.getSemesterNumber() == semNo || semNo == 0) {
                    dlgCourse.addItem(c.getCourseCode() + " - " + c.getCourseName());
                }
            }
            if (dlgCourse.getItemCount() == 0) {
                for (Course c : courses) {
                    dlgCourse.addItem(c.getCourseCode() + " - " + c.getCourseName());
                }
            }
        }

        private JLabel createLabel(String text) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(FONT_BODY_BOLD);
            lbl.setForeground(COLOR_TEXT_DARK);
            return lbl;
        }

        private void handleSave(List<Course> courses) {
            String studentItem = (String) dlgStudent.getSelectedItem();
            String courseItem = (String) dlgCourse.getSelectedItem();
            String evalItem = (String) dlgEvaluation.getSelectedItem();
            String marksStr = dlgMarks.getText().trim();

            if (studentItem == null || studentItem.trim().isEmpty() ||
                    courseItem == null || courseItem.trim().isEmpty() ||
                    evalItem == null || evalItem.trim().isEmpty() ||
                    marksStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double score;
            try {
                score = Double.parseDouble(marksStr);
                if (score < 0.0 || score > 100.0) {
                    JOptionPane.showMessageDialog(this, "Marks must strictly be between 0.0 and 100.0.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric mark (e.g. 85.0).", "Invalid Format", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Extract student ID/username
            String studentId = studentItem.split(" - ")[0].trim();

            // Extract course ID
            String courseCode = courseItem.split(" - ")[0].trim();
            int courseId = 1;
            for (Course c : courses) {
                if (c.getCourseCode().equalsIgnoreCase(courseCode)) {
                    courseId = c.getCourseId();
                    break;
                }
            }

            // Determine eval type
            String evalType = "ASSESSMENT";
            String upEval = evalItem.toUpperCase();
            if (upEval.contains("QUIZ")) evalType = "QUIZ";
            else if (upEval.contains("MID")) evalType = "MID_TERM";
            else if (upEval.contains("PRACTICAL") || upEval.contains("LAB")) evalType = "PRACTICAL";
            else if (upEval.contains("FINAL") || upEval.contains("EXAM")) evalType = "FINAL";

            try {
                int evalId = markService.getOrCreateEvaluation(courseId, evalItem, evalType, 100.0);
                boolean ok = markService.saveOrUpdateMark(studentId, evalId, score);

                if (ok) {
                    saved = true;
                    JOptionPane.showMessageDialog(this, "Mark successfully saved to the database!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save mark in database.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public boolean isSaved() {
            return saved;
        }
    }

    // =========================================================================
    // INNER DIALOG: EDIT / UPDATE MARK
    // =========================================================================
    private class EditMarkDialog extends JDialog {
        private boolean updated = false;
        private JTextField txtNewMarks;

        public EditMarkDialog(Frame parent, Mark mark) {
            super(parent, "Edit & Update Student Mark", true);
            setSize(460, 380);
            setLocationRelativeTo(parent);
            setResizable(false);
            setLayout(new BorderLayout());
            getContentPane().setBackground(COLOR_PAGE_BG);

            // Header
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(COLOR_BURGUNDY);
            header.setBorder(new EmptyBorder(16, 20, 16, 20));

            JLabel title = new JLabel("Update Student Mark");
            title.setFont(new Font("Segoe UI", Font.BOLD, 17));
            title.setForeground(Color.WHITE);

            JLabel sub = new JLabel("Modify score and commit changes directly to the database.");
            sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            sub.setForeground(new Color(254, 205, 211));

            JPanel headerText = new JPanel();
            headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
            headerText.setOpaque(false);
            headerText.add(title);
            headerText.add(Box.createVerticalStrut(3));
            headerText.add(sub);
            header.add(headerText, BorderLayout.CENTER);
            add(header, BorderLayout.NORTH);

            // Form
            JPanel form = new JPanel(new GridLayout(5, 2, 10, 12));
            form.setOpaque(false);
            form.setBorder(new EmptyBorder(20, 24, 16, 24));

            JLabel lblStudent = new JLabel(mark.getStudentUsername() + " (" + mark.getStudentName() + ")");
            lblStudent.setFont(FONT_BODY_BOLD);
            lblStudent.setForeground(COLOR_TEXT_DARK);

            JLabel lblCourse = new JLabel(mark.getCourseCode() + " - " + mark.getCourseName());
            lblCourse.setFont(FONT_BODY);
            lblCourse.setForeground(COLOR_TEXT_DARK);

            JLabel lblEval = new JLabel(mark.getEvaluationTitle() != null ? mark.getEvaluationTitle() : "Assessment");
            lblEval.setFont(FONT_BODY);
            lblEval.setForeground(COLOR_TEXT_DARK);

            JLabel lblCurrent = new JLabel(String.format("%.1f / %.1f", mark.getMarksObtained(), mark.getMaxMarks()));
            lblCurrent.setFont(FONT_BODY_BOLD);
            lblCurrent.setForeground(COLOR_BURGUNDY);

            txtNewMarks = new JTextField(String.format("%.1f", mark.getMarksObtained()));
            txtNewMarks.setFont(FONT_BODY_BOLD);
            txtNewMarks.setPreferredSize(new Dimension(100, 34));

            form.add(createBoldLabel("Student:"));
            form.add(lblStudent);
            form.add(createBoldLabel("Course:"));
            form.add(lblCourse);
            form.add(createBoldLabel("Assessment:"));
            form.add(lblEval);
            form.add(createBoldLabel("Current Score:"));
            form.add(lblCurrent);
            form.add(createBoldLabel("New Mark (0 - 100) *:"));
            form.add(txtNewMarks);

            add(form, BorderLayout.CENTER);

            // Footer
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
            footer.setBackground(Color.WHITE);
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER));

            WhiteOutlineButton btnCancel = new WhiteOutlineButton("Cancel");
            btnCancel.setPreferredSize(new Dimension(90, 36));
            btnCancel.addActionListener(e -> dispose());

            BurgundyButton btnUpdate = new BurgundyButton("Update Mark");
            btnUpdate.setPreferredSize(new Dimension(130, 36));
            btnUpdate.addActionListener(e -> handleUpdate(mark));

            footer.add(btnCancel);
            footer.add(btnUpdate);
            add(footer, BorderLayout.SOUTH);
        }

        private JLabel createBoldLabel(String text) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(FONT_BODY_BOLD);
            lbl.setForeground(COLOR_TEXT_DARK);
            return lbl;
        }

        private void handleUpdate(Mark mark) {
            String input = txtNewMarks.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a mark score.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double newScore;
            try {
                newScore = Double.parseDouble(input);
                if (newScore < 0.0 || newScore > 100.0) {
                    JOptionPane.showMessageDialog(this, "Marks must strictly be between 0.0 and 100.0.", "Range Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric number (e.g. 88.5).", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                boolean success;
                if (mark.getMarkId() > 0) {
                    success = markService.updateMarkById(mark.getMarkId(), newScore);
                } else {
                    success = markService.saveOrUpdateMark(mark.getStudentId(), mark.getEvaluationId(), newScore);
                }

                if (success) {
                    updated = true;
                    JOptionPane.showMessageDialog(this, "Mark updated successfully in the database!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update mark in database.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database update error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public boolean isUpdated() {
            return updated;
        }
    }
}
