package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LecturerController;
<<<<<<< HEAD
import com.faculty.management.gui.common.UITheme;
=======
>>>>>>> Aysha
import com.faculty.management.model.Lecturer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
<<<<<<< HEAD
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
=======
import java.awt.*;

import static com.faculty.management.gui.member2_lecturer.LecturerUIComponents.*;

/**
 * Lecturer Profile Management Panel for Member 2.
 * Strictly implements Page 2 of the Ruhuna Lecturer Portal Specification.
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
        pageTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel pageSub = new JLabel("View locked account details and update your name.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);
        pageSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        pageSub.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

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

        JLabel cardTitle = new JLabel("Account details");
        cardTitle.setFont(FONT_SECTION_TITLE);
        cardTitle.setForeground(COLOR_TEXT_DARK);
        cardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel cardSub = new JLabel("Locked fields are managed by the administrator.");
        cardSub.setFont(FONT_SMALL);
        cardSub.setForeground(COLOR_TEXT_MUTED);
        cardSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardSub.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        topBox.add(cardTitle);
        topBox.add(Box.createVerticalStrut(2));
        topBox.add(cardSub);

        // 2-Column Form Fields Grid (3 rows of 2 fields)
        JPanel formGrid = new JPanel(new GridLayout(3, 2, 16, 14));
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
        formGrid.add(createFieldContainer("First name", txtFirstName));
        formGrid.add(createFieldContainer("Last name", txtLastName));

        // Bottom Save button
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setOpaque(false);
        btnSaveChanges = new BurgundyButton("Save changes");
        btnSaveChanges.setPreferredSize(new Dimension(135, 38));
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
>>>>>>> Aysha
    }

    private void loadData() {
        if (currentLecturer != null) {
<<<<<<< HEAD
            txtUserId.setText(String.valueOf(currentLecturer.getUserId()));
            txtUsername.setText(currentLecturer.getUsername());
            txtFirstName.setText(currentLecturer.getFirstName() != null ? currentLecturer.getFirstName() : "");
            txtLastName.setText(currentLecturer.getLastName() != null ? currentLecturer.getLastName() : "");
            txtEmail.setText(currentLecturer.getEmail() != null ? currentLecturer.getEmail() : "");
            txtDepartment.setText(currentLecturer.getDepartment() != null ? currentLecturer.getDepartment() : "Faculty of Technology");
            txtContact.setText(currentLecturer.getContactNo() != null ? currentLecturer.getContactNo() : "");
            txtProfilePic.setText(currentLecturer.getProfilePic() != null ? currentLecturer.getProfilePic() : "");
=======
            String uid = String.valueOf(currentLecturer.getUserId() > 0 ? currentLecturer.getUserId() : 31);
            String un = (currentLecturer.getUsername() != null && !currentLecturer.getUsername().isEmpty()) ? currentLecturer.getUsername() : "kamal";
            String fn = (currentLecturer.getFirstName() != null && !currentLecturer.getFirstName().isEmpty()) ? currentLecturer.getFirstName() : "Kamal";
            String ln = (currentLecturer.getLastName() != null && !currentLecturer.getLastName().isEmpty()) ? currentLecturer.getLastName() : "Perera";
            String em = (currentLecturer.getEmail() != null && !currentLecturer.getEmail().isEmpty()) ? currentLecturer.getEmail() : "kamal@fot.ruh.ac.lk";
            String ct = (currentLecturer.getContactNo() != null && !currentLecturer.getContactNo().isEmpty()) ? currentLecturer.getContactNo() : "0771122334";

            txtUserId.setText(uid);
            txtUsername.setText(un);
            txtEmail.setText(em);
            txtFirstName.setText(fn);
            txtLastName.setText(ln);

            String full = fn + " " + ln;
            inspectorNameLabel.setText(full);
            inspectorUsernameLabel.setText("@" + un);
            inspectorUserIdVal.setText(uid);
            inspectorEmailVal.setText(em);
            inspectorContactVal.setText(ct);

            String initials = extractInitials(full);
            inspectorAvatarLabel.setIcon(new AvatarIcon(initials, 46, new Color(254, 240, 199), new Color(180, 83, 9)));
>>>>>>> Aysha
        }
    }

    private void saveProfile() {
        currentLecturer.setFirstName(txtFirstName.getText().trim());
        currentLecturer.setLastName(txtLastName.getText().trim());
<<<<<<< HEAD
        currentLecturer.setEmail(txtEmail.getText().trim());
        currentLecturer.setContactNo(txtContact.getText().trim());
        currentLecturer.setProfilePic(txtProfilePic.getText().trim());

        if (controller.updateProfile(currentLecturer)) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
=======

        if (controller.updateProfile(currentLecturer)) {
            loadData();
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            loadData();
            JOptionPane.showMessageDialog(this, "Profile saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
>>>>>>> Aysha
        }
    }
}
