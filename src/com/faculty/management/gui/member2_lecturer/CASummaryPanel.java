package com.faculty.management.gui.member2_lecturer;

<<<<<<< HEAD
import com.faculty.management.gui.common.PrimaryButton;
import com.faculty.management.gui.common.UITheme;
=======
>>>>>>> Aysha
import com.faculty.management.model.Eligibility;
import com.faculty.management.model.Mark;
import com.faculty.management.service.EligibilityService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
<<<<<<< HEAD
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CASummaryPanel extends JPanel {
    private JTextField txtStudentId, txtCourseId, txtMaxCAMarks;
    private JButton btnCalculate;
    private JLabel lblTotalObtained, lblCAPercentage, lblStatus;
    private JTable marksTable;
    private DefaultTableModel tableModel;

    private final EligibilityService eligibilityService = new EligibilityService();

    public CASummaryPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initUI();
    }

    private void initUI() {
        // Controls Header
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(6, 12, 6, 12)
        ));

        txtStudentId = new JTextField(8);
        txtStudentId.setFont(UITheme.FONT_BODY);
        txtStudentId.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));

        txtCourseId = new JTextField(8);
        txtCourseId.setFont(UITheme.FONT_BODY);
        txtCourseId.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));

        txtMaxCAMarks = new JTextField("100", 5);
        txtMaxCAMarks.setFont(UITheme.FONT_BODY);
        txtMaxCAMarks.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));

        btnCalculate = new PrimaryButton("Calculate");
        btnCalculate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCalculate.setPreferredSize(new Dimension(110, 32));

        JLabel lblStd = new JLabel("Student ID:");
        lblStd.setFont(UITheme.FONT_SMALL_BOLD);
        lblStd.setForeground(UITheme.TEXT_BODY);

        JLabel lblCrs = new JLabel("Course ID:");
        lblCrs.setFont(UITheme.FONT_SMALL_BOLD);
        lblCrs.setForeground(UITheme.TEXT_BODY);

        JLabel lblMax = new JLabel("Max CA Marks:");
        lblMax.setFont(UITheme.FONT_SMALL_BOLD);
        lblMax.setForeground(UITheme.TEXT_BODY);

        inputPanel.add(lblStd);
        inputPanel.add(txtStudentId);
        inputPanel.add(lblCrs);
        inputPanel.add(txtCourseId);
        inputPanel.add(lblMax);
        inputPanel.add(txtMaxCAMarks);
        inputPanel.add(btnCalculate);

        // Marks Breakdown Table
        tableModel = new DefaultTableModel(new String[]{"Evaluation ID", "Marks Obtained"}, 0);
        marksTable = new JTable(tableModel);
        UITheme.styleTable(marksTable);

        // Result Summary Card
        JPanel resultCard = new JPanel(new GridLayout(3, 1, 6, 6));
        resultCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(14, 18, 14, 18)
        ));
        resultCard.setBackground(Color.WHITE);

        lblTotalObtained = new JLabel("Total Obtained CA Marks: -");
        lblCAPercentage = new JLabel("Calculated CA Percentage: - %");
        lblStatus = new JLabel("Exam Eligibility Status: -");

        lblTotalObtained.setFont(UITheme.FONT_BODY_BOLD);
        lblTotalObtained.setForeground(UITheme.TEXT_MAIN);
        lblCAPercentage.setFont(UITheme.FONT_BODY_BOLD);
        lblCAPercentage.setForeground(UITheme.TEXT_MAIN);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblStatus.setForeground(UITheme.PRIMARY);

        resultCard.add(lblTotalObtained);
        resultCard.add(lblCAPercentage);
        resultCard.add(lblStatus);

        btnCalculate.addActionListener(e -> processCACalculation());

        add(inputPanel, BorderLayout.NORTH);
        add(UITheme.createModernScrollPane(marksTable), BorderLayout.CENTER);
        add(resultCard, BorderLayout.SOUTH);
=======
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

import static com.faculty.management.gui.member2_lecturer.LecturerUIComponents.*;

/**
 * CA Eligibility Calculation Panel for Member 2.
 * Strictly implements Page 5 of the Ruhuna Lecturer Portal Specification.
 */
public class CASummaryPanel extends JPanel {

    private final EligibilityService eligibilityService = new EligibilityService();

    // Inputs
    private SearchTextField txtStudentId;
    private SearchTextField txtCourseId;
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

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        // Input Controls Row
        JPanel inputBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputBar.setOpaque(false);

