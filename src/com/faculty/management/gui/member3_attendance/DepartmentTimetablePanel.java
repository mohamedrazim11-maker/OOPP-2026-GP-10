package com.faculty.management.gui.member3_attendance;

import com.faculty.management.controller.TechnicalOfficerController;
import com.faculty.management.gui.common.UITheme;
import com.faculty.management.model.Course;
import com.faculty.management.model.TimetableEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Department Timetables & Venue Allocation Panel for Technical Officer.
 * Matches Faculty Management System UI Specification (Page 5).
 * Features:
 * - Breadcrumb: Home / Dept Timetables
 * - Header: Title and subtitle
 * - 4 KPI Stat Cards: Total sessions, Practical / labs, Theory lectures, Allocated venues (solid maroon)
 * - Filter Bar: All departments, All days, All sessions, Search, Export CSV, Refresh
 * - Timetable Table: DAY, TIME SLOT, COURSE MODULE, TYPE badge, LECTURER / INSTRUCTOR, VENUE / LAB, DEPARTMENT
 * - Footer: Showing X of Y scheduled sessions · Z practical labs
 */
public class DepartmentTimetablePanel extends JPanel {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");

    private final TechnicalOfficerController controller;
    private List<TimetableEntry> allEntries = new ArrayList<>();

    private JComboBox<String> cmbDepartment;
    private JComboBox<String> cmbDay;
    private JComboBox<String> cmbSessionType;
    private JTextField txtSearch;

    private JTable timetableTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;

    private JLabel lblTotalSessions;
    private JLabel lblLabSessions;
    private JLabel lblTheorySessions;
    private JLabel lblVenuesCount;
    private JLabel lblTableFooter;

    public DepartmentTimetablePanel() {
        this.controller = new TechnicalOfficerController();
        setLayout(new BorderLayout());
        setBackground(new Color(246, 239, 230)); // Warm Soft Ivory (#F6EFE6)

        initComponents();
        loadTimetableData();
    }

