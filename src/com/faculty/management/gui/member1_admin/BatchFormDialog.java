package com.faculty.management.gui.member1_admin;

import com.faculty.management.controller.AdminController;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Batch;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static com.faculty.management.gui.member1_admin.AdminUIComponents.*;

/**
 * Modern dialog for creating and modifying undergraduate batches.
 * Matches the design language of Member 1 Admin management forms.
 */
public class BatchFormDialog extends JDialog {

    public enum Mode { CREATE, UPDATE }

    private final Mode mode;
    private final Batch batch;
    private final AdminController adminController;
    private boolean saved = false;

    private JTextField batchNameField;
    private JTextField academicYearField;
    private JTextField descriptionField;
    private JComboBox<String> statusComboBox;
    private JLabel errorLabel;

    public BatchFormDialog(Window owner, Mode mode, Batch batch, AdminController adminController) {
        super(owner, mode == Mode.CREATE ? "Create Undergraduate Batch" : "Edit Undergraduate Batch", ModalityType.APPLICATION_MODAL);
        this.mode = mode;
        this.batch = batch;
        this.adminController = (adminController != null) ? adminController : new AdminController();

        initComponents();
        if (mode == Mode.UPDATE && batch != null) {
            populateFields(batch);
        }
    }

    private void initComponents() {
        setSize(480, 460);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BURGUNDY);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel(mode == Mode.CREATE ? "Add New Batch" : "Update Batch");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel(mode == Mode.CREATE ? "Create an undergraduate academic cohort for student enrollments." : "Modify batch details and intake status.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(230, 204, 210));

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.add(title);
        headerText.add(Box.createVerticalStrut(3));
        headerText.add(sub);
        header.add(headerText, BorderLayout.CENTER);

        // Form fields
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(20, 24, 16, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        // 1. Batch Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        form.add(createFieldLabel("Batch Name *"), gbc);
        batchNameField = createStyledTextField();
        batchNameField.setToolTipText("e.g. 2026/2027 or 2025/2026");
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.65;
        form.add(batchNameField, gbc);

        // 2. Academic Year
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        form.add(createFieldLabel("Academic Year *"), gbc);
        academicYearField = createStyledTextField();
        academicYearField.setToolTipText("e.g. 2026/2027");
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.65;
        form.add(academicYearField, gbc);

        // 3. Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35;
        form.add(createFieldLabel("Description"), gbc);
        descriptionField = createStyledTextField();
        descriptionField.setToolTipText("e.g. Faculty of Technology 8th Intake");
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.65;
        form.add(descriptionField, gbc);

        // 4. Status
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.35;
        form.add(createFieldLabel("Status *"), gbc);
        statusComboBox = new JComboBox<>(new String[]{"ACTIVE", "UPCOMING", "COMPLETED"});
        styleComboBox(statusComboBox);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.65;
        form.add(statusComboBox, gbc);

        // 5. Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(220, 38, 38));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        form.add(errorLabel, gbc);

        // Sync batchName with academicYear on type if academicYear is empty
        batchNameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { sync(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { sync(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { sync(); }
            private void sync() {
                if (mode == Mode.CREATE && (academicYearField.getText().trim().isEmpty() || academicYearField.getText().trim().equals(batchNameField.getText().trim().substring(0, Math.max(0, batchNameField.getText().trim().length() - 1))))) {
                    academicYearField.setText(batchNameField.getText().trim());
                }
            }
        });

        // Bottom actions
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        buttons.setBackground(new Color(248, 250, 252));
        buttons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER));

        WhiteOutlineButton cancelBtn = new WhiteOutlineButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(90, 34));
        cancelBtn.addActionListener(e -> dispose());

        BurgundyButton saveBtn = new BurgundyButton(mode == Mode.CREATE ? "Create Batch" : "Save Changes");
        saveBtn.setPreferredSize(new Dimension(130, 34));
        saveBtn.addActionListener(e -> handleSave());

        buttons.add(cancelBtn);
        buttons.add(saveBtn);

        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void populateFields(Batch b) {
        if (b.getBatchName() != null) batchNameField.setText(b.getBatchName());
        if (b.getAcademicYear() != null) academicYearField.setText(b.getAcademicYear());
        if (b.getDescription() != null) descriptionField.setText(b.getDescription());
        if (b.getStatus() != null) statusComboBox.setSelectedItem(b.getStatus().toUpperCase());
    }

    private void handleSave() {
        errorLabel.setText(" ");
        String name = batchNameField.getText().trim();
        String year = academicYearField.getText().trim();
        String desc = descriptionField.getText().trim();
        String status = (String) statusComboBox.getSelectedItem();

        if (name.isEmpty()) {
            errorLabel.setText("Batch Name is required.");
            batchNameField.requestFocus();
            return;
        }

        try {
            if (mode == Mode.CREATE) {
                adminController.handleCreateBatch(name, year, desc, status);
            } else {
                adminController.handleUpdateBatch(batch.getBatchId(), name, year, desc, status);
            }
            saved = true;
            dispose();
        } catch (ValidationException | DatabaseException ex) {
            errorLabel.setText(ex.getMessage());
        }
    }

    public boolean isSaved() {
        return saved;
    }

    private static JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SMALL_BOLD);
        l.setForeground(COLOR_TEXT_DARK);
        return l;
    }

    private static JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setPreferredSize(new Dimension(220, 32));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    private static void styleComboBox(JComboBox<?> box) {
        box.setFont(FONT_BODY);
        box.setPreferredSize(new Dimension(220, 32));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
    }
}
