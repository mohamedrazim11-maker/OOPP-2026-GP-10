package com.faculty.management.gui.member3_attendance;

import javax.swing.*;
import java.awt.*;

/**
 * Notice Viewer Dialog for Technical Officer.
 * Provides backwards-compatibility for callers opening notices in a standalone dialog.
 * Embeds NoticeViewerPanel.
 */
public class NoticeViewerDialog extends JDialog {

    private final NoticeViewerPanel panel;

    public NoticeViewerDialog(JFrame parent) {
        super(parent, "Faculty Notice Board & Official Circulars — Technical Operations", true);
        setLayout(new BorderLayout());
        setSize(1000, 720);
        setMinimumSize(new Dimension(880, 600));
        setLocationRelativeTo(parent);

        panel = new NoticeViewerPanel();
        add(panel, BorderLayout.CENTER);

        JPanel closeBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        closeBar.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Close Window");
        btnClose.addActionListener(e -> dispose());
        closeBar.add(btnClose);
        add(closeBar, BorderLayout.SOUTH);
    }

    public NoticeViewerPanel getPanel() {
        return panel;
    }
}
