package com.faculty.management.gui.member3_attendance;

import com.faculty.management.dao.NoticeDAO;
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
 * Notice Board Viewer Dialog for Technical Officer.
 * Displays official faculty notices, circulars, exam memos, and lab announcements.
 * Fulfills the requirement: "Technical Officer can see notices".
 */
public class NoticeViewerDialog extends JDialog {

    private final NoticeDAO noticeDAO;
    private List<Notice> allNotices = new ArrayList<>();

    private JTextField txtSearch;
    private JComboBox<String> cmbCategory;
    private JComboBox<String> cmbPriority;
    private JPanel noticeListPanel;
    private JLabel lblTotalCount;
    private JLabel lblUrgentCount;
    private JLabel lblLatestDate;
    private JLabel lblStatus;

    public NoticeViewerDialog(JFrame parent) {
        super(parent, "Faculty Notice Board & Official Circulars — Technical Operations", true);
        this.noticeDAO = new NoticeDAO();

        initComponents();
        loadNotices();
    }

    private void initComponents() {
        setSize(1000, 720);
        setMinimumSize(new Dimension(880, 600));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_PAGE);

        // 1. Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42)); // Slate Navy
        headerPanel.setBorder(new EmptyBorder(16, 25, 16, 25));

        JLabel titleLabel = new JLabel("📢 Faculty Notice Board & Announcements");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Review official circulars, examination eligibility memos, lab venue notices, and schedules");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(148, 163, 184));

        JPanel headerText = new JPanel(new GridLayout(2, 1, 0, 4));
        headerText.setOpaque(false);
        headerText.add(titleLabel);
        headerText.add(subLabel);

        UITheme.ModernButton refreshBtn = new UITheme.ModernButton("🔄 Refresh", UITheme.ButtonStyle.SECONDARY, 6);
        refreshBtn.setFont(UITheme.FONT_SMALL_BOLD);
        refreshBtn.addActionListener(e -> loadNotices());

        headerPanel.add(headerText, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        // 2. KPI Summary Stat Cards
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        kpiPanel.setOpaque(false);
        kpiPanel.setBorder(new EmptyBorder(16, 24, 8, 24));

        lblTotalCount = new JLabel("0 Notices", SwingConstants.CENTER);
        lblUrgentCount = new JLabel("0 Urgent", SwingConstants.CENTER);
        lblLatestDate = new JLabel("Today", SwingConstants.CENTER);

        kpiPanel.add(createKpiCard("📢 Total Active Notices", lblTotalCount, UITheme.PRIMARY));
        kpiPanel.add(createKpiCard("🚨 Urgent Announcements", lblUrgentCount, UITheme.DANGER));
        kpiPanel.add(createKpiCard("📅 Latest Notice Date", lblLatestDate, UITheme.SUCCESS_DARK));

        // 3. Search & Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterBar.setBackground(Color.WHITE);
        filterBar.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(8, 24, 8, 24),
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240))
        ));

        filterBar.add(new JLabel("Search:"));
        txtSearch = new JTextField(18);
        txtSearch.setPreferredSize(new Dimension(200, 32));
        txtSearch.putClientProperty("JTextField.placeholderText", "Search keywords...");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        filterBar.add(txtSearch);

        filterBar.add(new JLabel("Category:"));
        cmbCategory = new JComboBox<>(new String[]{"ALL CATEGORIES", "ACADEMIC", "EXAMINATION", "TIMETABLE", "GENERAL"});
        cmbCategory.setPreferredSize(new Dimension(150, 32));
        cmbCategory.addActionListener(e -> applyFilters());
        filterBar.add(cmbCategory);

        filterBar.add(new JLabel("Priority:"));
        cmbPriority = new JComboBox<>(new String[]{"ALL PRIORITIES", "URGENT", "NORMAL", "INFO"});
        cmbPriority.setPreferredSize(new Dimension(130, 32));
        cmbPriority.addActionListener(e -> applyFilters());
        filterBar.add(cmbPriority);

        lblStatus = new JLabel("Loading notices...");
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.TEXT_MUTED);
        filterBar.add(lblStatus);

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        topSection.add(headerPanel);
        topSection.add(kpiPanel);
        topSection.add(filterBar);

        // 4. Scrollable Notices Container
        noticeListPanel = new JPanel();
        noticeListPanel.setLayout(new BoxLayout(noticeListPanel, BoxLayout.Y_AXIS));
        noticeListPanel.setOpaque(false);
        noticeListPanel.setBorder(new EmptyBorder(12, 24, 16, 24));

        JScrollPane scrollPane = UITheme.createModernScrollPane(noticeListPanel);
        scrollPane.setBorder(null);

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createKpiCard(String title, JLabel valLabel, Color accentColor) {
        UITheme.ModernCard card = new UITheme.ModernCard(10);
        card.setLayout(new BorderLayout(8, 6));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL_BOLD);
        titleLbl.setForeground(UITheme.TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valLabel.setForeground(accentColor);
        valLabel.setHorizontalAlignment(SwingConstants.LEFT);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        return card;
    }

    private void loadNotices() {
        try {
            allNotices = noticeDAO.findAll();
            if (allNotices == null) allNotices = new ArrayList<>();

            int total = allNotices.size();
            long urgent = allNotices.stream().filter(n -> "URGENT".equalsIgnoreCase(n.getPriority())).count();
            lblTotalCount.setText(total + " Notices");
            lblUrgentCount.setText(urgent + " Urgent");

            if (!allNotices.isEmpty()) {
                lblLatestDate.setText(allNotices.get(0).getFormattedDate());
            } else {
                lblLatestDate.setText("None");
            }

            lblStatus.setText("Loaded " + total + " official notices.");
            applyFilters();
        } catch (Exception e) {
            lblStatus.setText("Error loading notices: " + e.getMessage());
        }
    }

    private void applyFilters() {
        if (noticeListPanel == null) return;
        noticeListPanel.removeAll();

        String query = txtSearch.getText().trim().toLowerCase();
        String selectedCat = (String) cmbCategory.getSelectedItem();
        String selectedPri = (String) cmbPriority.getSelectedItem();

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
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

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
}
