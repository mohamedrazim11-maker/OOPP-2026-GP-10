package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LecturerController;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Lecturer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Lecturer Profile Management Panel.
 * Implements Wireframe 02 My Profile (locked fields).
 */
public class LecturerProfilePanel extends JPanel {
    private JTextField txtUserId, txtUsername, txtFirstName, txtLastName, txtEmail, txtDepartment, txtContact;
    private JPasswordField txtPassword;
    private JButton btnSave;
    private Lecturer currentLecturer;
    private final LecturerController controller = new LecturerController();

    public LecturerProfilePanel(Lecturer lecturer) {
        this.currentLecturer = lecturer;
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(20, 30, 20, 30));
        initUI();
        loadData();
    }

    private void initUI() {
        // Heading per Wireframe 02 My Profile
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel titleLabel = new JLabel("Lecturer Profile Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(15, 23, 42));
        headerPanel.add(titleLabel);

        // Form fields container (vertical stacked fields per wireframe)
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);

        // 1. Locked fields (grey background)
        txtUserId = createStyledField(false);
        txtUsername = createStyledField(false);
        txtPassword = new JPasswordField("••••••••");
        txtPassword.setEditable(false);
        styleLockedField(txtPassword);

        // 2. Editable fields
        txtFirstName = createStyledField(true);
        txtLastName = createStyledField(true);
        txtEmail = createStyledField(true);
        txtDepartment = createStyledField(true);
        txtContact = createStyledField(true);

        formContainer.add(createFieldBlock("User ID (Locked)", txtUserId));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Username (Locked)", txtUsername));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Password (Locked)", txtPassword));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("First Name", txtFirstName));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Last Name", txtLastName));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Email", txtEmail));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Department", txtDepartment));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Contact No", txtContact));
        formContainer.add(Box.createVerticalStrut(18));

        // Bottom Button per Wireframe 02
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setOpaque(false);

        btnSave = new JButton("Update Profile");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(new Color(15, 23, 42)); // Dark solid per wireframe
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(15, 23, 42), 1),
                new EmptyBorder(8, 20, 8, 20)
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

    private JPanel createFieldBlock(String labelText, JComponent field) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(71, 85, 105));
        label.setBorder(new EmptyBorder(0, 2, 4, 0));

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        block.add(label);
        block.add(field);
        return block;
    }

    private JTextField createStyledField(boolean editable) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setEditable(editable);
        if (!editable) {
            styleLockedField(tf);
        } else {
            tf.setBackground(Color.WHITE);
            tf.setForeground(new Color(15, 23, 42));
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                    new EmptyBorder(6, 10, 6, 10)
            ));
        }
        return tf;
    }

    private void styleLockedField(JComponent comp) {
        comp.setBackground(new Color(226, 232, 240)); // Locked grey tone per wireframe
        comp.setForeground(new Color(100, 116, 139));
        comp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void loadData() {
        if (currentLecturer != null) {
            txtUserId.setText(String.valueOf(currentLecturer.getUserId()));
            txtUsername.setText(currentLecturer.getUsername());
            txtFirstName.setText(currentLecturer.getFirstName());
            txtLastName.setText(currentLecturer.getLastName());
            txtEmail.setText(currentLecturer.getEmail());
            txtDepartment.setText(currentLecturer.getDepartment());
            txtContact.setText(currentLecturer.getContactNo());
        }
    }

    private void saveProfile() {
        currentLecturer.setFirstName(txtFirstName.getText().trim());
        currentLecturer.setLastName(txtLastName.getText().trim());
        currentLecturer.setEmail(txtEmail.getText().trim());
        currentLecturer.setDepartment(txtDepartment.getText().trim());
        currentLecturer.setContactNo(txtContact.getText().trim());

        if (controller.updateProfile(currentLecturer)) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
