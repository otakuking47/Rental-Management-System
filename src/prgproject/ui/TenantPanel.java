package prgproject.ui;

import prgproject.model.Tenant;
import prgproject.services.TenantService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tenant management panel.
 * UI layer only — calls TenantService methods exclusively.
 */
public class TenantPanel extends JPanel {

    private final TenantService tenantService = new TenantService();

    private JTable table;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField credentialField, firstNameField, lastNameField,
                       phoneField, emailField;
    private JComboBox<String> statusCombo;

    public TenantPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initTable();
        initForm();
        loadTable();
    }

    // ── Table ────────────────────────────────────────────────────────────────

    private void initTable() {
        String[] cols = {"ID/Credential", "First Name", "Last Name",
                         "Phone", "Email", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromSelection();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 240));
        add(scroll, BorderLayout.CENTER);
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            List<Tenant> tenants = tenantService.getAllTenants();
            for (Tenant t : tenants) {
                tableModel.addRow(new Object[]{
                    t.getCredential(), t.getFirstName(), t.getLastName(),
                    t.getPhoneNumber(), t.getEmail(), t.getStatus()
                });
            }
        } catch (RuntimeException e) {
            // No tenants yet — table stays empty
        }
    }

    private void populateFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        credentialField.setText(tableModel.getValueAt(row, 0).toString());
        firstNameField.setText(tableModel.getValueAt(row, 1).toString());
        lastNameField.setText(tableModel.getValueAt(row, 2).toString());
        phoneField.setText(tableModel.getValueAt(row, 3).toString());
        emailField.setText(tableModel.getValueAt(row, 4).toString());
        statusCombo.setSelectedItem(tableModel.getValueAt(row, 5).toString());
    }

    // ── Form ─────────────────────────────────────────────────────────────────

    private void initForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Tenant Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        credentialField = new JTextField(12);
        firstNameField  = new JTextField(12);
        lastNameField   = new JTextField(12);
        phoneField      = new JTextField(12);
        emailField      = new JTextField(16);
        statusCombo     = new JComboBox<>(new String[]{"Active", "Blacklisted"});

        addFormRow(formPanel, gbc, 0, "ID / Credential:", credentialField);
        addFormRow(formPanel, gbc, 1, "First Name:",      firstNameField);
        addFormRow(formPanel, gbc, 2, "Last Name:",       lastNameField);
        addFormRow(formPanel, gbc, 3, "Phone Number:",    phoneField);
        addFormRow(formPanel, gbc, 4, "Email:",           emailField);
        addFormRow(formPanel, gbc, 5, "Status:",          statusCombo);

        // ── Buttons ──────────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        btnPanel.add(makeButton("Add",    new Color(39, 174, 96),  e -> handleAdd()));
        btnPanel.add(makeButton("Update", new Color(52, 152, 219), e -> handleUpdate()));
        btnPanel.add(makeButton("Delete", new Color(231, 76, 60),  e -> handleDelete()));
        btnPanel.add(makeButton("Clear",  new Color(127, 140, 141),e -> clearForm()));
        btnPanel.add(makeButton("Refresh",new Color(243, 156, 18), e -> loadTable()));

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc,
                            int row, String label, JComponent field) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        p.add(field, gbc);
    }

    private JButton makeButton(String text, Color bg,
                               java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(action);
        return btn;
    }

    // ── Handlers ─────────────────────────────────────────────────────────────

    private void handleAdd() {
        try {
            Tenant t = buildTenantFromForm();
            tenantService.addTenant(t);
            JOptionPane.showMessageDialog(this, "Tenant added successfully.");
            clearForm();
            loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Please select a tenant to update.");
            return;
        }
        try {
            Tenant t = buildTenantFromForm();
            tenantService.updateTenant(t);
            JOptionPane.showMessageDialog(this, "Tenant updated successfully.");
            clearForm();
            loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Please select a tenant to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this tenant?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            int cred = Integer.parseInt(credentialField.getText().trim());
            tenantService.deleteTenant(cred);
            JOptionPane.showMessageDialog(this, "Tenant deleted.");
            clearForm();
            loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Tenant buildTenantFromForm() {
        int cred   = Integer.parseInt(credentialField.getText().trim());
        String fn  = firstNameField.getText().trim();
        String ln  = lastNameField.getText().trim();
        int phone  = Integer.parseInt(phoneField.getText().trim());
        String email = emailField.getText().trim();
        String status = statusCombo.getSelectedItem().toString();
        return new Tenant(fn, ln, cred, phone, email, status);
    }

    private void clearForm() {
        credentialField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        statusCombo.setSelectedIndex(0);
        table.clearSelection();
    }
}
