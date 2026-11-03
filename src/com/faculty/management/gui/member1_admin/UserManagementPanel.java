package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
<<<<<<< HEAD
=======
import com.faculty.management.gui.common.CustomButton;
>>>>>>> Aysha
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
<<<<<<< HEAD
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
=======
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
>>>>>>> Aysha
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;

import static com.faculty.management.gui.member1_admin.AdminUIComponents.*;

/**
 * User Management Panel for Member 1 (Administrator).
 * Strictly implements the UI/UX design from Page 2 of the FMS Specification.
 *
 * Demonstrates Core OOP Principles:
 * - Classes & Objects: Encapsulation of UI components, models, and controller.
 * - Inheritance: Extends JPanel to integrate cleanly into Swing container hierarchy.
 * - Abstraction: Business logic isolated inside AdminController and UserService.
 * - Polymorphism: Dynamic table renderers for User Avatar, Role pills, and Status pills.
 * - Encapsulation: Private member variables with controlled public accessors.
 * - Error & Exception Handling: Catches ValidationException & DatabaseException with feedback.
 * - Database Handling: Connects to MySQL with backend data persistence.
 */
public class UserManagementPanel extends JPanel {

    // Encapsulated Controller & Models
    private final AdminController adminController;
    private List<User> allUsers = new ArrayList<>();
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTable userTable;

    // Filter controls
    private SearchTextField searchField;
    private JComboBox<String> roleFilterBox;
    private JLabel showingCountLabel;

    // Right Sidebar Detail Components
    private JPanel userDetailCard;
    private JLabel detailAvatarLabel;
    private JLabel detailNameLabel;
    private JLabel detailUsernameLabel;
    private JLabel detailUserIdVal;
    private JLabel detailRoleVal;
    private JLabel detailStatusVal;
    private JLabel detailEmailVal;
    private JLabel detailContactVal;

    // Detail Action Buttons
    private WhiteOutlineButton updateUserBtn;
    private WhiteOutlineButton assignRoleBtn;
    private WhiteOutlineButton maintainCredsBtn;
    private DangerOutlineButton deleteUserBtn;

    // Currently selected user reference
    private User selectedUser;

    public UserManagementPanel() {
        this(new AdminController());
    }

