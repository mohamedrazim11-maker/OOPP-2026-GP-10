package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.gui.common.CustomButton;
import com.faculty.management.gui.common.PrimaryButton;
import com.faculty.management.model.Notice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Notice Management Panel for Administrator (Member 1).
 * Represents the Notice Option UI inside the Admin Dashboard.
 * 
 * Strictly provides the "Create Notice", "Edit Notice", "Delete Notice", and "View Notice" options UI
 * for managing official announcements and circulars across the faculty.
 * Strictly avoids adding any other option inside notice management as requested.
 * 
 * Demonstrates Core OOP Principles:
 * - Classes and Objects: Instantiation of UI cards, table models, controllers, and Notice entities.
 * - Inheritance: Extends JPanel to inherit Swing container hierarchy.
 * - Abstraction: Controller and service layers abstract backend database logic from UI components.
 * - Polymorphism: Swing event listener dispatching, overloaded dialog triggers, and dynamic filtering.
 * - Encapsulation: Private component fields with controlled public accessors.
 * - Error and Exception Handling: Catches and manages errors through NoticeFormDialog and message dialogs.
 * - Database Handling: Connects to MySQL / SQL Server database through AdminController, NoticeService, and NoticeDAO.
 */
public class NoticeManagementPanel extends JPanel {

    // Encapsulated Controller & Components
    private final AdminController adminController;
    private PrimaryButton createNoticeButton;
    private CustomButton editNoticeButton;
    private CustomButton deleteNoticeButton;
    private CustomButton viewNoticeButton;
    private JButton refreshButton;

    private JTextField searchField;
    private JComboBox<String> categoryFilterBox;
    private JTable noticeTable;
    private DefaultTableModel tableModel;
    private JLabel totalNoticesCountLabel;
    private JLabel lastActionNoticeLabel;

    private List<Notice> currentNoticeList = new ArrayList<>();

    public NoticeManagementPanel() {
        this.adminController = new AdminController();
        initComponents();
        loadNotices();
    }

    public NoticeManagementPanel(AdminController adminController) {
        this.adminController = (adminController != null) ? adminController : new AdminController();
        initComponents();
        loadNotices();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // 1. Header Title & Description
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel mainTitleLabel = new JLabel("Notice Board Management");
        mainTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mainTitleLabel.setForeground(new Color(15, 23, 42));

        JLabel subTitleLabel = new JLabel("Publish and maintain official faculty announcements, circulars, examination updates, and timetables.");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subTitleLabel.setForeground(new Color(100, 116, 139));

        headerPanel.add(mainTitleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subTitleLabel);

        // 2. Action Toolbar (Strictly only "Create Notice", "Edit Notice", "Delete Notice", and "View Notice" options)
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false);

        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actionToolbar.setOpaque(false);

        createNoticeButton = new PrimaryButton("📢  Create Notice");
        createNoticeButton.setPreferredSize(new Dimension(160, 42));
        createNoticeButton.setToolTipText("Open dialog to draft and publish a new faculty announcement");
        createNoticeButton.addActionListener(e -> handleCreateNoticeAction());

        editNoticeButton = new CustomButton(
                "✏️  Edit Notice",
                new Color(15, 118, 110),
                new Color(13, 148, 136),
                new Color(17, 94, 89),
                Color.WHITE,
                null,
                8
        );
        editNoticeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        editNoticeButton.setPreferredSize(new Dimension(150, 42));
        editNoticeButton.setToolTipText("Select and modify an existing published notice");
        editNoticeButton.addActionListener(e -> handleEditNoticeAction());

        deleteNoticeButton = new CustomButton(
                "🗑️  Delete Notice",
                new Color(220, 38, 38),
                new Color(185, 28, 28),
                new Color(153, 27, 27),
                Color.WHITE,
                null,
                8
        );
        deleteNoticeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteNoticeButton.setPreferredSize(new Dimension(160, 42));
        deleteNoticeButton.setToolTipText("Permanently remove an outdated announcement from the notice board");
        deleteNoticeButton.addActionListener(e -> handleDeleteNoticeAction());

