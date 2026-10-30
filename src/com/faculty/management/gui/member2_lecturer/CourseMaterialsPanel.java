package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.controller.LecturerController;
import com.faculty.management.model.CourseMaterial;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * Course Materials Panel.
 * Implements Wireframe 03 Course Materials.
 */
public class CourseMaterialsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtCourseSearch;
    private JButton btnSearch, btnUpload;
    private JLabel subtextLabel;
    private final LecturerController controller = new LecturerController();

    public CourseMaterialsPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(18, 24, 18, 24));
        initUI();
    }

    private void initUI() {
        // Top row per Wireframe 03: Course ID: [ textfield ]  [ Search Course ]  [ Upload Material ]
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setOpaque(false);

        JLabel lblCourseId = new JLabel("Course ID:");
        lblCourseId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCourseId.setForeground(new Color(51, 65, 85));

        txtCourseSearch = new JTextField(12);
        txtCourseSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCourseSearch.setPreferredSize(new Dimension(130, 34));
        txtCourseSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(4, 8, 4, 8)
        ));

        btnSearch = new JButton("Search Course");
        btnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSearch.setBackground(Color.WHITE);
        btnSearch.setForeground(new Color(15, 23, 42));
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(148, 163, 184), 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

        btnUpload = new JButton("Upload Material");
        btnUpload.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnUpload.setBackground(Color.WHITE);
        btnUpload.setForeground(new Color(15, 23, 42));
        btnUpload.setFocusPainted(false);
        btnUpload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpload.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(148, 163, 184), 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

        topPanel.add(lblCourseId);
        topPanel.add(txtCourseSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnUpload);

        // Table per Wireframe 03: ID | Course ID | Title | File Path | Uploaded Date
        tableModel = new DefaultTableModel(new String[]{"ID", "Course ID", "Title", "File Path", "Uploaded Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(34);
        table.setShowGrid(true);
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(new Color(15, 23, 42));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(new Color(51, 65, 85));
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(320);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRender);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRender);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Bottom subtext per Wireframe 03: "Upload opens JFileChooser → saves path + date"
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        bottomPanel.setOpaque(false);

        subtextLabel = new JLabel("Upload opens JFileChooser → saves path + date");
        subtextLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subtextLabel.setForeground(new Color(100, 116, 139));
        bottomPanel.add(subtextLabel);

        btnSearch.addActionListener(e -> loadMaterials());
        btnUpload.addActionListener(e -> {
            MaterialFormDialog dialog = new MaterialFormDialog((Frame) SwingUtilities.getWindowAncestor(this), controller);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadMaterials();
            }
        });

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
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
