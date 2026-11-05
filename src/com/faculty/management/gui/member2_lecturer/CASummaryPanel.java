package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.dao.CourseDAO;
import com.faculty.management.dao.UserDAO;
import com.faculty.management.model.Course;
import com.faculty.management.model.Eligibility;
import com.faculty.management.model.Mark;
import com.faculty.management.model.User;
import com.faculty.management.service.EligibilityService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.faculty.management.gui.member2_lecturer.LecturerUIComponents.*;

/**
 * CA Eligibility Calculation Panel for Member 2.
 * Strictly implements Requirement 5:
 * - Calculate whether CA >= 40%
 * - Displays: Eligible / Not Eligible
 * - Fetches real marks from the database and saves calculated eligibility
 */
public class CASummaryPanel extends JPanel {

    private final EligibilityService eligibilityService = new EligibilityService();
    private final CourseDAO courseDAO = new CourseDAO();
    private final UserDAO userDAO = new UserDAO();
    private List<Course> allCourses = new ArrayList<>();

    // Inputs
    private SearchTextField txtStudentId;
    private JComboBox<String> cmbCourse;
    private SearchTextField txtMaxCAMarks;
    private BurgundyButton btnCalculate;

    // Table
    private JTable marksTable;
    private DefaultTableModel tableModel;
    private JLabel footerCountLabel;

    // Right Inspector Card components
    private JLabel valTotalMarks;
    private JLabel valCAPercentage;
    private JLabel valRule;
    private PillBadge badgeStatus;

