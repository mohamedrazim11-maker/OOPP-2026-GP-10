package com.faculty.management.gui.member3_attendance;

import com.faculty.management.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Technical Officer Profile Dialog.
 * Provides backwards-compatibility for callers opening profile in a standalone dialog.
 * Embeds TechnicalOfficerProfilePanel.
 */
public class TechnicalOfficerProfileDialog extends JDialog {

    private final TechnicalOfficerProfilePanel panel;

    public TechnicalOfficerProfileDialog(JFrame parent, User user) {
        super(parent, "My Profile - Technical Officer", true);
        setLayout(new BorderLayout());
        setSize(650, 650);
        setMinimumSize(new Dimension(550, 500));
        setLocationRelativeTo(parent);

        panel = new TechnicalOfficerProfilePanel(user);
        add(panel, BorderLayout.CENTER);

        JPanel closeBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        closeBar.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Close Window");
        btnClose.addActionListener(e -> dispose());
        closeBar.add(btnClose);
        add(closeBar, BorderLayout.SOUTH);
    }

    public TechnicalOfficerProfilePanel getPanel() {
        return panel;
    }
}