        viewNoticeButton = new CustomButton(
                "👁️  View Notice",
                new Color(71, 85, 105),
                new Color(100, 116, 139),
                new Color(51, 65, 85),
                Color.WHITE,
                null,
                8
        );
        viewNoticeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewNoticeButton.setPreferredSize(new Dimension(150, 42));
        viewNoticeButton.setToolTipText("Inspect the full text and details of selected notice");
        viewNoticeButton.addActionListener(e -> handleViewNoticeAction());

        actionToolbar.add(createNoticeButton);
        actionToolbar.add(editNoticeButton);
        actionToolbar.add(deleteNoticeButton);
        actionToolbar.add(viewNoticeButton);

        // 3. Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setOpaque(false);
        statusBar.setBorder(new EmptyBorder(12, 4, 12, 4));

        totalNoticesCountLabel = new JLabel("Total Notices: 0 published announcements");
        totalNoticesCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        totalNoticesCountLabel.setForeground(new Color(51, 65, 85));

        lastActionNoticeLabel = new JLabel("Ready. Strictly Notice Board Management options active.");
        lastActionNoticeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lastActionNoticeLabel.setForeground(new Color(100, 116, 139));

        statusBar.add(totalNoticesCountLabel, BorderLayout.WEST);
        statusBar.add(lastActionNoticeLabel, BorderLayout.EAST);

