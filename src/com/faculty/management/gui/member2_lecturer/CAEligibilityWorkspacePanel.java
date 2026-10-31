package com.faculty.management.gui.member2_lecturer;

import com.faculty.management.gui.common.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * CA Eligibility Workspace Panel (Requirement 5).
 * Combines Individual Student CA calculation and Whole-Batch Eligibility monitoring.
 * Verifies CA >= 40% rule and displays Eligible / Not Eligible for both.
 */
public class CAEligibilityWorkspacePanel extends JPanel {

    public CAEligibilityWorkspacePanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(10, 14, 10, 14));

        JTabbedPane tabbedPane = new JTabbedPane();
        UITheme.styleTabbedPane(tabbedPane);

        // Sub-tab 1: Individual Student CA Eligibility Calculation
        tabbedPane.addTab("Individual Student CA Summary", new CASummaryPanel());

        // Sub-tab 2: Whole-Batch Exam Eligibility Review
        tabbedPane.addTab("Whole-Batch Exam Eligibility", new EligibilityPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
}
