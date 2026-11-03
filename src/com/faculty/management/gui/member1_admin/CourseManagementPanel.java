package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Course;

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
import java.util.ArrayList;
import java.util.List;

import static com.faculty.management.gui.member1_admin.AdminUIComponents.*;

/**
 * Course Management Panel for Member 1 (Administrator).
 * Strictly implements the UI/UX design from Page 3 of the FMS Specification.
 * Features semester group banners ("Semester 1  4 courses · 10 credits", etc.).
 *
 * Demonstrates Core OOP Principles:
 * - Classes & Objects: Encapsulation of course entity management, table models, and controllers.
 * - Inheritance: Extends JPanel to integrate into Swing container hierarchy.
 * - Abstraction: Database operations abstracted through AdminController and CourseService.
 * - Polymorphism: Custom TableCellRenderers for section headers, course data, and action links.
 * - Encapsulation: Private member variables with controlled public accessors.
 * - Error & Exception Handling: Structured handling of DatabaseException.
 * - Database Handling: Direct backend persistence to MySQL.
 */
public class CourseManagementPanel extends JPanel {

    private final AdminController adminController;
    private List<Course> allCourses = new ArrayList<>();
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTable courseTable;

    // Filters & summary
    private SearchTextField searchField;
    private JComboBox<String> semesterFilterBox;
    private JLabel totalSummaryLabel;

    public CourseManagementPanel() {
        this(new AdminController());
    }

