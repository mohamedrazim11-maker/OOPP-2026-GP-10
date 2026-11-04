package com.faculty.management.gui.common;

import com.faculty.management.controller.LoginController;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.time.Year;
import java.util.Map;

/**
 * Modern split-screen login for the Faculty Management System (FMS).
 *
 * Layout
 *  - Left 54%  : full-bleed university lake photo (uni_image.jpeg) with burgundy
 *                scrims, crest, title, tagline and motto.
 *  - Right 46% : clean ivory canvas with a single centered sign-in form
 *                (role, username, password, Sign In, Reset), inline error banner
 *                and a security footer.
 *
 * Pure Java Swing / Java 2D. Colors come from {@link UITheme}.
 * The public API (constructors, show/clear error, resetFields, getters/setters)
 * is unchanged, so {@link LoginController} keeps working as before.
 */
public class LoginFrame extends JFrame {

    // --- Design tokens ------------------------------------------------------
    private static final String SANS  = "Segoe UI";
    private static final String SERIF = "Georgia";

    private static final Color IVORY      = new Color(251, 248, 243);
    private static final Color GOLD       = UITheme.GOLD_ACTIVE_BG;
    private static final Color GOLD_LIGHT = new Color(245, 215, 140);
    private static final Color GOLD_DEEP  = new Color(176, 120, 30);
    private static final Color LABEL      = new Color(58, 46, 50);

    private static final int FORM_W  = 380;
    private static final int FIELD_H = 50;
    private static final int RADIUS  = 14;

    // --- Components (kept for API compatibility) -----------------------------
    private JComboBox<String> roleComboBox;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;   // state holder only (eye icon is the visible control)
    private JButton eyeToggleButton;
    private JButton loginButton;
    private JButton resetButton;
    private JLabel errorLabel;
    private ErrorBanner errorBanner;

    private boolean isPasswordVisible = false;
    private final BufferedImage universityImage;
    private final LoginController loginController;

    // cached, pre-scaled hero image
    private BufferedImage heroCache;
    private int heroCacheW = -1, heroCacheH = -1;

    public LoginFrame() {
        this(new LoginController());
    }

    public LoginFrame(LoginController controller) {
        this.loginController = (controller != null) ? controller : new LoginController();
        this.universityImage = loadUniversityImage();
        initComponents();
    }

    // =========================================================================
    // IMAGE LOADING
    // =========================================================================

    /** Loads uni_image.jpeg from the working dir, common resource folders or the classpath. */
    private BufferedImage loadUniversityImage() {
        String[] paths = {
                "uni_image.jpeg",
                "resources/images/uni_image.jpeg",
                "resources/uni_image.jpeg",
                "src/main/resources/images/uni_image.jpeg",
                "src/resources/images/uni_image.jpeg"
        };
        for (String p : paths) {
            File f = new File(p);
            if (f.exists()) {
                try { return ImageIO.read(f); } catch (Exception ignored) { }
            }
        }
        try {
            URL url = getClass().getResource("/images/uni_image.jpeg");
            if (url == null) url = getClass().getResource("/uni_image.jpeg");
            if (url != null) return ImageIO.read(url);
        } catch (Exception ignored) { }
        return null;
    }

    /** Returns the photo scaled to "cover" w x h (high quality, cached between repaints). */
    private BufferedImage heroImage(int w, int h) {
        if (universityImage == null || w <= 0 || h <= 0) return null;
        if (heroCache != null && heroCacheW == w && heroCacheH == h) return heroCache;

        double scale = Math.max((double) w / universityImage.getWidth(),
                                (double) h / universityImage.getHeight());
        int sw = (int) Math.ceil(universityImage.getWidth() * scale);
        int sh = (int) Math.ceil(universityImage.getHeight() * scale);

        // Step-down by halves first: much smoother than one big resample
        BufferedImage src = universityImage;
        while (src.getWidth() / 2 >= sw && src.getHeight() / 2 >= sh) {
            src = halve(src);
        }

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int dx = (w - sw) / 2;
        int dy = (int) ((h - sh) * 0.35);          // keep more of the sunset sky than the bottom water
        g.drawImage(src, dx, dy, sw, sh, null);
        g.dispose();

        heroCache = out;
        heroCacheW = w;
        heroCacheH = h;
        return out;
    }