    public CASummaryPanel() {
        initComponents();
        txtStudentId.setText("11");
        // Pre-select CS203 (Course ID 6) if available
        for (int i = 0; i < cmbCourse.getItemCount(); i++) {
            String item = cmbCourse.getItemAt(i);
            if (item != null && item.startsWith("CS203")) {
                cmbCourse.setSelectedIndex(i);
                break;
            }
        }
        processCACalculation();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(COLOR_PAGE_BG);
        setBorder(new EmptyBorder(0, 28, 20, 28));

        // 1. Header: Title & Subtitle
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("CA Eligibility");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(COLOR_TEXT_DARK);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pageTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel pageSub = new JLabel("Calculate CA percentage and exam eligibility for a student.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);
        pageSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        pageSub.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        headerPanel.add(titleBox, BorderLayout.CENTER);

        // 2. Main Two-Column Content: Left (Inputs + Table), Right (Eligibility Result Card)
        JPanel mainContent = new JPanel(new BorderLayout(24, 0));
        mainContent.setOpaque(false);

        JPanel leftPanel = createLeftPanel();
        JPanel rightCard = createResultCard();

        mainContent.add(leftPanel, BorderLayout.CENTER);
        mainContent.add(rightCard, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
    }

    private void loadCourses() {
        cmbCourse.removeAllItems();
        try {
            allCourses = courseDAO.getAllCourses();
            for (Course c : allCourses) {
                cmbCourse.addItem(c.getCourseCode() + " - " + c.getCourseName());
            }
        } catch (Exception e) {
            allCourses = new ArrayList<>();
        }
    }

    private Course resolveCourse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        String trimmed = input.trim();

        // 1. Check if item has "CODE - NAME" pattern
        if (trimmed.contains(" - ")) {
            String code = trimmed.split(" - ")[0].trim();
            for (Course c : allCourses) {
                if (c.getCourseCode().equalsIgnoreCase(code)) {
                    return c;
                }
            }
        }

        // 2. Check exact course code match (case-insensitive)
        for (Course c : allCourses) {
            if (c.getCourseCode().equalsIgnoreCase(trimmed)) {
                return c;
            }
        }

        // 3. Check integer course_id match in loaded courses
        try {
            int id = Integer.parseInt(trimmed);
            for (Course c : allCourses) {
                if (c.getCourseId() == id) {
                    return c;
                }
            }
        } catch (NumberFormatException ignored) {}

        // 4. Fallback search by code via DAO
        try {
            Course c = courseDAO.findByCode(trimmed);
            if (c != null) return c;
        } catch (Exception ignored) {}

        // 5. Fallback search by ID via DAO
        try {
            int id = Integer.parseInt(trimmed);
            Course c = courseDAO.findById(id);
            if (c != null) return c;
        } catch (Exception ignored) {}

        return null;
    }

    private User resolveStudent(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        String trimmed = input.trim();
        try {
            int id = Integer.parseInt(trimmed);
            try {
                User u = userDAO.findById(id);
                if (u != null) return u;
            } catch (Exception ignored) {}
        } catch (NumberFormatException ignored) {}

        try {
            User u = userDAO.findByUsername(trimmed);
            if (u != null) return u;
        } catch (Exception ignored) {}

        return null;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        // Input Controls Row
        JPanel inputBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        inputBar.setOpaque(false);
        inputBar.setPreferredSize(new Dimension(0, 40));

        txtStudentId = new SearchTextField("Student ID", 10);
        txtStudentId.setPreferredSize(new Dimension(105, 36));

        cmbCourse = new JComboBox<>();
        cmbCourse.setEditable(true);
        styleComboBox(cmbCourse);
        cmbCourse.setPreferredSize(new Dimension(185, 36));
        Component editor = cmbCourse.getEditor().getEditorComponent();
        if (editor instanceof JTextField) {
            JTextField tf = (JTextField) editor;
            tf.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
            tf.setBackground(Color.WHITE);
            tf.setFont(FONT_BODY);
        }
        loadCourses();

        txtMaxCAMarks = new SearchTextField("100", 5);
        txtMaxCAMarks.setText("100");
        txtMaxCAMarks.setPreferredSize(new Dimension(65, 36));

        btnCalculate = new BurgundyButton("Calculate");
        btnCalculate.setPreferredSize(new Dimension(100, 36));
        btnCalculate.addActionListener(e -> processCACalculation());

        inputBar.add(txtStudentId);
        inputBar.add(cmbCourse);
        inputBar.add(txtMaxCAMarks);
        inputBar.add(btnCalculate);

        // Table
        String[] columns = {"EVALUATION ID", "MARKS OBTAINED"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        marksTable = new JTable(tableModel);
        marksTable.setRowHeight(40);
        marksTable.setShowVerticalLines(false);
        marksTable.setGridColor(new Color(243, 244, 246));
        marksTable.setSelectionBackground(new Color(254, 249, 235));
        marksTable.setSelectionForeground(COLOR_TEXT_DARK);
        marksTable.setFont(FONT_BODY);

        JTableHeader th = marksTable.getTableHeader();
        th.setFont(FONT_SMALL_BOLD);
        th.setForeground(COLOR_TEXT_MUTED);
        th.setBackground(Color.WHITE);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        DefaultTableCellRenderer leftRender = new DefaultTableCellRenderer();
        leftRender.setBorder(new EmptyBorder(0, 12, 0, 0));
        marksTable.getColumnModel().getColumn(0).setPreferredWidth(320);
        marksTable.getColumnModel().getColumn(0).setCellRenderer(leftRender);

        marksTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        marksTable.getColumnModel().getColumn(1).setCellRenderer(leftRender);

        JScrollPane scrollPane = new JScrollPane(marksTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));

        // Footer Summary
        footerCountLabel = new JLabel("Showing 0 of 0 evaluations");
        footerCountLabel.setFont(FONT_SMALL);
        footerCountLabel.setForeground(COLOR_TEXT_MUTED);
        footerCountLabel.setBorder(new EmptyBorder(4, 4, 4, 4));

        panel.add(inputBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(footerCountLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createResultCard() {
        CardPanel card = new CardPanel();
        card.setPreferredSize(new Dimension(300, 480));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel contentBox = new JPanel();
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setOpaque(false);

        JLabel cardTitle = new JLabel("Eligibility result");
        cardTitle.setFont(FONT_SECTION_TITLE);
        cardTitle.setForeground(COLOR_TEXT_DARK);
        cardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentBox.add(cardTitle);
        contentBox.add(Box.createVerticalStrut(20));

        // Grid
        JPanel infoGrid = new JPanel(new GridLayout(4, 2, 8, 14));
        infoGrid.setOpaque(false);
        infoGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoGrid.add(createDetailLabel("Total CA marks"));
        valTotalMarks = createDetailValLabel("—");
        infoGrid.add(valTotalMarks);

        infoGrid.add(createDetailLabel("CA percentage"));
        valCAPercentage = createDetailValLabel("— %");
        infoGrid.add(valCAPercentage);

        infoGrid.add(createDetailLabel("Rule"));
        valRule = createDetailValLabel("CA ≥ 40%");
        infoGrid.add(valRule);

        infoGrid.add(createDetailLabel("Status"));
        badgeStatus = new PillBadge("Pending", new Color(254, 243, 199), new Color(180, 83, 9));
        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(badgeStatus);
        infoGrid.add(badgeWrap);

        contentBox.add(infoGrid);
        contentBox.add(Box.createVerticalGlue());

        JLabel noteLabel = new JLabel("Max CA marks defaults to 100.");
        noteLabel.setFont(FONT_SMALL);
        noteLabel.setForeground(COLOR_TEXT_MUTED);
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentBox.add(noteLabel);

        card.add(contentBox, BorderLayout.CENTER);
        return card;
    }

    private JLabel createDetailLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(COLOR_TEXT_MUTED);
        return l;
    }

    private JLabel createDetailValLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY_BOLD);
        l.setForeground(COLOR_TEXT_DARK);
        return l;
    }

    private void processCACalculation() {
        String studentIdStr = txtStudentId.getText().trim();
        Object selectedCourseObj = cmbCourse.getSelectedItem();
        String courseIdStr = (selectedCourseObj != null) ? selectedCourseObj.toString().trim() : "";
        String maxStr = txtMaxCAMarks.getText().trim();

        if (studentIdStr.isEmpty() || courseIdStr.isEmpty()) {
            return;
        }

        try {
            User student = resolveStudent(studentIdStr);
            if (student == null) {
                JOptionPane.showMessageDialog(this,
                        "Student '" + studentIdStr + "' not found in database.\nPlease enter a valid Student ID or Username (e.g., 11, 30, tg2021001, Rashkan).",
                        "Invalid Student",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Course course = resolveCourse(courseIdStr);
            if (course == null) {
                JOptionPane.showMessageDialog(this,
                        "Course '" + courseIdStr + "' not found in database.\nPlease select a valid course from the list (e.g., CS203, ICT2112).",
                        "Invalid Course",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int studentId = student.getUserId();
            int courseId = course.getCourseId();

            double maxMarks = 100.0;
            if (!maxStr.isEmpty()) {
                try {
                    maxMarks = Double.parseDouble(maxStr);
                    if (maxMarks <= 0) maxMarks = 100.0;
                } catch (NumberFormatException ignored) {
                    maxMarks = 100.0;
                }
            }

            List<Mark> studentMarks = eligibilityService.getStudentMarks(String.valueOf(studentId), String.valueOf(courseId));
            tableModel.setRowCount(0);

            if (studentMarks == null || studentMarks.isEmpty()) {
                footerCountLabel.setText("Showing 0 of 0 evaluations");
                valTotalMarks.setText("0.0");
                valCAPercentage.setText("0.0%");
                badgeStatus.setText("No Marks");
                badgeStatus.setColors(new Color(243, 244, 246), new Color(107, 114, 128));
                JOptionPane.showMessageDialog(this,
                        "No marks recorded for student " + student.getFullName() + " (" + student.getUsername() + ")\nin course " + course.getCourseCode() + " - " + course.getCourseName() + ".\n\nPlease enter marks in the Marks Entry tab first.",
                        "No Marks Found",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            double totalObtained = 0.0;
            double totalMaxMarks = 0.0;

            for (Mark m : studentMarks) {
                String evalLabel = "Eval #" + m.getEvaluationId();
                if (m.getEvaluationTitle() != null && !m.getEvaluationTitle().trim().isEmpty()) {
                    evalLabel += " (" + m.getEvaluationTitle().trim() + ")";
                } else if (m.getEvaluationType() != null && !m.getEvaluationType().trim().isEmpty()) {
                    evalLabel += " (" + m.getEvaluationType().trim() + ")";
                }
                tableModel.addRow(new Object[]{
                        evalLabel,
                        String.format("%.1f", m.getMarksObtained())
                });
                totalObtained += m.getMarksObtained();
                double mx = m.getMaxMarks() > 0 ? m.getMaxMarks() : 100.0;
                totalMaxMarks += mx;
            }

            int count = tableModel.getRowCount();
            footerCountLabel.setText("Showing " + count + " of " + count + " evaluations");

            double caPercentage = (totalMaxMarks > 0) ? ((totalObtained / totalMaxMarks) * 100.0) : 0.0;
            boolean isEligible = caPercentage >= 40.0;

            valTotalMarks.setText(String.format("%.1f", totalObtained));
            valCAPercentage.setText(String.format("%.1f%%", caPercentage));

            if (isEligible) {
                badgeStatus.setText("Eligible");
                badgeStatus.setColors(new Color(220, 252, 231), new Color(21, 128, 61));
            } else {
                badgeStatus.setText("Not Eligible");
                badgeStatus.setColors(new Color(254, 226, 226), new Color(185, 28, 28));
            }

            // Save or update calculated eligibility in database table eligibility
            eligibilityService.calculateAndSaveEligibility(studentId, courseId, caPercentage, 100.0);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Calculation error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
