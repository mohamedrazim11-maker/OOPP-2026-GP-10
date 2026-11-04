package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.faculty.management.gui.member1_admin.AdminUIComponents.*;

/**
 * User Management Panel for the Administrator (Member 1).
 *
 * <p>Drop-in replacement: same package, same constructors, same public
 * {@link #loadUserDataFromBackend()} method used by AdminDashboard.</p>
 *
 * <ul>
 *   <li><b>Real data only</b> - users are read from the database through
 *       {@link AdminController} on a background thread. No sample values: the table shows
 *       loading, empty, "no match" and error states instead.</li>
 *   <li><b>Responsive</b> - on wide windows the user table sits beside the detail card;
 *       on narrower windows the card moves under the table, the page scrolls, and the
 *       ID / Email columns are hidden (the details stay in the card).</li>
 *   <li><b>Same design language</b> as the dashboard: burgundy and gold, cream selection,
 *       rounded white cards, role and status pills.</li>
 * </ul>
 *
 * <p>OOP: encapsulation (private state), inheritance (extends JPanel), polymorphism
 * (custom table cell renderers), abstraction (logic stays in AdminController) and
 * structured exception handling.</p>
 */
public class UserManagementPanel extends JPanel {

    // ---------------------------------------------------------------------
    // Controller, data and table
    // ---------------------------------------------------------------------
    private final AdminController adminController;
    private List<User> allUsers = new ArrayList<>();
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTable userTable;

    // Table area states: "table" or "message"
    private CardLayout tableCards;
    private JPanel tableArea;
    private JLabel messageLabel;
    private WhiteOutlineButton messageRetryBtn;

    // Loading state
    private boolean loading = false;
    private String loadError = null;

    // Filters
    private SearchTextField searchField;
    private JComboBox<String> roleFilterBox;
    private JLabel showingCountLabel;

    // Layout pieces that change with the window width
    private JPanel mainContent;
    private JPanel detailHolder;
    private JPanel actionStack;
    private Boolean stackedMode = null;
    private Boolean idColumnHidden = null;
    private Boolean emailColumnHidden = null;

    // Detail card
    private JPanel userDetailCard;
    private JLabel detailAvatarLabel;
    private JLabel detailNameLabel;
    private JLabel detailUsernameLabel;
    private JLabel detailUserIdVal;
    private JLabel detailRoleVal;
    private JLabel detailStatusVal;
    private JLabel detailEmailVal;
    private JLabel detailContactVal;

    // Detail action buttons
    private WhiteOutlineButton updateUserBtn;
    private WhiteOutlineButton assignRoleBtn;
    private WhiteOutlineButton maintainCredsBtn;
    private DangerOutlineButton deleteUserBtn;

    // Currently selected user
    private User selectedUser;

    private static final String EM_DASH = "—";
    private static final Color SELECTION_BG = new Color(254, 249, 235);
    private static final int DETAIL_WIDTH = 300;
    private static final int STACK_BREAKPOINT = 980;
    private static final int HIDE_ID_BREAKPOINT = 900;
    private static final int HIDE_EMAIL_BREAKPOINT = 720;

    // ---------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------

    public UserManagementPanel() {
        this(new AdminController());
    }

