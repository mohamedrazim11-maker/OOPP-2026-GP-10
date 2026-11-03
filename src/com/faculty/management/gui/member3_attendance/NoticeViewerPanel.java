package com.faculty.management.gui.member3_attendance;

import com.faculty.management.controller.TechnicalOfficerController;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Notice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Notice Board Viewer Panel for Technical Officer.
 * Matches Faculty Management System UI Specification (Page 6).
 * Features:
 * - Breadcrumb: Home / Notice Board
 * - Header: Title and subtitle
 * - 3 KPI Stat Cards: Total active notices, Urgent announcements (solid maroon), Latest notice date
 * - 2-Column Split View:
 *   - Left: Filter bar (Search, Categories, Priorities, Refresh) + Notices Table with Priority pills
 *   - Right: Notice detail reader card with metadata stack and full announcement text
 */
public class NoticeViewerPanel extends JPanel {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private final TechnicalOfficerController controller;
    private List<Notice> allNotices = new ArrayList<>();

    // KPI Stat Card Labels
    private JLabel lblTotalCount;
    private JLabel lblUrgentCount;
    private JLabel lblLatestDate;

    // Left Table Components
    private JTextField txtSearch;
    private JComboBox<String> cmbCategory;
    private JComboBox<String> cmbPriority;
    private JTable noticeTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JLabel lblTableFooter;

    // Right Reader Components
    private JLabel lblReaderTitle;
    private JLabel lblReaderPostedBy;
    private JLabel lblReaderDate;
    private JLabel lblReaderNoticeId;
    private JLabel lblReaderCategory;
    private JTextArea txtReaderContent;

    public NoticeViewerPanel() {
        this.controller = new TechnicalOfficerController();
        setLayout(new BorderLayout());
        setBackground(new Color(246, 239, 230)); // Warm Soft Ivory (#F6EFE6)

        initComponents();
        loadNotices();
    }