    public CourseManagementPanel(AdminController adminController) {
        this.adminController = (adminController != null) ? adminController : new AdminController();
        initComponents();
        loadCourseTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(COLOR_PAGE_BG);
        setBorder(new EmptyBorder(0, 28, 20, 28));

        // 1. Header: Title, Subtitle, and Top Action Buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("Course Management");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(COLOR_TEXT_DARK);

        JLabel pageSub = new JLabel("Register and maintain academic course modules.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBtns.setOpaque(false);

        WhiteOutlineButton assignInfoBtn = new WhiteOutlineButton("Assign course info");
        assignInfoBtn.setPreferredSize(new Dimension(150, 38));
        assignInfoBtn.addActionListener(e -> handleAssignCourseInfoAction());

        BurgundyButton addCourseBtn = new BurgundyButton("+ Add course");
        addCourseBtn.setPreferredSize(new Dimension(135, 38));
        addCourseBtn.addActionListener(e -> handleAddCourseAction());

        actionBtns.add(assignInfoBtn);
        actionBtns.add(addCourseBtn);

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(actionBtns, BorderLayout.EAST);

        // 2. Filter Toolbar
        JPanel filterBar = new JPanel(new BorderLayout(10, 0));
        filterBar.setOpaque(false);

        searchField = new SearchTextField("Search by code or name", 24);
        searchField.setPreferredSize(new Dimension(280, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        String[] semesters = {"All semesters", "Semester 1", "Semester 2"};
        semesterFilterBox = new JComboBox<>(semesters);
        styleComboBox(semesterFilterBox);
        semesterFilterBox.setPreferredSize(new Dimension(140, 36));
        semesterFilterBox.addActionListener(e -> applyFilters());

        WhiteOutlineButton refreshBtn = createRefreshButton();
        refreshBtn.setPreferredSize(new Dimension(100, 36));
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            semesterFilterBox.setSelectedIndex(0);
            loadCourseTable();
        });

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftFilters.setOpaque(false);
        leftFilters.add(searchField);
        leftFilters.add(semesterFilterBox);

        filterBar.add(leftFilters, BorderLayout.WEST);
        filterBar.add(refreshBtn, BorderLayout.EAST);

        // 3. Table Container
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setOpaque(false);

        String[] columns = {"CODE", "COURSE NAME", "CREDITS", "THEORY HRS", "PRACTICAL HRS", "DEPT", "ACTIONS", "_DATA_"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        courseTable = new JTable(tableModel);
        courseTable.setRowHeight(42);
        courseTable.setShowVerticalLines(false);
        courseTable.setGridColor(new Color(243, 244, 246));
        courseTable.setSelectionBackground(new Color(254, 249, 235));
        courseTable.setSelectionForeground(COLOR_TEXT_DARK);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.setFont(FONT_BODY);

        // Header Styling
        JTableHeader th = courseTable.getTableHeader();
        th.setFont(FONT_SMALL_BOLD);
        th.setForeground(COLOR_TEXT_MUTED);
        th.setBackground(Color.WHITE);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Set column widths & renderers
        courseTable.getColumnModel().getColumn(0).setPreferredWidth(230); // CODE / Group Banner
        courseTable.getColumnModel().getColumn(0).setCellRenderer(new CourseCodeCellRenderer());

        courseTable.getColumnModel().getColumn(1).setPreferredWidth(230); // COURSE NAME
        courseTable.getColumnModel().getColumn(1).setCellRenderer(new CourseTextCellRenderer(JLabel.LEFT));

        courseTable.getColumnModel().getColumn(2).setPreferredWidth(75); // CREDITS
        courseTable.getColumnModel().getColumn(2).setCellRenderer(new CourseTextCellRenderer(JLabel.CENTER));

        courseTable.getColumnModel().getColumn(3).setPreferredWidth(90); // THEORY HRS
        courseTable.getColumnModel().getColumn(3).setCellRenderer(new CourseTextCellRenderer(JLabel.CENTER));

        courseTable.getColumnModel().getColumn(4).setPreferredWidth(105); // PRACTICAL HRS
        courseTable.getColumnModel().getColumn(4).setCellRenderer(new CourseTextCellRenderer(JLabel.CENTER));

        courseTable.getColumnModel().getColumn(5).setPreferredWidth(70); // DEPT
        courseTable.getColumnModel().getColumn(5).setCellRenderer(new CourseTextCellRenderer(JLabel.CENTER));

        courseTable.getColumnModel().getColumn(6).setPreferredWidth(130); // ACTIONS
        courseTable.getColumnModel().getColumn(6).setCellRenderer(new CourseActionsCellRenderer());

        // Hide internal course reference column
        courseTable.getColumnModel().getColumn(7).setMinWidth(0);
        courseTable.getColumnModel().getColumn(7).setMaxWidth(0);
        courseTable.getColumnModel().getColumn(7).setPreferredWidth(0);

        // Click listener for Edit and Delete action links in column 6
        courseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = courseTable.columnAtPoint(e.getPoint());
                int row = courseTable.rowAtPoint(e.getPoint());
                if (row >= 0 && col == 6) {
                    int modelRow = courseTable.convertRowIndexToModel(row);
                    Course c = (Course) tableModel.getValueAt(modelRow, 7);
                    if (c == null) return; // Ignore clicks on banner rows

                    Rectangle cellRect = courseTable.getCellRect(row, col, false);
                    int clickX = e.getX() - cellRect.x;

                    // Edit is roughly in the first half (0 to 55px), Delete in the second half
                    if (clickX < 55) {
                        handleEditCourse(c);
                    } else {
                        handleDeleteCourse(c);
                    }
                }
            }
        });

