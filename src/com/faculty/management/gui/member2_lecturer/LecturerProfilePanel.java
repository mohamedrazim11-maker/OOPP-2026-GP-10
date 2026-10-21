package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LecturerController;
import com.faculty.management.model.Lecturer;

import javax.swing.*;
import java.awt.*;

public class LecturerProfilePanel extends JPanel {
    private JTextField txtUserId, txtUsername, txtFirstName, txtLastName, txtEmail, txtDepartment, txtContact;
    private JPasswordField txtPassword;
    private JButton btnSave;
    private Lecturer currentLecturer;
    private final LecturerController controller = new LecturerController();

    public LecturerProfilePanel(Lecturer lecturer) {
        this.currentLecturer = lecturer;
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtUserId = new JTextField(); txtUserId.setEditable(false);
        txtUsername = new JTextField(); txtUsername.setEditable(false); // Locked
        txtPassword = new JPasswordField("********"); txtPassword.setEditable(false); // Locked
        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtEmail = new JTextField();
        txtDepartment = new JTextField();
        txtContact = new JTextField();

        formPanel.add(new JLabel("User ID (Locked):")); formPanel.add(txtUserId);
        formPanel.add(new JLabel("Username (Locked):")); formPanel.add(txtUsername);
        formPanel.add(new JLabel("Password (Locked):")); formPanel.add(txtPassword);
        formPanel.add(new JLabel("First Name:")); formPanel.add(txtFirstName);
        formPanel.add(new JLabel("Last Name:")); formPanel.add(txtLastName);
        formPanel.add(new JLabel("Email:")); formPanel.add(txtEmail);
        formPanel.add(new JLabel("Department:")); formPanel.add(txtDepartment);
        formPanel.add(new JLabel("Contact No:")); formPanel.add(txtContact);

        btnSave = new JButton("Update Profile");
        btnSave.addActionListener(e -> saveProfile());

        add(new JLabel("Lecturer Profile Management", SwingConstants.CENTER), BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(btnSave, BorderLayout.SOUTH);
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
        currentLecturer.setFirstName(txtFirstName.getText());
        currentLecturer.setLastName(txtLastName.getText());
        currentLecturer.setEmail(txtEmail.getText());
        currentLecturer.setDepartment(txtDepartment.getText());
        currentLecturer.setContactNo(txtContact.getText());

        if (controller.updateProfile(currentLecturer)) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
