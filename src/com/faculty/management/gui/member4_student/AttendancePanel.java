package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.AttendanceRecord;
import com.faculty.management.model.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern Undergraduate Attendance & Exam Eligibility Monitor.
 * Provides transparent tracking of Theory (15 sessions), Practical (15 sessions),
 * and Combined attendance percentages adhering to the mandatory 80% UGC eligibility rule.
 */
public class AttendancePanel extends JPanel {

    private final UndergraduateController controller;
    private final int studentId;

    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTable table;

    // KPI Card Labels
    private JLabel overallPctLabel;
    private JLabel totalSessionsLabel;
    private JLabel eligibilityLabel;

    // Filter & Search
    private UITheme.ModernTextField searchField;
    private JComboBox<String> filterCombo;
    private JLabel statusLabel;

    // Selected Course Details Card
    private UITheme.ModernCard detailCard;
    private JLabel detailTitleLabel;
    private JLabel detailHoursLabel;
    private JLabel detailAdviceLabel;
    private UITheme.PillBadge detailBadge;

    private List<AttendanceRecord> currentRecords = new ArrayList<>();

    public AttendancePanel(UndergraduateController controller, int studentId) {
        this.controller = controller;
        this.studentId = studentId;
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        buildUi();
        loadAttendance();
    }