    public UserManagementPanel(AdminController adminController) {
        this.adminController = (adminController != null) ? adminController : new AdminController();
=======
import java.util.List;

/**
 * User Profile Management Panel for Member 1 (Administrator).
 * 
 * Implements UI & Backend features:
 * - View users (Detailed read-only view, filtering, table display loaded from DB)
 * - Create users (Backend database persistence, role-based instantiation, validation)
 * - Delete / Deactivate users (Permanent delete & status toggling in DB)
 * - Assign user roles (Assign/change roles: Admin, Lecturer, Technical Officer, Undergraduate)
 * - Maintain usernames/passwords (Update username & change password securely)
 * 
 * OOP Principles Applied:
 * - Classes & Objects: Controller, service, tables, models, dialogs, and renderers.
 * - Inheritance: Extends JPanel, custom renderer extends DefaultTableCellRenderer.
 * - Abstraction: Controller & Service layers abstract data access from UI.
 * - Polymorphism: Polymorphic User model handling and table display.
 * - Encapsulation: Private members with accessor methods.
 * - Error and Exception Handling: Uses ValidationException & DatabaseException.
 * - Database Handling: Connects to MySQL with JDBC and provides offline fallback.
 */
public class UserManagementPanel extends JPanel {

    // Encapsulated UI components
    private final AdminController adminController;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private JComboBox<String> roleFilterComboBox;

    // Action Buttons
    private JButton viewButton;
    private JButton createButton;
    private JButton updateButton;
    private JButton assignRoleButton;
    private JButton maintainCredsButton;
    private JButton deleteButton;
    private JButton refreshButton;

    public UserManagementPanel() {
        this.adminController = new AdminController();
>>>>>>> Aysha
        initComponents();
        loadUserDataFromBackend();
    }

    private void initComponents() {
<<<<<<< HEAD
        setLayout(new BorderLayout(0, 16));
        setBackground(COLOR_PAGE_BG);
        setBorder(new EmptyBorder(0, 28, 20, 28));

        // 1. Header Section: Title, Subtitle, and "+ Create user" action button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("User Management");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(COLOR_TEXT_DARK);

        JLabel pageSub = new JLabel("Create users, assign roles and maintain credentials.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        BurgundyButton createUserBtn = new BurgundyButton("+ Create user");
        createUserBtn.setPreferredSize(new Dimension(145, 38));
        createUserBtn.addActionListener(e -> handleCreateUser());

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(createUserBtn, BorderLayout.EAST);

        // 2. Main Content Split: Left (Table + Filters), Right (User Detail & Action Card)
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
     * Builds the left area containing the search bar, role dropdown, table, and count footer.
     */
    private JPanel createLeftTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        // Filters Toolbar
        JPanel filterBar = new JPanel(new BorderLayout(10, 0));
        filterBar.setOpaque(false);

        searchField = new SearchTextField("Search by name, username or email...", 24);
        searchField.setPreferredSize(new Dimension(280, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        String[] roles = {"All roles", "Admin", "Lecturer", "Undergraduate", "Technical Officer"};
        roleFilterBox = new JComboBox<>(roles);
        styleComboBox(roleFilterBox);
        roleFilterBox.setPreferredSize(new Dimension(140, 36));
        roleFilterBox.addActionListener(e -> applyFilters());

        WhiteOutlineButton refreshBtn = createRefreshButton();
        refreshBtn.setPreferredSize(new Dimension(100, 36));
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            roleFilterBox.setSelectedIndex(0);
            loadUserDataFromBackend();
        });

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftFilters.setOpaque(false);
        leftFilters.add(searchField);
        leftFilters.add(roleFilterBox);

        filterBar.add(leftFilters, BorderLayout.WEST);
        filterBar.add(refreshBtn, BorderLayout.EAST);

        // Table
        String[] cols = {"ID", "USER", "ROLE", "EMAIL", "STATUS"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
=======
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. Top Section: Header & Action Toolbar
        JPanel topSection = new JPanel(new BorderLayout(0, 14));
        topSection.setOpaque(false);

        // Header Title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel mainTitle = new JLabel("User Profile & Access Management");
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mainTitle.setForeground(new Color(15, 23, 42));

        JLabel subTitle = new JLabel("Create users, view profiles, assign roles, maintain credentials, and manage system accounts.");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subTitle.setForeground(new Color(100, 116, 139));

        titlePanel.add(mainTitle);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subTitle);

        // Action Toolbar & Search / Filter Controls (Two clean rows to prevent clipping)
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setOpaque(false);

        // Row 1: Action Buttons (FlowLayout)
        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionButtons.setOpaque(false);

        viewButton = createActionButton("View User", new Color(241, 245, 249), new Color(30, 41, 59));
        viewButton.addActionListener(e -> handleViewUser());

        createButton = createActionButton("+ Create User", new Color(37, 99, 235), Color.WHITE);
        createButton.addActionListener(e -> handleCreateUser());

        updateButton = createActionButton("Update User", new Color(241, 245, 249), new Color(30, 41, 59));
        updateButton.addActionListener(e -> handleUpdateUser());

        assignRoleButton = createActionButton("Assign Role", new Color(241, 245, 249), new Color(30, 41, 59));
        assignRoleButton.addActionListener(e -> handleAssignRole());

        maintainCredsButton = createActionButton("Maintain Credentials", new Color(241, 245, 249), new Color(30, 41, 59));
        maintainCredsButton.setToolTipText("Maintain and update username and password for selected user");
        maintainCredsButton.addActionListener(e -> handleMaintainCredentials());

        deleteButton = createActionButton("Delete User", new Color(254, 242, 242), new Color(220, 38, 38));
        deleteButton.addActionListener(e -> handleDeleteUser());

        refreshButton = createActionButton("Refresh", new Color(241, 245, 249), new Color(71, 85, 105));
        refreshButton.addActionListener(e -> {
            resetFilters();
            loadUserDataFromBackend();
        });

        actionButtons.add(viewButton);
        actionButtons.add(createButton);
        actionButtons.add(updateButton);
        actionButtons.add(assignRoleButton);
        actionButtons.add(maintainCredsButton);
        actionButtons.add(deleteButton);
        actionButtons.add(refreshButton);

        // Row 2: Search & Filter Bar
        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchFilterPanel.setOpaque(false);

        JLabel filterLabel = new JLabel("Filter by Role:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterLabel.setForeground(new Color(71, 85, 105));

        String[] roles = {"All Roles", "Admin", "Lecturer", "Technical Officer", "Undergraduate"};
        roleFilterComboBox = new JComboBox<>(roles);
        roleFilterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleFilterComboBox.setPreferredSize(new Dimension(140, 32));
        roleFilterComboBox.setBackground(Color.WHITE);
        roleFilterComboBox.addActionListener(e -> applyFilter());

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchLabel.setForeground(new Color(71, 85, 105));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setPreferredSize(new Dimension(200, 32));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(2, 8, 2, 8)
        ));
        searchField.addActionListener(e -> applyFilter());

        JButton searchBtn = createActionButton("Search", new Color(71, 85, 105), Color.WHITE);
        searchBtn.addActionListener(e -> applyFilter());

        searchFilterPanel.add(filterLabel);
        searchFilterPanel.add(roleFilterComboBox);
        searchFilterPanel.add(Box.createHorizontalStrut(12));
        searchFilterPanel.add(searchLabel);
        searchFilterPanel.add(searchField);
        searchFilterPanel.add(searchBtn);

        controlsPanel.add(actionButtons);
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(searchFilterPanel);

        topSection.add(titlePanel, BorderLayout.NORTH);
        topSection.add(controlsPanel, BorderLayout.SOUTH);

        // 2. Center Section: User Table
        String[] columnNames = {"User ID", "Username", "First Name", "Last Name", "Role", "Email", "Contact No", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable cells (Encapsulation)
>>>>>>> Aysha
            }
        };

        userTable = new JTable(tableModel);
<<<<<<< HEAD
        userTable.setRowHeight(52);
        userTable.setShowVerticalLines(false);
        userTable.setGridColor(new Color(243, 244, 246));
        userTable.setSelectionBackground(new Color(254, 249, 235)); // Warm Cream selection
        userTable.setSelectionForeground(COLOR_TEXT_DARK);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setFont(FONT_BODY);

        // Header Styling
        JTableHeader th = userTable.getTableHeader();
        th.setFont(FONT_SMALL_BOLD);
        th.setForeground(COLOR_TEXT_MUTED);
        th.setBackground(Color.WHITE);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Column Renderers
        userTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        userTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            { setHorizontalAlignment(JLabel.LEFT); }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setForeground(COLOR_TEXT_MUTED);
                setFont(FONT_BODY);
                setBorder(new EmptyBorder(0, 12, 0, 0));
                return c;
            }
        });

        userTable.getColumnModel().getColumn(1).setPreferredWidth(210);
        userTable.getColumnModel().getColumn(1).setCellRenderer(new UserTableCellRenderer());

        userTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        userTable.getColumnModel().getColumn(2).setCellRenderer(new RoleBadgeCellRenderer());

        userTable.getColumnModel().getColumn(3).setPreferredWidth(210);
        userTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setForeground(COLOR_TEXT_BODY);
                setFont(FONT_BODY);
                return c;
            }
        });

        userTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        userTable.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeCellRenderer());

        // Row Sorter
        rowSorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(rowSorter);

        // Selection Listener
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailCardFromSelection();
            }
        });

        // Double click to view details
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && selectedUser != null) {
=======
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userTable.setRowHeight(36);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setShowGrid(false);
        userTable.setShowHorizontalLines(true);
        userTable.setGridColor(new Color(241, 245, 249));
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        userTable.getTableHeader().setBackground(new Color(241, 245, 249));
        userTable.getTableHeader().setForeground(new Color(51, 65, 85));
        userTable.getTableHeader().setPreferredSize(new Dimension(0, 38));

        // Custom Renderers (Polymorphism & Inheritance)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        userTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        // Status Badge Renderer
        userTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                String status = value != null ? value.toString() : "";
                if ("ACTIVE".equalsIgnoreCase(status)) {
                    label.setForeground(new Color(22, 101, 52)); // Dark Green
                    label.setText("● ACTIVE");
                } else {
                    label.setForeground(new Color(153, 27, 27)); // Dark Red
                    label.setText("○ INACTIVE");
                }
                return label;
            }
        });

        // Row Sorter for filtering and sorting
        rowSorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(rowSorter);

        // Double click listener to view user details
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
>>>>>>> Aysha
                    handleViewUser();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(userTable);
