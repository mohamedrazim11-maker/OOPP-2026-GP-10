package com.faculty.management.gui.member1_admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * User Form Dialog for Member 1 (Admin & User Management).
 * Allows Admin to:
 * - Create new users
 * - Update existing users
 * - Maintain usernames and passwords
 * 
 * Pure Java Swing - Adheres to OOP principles (Encapsulation, Inheritance).
 */
public class UserFormDialog extends JDialog {

    public enum FormMode {
        CREATE,
        UPDATE,
        RESET_PASSWORD
    }

    private final FormMode mode;
    private boolean saved = false;

    // Form Components (Encapsulated)
    private JComboBox<String> roleComboBox;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField contactField;
    private JComboBox<String> departmentComboBox;
    private JTextField batchField;
    private JComboBox<String> statusComboBox;
    private JLabel errorLabel;
    private JButton saveButton;
    private JButton cancelButton;

    // Extracted Form Data
    private Object[] userData;

    public UserFormDialog(JFrame parent, String title, FormMode mode, Object[] initialData) {
        super(parent, title, true);
        this.mode = mode;
        this.userData = initialData;
        initComponents();
        if (initialData != null) {
            populateFields(initialData);
        }
    }

    private void initComponents() {
        setSize(520, 620);
        setMinimumSize(new Dimension(480, 560));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        setResizable(false);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 58, 138)); // Deep Navy
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        String headerTitle = (mode == FormMode.CREATE) ? "Create New User Profile" :
                (mode == FormMode.UPDATE) ? "Update User Profile" : "Maintain User Credentials";
        JLabel titleLabel = new JLabel(headerTitle);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 25, 15, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.weightx = 1.0;

        // Error message banner
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setForeground(new Color(220, 38, 38));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(errorLabel, gbc);
        gbc.gridwidth = 1;

        // 1. Role Selection
        JLabel roleLabel = createFieldLabel("User Role *");
        String[] roles = {"Admin", "Lecturer", "Technical Officer", "Undergraduate"};
        roleComboBox = new JComboBox<>(roles);
        styleComboBox(roleComboBox);
        roleComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                onRoleChanged((String) e.getItem());
            }
        });

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(roleLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(roleComboBox, gbc);

        // 2. Username
        JLabel usernameLabel = createFieldLabel("Username *");
        usernameField = createStyledTextField();
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(usernameField, gbc);

        // 3. Password
        JLabel passwordLabel = createFieldLabel("Password *");
        passwordField = new JPasswordField();
        stylePasswordField(passwordField);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(passwordField, gbc);

        // Show password toggle
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        showPasswordCheckBox.setBackground(Color.WHITE);
        showPasswordCheckBox.setFocusPainted(false);
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(showPasswordCheckBox, gbc);

        // 4. First Name
        JLabel firstNameLabel = createFieldLabel("First Name *");
        firstNameField = createStyledTextField();
        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(firstNameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 5; formPanel.add(firstNameField, gbc);

        // 5. Last Name
        JLabel lastNameLabel = createFieldLabel("Last Name *");
        lastNameField = createStyledTextField();
        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(lastNameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 6; formPanel.add(lastNameField, gbc);

        // 6. Email
        JLabel emailLabel = createFieldLabel("Email *");
        emailField = createStyledTextField();
        gbc.gridx = 0; gbc.gridy = 7; formPanel.add(emailLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 7; formPanel.add(emailField, gbc);

        // 7. Contact No
        JLabel contactLabel = createFieldLabel("Contact No");
        contactField = createStyledTextField();
        gbc.gridx = 0; gbc.gridy = 8; formPanel.add(contactLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 8; formPanel.add(contactField, gbc);

        // 8. Department (For Lecturer & Technical Officer)
        JLabel departmentLabel = createFieldLabel("Department");
        String[] departments = {"Department of Information & Communication Technology", "Department of Biosystems Technology", "Department of Engineering Technology", "Multidisciplinary"};
        departmentComboBox = new JComboBox<>(departments);
        styleComboBox(departmentComboBox);
        gbc.gridx = 0; gbc.gridy = 9; formPanel.add(departmentLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 9; formPanel.add(departmentComboBox, gbc);

        // 9. Batch (For Undergraduate)
        JLabel batchLabel = createFieldLabel("Batch / Intake");
        batchField = createStyledTextField();
        batchField.setText("2021/2022");
        gbc.gridx = 0; gbc.gridy = 10; formPanel.add(batchLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 10; formPanel.add(batchField, gbc);

        // 10. Status
        JLabel statusLabel = createFieldLabel("Status *");
        String[] statuses = {"ACTIVE", "INACTIVE"};
        statusComboBox = new JComboBox<>(statuses);
        styleComboBox(statusComboBox);
        gbc.gridx = 0; gbc.gridy = 11; formPanel.add(statusLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 11; formPanel.add(statusComboBox, gbc);

        // Mode adjustments
        applyModeRestrictions();

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(new Color(248, 250, 252));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setPreferredSize(new Dimension(90, 34));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dispose());

        saveButton = new JButton((mode == FormMode.CREATE) ? "Create User" : (mode == FormMode.UPDATE) ? "Save Changes" : "Update Password");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveButton.setBackground(new Color(37, 99, 235)); // Primary Blue
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(130, 34));
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> handleSave());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void applyModeRestrictions() {
        if (mode == FormMode.RESET_PASSWORD) {
            roleComboBox.setEnabled(false);
            firstNameField.setEnabled(false);
            lastNameField.setEnabled(false);
            emailField.setEnabled(false);
            contactField.setEnabled(false);
            departmentComboBox.setEnabled(false);
            batchField.setEnabled(false);
            statusComboBox.setEnabled(false);
        } else if (mode == FormMode.UPDATE) {
            // In update mode, username can be maintained or password updated
        }
    }

    private void onRoleChanged(String selectedRole) {
        if ("Undergraduate".equalsIgnoreCase(selectedRole)) {
            batchField.setEnabled(true);
            departmentComboBox.setEnabled(true);
        } else if ("Lecturer".equalsIgnoreCase(selectedRole) || "Technical Officer".equalsIgnoreCase(selectedRole)) {
            batchField.setEnabled(false);
            departmentComboBox.setEnabled(true);
        } else {
            batchField.setEnabled(false);
            departmentComboBox.setEnabled(false);
        }
    }

    private void populateFields(Object[] data) {
        // data columns: [0: ID, 1: Username, 2: First Name, 3: Last Name, 4: Role, 5: Email, 6: Contact, 7: Status]
        if (data.length > 1 && data[1] != null) usernameField.setText(data[1].toString());
        if (data.length > 2 && data[2] != null) firstNameField.setText(data[2].toString());
        if (data.length > 3 && data[3] != null) lastNameField.setText(data[3].toString());
        if (data.length > 4 && data[4] != null) roleComboBox.setSelectedItem(data[4].toString());
        if (data.length > 5 && data[5] != null) emailField.setText(data[5].toString());
        if (data.length > 6 && data[6] != null) contactField.setText(data[6].toString());
        if (data.length > 7 && data[7] != null) statusComboBox.setSelectedItem(data[7].toString());
    }

    private void handleSave() {
        String role = (String) roleComboBox.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String status = (String) statusComboBox.getSelectedItem();

        // Validation & Exception Handling
        try {
            if (username.isEmpty()) {
                throw new IllegalArgumentException("Username is required.");
            }
            if (username.length() < 3) {
                throw new IllegalArgumentException("Username must be at least 3 characters.");
            }
            if (mode == FormMode.CREATE && password.isEmpty()) {
                throw new IllegalArgumentException("Password is required for new users.");
            }
            if (mode != FormMode.RESET_PASSWORD) {
                if (firstName.isEmpty() || lastName.isEmpty()) {
                    throw new IllegalArgumentException("First name and Last name are required.");
                }
                if (email.isEmpty() || !email.contains("@")) {
                    throw new IllegalArgumentException("Please enter a valid email address.");
                }
            }

            // Assemble updated row data
            userData = new Object[]{
                    (userData != null && userData.length > 0) ? userData[0] : (int)(Math.random() * 900 + 100),
                    username,
                    firstName,
                    lastName,
                    role,
                    email,
                    contact,
                    status
            };

            saved = true;
            dispose();

        } catch (IllegalArgumentException ex) {
            errorLabel.setText("⚠️ " + ex.getMessage());
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public Object[] getUserData() {
        return userData;
    }

    // UI Helper methods
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(51, 65, 85));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(240, 32));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(3, 8, 3, 8)
        ));
        return tf;
    }

    private void stylePasswordField(JPasswordField pf) {
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pf.setPreferredSize(new Dimension(240, 32));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(3, 8, 3, 8)
        ));
    }

    private void styleComboBox(JComboBox<String> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setPreferredSize(new Dimension(240, 32));
    }
}
