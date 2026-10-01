package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Enrollment;
import com.faculty.management.model.MedicalSubmission;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern Undergraduate Medical Submission & Absence Tracking Workspace.
 * Allows students to submit official medical certificates with PDF attachments,
 * track review approval statuses, and verify excused lecture / practical sessions.
 */
public class MedicalPanel extends JPanel {

    private final UndergraduateController controller;
    private final int studentId;

    // KPI Labels
    private JLabel totalSubmissionsLabel;
    private JLabel approvedCountLabel;
    private JLabel pendingCountLabel;
    private JLabel rejectedCountLabel;

    // Form Controls
    private JComboBox<String> courseComboBox;
    private List<Enrollment> enrolledCourses = new ArrayList<>();
    private UITheme.ModernTextField startDateField;
    private UITheme.ModernTextField endDateField;
    private JTextArea reasonArea;
    private JLabel durationHintLabel;

    // Attachment Box
    private File selectedPdfFile;
    private JLabel pdfNameLabel;
    private JLabel pdfSizeLabel;
    private UITheme.ModernButton attachButton;
    private UITheme.ModernButton removePdfButton;
    private JPanel uploadBox;

    // Feedback
    private JPanel feedbackBanner;
    private JLabel feedbackLabel;

    // Table & History
    private DefaultTableModel tableModel;
    private JTable historyTable;
    private JLabel historyStatusLabel;
    private List<MedicalSubmission> currentHistory = new ArrayList<>();

    public MedicalPanel(UndergraduateController controller, int studentId) {
        this.controller = controller;
        this.studentId = studentId;

        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        buildUi();
        loadCourses();
        loadHistory();
    }

