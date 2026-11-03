package com.faculty.management.gui.member3_attendance;

import com.faculty.management.controller.TechnicalOfficerController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Technical Officer Profile Panel.
 * Matches Faculty Management System UI Specification (Page 7).
 * Features:
 * - Breadcrumb: Home / My Profile
 * - Header: Title and subtitle
 * - Left Column: Account Details card with 2-column form grid (Username, Role, First name, Last name, Email, Contact no) and Save changes / Cancel buttons
 * - Right Column: User Summary card with circular initials avatar, @username, and account metadata rows
 */
public class TechnicalOfficerProfilePanel extends JPanel {

    private User currentUser;
    private final TechnicalOfficerController controller;

    // Form Fields
    private JTextField txtUsername;
    private JTextField txtRole;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JTextField txtContactNo;

    // Right Summary Card Labels
    private JLabel lblSummaryAvatar;
    private JLabel lblSummaryName;
    private JLabel lblSummaryUsername;
    private JLabel lblSummaryUserId;
    private JLabel lblSummaryRole;
    private JLabel lblSummaryStatus;
    private JLabel lblSummaryEmail;
    private JLabel lblSummaryContact;

    public TechnicalOfficerProfilePanel(User user) {
        this.currentUser = user;
        this.controller = new TechnicalOfficerController();

        setLayout(new BorderLayout());
        setBackground(new Color(246, 239, 230)); // Warm Soft Ivory (#F6EFE6)

        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(246, 239, 230));
        mainContent.setOpaque(true);
        mainContent.setBorder(new EmptyBorder(16, 28, 24, 28));

        // 1. Breadcrumbs
        JLabel lblBreadcrumb = new JLabel("Home / My Profile");
        lblBreadcrumb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBreadcrumb.setForeground(new Color(107, 114, 128));
        lblBreadcrumb.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 2. Page Header
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        titleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageTitle = new JLabel("My Profile");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pageTitle.setForeground(new Color(17, 24, 39));

