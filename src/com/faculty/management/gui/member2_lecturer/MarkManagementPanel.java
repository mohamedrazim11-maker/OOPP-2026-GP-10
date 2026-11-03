package com.faculty.management.gui.member2_lecturer;

<<<<<<< HEAD
import com.faculty.management.dao.CourseDAO;
import com.faculty.management.gui.common.DangerButton;
import com.faculty.management.gui.common.PrimaryButton;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Course;
=======
>>>>>>> Aysha
import com.faculty.management.model.Mark;
import com.faculty.management.service.MarkService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
<<<<<<< HEAD
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Marks Management & Summary Panel (Requirement 4 & 6).
 * Provides views for:
 * 1. Individual Marks (search student)
 * 2. Whole-Batch Marks (view all students)
 * 3. Marks by Subject (course breakdown by Quiz, Assessment, Mid-term, Practical, Final)
 * 4. Final Marks (final exam & overall scores)
 * Includes CRUD: Add Marks, Edit Selected, Delete Selected.
 */
public class MarkManagementPanel extends JPanel {
    private JTabbedPane summaryTabs;

    // --- Tab 1: Marks Entry & Individual Marks ---
    private JTextField txtSearchStudent;
    private JTextField txtSearchEval;
    private JButton btnSearchStudent;
    private JButton btnSearchEval;
    private JButton btnAddMark;
    private JButton btnEditMark;
    private JButton btnDeleteMark;
    private JTable markTable;
    private DefaultTableModel tableModel;

    // --- Tab 2: Marks by Subject & Whole-Batch ---
    private JComboBox<String> cmbSubjectFilter;
    private JButton btnLoadSubjectMarks;
    private JTable subjectTable;
    private DefaultTableModel subjectTableModel;
    private List<Course> courseList;

    // --- Tab 3: Final Marks Summary ---
    private JComboBox<String> cmbFinalSubjectFilter;
    private JButton btnLoadFinalMarks;
    private JTable finalMarksTable;
    private DefaultTableModel finalMarksTableModel;

    private final MarkService markService = new MarkService();
    private final CourseDAO courseDAO = new CourseDAO();