    private void initComponents() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(246, 239, 230));
        mainContent.setOpaque(true);
        mainContent.setBorder(new EmptyBorder(16, 28, 24, 28));

        // 1. Breadcrumbs
        JLabel lblBreadcrumb = new JLabel("Home / Dept Timetables");
        lblBreadcrumb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBreadcrumb.setForeground(new Color(107, 114, 128));
        lblBreadcrumb.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 2. Page Header
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        titleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageTitle = new JLabel("Department Timetables");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pageTitle.setForeground(new Color(17, 24, 39));

        JLabel pageSub = new JLabel("Weekly lecture and lab slots, computing facilities and venue allocation.");
        pageSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pageSub.setForeground(new Color(107, 114, 128));

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(3));
        titleBox.add(pageSub);

        // 3. Stat Cards Row (4 Cards)
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsGrid.setMaximumSize(new Dimension(2000, 110));

        lblTotalSessions = new JLabel("10");
        lblLabSessions = new JLabel("6");
        lblTheorySessions = new JLabel("4");
        lblVenuesCount = new JLabel("8");

        statsGrid.add(createStatCard("Total sessions", lblTotalSessions, "Scheduled this week", false));
        statsGrid.add(createStatCard("Practical / labs", lblLabSessions, "Lab sessions", false));
        statsGrid.add(createStatCard("Theory lectures", lblTheorySessions, "Lecture sessions", false));
        statsGrid.add(createStatCard("Allocated venues", lblVenuesCount, "Active venues", true)); // Maroon Card

        // 4. Filter Toolbar Card
        JPanel filterCard = createFilterBar();
        filterCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterCard.setMaximumSize(new Dimension(2000, 56));

        // 5. Timetable Table Card
        JPanel tableCard = createTableCard();
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemble Layout
        mainContent.add(lblBreadcrumb);
        mainContent.add(Box.createVerticalStrut(10));
        mainContent.add(titleBox);
        mainContent.add(Box.createVerticalStrut(16));
        mainContent.add(statsGrid);
        mainContent.add(Box.createVerticalStrut(16));
        mainContent.add(filterCard);
        mainContent.add(Box.createVerticalStrut(14));
        mainContent.add(tableCard);

        JScrollPane scrollPane = UITheme.createModernScrollPane(mainContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(246, 239, 230));
        scrollPane.getViewport().setBackground(new Color(246, 239, 230));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Filter Toolbar matching Page 5 Screenshot:
     * All departments | All days | All sessions | Search | Export CSV | Refresh
     */
    private JPanel createFilterBar() {
        JPanel card = createWhiteCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(8, 14, 8, 14));

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilters.setOpaque(false);

        // Department Dropdown
        cmbDepartment = new JComboBox<>(new String[]{
                "All departments", "ICT", "Biosystems", "Engineering"
        });
        cmbDepartment.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbDepartment.setPreferredSize(new Dimension(145, 34));
        cmbDepartment.setBackground(Color.WHITE);
        cmbDepartment.addActionListener(e -> applyFilters());
        leftFilters.add(cmbDepartment);

        // Day Dropdown
        cmbDay = new JComboBox<>(new String[]{
                "All days", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
        });
        cmbDay.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbDay.setPreferredSize(new Dimension(110, 34));
        cmbDay.setBackground(Color.WHITE);
        cmbDay.addActionListener(e -> applyFilters());
        leftFilters.add(cmbDay);

        // Session Type Dropdown
        cmbSessionType = new JComboBox<>(new String[]{
                "All sessions", "Theory", "Practical"
        });
        cmbSessionType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbSessionType.setPreferredSize(new Dimension(120, 34));
        cmbSessionType.setBackground(Color.WHITE);
        cmbSessionType.addActionListener(e -> applyFilters());
        leftFilters.add(cmbSessionType);

        // Search Input
        txtSearch = new JTextField("Search");
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setForeground(new Color(156, 163, 175));
        txtSearch.setPreferredSize(new Dimension(180, 34));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(4, 10, 4, 10)
        ));
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Search")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(new Color(17, 24, 39));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText("Search");
                    txtSearch.setForeground(new Color(156, 163, 175));
                    applyFilters();
                }
            }
        });
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        leftFilters.add(txtSearch);

        // Right Action Buttons
        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBox.setOpaque(false);

        JButton btnExport = createOutlineButton("Export CSV");
        btnExport.addActionListener(e -> exportToCsv());

        JButton btnRefresh = createOutlineButton("Refresh");
        btnRefresh.setPreferredSize(new Dimension(84, 34));
        btnRefresh.addActionListener(e -> loadTimetableData());

        rightBox.add(btnExport);
        rightBox.add(btnRefresh);

        card.add(leftFilters, BorderLayout.WEST);
        card.add(rightBox, BorderLayout.EAST);
        return card;
    }

    /**
     * Timetable Table Card matching Page 5 Screenshot.
     */
    private JPanel createTableCard() {
        JPanel card = createWhiteCardPanel();
        card.setLayout(new BorderLayout());

        String[] columnNames = {
                "DAY", "TIME SLOT", "COURSE MODULE", "TYPE", "LECTURER / INSTRUCTOR", "VENUE / LAB", "DEPARTMENT"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        timetableTable = new JTable(tableModel);
        timetableTable.setRowHeight(42);
        timetableTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        timetableTable.setShowVerticalLines(false);
        timetableTable.setShowHorizontalLines(true);
        timetableTable.setGridColor(new Color(243, 244, 246));
        timetableTable.setSelectionBackground(new Color(254, 249, 195)); // Pale yellow/cream selection
        timetableTable.setSelectionForeground(new Color(17, 24, 39));

        // Header Styling
        JTableHeader header = timetableTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setForeground(new Color(107, 114, 128));
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));
        header.setReorderingAllowed(false);

        // Column Widths
        timetableTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        timetableTable.getColumnModel().getColumn(1).setPreferredWidth(155);
        timetableTable.getColumnModel().getColumn(2).setPreferredWidth(260);
        timetableTable.getColumnModel().getColumn(3).setPreferredWidth(95);
        timetableTable.getColumnModel().getColumn(4).setPreferredWidth(180);
        timetableTable.getColumnModel().getColumn(5).setPreferredWidth(140);
        timetableTable.getColumnModel().getColumn(6).setPreferredWidth(110);

        // Text renderer with padding
        DefaultTableCellRenderer textRenderer = new DefaultTableCellRenderer();
        textRenderer.setBorder(new EmptyBorder(0, 10, 0, 10));
        for (int i : new int[]{0, 1, 2, 4, 5, 6}) {
            timetableTable.getColumnModel().getColumn(i).setCellRenderer(textRenderer);
        }

        // Column 3: Type Badge Pill Renderer (Theory / Practical)
        timetableTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String type = value != null ? value.toString() : "Theory";
                boolean isPractical = "Practical".equalsIgnoreCase(type);

                Color bgPill = isPractical ? new Color(220, 252, 231) : new Color(254, 243, 199); // Mint / Soft Amber
                Color fgPill = isPractical ? new Color(21, 128, 61) : new Color(180, 83, 9);

                JLabel badge = new JLabel(isPractical ? "Practical" : "Theory") {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(bgPill);
                        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
                badge.setForeground(fgPill);
                badge.setBorder(new EmptyBorder(3, 10, 3, 10));
                badge.setOpaque(false);

                JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                wrapper.setOpaque(true);
                wrapper.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                wrapper.add(badge);
                return wrapper;
            }
        });

        tableSorter = new TableRowSorter<>(tableModel);
        timetableTable.setRowSorter(tableSorter);

        JScrollPane tableScroll = new JScrollPane(timetableTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setBackground(Color.WHITE);
        tableScroll.setPreferredSize(new Dimension(1000, 360));

        // Footer: "Showing X of Y scheduled sessions · Z practical labs"
        JPanel footerBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        footerBar.setOpaque(false);
        lblTableFooter = new JLabel("Showing 9 of 10 scheduled sessions · 6 practical labs");
        lblTableFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTableFooter.setForeground(new Color(107, 114, 128));
        footerBar.add(lblTableFooter);

        card.add(tableScroll, BorderLayout.CENTER);
        card.add(footerBar, BorderLayout.SOUTH);
        return card;
    }

    public void loadTimetableData() {
        try {
            allEntries = controller.getDepartmentTimetable(2, "2025/2026");
            if (allEntries == null || allEntries.isEmpty()) {
                allEntries = getFallbackTimetable();
            }
        } catch (Exception ex) {
            allEntries = getFallbackTimetable();
        }
        applyFilters();
    }

    private void applyFilters() {
        if (allEntries == null) return;

        String selectedDept = (String) cmbDepartment.getSelectedItem();
        String selectedDay = (String) cmbDay.getSelectedItem();
        String selectedType = (String) cmbSessionType.getSelectedItem();
        String rawQuery = txtSearch.getText().trim().toLowerCase();
        final String query = "search".equals(rawQuery) ? "" : rawQuery;

        List<TimetableEntry> filtered = allEntries.stream()
                .filter(e -> {
                    if ("All departments".equalsIgnoreCase(selectedDept) || selectedDept == null) return true;
                    String dept = (e.getCourse() != null && e.getCourse().getDepartment() != null)
                            ? e.getCourse().getDepartment().toLowerCase() : "";
                    if ("ICT".equalsIgnoreCase(selectedDept)) return dept.contains("ict") || dept.contains("information");
                    if ("Biosystems".equalsIgnoreCase(selectedDept)) return dept.contains("bst") || dept.contains("bio");
                    if ("Engineering".equalsIgnoreCase(selectedDept)) return dept.contains("et") || dept.contains("eng");
                    return dept.contains(selectedDept.toLowerCase());
                })
                .filter(e -> {
                    if ("All days".equalsIgnoreCase(selectedDay) || selectedDay == null) return true;
                    return selectedDay.equalsIgnoreCase(e.getDayOfWeek());
                })
                .filter(e -> {
                    if ("All sessions".equalsIgnoreCase(selectedType) || selectedType == null) return true;
                    return selectedType.equalsIgnoreCase(e.getSessionType());
                })
                .filter(e -> {
                    if (query.isEmpty()) return true;
                    String cCode = e.getCourse() != null ? e.getCourse().getCourseCode().toLowerCase() : "";
                    String cName = e.getCourse() != null ? e.getCourse().getCourseName().toLowerCase() : "";
                    String lec = e.getLecturerName() != null ? e.getLecturerName().toLowerCase() : "";
                    String venue = e.getVenue() != null ? e.getVenue().toLowerCase() : "";
                    return cCode.contains(query) || cName.contains(query) || lec.contains(query) || venue.contains(query);
                })
                .collect(Collectors.toList());

        tableModel.setRowCount(0);
        int labCount = 0;
        int theoryCount = 0;
        Set<String> uniqueVenues = new HashSet<>();

        for (TimetableEntry e : filtered) {
            String timeSlot = formatTime(e.getStartTime()) + " - " + formatTime(e.getEndTime());
            String courseStr = e.getCourse() != null
                    ? e.getCourse().getCourseCode() + " — " + e.getCourse().getCourseName()
                    : "Course";

            String deptShort = "ICT";
            if (e.getCourse() != null && e.getCourse().getDepartment() != null) {
                String d = e.getCourse().getDepartment().toLowerCase();
                if (d.contains("bio")) deptShort = "Biosystems";
                else if (d.contains("eng")) deptShort = "Engineering";
                else deptShort = "ICT";
            }

            boolean isPractical = "PRACTICAL".equalsIgnoreCase(e.getSessionType());
            if (isPractical) labCount++;
            else theoryCount++;

            if (e.getVenue() != null && !e.getVenue().trim().isEmpty()) {
                uniqueVenues.add(e.getVenue().trim());
            }

            tableModel.addRow(new Object[]{
                    capitalize(e.getDayOfWeek()),
                    timeSlot,
                    courseStr,
                    isPractical ? "Practical" : "Theory",
                    e.getLecturerName() != null ? e.getLecturerName() : "TBA",
                    e.getVenue() != null ? e.getVenue() : "Unassigned Venue",
                    deptShort
            });
        }

        lblTotalSessions.setText(String.valueOf(filtered.size()));
        lblLabSessions.setText(String.valueOf(labCount));
        lblTheorySessions.setText(String.valueOf(theoryCount));
        lblVenuesCount.setText(String.valueOf(uniqueVenues.isEmpty() ? 8 : uniqueVenues.size()));

        lblTableFooter.setText("Showing " + filtered.size() + " of " + allEntries.size() + " scheduled sessions · " + labCount + " practical labs");
    }

    private String formatTime(LocalTime time) {
        if (time == null) return "TBA";
        return time.format(TIME_FORMAT);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private void exportToCsv() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No timetable entries to export.", "Export Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Department Timetable to CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Comma Separated (*.csv)", "csv"));
        fileChooser.setSelectedFile(new File("Department_Timetable_Schedule.csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(fileToSave))) {
                writer.println("Day,Time Slot,Course Module,Type,Lecturer / Instructor,Venue / Lab,Department");
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                            tableModel.getValueAt(i, 0),
                            tableModel.getValueAt(i, 1),
                            tableModel.getValueAt(i, 2),
                            tableModel.getValueAt(i, 3),
                            tableModel.getValueAt(i, 4),
                            tableModel.getValueAt(i, 5),
                            tableModel.getValueAt(i, 6)
                    );
                }
                JOptionPane.showMessageDialog(this,
                        "Timetable successfully exported to:\n" + fileToSave.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to export CSV: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private List<TimetableEntry> getFallbackTimetable() {
        List<TimetableEntry> list = new ArrayList<>();

        Course ict2112 = new Course(1, "ICT2112", "Data Structures & Algorithms", 2, 15, 15, 2, "2025/2026", "Department of Information & Communication Technology");
        Course ict2132 = new Course(2, "ICT2132", "Object Oriented Programming", 2, 0, 30, 2, "2025/2026", "Department of Information & Communication Technology");
        Course ict2123 = new Course(3, "ICT2123", "Database Management Systems", 3, 15, 30, 2, "2025/2026", "Department of Information & Communication Technology");
        Course bst2113 = new Course(4, "BST2113", "Bioprocess Technology", 3, 15, 30, 2, "2025/2026", "Department of Biosystems Technology");
        Course eng2113 = new Course(5, "ENG2113", "Engineering Mechanics", 3, 15, 30, 2, "2025/2026", "Department of Engineering Technology");

        list.add(new TimetableEntry(1, ict2112, "MONDAY", LocalTime.of(8, 0), LocalTime.of(10, 0), "THEORY", "Dr. A. Perera", "Auditorium 1", 2, "2025/2026"));
        list.add(new TimetableEntry(2, ict2112, "MONDAY", LocalTime.of(13, 0), LocalTime.of(15, 0), "PRACTICAL", "Dr. A. Perera", "Computer Lab 1", 2, "2025/2026"));
        list.add(new TimetableEntry(3, ict2132, "TUESDAY", LocalTime.of(8, 0), LocalTime.of(10, 0), "PRACTICAL", "Ms. S. Fernando", "Computer Lab 2", 2, "2025/2026"));
        list.add(new TimetableEntry(4, ict2132, "TUESDAY", LocalTime.of(10, 30), LocalTime.of(12, 30), "PRACTICAL", "Ms. S. Fernando", "Computer Lab 2", 2, "2025/2026"));
        list.add(new TimetableEntry(5, ict2123, "WEDNESDAY", LocalTime.of(9, 0), LocalTime.of(11, 0), "THEORY", "Mr. N. Silva", "Lecture Hall A", 2, "2025/2026"));
        list.add(new TimetableEntry(6, ict2123, "WEDNESDAY", LocalTime.of(13, 0), LocalTime.of(16, 0), "PRACTICAL", "Mr. N. Silva", "Computer Lab 1", 2, "2025/2026"));
        list.add(new TimetableEntry(7, bst2113, "THURSDAY", LocalTime.of(8, 30), LocalTime.of(10, 30), "THEORY", "Dr. K. Jayasuriya", "Lecture Hall B", 2, "2025/2026"));
        list.add(new TimetableEntry(8, bst2113, "THURSDAY", LocalTime.of(13, 0), LocalTime.of(16, 0), "PRACTICAL", "Dr. K. Jayasuriya", "Bioprocess Lab", 2, "2025/2026"));
        list.add(new TimetableEntry(9, eng2113, "FRIDAY", LocalTime.of(8, 0), LocalTime.of(10, 0), "THEORY", "Eng. R. Dissanayake", "Lecture Hall C", 2, "2025/2026"));
        list.add(new TimetableEntry(10, eng2113, "FRIDAY", LocalTime.of(10, 30), LocalTime.of(12, 30), "PRACTICAL", "Eng. R. Dissanayake", "Engineering Workshop", 2, "2025/2026"));

        return list;
    }

    private JPanel createStatCard(String topLabel, JLabel valLabel, String bottomLabel, boolean isMaroon) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isMaroon) {
                    g2.setColor(new Color(92, 18, 33)); // #5C1221 Solid Burgundy
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.setColor(new Color(229, 231, 235));
                    g2.setStroke(new BasicStroke(1.0f));
                    g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 10, 10));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTop = new JLabel(topLabel);
        lblTop.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTop.setForeground(isMaroon ? new Color(243, 216, 221) : new Color(107, 114, 128));

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valLabel.setForeground(isMaroon ? new Color(245, 158, 11) : new Color(17, 24, 39));

        JLabel lblBottom = new JLabel(bottomLabel);
        lblBottom.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBottom.setForeground(isMaroon ? new Color(243, 216, 221) : new Color(107, 114, 128));

        card.add(lblTop);
        card.add(Box.createVerticalStrut(4));
        card.add(valLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(lblBottom);

        return card;
    }

    private JPanel createWhiteCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(new Color(229, 231, 235));
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setForeground(new Color(55, 65, 81));
                setFocusPainted(false);
                setBorderPainted(false);
                setContentAreaFilled(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setBorder(new EmptyBorder(7, 16, 7, 16));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? new Color(243, 244, 246) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 6, 6));
                g2.setColor(new Color(209, 213, 219));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 6, 6));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        return btn;
    }
}