    public UserManagementPanel(AdminController adminController) {
        this.adminController = (adminController != null) ? adminController : new AdminController();
        initComponents();
        loadUserDataFromBackend();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(COLOR_PAGE_BG);

        ScrollablePanel content = new ScrollablePanel(new BorderLayout(0, 16));
        content.setOpaque(true);
        content.setBackground(COLOR_PAGE_BG);
        content.setBorder(new EmptyBorder(16, 28, 20, 28));

        // ---- Header: title + "+ Create user" ----
        JPanel headerPanel = new JPanel(new BorderLayout(12, 0));
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("User Management");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(COLOR_TEXT_DARK);

        EllipsisLabel pageSub = new EllipsisLabel("Create users, assign roles and maintain credentials.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        BurgundyButton createUserBtn = new BurgundyButton("+ Create user");
        createUserBtn.setPreferredSize(new Dimension(145, 38));
        createUserBtn.addActionListener(e -> handleCreateUser());
        JPanel createWrap = new JPanel(new GridBagLayout());
        createWrap.setOpaque(false);
        createWrap.add(createUserBtn);

        headerPanel.add(titleBox, BorderLayout.CENTER);
        headerPanel.add(createWrap, BorderLayout.EAST);

        // ---- Main area: table (left / top) + detail card (right / bottom) ----
        mainContent = new JPanel(new BorderLayout(24, 18));
        mainContent.setOpaque(false);

        JPanel leftPanel = createLeftTablePanel();
        detailHolder = new JPanel(new BorderLayout());
        detailHolder.setOpaque(false);
        detailHolder.add(createRightDetailCard(), BorderLayout.CENTER);

        mainContent.add(leftPanel, BorderLayout.CENTER);
        mainContent.add(detailHolder, BorderLayout.EAST);

        content.add(headerPanel, BorderLayout.NORTH);
        content.add(mainContent, BorderLayout.CENTER);

        JScrollPane page = new JScrollPane(content,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        page.setBorder(null);
        page.getViewport().setBackground(COLOR_PAGE_BG);
        page.getVerticalScrollBar().setUnitIncrement(16);
        add(page, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyResponsiveLayout();
            }
        });

        clearDetailCard();
        updateTableView();
    }

    // =========================================================================
    // RESPONSIVE LAYOUT
    // =========================================================================

    private void applyResponsiveLayout() {
        int w = getWidth();
        if (w <= 0) return;

        boolean stacked = w < STACK_BREAKPOINT;
        if (stackedMode == null || stackedMode != stacked) {
            stackedMode = stacked;
            mainContent.remove(detailHolder);
            if (stacked) {
                actionStack.setLayout(new GridLayout(0, 2, 10, 10));
                detailHolder.setPreferredSize(null);
                tableArea.setPreferredSize(new Dimension(0, 340));
                mainContent.add(detailHolder, BorderLayout.SOUTH);
            } else {
                actionStack.setLayout(new GridLayout(0, 1, 0, 10));
                tableArea.setPreferredSize(null);
                detailHolder.setPreferredSize(new Dimension(DETAIL_WIDTH, userDetailCard.getPreferredSize().height));
                mainContent.add(detailHolder, BorderLayout.EAST);
            }
            mainContent.revalidate();
            mainContent.repaint();
        }

        boolean hideId = w < HIDE_ID_BREAKPOINT;
        if (idColumnHidden == null || idColumnHidden != hideId) {
            idColumnHidden = hideId;
            setColumnHidden(0, hideId, 50);
        }

        boolean hideEmail = w < HIDE_EMAIL_BREAKPOINT;
        if (emailColumnHidden == null || emailColumnHidden != hideEmail) {
            emailColumnHidden = hideEmail;
            setColumnHidden(3, hideEmail, 220);
        }
    }

    private void setColumnHidden(int columnIndex, boolean hidden, int shownWidth) {
        TableColumn col = userTable.getColumnModel().getColumn(columnIndex);
        if (hidden) {
            col.setMinWidth(0);
            col.setMaxWidth(0);
            col.setPreferredWidth(0);
        } else {
            col.setMinWidth(15);
            col.setMaxWidth(Integer.MAX_VALUE);
            col.setPreferredWidth(shownWidth);
        }
        userTable.revalidate();
    }

    // =========================================================================
    // LEFT SIDE: filters + table
    // =========================================================================

    private JPanel createLeftTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        // ---- Filters toolbar: search grows, role + refresh stay on the right ----
        JPanel filterBar = new JPanel(new BorderLayout(10, 0));
        filterBar.setOpaque(false);

        searchField = new SearchTextField("Search by name, username or email...", 24);
        searchField.setPreferredSize(new Dimension(200, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        String[] roles = {"All roles", "Admin", "Lecturer", "Undergraduate", "Technical Officer"};
        roleFilterBox = new JComboBox<>(roles);
        styleComboBox(roleFilterBox);
        roleFilterBox.setPreferredSize(new Dimension(150, 36));
        roleFilterBox.addActionListener(e -> applyFilters());

        WhiteOutlineButton refreshBtn = createRefreshButton();
        refreshBtn.setPreferredSize(new Dimension(100, 36));
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            roleFilterBox.setSelectedIndex(0);
            loadUserDataFromBackend();
        });

