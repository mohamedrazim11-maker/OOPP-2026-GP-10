package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.exception.BusinessRuleException;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Undergraduate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * Modern Student Profile view & edit screen.
 * Per university spec, a student may update ONLY their contact number and profile
 * picture — username, email, and academic identity stay strictly read-only.
 */
public class StudentProfilePanel extends JPanel {

    private final UndergraduateController controller;
    private final int studentId;

    private JLabel nameDisplayLabel;
    private JLabel usernameBadgeLabel;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField nameField;
    private JTextField roleField;
    private UITheme.ModernTextField contactField;
    private UITheme.ModernTextField profilePicField;
    private UITheme.CircularAvatar profileAvatar;
    private JPanel statusBanner;
    private JLabel statusLabel;

    public StudentProfilePanel(UndergraduateController controller, int studentId) {
        this.controller = controller;
        this.studentId = studentId;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(20, 24, 20, 24));
        buildUi();
        loadProfile();
    }

    private void buildUi() {
        // Heading
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        titleBox.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel heading = new JLabel("Student Profile Management");
        heading.setFont(UITheme.FONT_PAGE_TITLE);
        heading.setForeground(UITheme.TEXT_MAIN);

        JLabel subheading = new JLabel("View your official university record and manage personal contact information.");
        subheading.setFont(UITheme.FONT_BODY);
        subheading.setForeground(UITheme.TEXT_MUTED);

        titleBox.add(heading);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(subheading);

        // Status Feedback Banner
        statusBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        statusBanner.setBackground(UITheme.SUCCESS_LIGHT);
        statusBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.SUCCESS_BORDER, 1),
                new EmptyBorder(2, 8, 2, 8)
        ));
        statusBanner.setVisible(false);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_BODY_BOLD);
        statusBanner.add(statusLabel);

        // Main Profile Split Layout (Left: Photo Avatar Card, Right: Details Cards)
        JPanel splitPanel = new JPanel(new BorderLayout(20, 0));
        splitPanel.setOpaque(false);

        // -------------------------------------------------------------
        // LEFT: Avatar Showcase Card (Circular Profile Photo)
        // -------------------------------------------------------------
        UITheme.ModernCard leftAvatarCard = new UITheme.ModernCard(14);
        leftAvatarCard.setLayout(new BoxLayout(leftAvatarCard, BoxLayout.Y_AXIS));
        leftAvatarCard.setPreferredSize(new Dimension(280, 440));
        leftAvatarCard.setBorder(new EmptyBorder(24, 20, 24, 20));

        profileAvatar = new UITheme.CircularAvatar(148, UITheme.PRIMARY);
        profileAvatar.setRingStroke(3.5f);
        profileAvatar.setPlaceholder("👤");
        profileAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameDisplayLabel = new JLabel("Student Name");
        nameDisplayLabel.setFont(UITheme.FONT_CARD_TITLE);
        nameDisplayLabel.setForeground(UITheme.TEXT_MAIN);
        nameDisplayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameBadgeLabel = new JLabel("REG NUMBER");
        usernameBadgeLabel.setFont(UITheme.FONT_SMALL_BOLD);
        usernameBadgeLabel.setForeground(UITheme.PRIMARY);
        usernameBadgeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        UITheme.PillBadge rolePill = new UITheme.PillBadge("UNDERGRADUATE", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY, UITheme.PRIMARY_TINT);
        rolePill.setAlignmentX(Component.CENTER_ALIGNMENT);

        UITheme.ModernButton browseBtn = new UITheme.ModernButton("📷  Change Photo...", UITheme.ButtonStyle.OUTLINE, 8);
        browseBtn.setFont(UITheme.FONT_SMALL_BOLD);
        browseBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        browseBtn.addActionListener(e -> chooseProfilePicture());

        JLabel photoTip = new JLabel("<html><div style='text-align:center; width:180px; color:#94A3B8; font-size:11px;'>JPG or PNG formats supported. Stored locally or via URL path.</div></html>");
        photoTip.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftAvatarCard.add(Box.createVerticalStrut(8));
        leftAvatarCard.add(profileAvatar);
        leftAvatarCard.add(Box.createVerticalStrut(14));
        leftAvatarCard.add(nameDisplayLabel);
        leftAvatarCard.add(Box.createVerticalStrut(4));
        leftAvatarCard.add(usernameBadgeLabel);
        leftAvatarCard.add(Box.createVerticalStrut(8));
        leftAvatarCard.add(rolePill);
        leftAvatarCard.add(Box.createVerticalStrut(18));
        leftAvatarCard.add(browseBtn);
        leftAvatarCard.add(Box.createVerticalStrut(12));
        leftAvatarCard.add(photoTip);
        leftAvatarCard.add(Box.createVerticalGlue());

        // -------------------------------------------------------------
        // RIGHT: Information Cards
        // -------------------------------------------------------------
        JPanel rightFormsPanel = new JPanel();
        rightFormsPanel.setLayout(new BoxLayout(rightFormsPanel, BoxLayout.Y_AXIS));
        rightFormsPanel.setOpaque(false);

        // Section 1: Academic Identity (Official — Read Only)
        UITheme.ModernCard academicCard = new UITheme.ModernCard(14);
        academicCard.setLayout(new BorderLayout(10, 10));
        academicCard.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel sec1Title = new JLabel("🔒  Official University Information (Read Only)");
        sec1Title.setFont(UITheme.FONT_SECTION_TITLE);
        sec1Title.setForeground(UITheme.TEXT_MAIN);

        JPanel academicGrid = new JPanel(new GridLayout(2, 2, 16, 12));
        academicGrid.setOpaque(false);
        academicGrid.setBorder(new EmptyBorder(8, 0, 0, 0));

        usernameField = lockedField();
        nameField = lockedField();
        emailField = lockedField();
        roleField = lockedField();

        academicGrid.add(createFieldGroup("Username / Student Reg ID", usernameField));
        academicGrid.add(createFieldGroup("Full Name", nameField));
        academicGrid.add(createFieldGroup("Official University Email", emailField));
        academicGrid.add(createFieldGroup("System Role", roleField));

        academicCard.add(sec1Title, BorderLayout.NORTH);
        academicCard.add(academicGrid, BorderLayout.CENTER);

        // Section 2: Contact Information (Editable)
        UITheme.ModernCard contactCard = new UITheme.ModernCard(14);
        contactCard.setLayout(new BorderLayout(10, 12));
        contactCard.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel sec2Title = new JLabel("✏️  Editable Contact Details");
        sec2Title.setFont(UITheme.FONT_SECTION_TITLE);
        sec2Title.setForeground(UITheme.TEXT_MAIN);

        JPanel contactForm = new JPanel(new GridLayout(2, 1, 10, 12));
        contactForm.setOpaque(false);
        contactForm.setBorder(new EmptyBorder(8, 0, 0, 0));

        contactField = new UITheme.ModernTextField("e.g. 0712345678");
        contactField.setPreferredSize(new Dimension(300, 38));

        profilePicField = new UITheme.ModernTextField("Path to image file (optional)");
        profilePicField.setPreferredSize(new Dimension(300, 38));

        contactForm.add(createFieldGroup("Mobile Contact Number *", contactField));
        contactForm.add(createFieldGroup("Profile Picture File Location", profilePicField));

        // Save Button Row
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        actionRow.setOpaque(false);

        UITheme.ModernButton saveButton = new UITheme.ModernButton("💾  Save Changes", UITheme.ButtonStyle.PRIMARY, 10);
        saveButton.setPreferredSize(new Dimension(160, 40));
        saveButton.addActionListener(e -> saveProfile());

        actionRow.add(saveButton);

        contactCard.add(sec2Title, BorderLayout.NORTH);
        contactCard.add(contactForm, BorderLayout.CENTER);
        contactCard.add(actionRow, BorderLayout.SOUTH);

        rightFormsPanel.add(academicCard);
        rightFormsPanel.add(Box.createVerticalStrut(16));
        rightFormsPanel.add(contactCard);

        splitPanel.add(leftAvatarCard, BorderLayout.WEST);
        splitPanel.add(rightFormsPanel, BorderLayout.CENTER);

        // Assemble Top Header + Status Banner + Content
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.add(titleBox);
        wrapper.add(statusBanner);
        wrapper.add(Box.createVerticalStrut(12));
        wrapper.add(splitPanel);

        add(UITheme.createModernScrollPane(wrapper), BorderLayout.CENTER);
    }

    private JPanel createFieldGroup(String label, JComponent comp) {
        JPanel group = new JPanel(new BorderLayout(0, 4));
        group.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BODY_BOLD);
        lbl.setForeground(UITheme.TEXT_BODY);

        group.add(lbl, BorderLayout.NORTH);
        group.add(comp, BorderLayout.CENTER);
        return group;
    }

    private JTextField lockedField() {
        JTextField field = new JTextField(20);
        field.setFont(UITheme.FONT_BODY);
        field.setEditable(false);
        field.setBackground(new Color(241, 245, 249));
        field.setForeground(UITheme.TEXT_MUTED);
        field.setPreferredSize(new Dimension(200, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        return field;
    }

    private void chooseProfilePicture() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files (*.jpg, *.png)", "jpg", "jpeg", "png"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            profilePicField.setText(file.getAbsolutePath());
            renderProfileImage(file.getAbsolutePath());
        }
    }

    private void loadProfile() {
        try {
            Undergraduate student = controller.getProfile(studentId);
            nameDisplayLabel.setText(student.getFullName());
            usernameBadgeLabel.setText(student.getUsername().toUpperCase());
            usernameField.setText(student.getUsername());
            nameField.setText(student.getFullName());
            emailField.setText(student.getEmail());
            roleField.setText(student.getRoleTitle());
            contactField.setText(student.getContactNo() != null ? student.getContactNo() : "");
            profilePicField.setText(student.getProfilePic() != null ? student.getProfilePic() : "");
            renderProfileImage(student.getProfilePic());
        } catch (DatabaseException | BusinessRuleException e) {
            showStatus("Failed to load profile: " + e.getMessage(), true);
        }
    }

    private void renderProfileImage(String imagePath) {
        // The profile display is a CircularAvatar, not a button/label.  Delegating
        // loading to it also clears the image safely for empty, missing, or invalid paths.
        if (imagePath == null || imagePath.trim().isEmpty()) {
            profileAvatar.setImage(null);
            profileAvatar.setPlaceholder("👤");
            return;
        }

        File imageFile = new File(imagePath);
        if (!imageFile.exists() || !imageFile.isFile()) {
            profileAvatar.setImage(null);
            profileAvatar.setPlaceholder("⚠️");
            return;
        }
        profileAvatar.setPlaceholder("👤");
        profileAvatar.setImagePath(imageFile.getPath());
    }

    private void saveProfile() {
        try {
            controller.updateOwnProfile(studentId, contactField.getText().trim(), profilePicField.getText().trim());
            showStatus("✓ Profile contact details updated successfully.", false);
        } catch (ValidationException e) {
            showStatus("⚠️ " + e.getMessage(), true);
        } catch (DatabaseException e) {
            showStatus("⚠️ Failed to save profile: " + e.getMessage(), true);
        }
    }

    private void showStatus(String message, boolean error) {
        statusLabel.setText(message);
        if (error) {
            statusBanner.setBackground(UITheme.DANGER_LIGHT);
            statusBanner.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.DANGER_BORDER, 1),
                    new EmptyBorder(2, 8, 2, 8)
            ));
            statusLabel.setForeground(UITheme.DANGER_DARK);
        } else {
            statusBanner.setBackground(UITheme.SUCCESS_LIGHT);
            statusBanner.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.SUCCESS_BORDER, 1),
                    new EmptyBorder(2, 8, 2, 8)
            ));
            statusLabel.setForeground(UITheme.SUCCESS_DARK);
        }
        statusBanner.setVisible(true);
        statusBanner.revalidate();
        statusBanner.repaint();
    }
}
