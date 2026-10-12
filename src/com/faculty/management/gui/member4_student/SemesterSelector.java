package com.faculty.management.gui.member4_student;

import com.faculty.management.controller.UndergraduateController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.gui.common.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Modern reusable "Semester: [combo box]" control backed by the student's
 * actual enrollment history (via UndergraduateController.getEnrolledSemesters).
 * Styled with modern padding, typography, and borders.
 */
public class SemesterSelector {

    private final JComboBox<String> comboBox = new JComboBox<>();
    private String[][] semesterValues = new String[0][];

    public SemesterSelector(UndergraduateController controller, int studentId) {
        comboBox.setFont(UITheme.FONT_BODY_BOLD);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(UITheme.TEXT_MAIN);
        comboBox.setFocusable(false);
        comboBox.setPreferredSize(new Dimension(220, 36));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(2, 6, 2, 6)
        ));

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                setFont(UITheme.FONT_BODY);
                if (isSelected) {
                    setBackground(UITheme.PRIMARY_LIGHT);
                    setForeground(UITheme.PRIMARY_DARK);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(UITheme.TEXT_MAIN);
                }
                return c;
            }
        });

        try {
            List<String[]> semesters = controller.getEnrolledSemesters(studentId);
            semesterValues = semesters.toArray(new String[0][]);
            for (String[] s : semesterValues) {
                comboBox.addItem("Semester " + s[0] + " (" + s[1] + ")");
            }
        } catch (DatabaseException e) {
            comboBox.addItem("No semesters found");
        }
    }

    public JComboBox<String> getComboBox() {
        return comboBox;
    }

    public boolean hasSelection() {
        return comboBox.getSelectedIndex() >= 0 && comboBox.getSelectedIndex() < semesterValues.length;
    }

    public int getSelectedSemesterNumber() {
        return Integer.parseInt(semesterValues[comboBox.getSelectedIndex()][0]);
    }

    public String getSelectedAcademicYear() {
        return semesterValues[comboBox.getSelectedIndex()][1];
    }
}