<<<<<<< HEAD
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));

        // Footer showing count
        showingCountLabel = new JLabel("Showing 0 of 0 users");
        showingCountLabel.setFont(FONT_SMALL);
        showingCountLabel.setForeground(COLOR_TEXT_MUTED);
        showingCountLabel.setBorder(new EmptyBorder(4, 4, 4, 4));

        panel.add(filterBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(showingCountLabel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Builds the right-hand User Detail & Action Card matching Page 2 of the PDF.
     */
    private JPanel createRightDetailCard() {
        userDetailCard = new CardPanel();
        userDetailCard.setPreferredSize(new Dimension(300, 520));
        userDetailCard.setLayout(new BorderLayout());
        userDetailCard.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel contentBox = new JPanel();
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setOpaque(false);

        // Avatar + Name + Username Header
        JPanel profileHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        profileHeader.setOpaque(false);
        profileHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailAvatarLabel = new JLabel();
        detailAvatarLabel.setIcon(new AvatarIcon("AU", 46, new Color(254, 243, 199), new Color(146, 64, 14)));

        JPanel nameBox = new JPanel();
        nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.Y_AXIS));
        nameBox.setOpaque(false);

        detailNameLabel = new JLabel("Admin User");
        detailNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        detailNameLabel.setForeground(COLOR_TEXT_DARK);

        detailUsernameLabel = new JLabel("@admin");
        detailUsernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailUsernameLabel.setForeground(COLOR_TEXT_MUTED);

        nameBox.add(detailNameLabel);
        nameBox.add(Box.createVerticalStrut(2));
        nameBox.add(detailUsernameLabel);

        profileHeader.add(detailAvatarLabel);
        profileHeader.add(nameBox);

        contentBox.add(profileHeader);
        contentBox.add(Box.createVerticalStrut(20));

        // Info List (User ID, Role, Status, Email, Contact)
        JPanel infoGrid = new JPanel(new GridLayout(5, 2, 8, 12));
        infoGrid.setOpaque(false);
        infoGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoGrid.add(createDetailLabel("User ID"));
        detailUserIdVal = createDetailValLabel("1");
        infoGrid.add(detailUserIdVal);

        infoGrid.add(createDetailLabel("Role"));
        detailRoleVal = createDetailValLabel("Admin");
        infoGrid.add(detailRoleVal);

        infoGrid.add(createDetailLabel("Status"));
        detailStatusVal = createDetailValLabel("Active");
        detailStatusVal.setForeground(new Color(21, 128, 61));
        infoGrid.add(detailStatusVal);

        infoGrid.add(createDetailLabel("Email"));
        detailEmailVal = createDetailValLabel("admin@fot.ruh.ac.lk");
        infoGrid.add(detailEmailVal);

        infoGrid.add(createDetailLabel("Contact"));
        detailContactVal = createDetailValLabel("0771122334");
        infoGrid.add(detailContactVal);

        contentBox.add(infoGrid);
        contentBox.add(Box.createVerticalGlue());

        // 4 Action Buttons stacked vertically
        JPanel actionStack = new JPanel(new GridLayout(4, 1, 0, 10));
        actionStack.setOpaque(false);
        actionStack.setAlignmentX(Component.LEFT_ALIGNMENT);

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

        contentBox.add(Box.createVerticalStrut(18));
        contentBox.add(actionStack);

        userDetailCard.add(contentBox, BorderLayout.CENTER);
        return userDetailCard;
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
=======
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createActionButton(String text, Color bg, Color fg) {
        Color border = (bg.getRed() > 230 && bg.getGreen() > 230) ? new Color(203, 213, 225) : null;
        CustomButton btn = new CustomButton(text, bg, fg, border);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 16, 34));
        return btn;
