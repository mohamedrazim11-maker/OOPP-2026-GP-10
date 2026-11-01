package com.faculty.management.gui.member3_attendance;

import com.faculty.management.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Attendance Entry Dialog for Technical Officer.
 * Provides backwards-compatibility for callers opening attendance entry in a standalone dialog.
 * Embeds AttendanceEntryPanel.
 */
public class AttendanceEntryDialog extends JDialog {

    private final AttendanceEntryPanel panel;

    public AttendanceEntryDialog(JFrame parent, User currentUser) {
        super(parent, "Attendance Management — Technical Operations", true);
        setLayout(new BorderLayout());
        setSize(980, 700);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(parent);

        panel = new AttendanceEntryPanel(currentUser);
        add(panel, BorderLayout.CENTER);

        JPanel closeBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        closeBar.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Close Window");
        btnClose.addActionListener(e -> dispose());
        closeBar.add(btnClose);
        add(closeBar, BorderLayout.SOUTH);
    }

    public AttendanceEntryPanel getPanel() {
        return panel;
    }
}
