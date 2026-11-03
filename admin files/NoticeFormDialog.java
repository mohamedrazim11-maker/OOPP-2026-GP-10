package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.gui.common.CustomButton;
import com.faculty.management.gui.common.PrimaryButton;
import com.faculty.management.gui.common.SecondaryButton;
import com.faculty.management.model.Notice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Notice Form Dialog for Administrator (Member 1).
 * Supports "Create Notice", "Edit Notice", "Delete Notice", and "View Notice" operations.
 * 
 * Demonstrates Core OOP Principles:
 * - Classes and Objects: Dialog window and UI components instantiation.
 * - Inheritance: Extends JDialog to inherit standard modal window behaviors.
 * - Abstraction: Interacts with backend layers through AdminController.
 * - Polymorphism: Overloaded constructors and mode-based polymorphic form behavior (ADD, UPDATE, DELETE, VIEW).
 * - Encapsulation: Private UI controls and state variables with controlled accessors.
 * - Error and Exception Handling: Catches ValidationException and DatabaseException with friendly error feedback.
 * - Database Handling: Persists notice operations directly to MySQL / SQL Server via NoticeDAO.
 */
public class NoticeFormDialog extends JDialog {

    public enum FormMode {
        ADD,
        UPDATE,
        DELETE,
        VIEW
    }

    private final FormMode mode;
    private final AdminController adminController;
    private boolean saved = false;
    private Notice notice;
    private int noticeIdToUpdate = 0;

    // Encapsulated UI components
    private JComboBox<Notice> noticeSelectorComboBox;
    private JTextField titleField;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> priorityComboBox;
    private JTextField postedByField;
    private JTextArea contentArea;
    private JLabel dateLabel;
    private JLabel errorLabel;
    private JButton submitButton;
    private JButton cancelButton;

    /**
     * Default constructor for Add/Create mode.
     */
    public NoticeFormDialog(JFrame parent, AdminController adminController) {
        this(parent, adminController, FormMode.ADD, null);
    }

    /**
     * Constructor specifying the operational mode.
     */
    public NoticeFormDialog(JFrame parent, AdminController adminController, FormMode mode) {
        this(parent, adminController, mode, null);
    }

    /**
     * Overloaded constructor specifying mode and initial notice entity.
     */
    public NoticeFormDialog(JFrame parent, AdminController adminController, FormMode mode, Notice initialNotice) {
        super(parent, getDialogTitle(mode), true);
        this.mode = (mode != null) ? mode : FormMode.ADD;
        this.adminController = (adminController != null) ? adminController : new AdminController();
        this.notice = initialNotice;
        if (initialNotice != null) {
            this.noticeIdToUpdate = initialNotice.getNoticeId();
        }

        initComponents();

        if (this.mode != FormMode.ADD) {
            loadExistingNotices(initialNotice);
        }
    }

    private static String getDialogTitle(FormMode mode) {
        if (mode == FormMode.DELETE) {
            return "Delete Notice — Faculty Management System";
        } else if (mode == FormMode.UPDATE) {
            return "Edit Notice — Faculty Management System";
        } else if (mode == FormMode.VIEW) {
            return "View Notice Details — Faculty Management System";
        }
        return "Create New Notice — Faculty Management System";
    }

    private void initComponents() {
        setSize(580, 700);
        setMinimumSize(new Dimension(520, 620));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        // 1. Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        Color bannerColor;
        String bannerTitle;
        String bannerSubtitle;

        if (mode == FormMode.DELETE) {
            bannerColor = new Color(185, 28, 28);
            bannerTitle = "🗑️  Delete Faculty Notice";
            bannerSubtitle = "Permanently remove an official notice or circular from the faculty board.";
        } else if (mode == FormMode.UPDATE) {
            bannerColor = new Color(110, 22, 36);
            bannerTitle = "✏️  Edit Faculty Notice";
            bannerSubtitle = "Modify title, category, priority, or body content of published notices.";
        } else if (mode == FormMode.VIEW) {
            bannerColor = new Color(92, 18, 33);
            bannerTitle = "👁️  View Notice Details";
            bannerSubtitle = "Read-only preview of published circulars and official announcements.";
        } else {
            bannerColor = new Color(92, 18, 33);
            bannerTitle = "📢  Create Official Notice";
            bannerSubtitle = "Publish a new academic announcement, examination notice, or circular.";
        }

        headerPanel.setBackground(bannerColor);
        headerPanel.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel titleLbl = new JLabel(bannerTitle);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLbl.setForeground(Color.WHITE);

        JLabel subtitleLbl = new JLabel(bannerSubtitle);
        subtitleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLbl.setForeground(new Color(226, 232, 240));

        JPanel bannerTextPanel = new JPanel();
        bannerTextPanel.setLayout(new BoxLayout(bannerTextPanel, BoxLayout.Y_AXIS));
        bannerTextPanel.setOpaque(false);
        bannerTextPanel.add(titleLbl);
        bannerTextPanel.add(Box.createVerticalStrut(3));
        bannerTextPanel.add(subtitleLbl);

        headerPanel.add(bannerTextPanel, BorderLayout.CENTER);

        // 2. Form Fields Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Error message banner
        errorLabel = new JLabel();
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setForeground(new Color(220, 38, 38));
        errorLabel.setVisible(false);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(8));

