package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LecturerController;
import com.faculty.management.model.CourseMaterial;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CourseMaterialsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtCourseSearch;
    private JButton btnSearch, btnUpload;
    private final LecturerController controller = new LecturerController();

    public CourseMaterialsPanel() {
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtCourseSearch = new JTextField(10);
        btnSearch = new JButton("Search Course");
        btnUpload = new JButton("Upload Material");

        topPanel.add(new JLabel("Course ID:"));
        topPanel.add(txtCourseSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnUpload);

        tableModel = new DefaultTableModel(new String[]{"ID", "Course ID", "Title", "File Path", "Uploaded Date"}, 0);
        table = new JTable(tableModel);

        btnSearch.addActionListener(e -> loadMaterials());
        btnUpload.addActionListener(e -> {
            MaterialFormDialog dialog = new MaterialFormDialog((Frame) SwingUtilities.getWindowAncestor(this), controller);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadMaterials();
            }
        });

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadMaterials() {
        tableModel.setRowCount(0);
        String courseId = txtCourseSearch.getText().trim();
        if (courseId.isEmpty()) return;

        List<CourseMaterial> list = controller.getCourseMaterials(courseId);
        for (CourseMaterial cm : list) {
            tableModel.addRow(new Object[]{
                    cm.getMaterialId(), cm.getCourseId(), cm.getTitle(), cm.getFilePath(), cm.getUploadedDate()
            });
        }
    }
}
