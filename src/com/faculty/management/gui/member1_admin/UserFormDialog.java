package com.faculty.management.gui.member1_admin;

import com.faculty.management.exception.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * User Form Dialog for Member 1 (Admin & User Management).
 * 
 * Supports:
 * - View user details (Read-only view)
 * - Create new users
 * - Update existing user profiles
 * - Assign user roles
 * - Maintain usernames and passwords
 * 
 * Demonstrates:
 * - Classes and Objects: Dialog and components instantiation.
 * - Inheritance: Extends JDialog.
 * - Abstraction & Polymorphism: Mode-based polymorphic configuration & abstraction of form validation.
 * - Encapsulation: Private fields with accessor methods.
 * - Error & Exception Handling: Uses ValidationException to validate user inputs.
 */
public class UserFormDialog extends JDialog {

    public enum FormMode {
        VIEW,
        CREATE,
        UPDATE,
        ASSIGN_ROLE,
        MAINTAIN_CREDENTIALS
    }

    // Encapsulated Fields
    private final FormMode mode;
    private boolean saved = false;
    private Object[] userData;

    // UI Components (Encapsulated)
    private JComboBox<String> roleComboBox;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
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

    public UserFormDialog(JFrame parent, String title, FormMode mode, Object[] initialData) {
        super(parent, title, true);
        this.mode = mode;
        this.userData = initialData;
        initComponents();
        if (initialData != null) {
            populateFields(initialData);
        }
        applyModeRestrictions();
    }

