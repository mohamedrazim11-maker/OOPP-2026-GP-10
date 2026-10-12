package com.faculty.management.gui.member4_student;

import com.faculty.management.gui.common.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern placeholder card for tabs that depend on another team member's
 * module (e.g. Attendance/Medical records belong to Member 3 — Technical Officer).
 * Visually communicates integration status and roadmap.
 */
public class PendingModulePanel extends JPanel {

    public PendingModulePanel(String icon, String title, String message) {
        setLayout(new GridBagLayout());
        setBackground(UITheme.BG_PAGE);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        UITheme.ModernCard card = new UITheme.ModernCard(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(36, 44, 36, 44));
        card.setPreferredSize(new Dimension(540, 360));
        card.setMaximumSize(new Dimension(560, 380));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.FONT_PAGE_TITLE);
        titleLabel.setForeground(UITheme.TEXT_MAIN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(12, 0, 8, 0));

        UITheme.PillBadge statusBadge = new UITheme.PillBadge("MEMBER 3 (TECHNICAL OFFICER) MODULE INTEGRATION",
                UITheme.WARNING_LIGHT, UITheme.WARNING_DARK, UITheme.WARNING_BORDER);
        statusBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel("<html><div style='text-align:center; width:380px; color:#64748B; font-size:13px; line-height:1.4;'>"
                + message + "</div></html>");
        messageLabel.setFont(UITheme.FONT_BODY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setBorder(new EmptyBorder(16, 0, 20, 0));

        // Checklist of integration readiness
        JPanel checklist = new JPanel();
        checklist.setLayout(new BoxLayout(checklist, BoxLayout.Y_AXIS));
        checklist.setOpaque(false);
        checklist.setAlignmentX(Component.CENTER_ALIGNMENT);

        addCheckItem(checklist, "✓ Database tables configured (attendance, medical_records)");
        addCheckItem(checklist, "✓ Student navigation and routing wired to dashboard tabs");
        addCheckItem(checklist, "⏳ Awaiting final merge of Member 3 Technical Officer module");

        card.add(Box.createVerticalGlue());
        card.add(iconLabel);
        card.add(titleLabel);
        card.add(statusBadge);
        card.add(messageLabel);
        card.add(checklist);
        card.add(Box.createVerticalGlue());

        add(card);
    }

    private void addCheckItem(JPanel parent, String text) {
        JLabel item = new JLabel(text);
        item.setFont(UITheme.FONT_SMALL);
        item.setForeground(UITheme.TEXT_MUTED);
        item.setAlignmentX(Component.CENTER_ALIGNMENT);
        item.setBorder(new EmptyBorder(2, 0, 2, 0));
        parent.add(item);
    }
}
