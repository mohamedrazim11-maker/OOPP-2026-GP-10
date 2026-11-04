package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.controller.LoginController;
import com.faculty.management.model.Admin;
import com.faculty.management.model.Course;
import com.faculty.management.model.Notice;
import com.faculty.management.model.Role;
import com.faculty.management.model.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.faculty.management.gui.member1_admin.AdminUIComponents.*;

/**
 * Admin Dashboard GUI for the Administrator (Member 1).
 *
 * <p>Drop-in replacement for the previous AdminDashboard. Same package, same
 * constructors, same public {@link #switchTab(int)} method.</p>
 *
 * <ul>
 *   <li><b>Real data only</b> - every figure and notice on the Overview is loaded from the
 *       database through {@link AdminController} on a background thread (SwingWorker), so the
 *       window never freezes. There are no hard-coded sample values or fallback rows; loading,
 *       empty and error states are shown instead.</li>
 *   <li><b>Responsive</b> - the page scrolls vertically and reflows with the window width:
 *       stat cards go 4 -> 2 -> 1 columns, the two lower panels stack, and the header,
 *       navigation bar and footer condense on narrow windows.</li>
 *   <li><b>No trend text</b> - the "+1 from last week" style captions are gone.</li>
 * </ul>
 *
 * <p>OOP principles demonstrated: classes and objects (modular panels), inheritance (extends
 * JFrame, custom painted components), constructor chaining (this(...)), encapsulation (private
 * state), polymorphism (overridden paint/layout methods) and structured exception handling.</p>
 */
public class AdminDashboard extends JFrame {

    // ---------------------------------------------------------------------
    // State and controllers (encapsulated)
    // ---------------------------------------------------------------------
    private final User currentUser;
    private final AdminController adminController;

    // Header / navigation / footer
    private JLabel headerSubTitleLabel;
    private JPanel headerUserTextBox;
    private JLabel footerTaglineLabel;
    private JLabel breadcrumbLabel;
    private JLabel lastUpdatedLabel;
    private final List<ModernNavTabButton> navTabButtons = new ArrayList<>();

    // Card navigation
    private JPanel cardsContainer;
    private CardLayout cardLayout;

    // Tab panels
    private UserManagementPanel userManagementPanel;
    private CourseManagementPanel courseManagementPanel;
    private NoticeManagementPanel noticeManagementPanel;

    // Overview widgets (filled with real database data)
    private JPanel topHeaderRow;
    private JPanel lastUpdatedBox;
    private JPanel statsRow;
    private JPanel columnsPanel;
    private JPanel errorBanner;
    private JLabel errorBannerLabel;
    private StatCard totalUsersCard;
    private StatCard coursesCard;
    private StatCard noticesCard;
    private StatCard urgentCard;
    private JPanel recentNoticesListContainer;

    // Overview loading state
    private SwingWorker<DashboardData, Void> overviewLoader;
    private boolean overviewLoadedOnce = false;

    // Responsive state (-1 / null = not applied yet)
    private int appliedStatCols = -1;
    private int appliedPanelCols = -1;
    private Boolean headerCompact = null;
    private Boolean navCompact = null;
    private Boolean topRowStacked = null;
    private Boolean footerCompact = null;

    private int currentTabIndex = 0;

    private static final String[] TAB_NAMES = {
            "Overview",
            "User Management",
            "Course Management",
            "Notice Board",
            "Timetable"
    };

    private static final int RECENT_NOTICE_LIMIT = 4;
    private static final DateTimeFormatter NOTICE_DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter UPDATED_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy · hh:mm a");
    private static final String EM_DASH = "—";

    // Optional header watermark (no logo is drawn anywhere)
    private static BufferedImage watermarkImage;

    static {
        try {
            File[] candidates = {
                    new File("resources/images/uni_image.jpeg"),
                    new File("resources/uni_image.jpeg"),
                    new File("uni_image.jpeg")
            };
            for (File f : candidates) {
                if (f.exists()) {
                    watermarkImage = ImageIO.read(f);
                    break;
                }
            }
        } catch (Exception ignored) {
            // The watermark is purely decorative.
        }
    }

    // ---------------------------------------------------------------------
    // Constructors (chaining)
    // ---------------------------------------------------------------------

    public AdminDashboard() {
        this(null);
    }

    public AdminDashboard(User user) {
        super("Faculty Management System — Administrator");
        this.currentUser = (user != null) ? user : new Admin(1, "admin", "admin123", "admin@fot.ruh.ac.lk", "Admin", "User", new Role(1, Role.ADMIN, "Administrator"), "0771234567", null, "ACTIVE");
        this.adminController = new AdminController();
        initComponents();
    }