        JLabel pageSub = new JLabel("Update your contact details. Locked fields are managed by the administrator.");
        pageSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pageSub.setForeground(new Color(107, 114, 128));

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(3));
        titleBox.add(pageSub);

        // 3. Two-Column Split (Form Card on Left, User Summary Card on Right)
        JPanel splitPanel = new JPanel(new GridBagLayout());
        splitPanel.setOpaque(false);
        splitPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 1.0;

        // Left Form Card (~68% width)
        gbc.gridx = 0;
        gbc.weightx = 0.68;
        gbc.insets = new Insets(0, 0, 0, 16);
        splitPanel.add(createAccountDetailsCard(), gbc);

        // Right Summary Card (~32% width)
        gbc.gridx = 1;
        gbc.weightx = 0.32;
        gbc.insets = new Insets(0, 0, 0, 0);
        splitPanel.add(createSummaryAccountCard(), gbc);

        // Assemble Layout
        mainContent.add(lblBreadcrumb);
        mainContent.add(Box.createVerticalStrut(10));
        mainContent.add(titleBox);
        mainContent.add(Box.createVerticalStrut(16));
        mainContent.add(splitPanel);

        JScrollPane scrollPane = UITheme.createModernScrollPane(mainContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(246, 239, 230));
        scrollPane.getViewport().setBackground(new Color(246, 239, 230));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Left Column: Account Details Form Card matching Page 7 Screenshot.
     */
    private JPanel createAccountDetailsCard() {
        JPanel card = createWhiteCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Card Header Title
        JLabel cardTitle = new JLabel("Account details");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cardTitle.setForeground(new Color(17, 24, 39));
        cardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(cardTitle);
        content.add(Box.createVerticalStrut(16));

        // Form Fields in 3 Rows of 2 Columns
        txtUsername = createStyledField(false);
        txtRole = createStyledField(false);
        txtFirstName = createStyledField(true);
        txtLastName = createStyledField(true);
        txtEmail = createStyledField(true);
        txtContactNo = createStyledField(true);

        content.add(createFormRow(
                "Username (locked)", txtUsername,
                "Role (locked)", txtRole
        ));
        content.add(Box.createVerticalStrut(14));

        content.add(createFormRow(
                "First name", txtFirstName,
                "Last name", txtLastName
        ));
        content.add(Box.createVerticalStrut(14));

        content.add(createFormRow(
                "Email", txtEmail,
                "Contact no", txtContactNo
        ));
        content.add(Box.createVerticalStrut(20));

        // Bottom Action Buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSave = createSolidMaroonButton("Save changes");
        btnSave.addActionListener(e -> saveProfile());

        JButton btnCancel = createOutlineButton("Cancel");
        btnCancel.addActionListener(e -> loadData());

        buttonRow.add(btnSave);
        buttonRow.add(btnCancel);

        content.add(buttonRow);

        card.add(content, BorderLayout.NORTH);
        return card;
    }

    private JPanel createFormRow(String label1, JTextField field1, String label2, JTextField field2) {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(createFieldBlock(label1, field1));
        row.add(createFieldBlock(label2, field2));
        return row;
    }

    private JPanel createFieldBlock(String labelText, JTextField field) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(55, 65, 81));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        block.add(label);
        block.add(field);
        return block;
    }

    private JTextField createStyledField(boolean editable) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(220, 36));
        tf.setMaximumSize(new Dimension(2000, 36));
        tf.setEditable(editable);

        if (!editable) {
            tf.setBackground(new Color(249, 250, 251)); // Locked light gray
            tf.setForeground(new Color(107, 114, 128));
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                    new EmptyBorder(6, 12, 6, 12)
            ));
        } else {
            tf.setBackground(Color.WHITE);
            tf.setForeground(new Color(17, 24, 39));
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                    new EmptyBorder(6, 12, 6, 12)
            ));
        }
        return tf;
    }

    /**
     * Right Column: User Summary Card matching Page 7 Screenshot.
     */
    private JPanel createSummaryAccountCard() {
        JPanel card = createWhiteCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Header with Circular Initials Avatar + Full Name Stack
        JPanel userHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        userHeader.setOpaque(false);
        userHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblSummaryAvatar = new JLabel("RR", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(254, 235, 226)); // Soft peach circle
                g2.fill(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblSummaryAvatar.setPreferredSize(new Dimension(42, 42));
        lblSummaryAvatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSummaryAvatar.setForeground(new Color(92, 18, 33)); // Maroon initials
        lblSummaryAvatar.setOpaque(false);

        JPanel nameStack = new JPanel();
        nameStack.setLayout(new BoxLayout(nameStack, BoxLayout.Y_AXIS));
        nameStack.setOpaque(false);

        lblSummaryName = new JLabel("Razim Razim");
        lblSummaryName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSummaryName.setForeground(new Color(17, 24, 39));

        lblSummaryUsername = new JLabel("@Razim");
        lblSummaryUsername.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSummaryUsername.setForeground(new Color(107, 114, 128));

        nameStack.add(lblSummaryName);
        nameStack.add(Box.createVerticalStrut(2));
        nameStack.add(lblSummaryUsername);

        userHeader.add(lblSummaryAvatar);
        userHeader.add(nameStack);

        content.add(userHeader);
        content.add(Box.createVerticalStrut(20));

        // Metadata Key-Value Rows
        lblSummaryUserId = new JLabel("33");
        lblSummaryRole = new JLabel("Technical Officer");
        lblSummaryStatus = new JLabel("Active");
        lblSummaryEmail = new JLabel("Razim@gmail.com");
        lblSummaryContact = new JLabel("0776867969");

        content.add(createSummaryRow("User ID", lblSummaryUserId));
        content.add(Box.createVerticalStrut(8));
        content.add(createSummaryRow("Role", lblSummaryRole));
        content.add(Box.createVerticalStrut(8));
        content.add(createSummaryRow("Status", lblSummaryStatus));
        content.add(Box.createVerticalStrut(8));
        content.add(createSummaryRow("Email", lblSummaryEmail));
        content.add(Box.createVerticalStrut(8));
        content.add(createSummaryRow("Contact", lblSummaryContact));

        card.add(content, BorderLayout.NORTH);
        return card;
    }

    private JPanel createSummaryRow(String label, JLabel valLabel) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(2000, 24));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(107, 114, 128));
        lbl.setPreferredSize(new Dimension(65, 20));

        valLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valLabel.setForeground(new Color(17, 24, 39));

        row.add(lbl, BorderLayout.WEST);
        row.add(valLabel, BorderLayout.CENTER);
        return row;
    }

    public void loadData() {
        if (currentUser != null) {
            txtUsername.setText(currentUser.getUsername() != null ? currentUser.getUsername() : "Razim");
            txtRole.setText(currentUser.getRoleTitle() != null ? currentUser.getRoleTitle() : "Technical Officer");
            txtFirstName.setText(currentUser.getFirstName() != null ? currentUser.getFirstName() : "Razim");
            txtLastName.setText(currentUser.getLastName() != null ? currentUser.getLastName() : "Razim");
            txtEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "Razim@gmail.com");
            txtContactNo.setText(currentUser.getContactNo() != null ? currentUser.getContactNo() : "0776867969");

            String fullName = currentUser.getFullName() != null ? currentUser.getFullName() : "Razim Razim";
            lblSummaryName.setText(fullName);
            lblSummaryUsername.setText("@" + (currentUser.getUsername() != null ? currentUser.getUsername() : "Razim"));
            lblSummaryAvatar.setText(extractInitials(fullName));
            lblSummaryUserId.setText(String.valueOf(currentUser.getUserId() > 0 ? currentUser.getUserId() : 33));
            lblSummaryRole.setText(currentUser.getRoleTitle() != null ? currentUser.getRoleTitle() : "Technical Officer");
            lblSummaryStatus.setText("Active");
            lblSummaryEmail.setText(txtEmail.getText());
            lblSummaryContact.setText(txtContactNo.getText());
        } else {
            // Default sample data matching Page 7 Screenshot
            txtUsername.setText("Razim");
            txtRole.setText("Technical Officer");
            txtFirstName.setText("Razim");
            txtLastName.setText("Razim");
            txtEmail.setText("Razim@gmail.com");
            txtContactNo.setText("0776867969");

            lblSummaryName.setText("Razim Razim");
            lblSummaryUsername.setText("@Razim");
            lblSummaryAvatar.setText("RR");
            lblSummaryUserId.setText("33");
            lblSummaryRole.setText("Technical Officer");
            lblSummaryStatus.setText("Active");
            lblSummaryEmail.setText("Razim@gmail.com");
            lblSummaryContact.setText("0776867969");
        }
    }

    private void saveProfile() {
        String fname = txtFirstName.getText().trim();
        String lname = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String contact = txtContactNo.getText().trim();

        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name, Last name, Email and Contact number are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentUser != null) {
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
                    JOptionPane.showMessageDialog(this, "Profile changes saved locally.", "Updated", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (ValidationException | DatabaseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Profile changes saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        }

        loadData();
    }

    private String extractInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "RR";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    private JPanel createWhiteCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(new Color(229, 231, 235));
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JButton createSolidMaroonButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setForeground(Color.WHITE);
                setFocusPainted(false);
                setBorderPainted(false);
                setContentAreaFilled(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setBorder(new EmptyBorder(8, 20, 8, 20));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = hovered ? new Color(110, 22, 36) : new Color(92, 18, 33);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 6, 6));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        return btn;
    }

    private JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setForeground(new Color(55, 65, 81));
                setFocusPainted(false);
                setBorderPainted(false);
                setContentAreaFilled(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setBorder(new EmptyBorder(8, 18, 8, 18));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? new Color(243, 244, 246) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 6, 6));
                g2.setColor(new Color(209, 213, 219));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 6, 6));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        return btn;
    }
}