        // Selector for UPDATE / DELETE / VIEW
        if (mode != FormMode.ADD) {
            JLabel selectLbl = new JLabel("Select Notice to " + (mode == FormMode.DELETE ? "Delete" : (mode == FormMode.UPDATE ? "Edit" : "View")) + ": *");
            selectLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            selectLbl.setForeground(new Color(51, 65, 85));
            selectLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            noticeSelectorComboBox = new JComboBox<>();
            noticeSelectorComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            noticeSelectorComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            noticeSelectorComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            noticeSelectorComboBox.addActionListener(e -> onNoticeSelected());

            formPanel.add(selectLbl);
            formPanel.add(Box.createVerticalStrut(4));
            formPanel.add(noticeSelectorComboBox);
            formPanel.add(Box.createVerticalStrut(14));
        }

        // Title Field
        JLabel titleFieldLbl = new JLabel("Notice Title: *");
        titleFieldLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleFieldLbl.setForeground(new Color(51, 65, 85));
        titleFieldLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleField = new JTextField();
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        titleField.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(titleFieldLbl);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(titleField);
        formPanel.add(Box.createVerticalStrut(14));

        // Category & Priority row
        JPanel catPrioRow = new JPanel(new GridLayout(1, 2, 16, 0));
        catPrioRow.setOpaque(false);
        catPrioRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        catPrioRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        // Category
        JPanel catPanel = new JPanel();
        catPanel.setLayout(new BoxLayout(catPanel, BoxLayout.Y_AXIS));
        catPanel.setOpaque(false);

        JLabel catLbl = new JLabel("Category");
        catLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        catLbl.setForeground(new Color(51, 65, 85));

        categoryComboBox = new JComboBox<>(new String[]{"GENERAL", "ACADEMIC", "EXAMINATION", "TIMETABLE"});
        categoryComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        catPanel.add(catLbl);
        catPanel.add(Box.createVerticalStrut(4));
        catPanel.add(categoryComboBox);

        // Priority
        JPanel prioPanel = new JPanel();
        prioPanel.setLayout(new BoxLayout(prioPanel, BoxLayout.Y_AXIS));
        prioPanel.setOpaque(false);

        JLabel prioLbl = new JLabel("Priority");
        prioLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        prioLbl.setForeground(new Color(51, 65, 85));

        priorityComboBox = new JComboBox<>(new String[]{"NORMAL", "URGENT", "INFO"});
        priorityComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        priorityComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        prioPanel.add(prioLbl);
        prioPanel.add(Box.createVerticalStrut(4));
        prioPanel.add(priorityComboBox);

        catPrioRow.add(catPanel);
        catPrioRow.add(prioPanel);

        formPanel.add(catPrioRow);
        formPanel.add(Box.createVerticalStrut(14));

        // Posted By
        JLabel postedByLbl = new JLabel("Posted By");
        postedByLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        postedByLbl.setForeground(new Color(51, 65, 85));
        postedByLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        postedByField = new JTextField("Faculty Administration");
        postedByField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        postedByField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        postedByField.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(postedByLbl);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(postedByField);
        formPanel.add(Box.createVerticalStrut(14));

        // Content Area
        JLabel contentLbl = new JLabel("Content");
        contentLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        contentLbl.setForeground(new Color(51, 65, 85));
        contentLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentArea = new JTextArea(6, 30);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        formPanel.add(contentLbl);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(contentScroll);
        formPanel.add(Box.createVerticalStrut(10));

        // Date Display
        dateLabel = new JLabel("Posted Date: " + LocalDate.now());
        dateLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        dateLabel.setForeground(new Color(100, 116, 139));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(dateLabel);

        // Configure read-only / delete states
        if (mode == FormMode.VIEW) {
            titleField.setEditable(false);
            categoryComboBox.setEnabled(false);
            priorityComboBox.setEnabled(false);
            postedByField.setEditable(false);
            contentArea.setEditable(false);
        } else if (mode == FormMode.DELETE) {
            titleField.setEditable(false);
            categoryComboBox.setEnabled(false);
            priorityComboBox.setEnabled(false);
            postedByField.setEditable(false);
            contentArea.setEditable(false);
        }

        JScrollPane mainScrollPane = new JScrollPane(formPanel);
        mainScrollPane.setBorder(null);

        // 3. Footer Action Buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 15));
        footerPanel.setBackground(new Color(241, 245, 249));
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        cancelButton = new SecondaryButton(mode == FormMode.VIEW ? "Close" : "Cancel");
        cancelButton.setPreferredSize(new Dimension(95, 38));
        cancelButton.addActionListener(e -> dispose());

