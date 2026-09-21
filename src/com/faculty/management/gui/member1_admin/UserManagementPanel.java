package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.gui.common.CustomButton;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        initComponents();
        loadUserDataFromBackend();
    }

    private void initComponents() {
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
            }
        };

        userTable = new JTable(tableModel);
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
                    handleViewUser();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(userTable);
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
    }

    /**
     * Loads all user data from backend service/database into the table.
     */
    private void loadUserDataFromBackend() {
        try {
            tableModel.setRowCount(0);
            List<User> userList = adminController.loadAllUsers();
            for (User u : userList) {
                String roleTitle = (u.getRole() != null) ? u.getRole().getRoleName() : "User";
                // Format role name nicely for UI
                roleTitle = formatRoleDisplay(roleTitle);

                tableModel.addRow(new Object[]{
                        u.getUserId(),
                        u.getUsername(),
                        u.getFirstName(),
                        u.getLastName(),
                        roleTitle,
                        u.getEmail(),
                        u.getContactNo(),
                        u.getStatus()
                });
            }
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
        }
    }

    /**
     * 2. Create New User Profile with Backend Database Logic
     */
    private void handleCreateUser() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UserFormDialog dialog = new UserFormDialog(parentFrame, "Create New User", UserFormDialog.FormMode.CREATE, adminController);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
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
        }
    }

    /**
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
    }
}
