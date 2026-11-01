package com.faculty.management.gui.member3_attendance;

import com.faculty.management.controller.TechnicalOfficerController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Technical Officer Profile Management Panel.
 * Embedded directly inside TechnicalOfficerDashboard tabbed pane.
 * Follows the clean, modern card-based profile wireframe.
 */
public class TechnicalOfficerProfilePanel extends JPanel {

    private User currentUser;
    private final TechnicalOfficerController controller;

    private JTextField txtUserId;
    private JTextField txtUsername;
    private JTextField txtRole;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JTextField txtContactNo;
    private JButton btnSave;

    public TechnicalOfficerProfilePanel(User user) {
        this.currentUser = user;
        this.controller = new TechnicalOfficerController();

        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        initUI();
        loadData();
    }

    private void initUI() {
        // Heading
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel titleLabel = new JLabel("Technical Officer Profile Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(15, 23, 42));
        headerPanel.add(titleLabel);

        JLabel subLabel = new JLabel("View staff account credentials and update personal contact information.");
        subLabel.setFont(UITheme.FONT_BODY);
        subLabel.setForeground(UITheme.TEXT_MUTED);
        headerPanel.add(subLabel);

        // Form fields container
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);

        // 1. Locked fields
        txtUserId = createStyledField(false);
        txtUsername = createStyledField(false);
        txtRole = createStyledField(false);

        // 2. Editable fields
        txtFirstName = createStyledField(true);
        txtLastName = createStyledField(true);
        txtEmail = createStyledField(true);
        txtContactNo = createStyledField(true);

        formContainer.add(createFieldBlock("Staff User ID (Locked)", txtUserId));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Username (Locked)", txtUsername));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Assigned Role (Locked)", txtRole));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("First Name", txtFirstName));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Last Name", txtLastName));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Email Address", txtEmail));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Contact Number", txtContactNo));
        formContainer.add(Box.createVerticalStrut(18));

        // Bottom Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setOpaque(false);

        btnSave = new JButton("Update Profile");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(UITheme.PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.PRIMARY, 1),
                new EmptyBorder(9, 24, 9, 24)
        ));
        btnSave.addActionListener(e -> saveProfile());
        buttonPanel.add(btnSave);

        formContainer.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(formContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createFieldBlock(String labelText, JTextField field) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);
        block.setMaximumSize(new Dimension(550, 65));
        block.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(51, 65, 85));
        label.setBorder(new EmptyBorder(0, 0, 4, 0));

        block.add(label);
        block.add(field);
        return block;
    }

    private JTextField createStyledField(boolean editable) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setMaximumSize(new Dimension(550, 36));
        tf.setPreferredSize(new Dimension(550, 36));
        tf.setEditable(editable);

        if (!editable) {
            tf.setBackground(new Color(241, 245, 249)); // Slate 100
            tf.setForeground(new Color(100, 116, 139)); // Slate 500
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225)),
                    new EmptyBorder(5, 10, 5, 10)
            ));
        } else {
            tf.setBackground(Color.WHITE);
            tf.setForeground(new Color(15, 23, 42));
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225)),
                    new EmptyBorder(5, 10, 5, 10)
            ));
        }
        return tf;
    }

    public void loadData() {
        if (currentUser != null) {
            txtUserId.setText(String.valueOf(currentUser.getUserId()));
            txtUsername.setText(currentUser.getUsername());
            txtRole.setText(currentUser.getRoleTitle() != null ? currentUser.getRoleTitle() : "Technical Officer");
            txtFirstName.setText(currentUser.getFirstName());
            txtLastName.setText(currentUser.getLastName());
            txtEmail.setText(currentUser.getEmail());
            txtContactNo.setText(currentUser.getContactNo());
        }
    }

    private void saveProfile() {
        String fname = txtFirstName.getText().trim();
        String lname = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String contact = txtContactNo.getText().trim();

        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All personal fields are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentUser.setFirstName(fname);
        currentUser.setLastName(lname);
        currentUser.setEmail(email);
        currentUser.setContactNo(contact);

        try {
            boolean success = controller.updateProfile(currentUser);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Technical Officer Profile updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ValidationException | DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
