package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Course;
import com.faculty.management.model.TimetableEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.faculty.management.gui.member1_admin.AdminUIComponents.*;

/**
 * Timetable Management Panel for Administrator (Member 1).
 * Allows Administrator to view, create, update, and maintain weekly academic
 * lecture and laboratory timetables.
 *
 * Demonstrates Core OOP Principles:
 * - Classes & Objects: Encapsulation of timetable entity management, table models, and controllers.
 * - Inheritance: Extends JPanel.
 * - Abstraction: Database operations abstracted through AdminController and TimetableService.
 * - Polymorphism: Custom TableCellRenderers for session type badges and formatted time slots.
 * - Encapsulation: Private member variables with controlled public accessors.
 * - Error & Exception Handling: Catches DatabaseException with user-friendly feedback dialogs.
 * - Database Handling: Direct backend persistence to Microsoft SQL Server / MySQL.
 */
public class TimetableManagementPanel extends JPanel {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    private final AdminController adminController;
    private List<TimetableEntry> allTimetableEntries = new ArrayList<>();
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTable timetableTable;

    // Filters & search
    private SearchTextField searchField;
    private JComboBox<String> dayFilterBox;
    private JComboBox<String> typeFilterBox;
    private JComboBox<String> semesterFilterBox;

    // KPI Stat labels
    private JLabel totalSessionsVal;
    private JLabel theorySessionsVal;
    private JLabel practicalSessionsVal;
    private JLabel venuesCountVal;
    private JLabel summaryFooterLabel;

    // Action buttons
    private WhiteOutlineButton viewSlotBtn;
    private WhiteOutlineButton editSlotBtn;
    private DangerOutlineButton deleteSlotBtn;

    public TimetableManagementPanel() {
        this(new AdminController());
    }

    public TimetableManagementPanel(AdminController adminController) {
        this.adminController = (adminController != null) ? adminController : new AdminController();
        initComponents();
        loadTimetableData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(COLOR_PAGE_BG);
        setBorder(new EmptyBorder(0, 28, 20, 28));

        // 1. Header: Title, Subtitle, and Top Action Buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("Timetable Management");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(COLOR_TEXT_DARK);

        JLabel pageSub = new JLabel("Create, review and maintain lecture and laboratory timetables.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBtns.setOpaque(false);

        WhiteOutlineButton refreshBtn = new WhiteOutlineButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(100, 38));
        refreshBtn.addActionListener(e -> loadTimetableData());

        BurgundyButton createSlotBtn = new BurgundyButton("+ Create timetable");
        createSlotBtn.setPreferredSize(new Dimension(175, 38));
        createSlotBtn.addActionListener(e -> handleCreateSlotAction());

        actionBtns.add(refreshBtn);
        actionBtns.add(createSlotBtn);

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(actionBtns, BorderLayout.EAST);

        // 2. Main Body Container (KPI Cards + Card with Filter Bar & Table)
        JPanel bodyContainer = new JPanel();
        bodyContainer.setLayout(new BoxLayout(bodyContainer, BoxLayout.Y_AXIS));
        bodyContainer.setOpaque(false);

        // KPI Stat Cards Row
        bodyContainer.add(createKpiCardsRow());
        bodyContainer.add(Box.createVerticalStrut(14));

        // Card Container holding Filters, Table, and Footer
        CardPanel tableCard = new CardPanel();
        tableCard.setLayout(new BorderLayout(0, 12));
        tableCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        tableCard.add(createFilterBar(), BorderLayout.NORTH);
        tableCard.add(createTableScrollPane(), BorderLayout.CENTER);
        tableCard.add(createTableFooter(), BorderLayout.SOUTH);

        bodyContainer.add(tableCard);

        add(headerPanel, BorderLayout.NORTH);
        add(bodyContainer, BorderLayout.CENTER);
    }

    private JPanel createKpiCardsRow() {
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);

        totalSessionsVal = new JLabel("0");
        theorySessionsVal = new JLabel("0");
        practicalSessionsVal = new JLabel("0");
        venuesCountVal = new JLabel("0");