        // 4. Search and Filter Bar
        JPanel searchFilterBar = new JPanel(new BorderLayout(15, 0));
        searchFilterBar.setOpaque(false);
        searchFilterBar.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);

        JLabel searchLbl = new JLabel("🔍 Search:");
        searchLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchLbl.setForeground(new Color(51, 65, 85));

        searchField = new JTextField(18);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 34));
        searchField.addActionListener(e -> applyFilter());

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchBtn.setPreferredSize(new Dimension(80, 34));
        searchBtn.addActionListener(e -> applyFilter());

        JLabel categoryLbl = new JLabel("Category:");
        categoryLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        categoryLbl.setForeground(new Color(51, 65, 85));

        categoryFilterBox = new JComboBox<>(new String[]{"ALL", "ACADEMIC", "EXAMINATION", "TIMETABLE", "GENERAL"});
        categoryFilterBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryFilterBox.setPreferredSize(new Dimension(140, 34));
        categoryFilterBox.addActionListener(e -> applyFilter());

        searchPanel.add(searchLbl);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(categoryLbl);
        searchPanel.add(categoryFilterBox);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightControls.setOpaque(false);

        refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setPreferredSize(new Dimension(100, 34));
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            categoryFilterBox.setSelectedIndex(0);
            loadNotices();
        });
        rightControls.add(refreshButton);

        searchFilterBar.add(searchPanel, BorderLayout.WEST);
        searchFilterBar.add(rightControls, BorderLayout.EAST);

        // 5. Notices Table
        String[] columns = {"ID", "Title", "Category", "Priority", "Posted By", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        noticeTable = new JTable(tableModel);
        noticeTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noticeTable.setRowHeight(36);
        noticeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        noticeTable.setShowGrid(true);
        noticeTable.setGridColor(new Color(241, 245, 249));
        noticeTable.setSelectionBackground(new Color(224, 231, 255));
        noticeTable.setSelectionForeground(new Color(30, 41, 59));

        // Column widths
        noticeTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        noticeTable.getColumnModel().getColumn(1).setPreferredWidth(320);
        noticeTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        noticeTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        noticeTable.getColumnModel().getColumn(4).setPreferredWidth(160);
        noticeTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Center align ID, Category, Priority, Date
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        noticeTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        noticeTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        noticeTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        noticeTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JTableHeader header = noticeTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(new Color(51, 65, 85));
        header.setPreferredSize(new Dimension(0, 38));

        // Double click to view
        noticeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && noticeTable.getSelectedRow() != -1) {
                    handleViewNoticeAction();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(noticeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        mainContentPanel.add(actionToolbar);
        mainContentPanel.add(Box.createVerticalStrut(10));
        mainContentPanel.add(statusBar);
        mainContentPanel.add(searchFilterBar);
        mainContentPanel.add(scrollPane);

        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    /**
     * Loads all notices from the database via AdminController and updates the table.
     */
    public void loadNotices() {
        try {
            List<Notice> notices = adminController.loadAllNotices();
            this.currentNoticeList = (notices != null) ? notices : new ArrayList<>();
            updateTable(this.currentNoticeList);

            int total = adminController.handleGetNoticeCount();
            totalNoticesCountLabel.setText("Total Notices: " + total + " published announcement(s)");
            lastActionNoticeLabel.setText("Synchronized with database. " + currentNoticeList.size() + " notice(s) loaded.");
        } catch (DatabaseException e) {
            lastActionNoticeLabel.setText("Error loading notices: " + e.getMessage());
        }
    }

    private void updateTable(List<Notice> list) {
        tableModel.setRowCount(0);
        for (Notice n : list) {
            tableModel.addRow(new Object[]{
                    n.getNoticeId(),
                    n.getTitle(),
                    n.getCategory(),
                    n.getPriority(),
                    n.getPostedBy(),
                    n.getFormattedDate()
            });
        }
    }

    private void applyFilter() {
        String query = searchField.getText().trim();
        String cat = (String) categoryFilterBox.getSelectedItem();

        try {
            List<Notice> filtered;
            if (!query.isEmpty()) {
                filtered = adminController.handleSearchNotices(query);
            } else if (cat != null && !cat.equalsIgnoreCase("ALL")) {
                filtered = adminController.handleViewNoticesByCategory(cat);
            } else {
                filtered = adminController.loadAllNotices();
            }

            if (cat != null && !cat.equalsIgnoreCase("ALL") && !query.isEmpty()) {
                List<Notice> dual = new ArrayList<>();
                for (Notice n : filtered) {
                    if (cat.equalsIgnoreCase(n.getCategory())) {
                        dual.add(n);
                    }
                }
                filtered = dual;
            }

            this.currentNoticeList = filtered;
            updateTable(filtered);
            lastActionNoticeLabel.setText("Found " + filtered.size() + " matching notice(s).");
        } catch (DatabaseException e) {
            lastActionNoticeLabel.setText("Filter error: " + e.getMessage());
        }
    }

    private Notice getSelectedNotice() {
        int selectedRow = noticeTable.getSelectedRow();
        if (selectedRow == -1 || selectedRow >= currentNoticeList.size()) {
            return null;
        }
        int noticeId = (int) tableModel.getValueAt(selectedRow, 0);
        for (Notice n : currentNoticeList) {
            if (n.getNoticeId() == noticeId) {
                return n;
            }
        }
        return null;
    }

    // =========================================================================
    // STRICT NOTICE ACTION HANDLERS
    // =========================================================================

    /**
     * Handles "Create Notice" action UI.
     */
    public void handleCreateNoticeAction() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        NoticeFormDialog dialog = new NoticeFormDialog(parentFrame, adminController, NoticeFormDialog.FormMode.ADD);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadNotices();
            lastActionNoticeLabel.setText("Notice published successfully to the database.");
        }
    }

    /**
     * Programmatically creates a new notice through the controller and refreshes table.
     * Demonstrates Abstraction, Encapsulation, Error/Exception Handling, and Database Handling.
     */
    public Notice createNotice(Notice notice) throws ValidationException, DatabaseException {
        Notice created = adminController.handleCreateNotice(notice);
        loadNotices();
        lastActionNoticeLabel.setText("Notice published: " + created.getTitle());
        return created;
    }

    /**
     * Polymorphic overload to create a notice using parameter list.
     */
    public Notice createNotice(String title, String content, String category, String priority, String postedBy)
            throws ValidationException, DatabaseException {
        Notice created = adminController.handleCreateNotice(title, content, category, priority, postedBy);
        loadNotices();
        lastActionNoticeLabel.setText("Notice published: " + created.getTitle());
        return created;
    }

    /**
     * Handles "Edit Notice" action UI.
     */
    public void handleEditNoticeAction() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        Notice selected = getSelectedNotice();

        NoticeFormDialog dialog = new NoticeFormDialog(parentFrame, adminController, NoticeFormDialog.FormMode.UPDATE, selected);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadNotices();
            lastActionNoticeLabel.setText("Notice updated successfully in the database.");
        }
    }

    /**
     * Programmatically updates an existing notice through the controller and refreshes table.
     * Demonstrates Abstraction, Encapsulation, Error/Exception Handling, and Database Handling.
     */
    public boolean updateNotice(Notice notice) throws ValidationException, DatabaseException {
        boolean updated = adminController.handleUpdateNotice(notice);
        if (updated) {
            loadNotices();
            lastActionNoticeLabel.setText("Notice updated: " + notice.getTitle());
        }
        return updated;
    }

    /**
     * Polymorphic overload to update a notice using parameter list.
     */
    public boolean updateNotice(int noticeId, String title, String content, String category, String priority, String postedBy)
            throws ValidationException, DatabaseException {
        boolean updated = adminController.handleUpdateNotice(noticeId, title, content, category, priority, postedBy);
        if (updated) {
            loadNotices();
            lastActionNoticeLabel.setText("Notice updated: " + title);
        }
        return updated;
    }

    /**
     * Handles "Delete Notice" action UI.
     */
    public void handleDeleteNoticeAction() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        Notice selected = getSelectedNotice();

        NoticeFormDialog dialog = new NoticeFormDialog(parentFrame, adminController, NoticeFormDialog.FormMode.DELETE, selected);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadNotices();
            lastActionNoticeLabel.setText("Notice removed successfully from the database.");
        }
    }

    /**
     * Programmatically deletes a notice by ID through the controller and refreshes table.
     * Demonstrates Abstraction, Encapsulation, Error/Exception Handling, and Database Handling.
     */
    public boolean deleteNotice(int noticeId) throws ValidationException, DatabaseException {
        boolean deleted = adminController.handleDeleteNotice(noticeId);
        if (deleted) {
            loadNotices();
            lastActionNoticeLabel.setText("Notice ID #" + noticeId + " deleted successfully.");
        }
        return deleted;
    }

    /**
     * Polymorphic overload to delete a notice by Notice entity.
     */
    public boolean deleteNotice(Notice notice) throws ValidationException, DatabaseException {
        if (notice == null) {
            throw new ValidationException("Notice cannot be null for deletion.");
        }
        return deleteNotice(notice.getNoticeId());
    }

    /**
     * Polymorphic overload to delete a notice by title string.
     */
    public boolean deleteNotice(String title) throws ValidationException, DatabaseException {
        boolean deleted = adminController.handleDeleteNotice(title);
        if (deleted) {
            loadNotices();
            lastActionNoticeLabel.setText("Notice '" + title + "' deleted successfully.");
        }
        return deleted;
    }

    /**
     * Handles "View Notice" action UI.
     */
    public void handleViewNoticeAction() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        Notice selected = getSelectedNotice();

        NoticeFormDialog dialog = new NoticeFormDialog(parentFrame, adminController, NoticeFormDialog.FormMode.VIEW, selected);
        dialog.setVisible(true);
    }

    /**
     * Programmatically retrieves full notice details by ID.
     * Demonstrates Abstraction, Encapsulation, Error/Exception Handling, and Database Handling.
     */
    public Notice viewNotice(int noticeId) throws ValidationException, DatabaseException {
        return adminController.handleViewNotice(noticeId);
    }

    /**
     * Polymorphic overload to retrieve notice details using a Notice entity.
     */
    public Notice viewNotice(Notice notice) throws ValidationException, DatabaseException {
        return adminController.handleViewNotice(notice);
    }

    /**
     * Polymorphic overload to retrieve notice details by title string.
     */
    public Notice viewNoticeByTitle(String title) throws ValidationException, DatabaseException {
        return adminController.handleViewNoticeByTitle(title);
    }

    /**
     * Programmatically searches notices by keyword and refreshes the view.
     */
    public List<Notice> searchNotices(String keyword) throws DatabaseException {
        searchField.setText(keyword != null ? keyword : "");
        applyFilter();
        return currentNoticeList;
    }

    /**
     * Programmatically filters notices by category and refreshes the view.
     */
    public List<Notice> filterByCategory(String category) throws DatabaseException {
        categoryFilterBox.setSelectedItem(category != null ? category : "ALL");
        applyFilter();
        return currentNoticeList;
    }

    // Encapsulated Getters for testing and programmatic automation

    public PrimaryButton getCreateNoticeButton() {
        return createNoticeButton;
    }

    public CustomButton getEditNoticeButton() {
        return editNoticeButton;
    }

    public CustomButton getDeleteNoticeButton() {
        return deleteNoticeButton;
    }

    public CustomButton getViewNoticeButton() {
        return viewNoticeButton;
    }

    public JTable getNoticeTable() {
        return noticeTable;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JComboBox<String> getCategoryFilterBox() {
        return categoryFilterBox;
    }
}