    private void buildUi() {
        // -------------------------------------------------------------
        // 1. TOP HEADER & KPI CARDS
        // -------------------------------------------------------------
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // Header Title Bar
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setOpaque(false);
        headerBar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel heading = new JLabel("Attendance & Examination Eligibility");
        heading.setFont(UITheme.FONT_PAGE_TITLE);
        heading.setForeground(UITheme.TEXT_MAIN);

        JLabel subheading = new JLabel("Track theory & practical attendance records against the mandatory 80% UGC examination eligibility threshold.");
        subheading.setFont(UITheme.FONT_BODY);
        subheading.setForeground(UITheme.TEXT_MUTED);

        titleBox.add(heading);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(subheading);

        UITheme.ModernButton refreshBtn = new UITheme.ModernButton("🔄  Refresh Attendance", UITheme.ButtonStyle.OUTLINE, 8);
        refreshBtn.setFont(UITheme.FONT_SMALL_BOLD);
        refreshBtn.addActionListener(e -> loadAttendance());

        headerBar.add(titleBox, BorderLayout.WEST);
        headerBar.add(refreshBtn, BorderLayout.EAST);

        // 4 KPI Summary Stat Cards
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.setBorder(new EmptyBorder(0, 0, 16, 0));

        kpiGrid.add(createKpiCard("📊", "Overall Combined %", "0.0%", UITheme.PRIMARY));
        kpiGrid.add(createKpiCard("🗓️", "Sessions Attended", "0 / 0", UITheme.ACCENT_BLUE));
        kpiGrid.add(createKpiCard("🎯", "Eligibility Status", "Evaluating...", UITheme.SUCCESS_DARK));
        kpiGrid.add(createKpiCard("📐", "UGC Requirement", "≥ 80.0% Required", UITheme.WARNING_DARK));

        topContainer.add(headerBar);
        topContainer.add(kpiGrid);

        // -------------------------------------------------------------
        // 2. SEARCH & FILTER CONTROLS BAR
        // -------------------------------------------------------------
        UITheme.ModernCard filterCard = new UITheme.ModernCard(12);
        filterCard.setLayout(new BorderLayout(14, 0));
        filterCard.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel leftFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilter.setOpaque(false);

        searchField = new UITheme.ModernTextField("🔍  Search course code or title...", 20);
        searchField.setPreferredSize(new Dimension(280, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        JLabel filterLbl = new JLabel("Filter Status:");
        filterLbl.setFont(UITheme.FONT_BODY_BOLD);
        filterLbl.setForeground(UITheme.TEXT_MAIN);

        filterCombo = new JComboBox<>(new String[]{"All Modules", "Eligible Only (≥ 80%)", "Shortage Warning (< 80%)"});
        filterCombo.setFont(UITheme.FONT_BODY);
        filterCombo.setBackground(Color.WHITE);
        filterCombo.setPreferredSize(new Dimension(190, 36));
        filterCombo.addActionListener(e -> applyFilters());

        leftFilter.add(searchField);
        leftFilter.add(Box.createHorizontalStrut(6));
        leftFilter.add(filterLbl);
        leftFilter.add(filterCombo);

        // Right Legend Indicators
        JPanel rightLegend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightLegend.setOpaque(false);

        UITheme.PillBadge eligibleBadge = new UITheme.PillBadge("✓ ≥80% Eligible", UITheme.SUCCESS_LIGHT, UITheme.SUCCESS_DARK, UITheme.SUCCESS_BORDER);
        UITheme.PillBadge warningBadge = new UITheme.PillBadge("⚠️ <80% Shortage", UITheme.DANGER_LIGHT, UITheme.DANGER_DARK, UITheme.DANGER_BORDER);

        rightLegend.add(eligibleBadge);
        rightLegend.add(warningBadge);

        filterCard.add(leftFilter, BorderLayout.WEST);
        filterCard.add(rightLegend, BorderLayout.EAST);

        topContainer.add(filterCard);
        add(topContainer, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 3. MAIN TABLE OF ATTENDANCE
        // -------------------------------------------------------------
        String[] columns = {
                "Course Module", "Credits", "Theory Sessions", "Theory %",
                "Practical Sessions", "Practical %", "Combined %", "Exam Eligibility"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setRowHeight(42);

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Custom Progress Bar Renderers
        UITheme.ProgressBarTableCellRenderer progressRenderer = new UITheme.ProgressBarTableCellRenderer();
        table.getColumnModel().getColumn(3).setCellRenderer(progressRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(progressRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(progressRenderer);

        // Status Badge Renderer for column 7
        table.getColumnModel().getColumn(7).setCellRenderer(new UITheme.StatusBadgeRenderer());

        // Center credits
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(260);
        table.getColumnModel().getColumn(1).setPreferredWidth(60);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(115);
        table.getColumnModel().getColumn(5).setPreferredWidth(140);
        table.getColumnModel().getColumn(6).setPreferredWidth(140);
        table.getColumnModel().getColumn(7).setPreferredWidth(130);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailCard();
            }
        });

        // -------------------------------------------------------------
        // 4. BOTTOM DETAIL & ADVISORY CARD
        // -------------------------------------------------------------
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setOpaque(false);
        bottomContainer.setBorder(new EmptyBorder(10, 0, 0, 0));

        detailCard = new UITheme.ModernCard(12);
        detailCard.setLayout(new BorderLayout(16, 8));
        detailCard.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel detailHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        detailHeader.setOpaque(false);

        detailTitleLabel = new JLabel("Select any module above to inspect hour breakdown & official advice");
        detailTitleLabel.setFont(UITheme.FONT_CARD_TITLE);
        detailTitleLabel.setForeground(UITheme.TEXT_MAIN);

        detailBadge = new UITheme.PillBadge("COURSE DETAILS", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, UITheme.PRIMARY_TINT);

        detailHeader.add(detailTitleLabel);
        detailHeader.add(detailBadge);

        JPanel detailBody = new JPanel(new GridLayout(2, 1, 4, 4));
        detailBody.setOpaque(false);

        detailHoursLabel = new JLabel("Credit Calculation Rule: 1 Theory Credit (1T) = 2 Hours/Session | 1 Practical Credit (1P) = 2 Hours/Session (15 total sessions).");
        detailHoursLabel.setFont(UITheme.FONT_BODY);
        detailHoursLabel.setForeground(UITheme.TEXT_MUTED);

        detailAdviceLabel = new JLabel("To excuse missed sessions due to illness or official leave, submit valid medical certificates under the Medical tab.");
        detailAdviceLabel.setFont(UITheme.FONT_BODY_BOLD);
        detailAdviceLabel.setForeground(UITheme.PRIMARY);

        detailBody.add(detailHoursLabel);
        detailBody.add(detailAdviceLabel);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setBorder(new EmptyBorder(6, 4, 0, 0));

        detailCard.add(detailHeader, BorderLayout.NORTH);
        detailCard.add(detailBody, BorderLayout.CENTER);

        bottomContainer.add(detailCard);
        bottomContainer.add(statusLabel);

        add(UITheme.createModernScrollPane(table), BorderLayout.CENTER);
        add(bottomContainer, BorderLayout.SOUTH);
    }

    private JPanel createKpiCard(String icon, String title, String initialVal, Color accent) {
        UITheme.ModernCard card = new UITheme.ModernCard(12);
        card.setLayout(new BorderLayout(10, 6));
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        top.setOpaque(false);

        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        JLabel ttl = new JLabel(title.toUpperCase());
        ttl.setFont(UITheme.FONT_SMALL_BOLD);
        ttl.setForeground(UITheme.TEXT_MUTED);

        top.add(ico);
        top.add(ttl);

        JLabel val = new JLabel(initialVal);
        val.setFont(UITheme.FONT_KPI_VALUE);
        val.setForeground(accent != null ? accent : UITheme.TEXT_MAIN);
        val.setBorder(new EmptyBorder(4, 2, 0, 0));

        if (title.contains("Overall")) overallPctLabel = val;
        else if (title.contains("Sessions")) totalSessionsLabel = val;
        else if (title.contains("Eligibility")) eligibilityLabel = val;

        card.add(top, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    public void loadAttendance() {
        try {
            currentRecords = controller.getAttendance(studentId);
            tableModel.setRowCount(0);

            if (currentRecords == null || currentRecords.isEmpty()) {
                statusLabel.setText("No enrolled course attendance records found for this student account.");
                statusLabel.setForeground(UITheme.TEXT_MUTED);
                overallPctLabel.setText("0.0%");
                totalSessionsLabel.setText("0 / 0");
                eligibilityLabel.setText("No Records");
                return;
            }

            int totalAttended = 0;
            int totalSessions = 0;
            int ineligibleCount = 0;

            for (AttendanceRecord r : currentRecords) {
                Course c = r.getCourse();
                int courseAttended = r.getTheoryAttended() + r.getPracticalAttended();
                int courseTotal = r.getTheoryTotal() + r.getPracticalTotal();

                totalAttended += courseAttended;
                totalSessions += courseTotal;

                double theoryPct = r.getTheoryPercentage();
                double practicalPct = r.getPracticalPercentage();
                double combinedPct = r.getCombinedPercentage();

                boolean isEligible = combinedPct >= 80.0;
                if (!isEligible) {
                    ineligibleCount++;
                }

                String theorySessionsStr = r.getTheoryAttended() + " / " + r.getTheoryTotal();
                String practicalSessionsStr = r.getPracticalTotal() > 0
                        ? r.getPracticalAttended() + " / " + r.getPracticalTotal()
                        : "N/A (Theory Only)";

                Object practicalPctVal = r.getPracticalTotal() > 0 ? practicalPct : "N/A";

                tableModel.addRow(new Object[]{
                        c.getCourseCode() + " — " + c.getCourseName(),
                        c.getCredits() + " Credits",
                        theorySessionsStr,
                        theoryPct,
                        practicalSessionsStr,
                        practicalPctVal,
                        combinedPct,
                        isEligible ? "ELIGIBLE" : "SHORTAGE (<80%)"
                });
            }

            double overallPct = totalSessions > 0 ? (totalAttended * 100.0 / totalSessions) : 0.0;
            overallPctLabel.setText(String.format("%.1f%%", overallPct));
            overallPctLabel.setForeground(overallPct >= 80.0 ? UITheme.SUCCESS : UITheme.DANGER);

            totalSessionsLabel.setText(totalAttended + " / " + totalSessions);

            if (ineligibleCount == 0) {
                eligibilityLabel.setText("All Eligible (" + currentRecords.size() + ")");
                eligibilityLabel.setForeground(UITheme.SUCCESS_DARK);
            } else {
                eligibilityLabel.setText(ineligibleCount + " at Risk (<80%)");
                eligibilityLabel.setForeground(UITheme.DANGER);
            }

            statusLabel.setForeground(UITheme.TEXT_MUTED);
            statusLabel.setText("Loaded " + currentRecords.size() + " enrolled course attendance records successfully.");

            if (table.getRowCount() > 0) {
                table.setRowSelectionInterval(0, 0);
            }

        } catch (DatabaseException e) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Failed to load attendance records: " + e.getMessage());
        }
    }

    private void applyFilters() {
        String query = searchField.getText().trim();
        int filterIdx = filterCombo.getSelectedIndex();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!query.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + query, 0));
        }

        if (filterIdx == 1) { // Eligible
            filters.add(RowFilter.regexFilter("(?i)ELIGIBLE", 7));
        } else if (filterIdx == 2) { // Shortage
            filters.add(RowFilter.regexFilter("(?i)SHORTAGE", 7));
        }

        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void updateDetailCard() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return;

        int modelRow = table.convertRowIndexToModel(selectedRow);
        if (modelRow >= 0 && modelRow < currentRecords.size()) {
            AttendanceRecord r = currentRecords.get(modelRow);
            Course c = r.getCourse();
            double combined = r.getCombinedPercentage();
            boolean eligible = combined >= 80.0;

            detailTitleLabel.setText(c.getCourseCode() + ": " + c.getCourseName());

            int theoryHours = r.getTheoryAttended() * 2;
            int totalTheoryHours = r.getTheoryTotal() * 2;
            int practicalHours = r.getPracticalAttended() * 2;
            int totalPracticalHours = r.getPracticalTotal() * 2;

            detailHoursLabel.setText("Attendance Hours: Theory " + theoryHours + "/" + totalTheoryHours + " hrs (" +
                    String.format("%.1f%%", r.getTheoryPercentage()) + ") | Practical: " +
                    (r.getPracticalTotal() > 0 ? practicalHours + "/" + totalPracticalHours + " hrs (" + String.format("%.1f%%", r.getPracticalPercentage()) + ")" : "N/A"));

            if (eligible) {
                detailBadge.setColors(UITheme.SUCCESS_LIGHT, UITheme.SUCCESS_DARK, UITheme.SUCCESS_BORDER);
                detailBadge.setText("ELIGIBLE TO SIT FOR EXAM");
                detailAdviceLabel.setText("✓ Student has achieved " + String.format("%.1f%%", combined) +
                        " attendance, meeting the 80% UGC requirement to sit for final semester examinations.");
                detailAdviceLabel.setForeground(UITheme.SUCCESS_DARK);
            } else {
                detailBadge.setColors(UITheme.DANGER_LIGHT, UITheme.DANGER_DARK, UITheme.DANGER_BORDER);
                detailBadge.setText("ATTENDANCE SHORTAGE");
                detailAdviceLabel.setText("⚠️ Student attendance is " + String.format("%.1f%%", combined) +
                        " (< 80%). You must submit a verified medical certificate via the Medical tab to regularize eligibility.");
                detailAdviceLabel.setForeground(UITheme.DANGER_DARK);
            }
        }
    }
}
