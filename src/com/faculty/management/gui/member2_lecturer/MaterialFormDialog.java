package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LecturerController;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MaterialFormDialog extends JDialog {
    private JTextField txtTitle, txtCourseId, txtFilePath;
    private JButton btnBrowse, btnUpload;
    private File selectedFile;
    private final LecturerController controller;
    private boolean success = false;

    public MaterialFormDialog(Frame parent, LecturerController controller) {
        super(parent, "Upload Course Material", true);
        this.controller = controller;
        setLayout(new GridLayout(4, 2, 10, 10));

        txtCourseId = new JTextField();
        txtTitle = new JTextField();
        txtFilePath = new JTextField(); txtFilePath.setEditable(false);

        btnBrowse = new JButton("Browse File");
        btnBrowse.addActionListener(e -> chooseFile());

        btnUpload = new JButton("Upload");
        btnUpload.addActionListener(e -> doUpload());

        add(new JLabel("Course ID:")); add(txtCourseId);
        add(new JLabel("Material Title:")); add(txtTitle);
        add(btnBrowse); add(txtFilePath);
        add(new JLabel("")); add(btnUpload);

        pack();
        setLocationRelativeTo(parent);
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            txtFilePath.setText(selectedFile.getAbsolutePath());
        }
    }

    private void doUpload() {
        if (selectedFile == null || txtTitle.getText().isEmpty() || txtCourseId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and select a file.");
            return;
        }
        success = controller.uploadCourseMaterial(txtCourseId.getText(), txtTitle.getText(), selectedFile);
        if (success) {
            JOptionPane.showMessageDialog(this, "File uploaded successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Upload failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() { return success; }
}
