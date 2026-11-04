package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LecturerController;
import com.faculty.management.model.CourseMaterial;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * Dialog for Adding and Modifying Course Materials (Requirement 2).
 * Supports file browsing, course assignment, title editing, and file replacement.
 */
public class MaterialFormDialog extends JDialog {
    private JTextField txtTitle;
    private JTextField txtCourseId;
    private JTextField txtFilePath;
    private JButton btnBrowse;
    private JButton btnSubmit;
    private JButton btnCancel;
    private File selectedFile;
    private final LecturerController controller;
    private final CourseMaterial materialToEdit;
    private boolean success = false;

    /**
     * Add material constructor.
     */
    public MaterialFormDialog(Frame parent, LecturerController controller) {
        this(parent, controller, null);
    }

    /**
     * Add or Modify material constructor.
     */
    public MaterialFormDialog(Frame parent, LecturerController controller, CourseMaterial materialToEdit) {
        super(parent, materialToEdit == null ? "Upload Course Material" : "Modify Course Material", true);
        this.controller = controller;
        this.materialToEdit = materialToEdit;

        initComponents(parent);
    }

    private void initComponents(Frame parent) {
        setLayout(new BorderLayout());
        setResizable(false);
        setSize(460, 290);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 24, 16, 24));
        mainPanel.setBackground(Color.WHITE);

        JLabel lblHeading = new JLabel(materialToEdit == null ? "Upload New Material" : "Edit Course Material");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeading.setForeground(new Color(114, 28, 36));

        JLabel lblSub = new JLabel(materialToEdit == null ? "Select file and specify course code." : "Update material details or replace file.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(107, 114, 128));

        mainPanel.add(lblHeading);
        mainPanel.add(Box.createVerticalStrut(4));
        mainPanel.add(lblSub);
        mainPanel.add(Box.createVerticalStrut(16));

        // Form fields
        JPanel grid = new JPanel(new GridLayout(3, 2, 10, 12));
        grid.setOpaque(false);

        JLabel lblCourse = new JLabel("Course Code / ID:");
        lblCourse.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtCourseId = new JTextField();
        txtCourseId.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblTitle = new JLabel("Material Title:");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtTitle = new JTextField();
        txtTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblFile = new JLabel("File Path:");
        lblFile.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel fileChoosePanel = new JPanel(new BorderLayout(6, 0));
        fileChoosePanel.setOpaque(false);
        txtFilePath = new JTextField();
        txtFilePath.setEditable(false);
        txtFilePath.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtFilePath.setBackground(new Color(249, 250, 251));

        btnBrowse = new JButton("Browse");
        btnBrowse.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnBrowse.setFocusPainted(false);
        btnBrowse.addActionListener(e -> chooseFile());

        fileChoosePanel.add(txtFilePath, BorderLayout.CENTER);
        fileChoosePanel.add(btnBrowse, BorderLayout.EAST);

        grid.add(lblCourse);
        grid.add(txtCourseId);
        grid.add(lblTitle);
        grid.add(txtTitle);
        grid.add(lblFile);
        grid.add(fileChoosePanel);

        mainPanel.add(grid);

        // Prepopulate if editing
        if (materialToEdit != null) {
            txtCourseId.setText(materialToEdit.getCourseId());
            txtTitle.setText(materialToEdit.getTitle());
            txtFilePath.setText(materialToEdit.getFilePath() != null ? materialToEdit.getFilePath() : "");
        }

        // Bottom action buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnPanel.setBackground(new Color(249, 250, 251));
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> dispose());

        btnSubmit = new JButton(materialToEdit == null ? "Upload" : "Save Changes");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSubmit.setBackground(new Color(114, 28, 36));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.addActionListener(e -> doSubmit());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSubmit);

        add(mainPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            txtFilePath.setText(selectedFile.getAbsolutePath());
        }
    }

    private void doSubmit() {
        String courseId = txtCourseId.getText().trim();
        String title = txtTitle.getText().trim();

        if (courseId.isEmpty() || title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter course code and material title.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (materialToEdit == null) {
            // Upload mode
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(this, "Please browse and select a file to upload.", "File Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            success = controller.uploadCourseMaterial(courseId, title, selectedFile);
            if (success) {
                JOptionPane.showMessageDialog(this, "Course material uploaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Upload failed. Please check course ID and database connection.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Modify mode
            success = controller.modifyCourseMaterial(materialToEdit.getMaterialId(), courseId, title, selectedFile);
            if (success) {
                JOptionPane.showMessageDialog(this, "Course material updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update material.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean isSuccess() {
        return success;
    }
}
