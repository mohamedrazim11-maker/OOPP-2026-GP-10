package com.faculty.management.gui.member3_attendance;

import javax.swing.*;
import java.awt.*;

/**
 * Medical Management Dialog for Technical Officer.
 * Provides backwards-compatibility for callers opening medical management in a standalone dialog.
 * Embeds MedicalManagementPanel.
 */
public class MedicalManagementDialog extends JDialog {

    private final MedicalManagementPanel panel;

    public MedicalManagementDialog(JFrame parent) {
        super(parent, "Medical Certificate Management & Verification — Technical Operations", true);
        setLayout(new BorderLayout());
        setSize(1000, 700);
        setMinimumSize(new Dimension(880, 600));
        setLocationRelativeTo(parent);

        panel = new MedicalManagementPanel();
        add(panel, BorderLayout.CENTER);

        JPanel closeBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        closeBar.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Close Window");
        btnClose.addActionListener(e -> dispose());
        closeBar.add(btnClose);
        add(closeBar, BorderLayout.SOUTH);
    }

    public MedicalManagementPanel getPanel() {
        return panel;
    }
}
