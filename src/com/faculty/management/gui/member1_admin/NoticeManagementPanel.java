package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.exception.DatabaseException;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.faculty.management.gui.member1_admin.AdminUIComponents.*;

/**
 * Notice Board Management Panel for Member 1 (Administrator).
 * Strictly implements the UI/UX design from Page 4 of the FMS Specification.
 *
 * Demonstrates Core OOP Principles:
 * - Classes & Objects: Encapsulation of notice entity management, table models, and controllers.
 * - Inheritance: Extends JPanel to integrate into Swing container hierarchy.
 * - Abstraction: Database operations abstracted through AdminController and NoticeService.
 * - Polymorphism: Custom TableCellRenderers for Category and Priority badges.
 * - Encapsulation: Private member variables with controlled public accessors.
 * - Error & Exception Handling: Catches DatabaseException with user-friendly dialogs.
 * - Database Handling: Direct backend persistence to MySQL.
 */
public class NoticeManagementPanel extends JPanel {

    private final AdminController adminController;
    private List<Notice> allNotices = new ArrayList<>();
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTable noticeTable;

    // Filters & summary
    private SearchTextField searchField;
    private JComboBox<String> categoryFilterBox;
    private JLabel publishedCountLabel;

    // Right Sidebar Detail Components
    private JPanel noticeDetailCard;
    private JPanel badgesPanel;
    private PillBadge detailCategoryBadge;
    private PillBadge detailPriorityBadge;
    private JLabel detailTitleLabel;
    private JLabel detailPostedByVal;
    private JLabel detailDateVal;
    private JLabel detailNoticeIdVal;
    private JTextArea detailBodyArea;

    // Action Buttons
    private WhiteOutlineButton viewNoticeBtn;
    private WhiteOutlineButton editNoticeBtn;
    private DangerOutlineButton deleteNoticeBtn;

    // Currently selected notice reference
    private Notice selectedNotice;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public NoticeManagementPanel() {
        this(new AdminController());
    }

    public NoticeManagementPanel(AdminController adminController) {
        this.adminController = (adminController != null) ? adminController : new AdminController();
        initComponents();
        loadNotices();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(COLOR_PAGE_BG);
        setBorder(new EmptyBorder(0, 28, 20, 28));

        // 1. Header: Title, Subtitle, and "+ Post notice" action button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("Notice Board");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(COLOR_TEXT_DARK);

        JLabel pageSub = new JLabel("Publish and maintain official announcements.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        BurgundyButton postNoticeBtn = new BurgundyButton("+ Post notice");
        postNoticeBtn.setPreferredSize(new Dimension(140, 38));
        postNoticeBtn.addActionListener(e -> handleCreateNoticeAction());

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(postNoticeBtn, BorderLayout.EAST);

        // 2. Main Content Split: Left (Table + Filters), Right (Notice Detail & Action Card)
        JPanel mainContent = new JPanel(new BorderLayout(24, 0));
        mainContent.setOpaque(false);

        JPanel leftPanel = createLeftTablePanel();
        JPanel rightPanel = createRightDetailCard();

        mainContent.add(leftPanel, BorderLayout.CENTER);
        mainContent.add(rightPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
    }

    /**
     * Builds the left area containing the search bar, category dropdown, table, and count.
     */
    private JPanel createLeftTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        // Filter Toolbar
        JPanel filterBar = new JPanel(new BorderLayout(10, 0));
        filterBar.setOpaque(false);

        searchField = new SearchTextField("Search notices", 20);
        searchField.setPreferredSize(new Dimension(240, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        String[] categories = {"All categories", "Academic", "General", "Examination", "Timetable"};
        categoryFilterBox = new JComboBox<>(categories);
        styleComboBox(categoryFilterBox);
        categoryFilterBox.setPreferredSize(new Dimension(140, 36));
        categoryFilterBox.addActionListener(e -> applyFilters());

        publishedCountLabel = new JLabel("0 published");
        publishedCountLabel.setFont(FONT_BODY);
        publishedCountLabel.setForeground(COLOR_TEXT_MUTED);

        WhiteOutlineButton refreshBtn = createRefreshButton();
        refreshBtn.setPreferredSize(new Dimension(100, 36));
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            categoryFilterBox.setSelectedIndex(0);
            loadNotices();
        });

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftFilters.setOpaque(false);
        leftFilters.add(searchField);
        leftFilters.add(categoryFilterBox);

        JPanel rightFilters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightFilters.setOpaque(false);
        rightFilters.add(publishedCountLabel);
        rightFilters.add(refreshBtn);

        filterBar.add(leftFilters, BorderLayout.WEST);
        filterBar.add(rightFilters, BorderLayout.EAST);

        // Notice Table
        String[] columns = {"ID", "TITLE", "CATEGORY", "PRIORITY", "DATE", "_DATA_"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        noticeTable = new JTable(tableModel);
        noticeTable.setRowHeight(44);
        noticeTable.setShowVerticalLines(false);
        noticeTable.setGridColor(new Color(243, 244, 246));
        noticeTable.setSelectionBackground(COLOR_ROW_SELECTED_BG);
        noticeTable.setSelectionForeground(COLOR_TEXT_DARK);
        noticeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        noticeTable.setFont(FONT_BODY);

        // Header Styling
        JTableHeader th = noticeTable.getTableHeader();
        th.setFont(FONT_SMALL_BOLD);
        th.setForeground(COLOR_TEXT_MUTED);
        th.setBackground(Color.WHITE);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Column Renderers
        noticeTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        noticeTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setForeground(COLOR_TEXT_MUTED);
                setFont(FONT_BODY);
                setBorder(new EmptyBorder(0, 12, 0, 0));
                return c;
            }
        });