    private void buildUi() {
        // -------------------------------------------------------------
        // 1. HEADER & KPI METRICS
        // -------------------------------------------------------------
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // Header Title Row
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setOpaque(false);
        headerBar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel heading = new JLabel("Medical Certificates & Excused Absences");
        heading.setFont(UITheme.FONT_PAGE_TITLE);
        heading.setForeground(UITheme.TEXT_MAIN);

        JLabel subheading = new JLabel("Submit official government medical certificates to regularize missed lectures, labs, or assessments.");
        subheading.setFont(UITheme.FONT_BODY);
        subheading.setForeground(UITheme.TEXT_MUTED);

        titleBox.add(heading);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(subheading);

        UITheme.ModernButton refreshBtn = new UITheme.ModernButton("🔄  Refresh History", UITheme.ButtonStyle.OUTLINE, 8);
        refreshBtn.setFont(UITheme.FONT_SMALL_BOLD);
        refreshBtn.addActionListener(e -> loadHistory());

        headerBar.add(titleBox, BorderLayout.WEST);
        headerBar.add(refreshBtn, BorderLayout.EAST);

        // KPI Stat Cards
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.setBorder(new EmptyBorder(0, 0, 16, 0));

        kpiGrid.add(createKpiCard("📋", "Total Submissions", "0 Requests", UITheme.PRIMARY));
        kpiGrid.add(createKpiCard("✅", "Approved Excuses", "0 Approved", UITheme.SUCCESS_DARK));
        kpiGrid.add(createKpiCard("⏳", "Pending Review", "0 Pending", UITheme.WARNING_DARK));
        kpiGrid.add(createKpiCard("❌", "Rejected", "0 Rejected", UITheme.DANGER_DARK));

        topContainer.add(headerBar);
        topContainer.add(kpiGrid);

        // Feedback Banner (Hidden by default)
        feedbackBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        feedbackBanner.setBackground(UITheme.SUCCESS_LIGHT);
        feedbackBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.SUCCESS_BORDER, 1),
                new EmptyBorder(4, 12, 4, 12)
        ));
        feedbackBanner.setVisible(false);

        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(UITheme.FONT_BODY_BOLD);
        feedbackBanner.add(feedbackLabel);

        topContainer.add(feedbackBanner);
        topContainer.add(Box.createVerticalStrut(8));

        add(topContainer, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. MAIN SPLIT CONTENT (Left: Submit Form, Right: History Table)
        // -------------------------------------------------------------
        JPanel mainSplit = new JPanel(new BorderLayout(20, 0));
        mainSplit.setOpaque(false);

        mainSplit.add(buildFormPanel(), BorderLayout.WEST);
        mainSplit.add(buildHistoryPanel(), BorderLayout.CENTER);

        add(mainSplit, BorderLayout.CENTER);
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

        if (title.contains("Total")) totalSubmissionsLabel = val;
        else if (title.contains("Approved")) approvedCountLabel = val;
        else if (title.contains("Pending")) pendingCountLabel = val;
        else if (title.contains("Rejected")) rejectedCountLabel = val;

        card.add(top, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    // -------------------------------------------------------------
    // FORM PANEL (LEFT)
    // -------------------------------------------------------------
    private JPanel buildFormPanel() {
        UITheme.ModernCard formCard = new UITheme.ModernCard(14);
        formCard.setPreferredSize(new Dimension(380, 0));
        formCard.setLayout(new BorderLayout(0, 12));
        formCard.setBorder(new EmptyBorder(18, 20, 18, 20));

        // Form Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel formTitle = new JLabel("📝  New Medical Request");
        formTitle.setFont(UITheme.FONT_SECTION_TITLE);
        formTitle.setForeground(UITheme.TEXT_MAIN);

        JLabel formSub = new JLabel("Provide valid dates and attach supporting PDF.");
        formSub.setFont(UITheme.FONT_SMALL);
        formSub.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(formTitle, BorderLayout.NORTH);
        titlePanel.add(formSub, BorderLayout.SOUTH);

        // Fields Container
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);

        // Field 1: Affected Module
        fieldsPanel.add(createFieldLabel("Affected Course Module:"));
        courseComboBox = new JComboBox<>();
        courseComboBox.setFont(UITheme.FONT_BODY);
        courseComboBox.setBackground(Color.WHITE);
        courseComboBox.addItem("🌐  All Enrolled Modules (General Period)");
        courseComboBox.setPreferredSize(new Dimension(340, 36));
        courseComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        fieldsPanel.add(courseComboBox);
        fieldsPanel.add(Box.createVerticalStrut(10));

        // Field 2: Date Range
        JPanel dateGrid = new JPanel(new GridLayout(1, 2, 10, 0));
        dateGrid.setOpaque(false);
        dateGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JPanel startBox = new JPanel(new BorderLayout(0, 4));
        startBox.setOpaque(false);
        startBox.add(createFieldLabel("Start Date (From):"), BorderLayout.NORTH);
        startDateField = new UITheme.ModernTextField(LocalDate.now().toString());
        startDateField.setPreferredSize(new Dimension(160, 36));
        startBox.add(startDateField, BorderLayout.CENTER);

        JPanel endBox = new JPanel(new BorderLayout(0, 4));
        endBox.setOpaque(false);
        endBox.add(createFieldLabel("End Date (To):"), BorderLayout.NORTH);
        endDateField = new UITheme.ModernTextField(LocalDate.now().toString());
        endDateField.setPreferredSize(new Dimension(160, 36));
        endBox.add(endDateField, BorderLayout.CENTER);

        dateGrid.add(startBox);
        dateGrid.add(endBox);
        fieldsPanel.add(dateGrid);

        // Quick Date Preset Chips
        JPanel presetRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        presetRow.setOpaque(false);
        presetRow.add(createDateChip("Today", 0));
        presetRow.add(createDateChip("3 Days", 2));
        presetRow.add(createDateChip("1 Week", 6));

        durationHintLabel = new JLabel("Duration: 1 Day");
        durationHintLabel.setFont(UITheme.FONT_SMALL_BOLD);
        durationHintLabel.setForeground(UITheme.PRIMARY);
        presetRow.add(Box.createHorizontalStrut(8));
        presetRow.add(durationHintLabel);

        fieldsPanel.add(presetRow);
        fieldsPanel.add(Box.createVerticalStrut(10));

        // Field 3: Reason Area
        fieldsPanel.add(createFieldLabel("Medical Reason & Diagnosis:"));
        reasonArea = new JTextArea(3, 20);
        reasonArea.setFont(UITheme.FONT_BODY);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1));
        reasonScroll.setPreferredSize(new Dimension(340, 68));
        reasonScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        fieldsPanel.add(reasonScroll);
        fieldsPanel.add(Box.createVerticalStrut(12));

        // Field 4: PDF Upload Box
        fieldsPanel.add(createFieldLabel("Medical Certificate (PDF Document):"));

        uploadBox = new JPanel(new BorderLayout(10, 6));
        uploadBox.setBackground(UITheme.BG_PAGE);
        uploadBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        uploadBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel pdfIcon = new JLabel("📄");
        pdfIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JPanel pdfInfoBox = new JPanel();
        pdfInfoBox.setLayout(new BoxLayout(pdfInfoBox, BoxLayout.Y_AXIS));
        pdfInfoBox.setOpaque(false);

        pdfNameLabel = new JLabel("No PDF certificate selected");
        pdfNameLabel.setFont(UITheme.FONT_BODY_BOLD);
        pdfNameLabel.setForeground(UITheme.TEXT_MUTED);

        pdfSizeLabel = new JLabel("Government hospital or registered physician certificate");
        pdfSizeLabel.setFont(UITheme.FONT_SMALL);
        pdfSizeLabel.setForeground(UITheme.TEXT_LIGHT);

        pdfInfoBox.add(pdfNameLabel);
        pdfInfoBox.add(Box.createVerticalStrut(2));
        pdfInfoBox.add(pdfSizeLabel);

        uploadBox.add(pdfIcon, BorderLayout.WEST);
        uploadBox.add(pdfInfoBox, BorderLayout.CENTER);
        uploadBox.setToolTipText("Click to choose a PDF medical certificate");
        uploadBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        uploadBox.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                choosePdf();
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnRow.setOpaque(false);

        attachButton = new UITheme.ModernButton("📎  Select PDF File...", UITheme.ButtonStyle.OUTLINE, 8);
        attachButton.setFont(UITheme.FONT_SMALL_BOLD);
        attachButton.addActionListener(e -> choosePdf());

        removePdfButton = new UITheme.ModernButton("✕ Remove", UITheme.ButtonStyle.GHOST, 8);
        removePdfButton.setFont(UITheme.FONT_SMALL_BOLD);
        removePdfButton.setForeground(UITheme.DANGER);
        removePdfButton.setVisible(false);
        removePdfButton.addActionListener(e -> clearSelectedPdf());

        btnRow.add(attachButton);
        btnRow.add(removePdfButton);

        JPanel uploadWrapper = new JPanel();
        uploadWrapper.setLayout(new BoxLayout(uploadWrapper, BoxLayout.Y_AXIS));
        uploadWrapper.setOpaque(false);
        uploadWrapper.add(uploadBox);
        uploadWrapper.add(btnRow);

        fieldsPanel.add(uploadWrapper);
        fieldsPanel.add(Box.createVerticalGlue());

        // Submit Button
        UITheme.ModernButton submitBtn = new UITheme.ModernButton("🚀  Submit Medical Request", UITheme.ButtonStyle.PRIMARY, 10);
        submitBtn.setFont(UITheme.FONT_BODY_BOLD);
        submitBtn.setPreferredSize(new Dimension(340, 42));
        submitBtn.addActionListener(e -> submitMedical());

        // The form is deliberately scrollable: on shorter dashboard windows the
        // certificate selector must never be hidden below the visible area.
        JScrollPane formScroll = new JScrollPane(fieldsPanel);
        formScroll.setBorder(null);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        formScroll.getViewport().setBackground(UITheme.BG_CARD);

        formCard.add(titlePanel, BorderLayout.NORTH);
        formCard.add(formScroll, BorderLayout.CENTER);
        formCard.add(submitBtn, BorderLayout.SOUTH);

        return formCard;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_BODY_BOLD);
        label.setForeground(UITheme.TEXT_BODY);
        label.setBorder(new EmptyBorder(0, 0, 4, 0));
        return label;
    }

    private JButton createDateChip(String label, int daysToAdd) {
        JButton btn = new JButton(label);
        btn.setFont(UITheme.FONT_SMALL);
        btn.setForeground(UITheme.PRIMARY);
        btn.setBackground(UITheme.PRIMARY_LIGHT);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.PRIMARY_TINT, 1),
                new EmptyBorder(3, 8, 3, 8)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            LocalDate today = LocalDate.now();
            startDateField.setText(today.toString());
            endDateField.setText(today.plusDays(daysToAdd).toString());
            updateDuration();
        });
        return btn;
    }

    private void updateDuration() {
        try {
            LocalDate start = LocalDate.parse(startDateField.getText().trim());
            LocalDate end = LocalDate.parse(endDateField.getText().trim());
            long days = ChronoUnit.DAYS.between(start, end) + 1;
            if (days <= 0) {
                durationHintLabel.setText("Invalid range");
                durationHintLabel.setForeground(UITheme.DANGER);
            } else {
                durationHintLabel.setText("Duration: " + days + " Day" + (days > 1 ? "s" : ""));
                durationHintLabel.setForeground(UITheme.PRIMARY);
            }
        } catch (Exception ignored) {
            durationHintLabel.setText("Use YYYY-MM-DD");
            durationHintLabel.setForeground(UITheme.TEXT_MUTED);
        }
    }

    // -------------------------------------------------------------
    // HISTORY PANEL (RIGHT)
    // -------------------------------------------------------------
    private JPanel buildHistoryPanel() {
        JPanel historyContainer = new JPanel(new BorderLayout(0, 10));
        historyContainer.setOpaque(false);

        // Header
        JPanel historyHeader = new JPanel(new BorderLayout());
        historyHeader.setOpaque(false);

        JLabel histTitle = new JLabel("📋  Submitted Requests & Status Log");
        histTitle.setFont(UITheme.FONT_SECTION_TITLE);
        histTitle.setForeground(UITheme.TEXT_MAIN);

        JLabel histSub = new JLabel("Review history and official verification status submitted for technical officer review.");
        histSub.setFont(UITheme.FONT_BODY);
        histSub.setForeground(UITheme.TEXT_MUTED);

        JPanel textStack = new JPanel();
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.setOpaque(false);
        textStack.add(histTitle);
        textStack.add(Box.createVerticalStrut(2));
        textStack.add(histSub);

        historyHeader.add(textStack, BorderLayout.WEST);

        // Table
        String[] columns = {"Course Module", "Medical Absence Period", "Reason / Diagnosis", "Certificate Document", "Verification Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        UITheme.styleTable(historyTable);
        historyTable.setRowHeight(42);

        // Status Badge Renderer for Column 4
        historyTable.getColumnModel().getColumn(4).setCellRenderer(new UITheme.StatusBadgeRenderer());

        historyTable.getColumnModel().getColumn(0).setPreferredWidth(140);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(230);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(130);

        historyStatusLabel = new JLabel("Loading medical records...");
        historyStatusLabel.setFont(UITheme.FONT_SMALL);
        historyStatusLabel.setForeground(UITheme.TEXT_MUTED);
        historyStatusLabel.setBorder(new EmptyBorder(6, 4, 0, 0));

        historyContainer.add(historyHeader, BorderLayout.NORTH);
        historyContainer.add(UITheme.createModernScrollPane(historyTable), BorderLayout.CENTER);
        historyContainer.add(historyStatusLabel, BorderLayout.SOUTH);

        return historyContainer;
    }

    private void choosePdf() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Official Medical Certificate (PDF)");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedPdfFile = chooser.getSelectedFile();
            long sizeKb = selectedPdfFile.length() / 1024;
            pdfNameLabel.setText(selectedPdfFile.getName());
            pdfNameLabel.setForeground(UITheme.TEXT_MAIN);
            pdfSizeLabel.setText("Ready for upload • " + sizeKb + " KB");
            pdfSizeLabel.setForeground(UITheme.SUCCESS_DARK);
            removePdfButton.setVisible(true);
            uploadBox.setBackground(UITheme.SUCCESS_LIGHT);
            uploadBox.revalidate();
            uploadBox.repaint();
        }
    }

    private void clearSelectedPdf() {
        selectedPdfFile = null;
        pdfNameLabel.setText("No PDF certificate selected");
        pdfNameLabel.setForeground(UITheme.TEXT_MUTED);
        pdfSizeLabel.setText("Government hospital or registered physician certificate");
        pdfSizeLabel.setForeground(UITheme.TEXT_LIGHT);
        removePdfButton.setVisible(false);
        uploadBox.setBackground(UITheme.BG_PAGE);
        uploadBox.revalidate();
        uploadBox.repaint();
    }

    private void loadCourses() {
        try {
            enrolledCourses = controller.getCourseDetails(studentId);
            for (Enrollment e : enrolledCourses) {
                courseComboBox.addItem(e.getCourse().getCourseCode() + " — " + e.getCourse().getCourseName());
            }
        } catch (DatabaseException e) {
            showBanner(false, "Could not load enrolled modules: " + e.getMessage());
        }
    }

    private void loadHistory() {
        try {
            currentHistory = controller.getMedicalHistory(studentId);
            tableModel.setRowCount(0);

            int approved = 0;
            int pending = 0;
            int rejected = 0;

            for (MedicalSubmission m : currentHistory) {
                String courseStr = m.getCourseCode() != null ? m.getCourseCode() : "All Modules";
                String periodStr = m.getStartDate() + "  →  " + m.getEndDate();
                String docStr = (m.getDocumentPath() != null && !m.getDocumentPath().isBlank())
                        ? "📄 Attached PDF"
                        : "No Document";

                String status = m.getStatus() != null ? m.getStatus().toUpperCase() : "PENDING";
                if (status.contains("APPROV")) approved++;
                else if (status.contains("REJECT")) rejected++;
                else pending++;

                tableModel.addRow(new Object[]{
                        courseStr,
                        periodStr,
                        m.getReason(),
                        docStr,
                        status
                });
            }

            totalSubmissionsLabel.setText(currentHistory.size() + " Requests");
            approvedCountLabel.setText(approved + " Approved");
            pendingCountLabel.setText(pending + " Pending");
            rejectedCountLabel.setText(rejected + " Rejected");

            historyStatusLabel.setText("Loaded " + currentHistory.size() + " medical submission records.");
            historyStatusLabel.setForeground(UITheme.TEXT_MUTED);

        } catch (DatabaseException e) {
            historyStatusLabel.setText("Failed to load medical history: " + e.getMessage());
            historyStatusLabel.setForeground(UITheme.DANGER_DARK);
        }
    }

    private void submitMedical() {
        try {
            if (selectedPdfFile == null) {
                throw new ValidationException("Please attach an official medical certificate as a PDF file.");
            }

            LocalDate start;
            LocalDate end;
            try {
                start = LocalDate.parse(startDateField.getText().trim());
                end = LocalDate.parse(endDateField.getText().trim());
            } catch (DateTimeParseException ex) {
                throw new ValidationException("Please specify valid dates in YYYY-MM-DD format.");
            }

            if (end.isBefore(start)) {
                throw new ValidationException("End date cannot be prior to start date.");
            }

            String reason = reasonArea.getText().trim();
            if (reason.length() < 5) {
                throw new ValidationException("Please provide a descriptive medical reason (minimum 5 characters).");
            }

            Integer selectedCourseId = null;
            String courseCodeDisplay = "All Modules";
            int selectedIdx = courseComboBox.getSelectedIndex();
            if (selectedIdx > 0 && selectedIdx - 1 < enrolledCourses.size()) {
                Enrollment selected = enrolledCourses.get(selectedIdx - 1);
                selectedCourseId = selected.getCourse().getCourseId();
                courseCodeDisplay = selected.getCourse().getCourseCode();
            }

            // Save PDF
            String storedPath = controller.uploadMedicalPdf(studentId, selectedPdfFile);

            // Save Database Record
            controller.submitMedical(studentId, selectedCourseId, start, end, reason, storedPath);

            // Add row directly to table model
            tableModel.insertRow(0, new Object[]{
                    courseCodeDisplay,
                    start + "  →  " + end,
                    reason,
                    "📄 " + selectedPdfFile.getName(),
                    "PENDING"
            });

            // Reset Form
            reasonArea.setText("");
            clearSelectedPdf();
            startDateField.setText(LocalDate.now().toString());
            endDateField.setText(LocalDate.now().toString());
            courseComboBox.setSelectedIndex(0);
            updateDuration();

            // Refresh KPI Stats
            loadHistory();

            showBanner(true, "✓ Medical request submitted successfully! Technical Officer will verify the document.");

            JOptionPane.showMessageDialog(this,
                    "Your medical request for period " + start + " to " + end + " has been submitted.\n" +
                            "The Technical Officer will review the attached certificate and update your attendance status.",
                    "Medical Submission Received",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (ValidationException | DatabaseException ex) {
            showBanner(false, ex.getMessage());
        }
    }

    private void showBanner(boolean isSuccess, String message) {
        feedbackBanner.setVisible(true);
        if (isSuccess) {
            feedbackBanner.setBackground(UITheme.SUCCESS_LIGHT);
            feedbackBanner.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.SUCCESS_BORDER, 1),
                    new EmptyBorder(4, 12, 4, 12)
            ));
            feedbackLabel.setForeground(UITheme.SUCCESS_DARK);
            feedbackLabel.setText("✓  " + message);
        } else {
            feedbackBanner.setBackground(UITheme.DANGER_LIGHT);
            feedbackBanner.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.DANGER_BORDER, 1),
                    new EmptyBorder(4, 12, 4, 12)
            ));
            feedbackLabel.setForeground(UITheme.DANGER_DARK);
            feedbackLabel.setText("⚠️  " + message);
        }
    }
}
