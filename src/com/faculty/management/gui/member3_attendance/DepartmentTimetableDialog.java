package com.faculty.management.gui.member3_attendance;

import javax.swing.*;
import java.awt.*;

/**
 * Department Timetable Dialog for Technical Officer.
 * Provides backwards-compatibility for callers opening timetables in a standalone dialog.
 * Embeds DepartmentTimetablePanel.
 */
public class DepartmentTimetableDialog extends JDialog {

    private final DepartmentTimetablePanel panel;

    public DepartmentTimetableDialog(JFrame parent) {
        super(parent, "Department Timetable & Laboratory Allocation — Technical Operations", true);
        setLayout(new BorderLayout());
        setSize(1040, 720);
        setMinimumSize(new Dimension(880, 600));
        setLocationRelativeTo(parent);

        panel = new DepartmentTimetablePanel();
        add(panel, BorderLayout.CENTER);

        JPanel closeBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        closeBar.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Close Window");
        btnClose.addActionListener(e -> dispose());
        closeBar.add(btnClose);
        add(closeBar, BorderLayout.SOUTH);
    }

    public DepartmentTimetablePanel getPanel() {
        return panel;
    }
}