        noticeTable.getColumnModel().getColumn(1).setPreferredWidth(270);
        noticeTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setFont(FONT_BODY_BOLD);
                setForeground(COLOR_TEXT_DARK);
                return c;
            }
        });

        noticeTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        noticeTable.getColumnModel().getColumn(2).setCellRenderer(new CategoryBadgeCellRenderer());

        noticeTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        noticeTable.getColumnModel().getColumn(3).setCellRenderer(new PriorityBadgeCellRenderer());

        noticeTable.getColumnModel().getColumn(4).setPreferredWidth(105);
        noticeTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setFont(FONT_BODY);
                setForeground(COLOR_TEXT_MUTED);
                return c;
            }
        });

        // Hide notice reference column
        noticeTable.getColumnModel().getColumn(5).setMinWidth(0);
        noticeTable.getColumnModel().getColumn(5).setMaxWidth(0);
        noticeTable.getColumnModel().getColumn(5).setPreferredWidth(0);

        // Row Sorter
        rowSorter = new TableRowSorter<>(tableModel);
        noticeTable.setRowSorter(rowSorter);

        // Selection Listener
        noticeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailCardFromSelection();
            }
        });

        // Double click to view notice
        noticeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && selectedNotice != null) {
                    handleViewNoticeAction();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(noticeTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));

        panel.add(filterBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Builds the right-hand Notice Detail Card matching Page 4 of the PDF.
     */
    private JPanel createRightDetailCard() {
        noticeDetailCard = new CardPanel();
        noticeDetailCard.setPreferredSize(new Dimension(320, 520));
        noticeDetailCard.setLayout(new BorderLayout());
        noticeDetailCard.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel contentBox = new JPanel();
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setOpaque(false);

        // Badges Row (Category + Priority)
        badgesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        badgesPanel.setOpaque(false);
        badgesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailCategoryBadge = new PillBadge("Examination", new Color(237, 233, 254), new Color(109, 40, 217));
        detailPriorityBadge = new PillBadge("Normal", new Color(243, 244, 246), new Color(75, 85, 99));

        badgesPanel.add(detailCategoryBadge);
        badgesPanel.add(detailPriorityBadge);

        contentBox.add(badgesPanel);
        contentBox.add(Box.createVerticalStrut(14));

        // Notice Title
        detailTitleLabel = new JLabel("<html><body style='width: 250px;'>Continuous Assessment (CA) Verification Period Open</body></html>");
        detailTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        detailTitleLabel.setForeground(COLOR_TEXT_DARK);
        detailTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentBox.add(detailTitleLabel);
        contentBox.add(Box.createVerticalStrut(16));

        // Notice Meta (Posted by, Date, Notice ID)
        JPanel metaGrid = new JPanel(new GridLayout(3, 2, 8, 10));
        metaGrid.setOpaque(false);
        metaGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        metaGrid.add(createDetailLabel("Posted by"));
        detailPostedByVal = createDetailValLabel("Faculty Examination Unit");
        metaGrid.add(detailPostedByVal);

        metaGrid.add(createDetailLabel("Date"));
        detailDateVal = createDetailValLabel("Oct 10, 2026");
        metaGrid.add(detailDateVal);

        metaGrid.add(createDetailLabel("Notice ID"));
        detailNoticeIdVal = createDetailValLabel("2");
        metaGrid.add(detailNoticeIdVal);

        contentBox.add(metaGrid);
        contentBox.add(Box.createVerticalStrut(16));

        // Notice Content Body preview
        detailBodyArea = new JTextArea("[Notice body appears here]");
        detailBodyArea.setFont(FONT_BODY);
        detailBodyArea.setForeground(COLOR_TEXT_BODY);
        detailBodyArea.setBackground(new Color(249, 250, 251));
        detailBodyArea.setLineWrap(true);
        detailBodyArea.setWrapStyleWord(true);
        detailBodyArea.setEditable(false);
        detailBodyArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane bodyScroll = new JScrollPane(detailBodyArea);
        bodyScroll.setBorder(null);
        bodyScroll.setPreferredSize(new Dimension(270, 140));
        bodyScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentBox.add(bodyScroll);
        contentBox.add(Box.createVerticalGlue());

        // 3 Action Buttons at bottom (View notice, Edit notice, Delete)
        JPanel actionRow = new JPanel(new GridLayout(1, 3, 8, 0));
        actionRow.setOpaque(false);
        actionRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        viewNoticeBtn = new WhiteOutlineButton("View\nnotice");
        viewNoticeBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        viewNoticeBtn.setText("<html><center>View<br>notice</center></html>");
        viewNoticeBtn.addActionListener(e -> handleViewNoticeAction());

        editNoticeBtn = new WhiteOutlineButton("Edit\nnotice");
        editNoticeBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        editNoticeBtn.setText("<html><center>Edit<br>notice</center></html>");
        editNoticeBtn.addActionListener(e -> handleEditNoticeAction());

        deleteNoticeBtn = new DangerOutlineButton("Delete");
        deleteNoticeBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        deleteNoticeBtn.addActionListener(e -> handleDeleteNoticeAction());

        actionRow.add(viewNoticeBtn);
        actionRow.add(editNoticeBtn);
        actionRow.add(deleteNoticeBtn);

        contentBox.add(Box.createVerticalStrut(16));
        contentBox.add(actionRow);

        noticeDetailCard.add(contentBox, BorderLayout.CENTER);
        return noticeDetailCard;
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

    /**
     * Loads notices from database.
     */
    public void loadNotices() {
        try {
            allNotices = adminController.loadAllNotices();
            tableModel.setRowCount(0);

            if (allNotices != null) {
                for (Notice n : allNotices) {
                    String dateStr = n.getPostedDate() != null ? n.getPostedDate().format(DATE_FORMATTER) : "—";
                    String cat = n.getCategory() != null ? toTitleCase(n.getCategory()) : "General";
                    String pri = n.getPriority() != null ? toTitleCase(n.getPriority()) : "Normal";
                    tableModel.addRow(new Object[]{
                            n.getNoticeId(),
                            n.getTitle(),
                            cat,
                            pri,
                            dateStr,
                            n
                    });
                }
            }

            applyFilters();

            if (noticeTable.getRowCount() > 0) {
                noticeTable.setRowSelectionInterval(0, 0);
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load notices from database: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Filters notice table according to search text and category selection.
     */
    private void applyFilters() {
        String query = searchField.getText().trim().toLowerCase();
        String selectedCategory = (String) categoryFilterBox.getSelectedItem();

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                Notice n = (Notice) entry.getValue(5);
                if (n == null) return false;

                // Category filter check
                if (selectedCategory != null && !selectedCategory.equals("All categories")) {
                    String cat = n.getCategory() != null ? n.getCategory() : "General";
                    if (!cat.equalsIgnoreCase(selectedCategory)) {
                        return false;
                    }
                }

                // Query search check (matches title or content)
                if (!query.isEmpty()) {
                    boolean titleMatch = n.getTitle() != null && n.getTitle().toLowerCase().contains(query);
                    boolean contentMatch = n.getContent() != null && n.getContent().toLowerCase().contains(query);
                    if (!titleMatch && !contentMatch) {
                        return false;
                    }
                }
                return true;
            }
        };

        rowSorter.setRowFilter(rf);
        int visibleCount = noticeTable.getRowCount();
        publishedCountLabel.setText(visibleCount + " published");

        if (visibleCount > 0 && noticeTable.getSelectedRow() == -1) {
            noticeTable.setRowSelectionInterval(0, 0);
        }
    }

    /**
     * Updates the right-hand Notice Detail Card from currently selected row.
     */
    private void updateDetailCardFromSelection() {
        int viewRow = noticeTable.getSelectedRow();
        if (viewRow == -1) {
            selectedNotice = null;
            detailTitleLabel.setText("No Notice Selected");
            detailPostedByVal.setText("-");
            detailDateVal.setText("-");
            detailNoticeIdVal.setText("-");
            detailBodyArea.setText("");
            return;
        }

        int modelRow = noticeTable.convertRowIndexToModel(viewRow);
        selectedNotice = (Notice) tableModel.getValueAt(modelRow, 5);

        if (selectedNotice != null) {
            detailTitleLabel.setText("<html><body style='width: 250px;'>" + selectedNotice.getTitle() + "</body></html>");
            detailPostedByVal.setText(selectedNotice.getPostedBy() != null ? selectedNotice.getPostedBy() : "Faculty Administration");
            detailDateVal.setText(selectedNotice.getPostedDate() != null ? selectedNotice.getPostedDate().format(DATE_FORMATTER) : "—");
            detailNoticeIdVal.setText(String.valueOf(selectedNotice.getNoticeId()));
            detailBodyArea.setText(selectedNotice.getContent() != null ? selectedNotice.getContent() : "[No content available]");
            detailBodyArea.setCaretPosition(0);

            String cat = selectedNotice.getCategory() != null ? toTitleCase(selectedNotice.getCategory()) : "General";
            detailCategoryBadge.setText(cat);
            applyCategoryBadgeStyle(detailCategoryBadge, cat);

            String pri = selectedNotice.getPriority() != null ? toTitleCase(selectedNotice.getPriority()) : "Normal";
            detailPriorityBadge.setText(pri);
            applyPriorityBadgeStyle(detailPriorityBadge, pri);
        }
    }

    private static void applyCategoryBadgeStyle(PillBadge badge, String category) {
        if ("Examination".equalsIgnoreCase(category)) {
            badge.setBackground(COLOR_GOLD_BG_LIGHT);
            badge.setForeground(COLOR_GOLD_TEXT);
        } else if ("Academic".equalsIgnoreCase(category)) {
            badge.setBackground(COLOR_GOLD_BG_LIGHT);
            badge.setForeground(COLOR_GOLD_TEXT);
        } else if ("Timetable".equalsIgnoreCase(category)) {
            badge.setBackground(COLOR_GOLD_BG_LIGHT);
            badge.setForeground(new Color(180, 83, 9));
        } else {
            badge.setBackground(COLOR_GOLD_BG_LIGHT);
            badge.setForeground(COLOR_GOLD_TEXT);
        }
    }

    private static void applyPriorityBadgeStyle(PillBadge badge, String priority) {
        if ("Urgent".equalsIgnoreCase(priority) || "High".equalsIgnoreCase(priority)) {
            badge.setBackground(new Color(254, 226, 226));
            badge.setForeground(new Color(220, 38, 38));
        } else {
            badge.setBackground(COLOR_GOLD_BG_LIGHT);
            badge.setForeground(COLOR_GOLD_TEXT);
        }
    }

    // =========================================================================
    // ACTION HANDLERS
    // =========================================================================

    private void handleCreateNoticeAction() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        NoticeFormDialog dialog = new NoticeFormDialog(parentFrame, adminController, NoticeFormDialog.FormMode.ADD);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadNotices();
            JOptionPane.showMessageDialog(this, "Notice published successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleEditNoticeAction() {
        if (selectedNotice == null) {
            JOptionPane.showMessageDialog(this, "Please select a notice to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        NoticeFormDialog dialog = new NoticeFormDialog(parentFrame, adminController, NoticeFormDialog.FormMode.UPDATE, selectedNotice);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadNotices();
            JOptionPane.showMessageDialog(this, "Notice updated successfully!", "Update Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleViewNoticeAction() {
        if (selectedNotice == null) {
            JOptionPane.showMessageDialog(this, "Please select a notice to view.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        NoticeFormDialog dialog = new NoticeFormDialog(parentFrame, adminController, NoticeFormDialog.FormMode.VIEW, selectedNotice);
        dialog.setVisible(true);
    }

    private void handleDeleteNoticeAction() {
        if (selectedNotice == null) {
            JOptionPane.showMessageDialog(this, "Please select a notice to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete notice '" + selectedNotice.getTitle() + "'?\nThis action cannot be undone!",
                "Confirm Delete Notice",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                adminController.handleDeleteNotice(selectedNotice.getNoticeId());
                loadNotices();
                JOptionPane.showMessageDialog(this, "Notice deleted successfully.", "Notice Deleted", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to delete notice: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // TABLE CELL RENDERERS
    // =========================================================================

    private static class CategoryBadgeCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final PillBadge badge = new PillBadge("Category", Color.WHITE, Color.BLACK);

        public CategoryBadgeCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 4, 8));
            setOpaque(true);
            add(badge);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? COLOR_ROW_SELECTED_BG : Color.WHITE);
            String cat = (value != null) ? value.toString() : "General";
            badge.setText(cat);
            applyCategoryBadgeStyle(badge, cat);
            return this;
        }
    }

    private static class PriorityBadgeCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final PillBadge badge = new PillBadge("Normal", Color.WHITE, Color.BLACK);

        public PriorityBadgeCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 4, 8));
            setOpaque(true);
            add(badge);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? COLOR_ROW_SELECTED_BG : Color.WHITE);
            String pri = (value != null) ? value.toString() : "Normal";
            badge.setText(pri);
            applyPriorityBadgeStyle(badge, pri);
            return this;
        }
    }
}
