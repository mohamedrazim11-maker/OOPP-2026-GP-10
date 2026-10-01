package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.exception.DatabaseException;
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
 * Modern Faculty Notice Board for Undergraduates.
 * Displays official academic announcements, examination guidelines,
 * timetable updates, and faculty circulars with search and category filtering.
 */
public class StudentNoticePanel extends JPanel {

    private final UndergraduateController controller;

    private List<Notice> allNotices = new ArrayList<>();
    private String selectedCategory = "ALL";
    private String searchQuery = "";

    // KPI Card Labels
    private JLabel totalNoticesLbl;
    private JLabel urgentNoticesLbl;
    private JLabel examNoticesLbl;
    private JLabel latestDateLbl;

    // Filter Chips
    private JPanel filterChipsPanel;
    private UITheme.ModernTextField searchField;
    private JPanel noticeCardsContainer;
    private JLabel statusLabel;

    public StudentNoticePanel(UndergraduateController controller, int studentId) {
        this.controller = controller;

        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        buildUi();
        loadNotices();
    }

    private void buildUi() {
        // -------------------------------------------------------------
        // 1. TOP HEADER & STATS
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

        JLabel heading = new JLabel("Faculty Notice Board & Circulars");
        heading.setFont(UITheme.FONT_PAGE_TITLE);
        heading.setForeground(UITheme.TEXT_MAIN);

        JLabel subheading = new JLabel("Official announcements, exam eligibility guidelines, timetable memos, and faculty notices.");
        subheading.setFont(UITheme.FONT_BODY);
        subheading.setForeground(UITheme.TEXT_MUTED);

        titleBox.add(heading);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(subheading);

        UITheme.ModernButton refreshBtn = new UITheme.ModernButton("🔄  Refresh Notices", UITheme.ButtonStyle.OUTLINE, 8);
        refreshBtn.setFont(UITheme.FONT_SMALL_BOLD);
        refreshBtn.addActionListener(e -> loadNotices());

        headerBar.add(titleBox, BorderLayout.WEST);
        headerBar.add(refreshBtn, BorderLayout.EAST);

        // KPI Stat Cards
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.setBorder(new EmptyBorder(0, 0, 16, 0));

        kpiGrid.add(createKpiCard("📢", "Active Notices", "0 Notices", UITheme.PRIMARY));
        kpiGrid.add(createKpiCard("🚨", "Urgent Notices", "0 Urgent", UITheme.DANGER_DARK));
        kpiGrid.add(createKpiCard("📝", "Exam Circulars", "0 Exams", UITheme.PURPLE));
        kpiGrid.add(createKpiCard("📅", "Latest Update", "Today", UITheme.SUCCESS_DARK));

        topContainer.add(headerBar);
        topContainer.add(kpiGrid);

        // -------------------------------------------------------------
        // 2. SEARCH & CATEGORY FILTER CARD
        // -------------------------------------------------------------
        UITheme.ModernCard filterCard = new UITheme.ModernCard(12);
        filterCard.setLayout(new BorderLayout(14, 0));
        filterCard.setBorder(new EmptyBorder(10, 16, 10, 16));

        // Category Filter Buttons
        filterChipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterChipsPanel.setOpaque(false);

        filterChipsPanel.add(createCategoryButton("ALL", "All Announcements"));
        filterChipsPanel.add(createCategoryButton("EXAMINATION", "Examinations"));
        filterChipsPanel.add(createCategoryButton("ACADEMIC", "Academic Affairs"));
        filterChipsPanel.add(createCategoryButton("TIMETABLE", "Timetable"));
        filterChipsPanel.add(createCategoryButton("GENERAL", "General"));

        // Search Field
        searchField = new UITheme.ModernTextField("🔍  Search announcements by title or content...", 22);
        searchField.setPreferredSize(new Dimension(320, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { onSearchChanged(); }
            @Override
            public void removeUpdate(DocumentEvent e) { onSearchChanged(); }
            @Override
            public void changedUpdate(DocumentEvent e) { onSearchChanged(); }
        });

        filterCard.add(filterChipsPanel, BorderLayout.WEST);
        filterCard.add(searchField, BorderLayout.EAST);

        topContainer.add(filterCard);
        add(topContainer, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 3. NOTICE CARDS LIST (SCROLLABLE)
        // -------------------------------------------------------------
        noticeCardsContainer = new JPanel();
        noticeCardsContainer.setLayout(new BoxLayout(noticeCardsContainer, BoxLayout.Y_AXIS));
        noticeCardsContainer.setOpaque(false);
        noticeCardsContainer.setBorder(new EmptyBorder(4, 0, 10, 0));

        JScrollPane scrollPane = UITheme.createModernScrollPane(noticeCardsContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Status Label (Bottom)
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setBorder(new EmptyBorder(6, 4, 0, 0));
        add(statusLabel, BorderLayout.SOUTH);
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

        if (title.contains("Active")) totalNoticesLbl = val;
        else if (title.contains("Urgent")) urgentNoticesLbl = val;
        else if (title.contains("Exam")) examNoticesLbl = val;
        else if (title.contains("Latest")) latestDateLbl = val;

        card.add(top, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    private JButton createCategoryButton(String categoryKey, String displayName) {
        JButton btn = new JButton(displayName);
        btn.setFont(UITheme.FONT_SMALL_BOLD);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

        updateCategoryButtonAppearance(btn, categoryKey.equalsIgnoreCase(selectedCategory));

        btn.addActionListener(e -> {
            selectedCategory = categoryKey;
            for (Component c : filterChipsPanel.getComponents()) {
                if (c instanceof JButton) {
                    JButton otherBtn = (JButton) c;
                    boolean isThis = otherBtn.getText().equalsIgnoreCase(displayName);
                    updateCategoryButtonAppearance(otherBtn, isThis);
                }
            }
            renderNotices();
        });

        return btn;
    }

    private void updateCategoryButtonAppearance(JButton btn, boolean isSelected) {
        if (isSelected) {
            btn.setBackground(UITheme.PRIMARY);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(UITheme.TEXT_BODY);
        }
    }

    private void onSearchChanged() {
        searchQuery = searchField.getText().trim().toLowerCase();
        renderNotices();
    }

    public void loadNotices() {
        try {
            allNotices = controller.getNotices();
            updateKpiMetrics();
            renderNotices();
            statusLabel.setForeground(UITheme.TEXT_MUTED);
            statusLabel.setText("Loaded " + allNotices.size() + " official faculty notices.");
        } catch (DatabaseException e) {
            statusLabel.setForeground(UITheme.DANGER_DARK);
            statusLabel.setText("Failed to load notices: " + e.getMessage());
        }
    }

    private void updateKpiMetrics() {
        int total = allNotices.size();
        int urgent = 0;
        int exam = 0;

        for (Notice n : allNotices) {
            if ("URGENT".equalsIgnoreCase(n.getPriority())) urgent++;
            if ("EXAMINATION".equalsIgnoreCase(n.getCategory())) exam++;
        }

        totalNoticesLbl.setText(total + " Notices");
        urgentNoticesLbl.setText(urgent + " Urgent");
        examNoticesLbl.setText(exam + " Exams");

        if (!allNotices.isEmpty()) {
            latestDateLbl.setText(allNotices.get(0).getFormattedDate());
        }
    }

    private void renderNotices() {
        noticeCardsContainer.removeAll();

        List<Notice> filtered = allNotices.stream()
                .filter(n -> selectedCategory.equalsIgnoreCase("ALL") || n.getCategory().equalsIgnoreCase(selectedCategory))
                .filter(n -> searchQuery.isEmpty() ||
                        n.getTitle().toLowerCase().contains(searchQuery) ||
                        n.getContent().toLowerCase().contains(searchQuery) ||
                        n.getPostedBy().toLowerCase().contains(searchQuery))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setOpaque(false);
            emptyPanel.setBorder(new EmptyBorder(60, 20, 60, 20));

            JLabel emptyIcon = new JLabel("📭");
            emptyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emptyLabel = new JLabel("No notices match your selected category or filter criteria.");
            emptyLabel.setFont(UITheme.FONT_CARD_TITLE);
            emptyLabel.setForeground(UITheme.TEXT_MUTED);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyPanel.add(emptyIcon);
            emptyPanel.add(Box.createVerticalStrut(10));
            emptyPanel.add(emptyLabel);
            noticeCardsContainer.add(emptyPanel);
        } else {
            for (Notice notice : filtered) {
                noticeCardsContainer.add(createNoticeCard(notice));
                noticeCardsContainer.add(Box.createVerticalStrut(12));
            }
        }

        noticeCardsContainer.revalidate();
        noticeCardsContainer.repaint();
    }

    private JPanel createNoticeCard(Notice notice) {
        UITheme.ModernCard card = new UITheme.ModernCard(12);
        card.setLayout(new BorderLayout(14, 10));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Top Metadata Strip
        JPanel topMeta = new JPanel(new BorderLayout());
        topMeta.setOpaque(false);

        JPanel badgesLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        badgesLeft.setOpaque(false);

        // Priority Badge
        UITheme.PillBadge priorityBadge;
        if ("URGENT".equalsIgnoreCase(notice.getPriority())) {
            priorityBadge = new UITheme.PillBadge("🚨 URGENT", UITheme.DANGER_LIGHT, UITheme.DANGER_DARK, UITheme.DANGER_BORDER);
        } else if ("INFO".equalsIgnoreCase(notice.getPriority())) {
            priorityBadge = new UITheme.PillBadge("ℹ️ INFO", UITheme.TEAL_LIGHT, UITheme.TEAL, new Color(153, 246, 228));
        } else {
            priorityBadge = new UITheme.PillBadge("📢 NOTICE", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, UITheme.PRIMARY_TINT);
        }

        // Category Badge
        UITheme.PillBadge categoryBadge = new UITheme.PillBadge(
                notice.getCategory().toUpperCase(),
                new Color(241, 245, 249),
                UITheme.TEXT_BODY,
                UITheme.BORDER_LIGHT
        );

        badgesLeft.add(priorityBadge);
        badgesLeft.add(categoryBadge);

        // Date & Department Label (Right)
        JLabel dateLabel = new JLabel("🗓️  " + notice.getFormattedDate() + " • " + notice.getPostedBy());
        dateLabel.setFont(UITheme.FONT_SMALL);
        dateLabel.setForeground(UITheme.TEXT_MUTED);

        topMeta.add(badgesLeft, BorderLayout.WEST);
        topMeta.add(dateLabel, BorderLayout.EAST);

        // Center Content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(6, 0, 4, 0));

        JLabel titleLabel = new JLabel(notice.getTitle());
        titleLabel.setFont(UITheme.FONT_CARD_TITLE);
        titleLabel.setForeground(UITheme.TEXT_MAIN);

        // Snippet
        String snippetText = notice.getContent();
        if (snippetText.length() > 220) {
            snippetText = snippetText.substring(0, 217) + "...";
        }
        JLabel contentLabel = new JLabel("<html><p style='width: 720px; line-height: 1.4; color: #475569;'>" + snippetText + "</p></html>");
        contentLabel.setFont(UITheme.FONT_BODY);
        contentLabel.setBorder(new EmptyBorder(6, 0, 0, 0));

        centerPanel.add(titleLabel);
        centerPanel.add(contentLabel);

        // Bottom Action Bar
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);

        JLabel deptLabel = new JLabel("🏛️ " + notice.getPostedBy());
        deptLabel.setFont(UITheme.FONT_SMALL_BOLD);
        deptLabel.setForeground(UITheme.PRIMARY);

        UITheme.ModernButton readMoreBtn = new UITheme.ModernButton("Read Full Announcement →", UITheme.ButtonStyle.OUTLINE, 8);
        readMoreBtn.setFont(UITheme.FONT_SMALL_BOLD);
        readMoreBtn.addActionListener(e -> showNoticeDetailDialog(notice));

        bottomBar.add(deptLabel, BorderLayout.WEST);
        bottomBar.add(readMoreBtn, BorderLayout.EAST);

        card.add(topMeta, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(bottomBar, BorderLayout.SOUTH);

        return card;
    }

    private void showNoticeDetailDialog(Notice notice) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Faculty Official Notice", true);
        dialog.setSize(620, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Badges
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        badgeRow.setOpaque(false);
        UITheme.PillBadge catBadge = new UITheme.PillBadge(notice.getCategory(), UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, UITheme.PRIMARY_TINT);
        UITheme.PillBadge prioBadge = new UITheme.PillBadge(notice.getPriority(), UITheme.WARNING_LIGHT, UITheme.WARNING_DARK, UITheme.WARNING_BORDER);
        badgeRow.add(catBadge);
        badgeRow.add(prioBadge);

        // Title
        JLabel title = new JLabel("<html><h3 style='margin: 8px 0 0 0; color: #0F172A;'>" + notice.getTitle() + "</h3></html>");
        title.setFont(UITheme.FONT_PAGE_TITLE);

        // Meta
        JLabel meta = new JLabel("Issued on " + notice.getFormattedDate() + " by " + notice.getPostedBy());
        meta.setFont(UITheme.FONT_SMALL);
        meta.setForeground(UITheme.TEXT_MUTED);
        meta.setBorder(new EmptyBorder(4, 0, 14, 0));

        // Notice Full Content
        JTextArea fullTextArea = new JTextArea(notice.getContent());
        fullTextArea.setFont(UITheme.FONT_BODY);
        fullTextArea.setForeground(UITheme.TEXT_MAIN);
        fullTextArea.setLineWrap(true);
        fullTextArea.setWrapStyleWord(true);
        fullTextArea.setEditable(false);
        fullTextArea.setBackground(UITheme.BG_PAGE);
        fullTextArea.setBorder(new EmptyBorder(14, 16, 14, 16));

        JScrollPane textScroll = UITheme.createModernScrollPane(fullTextArea);

        contentPanel.add(badgeRow);
        contentPanel.add(title);
        contentPanel.add(meta);
        contentPanel.add(textScroll);

        // Bottom Close Bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonBar.setBackground(new Color(248, 250, 252));
        buttonBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT));

        UITheme.ModernButton closeBtn = new UITheme.ModernButton("Close Notice", UITheme.ButtonStyle.PRIMARY, 8);
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonBar.add(closeBtn);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonBar, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
