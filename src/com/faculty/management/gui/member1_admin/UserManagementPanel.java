package com.faculty.management.gui.member1_admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * User Profile Management Panel for Member 1 (Administrator).
 * Enables Administrator to:
 * - Create users
 * - Update users
 * - Maintain usernames and passwords
 * - Filter and search user profiles
 * 
 * Demonstrates OOP (Encapsulation, Inheritance, Event Handling).
 */
public class UserManagementPanel extends JPanel {

    private JTable userTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private JComboBox<String> roleFilterComboBox;

    private JButton createButton;
    private JButton updateButton;
    private JButton maintainCredsButton;
    private JButton deleteButton;
    private JButton refreshButton;

    public UserManagementPanel() {
        initComponents();
        loadInitialUserData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. Top Section: Header & Action Toolbar
        JPanel topSection = new JPanel(new BorderLayout(0, 12));
        topSection.setOpaque(false);

        // Header Title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel mainTitle = new JLabel("👥 User Profile Management");
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mainTitle.setForeground(new Color(15, 23, 42));

        JLabel subTitle = new JLabel("Create, update, and maintain credentials for all faculty users (Admin, Lecturers, TOs, Students)");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subTitle.setForeground(new Color(100, 116, 139));

        titlePanel.add(mainTitle);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subTitle);

        // Action Toolbar & Search / Filter Controls
        JPanel controlsPanel = new JPanel(new BorderLayout(15, 10));
        controlsPanel.setOpaque(false);

        // Left Actions
        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionButtons.setOpaque(false);

        createButton = createActionButton("➕ Create User", new Color(37, 99, 235), Color.WHITE);
        createButton.addActionListener(e -> openCreateUserDialog());

        updateButton = createActionButton("✏️ Update User", new Color(241, 245, 249), new Color(30, 41, 59));
        updateButton.addActionListener(e -> openUpdateUserDialog());

        maintainCredsButton = createActionButton("🔑 Maintain Password", new Color(241, 245, 249), new Color(30, 41, 59));
        maintainCredsButton.addActionListener(e -> openMaintainPasswordDialog());

        deleteButton = createActionButton("🗑️ Delete / Deactivate", new Color(254, 242, 242), new Color(220, 38, 38));
        deleteButton.addActionListener(e -> handleDeleteUser());

        refreshButton = createActionButton("🔄 Refresh", new Color(241, 245, 249), new Color(71, 85, 105));
        refreshButton.addActionListener(e -> resetFilters());

        actionButtons.add(createButton);
        actionButtons.add(updateButton);
        actionButtons.add(maintainCredsButton);
        actionButtons.add(deleteButton);
        actionButtons.add(refreshButton);

        // Right Search & Filter Bar
        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchFilterPanel.setOpaque(false);