        // Row Sorter
        rowSorter = new TableRowSorter<>(tableModel);
        courseTable.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));

        // Footer summary label (e.g. "9 courses · 25 credits in total")
        totalSummaryLabel = new JLabel("0 courses · 0 credits in total");
        totalSummaryLabel.setFont(FONT_SMALL);
        totalSummaryLabel.setForeground(COLOR_TEXT_MUTED);
        totalSummaryLabel.setBorder(new EmptyBorder(4, 4, 4, 4));

        tableContainer.add(filterBar, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        tableContainer.add(totalSummaryLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
    }

    /**
     * Loads courses from database and organizes them with Semester 1 & Semester 2 group banners.
     */
    public void loadCourseTable() {
        try {
            allCourses = adminController.loadAllCourses();
            tableModel.setRowCount(0);

            if (allCourses == null) allCourses = new ArrayList<>();

            List<Course> sem1 = new ArrayList<>();
            List<Course> sem2 = new ArrayList<>();

            for (Course c : allCourses) {
                String sem = c.getSemester() != null ? c.getSemester().trim() : "Semester 1";
                if (sem.toLowerCase().contains("2")) {
                    sem2.add(c);
                } else {
                    sem1.add(c);
                }
            }

            // Populate Semester 1 Group
            if (!sem1.isEmpty()) {
                int sem1Creds = 0;
                for (Course c : sem1) sem1Creds += c.getCreditValue();
                String banner1 = "Semester 1  " + sem1.size() + " courses · " + sem1Creds + " credits";
                tableModel.addRow(new Object[]{banner1, "", "", "", "", "", "", null});

                for (Course c : sem1) {
                    addCourseRow(c);
                }
            }

            // Populate Semester 2 Group
            if (!sem2.isEmpty()) {
                int sem2Creds = 0;
                for (Course c : sem2) sem2Creds += c.getCreditValue();
                String banner2 = "Semester 2  " + sem2.size() + " courses · " + sem2Creds + " credits";
                tableModel.addRow(new Object[]{banner2, "", "", "", "", "", "", null});

                for (Course c : sem2) {
                    addCourseRow(c);
                }
            }

            applyFilters();
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load courses from database: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCourseRow(Course c) {
        int creds = c.getCreditValue();
        String theoryStr = c.getTheoryHours() > 0 ? String.valueOf(c.getTheoryHours()) : "—";
        String practicalStr = c.getPracticalHours() > 0 ? String.valueOf(c.getPracticalHours()) : "—";
        String dept = (c.getDepartment() != null && !c.getDepartment().isEmpty()) ? c.getDepartment() : "ICT";

        tableModel.addRow(new Object[]{
                c.getCourseCode(),
                c.getCourseName(),
                String.valueOf(creds),
                theoryStr,
                practicalStr,
                dept,
                "Edit   Delete",
                c
        });
    }

    /**
     * Filters courses according to search text and semester selection.
     */
    private void applyFilters() {
        String query = searchField.getText().trim().toLowerCase();
        String selectedSem = (String) semesterFilterBox.getSelectedItem();

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                Course c = (Course) entry.getValue(7);

                // For group banners (where course object is null)
                if (c == null) {
                    String bannerText = entry.getStringValue(0);
                    if (selectedSem != null && !selectedSem.equals("All semesters")) {
                        if (selectedSem.equalsIgnoreCase("Semester 1") && bannerText.startsWith("Semester 2")) return false;
                        if (selectedSem.equalsIgnoreCase("Semester 2") && bannerText.startsWith("Semester 1")) return false;
                    }
                    return query.isEmpty();
                }

                // Semester filter check
                if (selectedSem != null && !selectedSem.equals("All semesters")) {
                    String sem = c.getSemester() != null ? c.getSemester() : "Semester 1";
                    if (!sem.equalsIgnoreCase(selectedSem)) {
                        return false;
                    }
                }

                // Query search check (matches course code or course name)
                if (!query.isEmpty()) {
                    boolean codeMatch = c.getCourseCode() != null && c.getCourseCode().toLowerCase().contains(query);
                    boolean nameMatch = c.getCourseName() != null && c.getCourseName().toLowerCase().contains(query);
                    if (!codeMatch && !nameMatch) {
                        return false;
                    }
                }
                return true;
            }
        };

        rowSorter.setRowFilter(rf);

        int visibleCourses = 0;
        int visibleCredits = 0;
        for (int i = 0; i < courseTable.getRowCount(); i++) {
            int modelRow = courseTable.convertRowIndexToModel(i);
            Course c = (Course) tableModel.getValueAt(modelRow, 7);
            if (c != null) {
                visibleCourses++;
                visibleCredits += c.getCreditValue();
            }
        }
        totalSummaryLabel.setText(visibleCourses + " courses · " + visibleCredits + " credits in total");
    }

    // =========================================================================
    // ACTION HANDLERS
    // =========================================================================

    private void handleAddCourseAction() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        CourseFormDialog dialog = new CourseFormDialog(parentFrame, adminController, CourseFormDialog.FormMode.ADD);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadCourseTable();
            JOptionPane.showMessageDialog(this, "Course registered successfully in the database!", "Course Added", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleEditCourse(Course course) {
        if (course == null) return;
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        CourseFormDialog dialog = new CourseFormDialog(parentFrame, adminController, CourseFormDialog.FormMode.UPDATE, course);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadCourseTable();
            JOptionPane.showMessageDialog(this, "Course updated successfully!", "Update Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleDeleteCourse(Course course) {
        if (course == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete course " + course.getCourseCode() + " (" + course.getCourseName() + ")?\nThis action cannot be undone!",
                "Confirm Delete Course",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                adminController.handleDeleteCourse(course.getCourseCode());
                loadCourseTable();
                JOptionPane.showMessageDialog(this, "Course deleted successfully.", "Course Deleted", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to delete course: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleAssignCourseInfoAction() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        CourseFormDialog dialog = new CourseFormDialog(parentFrame, adminController, CourseFormDialog.FormMode.ASSIGN_INFO);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadCourseTable();
            JOptionPane.showMessageDialog(this, "Course information assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // =========================================================================
    // TABLE CELL RENDERERS (Handles Group Banners & Rows)
    // =========================================================================

    private static final Color BANNER_BG = new Color(251, 243, 228);     // #FBF3E4 Warm Light Gold
    private static final Color BANNER_TEXT = new Color(120, 53, 15);      // #78350F Brown Gold

    private static class CourseCodeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            int modelRow = table.convertRowIndexToModel(row);
            Object dataObj = table.getModel().getValueAt(modelRow, 7);

            if (dataObj == null) {
                // Group Banner Header
                setBackground(BANNER_BG);
                setForeground(BANNER_TEXT);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setBorder(new EmptyBorder(0, 14, 0, 0));
            } else {
                setBackground(isSelected ? new Color(254, 249, 235) : Color.WHITE);
                setForeground(COLOR_TEXT_DARK);
                setFont(FONT_BODY_BOLD);
                setBorder(new EmptyBorder(0, 14, 0, 0));
            }
            return c;
        }
    }

    private static class CourseTextCellRenderer extends DefaultTableCellRenderer {
        public CourseTextCellRenderer(int alignment) {
            setHorizontalAlignment(alignment);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            int modelRow = table.convertRowIndexToModel(row);
            Object dataObj = table.getModel().getValueAt(modelRow, 7);

            if (dataObj == null) {
                setBackground(BANNER_BG);
                setText("");
            } else {
                setBackground(isSelected ? new Color(254, 249, 235) : Color.WHITE);
                setForeground(COLOR_TEXT_DARK);
                setFont(FONT_BODY);
            }
            return c;
        }
    }

    private static class CourseActionsCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JLabel editLink = new JLabel("Edit");
        private final JLabel deleteLink = new JLabel("Delete");

        public CourseActionsCellRenderer() {
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
            int modelRow = table.convertRowIndexToModel(row);
            Object dataObj = table.getModel().getValueAt(modelRow, 7);

            if (dataObj == null) {
                setBackground(BANNER_BG);
                editLink.setVisible(false);
                deleteLink.setVisible(false);
            } else {
                setBackground(isSelected ? new Color(254, 249, 235) : Color.WHITE);
                editLink.setVisible(true);
                deleteLink.setVisible(true);
            }
            return this;
        }
    }
}
