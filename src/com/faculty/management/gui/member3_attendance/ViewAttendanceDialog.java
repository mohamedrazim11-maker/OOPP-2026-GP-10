package com.faculty.management.gui.member3_attendance;

import javax.swing.*;
import java.awt.*;

/**
 * View Attendance Dialog for viewing batch attendance percentages and eligibility.
 * Retained for backwards-compatibility across modules (e.g. Lecturer Dashboard).
 * Embeds ViewAttendancePanel.
 */
public class ViewAttendanceDialog extends JDialog {

    private final ViewAttendancePanel panel;

    public ViewAttendanceDialog(JFrame parent) {
        super(parent, "Batch Attendance Summary & Eligibility — Technical Operations", true);
        setLayout(new BorderLayout());
        setSize(980, 700);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(parent);

        panel = new ViewAttendancePanel();
        add(panel, BorderLayout.CENTER);

        JPanel closeBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        closeBar.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Close Window");
        btnClose.addActionListener(e -> dispose());
        closeBar.add(btnClose);
        add(closeBar, BorderLayout.SOUTH);
    }

    public ViewAttendancePanel getPanel() {
        return panel;
    }
}