        JLabel filterLabel = new JLabel("Filter Role:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterLabel.setForeground(new Color(71, 85, 105));

        String[] roles = {"All Roles", "Admin", "Lecturer", "Technical Officer", "Undergraduate"};
        roleFilterComboBox = new JComboBox<>(roles);
        roleFilterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleFilterComboBox.setPreferredSize(new Dimension(140, 32));
        roleFilterComboBox.setBackground(Color.WHITE);
        roleFilterComboBox.addActionListener(e -> applyFilter());

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setPreferredSize(new Dimension(180, 32));
        searchField.putClientProperty("JTextField.placeholderText", "Search users...");
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(2, 8, 2, 8)
        ));
        searchField.addActionListener(e -> applyFilter());

        JButton searchBtn = createActionButton("Search", new Color(71, 85, 105), Color.WHITE);
        searchBtn.addActionListener(e -> applyFilter());

        searchFilterPanel.add(filterLabel);
        searchFilterPanel.add(roleFilterComboBox);
        searchFilterPanel.add(searchField);
        searchFilterPanel.add(searchBtn);

        controlsPanel.add(actionButtons, BorderLayout.WEST);
        controlsPanel.add(searchFilterPanel, BorderLayout.EAST);

        topSection.add(titlePanel, BorderLayout.NORTH);
        topSection.add(controlsPanel, BorderLayout.SOUTH);

        // 2. Center Section: User Table
        String[] columnNames = {"User ID", "Username", "First Name", "Last Name", "Role", "Email", "Contact No", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable table cells
            }
        };

        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userTable.setRowHeight(34);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setShowGrid(false);
        userTable.setShowHorizontalLines(true);
        userTable.setGridColor(new Color(241, 245, 249));
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        userTable.getTableHeader().setBackground(new Color(241, 245, 249));
        userTable.getTableHeader().setForeground(new Color(51, 65, 85));
        userTable.getTableHeader().setPreferredSize(new Dimension(0, 38));

        // Center render ID and Status
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        userTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        // Row Sorter for search and filter
        rowSorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(rowSorter);

        // Double click listener to edit user
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openUpdateUserDialog();
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
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 10, 32));
        return btn;
    }

    private void openCreateUserDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UserFormDialog dialog = new UserFormDialog(parentFrame, "Create New User", UserFormDialog.FormMode.CREATE, null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Object[] data = dialog.getUserData();
            tableModel.addRow(data);
            JOptionPane.showMessageDialog(this, "User '" + data[1] + "' created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openUpdateUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        Object[] rowData = new Object[tableModel.getColumnCount()];
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            rowData[i] = tableModel.getValueAt(modelRow, i);
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UserFormDialog dialog = new UserFormDialog(parentFrame, "Update User Profile", UserFormDialog.FormMode.UPDATE, rowData);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Object[] updatedData = dialog.getUserData();
            for (int i = 0; i < updatedData.length; i++) {
                tableModel.setValueAt(updatedData[i], modelRow, i);
            }
            JOptionPane.showMessageDialog(this, "User profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openMaintainPasswordDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table to maintain password/credentials.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        Object[] rowData = new Object[tableModel.getColumnCount()];
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            rowData[i] = tableModel.getValueAt(modelRow, i);
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        UserFormDialog dialog = new UserFormDialog(parentFrame, "Maintain User Credentials", UserFormDialog.FormMode.RESET_PASSWORD, rowData);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            JOptionPane.showMessageDialog(this, "Password updated successfully for " + rowData[1] + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleDeleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        String username = (String) tableModel.getValueAt(modelRow, 1);
        String currentStatus = (String) tableModel.getValueAt(modelRow, 7);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Do you want to toggle the status or remove user '" + username + "'?",
                "Confirm User Action",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String newStatus = "ACTIVE".equalsIgnoreCase(currentStatus) ? "INACTIVE" : "ACTIVE";
            tableModel.setValueAt(newStatus, modelRow, 7);
            JOptionPane.showMessageDialog(this, "User status updated to " + newStatus, "Status Updated", JOptionPane.INFORMATION_MESSAGE);
        } else if (confirm == JOptionPane.NO_OPTION) {
            tableModel.removeRow(modelRow);
            JOptionPane.showMessageDialog(this, "User removed from table.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void applyFilter() {
        String role = (String) roleFilterComboBox.getSelectedItem();
        String searchText = searchField.getText().trim();

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                // Role filter
                if (role != null && !"All Roles".equals(role)) {
                    String rowRole = entry.getStringValue(4);
                    if (!role.equalsIgnoreCase(rowRole)) {
                        return false;
                    }
                }
                // Text search filter
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

    /**
     * Seeds initial user data matching project requirements (Admin, Lecturers, TOs, Students).
     */
    private void loadInitialUserData() {
        tableModel.setRowCount(0);

        // 1 Admin
        tableModel.addRow(new Object[]{1, "admin", "System", "Admin", "Admin", "admin@fot.ruh.ac.lk", "0711234567", "ACTIVE"});

        // 5 Lecturers
        tableModel.addRow(new Object[]{2, "lec_kamal", "Kamal", "Perera", "Lecturer", "kamal@fot.ruh.ac.lk", "0771122334", "ACTIVE"});
        tableModel.addRow(new Object[]{3, "lec_sunil", "Sunil", "Fernando", "Lecturer", "sunil@fot.ruh.ac.lk", "0772233445", "ACTIVE"});
        tableModel.addRow(new Object[]{4, "lec_anura", "Anura", "Silva", "Lecturer", "anura@fot.ruh.ac.lk", "0773344556", "ACTIVE"});
        tableModel.addRow(new Object[]{5, "lec_nimal", "Nimal", "Jayasinghe", "Lecturer", "nimal@fot.ruh.ac.lk", "0774455667", "ACTIVE"});
        tableModel.addRow(new Object[]{6, "lec_malini", "Malini", "Gunaratne", "Lecturer", "malini@fot.ruh.ac.lk", "0775566778", "ACTIVE"});

        // 4 Technical Officers
        tableModel.addRow(new Object[]{7, "to_bandara", "Bandara", "Herath", "Technical Officer", "bandara@fot.ruh.ac.lk", "0761122334", "ACTIVE"});
        tableModel.addRow(new Object[]{8, "to_chaminda", "Chaminda", "Kulatunga", "Technical Officer", "chaminda@fot.ruh.ac.lk", "0762233445", "ACTIVE"});
        tableModel.addRow(new Object[]{9, "to_saman", "Saman", "Kumara", "Technical Officer", "saman@fot.ruh.ac.lk", "0763344556", "ACTIVE"});
        tableModel.addRow(new Object[]{10, "to_rohan", "Rohan", "Wickrama", "Technical Officer", "rohan@fot.ruh.ac.lk", "0764455667", "ACTIVE"});

        // Undergraduates (Sample)
        tableModel.addRow(new Object[]{11, "tg2021001", "Kasun", "Kalhara", "Undergraduate", "tg2021001@fot.ruh.ac.lk", "0701122334", "ACTIVE"});
        tableModel.addRow(new Object[]{12, "tg2021002", "Nipuni", "Hansika", "Undergraduate", "tg2021002@fot.ruh.ac.lk", "0702233445", "ACTIVE"});
        tableModel.addRow(new Object[]{13, "tg2021003", "Dulshan", "Pradeep", "Undergraduate", "tg2021003@fot.ruh.ac.lk", "0703344556", "ACTIVE"});
        tableModel.addRow(new Object[]{14, "tg2021004", "Sanduni", "Kavindya", "Undergraduate", "tg2021004@fot.ruh.ac.lk", "0704455667", "ACTIVE"});
        tableModel.addRow(new Object[]{15, "tg2021005", "Thisara", "Madusanka", "Undergraduate", "tg2021005@fot.ruh.ac.lk", "0705566778", "ACTIVE"});
    }

    public JTable getUserTable() {
        return userTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}
