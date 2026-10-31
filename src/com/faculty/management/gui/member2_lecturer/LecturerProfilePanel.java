package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LecturerController;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Lecturer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * Lecturer Profile Management Panel.
 * Implements Wireframe 02 My Profile (locked username/password, editable contact & profile pic).
 */
public class LecturerProfilePanel extends JPanel {
    private JTextField txtUserId, txtUsername, txtFirstName, txtLastName, txtEmail, txtDepartment, txtContact, txtProfilePic;
    private JPasswordField txtPassword;
    private JButton btnBrowsePic, btnSave;
    private Lecturer currentLecturer;
    private final LecturerController controller = new LecturerController();

    public LecturerProfilePanel(Lecturer lecturer) {
        this.currentLecturer = lecturer;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);
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

        JLabel subtitleLabel = new JLabel("View and update personal details, contact information, and profile picture.");
        subtitleLabel.setFont(UITheme.FONT_BODY);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitleLabel);

        // Form fields container (vertical stacked fields per wireframe)
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);

        // 1. Locked fields (grey background) - Username and Password strictly cannot be changed
        txtUserId = createStyledField(false);
        txtUsername = createStyledField(false);
        txtPassword = new JPasswordField("••••••••");
        txtPassword.setEditable(false);
        styleLockedField(txtPassword);

        // 2. Editable fields
        txtFirstName = createStyledField(true);
        txtLastName = createStyledField(true);
        txtEmail = createStyledField(true);
        txtDepartment = createStyledField(false); // Department is assigned by faculty admin
        styleLockedField(txtDepartment);
        txtContact = createStyledField(true);

        // Profile Picture with Browse button
        txtProfilePic = createStyledField(true);
        btnBrowsePic = new JButton("Choose Picture...");
        btnBrowsePic.setFont(UITheme.FONT_SMALL_BOLD);
        btnBrowsePic.setBackground(Color.WHITE);
        btnBrowsePic.setForeground(UITheme.TEXT_BODY);
        btnBrowsePic.setFocusPainted(false);
        btnBrowsePic.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBrowsePic.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnBrowsePic.addActionListener(e -> chooseProfilePicture());

        JPanel picChooserPanel = new JPanel(new BorderLayout(8, 0));
        picChooserPanel.setOpaque(false);
        picChooserPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        picChooserPanel.add(txtProfilePic, BorderLayout.CENTER);
        picChooserPanel.add(btnBrowsePic, BorderLayout.EAST);

        formContainer.add(createFieldBlock("User ID (Locked)", txtUserId));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Username (Locked - Cannot be changed)", txtUsername));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Password (Locked - Cannot be changed)", txtPassword));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("First Name", txtFirstName));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Last Name", txtLastName));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Email Address", txtEmail));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Department", txtDepartment));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Contact No", txtContact));
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(createFieldBlock("Profile Picture Location", picChooserPanel));
        formContainer.add(Box.createVerticalStrut(18));

        // Bottom Button per Wireframe 02
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
                new EmptyBorder(8, 22, 8, 22)
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

    private void chooseProfilePicture() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Profile Picture");
        chooser.setFileFilter(new FileNameExtensionFilter("Image Files (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png"));
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            txtProfilePic.setText(selected.getAbsolutePath());
        }
    }

    private JPanel createFieldBlock(String labelText, JComponent field) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_SMALL_BOLD);
        label.setForeground(UITheme.TEXT_BODY);
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
            tf.setForeground(UITheme.TEXT_MAIN);
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BORDER_INPUT, 1),
                    new EmptyBorder(6, 10, 6, 10)
            ));
        }
        return tf;
    }

    private void styleLockedField(JComponent comp) {
        comp.setBackground(new Color(238, 232, 225));
        comp.setForeground(UITheme.TEXT_MUTED);
        comp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void loadData() {
        if (currentLecturer != null) {
            txtUserId.setText(String.valueOf(currentLecturer.getUserId()));
            txtUsername.setText(currentLecturer.getUsername());
            txtFirstName.setText(currentLecturer.getFirstName() != null ? currentLecturer.getFirstName() : "");
            txtLastName.setText(currentLecturer.getLastName() != null ? currentLecturer.getLastName() : "");
            txtEmail.setText(currentLecturer.getEmail() != null ? currentLecturer.getEmail() : "");
            txtDepartment.setText(currentLecturer.getDepartment() != null ? currentLecturer.getDepartment() : "Faculty of Technology");
            txtContact.setText(currentLecturer.getContactNo() != null ? currentLecturer.getContactNo() : "");
            txtProfilePic.setText(currentLecturer.getProfilePic() != null ? currentLecturer.getProfilePic() : "");
        }
    }

    private void saveProfile() {
        currentLecturer.setFirstName(txtFirstName.getText().trim());
        currentLecturer.setLastName(txtLastName.getText().trim());
        currentLecturer.setEmail(txtEmail.getText().trim());
        currentLecturer.setContactNo(txtContact.getText().trim());
        currentLecturer.setProfilePic(txtProfilePic.getText().trim());

        if (controller.updateProfile(currentLecturer)) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
