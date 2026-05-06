package prgproject.ui;

import prgproject.model.Lease;
import prgproject.services.LeaseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Lease management panel.
 * UI layer only — calls LeaseService methods exclusively.
 */
public class LeasePanel extends JPanel {

    private final LeaseService leaseService = new LeaseService();

    private JTable table;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField leaseIdField, startDateField, endDateField,
                       rentAmountField, secDepositField, penaltyField, graceField;

    public LeasePanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initTable();
        initForm();
        loadTable();
    }

    // ── Table ────────────────────────────────────────────────────────────────

    private void initTable() {
        String[] cols = {"Lease ID", "Start Date", "End Date",
                         "Rent (N$)", "Security Deposit", "Penalty Rate (%)", "Grace (days)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(
            e -> { if (!e.getValueIsAdjusting()) populateFormFromSelection(); });
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 240));
        add(scroll, BorderLayout.CENTER);
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            List<Lease> leases = leaseService.getAllLeases();
            for (Lease l : leases) {
                tableModel.addRow(new Object[]{
                    l.getLeaseID(), l.getStartDate(), l.getEndDate(),
                    l.getRentAmount(), l.getSecurityDeposit(),
                    l.getLatePenaltyRate(), l.getGracePeriod()
                });
            }
        } catch (RuntimeException ignored) {}
    }

    private void populateFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        leaseIdField.setText(tableModel.getValueAt(row, 0).toString());
        startDateField.setText(tableModel.getValueAt(row, 1).toString());
        endDateField.setText(tableModel.getValueAt(row, 2).toString());
        rentAmountField.setText(tableModel.getValueAt(row, 3).toString());
        secDepositField.setText(tableModel.getValueAt(row, 4).toString());
        penaltyField.setText(tableModel.getValueAt(row, 5).toString());
        graceField.setText(tableModel.getValueAt(row, 6).toString());
    }

    // ── Form ─────────────────────────────────────────────────────────────────

    private void initForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            "Lease Details  (Dates: YYYY-MM-DD)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        leaseIdField    = new JTextField(8);
        startDateField  = new JTextField(12);
        endDateField    = new JTextField(12);
        rentAmountField = new JTextField(10);
        secDepositField = new JTextField(10);
        penaltyField    = new JTextField(8);
        graceField      = new JTextField(6);

        Object[][] rows = {
            {"Lease ID:",             leaseIdField},
            {"Start Date:",           startDateField},
            {"End Date:",             endDateField},
            {"Monthly Rent (N$):",    rentAmountField},
            {"Security Deposit (N$):",secDepositField},
            {"Late Penalty Rate (%):", penaltyField},
            {"Grace Period (days):",  graceField}
        };

        for (int i = 0; i < rows.length; i++) {
            gbc.gridx = (i % 2) * 2; gbc.gridy = i / 2;
            gbc.weightx = 0.2;
            formPanel.add(new JLabel((String) rows[i][0]), gbc);
            gbc.gridx++; gbc.weightx = 0.8;
            formPanel.add((JComponent) rows[i][1], gbc);
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        btnPanel.add(makeButton("Add",    new Color(39, 174, 96),  e -> handleAdd()));
        btnPanel.add(makeButton("Update", new Color(52, 152, 219), e -> handleUpdate()));
        btnPanel.add(makeButton("Delete", new Color(231, 76, 60),  e -> handleDelete()));
        btnPanel.add(makeButton("Clear",  new Color(127,140,141),  e -> clearForm()));
        btnPanel.add(makeButton("Refresh",new Color(243,156,18),   e -> loadTable()));

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);
    }

    // ── Handlers ─────────────────────────────────────────────────────────────

    private void handleAdd() {
        try {
            Lease l = buildLeaseFromForm();
            LeaseService.saveLease(l);   // static method on LeaseDao
            JOptionPane.showMessageDialog(this, "Lease saved.");
            clearForm(); loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Select a lease to update.");
            return;
        }
        try {
            Lease l = buildLeaseFromForm();
            leaseService.updateLease(l);
            JOptionPane.showMessageDialog(this, "Lease updated.");
            clearForm(); loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Select a lease to delete.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete this lease?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            int id = Integer.parseInt(leaseIdField.getText().trim());
            leaseService.deleteLease(id);
            JOptionPane.showMessageDialog(this, "Lease deleted.");
            clearForm(); loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Lease buildLeaseFromForm() {
        return new Lease(
            Integer.parseInt(leaseIdField.getText().trim()),
            LocalDate.parse(startDateField.getText().trim()),
            LocalDate.parse(endDateField.getText().trim()),
            Double.parseDouble(rentAmountField.getText().trim()),
            Double.parseDouble(secDepositField.getText().trim()),
            Double.parseDouble(penaltyField.getText().trim()),
            Integer.parseInt(graceField.getText().trim())
        );
    }

    private void clearForm() {
        for (JTextField f : new JTextField[]{leaseIdField, startDateField,
                endDateField, rentAmountField, secDepositField, penaltyField, graceField})
            f.setText("");
        table.clearSelection();
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
}