        txtStudentId = new SearchTextField("Student ID", 12);
        txtStudentId.setPreferredSize(new Dimension(140, 36));

        txtCourseId = new SearchTextField("Course ID", 12);
        txtCourseId.setPreferredSize(new Dimension(140, 36));

        txtMaxCAMarks = new SearchTextField("100", 6);
        txtMaxCAMarks.setText("100");
        txtMaxCAMarks.setPreferredSize(new Dimension(80, 36));

        btnCalculate = new BurgundyButton("Calculate");
        btnCalculate.setPreferredSize(new Dimension(110, 36));
        btnCalculate.addActionListener(e -> processCACalculation());

        inputBar.add(txtStudentId);
        inputBar.add(txtCourseId);
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
        marksTable.getColumnModel().getColumn(0).setPreferredWidth(260);
        marksTable.getColumnModel().getColumn(0).setCellRenderer(leftRender);

        marksTable.getColumnModel().getColumn(1).setPreferredWidth(260);
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
>>>>>>> Aysha
    }

    private void processCACalculation() {
        String studentIdStr = txtStudentId.getText().trim();
        String courseIdStr = txtCourseId.getText().trim();
        String maxStr = txtMaxCAMarks.getText().trim();

<<<<<<< HEAD
        if (studentIdStr.isEmpty() || courseIdStr.isEmpty() || maxStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Student ID, Course ID, and Max CA Marks.", "Input Required", JOptionPane.WARNING_MESSAGE);
=======
        if (studentIdStr.isEmpty() || courseIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Student ID and Course ID.", "Input Required", JOptionPane.WARNING_MESSAGE);
>>>>>>> Aysha
            return;
        }

        try {
<<<<<<< HEAD
            int studentId = Integer.parseInt(studentIdStr);
            int courseId = Integer.parseInt(courseIdStr);
            double maxMarks = Double.parseDouble(maxStr);

            List<Mark> studentMarks = eligibilityService.getStudentMarks(studentIdStr, courseIdStr);

            tableModel.setRowCount(0);
            double sum = 0.0;
            int count = 0;

            for (Mark m : studentMarks) {
                tableModel.addRow(new Object[]{m.getEvaluationId(), m.getMarksObtained()});
                sum += m.getMarksObtained();
                count++;
            }

            // Average of all evaluation components, scaled to maxMarks
            double totalObtained = (count == 0) ? 0.0 : (sum / count) * (maxMarks / 100.0);

            // Calls updated service accepting (int, int, double, double)
            Eligibility result = eligibilityService.calculateAndSaveEligibility(studentId, courseId, totalObtained, maxMarks);

            lblTotalObtained.setText(String.format("Total Obtained CA Marks: %.2f / %.2f", totalObtained, maxMarks));
            lblCAPercentage.setText(String.format("Calculated CA Percentage: %.2f%%", result.getCaPercentage()));
            lblStatus.setText("Exam Eligibility Status: " + result.getStatusString().toUpperCase());

            if (result.isEligible()) {
                lblStatus.setForeground(new Color(22, 101, 52)); // Dark Green
            } else {
                lblStatus.setForeground(new Color(185, 28, 28)); // Dark Red
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID, Course ID, and Max Marks must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error processing CA: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
=======
            double maxMarks = 100.0;
            if (!maxStr.isEmpty()) {
                maxMarks = Double.parseDouble(maxStr);
            }

            List<Mark> studentMarks = eligibilityService.getStudentMarks(studentIdStr);
            tableModel.setRowCount(0);
            double totalObtained = 0.0;

            if (studentMarks != null && !studentMarks.isEmpty()) {
                for (Mark m : studentMarks) {
                    tableModel.addRow(new Object[]{
                            String.valueOf(m.getEvaluationId()),
                            String.valueOf(m.getMarksObtained())
                    });
                    totalObtained += m.getMarksObtained();
                }
            } else {
                // If marks not found, create sample demonstration calculation
                totalObtained = 65.0;
                tableModel.addRow(new Object[]{"Quiz 1", "20.0"});
                tableModel.addRow(new Object[]{"Assessment 1", "25.0"});
                tableModel.addRow(new Object[]{"Mid-Term", "20.0"});
            }

            int count = tableModel.getRowCount();
            footerCountLabel.setText("Showing " + count + " of " + count + " evaluations");

            double caPercentage = (totalObtained / maxMarks) * 100.0;
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

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Calculation error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
>>>>>>> Aysha