        statsRow.add(createStatCard("Total Sessions", totalSessionsVal, new Color(59, 130, 246)));
        statsRow.add(createStatCard("Theory Lectures", theorySessionsVal, new Color(16, 185, 129)));
        statsRow.add(createStatCard("Practical / Labs", practicalSessionsVal, new Color(139, 92, 246)));
        statsRow.add(createStatCard("Venues Allocated", venuesCountVal, COLOR_BURGUNDY));

        return statsRow;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(8, 6));
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(COLOR_TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(COLOR_TEXT_DARK);

        JPanel pill = new JPanel();
        pill.setPreferredSize(new Dimension(4, 32));
        pill.setBackground(accentColor);

        JPanel leftBox = new JPanel();
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
        leftBox.setOpaque(false);
        leftBox.add(titleLbl);
        leftBox.add(Box.createVerticalStrut(4));
        leftBox.add(valueLabel);

        card.add(pill, BorderLayout.WEST);
        card.add(leftBox, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFilterBar() {
        JPanel filterBar = new JPanel(new BorderLayout(14, 0));
        filterBar.setOpaque(false);

        // Search Field
        searchField = new SearchTextField("Search timetable by course, lecturer, or venue...", 25);
        searchField.setPreferredSize(new Dimension(320, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        // Dropdown Filters
        JPanel dropdowns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        dropdowns.setOpaque(false);

        dayFilterBox = new JComboBox<>(new String[]{"All Days", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"});
        dayFilterBox.setFont(FONT_BODY);
        dayFilterBox.setPreferredSize(new Dimension(130, 36));
        dayFilterBox.addActionListener(e -> applyFilters());

        typeFilterBox = new JComboBox<>(new String[]{"All Types", "THEORY", "PRACTICAL"});
        typeFilterBox.setFont(FONT_BODY);
        typeFilterBox.setPreferredSize(new Dimension(120, 36));
        typeFilterBox.addActionListener(e -> applyFilters());

        semesterFilterBox = new JComboBox<>(new String[]{"All Semesters", "Semester 1", "Semester 2"});
        semesterFilterBox.setFont(FONT_BODY);
        semesterFilterBox.setPreferredSize(new Dimension(135, 36));
        semesterFilterBox.addActionListener(e -> applyFilters());

        dropdowns.add(dayFilterBox);
        dropdowns.add(typeFilterBox);
        dropdowns.add(semesterFilterBox);

        filterBar.add(searchField, BorderLayout.WEST);
        filterBar.add(dropdowns, BorderLayout.EAST);
        return filterBar;
    }

    private JScrollPane createTableScrollPane() {
        String[] columnNames = {
                "ID", "Day", "Time Slot", "Course Module", "Type", "Lecturer / Instructor", "Venue / Lab", "Semester"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        timetableTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        timetableTable.setRowSorter(rowSorter);

        timetableTable.setFont(FONT_BODY);
        timetableTable.setRowHeight(40);
        timetableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        timetableTable.setShowVerticalLines(false);
        timetableTable.setGridColor(COLOR_BORDER);
        timetableTable.setSelectionBackground(new Color(243, 230, 233));
        timetableTable.setSelectionForeground(COLOR_TEXT_DARK);

        JTableHeader tableHeader = timetableTable.getTableHeader();
        tableHeader.setFont(FONT_BODY_BOLD);
        tableHeader.setForeground(COLOR_TEXT_DARK);
        tableHeader.setBackground(new Color(249, 250, 251));
        tableHeader.setPreferredSize(new Dimension(tableHeader.getPreferredSize().width, 38));
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        // Column widths
        timetableTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        timetableTable.getColumnModel().getColumn(1).setPreferredWidth(105);
        timetableTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        timetableTable.getColumnModel().getColumn(3).setPreferredWidth(260);
        timetableTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        timetableTable.getColumnModel().getColumn(5).setPreferredWidth(170);
        timetableTable.getColumnModel().getColumn(6).setPreferredWidth(140);
        timetableTable.getColumnModel().getColumn(7).setPreferredWidth(90);

        // Center ID & Day
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        timetableTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        timetableTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(FONT_BODY_BOLD);
                setForeground(new Color(30, 58, 138));
                return c;
            }
        });

        // Type Badge Renderer (THEORY / PRACTICAL)
        timetableTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String type = value != null ? value.toString() : "THEORY";
                JPanel p = new JPanel(new GridBagLayout());
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

                PillBadge badge;
                if ("PRACTICAL".equalsIgnoreCase(type)) {
                    badge = new PillBadge("PRACTICAL", new Color(243, 232, 255), new Color(126, 34, 206));
                } else {
                    badge = new PillBadge("THEORY", new Color(224, 231, 255), new Color(55, 48, 163));
                }
                p.add(badge);
                return p;
            }
        });

        // Selection Listener
        timetableTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = timetableTable.getSelectedRow() != -1;
            viewSlotBtn.setEnabled(hasSelection);
            editSlotBtn.setEnabled(hasSelection);
            deleteSlotBtn.setEnabled(hasSelection);
        });

        // Double click to edit
        timetableTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && timetableTable.getSelectedRow() != -1) {
                    handleEditSlotAction();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(timetableTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    private JPanel createTableFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        summaryFooterLabel = new JLabel("Showing 0 scheduled sessions");
        summaryFooterLabel.setFont(FONT_BODY);
        summaryFooterLabel.setForeground(COLOR_TEXT_MUTED);

        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionGroup.setOpaque(false);

        viewSlotBtn = new WhiteOutlineButton("View Slot");
        viewSlotBtn.setPreferredSize(new Dimension(105, 34));
        viewSlotBtn.setEnabled(false);
        viewSlotBtn.addActionListener(e -> handleViewSlotAction());

        editSlotBtn = new WhiteOutlineButton("Edit Selected");
        editSlotBtn.setPreferredSize(new Dimension(120, 34));
        editSlotBtn.setEnabled(false);
        editSlotBtn.addActionListener(e -> handleEditSlotAction());

        deleteSlotBtn = new DangerOutlineButton("Delete Selected");
        deleteSlotBtn.setPreferredSize(new Dimension(130, 34));
        deleteSlotBtn.setEnabled(false);
        deleteSlotBtn.addActionListener(e -> handleDeleteSlotAction());

        actionGroup.add(viewSlotBtn);
        actionGroup.add(editSlotBtn);
        actionGroup.add(deleteSlotBtn);

        footer.add(summaryFooterLabel, BorderLayout.WEST);
        footer.add(actionGroup, BorderLayout.EAST);
        return footer;
    }

    public void loadTimetableData() {
        new SwingWorker<List<TimetableEntry>, Void>() {
            @Override
            protected List<TimetableEntry> doInBackground() throws Exception {
                return adminController.handleGetAllTimetables();
            }

            @Override
            protected void done() {
                try {
                    allTimetableEntries = get();
                    populateTable(allTimetableEntries);
                    updateKpiStats(allTimetableEntries);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TimetableManagementPanel.this,
                            "Failed to load timetable data from database: " + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void populateTable(List<TimetableEntry> entries) {
        tableModel.setRowCount(0);

        for (TimetableEntry entry : entries) {
            String timeSlot = "N/A";
            if (entry.getStartTime() != null && entry.getEndTime() != null) {
                timeSlot = entry.getStartTime().format(TIME_FORMATTER) + " - " + entry.getEndTime().format(TIME_FORMATTER);
            }

            String courseName = (entry.getCourse() != null)
                    ? entry.getCourse().getCourseCode() + " - " + entry.getCourse().getCourseName()
                    : "Unknown Course";

            tableModel.addRow(new Object[]{
                    entry.getTimetableId(),
                    entry.getDayOfWeek() != null ? entry.getDayOfWeek().toUpperCase() : "MONDAY",
                    timeSlot,
                    courseName,
                    entry.getSessionType() != null ? entry.getSessionType().toUpperCase() : "THEORY",
                    entry.getLecturerName() != null ? entry.getLecturerName() : "TBA",
                    entry.getVenue() != null ? entry.getVenue() : "TBA",
                    "Sem " + entry.getSemesterNumber()
            });
        }

        applyFilters();
    }

    private void updateKpiStats(List<TimetableEntry> entries) {
        int total = entries.size();
        int theory = 0;
        int practical = 0;
        Set<String> venues = new HashSet<>();

        for (TimetableEntry e : entries) {
            if ("PRACTICAL".equalsIgnoreCase(e.getSessionType())) {
                practical++;
            } else {
                theory++;
            }
            if (e.getVenue() != null && !e.getVenue().trim().isEmpty()) {
                venues.add(e.getVenue().trim());
            }
        }

        totalSessionsVal.setText(String.valueOf(total));
        theorySessionsVal.setText(String.valueOf(theory));
        practicalSessionsVal.setText(String.valueOf(practical));
        venuesCountVal.setText(String.valueOf(venues.size()));
    }

    private void applyFilters() {
        String searchText = searchField.getText().trim().toLowerCase();
        String selectedDay = (String) dayFilterBox.getSelectedItem();
        String selectedType = (String) typeFilterBox.getSelectedItem();
        String selectedSem = (String) semesterFilterBox.getSelectedItem();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(searchText)));
        }

        if (selectedDay != null && !selectedDay.equals("All Days")) {
            filters.add(RowFilter.regexFilter("^" + selectedDay + "$", 1));
        }

        if (selectedType != null && !selectedType.equals("All Types")) {
            filters.add(RowFilter.regexFilter("^" + selectedType + "$", 4));
        }

        if (selectedSem != null && !selectedSem.equals("All Semesters")) {
            String semNumber = selectedSem.replace("Semester ", "").trim();
            filters.add(RowFilter.regexFilter("Sem " + semNumber, 7));
        }

        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }

        int visibleCount = timetableTable.getRowCount();
        int totalCount = allTimetableEntries.size();
        summaryFooterLabel.setText("Showing " + visibleCount + " of " + totalCount + " scheduled slots");
    }

    private TimetableEntry getSelectedEntry() {
        int selectedRow = timetableTable.getSelectedRow();
        if (selectedRow == -1) return null;
        int modelRow = timetableTable.convertRowIndexToModel(selectedRow);
        int timetableId = (int) tableModel.getValueAt(modelRow, 0);

        for (TimetableEntry e : allTimetableEntries) {
            if (e.getTimetableId() == timetableId) {
                return e;
            }
        }
        return null;
    }

    private void handleCreateSlotAction() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        TimetableFormDialog dialog = new TimetableFormDialog(parentFrame, adminController, TimetableFormDialog.FormMode.ADD, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadTimetableData();
        }
    }

    private void handleEditSlotAction() {
        TimetableEntry selected = getSelectedEntry();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a timetable slot to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        TimetableFormDialog dialog = new TimetableFormDialog(parentFrame, adminController, TimetableFormDialog.FormMode.UPDATE, selected);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadTimetableData();
        }
    }

    private void handleViewSlotAction() {
        TimetableEntry selected = getSelectedEntry();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a timetable slot to view.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        TimetableFormDialog dialog = new TimetableFormDialog(parentFrame, adminController, TimetableFormDialog.FormMode.VIEW, selected);
        dialog.setVisible(true);
    }

    private void handleDeleteSlotAction() {
        TimetableEntry selected = getSelectedEntry();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a timetable slot to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseCode = selected.getCourse() != null ? selected.getCourse().getCourseCode() : "Course";
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the scheduled timetable slot for:\n" +
                courseCode + " on " + selected.getDayOfWeek() + " (" + selected.getVenue() + ")?\n\n" +
                "This action cannot be undone.",
                "Confirm Slot Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = adminController.handleDeleteTimetable(selected.getTimetableId());
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Timetable slot deleted successfully.", "Slot Removed", JOptionPane.INFORMATION_MESSAGE);
                    loadTimetableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete timetable slot. Entry not found.", "Deletion Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(this, "Database error while deleting timetable slot: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