    private void initComponents() {
        setSize(540, 660);
        setMinimumSize(new Dimension(500, 580));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        setResizable(false);

        // 1. Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 58, 138)); // Deep Navy Blue
        headerPanel.setBorder(new EmptyBorder(16, 22, 16, 22));

        String headerTitle = getHeaderTitle();
        JLabel titleLabel = new JLabel(headerTitle);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // 2. Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Error message label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setForeground(new Color(220, 38, 38));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(errorLabel, gbc);
        gbc.gridwidth = 1;

        // Role Selection
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

        // Username
        JLabel usernameLabel = createFieldLabel("Username *");
        usernameField = createStyledTextField();
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = createFieldLabel(mode == FormMode.MAINTAIN_CREDENTIALS ? "New Password *" : "Password *");
        passwordField = new JPasswordField();
        stylePasswordField(passwordField);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(passwordField, gbc);

        // Confirm Password (for credentials maintenance & create)
        JLabel confirmPasswordLabel = createFieldLabel("Confirm Password *");
        confirmPasswordField = new JPasswordField();
        stylePasswordField(confirmPasswordField);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(confirmPasswordField, gbc);

        // Show Password toggle
        showPasswordCheckBox = new JCheckBox("Show Passwords");
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        showPasswordCheckBox.setBackground(Color.WHITE);
        showPasswordCheckBox.setFocusPainted(false);
        showPasswordCheckBox.addActionListener(e -> {
            char echo = showPasswordCheckBox.isSelected() ? (char) 0 : '•';
            passwordField.setEchoChar(echo);
            confirmPasswordField.setEchoChar(echo);
        });
        gbc.gridx = 1; gbc.gridy = 5; formPanel.add(showPasswordCheckBox, gbc);

        // First Name
        JLabel firstNameLabel = createFieldLabel("First Name *");
        firstNameField = createStyledTextField();
        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(firstNameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 6; formPanel.add(firstNameField, gbc);

        // Last Name
        JLabel lastNameLabel = createFieldLabel("Last Name *");
        lastNameField = createStyledTextField();
        gbc.gridx = 0; gbc.gridy = 7; formPanel.add(lastNameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 7; formPanel.add(lastNameField, gbc);

        // Email
        JLabel emailLabel = createFieldLabel("Email *");
        emailField = createStyledTextField();
        gbc.gridx = 0; gbc.gridy = 8; formPanel.add(emailLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 8; formPanel.add(emailField, gbc);

        // Contact No
        JLabel contactLabel = createFieldLabel("Contact No");
        contactField = createStyledTextField();
        gbc.gridx = 0; gbc.gridy = 9; formPanel.add(contactLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 9; formPanel.add(contactField, gbc);

        // Department
        JLabel departmentLabel = createFieldLabel("Department");
        String[] departments = {
                "Department of Information & Communication Technology",
                "Department of Biosystems Technology",
                "Department of Engineering Technology",
                "Multidisciplinary"
        };
        departmentComboBox = new JComboBox<>(departments);
        styleComboBox(departmentComboBox);
        gbc.gridx = 0; gbc.gridy = 10; formPanel.add(departmentLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 10; formPanel.add(departmentComboBox, gbc);

        // Batch / Intake
        JLabel batchLabel = createFieldLabel("Batch / Intake");
        batchField = createStyledTextField();
        batchField.setText("2021/2022");
        gbc.gridx = 0; gbc.gridy = 11; formPanel.add(batchLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 11; formPanel.add(batchField, gbc);

        // Status
        JLabel statusLabel = createFieldLabel("Status *");
        String[] statuses = {"ACTIVE", "INACTIVE"};
        statusComboBox = new JComboBox<>(statuses);
        styleComboBox(statusComboBox);
        gbc.gridx = 0; gbc.gridy = 12; formPanel.add(statusLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 12; formPanel.add(statusComboBox, gbc);

        // 3. Bottom Action Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(new Color(248, 250, 252));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        cancelButton = new JButton(mode == FormMode.VIEW ? "Close" : "Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setPreferredSize(new Dimension(90, 34));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dispose());

        saveButton = new JButton(getSaveButtonText());
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveButton.setBackground(new Color(37, 99, 235));
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(150, 34));
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> handleSave());

        buttonPanel.add(cancelButton);
        if (mode != FormMode.VIEW) {
            buttonPanel.add(saveButton);
        }

        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private String getHeaderTitle() {
        switch (mode) {
            case VIEW:
                return "👤 User Profile Details (Read-Only)";
            case CREATE:
                return "➕ Create New User Profile";
            case UPDATE:
                return "✏️ Update User Profile";
            case ASSIGN_ROLE:
                return "🎭 Assign User Role";
            case MAINTAIN_CREDENTIALS:
                return "🔑 Maintain Username & Password";
            default:
                return "User Form";
        }
    }

    private String getSaveButtonText() {
        switch (mode) {
            case CREATE:
                return "Create User";
            case UPDATE:
                return "Save Changes";
            case ASSIGN_ROLE:
                return "Assign Role";
            case MAINTAIN_CREDENTIALS:
                return "Update Credentials";
            default:
                return "Save";
        }
    }

    private void applyModeRestrictions() {
        switch (mode) {
            case VIEW:
                roleComboBox.setEnabled(false);
                usernameField.setEditable(false);
                passwordField.setEnabled(false);
                confirmPasswordField.setEnabled(false);
                showPasswordCheckBox.setEnabled(false);
                firstNameField.setEditable(false);
                lastNameField.setEditable(false);
                emailField.setEditable(false);
                contactField.setEditable(false);
                departmentComboBox.setEnabled(false);
                batchField.setEditable(false);
                statusComboBox.setEnabled(false);
                passwordField.setText("********");
                confirmPasswordField.setText("********");
                break;

            case ASSIGN_ROLE:
                roleComboBox.setEnabled(true);
                usernameField.setEditable(false);
                passwordField.setEnabled(false);
                confirmPasswordField.setEnabled(false);
                showPasswordCheckBox.setEnabled(false);
                firstNameField.setEditable(false);
                lastNameField.setEditable(false);
                emailField.setEditable(false);
                contactField.setEditable(false);
                departmentComboBox.setEnabled(true);
                batchField.setEnabled(true);
                statusComboBox.setEnabled(false);
                break;

            case MAINTAIN_CREDENTIALS:
                roleComboBox.setEnabled(false);
                usernameField.setEditable(true); // Maintain username
                passwordField.setEnabled(true);  // Maintain password
                confirmPasswordField.setEnabled(true);
                showPasswordCheckBox.setEnabled(true);
                firstNameField.setEditable(false);
                lastNameField.setEditable(false);
                emailField.setEditable(false);
                contactField.setEditable(false);
                departmentComboBox.setEnabled(false);
                batchField.setEditable(false);
                statusComboBox.setEnabled(false);
                break;

            case UPDATE:
                passwordField.setEnabled(false);
                confirmPasswordField.setEnabled(false);
                showPasswordCheckBox.setEnabled(false);
                break;

            case CREATE:
            default:
                break;
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
        if (data.length > 1 && data[1] != null) usernameField.setText(data[1].toString());
        if (data.length > 2 && data[2] != null) firstNameField.setText(data[2].toString());
        if (data.length > 3 && data[3] != null) lastNameField.setText(data[3].toString());
        if (data.length > 4 && data[4] != null) roleComboBox.setSelectedItem(data[4].toString());
        if (data.length > 5 && data[5] != null) emailField.setText(data[5].toString());
        if (data.length > 6 && data[6] != null) contactField.setText(data[6].toString());
        if (data.length > 7 && data[7] != null) statusComboBox.setSelectedItem(data[7].toString());
    }

    /**
     * Validates input values using ValidationException and updates userData.
     */
    private void handleSave() {
        String role = (String) roleComboBox.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String status = (String) statusComboBox.getSelectedItem();

        try {
            // Validate based on mode
            if (mode == FormMode.MAINTAIN_CREDENTIALS) {
                if (username.isEmpty()) {
                    throw new ValidationException("Username cannot be empty.");
                }
                if (username.length() < 3) {
                    throw new ValidationException("Username must be at least 3 characters long.");
                }
                if (password.isEmpty()) {
                    throw new ValidationException("New password cannot be empty.");
                }
                if (password.length() < 6) {
                    throw new ValidationException("Password must be at least 6 characters long.");
                }
                if (!password.equals(confirmPassword)) {
                    throw new ValidationException("Passwords do not match. Please re-type password.");
                }
            } else if (mode == FormMode.ASSIGN_ROLE) {
                if (role == null || role.isEmpty()) {
                    throw new ValidationException("Please select a valid user role to assign.");
                }
            } else if (mode == FormMode.CREATE) {
                if (username.isEmpty()) {
                    throw new ValidationException("Username is required.");
                }
                if (username.length() < 3) {
                    throw new ValidationException("Username must be at least 3 characters long.");
                }
                if (password.isEmpty()) {
                    throw new ValidationException("Password is required for a new user.");
                }
                if (password.length() < 6) {
                    throw new ValidationException("Password must be at least 6 characters long.");
                }
                if (!password.equals(confirmPassword)) {
                    throw new ValidationException("Passwords do not match.");
                }
                if (firstName.isEmpty() || lastName.isEmpty()) {
                    throw new ValidationException("First Name and Last Name are required.");
                }
                if (email.isEmpty() || !email.contains("@")) {
                    throw new ValidationException("Please enter a valid email address.");
                }
            } else if (mode == FormMode.UPDATE) {
                if (username.isEmpty()) {
                    throw new ValidationException("Username is required.");
                }
                if (firstName.isEmpty() || lastName.isEmpty()) {
                    throw new ValidationException("First Name and Last Name are required.");
                }
                if (email.isEmpty() || !email.contains("@")) {
                    throw new ValidationException("Please enter a valid email address.");
                }
            }

            // Construct new/updated user record array
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

        } catch (ValidationException ex) {
            errorLabel.setText("⚠️ " + ex.getMessage());
        } catch (Exception ex) {
            errorLabel.setText("⚠️ Unexpected error: " + ex.getMessage());
        }
    }

    // Encapsulated Getters
    public boolean isSaved() {
        return saved;
    }

    public Object[] getUserData() {
        return userData;
    }

    public FormMode getMode() {
        return mode;
    }

    // UI Helper methods (Encapsulation)
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

