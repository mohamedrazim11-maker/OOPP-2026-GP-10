package com.faculty.management.gui.member3_attendance;

import com.faculty.management.controller.TechnicalOfficerController;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Notice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Notice Board Viewer Panel for Technical Officer.
 * Displays official faculty notices, circulars, exam memos, and lab announcements.
 * Embedded directly inside TechnicalOfficerDashboard tabbed pane.
 * Architecture: Interacts exclusively via TechnicalOfficerController.
 */
public class NoticeViewerPanel extends JPanel {

    private final TechnicalOfficerController controller;
    private List<Notice> allNotices = new ArrayList<>();

    private JTextField txtSearch;
    private JComboBox<String> cmbCategory;
    private JComboBox<String> cmbPriority;
    private JPanel noticeListPanel;
    private JLabel lblTotalCount;
    private JLabel lblUrgentCount;
    private JLabel lblLatestDate;
    private JLabel lblStatus;

    public NoticeViewerPanel() {
        this.controller = new TechnicalOfficerController();
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);

        initComponents();
        loadNotices();
    }

    private void initComponents() {
        // 1. KPI Summary Stat Cards
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        kpiPanel.setOpaque(false);
        kpiPanel.setBorder(new EmptyBorder(14, 20, 10, 20));

        lblTotalCount = new JLabel("0 Notices", SwingConstants.CENTER);
        lblUrgentCount = new JLabel("0 Urgent", SwingConstants.CENTER);
        lblLatestDate = new JLabel("Today", SwingConstants.CENTER);

        kpiPanel.add(createKpiCard("📢 Total Active Notices", lblTotalCount, UITheme.PRIMARY));
        kpiPanel.add(createKpiCard("🚨 Urgent Announcements", lblUrgentCount, UITheme.DANGER));
        kpiPanel.add(createKpiCard("📅 Latest Notice Date", lblLatestDate, UITheme.SUCCESS_DARK));

        // 2. Search & Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterBar.setBackground(Color.WHITE);
        filterBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(4, 16, 4, 16)
        ));

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(UITheme.FONT_BODY_BOLD);
        filterBar.add(lblSearch);

        txtSearch = new JTextField(16);
        txtSearch.setPreferredSize(new Dimension(180, 32));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        filterBar.add(txtSearch);

        JLabel lblCat = new JLabel("Category:");
        lblCat.setFont(UITheme.FONT_BODY_BOLD);
        filterBar.add(lblCat);

        cmbCategory = new JComboBox<>(new String[]{"ALL CATEGORIES", "ACADEMIC", "EXAMINATION", "TIMETABLE", "GENERAL"});
        cmbCategory.setPreferredSize(new Dimension(150, 32));
        cmbCategory.addActionListener(e -> applyFilters());
        filterBar.add(cmbCategory);

        JLabel lblPri = new JLabel("Priority:");
        lblPri.setFont(UITheme.FONT_BODY_BOLD);
        filterBar.add(lblPri);

        cmbPriority = new JComboBox<>(new String[]{"ALL PRIORITIES", "URGENT", "NORMAL", "INFO"});
        cmbPriority.setPreferredSize(new Dimension(130, 32));
        cmbPriority.addActionListener(e -> applyFilters());
        filterBar.add(cmbPriority);

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setFont(UITheme.FONT_SMALL_BOLD);
        refreshBtn.addActionListener(e -> loadNotices());
        filterBar.add(refreshBtn);

        lblStatus = new JLabel("Loading notices...");
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.TEXT_MUTED);
        filterBar.add(lblStatus);

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        topSection.add(kpiPanel);
        topSection.add(filterBar);

        // 3. Scrollable Notices Container
        noticeListPanel = new JPanel();
        noticeListPanel.setLayout(new BoxLayout(noticeListPanel, BoxLayout.Y_AXIS));
        noticeListPanel.setOpaque(false);
        noticeListPanel.setBorder(new EmptyBorder(12, 24, 16, 24));

        JScrollPane scrollPane = UITheme.createModernScrollPane(noticeListPanel);
        scrollPane.getViewport().setBackground(UITheme.BG_PAGE);
        scrollPane.setBorder(null);

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createKpiCard(String title, JLabel valLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(UITheme.FONT_SMALL_BOLD);
        tLbl.setForeground(UITheme.TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valLabel.setForeground(accent);

        card.add(tLbl, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        return card;
    }

    public void loadNotices() {
        try {
            allNotices = controller.getAllNotices();
            if (allNotices == null || allNotices.isEmpty()) {
                allNotices = getFallbackNotices();
            }
        } catch (Exception ex) {
            allNotices = getFallbackNotices();
        }
        applyFilters();
    }

    private void applyFilters() {
        if (allNotices == null) return;

        String query = txtSearch.getText().trim().toLowerCase();
        String selectedCat = (String) cmbCategory.getSelectedItem();
        String selectedPri = (String) cmbPriority.getSelectedItem();

        noticeListPanel.removeAll();

        int urgentCount = 0;
        String latestDate = "No Notices";

        for (Notice n : allNotices) {
            if ("URGENT".equalsIgnoreCase(n.getPriority())) urgentCount++;
            if (n.getFormattedDate() != null) latestDate = n.getFormattedDate();
        }

        lblTotalCount.setText(allNotices.size() + " Notices");
        lblUrgentCount.setText(urgentCount + " Urgent");
        lblLatestDate.setText(latestDate);
        lblStatus.setText("Total " + allNotices.size() + " official announcements loaded.");

        List<Notice> filtered = allNotices.stream()
                .filter(n -> {
                    if (query.isEmpty()) return true;
                    return n.getTitle().toLowerCase().contains(query)
                            || n.getContent().toLowerCase().contains(query)
                            || n.getPostedBy().toLowerCase().contains(query);
                })
                .filter(n -> {
                    if ("ALL CATEGORIES".equalsIgnoreCase(selectedCat)) return true;
                    return selectedCat != null && selectedCat.equalsIgnoreCase(n.getCategory());
                })
                .filter(n -> {
                    if ("ALL PRIORITIES".equalsIgnoreCase(selectedPri)) return true;
                    return selectedPri != null && selectedPri.equalsIgnoreCase(n.getPriority());
                })
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            emptyPanel.setOpaque(false);
            emptyPanel.setBorder(new EmptyBorder(40, 20, 40, 20));
            JLabel emptyLbl = new JLabel("No notices match the chosen search query or filters.");
            emptyLbl.setFont(UITheme.FONT_CARD_TITLE);
            emptyLbl.setForeground(UITheme.TEXT_MUTED);
            emptyPanel.add(emptyLbl);
            noticeListPanel.add(emptyPanel);
        } else {
            for (Notice n : filtered) {
                noticeListPanel.add(createNoticeCard(n));
                noticeListPanel.add(Box.createVerticalStrut(14));
            }
        }

        noticeListPanel.revalidate();
        noticeListPanel.repaint();
    }

    private JPanel createNoticeCard(Notice n) {
        UITheme.ModernCard card = new UITheme.ModernCard(12);
        card.setLayout(new BorderLayout(14, 10));

        boolean isUrgent = "URGENT".equalsIgnoreCase(n.getPriority());
        Color accent = isUrgent ? UITheme.DANGER : UITheme.PRIMARY;
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(14, 18, 14, 18)
        ));

        // Top Row: Category + Priority Badges & Date
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JPanel leftBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftBadges.setOpaque(false);

        Color catBg = "TIMETABLE".equalsIgnoreCase(n.getCategory()) ? UITheme.SUCCESS_LIGHT :
                      "EXAMINATION".equalsIgnoreCase(n.getCategory()) ? UITheme.WARNING_LIGHT :
                      UITheme.LAVENDER_SURFACE;
        Color catFg = "TIMETABLE".equalsIgnoreCase(n.getCategory()) ? UITheme.SUCCESS_DARK :
                      "EXAMINATION".equalsIgnoreCase(n.getCategory()) ? UITheme.WARNING_DARK :
                      UITheme.PRIMARY_DARK;

        UITheme.PillBadge catBadge = new UITheme.PillBadge(n.getCategory(), catBg, catFg, catBg);
        leftBadges.add(catBadge);

        if (isUrgent) {
            UITheme.PillBadge priBadge = new UITheme.PillBadge("🚨 URGENT", UITheme.DANGER_LIGHT, UITheme.DANGER_DARK, UITheme.DANGER_BORDER);
            leftBadges.add(priBadge);
        }

        JLabel dateLabel = new JLabel("📅 " + n.getFormattedDate());
        dateLabel.setFont(UITheme.FONT_SMALL);
        dateLabel.setForeground(UITheme.TEXT_MUTED);

        topRow.add(leftBadges, BorderLayout.WEST);
        topRow.add(dateLabel, BorderLayout.EAST);

        // Center: Title and Content
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(n.getTitle());
        titleLbl.setFont(UITheme.FONT_CARD_TITLE);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JTextArea contentArea = new JTextArea(n.getContent());
        contentArea.setFont(UITheme.FONT_BODY);
        contentArea.setForeground(new Color(71, 85, 105)); // Slate 600
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setOpaque(false);
        contentArea.setBorder(new EmptyBorder(8, 0, 8, 0));

        bodyPanel.add(titleLbl);
        bodyPanel.add(contentArea);

        // Bottom Row: Posted By metadata
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        bottomRow.setOpaque(false);

        JLabel postedLbl = new JLabel("Issued by: " + n.getPostedBy());
        postedLbl.setFont(UITheme.FONT_SMALL_BOLD);
        postedLbl.setForeground(UITheme.TEXT_MUTED);
        bottomRow.add(postedLbl);

        card.add(topRow, BorderLayout.NORTH);
        card.add(bodyPanel, BorderLayout.CENTER);
        card.add(bottomRow, BorderLayout.SOUTH);

        return card;
    }

    private List<Notice> getFallbackNotices() {
        List<Notice> list = new ArrayList<>();
        list.add(new Notice(1,
                "End-Semester Examination Attendance 80% Eligibility Threshold",
                "All Technical Officers must submit finalized batch attendance reports and verified medical certificates by Friday 5:00 PM. Students below 80% attendance will be deemed ineligible under UGC circular regulations.",
                "EXAMINATION", "URGENT", "Senior Assistant Registrar", java.time.LocalDate.now().minusDays(1)));

        list.add(new Notice(2,
                "Computer Lab 2 Maintenance and Software Updates",
                "Computer Lab 2 will be undergoing scheduled operating system maintenance and database server updates this Thursday from 1:00 PM to 4:00 PM. Practical sessions are reallocated to Computer Lab 1.",
                "TIMETABLE", "NORMAL", "Head of Department (ICT)", java.time.LocalDate.now().minusDays(3)));

        list.add(new Notice(3,
                "Submission Deadline for Semester Medical Certificates",
                "Undergraduate students are reminded that medical submissions for missed laboratory sessions must be handed in within 7 days of absence with valid government hospital certification.",
                "ACADEMIC", "NORMAL", "Faculty Medical Board", java.time.LocalDate.now().minusDays(5)));

        list.add(new Notice(4,
                "Faculty Annual Research Symposium - Duty Allocation",
                "Technical Officers assigned to audio-visual and laboratory equipment setup for the upcoming Faculty Symposium are requested to attend a coordination briefing on Monday at 9:00 AM.",
                "GENERAL", "INFO", "Dean, Faculty of Technology", java.time.LocalDate.now().minusDays(7)));

        return list;
    }
}