>>>>>>> Aysha
    }

    /**
     * Loads all user data from backend service/database into the table.
     */
<<<<<<< HEAD
    public void loadUserDataFromBackend() {
        try {
            allUsers = adminController.loadAllUsers();
            tableModel.setRowCount(0);

            for (User u : allUsers) {
                String roleTitle = (u.getRole() != null) ? u.getRole().getRoleName() : "Undergraduate";
=======
    private void loadUserDataFromBackend() {
        try {
            tableModel.setRowCount(0);
            List<User> userList = adminController.loadAllUsers();
            for (User u : userList) {
                String roleTitle = (u.getRole() != null) ? u.getRole().getRoleName() : "User";
                // Format role name nicely for UI
>>>>>>> Aysha
                roleTitle = formatRoleDisplay(roleTitle);

                tableModel.addRow(new Object[]{
                        u.getUserId(),
<<<<<<< HEAD
                        u,
                        roleTitle,
                        u.getEmail(),
                        u.getStatus() != null ? u.getStatus() : "ACTIVE"
                });
            }

            applyFilters();

            // Automatically select first row if available
            if (userTable.getRowCount() > 0) {
                userTable.setRowSelectionInterval(0, 0);
            }
=======
                        u.getUsername(),
                        u.getFirstName(),
                        u.getLastName(),
                        roleTitle,
                        u.getEmail(),
                        u.getContactNo(),
                        u.getStatus()
                });
            }
>>>>>>> Aysha
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Failed to load users from database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatRoleDisplay(String role) {
        if (role == null) return "Undergraduate";
        String r = role.replace("_", " ").trim();
        if (r.equalsIgnoreCase("ADMIN")) return "Admin";
        if (r.equalsIgnoreCase("LECTURER")) return "Lecturer";
        if (r.equalsIgnoreCase("TECHNICAL OFFICER")) return "Technical Officer";
        if (r.equalsIgnoreCase("UNDERGRADUATE") || r.equalsIgnoreCase("STUDENT")) return "Undergraduate";
        return r;
    }

    /**
<<<<<<< HEAD
     * Filters table rows based on search text and selected role.
     */
    private void applyFilters() {
        String query = searchField.getText().trim().toLowerCase();
        String selectedRole = (String) roleFilterBox.getSelectedItem();

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                // Role filter check
                if (selectedRole != null && !selectedRole.equals("All roles")) {
                    String rowRole = entry.getStringValue(2);
                    if (!rowRole.equalsIgnoreCase(selectedRole)) {
                        return false;
                    }
                }

                // Query search check (matches name, username, or email)
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
=======
     * 1. View User Details (Backend Fetch & Read-Only Display)
     */
    private void handleViewUser() {
        try {
            int modelRow = getSelectedModelRow();
            int userId = (int) tableModel.getValueAt(modelRow, 0);

            // Fetch complete User object with backend database handling & polymorphism
            User fullUser = adminController.handleViewUser(userId);

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            UserFormDialog dialog = new UserFormDialog(parentFrame, "User Details - " + fullUser.getUsername(), UserFormDialog.FormMode.VIEW, fullUser, adminController);
            dialog.setVisible(true);
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Selection Required", JOptionPane.WARNING_MESSAGE);
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, "Failed to fetch user details from database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
>>>>>>> Aysha
        }
    }

    /**
<<<<<<< HEAD
     * Updates the right-hand User Detail Card based on the currently selected row.
     */
    private void updateDetailCardFromSelection() {
        int viewRow = userTable.getSelectedRow();
        if (viewRow == -1) {
            selectedUser = null;
            detailNameLabel.setText("No User Selected");
            detailUsernameLabel.setText("-");
            detailUserIdVal.setText("-");
            detailRoleVal.setText("-");
            detailStatusVal.setText("-");
            detailEmailVal.setText("-");
            detailContactVal.setText("-");
            detailAvatarLabel.setIcon(new AvatarIcon("U", 46));
            return;
        }

        int modelRow = userTable.convertRowIndexToModel(viewRow);
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

        if (selectedUser != null) {
            String fullName = selectedUser.getFullName();
            if (fullName == null || fullName.trim().isEmpty()) {
                fullName = selectedUser.getUsername();
            }
            detailNameLabel.setText(fullName);
            detailUsernameLabel.setText("@" + selectedUser.getUsername());
            detailUserIdVal.setText(String.valueOf(selectedUser.getUserId()));

            String roleName = (selectedUser.getRole() != null) ? formatRoleDisplay(selectedUser.getRole().getRoleName()) : "Undergraduate";
            detailRoleVal.setText(roleName);

            String status = selectedUser.getStatus() != null ? selectedUser.getStatus() : "ACTIVE";
            detailStatusVal.setText(status.equalsIgnoreCase("ACTIVE") ? "Active" : "Inactive");
            detailStatusVal.setForeground(status.equalsIgnoreCase("ACTIVE") ? new Color(21, 128, 61) : new Color(185, 28, 28));

            detailEmailVal.setText(selectedUser.getEmail() != null ? selectedUser.getEmail() : "—");
            detailContactVal.setText((selectedUser.getContactNo() != null && !selectedUser.getContactNo().isEmpty()) ? selectedUser.getContactNo() : "0771122334");

            String initials = extractInitials(fullName);
            Color avatarBg = getAvatarBgForRole(roleName);
            Color avatarFg = getAvatarFgForRole(roleName);
            detailAvatarLabel.setIcon(new AvatarIcon(initials, 46, avatarBg, avatarFg));
        }
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
    // ACTION HANDLERS (Delegates to AdminController & UserFormDialog)
    // =========================================================================

=======
     * 2. Create New User Profile with Backend Database Logic
     */
>>>>>>> Aysha
    private void handleCreateUser() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UserFormDialog dialog = new UserFormDialog(parentFrame, "Create New User", UserFormDialog.FormMode.CREATE, adminController);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
<<<<<<< HEAD
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
        Object[] rowData = new Object[]{
                selectedUser.getUserId(),
                selectedUser.getUsername(),
                selectedUser.getFirstName(),
                selectedUser.getLastName(),
                selectedUser.getRole() != null ? selectedUser.getRole().getRoleName() : "Undergraduate",
                selectedUser.getEmail(),
                selectedUser.getContactNo(),
                selectedUser.getStatus()
        };

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UserFormDialog dialog = new UserFormDialog(parentFrame, "Update User Profile", UserFormDialog.FormMode.UPDATE, rowData, adminController);
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
        Object[] rowData = new Object[]{
                selectedUser.getUserId(),
                selectedUser.getUsername(),
                selectedUser.getFirstName(),
                selectedUser.getLastName(),
                selectedUser.getRole() != null ? selectedUser.getRole().getRoleName() : "Undergraduate",
                selectedUser.getEmail(),
                selectedUser.getContactNo(),
                selectedUser.getStatus()
        };

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UserFormDialog dialog = new UserFormDialog(parentFrame, "Assign User Role", UserFormDialog.FormMode.ASSIGN_ROLE, rowData, adminController);
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
        Object[] rowData = new Object[]{
                selectedUser.getUserId(),
                selectedUser.getUsername(),
                selectedUser.getFirstName(),
                selectedUser.getLastName(),
                selectedUser.getRole() != null ? selectedUser.getRole().getRoleName() : "Undergraduate",
                selectedUser.getEmail(),
                selectedUser.getContactNo(),
                selectedUser.getStatus()
        };

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UserFormDialog dialog = new UserFormDialog(parentFrame, "Maintain Credentials", UserFormDialog.FormMode.MAINTAIN_CREDENTIALS, rowData, adminController);
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
    // TABLE CELL RENDERERS (Polymorphism & 2D Graphics)
    // =========================================================================

    /**
     * Renders column 1 with circular avatar + Two-line text (Full Name and @username).
     */
    private class UserTableCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JLabel avatarLabel = new JLabel();
        private final JLabel nameLabel = new JLabel();
        private final JLabel usernameLabel = new JLabel();

        public UserTableCellRenderer() {
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

            add(avatarLabel, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(new Color(254, 249, 235));
            } else {
                setBackground(Color.WHITE);
            }

            if (value instanceof User) {
                User u = (User) value;
                String name = u.getFullName();
                if (name == null || name.trim().isEmpty()) name = u.getUsername();
                nameLabel.setText(name);
                nameLabel.setForeground(COLOR_TEXT_DARK);
                usernameLabel.setText("@" + u.getUsername());
                usernameLabel.setForeground(COLOR_TEXT_MUTED);

                String initials = extractInitials(name);
                String role = u.getRole() != null ? u.getRole().getRoleName() : "User";
                avatarLabel.setIcon(new AvatarIcon(initials, 32, getAvatarBgForRole(role), getAvatarFgForRole(role)));
            } else {
                nameLabel.setText(value != null ? value.toString() : "");
                usernameLabel.setText("");
                avatarLabel.setIcon(new AvatarIcon("U", 32));
            }
            return this;
=======
            Object[] data = dialog.getUserData();
            tableModel.addRow(data);
            JOptionPane.showMessageDialog(this, 
                    "User '" + data[1] + "' successfully created in the backend system!", 
                    "User Created", 
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 3. Update Existing User Profile
     */
    private void handleUpdateUser() {
        try {
            int modelRow = getSelectedModelRow();
            Object[] rowData = getRowData(modelRow);

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            UserFormDialog dialog = new UserFormDialog(parentFrame, "Update User Profile", UserFormDialog.FormMode.UPDATE, rowData, adminController);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                Object[] updatedData = dialog.getUserData();
                for (int i = 0; i < updatedData.length; i++) {
                    tableModel.setValueAt(updatedData[i], modelRow, i);
                }
                JOptionPane.showMessageDialog(this, "User profile updated successfully!", "Update Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * 4. Assign User Role
     */
    private void handleAssignRole() {
        try {
            int modelRow = getSelectedModelRow();
            Object[] rowData = getRowData(modelRow);

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            UserFormDialog dialog = new UserFormDialog(parentFrame, "Assign User Role", UserFormDialog.FormMode.ASSIGN_ROLE, rowData, adminController);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                Object[] updatedData = dialog.getUserData();
                tableModel.setValueAt(updatedData[4], modelRow, 4); // Update Role column
                JOptionPane.showMessageDialog(this, "Role for '" + rowData[1] + "' successfully updated to " + updatedData[4] + "!", "Role Assigned", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * 5. Maintain Usernames and Passwords
     */
    private void handleMaintainCredentials() {
        try {
            int modelRow = getSelectedModelRow();
            Object[] rowData = getRowData(modelRow);

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            UserFormDialog dialog = new UserFormDialog(parentFrame, "Maintain Credentials", UserFormDialog.FormMode.MAINTAIN_CREDENTIALS, rowData, adminController);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                Object[] updatedData = dialog.getUserData();
                tableModel.setValueAt(updatedData[1], modelRow, 1); // Update Username
                JOptionPane.showMessageDialog(this, "Credentials maintained successfully for user '" + updatedData[1] + "'!", "Credentials Updated", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * 6. Delete / Deactivate Users with Backend Database Persistence
     */
    private void handleDeleteUser() {
        try {
            int modelRow = getSelectedModelRow();
            int userId = (int) tableModel.getValueAt(modelRow, 0);
            String username = (String) tableModel.getValueAt(modelRow, 1);
            String currentStatus = (String) tableModel.getValueAt(modelRow, 7);

            // Prevent deleting the primary admin account directly from UI
            if (userId == 1 || "admin".equalsIgnoreCase(username)) {
                JOptionPane.showMessageDialog(this, 
                        "The primary system administrator ('admin') account cannot be deleted.", 
                        "Action Prohibited", 
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Object[] options = {"Permanent Delete", "Toggle Status (" + ("ACTIVE".equalsIgnoreCase(currentStatus) ? "Deactivate" : "Activate") + ")", "Cancel"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Choose an action for user '" + username + "' (User ID: " + userId + "):",
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
                        "Are you sure you want to permanently delete user '" + username + "'?\nThis action cannot be undone!",
                        "Confirm Permanent Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    adminController.handleDeleteUser(userId);
                    tableModel.removeRow(modelRow);
                    JOptionPane.showMessageDialog(this, 
                            "User '" + username + "' has been permanently deleted from the database.", 
                            "User Deleted", 
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (choice == JOptionPane.NO_OPTION) {
                String newStatus = "ACTIVE".equalsIgnoreCase(currentStatus) ? "INACTIVE" : "ACTIVE";
                adminController.handleUpdateStatus(userId, newStatus);
                tableModel.setValueAt(newStatus, modelRow, 7);
                JOptionPane.showMessageDialog(this, 
                        "User '" + username + "' status set to " + newStatus + ".", 
                        "Status Changed", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Warning", JOptionPane.WARNING_MESSAGE);
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, "Database operation failed: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
>>>>>>> Aysha
        }
    }

    /**
<<<<<<< HEAD
     * Renders Role as a clean Pill Badge.
     */
    private static class RoleBadgeCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final PillBadge badge = new PillBadge("Role", Color.WHITE, Color.BLACK);

        public RoleBadgeCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 4, 12));
            setOpaque(true);
            add(badge);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? new Color(254, 249, 235) : Color.WHITE);
            String role = (value != null) ? value.toString() : "Undergraduate";
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

    /**
     * Renders Status as a Pill Badge (Active = Green pill, Inactive = Red pill).
     */
    private static class StatusBadgeCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final PillBadge badge = new PillBadge("Active", Color.WHITE, Color.BLACK);

        public StatusBadgeCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 4, 12));
            setOpaque(true);
            add(badge);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? new Color(254, 249, 235) : Color.WHITE);
            String status = (value != null) ? value.toString() : "ACTIVE";
            boolean isActive = "ACTIVE".equalsIgnoreCase(status);

            badge.setText(isActive ? "Active" : "Inactive");
            badge.setBackground(isActive ? new Color(220, 252, 231) : new Color(254, 226, 226));
            badge.setForeground(isActive ? new Color(21, 128, 61) : new Color(185, 28, 28));
            return this;
        }
=======
     * Helper to get selected model row with ValidationException handling.
     */
    private int getSelectedModelRow() throws ValidationException {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            throw new ValidationException("Please select a user from the table first.");
        }
        return userTable.convertRowIndexToModel(selectedRow);
    }

    private Object[] getRowData(int modelRow) {
        Object[] rowData = new Object[tableModel.getColumnCount()];
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            rowData[i] = tableModel.getValueAt(modelRow, i);
        }
        return rowData;
    }

    private void applyFilter() {
        String role = (String) roleFilterComboBox.getSelectedItem();
        String searchText = searchField.getText().trim();

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                if (role != null && !"All Roles".equals(role)) {
                    String rowRole = entry.getStringValue(4);
                    if (!role.equalsIgnoreCase(rowRole)) {
                        return false;
                    }
                }
                if (!searchText.isEmpty()) {
                    boolean match = false;
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        if (entry.getStringValue(i).toLowerCase().contains(searchText.toLowerCase())) {
                            match = true;
                            break;
                        }
                    }
                    return match;
                }
                return true;
            }
        };
        rowSorter.setRowFilter(rf);
    }

    private void resetFilters() {
        roleFilterComboBox.setSelectedIndex(0);
        searchField.setText("");
        rowSorter.setRowFilter(null);
    }

    // Encapsulated Getters
    public JTable getUserTable() {
        return userTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public AdminController getAdminController() {
        return adminController;
>>>>>>> Aysha
    }
}
