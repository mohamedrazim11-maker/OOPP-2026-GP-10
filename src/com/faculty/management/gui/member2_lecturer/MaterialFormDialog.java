package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LecturerController;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.CourseMaterial;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class MaterialFormDialog extends JDialog {
    private JTextField txtTitle, txtCourseId, txtFilePath;
    private JButton btnBrowse, btnSave, btnCancel;
    private File selectedFile;
    private final LecturerController controller;
    private final CourseMaterial existingMaterial; // Null if adding new
    private boolean success = false;

    public MaterialFormDialog(Frame parent, LecturerController controller) {
        this(parent, controller, null);
    }

    public MaterialFormDialog(Frame parent, LecturerController controller, CourseMaterial materialToEdit) {
        super(parent, materialToEdit == null ? "Upload Course Material" : "Modify Course Material", true);
        this.controller = controller;
        this.existingMaterial = materialToEdit;

        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(460, 280));
        setResizable(false);
        setLocationRelativeTo(parent);

        initUI();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 12, 16));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(22, 24, 18, 24));

        txtCourseId = new JTextField();
        txtCourseId.setFont(UITheme.FONT_BODY);
        txtTitle = new JTextField();
        txtTitle.setFont(UITheme.FONT_BODY);
        txtFilePath = new JTextField();
        txtFilePath.setFont(UITheme.FONT_BODY);
        txtFilePath.setEditable(false);

        btnBrowse = new JButton("Browse File");
        btnBrowse.setFont(UITheme.FONT_SMALL_BOLD);
        btnBrowse.setBackground(Color.WHITE);
        btnBrowse.setForeground(UITheme.TEXT_BODY);
        btnBrowse.setFocusPainted(false);
        btnBrowse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBrowse.addActionListener(e -> chooseFile());

        JPanel filePickRow = new JPanel(new BorderLayout(8, 0));
        filePickRow.setOpaque(false);
        filePickRow.add(txtFilePath, BorderLayout.CENTER);
        filePickRow.add(btnBrowse, BorderLayout.EAST);

        JLabel lblCrs = new JLabel("Course Code / ID:");
        lblCrs.setFont(UITheme.FONT_SMALL_BOLD);
        lblCrs.setForeground(UITheme.TEXT_BODY);

        JLabel lblTitle = new JLabel("Material Title:");
        lblTitle.setFont(UITheme.FONT_SMALL_BOLD);
        lblTitle.setForeground(UITheme.TEXT_BODY);

        JLabel lblFile = new JLabel("Document File:");
        lblFile.setFont(UITheme.FONT_SMALL_BOLD);
        lblFile.setForeground(UITheme.TEXT_BODY);

        formPanel.add(lblCrs);
        formPanel.add(txtCourseId);
        formPanel.add(lblTitle);
        formPanel.add(txtTitle);
        formPanel.add(lblFile);
        formPanel.add(filePickRow);

        // Prepopulate if editing
        if (existingMaterial != null) {
            txtCourseId.setText(existingMaterial.getCourseId());
            txtTitle.setText(existingMaterial.getTitle());
            txtFilePath.setText(existingMaterial.getFilePath() != null ? existingMaterial.getFilePath() : "");
        }

        // Bottom action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        buttonPanel.setBackground(UITheme.BG_PAGE);

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(UITheme.FONT_SMALL_BOLD);
        btnCancel.addActionListener(e -> dispose());

        btnSave = new JButton(existingMaterial == null ? "Upload Material" : "Save Changes");
        btnSave.setFont(UITheme.FONT_SMALL_BOLD);
        btnSave.setBackground(UITheme.PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.PRIMARY, 1),
                new EmptyBorder(6, 16, 6, 16)
        ));
        btnSave.addActionListener(e -> doSave());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Course Material File (PDF, Slides, Doc)");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            txtFilePath.setText(selectedFile.getAbsolutePath());
        }
    }

    private void doSave() {
        String courseId = txtCourseId.getText().trim();
        String title = txtTitle.getText().trim();

        if (courseId.isEmpty() || title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Course ID and Material Title.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (existingMaterial == null) {
            // New upload requires a file
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(this, "Please select a file to upload.", "File Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            success = controller.uploadCourseMaterial(courseId, title, selectedFile);
            if (success) {
                JOptionPane.showMessageDialog(this, "Course material uploaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Upload failed. Please verify database connection.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Modification
            success = controller.modifyCourseMaterial(existingMaterial.getMaterialId(), courseId, title, selectedFile);
            if (success) {
                JOptionPane.showMessageDialog(this, "Course material updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. Please verify database connection.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean isSuccess() {
        return success;
    }
}