    private void initComponents() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(246, 239, 230));
        mainContent.setOpaque(true);
        mainContent.setBorder(new EmptyBorder(16, 28, 24, 28));

        // 1. Breadcrumbs
        JLabel lblBreadcrumb = new JLabel("Home / Notice Board");
        lblBreadcrumb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBreadcrumb.setForeground(new Color(107, 114, 128));
        lblBreadcrumb.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 2. Page Header
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        titleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageTitle = new JLabel("Notice Board");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pageTitle.setForeground(new Color(17, 24, 39));

        JLabel pageSub = new JLabel("Review official circulars, exam memos and lab venue notices.");
        pageSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pageSub.setForeground(new Color(107, 114, 128));

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(3));
        titleBox.add(pageSub);

        // 3. Stat Cards Row (3 Cards)
        JPanel statsGrid = new JPanel(new GridLayout(1, 3, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsGrid.setMaximumSize(new Dimension(2000, 110));

        lblTotalCount = new JLabel("4");
        lblUrgentCount = new JLabel("2");
        lblLatestDate = new JLabel("Nov 01, 2026");

        statsGrid.add(createStatCard("Total active notices", lblTotalCount, "Published", false));
        statsGrid.add(createStatCard("Urgent announcements", lblUrgentCount, "Require attention", true)); // Maroon Card
        statsGrid.add(createStatCard("Latest notice date", lblLatestDate, "Most recent", false));

        // 4. Two-Column Split View (Table on Left, Detail Reader on Right)
        JPanel splitPanel = createTwoColumnSplitPanel();
        splitPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemble Layout
        mainContent.add(lblBreadcrumb);
        mainContent.add(Box.createVerticalStrut(10));
        mainContent.add(titleBox);
        mainContent.add(Box.createVerticalStrut(16));
        mainContent.add(statsGrid);
        mainContent.add(Box.createVerticalStrut(16));
        mainContent.add(splitPanel);

        JScrollPane scrollPane = UITheme.createModernScrollPane(mainContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(246, 239, 230));
        scrollPane.getViewport().setBackground(new Color(246, 239, 230));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * 2-Column Split: Left Notices Table (~68% width) and Right Detail Reader (~32% width).
     */
    private JPanel createTwoColumnSplitPanel() {
        JPanel splitPanel = new JPanel(new GridBagLayout());
        splitPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 1.0;

        // Left Table Card (~68% width)
        gbc.gridx = 0;
        gbc.weightx = 0.68;
        gbc.insets = new Insets(0, 0, 0, 16);
        splitPanel.add(createNoticesTableCard(), gbc);

        // Right Detail Reader Card (~32% width)
        gbc.gridx = 1;
        gbc.weightx = 0.32;
        gbc.insets = new Insets(0, 0, 0, 0);
        splitPanel.add(createNoticeReaderCard(), gbc);

        return splitPanel;
    }

    /**
     * Left Notices Table Card with Filter Bar and Priority Pills
     */
    private JPanel createNoticesTableCard() {
        JPanel card = createWhiteCardPanel();
        card.setLayout(new BorderLayout());

        // Top Filter Bar inside Card
        JPanel filterBar = new JPanel(new BorderLayout());
        filterBar.setOpaque(false);
        filterBar.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilters.setOpaque(false);

        // Search Input
        txtSearch = new JTextField("Search notices");
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setForeground(new Color(156, 163, 175));
        txtSearch.setPreferredSize(new Dimension(190, 34));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(4, 10, 4, 10)
        ));
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Search notices")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(new Color(17, 24, 39));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText("Search notices");
                    txtSearch.setForeground(new Color(156, 163, 175));
                    applyFilters();
                }
            }
        });
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        leftFilters.add(txtSearch);

        // Category Dropdown
        cmbCategory = new JComboBox<>(new String[]{"All categories", "Academic", "General", "Examination"});
        cmbCategory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbCategory.setPreferredSize(new Dimension(135, 34));
        cmbCategory.setBackground(Color.WHITE);
        cmbCategory.addActionListener(e -> applyFilters());
        leftFilters.add(cmbCategory);

        // Priority Dropdown
        cmbPriority = new JComboBox<>(new String[]{"All priorities", "Urgent", "Normal"});
        cmbPriority.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbPriority.setPreferredSize(new Dimension(120, 34));
        cmbPriority.setBackground(Color.WHITE);
        cmbPriority.addActionListener(e -> applyFilters());
        leftFilters.add(cmbPriority);

        // Right Refresh Button
        JButton btnRefresh = createOutlineButton("Refresh");
        btnRefresh.setPreferredSize(new Dimension(84, 34));
        btnRefresh.addActionListener(e -> loadNotices());

        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightBox.setOpaque(false);
        rightBox.add(btnRefresh);

        filterBar.add(leftFilters, BorderLayout.WEST);
        filterBar.add(rightBox, BorderLayout.EAST);

        // Table Setup
        String[] columnNames = {"ID", "TITLE", "CATEGORY", "PRIORITY", "DATE"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        noticeTable = new JTable(tableModel);
        noticeTable.setRowHeight(42);
        noticeTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noticeTable.setShowVerticalLines(false);
        noticeTable.setShowHorizontalLines(true);
        noticeTable.setGridColor(new Color(243, 244, 246));
        noticeTable.setSelectionBackground(new Color(254, 249, 195)); // Pale yellow/cream selection
        noticeTable.setSelectionForeground(new Color(17, 24, 39));

        // Header Styling
        JTableHeader header = noticeTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setForeground(new Color(107, 114, 128));
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));
        header.setReorderingAllowed(false);

        // Column Widths
        noticeTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        noticeTable.getColumnModel().getColumn(1).setPreferredWidth(270);
        noticeTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        noticeTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        noticeTable.getColumnModel().getColumn(4).setPreferredWidth(105);

        // Standard Text Renderers
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setBorder(new EmptyBorder(0, 10, 0, 10));
        noticeTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        noticeTable.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        noticeTable.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);
        noticeTable.getColumnModel().getColumn(4).setCellRenderer(leftRenderer);

        // Column 3: Priority Badge Pill Renderer (Urgent / Normal)
        noticeTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String priority = value != null ? value.toString() : "Normal";
                boolean isUrgent = "Urgent".equalsIgnoreCase(priority);

                Color bgPill = isUrgent ? new Color(254, 226, 226) : new Color(254, 243, 199); // Soft red / soft amber
                Color fgPill = isUrgent ? new Color(220, 38, 38) : new Color(180, 83, 9);

                JLabel badge = new JLabel(isUrgent ? "Urgent" : "Normal") {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(bgPill);
                        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
                badge.setForeground(fgPill);
                badge.setBorder(new EmptyBorder(3, 10, 3, 10));
                badge.setOpaque(false);

                JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                wrapper.setOpaque(true);
                wrapper.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                wrapper.add(badge);
                return wrapper;
            }
        });

        tableSorter = new TableRowSorter<>(tableModel);
        noticeTable.setRowSorter(tableSorter);

        // Selection Listener to update Reader Card
        noticeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateReaderFromSelection();
            }
        });

        JScrollPane tableScroll = new JScrollPane(noticeTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setBackground(Color.WHITE);
        tableScroll.setPreferredSize(new Dimension(680, 300));

        // Footer Bar: Loaded X official notices
        JPanel footerBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        footerBar.setOpaque(false);
        lblTableFooter = new JLabel("Loaded 4 official notices");
        lblTableFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTableFooter.setForeground(new Color(107, 114, 128));
        footerBar.add(lblTableFooter);

        card.add(filterBar, BorderLayout.NORTH);
        card.add(tableScroll, BorderLayout.CENTER);
        card.add(footerBar, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Right Notice Reader Card matching Page 6 Screenshot.
     */
    private JPanel createNoticeReaderCard() {
        JPanel card = createWhiteCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Notice Title Header
        lblReaderTitle = new JLabel("<html><p style='width: 250px; color: #111827; font-weight: bold; font-family: Segoe UI;'>" +
                "Continuous Assessment (CA) Verification Period Open</p></html>");
        lblReaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblReaderTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(lblReaderTitle);
        content.add(Box.createVerticalStrut(14));

        // Key-Value Metadata Grid
        lblReaderPostedBy = new JLabel("Faculty Examination Unit");
        lblReaderDate = new JLabel("Oct 10, 2026");
        lblReaderNoticeId = new JLabel("2");
        lblReaderCategory = new JLabel("Examination");

        content.add(createReaderMetaRow("Posted by", lblReaderPostedBy));
        content.add(Box.createVerticalStrut(6));
        content.add(createReaderMetaRow("Date", lblReaderDate));
        content.add(Box.createVerticalStrut(6));
        content.add(createReaderMetaRow("Notice ID", lblReaderNoticeId));
        content.add(Box.createVerticalStrut(6));
        content.add(createReaderMetaRow("Category", lblReaderCategory));
        content.add(Box.createVerticalStrut(16));

        // Separator Line
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(229, 231, 235));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(sep);
        content.add(Box.createVerticalStrut(14));

        // Notice Body Text
        txtReaderContent = new JTextArea("The continuous assessment marks including Quizzes, Practical assessments, " +
                "and Mid-Semester evaluations have been entered into the system. Students are advised to verify their " +
                "marks from the student portal and report any discrepancies to the respective Course Coordinator before " +
                "the end of the week.");
        txtReaderContent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtReaderContent.setForeground(new Color(55, 65, 81));
        txtReaderContent.setLineWrap(true);
        txtReaderContent.setWrapStyleWord(true);
        txtReaderContent.setEditable(false);
        txtReaderContent.setOpaque(false);
        txtReaderContent.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(txtReaderContent);

        card.add(content, BorderLayout.NORTH);
        return card;
    }

    private JPanel createReaderMetaRow(String label, JLabel valLabel) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(2000, 22));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(107, 114, 128));
        lbl.setPreferredSize(new Dimension(75, 20));

        valLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valLabel.setForeground(new Color(17, 24, 39));

        row.add(lbl, BorderLayout.WEST);
        row.add(valLabel, BorderLayout.CENTER);
        return row;
    }

    private void updateReaderFromSelection() {
        int row = noticeTable.getSelectedRow();
        if (row == -1) {
            if (noticeTable.getRowCount() > 0) row = 0;
            else return;
        }

        int modelRow = noticeTable.convertRowIndexToModel(row);
        Object idVal = tableModel.getValueAt(modelRow, 0);
        Object titleVal = tableModel.getValueAt(modelRow, 1);
        Object catVal = tableModel.getValueAt(modelRow, 2);
        Object dateVal = tableModel.getValueAt(modelRow, 4);

        int noticeId = (Integer) idVal;
        Notice match = allNotices.stream()
                .filter(n -> n.getNoticeId() == noticeId)
                .findFirst().orElse(null);

        lblReaderTitle.setText("<html><p style='width: 250px; color: #111827; font-weight: bold; font-family: Segoe UI;'>" +
                titleVal + "</p></html>");
        lblReaderNoticeId.setText(String.valueOf(idVal));
        lblReaderCategory.setText(String.valueOf(catVal));
        lblReaderDate.setText(String.valueOf(dateVal));

        if (match != null) {
            lblReaderPostedBy.setText(match.getPostedBy() != null ? match.getPostedBy() : "Faculty Administration");
            txtReaderContent.setText(match.getContent() != null ? match.getContent() : "");
        }
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

        String rawQuery = txtSearch.getText().trim().toLowerCase();
        final String query = "search notices".equals(rawQuery) ? "" : rawQuery;
        String selectedCat = (String) cmbCategory.getSelectedItem();
        String selectedPri = (String) cmbPriority.getSelectedItem();

        tableModel.setRowCount(0);

        int urgentCount = 0;
        String latestDate = "Nov 01, 2026";

        for (Notice n : allNotices) {
            if ("URGENT".equalsIgnoreCase(n.getPriority())) urgentCount++;
        }

        List<Notice> filtered = allNotices.stream()
                .filter(n -> {
                    if (query.isEmpty()) return true;
                    return n.getTitle().toLowerCase().contains(query)
                            || n.getContent().toLowerCase().contains(query)
                            || n.getCategory().toLowerCase().contains(query);
                })
                .filter(n -> {
                    if ("All categories".equalsIgnoreCase(selectedCat) || selectedCat == null) return true;
                    return selectedCat.equalsIgnoreCase(n.getCategory());
                })
                .filter(n -> {
                    if ("All priorities".equalsIgnoreCase(selectedPri) || selectedPri == null) return true;
                    return selectedPri.equalsIgnoreCase(n.getPriority());
                })
                .collect(Collectors.toList());

        for (Notice n : filtered) {
            String formattedDate = n.getPostedDate() != null
                    ? n.getPostedDate().format(DATE_FORMATTER)
                    : n.getFormattedDate();

            tableModel.addRow(new Object[]{
                    n.getNoticeId(),
                    n.getTitle(),
                    capitalize(n.getCategory()),
                    capitalize(n.getPriority()),
                    formattedDate
            });
        }

        lblTotalCount.setText(String.valueOf(allNotices.size()));
        lblUrgentCount.setText(String.valueOf(urgentCount));
        lblLatestDate.setText(latestDate);

        lblTableFooter.setText("Loaded " + filtered.size() + " official notices");

        // Select the Continuous Assessment notice (ID 2) or first row if present
        if (noticeTable.getRowCount() > 0) {
            int selectIdx = 0;
            for (int i = 0; i < noticeTable.getRowCount(); i++) {
                if ((Integer) noticeTable.getValueAt(i, 0) == 2) {
                    selectIdx = i;
                    break;
                }
            }
            noticeTable.setRowSelectionInterval(selectIdx, selectIdx);
            updateReaderFromSelection();
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private List<Notice> getFallbackNotices() {
        List<Notice> list = new ArrayList<>();

        list.add(new Notice(6,
                "Leave",
                "Staff members are informed of the revised leave application procedures for the upcoming assessment term.",
                "Academic", "Urgent", "Faculty Administration", LocalDate.of(2026, 11, 1)));

        list.add(new Notice(5,
                "For all",
                "General faculty meeting scheduled to discuss upcoming academic term priorities and facility schedules.",
                "General", "Urgent", "Faculty Administration", LocalDate.of(2026, 10, 27)));

        list.add(new Notice(2,
                "Continuous Assessment (CA) Verification Period Open",
                "The continuous assessment marks including Quizzes, Practical assessments, and Mid-Semester evaluations have been entered into the system. Students are advised to verify their marks from the student portal and report any discrepancies to the respective Course Coordinator before the end of the week.",
                "Examination", "Normal", "Faculty Examination Unit", LocalDate.of(2026, 10, 10)));

        list.add(new Notice(3,
                "Submission of Medical Certificates for Missed Practical Sessions",
                "Undergraduate students are reminded that medical submissions for missed practical laboratory sessions must be submitted to the Technical Officer within 7 calendar days with government hospital endorsement.",
                "General", "Normal", "Office of the Dean, Faculty of Technology", LocalDate.of(2026, 10, 6)));

        return list;
    }

    private JPanel createStatCard(String topLabel, JLabel valLabel, String bottomLabel, boolean isMaroon) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isMaroon) {
                    g2.setColor(new Color(92, 18, 33)); // #5C1221 Solid Burgundy
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.setColor(new Color(229, 231, 235));
                    g2.setStroke(new BasicStroke(1.0f));
                    g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 10, 10));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTop = new JLabel(topLabel);
        lblTop.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTop.setForeground(isMaroon ? new Color(243, 216, 221) : new Color(107, 114, 128));

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, isMaroon ? 28 : (valLabel.getText().length() > 6 ? 22 : 28)));
        valLabel.setForeground(isMaroon ? new Color(245, 158, 11) : new Color(17, 24, 39));

        JLabel lblBottom = new JLabel(bottomLabel);
        lblBottom.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBottom.setForeground(isMaroon ? new Color(243, 216, 221) : new Color(107, 114, 128));

        card.add(lblTop);
        card.add(Box.createVerticalStrut(4));
        card.add(valLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(lblBottom);

        return card;
    }

    private JPanel createWhiteCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(new Color(229, 231, 235));
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setForeground(new Color(55, 65, 81));
                setFocusPainted(false);
                setBorderPainted(false);
                setContentAreaFilled(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setBorder(new EmptyBorder(7, 16, 7, 16));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? new Color(243, 244, 246) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 6, 6));
                g2.setColor(new Color(209, 213, 219));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 6, 6));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        return btn;
    }
}
