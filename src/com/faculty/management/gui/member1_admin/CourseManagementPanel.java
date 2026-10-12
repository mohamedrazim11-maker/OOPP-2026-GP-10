package com.faculty.management.gui.member1_admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Course Management Panel for Administrator (Member 1).
 * Represents the Course Management tab in Admin Dashboard.
 * 
 * Demonstrates OOP Concepts:
 * - Classes & Objects: Component instances and encapsulation.
 * - Inheritance: Extends JPanel to inherit Swing container behavior.
 */
public class CourseManagementPanel extends JPanel {

    public CourseManagementPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(30, 35, 30, 35));

        // 1. Header Section
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Course Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(15, 23, 42));

        JLabel subTitleLabel = new JLabel("Faculty of Technology — Course & Academic Module Management");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subTitleLabel.setForeground(new Color(100, 116, 139));

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subTitleLabel);

        // 2. Main Content Card Area
        JPanel contentCard = new JPanel(new GridBagLayout());
        contentCard.setBackground(Color.WHITE);
        contentCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(50, 40, 50, 40)
        ));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel infoTitle = new JLabel("Course Management");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoTitle.setForeground(new Color(30, 41, 59));
        infoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel infoDesc = new JLabel("Course management section is active. Module features will be configured here.");
        infoDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoDesc.setForeground(new Color(100, 116, 139));
        infoDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(infoTitle);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(infoDesc);

        contentCard.add(infoPanel);

        add(headerPanel, BorderLayout.NORTH);
        add(contentCard, BorderLayout.CENTER);
    }
}
