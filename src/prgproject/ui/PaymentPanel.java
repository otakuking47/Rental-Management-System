package prgproject.ui;

import prgproject.model.Payment;
import prgproject.services.PaymentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Payment management panel.
 * UI layer only — calls PaymentService methods exclusively.
 */
public class PaymentPanel extends JPanel {

    private final PaymentService paymentService = new PaymentService();

    private JTable table;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField receiptField, amountField, dateField, leaseIdField;
    private JComboBox<String> statusCombo;
    private JCheckBox partialBox;

    public PaymentPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initTable();
        initForm();
        loadTable();
    }

    // ── Table ────────────────────────────────────────────────────────────────

    private void initTable() {
        String[] cols = {"Receipt No.", "Partial", "Amount (N$)",
                         "Payment Date", "Status", "Lease ID"};
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
            List<Payment> payments = paymentService.getAllPayments();
            for (Payment p : payments) {
                tableModel.addRow(new Object[]{
                    p.getReceipt(), p.isPartial(), p.getAmount(),
                    p.getPaymentDate(), p.getStatus(), p.getLeaseID()
                });
            }
        } catch (RuntimeException ignored) {}
    }

    private void populateFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        receiptField.setText(tableModel.getValueAt(row, 0).toString());
        partialBox.setSelected(Boolean.parseBoolean(tableModel.getValueAt(row, 1).toString()));
        amountField.setText(tableModel.getValueAt(row, 2).toString());
        dateField.setText(tableModel.getValueAt(row, 3).toString());
        statusCombo.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        leaseIdField.setText(tableModel.getValueAt(row, 5).toString());
    }

    // ── Form ─────────────────────────────────────────────────────────────────

    private void initForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            "Payment Details  (Date format: YYYY-MM-DD)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        receiptField  = new JTextField(8);
        amountField   = new JTextField(10);
        dateField     = new JTextField(12);
        leaseIdField  = new JTextField(8);
        statusCombo   = new JComboBox<>(new String[]{"Pending", "Paid", "Late", "Partial"});
        partialBox    = new JCheckBox("Partial Payment");

        addFormRow(formPanel, gbc, 0, "Receipt No.:",   receiptField);
        addFormRow(formPanel, gbc, 1, "Amount (N$):",   amountField);
        addFormRow(formPanel, gbc, 2, "Payment Date:",  dateField);
        addFormRow(formPanel, gbc, 3, "Status:",        statusCombo);
        addFormRow(formPanel, gbc, 4, "Lease ID:",      leaseIdField);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(partialBox, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        btnPanel.add(makeButton("Record Payment", new Color(39, 174, 96),  e -> handleAdd()));
        btnPanel.add(makeButton("Update",         new Color(52, 152, 219), e -> handleUpdate()));
        btnPanel.add(makeButton("Delete",         new Color(231, 76, 60),  e -> handleDelete()));
        btnPanel.add(makeButton("Clear",          new Color(127,140,141),  e -> clearForm()));
        btnPanel.add(makeButton("Refresh",        new Color(243,156,18),   e -> loadTable()));

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc,
                            int row, String label, JComponent field) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        p.add(field, gbc);
    }

    // ── Handlers ─────────────────────────────────────────────────────────────

    private void handleAdd() {
        try {
            Payment pay = buildPaymentFromForm();
            paymentService.savePayment(pay);
            JOptionPane.showMessageDialog(this, "Payment recorded.");
            clearForm(); loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Select a payment to update.");
            return;
        }
        try {
            Payment pay = buildPaymentFromForm();
            paymentService.updatePayment(pay);
            JOptionPane.showMessageDialog(this, "Payment updated.");
            clearForm(); loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Select a payment to delete.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete this payment record?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            int receipt = Integer.parseInt(receiptField.getText().trim());
            paymentService.deletePayment(receipt);
            JOptionPane.showMessageDialog(this, "Payment deleted.");
            clearForm(); loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Payment buildPaymentFromForm() {
        return new Payment(
            Integer.parseInt(receiptField.getText().trim()),
            partialBox.isSelected(),
            Double.parseDouble(amountField.getText().trim()),
            dateField.getText().trim(),
            statusCombo.getSelectedItem().toString(),
            Integer.parseInt(leaseIdField.getText().trim())
        );
    }

    private void clearForm() {
        for (JTextField f : new JTextField[]{receiptField, amountField, dateField, leaseIdField})
            f.setText("");
        statusCombo.setSelectedIndex(0);
        partialBox.setSelected(false);
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