    public MarkManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(12, 16, 12, 16));
        loadCourses();
        initUI();
    }

    private void loadCourses() {
        try {
            courseList = courseDAO.getAllCourses();
        } catch (Exception e) {
            courseList = List.of();
        }
    }

    private void initUI() {
        summaryTabs = new JTabbedPane();
        UITheme.styleTabbedPane(summaryTabs);

        // Sub-Screen 1: Individual Marks & CRUD
        summaryTabs.addTab("Individual Marks & Entry", createIndividualMarksView());

        // Sub-Screen 2: Whole-Batch Marks by Subject
        summaryTabs.addTab("Whole-Batch Marks by Subject", createMarksBySubjectView());

        // Sub-Screen 3: Final Marks Summary
        summaryTabs.addTab("Final Marks Summary", createFinalMarksView());

        // Automatically load marks when switching to batch or final views
        summaryTabs.addChangeListener(e -> {
            int selected = summaryTabs.getSelectedIndex();
            if (selected == 1) {
                loadSubjectMarks();
            } else if (selected == 2) {
                loadFinalMarks();
            }
        });

        add(summaryTabs, BorderLayout.CENTER);

        // Initial background load
        SwingUtilities.invokeLater(() -> {
            loadSubjectMarks();
            loadFinalMarks();
        });
    }

    private JPanel createIndividualMarksView() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        // Top Toolbar Panel (Student Lookup & Filters)
        JPanel topToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topToolbar.setOpaque(false);

        txtSearchStudent = new JTextField(10);
        txtSearchStudent.setFont(UITheme.FONT_BODY);
        txtSearchStudent.setPreferredSize(new Dimension(110, 32));
        txtSearchStudent.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(2, 6, 2, 6)
        ));

        btnSearchStudent = new JButton("Search Student");
        btnSearchStudent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearchStudent.setBackground(Color.WHITE);
        btnSearchStudent.setForeground(UITheme.TEXT_BODY);
        btnSearchStudent.setFocusPainted(false);
        btnSearchStudent.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearchStudent.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(5, 12, 5, 12)
        ));

        txtSearchEval = new JTextField(8);
        txtSearchEval.setFont(UITheme.FONT_BODY);
        txtSearchEval.setPreferredSize(new Dimension(90, 32));
        txtSearchEval.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(2, 6, 2, 6)
        ));

        btnSearchEval = new JButton("Search Evaluation");
        btnSearchEval.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearchEval.setBackground(Color.WHITE);
        btnSearchEval.setForeground(UITheme.TEXT_BODY);
        btnSearchEval.setFocusPainted(false);
        btnSearchEval.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearchEval.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(5, 12, 5, 12)
        ));

        JLabel lblStudent = new JLabel("Student ID:");
        lblStudent.setFont(UITheme.FONT_SMALL_BOLD);
        lblStudent.setForeground(UITheme.TEXT_BODY);

        JLabel lblEval = new JLabel("Eval ID:");
        lblEval.setFont(UITheme.FONT_SMALL_BOLD);
        lblEval.setForeground(UITheme.TEXT_BODY);

        topToolbar.add(lblStudent);
        topToolbar.add(txtSearchStudent);
        topToolbar.add(btnSearchStudent);
        topToolbar.add(Box.createHorizontalStrut(8));
        topToolbar.add(lblEval);
        topToolbar.add(txtSearchEval);
        topToolbar.add(btnSearchEval);

        // Table Model Setup
        tableModel = new DefaultTableModel(new String[]{"Student ID", "Evaluation ID", "Component Type", "Marks Obtained (out of 100)"}, 0) {
=======
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.faculty.management.gui.member2_lecturer.LecturerUIComponents.*;

/**
 * Marks Entry & Management Panel for Member 2.
 * Strictly implements Page 4 of the Ruhuna Lecturer Portal Specification.
 */
public class MarkManagementPanel extends JPanel {

    private final MarkService markService = new MarkService();

    private SearchTextField searchStudentField;
    private SearchTextField searchEvalField;
    private JTable markTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel footerSummaryLabel;

    public MarkManagementPanel() {
        initComponents();
        loadAllMarks();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(COLOR_PAGE_BG);
        setBorder(new EmptyBorder(0, 28, 20, 28));

        // 1. Header: Title, Subtitle, and "+ Add marks" button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("Marks Entry");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(COLOR_TEXT_DARK);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pageTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel pageSub = new JLabel("Add, edit and remove student marks by evaluation.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);
        pageSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        pageSub.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        BurgundyButton addMarksBtn = new BurgundyButton("+ Add marks");
        addMarksBtn.setPreferredSize(new Dimension(135, 38));
        addMarksBtn.addActionListener(e -> openAddDialog());

        headerPanel.add(titleBox, BorderLayout.CENTER);
        headerPanel.add(addMarksBtn, BorderLayout.EAST);

        // 2. Filter Toolbar
        JPanel filterBar = new JPanel(new BorderLayout(10, 0));
        filterBar.setOpaque(false);

        searchStudentField = new SearchTextField("Search by student ID", 18);
        searchStudentField.setPreferredSize(new Dimension(200, 36));
        searchStudentField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        searchEvalField = new SearchTextField("Search by evaluation ID", 18);
        searchEvalField.setPreferredSize(new Dimension(200, 36));
        searchEvalField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        WhiteOutlineButton refreshBtn = createRefreshButton();
        refreshBtn.setPreferredSize(new Dimension(100, 36));
        refreshBtn.addActionListener(e -> {
            searchStudentField.setText("");
            searchEvalField.setText("");
            loadAllMarks();
        });

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftFilters.setOpaque(false);
        leftFilters.add(searchStudentField);
        leftFilters.add(searchEvalField);

        filterBar.add(leftFilters, BorderLayout.WEST);
        filterBar.add(refreshBtn, BorderLayout.EAST);

        // 3. Table Container
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setOpaque(false);

        String[] columns = {"STUDENT ID", "EVALUATION ID", "MARKS OBTAINED", "ACTIONS", "_DATA_"};
        tableModel = new DefaultTableModel(columns, 0) {
>>>>>>> Aysha
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        markTable = new JTable(tableModel);
<<<<<<< HEAD
        UITheme.styleTable(markTable);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        markTable.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        markTable.getColumnModel().getColumn(1).setCellRenderer(centerRender);
        markTable.getColumnModel().getColumn(3).setCellRenderer(centerRender);

        // Bottom Action Panel per Wireframe 04: [ + Add Marks ] [ Edit Selected ] [ Delete Selected ]
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bottomPanel.setOpaque(false);

        btnAddMark = new PrimaryButton("+ Add Marks");
        btnAddMark.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddMark.setPreferredSize(new Dimension(130, 34));

        btnEditMark = new JButton("Edit Selected");
        btnEditMark.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEditMark.setBackground(Color.WHITE);
        btnEditMark.setForeground(UITheme.TEXT_BODY);
        btnEditMark.setFocusPainted(false);
        btnEditMark.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditMark.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

        btnDeleteMark = new DangerButton("Delete Selected");
        btnDeleteMark.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDeleteMark.setPreferredSize(new Dimension(140, 34));

        bottomPanel.add(btnAddMark);
        bottomPanel.add(btnEditMark);
        bottomPanel.add(btnDeleteMark);

        // Event Listeners
        btnSearchStudent.addActionListener(e -> loadStudentMarks());
        btnSearchEval.addActionListener(e -> loadEvaluationMarks());
        btnAddMark.addActionListener(e -> openAddDialog());
        btnEditMark.addActionListener(e -> openEditDialog());
        btnDeleteMark.addActionListener(e -> deleteSelectedMark());

        panel.add(topToolbar, BorderLayout.NORTH);
        panel.add(UITheme.createModernScrollPane(markTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMarksBySubjectView() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 6, 10, 6));

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterBar.setOpaque(false);

        JLabel lblSubject = new JLabel("Subject / Course:");
        lblSubject.setFont(UITheme.FONT_BODY_BOLD);
        lblSubject.setForeground(UITheme.TEXT_BODY);

        cmbSubjectFilter = new JComboBox<>();
        cmbSubjectFilter.setFont(UITheme.FONT_BODY);
        cmbSubjectFilter.setPreferredSize(new Dimension(300, 34));
        populateCourseCombo(cmbSubjectFilter);
        cmbSubjectFilter.addActionListener(e -> loadSubjectMarks());

        btnLoadSubjectMarks = new JButton("Refresh / View Marks");
        btnLoadSubjectMarks.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLoadSubjectMarks.setBackground(new Color(13, 148, 136)); // Teal
        btnLoadSubjectMarks.setForeground(Color.WHITE);
        btnLoadSubjectMarks.setFocusPainted(false);
        btnLoadSubjectMarks.setOpaque(true);
        btnLoadSubjectMarks.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLoadSubjectMarks.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(13, 148, 136), 1),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btnLoadSubjectMarks.addActionListener(e -> loadSubjectMarks());

        filterBar.add(lblSubject);
        filterBar.add(cmbSubjectFilter);
        filterBar.add(btnLoadSubjectMarks);

        // Subject Table
        subjectTableModel = new DefaultTableModel(new String[]{
                "Student ID", "Quiz Marks", "Assessment", "Mid-Semester", "Practical", "CA %", "CA Status", "Final Exam"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        subjectTable = new JTable(subjectTableModel);
        UITheme.styleTable(subjectTable);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < subjectTable.getColumnCount(); i++) {
            subjectTable.getColumnModel().getColumn(i).setCellRenderer(centerRender);
        }

        panel.add(filterBar, BorderLayout.NORTH);
        panel.add(UITheme.createModernScrollPane(subjectTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFinalMarksView() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 6, 10, 6));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterBar.setOpaque(false);

        JLabel lblSubject = new JLabel("Subject / Course:");
        lblSubject.setFont(UITheme.FONT_BODY_BOLD);
        lblSubject.setForeground(UITheme.TEXT_BODY);

        cmbFinalSubjectFilter = new JComboBox<>();
        cmbFinalSubjectFilter.setFont(UITheme.FONT_BODY);
        cmbFinalSubjectFilter.setPreferredSize(new Dimension(300, 34));
        populateCourseCombo(cmbFinalSubjectFilter);
        cmbFinalSubjectFilter.addActionListener(e -> loadFinalMarks());

        btnLoadFinalMarks = new JButton("Refresh / View Final Marks");
        btnLoadFinalMarks.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLoadFinalMarks.setBackground(new Color(13, 148, 136)); // Teal
        btnLoadFinalMarks.setForeground(Color.WHITE);
        btnLoadFinalMarks.setFocusPainted(false);
        btnLoadFinalMarks.setOpaque(true);
        btnLoadFinalMarks.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLoadFinalMarks.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(13, 148, 136), 1),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btnLoadFinalMarks.addActionListener(e -> loadFinalMarks());

        filterBar.add(lblSubject);
        filterBar.add(cmbFinalSubjectFilter);
        filterBar.add(btnLoadFinalMarks);

        finalMarksTableModel = new DefaultTableModel(new String[]{
                "Student ID", "CA Marks (40%)", "Final Exam (60%)", "Overall Final Score", "Exam Eligibility", "Final Grade"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        finalMarksTable = new JTable(finalMarksTableModel);
        UITheme.styleTable(finalMarksTable);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < finalMarksTable.getColumnCount(); i++) {
            finalMarksTable.getColumnModel().getColumn(i).setCellRenderer(centerRender);
        }

        panel.add(filterBar, BorderLayout.NORTH);
        panel.add(UITheme.createModernScrollPane(finalMarksTable), BorderLayout.CENTER);

        return panel;
    }

    private void populateCourseCombo(JComboBox<String> combo) {
        combo.removeAllItems();
        if (courseList != null && !courseList.isEmpty()) {
            for (Course c : courseList) {
                String code = (c.getCourseCode() != null && !c.getCourseCode().trim().isEmpty()) 
                        ? c.getCourseCode().trim() : ("CRS" + c.getCourseId());
                String name = (c.getCourseName() != null && !c.getCourseName().trim().isEmpty()) 
                        ? c.getCourseName().trim() : "Academic Module";
                combo.addItem(c.getCourseId() + " - " + code + " (" + name + ")");
            }
        } else {
            combo.addItem("1 - ICT2112 (Data Structures)");
            combo.addItem("2 - ICT2123 (Database Management)");
            combo.addItem("3 - ICT2132 (OOP Practicum)");
            combo.addItem("4 - BST2113 (Bioprocess Technology)");
            combo.addItem("5 - ENG2113 (Engineering Mechanics)");
        }
    }

    private int getSelectedCourseId(JComboBox<String> combo) {
        String item = (String) combo.getSelectedItem();
        if (item != null && item.contains(" - ")) {
            try {
                return Integer.parseInt(item.split(" - ")[0].trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return 1;
    }

    private void loadStudentMarks() {
        String studentId = txtSearchStudent.getText().trim();
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID to search.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            List<Mark> list = markService.getMarksForStudent(studentId);
            tableModel.setRowCount(0);
            for (Mark m : list) {
                String evalName = getEvaluationName(m.getEvaluationId());
                tableModel.addRow(new Object[]{m.getStudentId(), m.getEvaluationId(), evalName, m.getMarksObtained()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load marks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEvaluationMarks() {
        String evalStr = txtSearchEval.getText().trim();
        if (evalStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Evaluation ID.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            int evalId = Integer.parseInt(evalStr);
            List<Mark> list = markService.getMarksForEvaluation(evalId);
            tableModel.setRowCount(0);
            for (Mark m : list) {
                String evalName = getEvaluationName(m.getEvaluationId());
                tableModel.addRow(new Object[]{m.getStudentId(), m.getEvaluationId(), evalName, m.getMarksObtained()});
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Evaluation ID must be an integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load marks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSubjectMarks() {
        int courseId = getSelectedCourseId(cmbSubjectFilter);
        try {
            List<Mark> list = markService.getMarksForCourse(courseId);
            subjectTableModel.setRowCount(0);

            if (list.isEmpty()) {
                for (int sid = 11; sid <= 15; sid++) {
                    subjectTableModel.addRow(new Object[]{
                            String.valueOf(sid), "-", "-", "-", "-", "0.0%", "Not Eligible", "-"
                    });
                }
            } else {
                for (Mark m : list) {
                    double ca = m.computeCaMarks();
                    boolean eligible = markService.isEligible(ca);
                    subjectTableModel.addRow(new Object[]{
                            m.getStudentId(),
                            formatMark(m.getQuizMarks()),
                            formatMark(m.getAssignmentMarks()),
                            formatMark(m.getMidSemMarks()),
                            formatMark(m.getPracticalMarks()),
                            String.format("%.1f%%", ca),
                            eligible ? "Eligible" : "Not Eligible",
                            formatMark(m.getFinalExamMarks())
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load subject marks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFinalMarks() {
        int courseId = getSelectedCourseId(cmbFinalSubjectFilter);
        try {
            List<Mark> list = markService.getMarksForCourse(courseId);
            finalMarksTableModel.setRowCount(0);

            if (list.isEmpty()) {
                for (int sid = 11; sid <= 15; sid++) {
                    finalMarksTableModel.addRow(new Object[]{
                            String.valueOf(sid), "-", "-", "-", "Not Eligible", "NE"
                    });
                }
            } else {
                for (Mark m : list) {
                    double ca = m.computeCaMarks();
                    boolean eligible = markService.isEligible(ca);
                    double finalExam = m.getFinalExamOrZero();
                    double finalScore = eligible ? markService.calculateFinalMark(ca, finalExam) : 0.0;
                    String grade = eligible ? calculateGradeLetter(finalScore) : "NE";

                    finalMarksTableModel.addRow(new Object[]{
                            m.getStudentId(),
                            String.format("%.1f", ca),
                            String.format("%.1f", finalExam),
                            String.format("%.1f", finalScore),
                            eligible ? "Eligible" : "Not Eligible",
                            grade
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load final marks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatMark(Double d) {
        return d == null ? "-" : String.format("%.1f", d);
    }

    private String calculateGradeLetter(double total) {
        if (total >= 85) return "A+";
        if (total >= 75) return "A";
        if (total >= 70) return "A-";
        if (total >= 65) return "B+";
        if (total >= 60) return "B";
        if (total >= 55) return "B-";
        if (total >= 50) return "C+";
        if (total >= 45) return "C";
        if (total >= 40) return "C-";
        if (total >= 30) return "D";
        return "E";
    }

    private String getEvaluationName(int evalId) {
        if (evalId >= 1 && evalId <= MarkEntryDialog.EVAL_TYPES.length) {
            return MarkEntryDialog.EVAL_TYPES[evalId - 1].split(" \\(")[0];
        }
        return "Evaluation #" + evalId;
=======
        markTable.setRowHeight(42);
        markTable.setShowVerticalLines(false);
        markTable.setGridColor(new Color(243, 244, 246));
        markTable.setSelectionBackground(new Color(254, 249, 235));
        markTable.setSelectionForeground(COLOR_TEXT_DARK);
        markTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        markTable.setFont(FONT_BODY);

        // Header Styling
        JTableHeader th = markTable.getTableHeader();
        th.setFont(FONT_SMALL_BOLD);
        th.setForeground(COLOR_TEXT_MUTED);
        th.setBackground(Color.WHITE);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Renderers
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(JLabel.LEFT);
        centerRender.setFont(FONT_BODY);

        markTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        markTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setFont(FONT_BODY_BOLD);
                setForeground(COLOR_TEXT_DARK);
                setBorder(new EmptyBorder(0, 12, 0, 0));
                return c;
            }
        });

        markTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        markTable.getColumnModel().getColumn(1).setCellRenderer(centerRender);

        markTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        markTable.getColumnModel().getColumn(2).setCellRenderer(centerRender);

        markTable.getColumnModel().getColumn(3).setPreferredWidth(140);
        markTable.getColumnModel().getColumn(3).setCellRenderer(new ActionsCellRenderer());

        // Hide raw mark data column
        markTable.getColumnModel().getColumn(4).setMinWidth(0);
        markTable.getColumnModel().getColumn(4).setMaxWidth(0);
        markTable.getColumnModel().getColumn(4).setPreferredWidth(0);

        // Actions click listener
        markTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = markTable.columnAtPoint(e.getPoint());
                int row = markTable.rowAtPoint(e.getPoint());
                if (row >= 0 && col == 3) {
                    int modelRow = markTable.convertRowIndexToModel(row);
                    Mark m = (Mark) tableModel.getValueAt(modelRow, 4);
                    if (m == null) return;

                    Rectangle cellRect = markTable.getCellRect(row, col, false);
                    int clickX = e.getX() - cellRect.x;

                    if (clickX < 55) {
                        openEditDialogForMark(m);
                    } else {
                        deleteSelectedMark(m);
                    }
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

        // Footer Summary
        footerSummaryLabel = new JLabel("Showing 0 of 0 records · Rows offer Edit and Delete");
        footerSummaryLabel.setFont(FONT_SMALL);
        footerSummaryLabel.setForeground(COLOR_TEXT_MUTED);
        footerSummaryLabel.setBorder(new EmptyBorder(4, 4, 4, 4));

        tableContainer.add(filterBar, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        tableContainer.add(footerSummaryLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
    }

    public void loadAllMarks() {
        tableModel.setRowCount(0);
        try {
            // Load sample/database marks
            List<Mark> list = markService.getMarksForStudent("11");
            if (list == null || list.isEmpty()) {
                list = markService.getMarksForStudent("1");
            }

            if (list != null) {
                for (Mark m : list) {
                    tableModel.addRow(new Object[]{
                            m.getStudentId(),
                            String.valueOf(m.getEvaluationId()),
                            String.valueOf(m.getMarksObtained()),
                            "Edit   Delete",
                            m
                    });
                }
            }
        } catch (Exception ignored) {
        }
        updateFooterCount();
    }

    private void applyFilters() {
        String sStudent = searchStudentField.getText().trim().toLowerCase();
        String sEval = searchEvalField.getText().trim().toLowerCase();

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                if (!sStudent.isEmpty()) {
                    String std = entry.getStringValue(0).toLowerCase();
                    if (!std.contains(sStudent)) return false;
                }
                if (!sEval.isEmpty()) {
                    String ev = entry.getStringValue(1).toLowerCase();
                    if (!ev.contains(sEval)) return false;
                }
                return true;
            }
        };

        rowSorter.setRowFilter(rf);
        updateFooterCount();
    }

    private void updateFooterCount() {
        int count = markTable.getRowCount();
        footerSummaryLabel.setText("Showing " + count + " of " + count + " records · Rows offer Edit and Delete");
>>>>>>> Aysha
    }

    private void openAddDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        MarkEntryDialog dialog = new MarkEntryDialog(parentFrame, "", "", "");
        dialog.setVisible(true);

        if (dialog.isSuccessful()) {
<<<<<<< HEAD
            if (!txtSearchStudent.getText().trim().isEmpty()) {
                loadStudentMarks();
            }
        }
    }

    private void openEditDialog() {
        int selectedRow = markTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a mark row to edit.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = tableModel.getValueAt(selectedRow, 0).toString();
        String evalId = tableModel.getValueAt(selectedRow, 1).toString();
        String currentMarks = tableModel.getValueAt(selectedRow, 3).toString();

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        MarkEntryDialog dialog = new MarkEntryDialog(parentFrame, studentId, evalId, currentMarks);
        dialog.setVisible(true);

        if (dialog.isSuccessful()) {
            loadStudentMarks();
        }
    }

    private void deleteSelectedMark() {
        int selectedRow = markTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = tableModel.getValueAt(selectedRow, 0).toString();
        int evalId = Integer.parseInt(tableModel.getValueAt(selectedRow, 1).toString());

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete mark for Student: " + studentId + " (Eval ID: " + evalId + ")?",
=======
            loadAllMarks();
        }
    }

    private void openEditDialogForMark(Mark m) {
        if (m == null) return;
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        MarkEntryDialog dialog = new MarkEntryDialog(parentFrame, m.getStudentId(), String.valueOf(m.getEvaluationId()), String.valueOf(m.getMarksObtained()));
        dialog.setVisible(true);

        if (dialog.isSuccessful()) {
            loadAllMarks();
        }
    }

    private void deleteSelectedMark(Mark m) {
        if (m == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete mark for Student: " + m.getStudentId() + " (Eval ID: " + m.getEvaluationId() + ")?",
>>>>>>> Aysha
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
<<<<<<< HEAD
                if (markService.removeMark(studentId, evalId)) {
                    JOptionPane.showMessageDialog(this, "Mark deleted successfully.");
                    loadStudentMarks();
=======
                if (markService.removeMark(m.getStudentId(), m.getEvaluationId())) {
                    loadAllMarks();
                    JOptionPane.showMessageDialog(this, "Mark deleted successfully.");
>>>>>>> Aysha
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting mark: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
<<<<<<< HEAD
}
=======

    private static class ActionsCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JLabel editLink = new JLabel("Edit");
        private final JLabel deleteLink = new JLabel("Delete");

        public ActionsCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
            setOpaque(true);

            editLink.setFont(FONT_BODY_BOLD);
            editLink.setForeground(COLOR_BURGUNDY);
            editLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

            deleteLink.setFont(FONT_BODY_BOLD);
            deleteLink.setForeground(new Color(220, 38, 38));
            deleteLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

            add(editLink);
            add(deleteLink);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? new Color(254, 249, 235) : Color.WHITE);
            return this;
        }
    }
}
>>>>>>> Aysha