    private static BufferedImage halve(BufferedImage s) {
        int w = s.getWidth() / 2, h = s.getHeight() / 2;
        BufferedImage d = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = d.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(s, 0, 0, w, h, null);
        g.dispose();
        return d;
    }

    // =========================================================================
    // FRAME SETUP
    // =========================================================================

    private void initComponents() {
        setTitle("Faculty Management System — University of Ruhuna");
        setMinimumSize(new Dimension(980, 640));
        setSize(1120, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(createWindowIcon());

        JPanel root = new JPanel(new SplitLayout());
        root.setBackground(IVORY);
        root.add(new HeroPanel());
        root.add(buildFormSide());
        setContentPane(root);

        getRootPane().setDefaultButton(loginButton);   // Enter = Sign In
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { usernameField.requestFocusInWindow(); }
        });
    }

    /** 54 / 46 split that follows the window size. */
    private static class SplitLayout implements LayoutManager {
        @Override public void addLayoutComponent(String n, Component c) { }
        @Override public void removeLayoutComponent(Component c) { }
        @Override public Dimension preferredLayoutSize(Container p) { return new Dimension(1120, 700); }
        @Override public Dimension minimumLayoutSize(Container p) { return new Dimension(980, 640); }
        @Override public void layoutContainer(Container p) {
            int w = p.getWidth(), h = p.getHeight();
            int left = (int) Math.round(w * 0.54);
            p.getComponent(0).setBounds(0, 0, left, h);
            p.getComponent(1).setBounds(left, 0, w - left, h);
        }
    }

    // =========================================================================
    // LEFT SIDE — PHOTO HERO
    // =========================================================================

    private class HeroPanel extends JPanel {
        HeroPanel() { setOpaque(true); setBackground(UITheme.MAROON_SIDEBAR); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int w = getWidth(), h = getHeight();

            // 1. Photo (or a burgundy gradient if the file can't be found)
            BufferedImage img = heroImage(w, h);
            if (img != null) {
                g2.drawImage(img, 0, 0, null);
            } else {
                g2.setPaint(new GradientPaint(0, 0, UITheme.PRIMARY, w, h, UITheme.PRIMARY_DARK));
                g2.fillRect(0, 0, w, h);
            }

            // 2. Scrims: soft dark at top (brand legibility), deep burgundy at bottom (text)
            g2.setPaint(new GradientPaint(0, 0, new Color(20, 6, 10, 150), 0, 170, new Color(20, 6, 10, 0)));
            g2.fillRect(0, 0, w, 170);

            int by = (int) (h * 0.36);
            g2.setPaint(new LinearGradientPaint(0, by, 0, h,
                    new float[]{0f, 0.45f, 1f},
                    new Color[]{new Color(52, 8, 20, 0), new Color(52, 8, 20, 170), new Color(46, 6, 16, 245)}));
            g2.fillRect(0, by, w, h - by);

            // 3. Gold seam between photo and form
            g2.setPaint(new GradientPaint(0, 0, GOLD_LIGHT, 0, h, GOLD));
            g2.fillRect(w - 4, 0, 4, h);

            // 4. Brand text (top-left)
            g2.setColor(Color.WHITE);
            g2.setFont(tracked(new Font(SANS, Font.BOLD, 12), 0.16f));
            g2.drawString("UNIVERSITY OF RUHUNA", 52, 58);
            g2.setColor(new Color(255, 255, 255, 215));
            g2.setFont(new Font(SERIF, Font.ITALIC, 13));
            g2.drawString("Faculty of Technology", 52, 78);

            // 5. Headline block (bottom-left)
            int x = 52;
            int s = Math.max(130, h - 270);
            int textW = Math.min(430, w - x * 2);

            g2.setColor(GOLD);
            g2.fillRoundRect(x, s, 44, 4, 4, 4);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font(SERIF, Font.BOLD, 38));
            g2.drawString("Faculty Management", x, s + 48);
            g2.drawString("System", x, s + 94);

            g2.setColor(new Color(255, 255, 255, 220));
            g2.setFont(new Font(SANS, Font.PLAIN, 14));
            int after = drawWrapped(g2,
                    "One secure portal for administrators, lecturers, technical officers and undergraduates.",
                    x, s + 130, textW, 23);

            g2.setColor(new Color(255, 255, 255, 55));
            g2.fillRect(x, after + 6, w - x * 2, 1);

            g2.setColor(GOLD_LIGHT);
            g2.setFont(new Font(SERIF, Font.ITALIC, 14));
            g2.drawString("Learn  •  Innovate  •  Build the Future", x, after + 38);

            g2.dispose();
        }
    }

    // =========================================================================
    // RIGHT SIDE — FORM
    // =========================================================================

    /** Ivory canvas with a small burgundy/gold corner ribbon. */
    private static class FormSide extends JPanel {
        FormSide() { setOpaque(true); setBackground(IVORY); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();

            Path2D.Double gold = new Path2D.Double();
            gold.moveTo(w - 170, 0);
            gold.curveTo(w - 136, 50, w - 68, 106, w, 122);
            gold.lineTo(w, 100);
            gold.curveTo(w - 60, 90, w - 120, 40, w - 154, 0);
            gold.closePath();
            g2.setPaint(new GradientPaint(w - 170, 0, GOLD_LIGHT, w, 122, GOLD_DEEP));
            g2.fill(gold);

            Path2D.Double maroon = new Path2D.Double();
            maroon.moveTo(w - 154, 0);
            maroon.curveTo(w - 120, 40, w - 60, 90, w, 100);
            maroon.lineTo(w, 0);
            maroon.closePath();
            g2.setColor(UITheme.MAROON_SIDEBAR);
            g2.fill(maroon);

            g2.dispose();
        }
    }

    private JPanel buildFormSide() {
        FormSide side = new FormSide();
        side.setLayout(new BorderLayout());

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(buildCard());
        side.add(center, BorderLayout.CENTER);

        JLabel copy = new JLabel("© " + Year.now().getValue() + " Faculty of Technology · University of Ruhuna",
                SwingConstants.CENTER);
        copy.setFont(new Font(SANS, Font.PLAIN, 11));
        copy.setForeground(UITheme.TEXT_LIGHT);
        copy.setBorder(new EmptyBorder(0, 0, 18, 0));
        side.add(copy, BorderLayout.SOUTH);
        return side;
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Header
        JLabel eyebrow = new JLabel("WELCOME BACK");
        eyebrow.setFont(tracked(new Font(SANS, Font.BOLD, 12), 0.18f));
        eyebrow.setForeground(GOLD_DEEP);
        fixed(eyebrow, FORM_W, 16);

        JLabel title = new JLabel("Sign in");
        title.setFont(new Font(SERIF, Font.BOLD, 34));
        title.setForeground(UITheme.PRIMARY);
        fixed(title, FORM_W, 42);

        JLabel subtitle = new JLabel("Use your university credentials to continue.");
        subtitle.setFont(new Font(SANS, Font.PLAIN, 14));
        subtitle.setForeground(UITheme.TEXT_MUTED);
        fixed(subtitle, FORM_W, 22);

        // Error banner (reserved slot, so the form never jumps)
        errorBanner = new ErrorBanner();
        fixed(errorBanner, FORM_W, 40);

        // Fields
        JPanel roleGroup = fieldGroup("Role", buildRoleBox());
        JPanel userGroup = fieldGroup("Username", buildUsernameBox());
        JPanel passGroup = fieldGroup("Password", buildPasswordBox());

        // Hidden state holder kept for backward compatibility with getShowPasswordCheckBox()
        showPasswordCheckBox = new JCheckBox("Show Password");

        // Buttons
        loginButton = new FlatButton("Sign In", FlatButton.Kind.PRIMARY, new ArrowIcon(Color.WHITE), true);
        loginButton.setFont(new Font(SANS, Font.BOLD, 15));
        loginButton.setToolTipText("Sign in to the portal");
        loginButton.addActionListener(e -> performLogin());
        fixed(loginButton, FORM_W, 52);

        resetButton = new FlatButton("Reset form", FlatButton.Kind.GHOST, new ReloadIcon(UITheme.TEXT_MUTED), false);
        resetButton.setFont(new Font(SANS, Font.BOLD, 13));
        resetButton.setToolTipText("Clear all fields");
        resetButton.addActionListener(e -> resetFields());
        fixed(resetButton, FORM_W, 36);

        // Footer
        JLabel secure = new JLabel("Secure access for authorized users",
                new LockIcon(UITheme.TEXT_MUTED), SwingConstants.CENTER);
        secure.setIconTextGap(7);
        secure.setFont(new Font(SANS, Font.PLAIN, 12));
        secure.setForeground(UITheme.TEXT_MUTED);
        fixed(secure, FORM_W, 18);

        card.add(eyebrow);
        card.add(Box.createVerticalStrut(2));
        card.add(title);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(14));
        card.add(errorBanner);
        card.add(Box.createVerticalStrut(4));
        card.add(roleGroup);
        card.add(Box.createVerticalStrut(12));
        card.add(userGroup);
        card.add(Box.createVerticalStrut(12));
        card.add(passGroup);
        card.add(Box.createVerticalStrut(20));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(8));
        card.add(resetButton);
        card.add(Box.createVerticalStrut(14));
        card.add(secure);

        for (Component c : card.getComponents()) {
            if (c instanceof JComponent) ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        return card;
    }

    private JPanel fieldGroup(String labelText, FieldBox box) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font(SANS, Font.BOLD, 12));
        label.setForeground(LABEL);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 2, 0, 0));
        fixed(label, FORM_W, 16);

        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.add(label);
        group.add(Box.createVerticalStrut(6));
        group.add(box);
        fixed(group, FORM_W, 16 + 6 + FIELD_H);
        return group;
    }

    // --- Individual field builders -------------------------------------------

    private FieldBox buildRoleBox() {
        FieldBox box = new FieldBox();
        box.add(new JLabel(new BadgeIcon(UITheme.PRIMARY)), BorderLayout.WEST);

        roleComboBox = new JComboBox<>(new String[]{"Admin", "Lecturer", "Technical Officer", "Undergraduate"});
        roleComboBox.setUI(new BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                JButton b = new JButton() {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(UITheme.TEXT_MUTED);
                        g2.setStroke(new BasicStroke(1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        int cx = getWidth() / 2, cy = getHeight() / 2;
                        g2.drawLine(cx - 5, cy - 2, cx, cy + 3);
                        g2.drawLine(cx, cy + 3, cx + 5, cy - 2);
                        g2.dispose();
                    }
                };
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setContentAreaFilled(false);
                b.setFocusPainted(false);
                b.setOpaque(false);
                b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return b;
            }
            @Override public void paintCurrentValueBackground(Graphics g, Rectangle r, boolean focus) { }
            @Override protected ComboPopup createPopup() {
                BasicComboPopup p = (BasicComboPopup) super.createPopup();
                p.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_INPUT));
                return p;
            }
        });
        roleComboBox.setOpaque(false);
        roleComboBox.setBorder(BorderFactory.createEmptyBorder());
        roleComboBox.setFont(new Font(SANS, Font.PLAIN, 14));
        roleComboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        roleComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean selected, boolean focus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, selected, false);
                l.setFont(new Font(SANS, Font.PLAIN, 14));
                if (index < 0) {                       // the closed combo display
                    l.setOpaque(false);
                    l.setBorder(new EmptyBorder(0, 2, 0, 0));
                    l.setForeground(UITheme.TEXT_MAIN);
                } else {                               // popup rows
                    l.setOpaque(true);
                    l.setBorder(new EmptyBorder(9, 12, 9, 12));
                    l.setBackground(selected ? UITheme.PRIMARY : Color.WHITE);
                    l.setForeground(selected ? Color.WHITE : UITheme.TEXT_MAIN);
                }
                return l;
            }
        });
        box.add(roleComboBox, BorderLayout.CENTER);
        box.bind(roleComboBox);
        return box;
    }

    private FieldBox buildUsernameBox() {
        FieldBox box = new FieldBox();
        box.add(new JLabel(new UserIcon(UITheme.PRIMARY)), BorderLayout.WEST);

        usernameField = new HintTextField("Enter your username");
        styleTextField(usernameField);
        usernameField.addActionListener(e -> performLogin());
        box.add(usernameField, BorderLayout.CENTER);
        box.bind(usernameField);
        return box;
    }

    private FieldBox buildPasswordBox() {
        FieldBox box = new FieldBox();
        box.add(new JLabel(new LockIcon(UITheme.PRIMARY)), BorderLayout.WEST);

        passwordField = new HintPasswordField("Enter your password");
        styleTextField(passwordField);
        passwordField.setEchoChar('•');
        passwordField.addActionListener(e -> performLogin());
        box.add(passwordField, BorderLayout.CENTER);
        box.bind(passwordField);

        eyeToggleButton = new JButton(new EyeIcon(UITheme.TEXT_MUTED, false));
        eyeToggleButton.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 2));
        eyeToggleButton.setContentAreaFilled(false);
        eyeToggleButton.setFocusPainted(false);
        eyeToggleButton.setFocusable(false);
        eyeToggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeToggleButton.setToolTipText("Show / hide password");
        eyeToggleButton.addActionListener(e -> setPasswordVisibility(!isPasswordVisible));
        box.add(eyeToggleButton, BorderLayout.EAST);
        return box;
    }

    private static void styleTextField(JTextField f) {
        f.setFont(new Font(SANS, Font.PLAIN, 14));
        f.setForeground(UITheme.TEXT_MAIN);
        f.setCaretColor(UITheme.PRIMARY);
        f.setSelectionColor(new Color(110, 22, 36, 70));
        f.setOpaque(false);
        f.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
    }

    // =========================================================================
    // CUSTOM COMPONENTS
    // =========================================================================

    /** Rounded white input container with a soft focus ring. */
    private static class FieldBox extends JPanel {
        private boolean focused;

        FieldBox() {
            super(new BorderLayout(10, 0));
            setOpaque(false);
            setBorder(new EmptyBorder(0, 16, 0, 10));
            fixed(this, FORM_W, FIELD_H);
        }

        void bind(Component c) {
            c.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focused = true;  repaint(); }
                @Override public void focusLost(FocusEvent e)   { focused = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            int w = getWidth(), h = getHeight();
            RoundRectangle2D.Float rr = new RoundRectangle2D.Float(1.5f, 1.5f, w - 3, h - 3, RADIUS, RADIUS);

            if (focused) {                                     // soft glow
                g2.setColor(new Color(110, 22, 36, 55));
                g2.setStroke(new BasicStroke(3f));
                g2.draw(rr);
            }
            g2.setColor(Color.WHITE);
            g2.fill(rr);
            g2.setColor(focused ? UITheme.PRIMARY : UITheme.BORDER_INPUT);
            g2.setStroke(new BasicStroke(focused ? 1.6f : 1.2f));
            g2.draw(rr);
            g2.dispose();
        }
    }

    /** Text field that paints its placeholder instead of storing it as text. */
    private static class HintTextField extends JTextField {
        private final String hint;
        HintTextField(String hint) { this.hint = hint; }
        @Override protected void paintComponent(Graphics g) { super.paintComponent(g); paintHint(this, g, hint); }
    }

    private static class HintPasswordField extends JPasswordField {
        private final String hint;
        HintPasswordField(String hint) { this.hint = hint; }
        @Override protected void paintComponent(Graphics g) { super.paintComponent(g); paintHint(this, g, hint); }
    }

    private static void paintHint(JTextComponent c, Graphics g, String hint) {
        if (hint == null || c.getDocument().getLength() > 0) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(c.getFont());
        g2.setColor(UITheme.TEXT_LIGHT);
        FontMetrics fm = g2.getFontMetrics();
        Insets in = c.getInsets();
        g2.drawString(hint, in.left, (c.getHeight() - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }

    /** Inline red banner; invisible (but space-reserving) when there is no message. */
    private class ErrorBanner extends JPanel {
        private final JLabel alert = new JLabel(new AlertIcon(UITheme.DANGER));

        ErrorBanner() {
            super(new BorderLayout(10, 0));
            setOpaque(false);
            setBorder(new EmptyBorder(0, 14, 0, 14));
            errorLabel = new JLabel(" ");
            errorLabel.setFont(new Font(SANS, Font.PLAIN, 12));
            errorLabel.setForeground(UITheme.DANGER);
            alert.setVisible(false);
            add(alert, BorderLayout.WEST);
            add(errorLabel, BorderLayout.CENTER);
        }

        boolean hasMessage() { return !errorLabel.getText().trim().isEmpty(); }

        void sync() {
            alert.setVisible(hasMessage());
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (hasMessage()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D.Float rr = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(UITheme.DANGER_LIGHT);
                g2.fill(rr);
                g2.setColor(UITheme.DANGER_BORDER);
                g2.draw(rr);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /** Flat rounded button with animated hover (primary = filled burgundy, ghost = text button). */
    private static class FlatButton extends JButton {
        enum Kind { PRIMARY, GHOST }

        private final Kind kind;
        private final Icon icon;
        private final boolean iconTrailing;
        private float hover = 0f;
        private float target = 0f;
        private final javax.swing.Timer anim;

        FlatButton(String text, Kind kind, Icon icon, boolean iconTrailing) {
            super(text);
            this.kind = kind;
            this.icon = icon;
            this.iconTrailing = iconTrailing;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            anim = new javax.swing.Timer(16, e -> {
                hover += (target > hover) ? 0.14f : -0.14f;
                if ((target > hover && hover >= target) || (target < hover && hover <= target)
                        || Math.abs(hover - target) < 0.14f) {
                    hover = target;
                    ((javax.swing.Timer) e.getSource()).stop();
                }
                repaint();
            });
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { go(1f); }
                @Override public void mouseExited(MouseEvent e)  { go(0f); }
            });
        }

        private void go(float t) { target = t; if (!anim.isRunning()) anim.start(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            int w = getWidth(), h = getHeight();
            int faceH = h;
            Color fg;

            if (kind == Kind.PRIMARY) {
                faceH = h - 4;
                g2.setColor(new Color(110, 22, 36, (int) (46 + 20 * hover)));        // drop shadow
                g2.fill(new RoundRectangle2D.Float(5, 6, w - 10, faceH, RADIUS + 2, RADIUS + 2));

                Color base = getModel().isPressed() ? UITheme.PRIMARY_DARK
                        : lerp(UITheme.PRIMARY, UITheme.PRIMARY_HOVER, hover);
                g2.setPaint(new GradientPaint(0, 0, lerp(base, Color.WHITE, 0.10f), 0, faceH, base));
                g2.fill(new RoundRectangle2D.Float(0, 0, w, faceH, RADIUS, RADIUS));

                if (isFocusOwner()) {
                    g2.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 210));
                    g2.setStroke(new BasicStroke(2f));
                    g2.draw(new RoundRectangle2D.Float(2, 2, w - 4, faceH - 4, RADIUS - 2, RADIUS - 2));
                }
                fg = Color.WHITE;
            } else {
                g2.setColor(new Color(110, 22, 36, (int) (20 * hover)));
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, RADIUS - 2, RADIUS - 2));
                fg = lerp(UITheme.TEXT_MUTED, UITheme.PRIMARY, hover);
            }

            // Content: [icon] text [icon]
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(getText());
            int iw = icon != null ? icon.getIconWidth() : 0;
            int gap = icon != null ? 10 : 0;
            int x = (w - (tw + gap + iw)) / 2;
            int ty = (faceH - fm.getHeight()) / 2 + fm.getAscent();

            if (icon instanceof VIcon) ((VIcon) icon).color = fg;
            if (icon != null && !iconTrailing) {
                icon.paintIcon(this, g2, x, (faceH - icon.getIconHeight()) / 2);
                x += iw + gap;
            }
            g2.setColor(fg);
            g2.drawString(getText(), x, ty);
            if (icon != null && iconTrailing) {
                int nudge = Math.round(4 * hover);                               // arrow slides on hover
                icon.paintIcon(this, g2, x + tw + gap + nudge, (faceH - icon.getIconHeight()) / 2);
            }
            g2.dispose();
        }
    }

    // =========================================================================
    // VECTOR ICONS (color is set by the owner; all drawn in a size x size box)
    // =========================================================================

    private abstract static class VIcon implements Icon {
        Color color;
        private final int size;
        VIcon(Color color, int size) { this.color = color; this.size = size; }

        @Override public final void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            draw(g2);
            g2.dispose();
        }
        protected abstract void draw(Graphics2D g2);
        @Override public int getIconWidth()  { return size; }
        @Override public int getIconHeight() { return size; }
    }

    private static class UserIcon extends VIcon {
        UserIcon(Color c) { super(c, 20); }
        @Override protected void draw(Graphics2D g2) {
            g2.draw(new Ellipse2D.Float(6, 2.5f, 8, 8));
            Path2D.Float p = new Path2D.Float();
            p.moveTo(3, 18);
            p.curveTo(3, 13.5f, 6.5f, 12.5f, 10, 12.5f);
            p.curveTo(13.5f, 12.5f, 17, 13.5f, 17, 18);
            g2.draw(p);
        }
    }

    private static class BadgeIcon extends VIcon {
        BadgeIcon(Color c) { super(c, 20); }
        @Override protected void draw(Graphics2D g2) {
            g2.draw(new RoundRectangle2D.Float(2, 4, 16, 13, 4, 4));
            g2.draw(new Ellipse2D.Float(5.5f, 8, 4, 4));
            g2.drawLine(12, 9, 15, 9);
            g2.drawLine(12, 12, 15, 12);
            g2.drawLine(8, 2, 12, 2);
        }
    }

    private static class LockIcon extends VIcon {
        LockIcon(Color c) { super(c, 18); }
        @Override protected void draw(Graphics2D g2) {
            g2.draw(new RoundRectangle2D.Float(3, 8, 12, 8.5f, 3, 3));
            Path2D.Float s = new Path2D.Float();
            s.moveTo(5.5f, 8);
            s.lineTo(5.5f, 5.5f);
            s.curveTo(5.5f, 1.5f, 12.5f, 1.5f, 12.5f, 5.5f);
            s.lineTo(12.5f, 8);
            g2.draw(s);
            g2.fill(new Ellipse2D.Float(8, 10.8f, 2, 2));
        }
    }

    private static class EyeIcon extends VIcon {
        private final boolean visible;
        EyeIcon(Color c, boolean visible) { super(c, 20); this.visible = visible; }
        @Override protected void draw(Graphics2D g2) {
            Path2D.Float eye = new Path2D.Float();
            eye.moveTo(2, 10);
            eye.curveTo(5, 4.5f, 15, 4.5f, 18, 10);
            eye.curveTo(15, 15.5f, 5, 15.5f, 2, 10);
            eye.closePath();
            g2.draw(eye);
            g2.fill(new Ellipse2D.Float(7.5f, 7.5f, 5, 5));
            if (!visible) g2.drawLine(4, 3, 16, 17);
        }
    }

    private static class ArrowIcon extends VIcon {
        ArrowIcon(Color c) { super(c, 18); }
        @Override protected void draw(Graphics2D g2) {
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(3, 9, 14, 9);
            g2.drawLine(10, 4, 14, 9);
            g2.drawLine(10, 14, 14, 9);
        }
    }

    private static class ReloadIcon extends VIcon {
        ReloadIcon(Color c) { super(c, 16); }
        @Override protected void draw(Graphics2D g2) {
            g2.setStroke(new BasicStroke(1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Arc2D.Float(2, 2, 12, 12, 60, 280, Arc2D.OPEN));
            g2.drawLine(14, 10, 11, 13);       // arrow head at arc end
            g2.drawLine(14, 10, 16, 13);
        }
    }

    private static class AlertIcon extends VIcon {
        AlertIcon(Color c) { super(c, 16); }
        @Override protected void draw(Graphics2D g2) {
            g2.draw(new Ellipse2D.Float(1.5f, 1.5f, 13, 13));
            g2.drawLine(8, 5, 8, 8);
            g2.fill(new Ellipse2D.Float(7.1f, 10, 1.8f, 1.8f));
        }
    }

    // =========================================================================
    // SMALL HELPERS
    // =========================================================================

    private static <T extends JComponent> T fixed(T c, int w, int h) {
        Dimension d = new Dimension(w, h);
        c.setPreferredSize(d);
        c.setMinimumSize(d);
        c.setMaximumSize(d);
        return c;
    }

    private static Color lerp(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        return new Color(
                Math.round(a.getRed()   + (b.getRed()   - a.getRed())   * t),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                Math.round(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t));
    }

    private static Font tracked(Font f, float tracking) {
        return f.deriveFont(Map.of(TextAttribute.TRACKING, tracking));
    }

    /** Word-wraps text at maxW; returns the baseline after the last line. */
    private static int drawWrapped(Graphics2D g2, String text, int x, int y, int maxW, int lineH) {
        FontMetrics fm = g2.getFontMetrics();
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(test) > maxW && line.length() > 0) {
                g2.drawString(line.toString(), x, y);
                y += lineH;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0) {
            g2.drawString(line.toString(), x, y);
            y += lineH;
        }
        return y;
    }

    /** Academic crest: burgundy tile, gold hairline, gold colonnade. Drawn in a 32x32 space. */
    private static void drawCrest(Graphics2D g2, int x, int y, int size) {
        Graphics2D g = (Graphics2D) g2.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(x, y);
        g.scale(size / 32.0, size / 32.0);

        g.setColor(UITheme.MAROON_SIDEBAR);
        g.fillRoundRect(0, 0, 32, 32, 9, 9);
        g.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 140));
        g.setStroke(new BasicStroke(0.9f));
        g.drawRoundRect(1, 1, 30, 30, 8, 8);

        g.setColor(GOLD);
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(6, 12, 16, 6);
        g.drawLine(16, 6, 26, 12);
        g.drawLine(6, 12, 26, 12);
        g.drawLine(9, 14, 9, 23);
        g.drawLine(16, 14, 16, 23);
        g.drawLine(23, 14, 23, 23);
        g.drawLine(6, 25, 26, 25);
        g.dispose();
    }

    private Image createWindowIcon() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawCrest(g2, 0, 0, 64);
        g2.dispose();
        return img;
    }

    // =========================================================================
    // BEHAVIOUR
    // =========================================================================

    private void setPasswordVisibility(boolean visible) {
        this.isPasswordVisible = visible;
        passwordField.setEchoChar(visible ? (char) 0 : '•');
        showPasswordCheckBox.setSelected(visible);
        eyeToggleButton.setIcon(new EyeIcon(visible ? UITheme.PRIMARY : UITheme.TEXT_MUTED, visible));
    }

    private void performLogin() {
        clearErrorMessage();
        String role = (String) roleComboBox.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        loginController.handleLogin(this, username, password, role);
    }

    public void showErrorMessage(String message) {
        String safe = String.valueOf(message).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        errorLabel.setText("<html><div style='width:290px'>" + safe + "</div></html>");
        errorBanner.sync();
    }

    public void clearErrorMessage() {
        errorLabel.setText(" ");
        if (errorBanner != null) errorBanner.sync();
    }

    public void resetFields() {
        roleComboBox.setSelectedIndex(0);
        usernameField.setText("");
        passwordField.setText("");
        setPasswordVisibility(false);
        clearErrorMessage();
        usernameField.requestFocusInWindow();
    }

    // =========================================================================
    // ACCESSORS (unchanged public API)
    // =========================================================================

    public String getSelectedRole() { return (String) roleComboBox.getSelectedItem(); }
    public void setSelectedRole(String role) { roleComboBox.setSelectedItem(role); }

    public String getUsername() { return usernameField.getText().trim(); }
    public void setUsername(String username) { usernameField.setText(username); }
    public void setPassword(String password) { passwordField.setText(password); }

    public JComboBox<String> getRoleComboBox() { return roleComboBox; }
    public JTextField getUsernameField() { return usernameField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
    public JButton getResetButton() { return resetButton; }
    public JLabel getErrorLabel() { return errorLabel; }
    public JCheckBox getShowPasswordCheckBox() { return showPasswordCheckBox; }
}