        JPanel rightFilters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightFilters.setOpaque(false);
        rightFilters.add(roleFilterBox);
        rightFilters.add(refreshBtn);

        filterBar.add(searchField, BorderLayout.CENTER);
        filterBar.add(rightFilters, BorderLayout.EAST);

        // ---- Table ----
        String[] cols = {"ID", "USER", "ROLE", "EMAIL", "STATUS"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setRowHeight(56);
        userTable.setShowVerticalLines(false);
        userTable.setShowHorizontalLines(true);
        userTable.setIntercellSpacing(new Dimension(0, 1));
        userTable.setGridColor(new Color(243, 244, 246));
        userTable.setSelectionBackground(SELECTION_BG);
        userTable.setSelectionForeground(COLOR_TEXT_DARK);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setFillsViewportHeight(true);
        userTable.setFont(FONT_BODY);

        JTableHeader th = userTable.getTableHeader();
        th.setFont(FONT_SMALL_BOLD);
        th.setForeground(COLOR_TEXT_MUTED);
        th.setBackground(Color.WHITE);
        th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 38));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // ID
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        userTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            { setHorizontalAlignment(JLabel.LEFT); }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, false, row, col);
                setForeground(COLOR_TEXT_MUTED);
                setFont(FONT_BODY);
                setBorder(new EmptyBorder(0, 12, 0, 0));
                return c;
            }
        });

        // USER (avatar + name + @username)
        userTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        userTable.getColumnModel().getColumn(1).setCellRenderer(new UserTableCellRenderer());

        // ROLE (pill)
        userTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        userTable.getColumnModel().getColumn(2).setCellRenderer(new RoleBadgeCellRenderer());

        // EMAIL
        userTable.getColumnModel().getColumn(3).setPreferredWidth(220);
        userTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, false, row, col);
                setForeground(COLOR_TEXT_BODY);
                setFont(FONT_BODY);
                setToolTipText(value != null ? value.toString() : null);
                return c;
            }
        });

        // STATUS (pill)
        userTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeCellRenderer());

        rowSorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(rowSorter);

        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailCardFromSelection();
            }
        });

        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && selectedUser != null) {
                    handleViewUser();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1, true));

        // ---- Message view (loading / empty / no match / error) ----
        JPanel messagePanel = new JPanel(new GridBagLayout());
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1, true));

        messageLabel = new JLabel("Loading users…", SwingConstants.CENTER);
        messageLabel.setFont(FONT_BODY);
        messageLabel.setForeground(COLOR_TEXT_MUTED);

        messageRetryBtn = new WhiteOutlineButton("Retry");
        messageRetryBtn.setPreferredSize(new Dimension(100, 36));
        messageRetryBtn.addActionListener(e -> loadUserDataFromBackend());
        messageRetryBtn.setVisible(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(0, 24, 12, 24);
        messagePanel.add(messageLabel, gc);
        gc.gridy = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        messagePanel.add(messageRetryBtn, gc);

        tableCards = new CardLayout();
        tableArea = new JPanel(tableCards);
        tableArea.setOpaque(false);
        tableArea.add(scrollPane, "table");
        tableArea.add(messagePanel, "message");

        // ---- Footer count ----
        showingCountLabel = new JLabel("Showing 0 of 0 users");
        showingCountLabel.setFont(FONT_SMALL);
        showingCountLabel.setForeground(COLOR_TEXT_MUTED);
        showingCountLabel.setBorder(new EmptyBorder(2, 4, 2, 4));

        panel.add(filterBar, BorderLayout.NORTH);
        panel.add(tableArea, BorderLayout.CENTER);
        panel.add(showingCountLabel, BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================================
    // RIGHT SIDE: user detail + actions
    // =========================================================================

    private JPanel createRightDetailCard() {
        userDetailCard = new CardPanel();
        userDetailCard.setLayout(new BorderLayout(0, 18));
        userDetailCard.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);

        // Avatar + name + username
        JPanel profileHeader = new JPanel(new BorderLayout(14, 0));
        profileHeader.setOpaque(false);
        profileHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailAvatarLabel = new JLabel(new AvatarIcon("U", 46));

        JPanel nameBox = new JPanel();
        nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.Y_AXIS));
        nameBox.setOpaque(false);

        detailNameLabel = new EllipsisLabel("No User Selected");
        detailNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        detailNameLabel.setForeground(COLOR_TEXT_DARK);

        detailUsernameLabel = new EllipsisLabel(EM_DASH);
        detailUsernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailUsernameLabel.setForeground(COLOR_TEXT_MUTED);

        nameBox.add(detailNameLabel);
        nameBox.add(Box.createVerticalStrut(2));
        nameBox.add(detailUsernameLabel);

        JPanel nameWrap = new JPanel(new GridBagLayout());
        nameWrap.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        nameWrap.add(nameBox, gc);

        profileHeader.add(detailAvatarLabel, BorderLayout.WEST);
        profileHeader.add(nameWrap, BorderLayout.CENTER);

        top.add(profileHeader);
        top.add(Box.createVerticalStrut(20));

        detailUserIdVal = createDetailValLabel(EM_DASH);
        detailRoleVal = createDetailValLabel(EM_DASH);
        detailStatusVal = createDetailValLabel(EM_DASH);
        detailEmailVal = createDetailValLabel(EM_DASH);
        detailContactVal = createDetailValLabel(EM_DASH);

        top.add(createDetailRow("User ID", detailUserIdVal));
        top.add(Box.createVerticalStrut(12));
        top.add(createDetailRow("Role", detailRoleVal));
        top.add(Box.createVerticalStrut(12));
        top.add(createDetailRow("Status", detailStatusVal));
        top.add(Box.createVerticalStrut(12));
        top.add(createDetailRow("Email", detailEmailVal));
        top.add(Box.createVerticalStrut(12));
        top.add(createDetailRow("Contact", detailContactVal));

        // Actions
        actionStack = new JPanel(new GridLayout(0, 1, 0, 10));
        actionStack.setOpaque(false);

        updateUserBtn = new WhiteOutlineButton("Update user");
        updateUserBtn.addActionListener(e -> handleUpdateUser());

        assignRoleBtn = new WhiteOutlineButton("Assign role");
        assignRoleBtn.addActionListener(e -> handleAssignRole());

        maintainCredsBtn = new WhiteOutlineButton("Maintain credentials");
        maintainCredsBtn.addActionListener(e -> handleMaintainCredentials());

        deleteUserBtn = new DangerOutlineButton("Delete user");
        deleteUserBtn.addActionListener(e -> handleDeleteUser());

        actionStack.add(updateUserBtn);
        actionStack.add(assignRoleBtn);
        actionStack.add(maintainCredsBtn);
        actionStack.add(deleteUserBtn);

        userDetailCard.add(top, BorderLayout.NORTH);
        userDetailCard.add(actionStack, BorderLayout.SOUTH);
        return userDetailCard;
    }

    private JPanel createDetailRow(String label, JLabel value) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(FONT_BODY);
        l.setForeground(COLOR_TEXT_MUTED);
        l.setPreferredSize(new Dimension(64, l.getPreferredSize().height));

        row.add(l, BorderLayout.WEST);
        row.add(value, BorderLayout.CENTER);
        return row;
    }

    private JLabel createDetailValLabel(String text) {
        EllipsisLabel l = new EllipsisLabel(text);
        l.setFont(FONT_BODY_BOLD);
        l.setForeground(COLOR_TEXT_DARK);
        return l;
    }

    private void clearDetailCard() {
        selectedUser = null;
        detailNameLabel.setText("No User Selected");
        detailUsernameLabel.setText(EM_DASH);
        setDetailValue(detailUserIdVal, EM_DASH, COLOR_TEXT_DARK);
        setDetailValue(detailRoleVal, EM_DASH, COLOR_TEXT_DARK);
        setDetailValue(detailStatusVal, EM_DASH, COLOR_TEXT_DARK);
        setDetailValue(detailEmailVal, EM_DASH, COLOR_TEXT_DARK);
        setDetailValue(detailContactVal, EM_DASH, COLOR_TEXT_DARK);
        detailAvatarLabel.setIcon(new AvatarIcon("U", 46));
    }

    private void setDetailValue(JLabel label, String text, Color color) {
        label.setText(text);
        label.setToolTipText(EM_DASH.equals(text) ? null : text);
        label.setForeground(color);
    }

    // =========================================================================
    // DATA LOADING (background thread, real database data only)
    // =========================================================================

    /** Reloads users from the database without blocking the UI. */
    public void loadUserDataFromBackend() {
        if (loading) return;
        loading = true;
        loadError = null;
        final int keepSelectedId = (selectedUser != null) ? selectedUser.getUserId() : -1;
        updateTableView();

        new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                List<User> users = adminController.loadAllUsers();
                if (users == null) {
                    throw new IllegalStateException("The database returned no data.");
                }
                return users;
            }

            @Override
            protected void done() {
                loading = false;
                try {
                    showUsers(get(), keepSelectedId);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    loadError = "Loading was interrupted.";
                    updateTableView();
                } catch (ExecutionException ex) {
                    Throwable cause = (ex.getCause() != null) ? ex.getCause() : ex;
                    String msg = (cause.getMessage() != null && !cause.getMessage().isEmpty())
                            ? cause.getMessage() : cause.getClass().getSimpleName();
                    loadError = "Could not load users from the database: " + msg;
                    updateTableView();
                }
            }
        }.execute();
    }

    private void showUsers(List<User> users, int keepSelectedId) {
        allUsers = users;
        tableModel.setRowCount(0);

        for (User u : allUsers) {
            String roleTitle = formatRoleDisplay(u.getRole() != null ? u.getRole().getRoleName() : null);
            tableModel.addRow(new Object[]{
                    u.getUserId(),
                    u,
                    roleTitle,
                    u.getEmail(),
                    u.getStatus() != null ? u.getStatus() : "ACTIVE"
            });
        }

        applyFilters();

        // Keep the same user selected after a reload when possible
        if (keepSelectedId != -1) {
            for (int viewRow = 0; viewRow < userTable.getRowCount(); viewRow++) {
                int modelRow = userTable.convertRowIndexToModel(viewRow);
                Object id = tableModel.getValueAt(modelRow, 0);
                if (id instanceof Integer && (Integer) id == keepSelectedId) {
                    userTable.setRowSelectionInterval(viewRow, viewRow);
                    break;
                }
            }
        }
        updateTableView();
    }

    private String formatRoleDisplay(String role) {
        if (role == null) return "Unassigned";
        String r = role.replace("_", " ").trim();
        if (r.equalsIgnoreCase("ADMIN")) return "Admin";
        if (r.equalsIgnoreCase("LECTURER")) return "Lecturer";
        if (r.equalsIgnoreCase("TECHNICAL OFFICER")) return "Technical Officer";
        if (r.equalsIgnoreCase("UNDERGRADUATE") || r.equalsIgnoreCase("STUDENT")) return "Undergraduate";
        return r;
    }

    /** Chooses between the table and the loading / empty / no-match / error message. */
    private void updateTableView() {
        if (tableCards == null) return;

        String message = null;
        boolean showRetry = false;

        if (loadError != null && tableModel.getRowCount() == 0) {
            message = loadError;
            showRetry = true;
        } else if (loading && tableModel.getRowCount() == 0) {
            message = "Loading users…";
        } else if (tableModel.getRowCount() == 0) {
            message = "No users found in the database. Use “+ Create user” to add one.";
        } else if (userTable.getRowCount() == 0) {
            message = "No users match your search.";
        }

        if (message == null) {
            tableCards.show(tableArea, "table");
        } else {
            messageLabel.setText(message);
            messageRetryBtn.setVisible(showRetry);
            tableCards.show(tableArea, "message");
        }
    }

    // =========================================================================
    // FILTERS
    // =========================================================================

    private void applyFilters() {
        String query = searchField.getText().trim().toLowerCase();
        String selectedRole = (String) roleFilterBox.getSelectedItem();

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                if (selectedRole != null && !selectedRole.equals("All roles")) {
                    String rowRole = entry.getStringValue(2);
                    if (!rowRole.equalsIgnoreCase(selectedRole)) {
                        return false;
                    }
                }

                if (!query.isEmpty()) {
                    Object userObj = entry.getValue(1);
                    if (userObj instanceof User) {
                        User u = (User) userObj;
                        boolean match = (u.getFullName() != null && u.getFullName().toLowerCase().contains(query)) ||
                                (u.getUsername() != null && u.getUsername().toLowerCase().contains(query)) ||
                                (u.getEmail() != null && u.getEmail().toLowerCase().contains(query));
                        if (!match) return false;
                    } else {
                        String idStr = entry.getStringValue(0);
                        String emailStr = entry.getStringValue(3);
                        if (!idStr.contains(query) && !emailStr.toLowerCase().contains(query)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        };

        rowSorter.setRowFilter(rf);
        int visibleCount = userTable.getRowCount();
        int totalCount = tableModel.getRowCount();
        showingCountLabel.setText("Showing " + visibleCount + " of " + totalCount + " users");

        if (visibleCount > 0 && userTable.getSelectedRow() == -1) {
            userTable.setRowSelectionInterval(0, 0);
        } else if (visibleCount == 0) {
            clearDetailCard();
        }
        updateTableView();
    }

    // =========================================================================
    // DETAIL CARD UPDATE
    // =========================================================================

    private void updateDetailCardFromSelection() {
        int viewRow = userTable.getSelectedRow();
        if (viewRow == -1) {
            clearDetailCard();
            return;
        }

        int modelRow = userTable.convertRowIndexToModel(viewRow);
        selectedUser = null;
        Object userObj = tableModel.getValueAt(modelRow, 1);
        if (userObj instanceof User) {
            selectedUser = (User) userObj;
        } else {
            int userId = (int) tableModel.getValueAt(modelRow, 0);
            for (User u : allUsers) {
                if (u.getUserId() == userId) {
                    selectedUser = u;
                    break;
                }
            }
        }

        if (selectedUser == null) {
            clearDetailCard();
            return;
        }

        String fullName = selectedUser.getFullName();
        if (fullName == null || fullName.trim().isEmpty()) {
            fullName = selectedUser.getUsername();
        }
        detailNameLabel.setText(fullName);
        detailNameLabel.setToolTipText(fullName);
        detailUsernameLabel.setText("@" + selectedUser.getUsername());

        setDetailValue(detailUserIdVal, String.valueOf(selectedUser.getUserId()), COLOR_TEXT_DARK);

        String roleName = formatRoleDisplay(selectedUser.getRole() != null ? selectedUser.getRole().getRoleName() : null);
        setDetailValue(detailRoleVal, roleName, COLOR_TEXT_DARK);

        String status = selectedUser.getStatus() != null ? selectedUser.getStatus() : "ACTIVE";
        boolean active = status.equalsIgnoreCase("ACTIVE");
        setDetailValue(detailStatusVal, active ? "Active" : "Inactive",
                active ? new Color(21, 128, 61) : new Color(185, 28, 28));

        String email = selectedUser.getEmail();
        setDetailValue(detailEmailVal, (email != null && !email.isEmpty()) ? email : EM_DASH, COLOR_TEXT_DARK);

        String contact = selectedUser.getContactNo();
        setDetailValue(detailContactVal, (contact != null && !contact.isEmpty()) ? contact : EM_DASH, COLOR_TEXT_DARK);

        detailAvatarLabel.setIcon(new AvatarIcon(extractInitials(fullName), 46,
                getAvatarBgForRole(roleName), getAvatarFgForRole(roleName)));
    }

    private Color getAvatarBgForRole(String role) {
        if ("Admin".equalsIgnoreCase(role)) return new Color(254, 243, 199);
        if ("Lecturer".equalsIgnoreCase(role)) return new Color(254, 240, 199);
        if ("Technical Officer".equalsIgnoreCase(role)) return new Color(254, 249, 195);
        return new Color(237, 233, 254);
    }

    private Color getAvatarFgForRole(String role) {
        if ("Admin".equalsIgnoreCase(role)) return new Color(146, 64, 14);
        if ("Lecturer".equalsIgnoreCase(role)) return new Color(180, 83, 9);
        if ("Technical Officer".equalsIgnoreCase(role)) return new Color(133, 77, 14);
        return new Color(67, 56, 202);
    }

    // =========================================================================
    // ACTION HANDLERS (unchanged behaviour: delegate to AdminController / UserFormDialog)
    // =========================================================================

    private Object[] selectedUserRow() {
        return new Object[]{
                selectedUser.getUserId(),
                selectedUser.getUsername(),
                selectedUser.getFirstName(),
                selectedUser.getLastName(),
                selectedUser.getRole() != null ? selectedUser.getRole().getRoleName() : "Undergraduate",
                selectedUser.getEmail(),
                selectedUser.getContactNo(),
                selectedUser.getStatus()
        };
    }

    private void handleCreateUser() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UserFormDialog dialog = new UserFormDialog(parentFrame, "Create New User", UserFormDialog.FormMode.CREATE, adminController);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadUserDataFromBackend();
            JOptionPane.showMessageDialog(this, "User created successfully in the database!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleViewUser() {
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to view.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            User fullUser = adminController.handleViewUser(selectedUser.getUserId());
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            UserFormDialog dialog = new UserFormDialog(parentFrame, "User Details - " + fullUser.getUsername(), UserFormDialog.FormMode.VIEW, fullUser, adminController);
            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load user details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateUser() {
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UserFormDialog dialog = new UserFormDialog(parentFrame, "Update User Profile", UserFormDialog.FormMode.UPDATE, selectedUserRow(), adminController);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadUserDataFromBackend();
            JOptionPane.showMessageDialog(this, "User profile updated successfully!", "Update Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleAssignRole() {
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to assign role.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UserFormDialog dialog = new UserFormDialog(parentFrame, "Assign User Role", UserFormDialog.FormMode.ASSIGN_ROLE, selectedUserRow(), adminController);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadUserDataFromBackend();
            JOptionPane.showMessageDialog(this, "User role successfully updated!", "Role Assigned", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleMaintainCredentials() {
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to maintain credentials.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UserFormDialog dialog = new UserFormDialog(parentFrame, "Maintain Credentials", UserFormDialog.FormMode.MAINTAIN_CREDENTIALS, selectedUserRow(), adminController);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadUserDataFromBackend();
            JOptionPane.showMessageDialog(this, "Credentials updated successfully!", "Credentials Maintained", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleDeleteUser() {
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = selectedUser.getUserId();
        String username = selectedUser.getUsername();
        String currentStatus = selectedUser.getStatus();

        if (userId == 1 || "admin".equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "The primary administrator account ('admin') cannot be deleted.", "Action Prohibited", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object[] options = {"Permanent Delete", "Toggle Status (" + ("ACTIVE".equalsIgnoreCase(currentStatus) ? "Deactivate" : "Activate") + ")", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose an action for user '" + username + "' (ID: " + userId + "):",
                "Delete / Deactivate User",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]
        );

        if (choice == JOptionPane.YES_OPTION) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to permanently delete user '" + username + "'?\nThis cannot be undone!",
                    "Confirm Permanent Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    adminController.handleDeleteUser(userId);
                    loadUserDataFromBackend();
                    JOptionPane.showMessageDialog(this, "User '" + username + "' has been permanently deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to delete user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (choice == JOptionPane.NO_OPTION) {
            try {
                String newStatus = "ACTIVE".equalsIgnoreCase(currentStatus) ? "INACTIVE" : "ACTIVE";
                adminController.handleUpdateStatus(userId, newStatus);
                loadUserDataFromBackend();
                JOptionPane.showMessageDialog(this, "User status set to " + newStatus + ".", "Status Changed", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to update status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // SMALL REUSABLE COMPONENTS
    // =========================================================================

    /** Page content that follows the viewport width (so it can reflow) and scrolls vertically. */
    private static class ScrollablePanel extends JPanel implements Scrollable {
        ScrollablePanel(LayoutManager layout) {
            super(layout);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return (orientation == SwingConstants.VERTICAL) ? Math.max(visibleRect.height - 32, 16) : visibleRect.width;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            Container parent = getParent();
            return parent instanceof JViewport && parent.getHeight() > getPreferredSize().height;
        }
    }

    /** A label that shrinks and shows "…" instead of forcing its parent wider. */
    private static class EllipsisLabel extends JLabel {
        EllipsisLabel(String text) {
            super(text);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(0, super.getPreferredSize().height);
        }
    }

    // =========================================================================
    // TABLE CELL RENDERERS
    // =========================================================================

    /** USER column: circular avatar + full name + @username. */
    private class UserTableCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JLabel avatarLabel = new JLabel();
        private final EllipsisLabel nameLabel = new EllipsisLabel("");
        private final EllipsisLabel usernameLabel = new EllipsisLabel("");

        UserTableCellRenderer() {
            setLayout(new BorderLayout(10, 0));
            setOpaque(true);
            setBorder(new EmptyBorder(6, 8, 6, 8));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);

            nameLabel.setFont(FONT_BODY_BOLD);
            usernameLabel.setFont(FONT_SMALL);

            textPanel.add(nameLabel);
            textPanel.add(Box.createVerticalStrut(2));
            textPanel.add(usernameLabel);

            JPanel textWrap = new JPanel(new GridBagLayout());
            textWrap.setOpaque(false);
            GridBagConstraints gc = new GridBagConstraints();
            gc.weightx = 1;
            gc.fill = GridBagConstraints.HORIZONTAL;
            textWrap.add(textPanel, gc);

            add(avatarLabel, BorderLayout.WEST);
            add(textWrap, BorderLayout.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? SELECTION_BG : Color.WHITE);

            if (value instanceof User) {
                User u = (User) value;
                String name = u.getFullName();
                if (name == null || name.trim().isEmpty()) name = u.getUsername();
                nameLabel.setText(name);
                nameLabel.setForeground(COLOR_TEXT_DARK);
                usernameLabel.setText("@" + u.getUsername());
                usernameLabel.setForeground(COLOR_TEXT_MUTED);

                String role = formatRoleDisplay(u.getRole() != null ? u.getRole().getRoleName() : null);
                avatarLabel.setIcon(new AvatarIcon(extractInitials(name), 34, getAvatarBgForRole(role), getAvatarFgForRole(role)));
            } else {
                nameLabel.setText(value != null ? value.toString() : "");
                usernameLabel.setText("");
                avatarLabel.setIcon(new AvatarIcon("U", 34));
            }
            return this;
        }
    }

    /** ROLE column: coloured pill. */
    private static class RoleBadgeCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final PillBadge badge = new PillBadge("Role", Color.WHITE, Color.BLACK);

        RoleBadgeCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 4, 14));
            setOpaque(true);
            add(badge);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? SELECTION_BG : Color.WHITE);
            String role = (value != null) ? value.toString() : "Unassigned";
            badge.setText(role);

            if ("Admin".equalsIgnoreCase(role)) {
                badge.setBackground(new Color(254, 243, 199));
                badge.setForeground(new Color(146, 64, 14));
            } else if ("Lecturer".equalsIgnoreCase(role)) {
                badge.setBackground(new Color(254, 243, 199));
                badge.setForeground(new Color(180, 83, 9));
            } else if ("Technical Officer".equalsIgnoreCase(role)) {
                badge.setBackground(new Color(254, 249, 195));
                badge.setForeground(new Color(133, 77, 14));
            } else {
                badge.setBackground(new Color(237, 233, 254));
                badge.setForeground(new Color(67, 56, 202));
            }
            return this;
        }
    }

    /** STATUS column: green "Active" / red "Inactive" pill. */
    private static class StatusBadgeCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final PillBadge badge = new PillBadge("Active", Color.WHITE, Color.BLACK);

        StatusBadgeCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 4, 14));
            setOpaque(true);
            add(badge);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? SELECTION_BG : Color.WHITE);
            String status = (value != null) ? value.toString() : "ACTIVE";
            boolean isActive = "ACTIVE".equalsIgnoreCase(status);

            badge.setText(isActive ? "Active" : "Inactive");
            badge.setBackground(isActive ? new Color(220, 252, 231) : new Color(254, 226, 226));
            badge.setForeground(isActive ? new Color(21, 128, 61) : new Color(185, 28, 28));
            return this;
        }
    }
}