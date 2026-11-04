package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LecturerController;
import com.faculty.management.model.Lecturer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

import static com.faculty.management.gui.member2_lecturer.LecturerUIComponents.*;

/**
 * Lecturer Profile Management Panel for Member 2.
 * Strictly implements Requirement 1:
 * - View profile
 * - Update profile (first name, last name)
 * - Update contact details (contact number)
 * - Update profile picture (browse file chooser)
 * - Cannot change username/password (locked fields)
 */
public class LecturerProfilePanel extends JPanel {

    private final Lecturer currentLecturer;
    private final LecturerController controller;

    // Form inputs
    private JTextField txtUserId;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtEmail;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtContactNo;
    private JTextField txtProfilePic;
    private JButton btnBrowsePic;
    private BurgundyButton btnSaveChanges;

    // Right Inspector Card components
    private JLabel inspectorAvatarLabel;
    private JLabel inspectorNameLabel;
    private JLabel inspectorUsernameLabel;
    private JLabel inspectorUserIdVal;
    private JLabel inspectorRoleVal;
    private JLabel inspectorStatusVal;
    private JLabel inspectorEmailVal;
    private JLabel inspectorContactVal;
    private WhiteOutlineButton btnUpdateProfile;

    public LecturerProfilePanel(Lecturer lecturer) {
        this.currentLecturer = (lecturer != null) ? lecturer : new Lecturer();
        this.controller = new LecturerController();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(COLOR_PAGE_BG);
        setBorder(new EmptyBorder(0, 28, 20, 28));

        // 1. Header Title & Subtitle
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("My Profile");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(COLOR_TEXT_DARK);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageSub = new JLabel("View academic credentials, update your personal contact details and profile picture.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);
        pageSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        headerPanel.add(titleBox, BorderLayout.CENTER);

        // 2. Main Two-Column Layout (Left Form Card + Right Profile Card)
        JPanel twoColumns = new JPanel(new BorderLayout(24, 0));
        twoColumns.setOpaque(false);

        JPanel leftCard = createAccountDetailsCard();
        JPanel rightCard = createProfileSummaryCard();

        twoColumns.add(leftCard, BorderLayout.CENTER);
        twoColumns.add(rightCard, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(twoColumns, BorderLayout.CENTER);
    }

    /**
     * Builds the left Account details form card.
     */
    private JPanel createAccountDetailsCard() {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel topBox = new JPanel();
        topBox.setLayout(new BoxLayout(topBox, BoxLayout.Y_AXIS));
        topBox.setOpaque(false);

        JLabel cardTitle = new JLabel("Account Details");
        cardTitle.setFont(FONT_SECTION_TITLE);
        cardTitle.setForeground(COLOR_TEXT_DARK);
        cardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cardSub = new JLabel("Username and password are locked. Update contact number, name, and profile picture.");
        cardSub.setFont(FONT_SMALL);
        cardSub.setForeground(COLOR_TEXT_MUTED);
        cardSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        topBox.add(cardTitle);
        topBox.add(Box.createVerticalStrut(2));
        topBox.add(cardSub);

        // 2-Column Form Fields Grid (4 rows of 2 fields)
        JPanel formGrid = new JPanel(new GridLayout(4, 2, 16, 14));
        formGrid.setOpaque(false);

        // Row 1: User ID (locked) and Username (locked)
        txtUserId = createStyledTextField(false);
        txtUsername = createStyledTextField(false);
        formGrid.add(createFieldContainer("User ID (locked)", txtUserId));
        formGrid.add(createFieldContainer("Username (locked)", txtUsername));

        // Row 2: Password (locked) and Email (locked)
        txtPassword = new JPasswordField("••••••••");
        txtPassword.setEditable(false);
        txtPassword.setFont(FONT_BODY);
        txtPassword.setBackground(new Color(249, 250, 251));
        txtPassword.setForeground(COLOR_TEXT_MUTED);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER_INPUT, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        txtEmail = createStyledTextField(false);
        formGrid.add(createFieldContainer("Password (locked)", txtPassword));
        formGrid.add(createFieldContainer("Email (locked)", txtEmail));

        // Row 3: First name (editable) and Last name (editable)
        txtFirstName = createStyledTextField(true);
        txtLastName = createStyledTextField(true);
        formGrid.add(createFieldContainer("First Name", txtFirstName));
        formGrid.add(createFieldContainer("Last Name", txtLastName));

        // Row 4: Contact No (editable) and Profile Picture (browse file)
        txtContactNo = createStyledTextField(true);

        JPanel picWrapper = new JPanel(new BorderLayout(6, 0));
        picWrapper.setOpaque(false);
        txtProfilePic = createStyledTextField(true);
        btnBrowsePic = new JButton("Browse");
        btnBrowsePic.setFont(FONT_SMALL_BOLD);
        btnBrowsePic.setFocusPainted(false);
        btnBrowsePic.addActionListener(e -> chooseProfilePicture());
        picWrapper.add(txtProfilePic, BorderLayout.CENTER);
        picWrapper.add(btnBrowsePic, BorderLayout.EAST);

        formGrid.add(createFieldContainer("Contact Number", txtContactNo));
        formGrid.add(createFieldContainer("Profile Picture Path", picWrapper));

        // Bottom Save button
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setOpaque(false);
        btnSaveChanges = new BurgundyButton("Save changes");
        btnSaveChanges.setPreferredSize(new Dimension(145, 38));
        btnSaveChanges.addActionListener(e -> saveProfile());
        btnPanel.add(btnSaveChanges);

        card.add(topBox, BorderLayout.NORTH);
        card.add(formGrid, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createFieldContainer(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FONT_BODY_BOLD);
        lbl.setForeground(COLOR_TEXT_DARK);

        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JTextField createStyledTextField(boolean editable) {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setEditable(editable);
        tf.setBackground(editable ? Color.WHITE : new Color(249, 250, 251));
        tf.setForeground(editable ? COLOR_TEXT_DARK : COLOR_TEXT_MUTED);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER_INPUT, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        return tf;
    }

    /**
     * Builds the right Profile Summary Inspector Card.
     */
    private JPanel createProfileSummaryCard() {
        CardPanel card = new CardPanel();
        card.setPreferredSize(new Dimension(300, 500));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel contentBox = new JPanel();
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setOpaque(false);

        // Header: Avatar + Full Name + Username
        JPanel profileHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        profileHeader.setOpaque(false);
        profileHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        inspectorAvatarLabel = new JLabel();
        inspectorAvatarLabel.setIcon(new AvatarIcon("KP", 46, new Color(254, 243, 199), new Color(146, 64, 14)));

        JPanel nameBox = new JPanel();
        nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.Y_AXIS));
        nameBox.setOpaque(false);

        inspectorNameLabel = new JLabel("Kamal Perera");
        inspectorNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        inspectorNameLabel.setForeground(COLOR_TEXT_DARK);

        inspectorUsernameLabel = new JLabel("@kamal");
        inspectorUsernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        inspectorUsernameLabel.setForeground(COLOR_TEXT_MUTED);

        nameBox.add(inspectorNameLabel);
        nameBox.add(Box.createVerticalStrut(2));
        nameBox.add(inspectorUsernameLabel);

        profileHeader.add(inspectorAvatarLabel);
        profileHeader.add(nameBox);

        contentBox.add(profileHeader);
        contentBox.add(Box.createVerticalStrut(20));

        // Info List
        JPanel infoGrid = new JPanel(new GridLayout(5, 2, 8, 12));
        infoGrid.setOpaque(false);
        infoGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoGrid.add(createDetailLabel("User ID"));
        inspectorUserIdVal = createDetailValLabel("31");
        infoGrid.add(inspectorUserIdVal);

        infoGrid.add(createDetailLabel("Role"));
        inspectorRoleVal = createDetailValLabel("Lecturer");
        infoGrid.add(inspectorRoleVal);

        infoGrid.add(createDetailLabel("Status"));
        inspectorStatusVal = createDetailValLabel("Active");
        inspectorStatusVal.setForeground(new Color(21, 128, 61));
        infoGrid.add(inspectorStatusVal);

        infoGrid.add(createDetailLabel("Email"));
        inspectorEmailVal = createDetailValLabel("kamal@fot.ruh.ac.lk");
        infoGrid.add(inspectorEmailVal);

        infoGrid.add(createDetailLabel("Contact"));
        inspectorContactVal = createDetailValLabel("0771122334");
        infoGrid.add(inspectorContactVal);

        contentBox.add(infoGrid);
        contentBox.add(Box.createVerticalGlue());

        // Update profile button
        btnUpdateProfile = new WhiteOutlineButton("Update profile");
        btnUpdateProfile.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnUpdateProfile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnUpdateProfile.addActionListener(e -> saveProfile());

        contentBox.add(Box.createVerticalStrut(18));
        contentBox.add(btnUpdateProfile);

        card.add(contentBox, BorderLayout.CENTER);
        return card;
    }

    private JLabel createDetailLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(COLOR_TEXT_MUTED);
        return l;
    }

    private JLabel createDetailValLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY_BOLD);
        l.setForeground(COLOR_TEXT_DARK);
        return l;
    }

    private void chooseProfilePicture() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Profile Picture");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            txtProfilePic.setText(file.getAbsolutePath());
        }
    }

    private void loadData() {
        if (currentLecturer != null) {
            String uid = String.valueOf(currentLecturer.getUserId() > 0 ? currentLecturer.getUserId() : 31);
            String un = (currentLecturer.getUsername() != null && !currentLecturer.getUsername().isEmpty()) ? currentLecturer.getUsername() : "kamal";
            String fn = (currentLecturer.getFirstName() != null && !currentLecturer.getFirstName().isEmpty()) ? currentLecturer.getFirstName() : "Kamal";
            String ln = (currentLecturer.getLastName() != null && !currentLecturer.getLastName().isEmpty()) ? currentLecturer.getLastName() : "Perera";
            String em = (currentLecturer.getEmail() != null && !currentLecturer.getEmail().isEmpty()) ? currentLecturer.getEmail() : "kamal@fot.ruh.ac.lk";
            String ct = (currentLecturer.getContactNo() != null && !currentLecturer.getContactNo().isEmpty()) ? currentLecturer.getContactNo() : "0771122334";
            String pic = (currentLecturer.getProfilePic() != null) ? currentLecturer.getProfilePic() : "";

            txtUserId.setText(uid);
            txtUsername.setText(un);
            txtEmail.setText(em);
            txtFirstName.setText(fn);
            txtLastName.setText(ln);
            txtContactNo.setText(ct);
            txtProfilePic.setText(pic);

            String full = fn + " " + ln;
            inspectorNameLabel.setText(full);
            inspectorUsernameLabel.setText("@" + un);
            inspectorUserIdVal.setText(uid);
            inspectorEmailVal.setText(em);
            inspectorContactVal.setText(ct);

            String initials = extractInitials(full);
            inspectorAvatarLabel.setIcon(new AvatarIcon(initials, 46, new Color(254, 240, 199), new Color(180, 83, 9)));
        }
    }

    private void saveProfile() {
        currentLecturer.setFirstName(txtFirstName.getText().trim());
        currentLecturer.setLastName(txtLastName.getText().trim());
        currentLecturer.setContactNo(txtContactNo.getText().trim());
        currentLecturer.setProfilePic(txtProfilePic.getText().trim());

        if (controller.updateProfile(currentLecturer)) {
            loadData();
            JOptionPane.showMessageDialog(this, "Profile and contact details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            loadData();
            JOptionPane.showMessageDialog(this, "Profile updated locally.", "Notice", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