    private void initComponents() {
        setSize(1260, 800);
        setMinimumSize(new Dimension(560, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_PAGE_BG);

        // Top: burgundy header + navigation bar
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);
        topWrapper.add(createTopBurgundyHeader());
        topWrapper.add(createHorizontalNavBar());
        add(topWrapper, BorderLayout.NORTH);

        // Centre: card container (each page scrolls on its own)
        cardLayout = new CardLayout();
        cardsContainer = new JPanel(cardLayout);
        cardsContainer.setOpaque(false);

        userManagementPanel = new UserManagementPanel(adminController);
        courseManagementPanel = new CourseManagementPanel(adminController);
        noticeManagementPanel = new NoticeManagementPanel(adminController);

        cardsContainer.add(wrapInScrollPane(createOverviewPanel()), TAB_NAMES[0]);
        cardsContainer.add(userManagementPanel, TAB_NAMES[1]);
        cardsContainer.add(courseManagementPanel, TAB_NAMES[2]);
        cardsContainer.add(noticeManagementPanel, TAB_NAMES[3]);
        cardsContainer.add(wrapInScrollPane(createTimetablePanel()), TAB_NAMES[4]);
        add(cardsContainer, BorderLayout.CENTER);

        // Bottom: footer
        add(createFooterBar(), BorderLayout.SOUTH);

        // Responsive behaviour
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyResponsiveLayout();
            }
        });
        applyResponsiveLayout();

        // Show Overview and load its data
        switchTab(0);
    }

    // =========================================================================
    // RESPONSIVE LAYOUT
    // =========================================================================

    /**
     * Re-flows the dashboard for the current window width. Only the parts whose
     * breakpoint actually changed are touched, so resizing stays smooth.
     */
    private void applyResponsiveLayout() {
        int w = getWidth();

        // Stat cards: 4 columns -> 2 -> 1
        int statCols = (w >= 1000) ? 4 : (w >= 600 ? 2 : 1);
        if (statsRow != null && statCols != appliedStatCols) {
            appliedStatCols = statCols;
            statsRow.setLayout(new GridLayout(0, statCols, 16, 16));
            statsRow.revalidate();
            statsRow.repaint();
        }

        // Modules + Recent notices: side by side -> stacked
        int panelCols = (w >= 900) ? 2 : 1;
        if (columnsPanel != null && panelCols != appliedPanelCols) {
            appliedPanelCols = panelCols;
            columnsPanel.setLayout(new GridLayout(0, panelCols, 18, 18));
            columnsPanel.revalidate();
            columnsPanel.repaint();
        }

        // Header: hide the subtitle and user name on narrow windows
        boolean hCompact = w < 760;
        if (headerCompact == null || headerCompact != hCompact) {
            headerCompact = hCompact;
            if (headerSubTitleLabel != null) headerSubTitleLabel.setVisible(!hCompact);
            if (headerUserTextBox != null) headerUserTextBox.setVisible(!hCompact);
        }

        // Navigation: icon-only tabs on narrow windows
        boolean nCompact = w < 860;
        if (navCompact == null || navCompact != nCompact) {
            navCompact = nCompact;
            for (ModernNavTabButton b : navTabButtons) {
                b.setCompact(nCompact);
            }
        }

        // Overview title row: move "Last updated" under the title on narrow windows
        boolean stacked = w < 680;
        if (topHeaderRow != null && lastUpdatedBox != null && (topRowStacked == null || topRowStacked != stacked)) {
            topRowStacked = stacked;
            topHeaderRow.remove(lastUpdatedBox);
            ((FlowLayout) lastUpdatedBox.getLayout()).setAlignment(stacked ? FlowLayout.LEFT : FlowLayout.RIGHT);
            topHeaderRow.add(lastUpdatedBox, stacked ? BorderLayout.SOUTH : BorderLayout.EAST);
            topHeaderRow.revalidate();
            topHeaderRow.repaint();
        }

        // Footer: hide the tagline on very narrow windows
        boolean fCompact = w < 700;
        if (footerCompact == null || footerCompact != fCompact) {
            footerCompact = fCompact;
            if (footerTaglineLabel != null) footerTaglineLabel.setVisible(!fCompact);
        }
    }

    private JScrollPane wrapInScrollPane(JComponent content) {
        JScrollPane sp = new JScrollPane(content,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.getViewport().setBackground(COLOR_PAGE_BG);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    // =========================================================================
    // HEADER
    // =========================================================================

    private JPanel createTopBurgundyHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(COLOR_BURGUNDY);
                g2.fillRect(0, 0, w, h);

                if (watermarkImage != null) {
                    int imgW = (int) (w * 0.48);
                    int imgX = w - imgW;
                    if (imgW > 0 && h > 0) {
                        BufferedImage blended = new BufferedImage(imgW, h, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D bg2 = blended.createGraphics();
                        bg2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        bg2.drawImage(watermarkImage, 0, 0, imgW, h, null);
                        Paint p = new GradientPaint(
                                0, 0, new Color(COLOR_BURGUNDY.getRed(), COLOR_BURGUNDY.getGreen(), COLOR_BURGUNDY.getBlue(), 255),
                                imgW, 0, new Color(COLOR_BURGUNDY.getRed(), COLOR_BURGUNDY.getGreen(), COLOR_BURGUNDY.getBlue(), 120));
                        bg2.setPaint(p);
                        bg2.fillRect(0, 0, imgW, h);
                        bg2.dispose();
                        g2.drawImage(blended, imgX, 0, null);
                    }
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 64));
        header.setBorder(new EmptyBorder(0, 24, 0, 24));

        // Left: accent bar + title + subtitle (no logo)
        JPanel brandBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        brandBox.setOpaque(false);

        JPanel vertBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 180));
                g2.fillRoundRect(0, 4, 2, getHeight() - 8, 2, 2);
                g2.dispose();
            }
        };
        vertBar.setPreferredSize(new Dimension(3, 40));
        vertBar.setOpaque(false);

        JPanel textStack = new JPanel();
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.setOpaque(false);

        JLabel appTitle = new JLabel("Faculty Management System");
        appTitle.setFont(FONT_HEADER_TITLE);
        appTitle.setForeground(Color.WHITE);

        headerSubTitleLabel = new JLabel("Faculty of Technology · University of Ruhuna");
        headerSubTitleLabel.setFont(FONT_HEADER_SUB);
        headerSubTitleLabel.setForeground(COLOR_TEXT_SUBHEADER);

        textStack.add(Box.createVerticalStrut(2));
        textStack.add(appTitle);
        textStack.add(Box.createVerticalStrut(3));
        textStack.add(headerSubTitleLabel);

        brandBox.add(vertBar);
        brandBox.add(textStack);

        JPanel brandWrapper = new JPanel(new GridBagLayout());
        brandWrapper.setOpaque(false);
        brandWrapper.add(brandBox);

        // Right: avatar, name/role, sign out (real signed-in user)
        JPanel userBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        userBox.setOpaque(false);

        String fullName = currentUser.getFullName();
        if (fullName == null || fullName.trim().isEmpty()) fullName = "Admin User";

        AvatarBadgeComponent avatar = new AvatarBadgeComponent(extractInitials(fullName), 38, new Color(250, 245, 236), COLOR_BURGUNDY);

        headerUserTextBox = new JPanel();
        headerUserTextBox.setLayout(new BoxLayout(headerUserTextBox, BoxLayout.Y_AXIS));
        headerUserTextBox.setOpaque(false);

        JLabel nameLabel = new JLabel(fullName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(Color.WHITE);

        String roleTitle = (currentUser.getRole() != null) ? currentUser.getRole().getRoleName() : "Administrator";
        if ("ADMIN".equalsIgnoreCase(roleTitle)) roleTitle = "Administrator";
        JLabel roleLabel = new JLabel(roleTitle);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(COLOR_TEXT_SUBHEADER);

        headerUserTextBox.add(nameLabel);
        headerUserTextBox.add(Box.createVerticalStrut(2));
        headerUserTextBox.add(roleLabel);

        JButton signOutBtn = new JButton("Sign out") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                if (getModel().isPressed()) {
                    g2.setColor(new Color(60, 10, 20, 220));
                    g2.fillRoundRect(0, 0, w, h, h, h);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(115, 24, 43, 200));
                    g2.fillRoundRect(0, 0, w, h, h, h);
                }
                g2.setColor(new Color(255, 255, 255, 140));
                g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        signOutBtn.setIcon(new SignOutIcon(13, Color.WHITE));
        signOutBtn.setIconTextGap(6);
        signOutBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        signOutBtn.setForeground(Color.WHITE);
        signOutBtn.setFocusPainted(false);
        signOutBtn.setBorderPainted(false);
        signOutBtn.setContentAreaFilled(false);
        signOutBtn.setRolloverEnabled(true);
        signOutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signOutBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
        signOutBtn.addActionListener(e -> handleSignOut());

        userBox.add(avatar);
        userBox.add(headerUserTextBox);
        userBox.add(Box.createHorizontalStrut(6));
        userBox.add(signOutBtn);

        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.add(userBox);

        header.add(brandWrapper, BorderLayout.WEST);
        header.add(rightWrapper, BorderLayout.EAST);
        return header;
    }

    private static String extractInitials(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty() && sb.length() < 2) {
                sb.append(Character.toUpperCase(p.charAt(0)));
            }
        }
        return sb.length() == 0 ? "AU" : sb.toString();
    }

    // =========================================================================
    // NAVIGATION BAR
    // =========================================================================

    private JPanel createHorizontalNavBar() {
        JPanel navBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(COLOR_NAV_BG);
                g2.fillRect(0, 0, w, h);

                int ribbonW = 220;
                Path2D.Double swoosh = new Path2D.Double();
                swoosh.moveTo(w - ribbonW, 0);
                swoosh.curveTo(w - ribbonW * 0.6, 0, w - ribbonW * 0.3, h * 0.8, w, h);
                swoosh.lineTo(w, 0);
                swoosh.closePath();
                g2.setPaint(new GradientPaint(w - ribbonW, 0, new Color(225, 151, 47, 40), w, h / 2f, new Color(225, 151, 47, 160)));
                g2.fill(swoosh);

                Path2D.Double innerSwoosh = new Path2D.Double();
                innerSwoosh.moveTo(w - ribbonW * 0.6, 0);
                innerSwoosh.curveTo(w - ribbonW * 0.3, 0, w - ribbonW * 0.1, h * 0.6, w, h * 0.7);
                innerSwoosh.lineTo(w, 0);
                innerSwoosh.closePath();
                g2.setPaint(new GradientPaint(w - ribbonW * 0.6f, 0, new Color(225, 151, 47, 30), w, 0, new Color(225, 151, 47, 210)));
                g2.fill(innerSwoosh);

                g2.setColor(COLOR_NAV_BORDER);
                g2.drawLine(0, h - 1, w, h - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        navBar.setOpaque(false);
        navBar.setPreferredSize(new Dimension(0, 46));
        navBar.setBorder(new EmptyBorder(0, 16, 0, 16));

        JPanel tabsFlow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        tabsFlow.setOpaque(false);

        Icon[] activeIcons = {
                new HomeIcon(15, Color.WHITE),
                new UserGroupIcon(15, Color.WHITE),
                new BookIcon(15, Color.WHITE),
                new MegaphoneIcon(15, Color.WHITE),
                new CalendarIcon(15, Color.WHITE)
        };
        Icon[] inactiveIcons = {
                new HomeIcon(15, COLOR_NAV_TEXT),
                new UserGroupIcon(15, COLOR_NAV_TEXT),
                new BookIcon(15, COLOR_NAV_TEXT),
                new MegaphoneIcon(15, COLOR_NAV_TEXT),
                new CalendarIcon(15, COLOR_NAV_TEXT)
        };

        navTabButtons.clear();
        for (int i = 0; i < TAB_NAMES.length; i++) {
            final int tabIdx = i;
            boolean hasSeparator = (i > 0 && i < TAB_NAMES.length - 1);
            ModernNavTabButton tabBtn = new ModernNavTabButton(TAB_NAMES[i], activeIcons[i], inactiveIcons[i], i == currentTabIndex, hasSeparator);
            tabBtn.addActionListener(e -> switchTab(tabIdx));
            navTabButtons.add(tabBtn);
            tabsFlow.add(tabBtn);
        }

        navBar.add(tabsFlow, BorderLayout.WEST);
        return navBar;
    }

    // =========================================================================
    // FOOTER
    // =========================================================================

    private JPanel createFooterBar() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(COLOR_FOOTER_BG);
        footer.setPreferredSize(new Dimension(0, 30));
        footer.setBorder(new EmptyBorder(0, 24, 0, 24));

        EllipsisLabel leftLbl = new EllipsisLabel("Faculty Management System  |  Faculty of Technology, University of Ruhuna");
        leftLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        leftLbl.setForeground(new Color(230, 204, 210));

        footerTaglineLabel = new JLabel("Knowledge · Innovation · Excellence —");
        footerTaglineLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        footerTaglineLabel.setForeground(COLOR_GOLD_ACCENT);

        footer.add(leftLbl, BorderLayout.CENTER);
        footer.add(footerTaglineLabel, BorderLayout.EAST);
        return footer;
    }

    // =========================================================================
    // TAB SWITCHING
    // =========================================================================

    public void switchTab(int index) {
        if (index < 0 || index >= TAB_NAMES.length) return;
        currentTabIndex = index;

        for (int i = 0; i < navTabButtons.size(); i++) {
            navTabButtons.get(i).setActive(i == index);
        }

        if (breadcrumbLabel != null) {
            breadcrumbLabel.setText("  /  " + TAB_NAMES[index]);
        }

        // Refresh dynamic data every time a data tab is opened
        if (index == 0) {
            refreshOverviewPanel();
        } else if (index == 1) {
            userManagementPanel.loadUserDataFromBackend();
        } else if (index == 2) {
            courseManagementPanel.loadCourseTable();
        } else if (index == 3) {
            noticeManagementPanel.loadNotices();
        }

        cardLayout.show(cardsContainer, TAB_NAMES[index]);
    }

    // =========================================================================
    // OVERVIEW PAGE
    // =========================================================================

    private JPanel createOverviewPanel() {
        ScrollablePanel panel = new ScrollablePanel(new BorderLayout(0, 16));
        panel.setOpaque(true);
        panel.setBackground(COLOR_PAGE_BG);
        panel.setBorder(new EmptyBorder(16, 24, 20, 24));

        // ---- Title row: breadcrumb, title, subtitle | Last updated + Refresh ----
        topHeaderRow = new JPanel(new BorderLayout());
        topHeaderRow.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JPanel breadcrumbBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        breadcrumbBox.setOpaque(false);
        breadcrumbLabel = new JLabel("  /  " + TAB_NAMES[0]);
        breadcrumbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        breadcrumbLabel.setForeground(COLOR_TEXT_MUTED);
        breadcrumbBox.add(new JLabel(new HomeIcon(12, COLOR_TEXT_MUTED)));
        breadcrumbBox.add(breadcrumbLabel);

        JLabel pageTitle = new JLabel("Overview");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(new Color(27, 36, 59));

        JLabel pageSub = new JLabel("Administrative summary for the Faculty of Technology.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);

        titleBox.add(breadcrumbBox);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(pageSub);

        lastUpdatedLabel = new JLabel("Not loaded yet");
        lastUpdatedLabel.setIcon(new CalendarIcon(13, COLOR_TEXT_MUTED));
        lastUpdatedLabel.setIconTextGap(6);
        lastUpdatedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lastUpdatedLabel.setForeground(COLOR_TEXT_MUTED);

        JLabel refreshLink = new JLabel("Refresh");
        refreshLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshLink.setForeground(COLOR_BURGUNDY);
        refreshLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                refreshOverviewPanel();
            }
        });

        lastUpdatedBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        lastUpdatedBox.setOpaque(false);
        lastUpdatedBox.add(lastUpdatedLabel);
        lastUpdatedBox.add(refreshLink);

        topHeaderRow.add(titleBox, BorderLayout.WEST);
        topHeaderRow.add(lastUpdatedBox, BorderLayout.EAST);

        // ---- Error banner (hidden until a load fails) ----
        errorBanner = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(254, 242, 242));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(248, 180, 180));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        errorBanner.setOpaque(false);
        errorBanner.setBorder(new EmptyBorder(10, 14, 10, 14));
        errorBanner.setVisible(false);

        errorBannerLabel = new EllipsisLabel("Could not load data.");
        errorBannerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorBannerLabel.setForeground(new Color(153, 27, 27));
        errorBannerLabel.setIcon(new AlertTriangleIcon(16, new Color(153, 27, 27)));
        errorBannerLabel.setIconTextGap(8);

        JLabel retryLink = new JLabel("Retry");
        retryLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        retryLink.setForeground(new Color(153, 27, 27));
        retryLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        retryLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                refreshOverviewPanel();
            }
        });

        errorBanner.add(errorBannerLabel, BorderLayout.CENTER);
        errorBanner.add(retryLink, BorderLayout.EAST);

        // ---- Stat cards (values are filled from the database) ----
        totalUsersCard = new StatCard("Total users", new UserGroupIcon(22, Color.WHITE),
                new Color(142, 34, 57), new Color(253, 236, 239));
        coursesCard = new StatCard("Courses", new BookIcon(22, Color.WHITE),
                new Color(212, 154, 50), new Color(253, 244, 229));
        noticesCard = new StatCard("Published notices", new MegaphoneIcon(22, Color.WHITE),
                new Color(30, 59, 92), new Color(235, 243, 250));
        urgentCard = new StatCard("Urgent notices", new AlertTriangleIcon(22, Color.WHITE),
                new Color(122, 19, 39), new Color(253, 236, 239));

        statsRow = new JPanel(new GridLayout(0, 4, 16, 16));
        statsRow.setOpaque(false);
        statsRow.add(totalUsersCard);
        statsRow.add(coursesCard);
        statsRow.add(noticesCard);
        statsRow.add(urgentCard);

        JPanel northStack = new JPanel(new BorderLayout(0, 12));
        northStack.setOpaque(false);
        northStack.add(errorBanner, BorderLayout.NORTH);
        northStack.add(statsRow, BorderLayout.CENTER);

        // ---- Lower area: Administration modules | Recent notices ----
        columnsPanel = new JPanel(new GridLayout(0, 2, 18, 18));
        columnsPanel.setOpaque(false);
        columnsPanel.add(createAdminModulesCard());
        columnsPanel.add(createRecentNoticesCard());

        JPanel centerArea = new JPanel(new BorderLayout(0, 18));
        centerArea.setOpaque(false);
        centerArea.add(northStack, BorderLayout.NORTH);
        centerArea.add(columnsPanel, BorderLayout.CENTER);

        panel.add(topHeaderRow, BorderLayout.NORTH);
        panel.add(centerArea, BorderLayout.CENTER);
        return panel;
    }

    // ---------------------------------------------------------------------
    // Card builders
    // ---------------------------------------------------------------------

    /**
     * Builds a card with a solid burgundy header (icon + title + link) and a white body.
     * The body is top-aligned so rows keep their natural height.
     */
    private JPanel createCard(String title, Icon icon, String linkText, Runnable onLink, JComponent bodyContent) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JPanel headerBanner = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_BURGUNDY_MODULE_HEADER);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + 10, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        headerBanner.setOpaque(false);
        headerBanner.setPreferredSize(new Dimension(0, 42));
        headerBanner.setBorder(new EmptyBorder(0, 16, 0, 16));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setIcon(icon);
        titleLbl.setIconTextGap(8);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(Color.WHITE);

        EllipsisLabel linkLbl = new EllipsisLabel(linkText);
        linkLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        linkLbl.setForeground(new Color(240, 214, 220));
        linkLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        linkLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onLink.run();
            }
        });

        headerBanner.add(titleLbl, BorderLayout.WEST);
        headerBanner.add(linkLbl, BorderLayout.CENTER);

        JPanel bodyCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, -10, getWidth(), getHeight() + 10, 10, 10));
                g2.setColor(COLOR_BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, -10f, getWidth() - 1, getHeight() + 9.5f, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bodyCard.setOpaque(false);
        bodyCard.setBorder(new EmptyBorder(10, 16, 10, 16));
        bodyCard.add(bodyContent, BorderLayout.NORTH);

        container.add(headerBanner, BorderLayout.NORTH);
        container.add(bodyCard, BorderLayout.CENTER);
        return container;
    }

    private JPanel createAdminModulesCard() {
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        list.add(createModuleItemRow("User Management", "Create, update and maintain user credentials and roles.",
                new UserGroupIcon(18, Color.WHITE), () -> switchTab(1)));
        list.add(createDivider());
        list.add(createModuleItemRow("Course Management", "Add, update, delete and view academic courses.",
                new GradCapIcon(18, Color.WHITE), () -> switchTab(2)));
        list.add(createDivider());
        list.add(createModuleItemRow("Notice Board", "Create, edit and delete official announcements.",
                new PushpinIcon(18, Color.WHITE), () -> switchTab(3)));
        list.add(createDivider());
        list.add(createModuleItemRow("Timetable Management", "Create lecture and lab timetables.",
                new CalendarIcon(18, Color.WHITE), () -> switchTab(4)));

        return createCard("Administration modules", new GearIcon(16, Color.WHITE),
                "Manage system sections →", () -> switchTab(1), list);
    }

    private JPanel createRecentNoticesCard() {
        recentNoticesListContainer = new JPanel();
        recentNoticesListContainer.setLayout(new BoxLayout(recentNoticesListContainer, BoxLayout.Y_AXIS));
        recentNoticesListContainer.setOpaque(false);
        showNoticesMessage("Loading notices…");

        return createCard("Recent Notices", new BellIcon(16, Color.WHITE),
                "View all notices →", () -> switchTab(3), recentNoticesListContainer);
    }

    private JPanel createModuleItemRow(String name, String desc, Icon icon, Runnable onOpen) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_BURGUNDY_MODULE_HEADER);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                if (icon != null) {
                    icon.paintIcon(this, g2, (getWidth() - icon.getIconWidth()) / 2, (getHeight() - icon.getIconHeight()) / 2);
                }
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(36, 36));
        badge.setOpaque(false);
        JPanel badgeWrap = new JPanel(new GridBagLayout());
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge);

        JPanel textBox = new JPanel();
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        textBox.setOpaque(false);

        EllipsisLabel nameLbl = new EllipsisLabel(name);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLbl.setForeground(new Color(27, 36, 59));

        EllipsisLabel descLbl = new EllipsisLabel(desc);
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLbl.setForeground(COLOR_TEXT_MUTED);
        descLbl.setToolTipText(desc);

        textBox.add(nameLbl);
        textBox.add(Box.createVerticalStrut(2));
        textBox.add(descLbl);

        JButton openBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                if (getModel().isPressed()) {
                    g2.setColor(new Color(235, 226, 214));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(243, 236, 224));
                } else {
                    g2.setColor(new Color(250, 245, 236));
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 6, 6));
                g2.setColor(new Color(232, 224, 210));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, 6, 6));
                ArrowRightIcon arrow = new ArrowRightIcon(14, new Color(43, 37, 32));
                arrow.paintIcon(this, g2, (w - arrow.getIconWidth()) / 2, (h - arrow.getIconHeight()) / 2);
                g2.dispose();
            }
        };
        openBtn.setPreferredSize(new Dimension(32, 32));
        openBtn.setFocusPainted(false);
        openBtn.setBorderPainted(false);
        openBtn.setContentAreaFilled(false);
        openBtn.setRolloverEnabled(true);
        openBtn.setToolTipText("Open " + name);
        openBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        openBtn.addActionListener(e -> onOpen.run());
        JPanel openWrap = new JPanel(new GridBagLayout());
        openWrap.setOpaque(false);
        openWrap.add(openBtn);

        row.add(badgeWrap, BorderLayout.WEST);
        row.add(textBox, BorderLayout.CENTER);
        row.add(openWrap, BorderLayout.EAST);
        return row;
    }

    private JComponent createDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(243, 244, 246));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    // ---------------------------------------------------------------------
    // Notices list (real rows only)
    // ---------------------------------------------------------------------

    private void showNoticesMessage(String message) {
        recentNoticesListContainer.removeAll();
        JLabel msg = new JLabel(message, SwingConstants.CENTER);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msg.setForeground(COLOR_TEXT_MUTED);
        msg.setBorder(new EmptyBorder(24, 0, 24, 0));
        msg.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(msg, BorderLayout.CENTER);
        recentNoticesListContainer.add(wrap);
        recentNoticesListContainer.revalidate();
        recentNoticesListContainer.repaint();
    }

    private void populateRecentNotices(List<Notice> recent) {
        recentNoticesListContainer.removeAll();
        if (recent == null || recent.isEmpty()) {
            showNoticesMessage("No notices have been published yet.");
            return;
        }
        for (int i = 0; i < recent.size(); i++) {
            Notice n = recent.get(i);

            Object postedBy = n.getPostedBy();
            String by = (postedBy != null && !String.valueOf(postedBy).trim().isEmpty())
                    ? String.valueOf(postedBy) : "Faculty Administration";
            String date = formatDate(n.getPostedDate());
            String sub = date.isEmpty() ? by : by + " · " + date;

            String title = (n.getTitle() != null && !n.getTitle().trim().isEmpty()) ? n.getTitle() : "(Untitled notice)";
            recentNoticesListContainer.add(createModernNoticeRow(title, sub, priorityLabel(n.getPriority())));
            if (i < recent.size() - 1) {
                recentNoticesListContainer.add(createDivider());
            }
        }
        recentNoticesListContainer.revalidate();
        recentNoticesListContainer.repaint();
    }

    private static String priorityLabel(String priority) {
        String p = (priority != null) ? priority.toUpperCase() : "NORMAL";
        if (p.contains("URGENT") || p.contains("HIGH")) return "Urgent";
        if (p.contains("INFO") || p.contains("LOW")) return "Info";
        return "Normal";
    }

    private static boolean isUrgent(Notice n) {
        return "Urgent".equals(priorityLabel(n.getPriority()));
    }

    private JPanel createModernNoticeRow(String titleText, String subText, String priorityType) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(8, 0, 8, 0));

        final Color badgeBg;
        final Icon badgeIcon;
        final Color pillBg;
        final Color pillFg;

        if ("Urgent".equalsIgnoreCase(priorityType)) {
            badgeBg = new Color(142, 34, 57);
            badgeIcon = new MegaphoneIcon(15, Color.WHITE);
            pillBg = new Color(254, 226, 226);
            pillFg = new Color(153, 27, 27);
        } else if ("Info".equalsIgnoreCase(priorityType)) {
            badgeBg = new Color(212, 154, 50);
            badgeIcon = new CalendarIcon(15, Color.WHITE);
            pillBg = new Color(254, 243, 199);
            pillFg = new Color(146, 64, 14);
        } else {
            badgeBg = new Color(30, 59, 92);
            badgeIcon = new InfoIcon(15, Color.WHITE);
            pillBg = new Color(224, 242, 254);
            pillFg = new Color(3, 105, 161);
        }

        JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(badgeBg);
                g2.fillOval(0, 0, getWidth(), getHeight());
                badgeIcon.paintIcon(this, g2, (getWidth() - badgeIcon.getIconWidth()) / 2, (getHeight() - badgeIcon.getIconHeight()) / 2);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(32, 32));
        badge.setOpaque(false);
        JPanel badgeWrap = new JPanel(new GridBagLayout());
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge);

        JPanel textBox = new JPanel();
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        textBox.setOpaque(false);

        EllipsisLabel titleLbl = new EllipsisLabel(titleText);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(new Color(27, 36, 59));
        titleLbl.setToolTipText(titleText);

        EllipsisLabel subLbl = new EllipsisLabel(subText);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLbl.setForeground(COLOR_TEXT_MUTED);
        subLbl.setToolTipText(subText);

        textBox.add(titleLbl);
        textBox.add(Box.createVerticalStrut(2));
        textBox.add(subLbl);

        JPanel pillWrapper = new JPanel(new GridBagLayout());
        pillWrapper.setOpaque(false);
        pillWrapper.add(new PillBadge(priorityType, pillBg, pillFg));

        row.add(badgeWrap, BorderLayout.WEST);
        row.add(textBox, BorderLayout.CENTER);
        row.add(pillWrapper, BorderLayout.EAST);
        return row;
    }

    // =========================================================================
    // DATABASE LOADING (background thread)
    // =========================================================================

    /** Immutable snapshot of everything the Overview page shows. */
    private static final class DashboardData {
        final int userCount;
        final int courseCount;
        final int noticeCount;
        final int urgentCount;
        final List<Notice> recentNotices;

        DashboardData(int userCount, int courseCount, int noticeCount, int urgentCount, List<Notice> recentNotices) {
            this.userCount = userCount;
            this.courseCount = courseCount;
            this.noticeCount = noticeCount;
            this.urgentCount = urgentCount;
            this.recentNotices = recentNotices;
        }
    }

    /** Runs off the Event Dispatch Thread. Reads straight from the database via the controller. */
    private DashboardData fetchDashboardData() throws Exception {
        List<User> users = adminController.loadAllUsers();
        List<Course> courses = adminController.loadAllCourses();
        List<Notice> notices = adminController.loadAllNotices();

        if (users == null || courses == null || notices == null) {
            throw new IllegalStateException("The database returned no data. Check the connection and try again.");
        }

        int urgent = 0;
        for (Notice n : notices) {
            if (isUrgent(n)) urgent++;
        }

        // Newest first
        List<Notice> sorted = new ArrayList<>(notices);
        sorted.sort((a, b) -> Long.compare(toEpochMillis(b.getPostedDate()), toEpochMillis(a.getPostedDate())));
        List<Notice> recent = new ArrayList<>(sorted.subList(0, Math.min(sorted.size(), RECENT_NOTICE_LIMIT)));

        return new DashboardData(users.size(), courses.size(), notices.size(), urgent, recent);
    }

    private void refreshOverviewPanel() {
        if (overviewLoader != null && !overviewLoader.isDone()) {
            return; // a load is already running
        }
        if (!overviewLoadedOnce) {
            totalUsersCard.setValue(EM_DASH);
            coursesCard.setValue(EM_DASH);
            noticesCard.setValue(EM_DASH);
            urgentCard.setValue(EM_DASH);
            showNoticesMessage("Loading notices…");
        }
        lastUpdatedLabel.setText("Updating…");

        overviewLoader = new SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                return fetchDashboardData();
            }

            @Override
            protected void done() {
                try {
                    applyDashboardData(get());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    showLoadError("Loading was interrupted.");
                } catch (ExecutionException ex) {
                    Throwable cause = (ex.getCause() != null) ? ex.getCause() : ex;
                    String msg = (cause.getMessage() != null && !cause.getMessage().isEmpty())
                            ? cause.getMessage() : cause.getClass().getSimpleName();
                    showLoadError("Could not load data from the database: " + msg);
                }
            }
        };
        overviewLoader.execute();
    }

    private void applyDashboardData(DashboardData data) {
        overviewLoadedOnce = true;
        errorBanner.setVisible(false);

        totalUsersCard.setValue(String.valueOf(data.userCount));
        coursesCard.setValue(String.valueOf(data.courseCount));
        noticesCard.setValue(String.valueOf(data.noticeCount));
        urgentCard.setValue(String.valueOf(data.urgentCount));

        populateRecentNotices(data.recentNotices);
        lastUpdatedLabel.setText("Last Updated: " + LocalDateTime.now().format(UPDATED_FMT));
    }

    private void showLoadError(String message) {
        errorBannerLabel.setText(message);
        errorBannerLabel.setToolTipText(message);
        errorBanner.setVisible(true);
        if (!overviewLoadedOnce) {
            totalUsersCard.setValue(EM_DASH);
            coursesCard.setValue(EM_DASH);
            noticesCard.setValue(EM_DASH);
            urgentCard.setValue(EM_DASH);
            showNoticesMessage("Notices are unavailable.");
        }
        lastUpdatedLabel.setText("Update failed");
    }

    // ---------------------------------------------------------------------
    // Date helpers (work whether the model stores LocalDateTime, LocalDate or java.util.Date)
    // ---------------------------------------------------------------------

    private static long toEpochMillis(Object d) {
        if (d == null) return Long.MIN_VALUE;
        ZoneId zone = ZoneId.systemDefault();
        if (d instanceof LocalDateTime) return ((LocalDateTime) d).atZone(zone).toInstant().toEpochMilli();
        if (d instanceof LocalDate) return ((LocalDate) d).atStartOfDay(zone).toInstant().toEpochMilli();
        if (d instanceof java.util.Date) return ((java.util.Date) d).getTime();
        return Long.MIN_VALUE;
    }

    private static String formatDate(Object d) {
        if (d == null) return "";
        long ms = toEpochMillis(d);
        if (ms == Long.MIN_VALUE) return String.valueOf(d);
        return Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).format(NOTICE_DATE_FMT);
    }

    // =========================================================================
    // TIMETABLE PAGE
    // =========================================================================

    private JPanel createTimetablePanel() {
        ScrollablePanel panel = new ScrollablePanel(new BorderLayout(0, 16));
        panel.setOpaque(true);
        panel.setBackground(COLOR_PAGE_BG);
        panel.setBorder(new EmptyBorder(16, 24, 20, 24));

        JPanel headerPanel = new JPanel(new BorderLayout(12, 0));
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("Timetable Management");
        pageTitle.setFont(FONT_PAGE_TITLE);
        pageTitle.setForeground(new Color(27, 36, 59));

        EllipsisLabel pageSub = new EllipsisLabel("Create, review and maintain lecture and laboratory timetables.");
        pageSub.setFont(FONT_PAGE_SUB);
        pageSub.setForeground(COLOR_TEXT_MUTED);

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        BurgundyButton createTtBtn = new BurgundyButton("+ Create timetable");
        createTtBtn.setPreferredSize(new Dimension(170, 38));
        createTtBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Timetable schedule builder initiated. Select department & semester to manage slots.",
                "Timetable Builder",
                JOptionPane.INFORMATION_MESSAGE));

        JPanel btnWrap = new JPanel(new GridBagLayout());
        btnWrap.setOpaque(false);
        btnWrap.add(createTtBtn);

        headerPanel.add(titleBox, BorderLayout.CENTER);
        headerPanel.add(btnWrap, BorderLayout.EAST);

        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel cardTitle = new JLabel("Department Schedules & Laboratory Allocation");
        cardTitle.setFont(FONT_SECTION_TITLE);
        cardTitle.setForeground(COLOR_TEXT_DARK);

        JPanel schedulesGrid = new JPanel(new GridLayout(0, 1, 0, 14));
        schedulesGrid.setOpaque(false);
        schedulesGrid.add(createTimetableItemRow("Department of Information and Communication Technology", "Level II Semester I & II · Theory & Practical Schedules"));
        schedulesGrid.add(createTimetableItemRow("Department of Biosystems Technology", "Level II Semester I & II · Agriculture & Bioprocess Labs"));
        schedulesGrid.add(createTimetableItemRow("Department of Engineering Technology", "Level II Semester I & II · Mechanics, Electronics & Workshop"));

        JPanel gridTop = new JPanel(new BorderLayout());
        gridTop.setOpaque(false);
        gridTop.add(schedulesGrid, BorderLayout.NORTH);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(gridTop, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTimetableItemRow(String deptName, String scheduleDetails) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)));

        JPanel leftBox = new JPanel();
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
        leftBox.setOpaque(false);

        EllipsisLabel deptLbl = new EllipsisLabel(deptName);
        deptLbl.setFont(FONT_BODY_BOLD);
        deptLbl.setForeground(COLOR_TEXT_DARK);
        deptLbl.setToolTipText(deptName);

        EllipsisLabel subLbl = new EllipsisLabel(scheduleDetails);
        subLbl.setFont(FONT_SMALL);
        subLbl.setForeground(COLOR_TEXT_MUTED);
        subLbl.setToolTipText(scheduleDetails);

        leftBox.add(deptLbl);
        leftBox.add(Box.createVerticalStrut(2));
        leftBox.add(subLbl);

        WhiteOutlineButton viewBtn = new WhiteOutlineButton("View schedule");
        viewBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Displaying timetable allocation for: " + deptName,
                "Timetable Schedule",
                JOptionPane.INFORMATION_MESSAGE));
        JPanel btnWrap = new JPanel(new GridBagLayout());
        btnWrap.setOpaque(false);
        btnWrap.add(viewBtn);

        row.add(leftBox, BorderLayout.CENTER);
        row.add(btnWrap, BorderLayout.EAST);
        return row;
    }

    // =========================================================================
    // SIGN OUT
    // =========================================================================

    private void handleSignOut() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to sign out from the Administrator Portal?",
                "Sign Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            LoginController.showLogin();
        }
    }

    // =========================================================================
    // SMALL REUSABLE COMPONENTS
    // =========================================================================

    /** Page content that follows the viewport width (so it can reflow) and scrolls vertically. */
    private static class ScrollablePanel extends JPanel implements Scrollable {
        ScrollablePanel(LayoutManager layout) {
            super(layout);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return (orientation == SwingConstants.VERTICAL) ? Math.max(visibleRect.height - 32, 16) : visibleRect.width;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            Container parent = getParent();
            return parent instanceof JViewport && parent.getHeight() > getPreferredSize().height;
        }
    }

    /** A label that shrinks and shows "…" instead of forcing its parent wider. */
    private static class EllipsisLabel extends JLabel {
        EllipsisLabel(String text) {
            super(text);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(0, super.getPreferredSize().height);
        }
    }

    /** Dashboard statistic card: coloured badge, caption and a big number. No trend text. */
    private static class StatCard extends JPanel {
        private final Color accent;
        private final Color wave;
        private final JLabel valueLabel;

        StatCard(String title, Icon icon, Color accent, Color wave) {
            this.accent = accent;
            this.wave = wave;
            setOpaque(false);
            setLayout(new BorderLayout(16, 0));
            setBorder(new EmptyBorder(14, 22, 14, 16));
            setPreferredSize(new Dimension(200, 92));

            JPanel badge = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(StatCard.this.accent);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    if (icon != null) {
                        icon.paintIcon(this, g2, (getWidth() - icon.getIconWidth()) / 2, (getHeight() - icon.getIconHeight()) / 2);
                    }
                    g2.dispose();
                }
            };
            badge.setOpaque(false);
            badge.setPreferredSize(new Dimension(54, 54));
            JPanel badgeWrap = new JPanel(new GridBagLayout());
            badgeWrap.setOpaque(false);
            badgeWrap.add(badge);

            EllipsisLabel titleLabel = new EllipsisLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            titleLabel.setForeground(COLOR_TEXT_DARK);

            valueLabel = new JLabel(EM_DASH);
            valueLabel.setFont(new Font("Georgia", Font.BOLD, 30));
            valueLabel.setForeground(COLOR_TEXT_DARK);

            JPanel textBox = new JPanel();
            textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
            textBox.setOpaque(false);
            textBox.add(titleLabel);
            textBox.add(valueLabel);

            JPanel textWrap = new JPanel(new GridBagLayout());
            textWrap.setOpaque(false);
            GridBagConstraints gc = new GridBagConstraints();
            gc.weightx = 1;
            gc.fill = GridBagConstraints.HORIZONTAL;
            textWrap.add(textBox, gc);

            add(badgeWrap, BorderLayout.WEST);
            add(textWrap, BorderLayout.CENTER);
        }

        void setValue(String value) {
            valueLabel.setText(value);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Shape card = new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, 14, 14);
            g2.setColor(Color.WHITE);
            g2.fill(card);

            Shape oldClip = g2.getClip();
            g2.clip(card);

            Path2D.Double waveShape = new Path2D.Double();
            waveShape.moveTo(w * 0.45, h);
            waveShape.curveTo(w * 0.65, h * 0.55, w * 0.85, h * 1.0, w, h * 0.25);
            waveShape.lineTo(w, h);
            waveShape.closePath();
            g2.setColor(wave);
            g2.fill(waveShape);

            g2.setColor(accent);
            g2.fillRect(0, 0, 4, h);
            g2.setClip(oldClip);

            g2.setColor(COLOR_BORDER);
            g2.draw(card);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================================
    // NAVIGATION TAB BUTTON
    // =========================================================================

    private static class ModernNavTabButton extends JButton {
        private boolean active;
        private final String fullText;
        private final Icon activeIcon;
        private final Icon inactiveIcon;
        private final boolean showSeparator;

        ModernNavTabButton(String text, Icon activeIcon, Icon inactiveIcon, boolean active, boolean showSeparator) {
            super(text);
            this.fullText = text;
            this.activeIcon = activeIcon;
            this.inactiveIcon = inactiveIcon;
            this.active = active;
            this.showSeparator = showSeparator;
            setIcon(active ? activeIcon : inactiveIcon);
            setIconTextGap(8);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(active ? Color.WHITE : COLOR_NAV_TEXT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setRolloverEnabled(true);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(7, 14, 7, 14));
        }

        void setActive(boolean active) {
            this.active = active;
            setIcon(active ? activeIcon : inactiveIcon);
            setForeground(active ? Color.WHITE : COLOR_NAV_TEXT);
            repaint();
        }

        /** Icon-only mode for narrow windows; the full name moves into the tooltip. */
        void setCompact(boolean compact) {
            setText(compact ? "" : fullText);
            setToolTipText(compact ? fullText : null);
            setBorder(new EmptyBorder(7, compact ? 12 : 14, 7, compact ? 12 : 14));
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            if (active) {
                g2.setColor(COLOR_NAV_PILL);
                g2.fill(new RoundRectangle2D.Float(2, 4, w - 4, h - 8, h - 8, h - 8));
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(235, 227, 218));
                g2.fill(new RoundRectangle2D.Float(2, 4, w - 4, h - 8, h - 8, h - 8));
            }

            if (showSeparator && !active && !getText().isEmpty()) {
                g2.setColor(COLOR_NAV_SEP);
                g2.drawLine(w - 1, h / 2 - 8, w - 1, h / 2 + 8);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}