        if (mode == FormMode.DELETE) {
            submitButton = new CustomButton(
                    "Delete Notice",
                    new Color(220, 38, 38),
                    new Color(185, 28, 28),
                    new Color(153, 27, 27),
                    Color.WHITE,
                    null,
                    8
            );
            submitButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
            submitButton.setPreferredSize(new Dimension(135, 38));
            submitButton.addActionListener(e -> executeDeleteNotice());
        } else if (mode == FormMode.UPDATE) {
            submitButton = new CustomButton(
                    "Update Notice",
                    new Color(15, 118, 110),
                    new Color(13, 148, 136),
                    new Color(17, 94, 89),
                    Color.WHITE,
                    null,
                    8
            );
            submitButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
            submitButton.setPreferredSize(new Dimension(135, 38));
            submitButton.addActionListener(e -> executeSaveNotice());
        } else if (mode == FormMode.VIEW) {
            submitButton = null;
        } else {
            submitButton = new PrimaryButton("Publish");
            submitButton.setPreferredSize(new Dimension(110, 38));
            submitButton.addActionListener(e -> executeSaveNotice());
        }

        footerPanel.add(cancelButton);
        if (submitButton != null) {
            footerPanel.add(submitButton);
        }

        add(headerPanel, BorderLayout.NORTH);
        add(mainScrollPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void loadExistingNotices(Notice initialNotice) {
        if (noticeSelectorComboBox == null) return;
        try {
            List<Notice> notices = adminController.loadAllNotices();
            noticeSelectorComboBox.removeAllItems();

            Notice targetToSelect = null;
            for (Notice n : notices) {
                noticeSelectorComboBox.addItem(n);
                if (initialNotice != null && n.getNoticeId() == initialNotice.getNoticeId()) {
                    targetToSelect = n;
                }
            }

            if (targetToSelect != null) {
                noticeSelectorComboBox.setSelectedItem(targetToSelect);
            } else if (noticeSelectorComboBox.getItemCount() > 0) {
                noticeSelectorComboBox.setSelectedIndex(0);
            }
        } catch (DatabaseException e) {
            showError("Unable to load notices: " + e.getMessage());
        }
    }

    private void onNoticeSelected() {
        if (noticeSelectorComboBox == null) return;
        Notice selected = (Notice) noticeSelectorComboBox.getSelectedItem();
        if (selected == null) return;

        this.noticeIdToUpdate = selected.getNoticeId();
        this.notice = selected;

        titleField.setText(selected.getTitle() != null ? selected.getTitle() : "");
        categoryComboBox.setSelectedItem(selected.getCategory() != null ? selected.getCategory().toUpperCase() : "GENERAL");
        priorityComboBox.setSelectedItem(selected.getPriority() != null ? selected.getPriority().toUpperCase() : "NORMAL");
        postedByField.setText(selected.getPostedBy() != null ? selected.getPostedBy() : "Faculty Administration");
        contentArea.setText(selected.getContent() != null ? selected.getContent() : "");
        dateLabel.setText("Posted Date: " + selected.getFormattedDate());
    }

    private void executeSaveNotice() {
        clearError();
        String title = titleField.getText().trim();
        String category = (String) categoryComboBox.getSelectedItem();
        String priority = (String) priorityComboBox.getSelectedItem();
        String postedBy = postedByField.getText().trim();
        String content = contentArea.getText().trim();

        try {
            if (mode == FormMode.ADD) {
                Notice newNotice = new Notice(title, content, category, priority, postedBy);
                Notice created = adminController.handleCreateNotice(newNotice);
                this.notice = created;
                this.saved = true;
                JOptionPane.showMessageDialog(this,
                        "Notice published successfully!\nTitle: " + created.getTitle() + "\nCategory: " + created.getCategory(),
                        "Notice Created",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else if (mode == FormMode.UPDATE) {
                if (noticeIdToUpdate <= 0) {
                    showError("Please select a valid notice to edit.");
                    return;
                }
                Notice updated = new Notice(noticeIdToUpdate, title, content, category, priority, postedBy, LocalDate.now());
                boolean success = adminController.handleUpdateNotice(updated);
                if (success) {
                    this.notice = updated;
                    this.saved = true;
                    JOptionPane.showMessageDialog(this,
                            "Notice updated successfully in the system database!",
                            "Notice Updated",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    showError("Notice update could not be completed.");
                }
            }
        } catch (ValidationException ve) {
            showError(ve.getMessage());
        } catch (DatabaseException de) {
            showError("Database Error: " + de.getMessage());
        }
    }

    private void executeDeleteNotice() {
        clearError();
        if (noticeIdToUpdate <= 0) {
            showError("Please select a valid notice to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this notice?\n\nTitle: " + titleField.getText() +
                "\nThis action is permanent and cannot be undone.",
                "Confirm Delete Notice",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted = adminController.handleDeleteNotice(noticeIdToUpdate);
            if (deleted) {
                this.saved = true;
                JOptionPane.showMessageDialog(this,
                        "Notice deleted successfully from the faculty database.",
                        "Notice Deleted",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                showError("Could not delete notice. Notice may have already been removed.");
            }
        } catch (ValidationException ve) {
            showError(ve.getMessage());
        } catch (DatabaseException de) {
            showError("Database Error: " + de.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText("⚠️ " + message);
        errorLabel.setVisible(true);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    public boolean isSaved() {
        return saved;
    }

    public Notice getNotice() {
        return notice;
    }
}
