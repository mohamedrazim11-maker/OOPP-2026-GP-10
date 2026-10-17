package com.faculty.management.gui.member3_attendance;

import com.faculty.management.dao.UserDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TechnicalOfficerProfileDialog extends JDialog {

    private User currentUser;
    private UserDAO userDAO;

    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JTextField txtContactNo;
    private JTextField txtUsername;

    public TechnicalOfficerProfileDialog(JFrame parent, User user) {
        super(parent, "My Profile - Technical Officer", true);
        this.currentUser = user;
        this.userDAO = new UserDAO();
        initComponents();
    }

    private void initComponents() {
        setSize(450, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(15, 23, 42));
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField(currentUser.getUsername());
        txtUsername.setEditable(false);
        txtUsername.setBackground(new Color(241, 245, 249));
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Role:"));
        JTextField txtRole = new JTextField(currentUser.getRoleTitle());
        txtRole.setEditable(false);
        txtRole.setBackground(new Color(241, 245, 249));
        formPanel.add(txtRole);

        formPanel.add(new JLabel("First Name:"));
        txtFirstName = new JTextField(currentUser.getFirstName());
        formPanel.add(txtFirstName);

        formPanel.add(new JLabel("Last Name:"));
        txtLastName = new JTextField(currentUser.getLastName());
        formPanel.add(txtLastName);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField(currentUser.getEmail());
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Contact No:"));
        txtContactNo = new JTextField(currentUser.getContactNo());
        formPanel.add(txtContactNo);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(Color.WHITE);

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBackground(new Color(16, 185, 129));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> updateProfile());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void updateProfile() {
        String fname = txtFirstName.getText().trim();
        String lname = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String contact = txtContactNo.getText().trim();

        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentUser.setFirstName(fname);
        currentUser.setLastName(lname);
        currentUser.setEmail(email);
        currentUser.setContactNo(contact);

        try {
            boolean success = userDAO.updateUser(currentUser);
            if (success) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                // Optionally trigger a UI refresh on the dashboard if necessary
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
