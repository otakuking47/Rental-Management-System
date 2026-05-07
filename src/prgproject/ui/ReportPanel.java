package prgproject.ui;

import prgproject.model.*;
import prgproject.services.*;
import prgproject.Reports.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Reports panel.
 * Fetches data via service classes and displays summary tables.
 * Export buttons call the Report layer (to be wired by Lee/Me when ready).
 * UI layer only — no SQL, no business logic.
 */
public class ReportPanel extends JPanel {

    private final TenantService tenantService     = new TenantService();
    private final HouseService houseService       = new HouseService();
    private final ApartmentService apartService   = new ApartmentService();
    private final TownHouseService townService    = new TownHouseService();
    private final LeaseService leaseService       = new LeaseService();
    private final PaymentService paymentService   = new PaymentService();

    private JTable reportTable;
    private DefaultTableModel reportModel;
    private JTextArea summaryArea;
    private String currentReportTitle = "Report";

    public ReportPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initToolbar();
        initContent();
    }

    // ── Toolbar ──────────────────────────────────────────────────────────────

    private void initToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        toolbar.setBorder(BorderFactory.createTitledBorder("Generate Report"));

        toolbar.add(makeButton("All Tenants",     new Color(52, 152, 219), e -> reportAllTenants()));
        toolbar.add(makeButton("All Properties",  new Color(39, 174, 96),  e -> reportAllProperties()));
        toolbar.add(makeButton("Active Leases",   new Color(142, 68, 173), e -> reportActiveLeases()));
        toolbar.add(makeButton("All Payments",    new Color(243, 156, 18), e -> reportAllPayments()));
        toolbar.add(makeButton("Summary",         new Color(30, 40, 55),   e -> reportSummary()));

        // Export buttons — call Report layer once implemented by Lee
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        exportPanel.add(new JLabel("Export:"));
        exportPanel.add(makeButton("CSV",   new Color(127,140,141), e -> exportCSV()));
        exportPanel.add(makeButton("PDF",   new Color(192, 57, 43), e -> exportPDF()));
        exportPanel.add(makeButton("Excel", new Color(39, 174, 96), e -> exportExcel()));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(toolbar, BorderLayout.WEST);
        topBar.add(exportPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);
    }

    // ── Content area ─────────────────────────────────────────────────────────

    private void initContent() {
        reportModel = new DefaultTableModel();
        reportTable = new JTable(reportModel);
        reportTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        summaryArea = new JTextArea(6, 40);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        summaryArea.setBorder(BorderFactory.createTitledBorder("Summary"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(reportTable),
            new JScrollPane(summaryArea));
        split.setResizeWeight(0.75);
        add(split, BorderLayout.CENTER);
    }

    // ── Report generators ────────────────────────────────────────────────────

    private void reportAllTenants() {
        currentReportTitle = "All Tenants";
        reportModel.setRowCount(0);
        reportModel.setColumnCount(0);
        reportModel.addColumn("ID"); reportModel.addColumn("First Name");
        reportModel.addColumn("Last Name"); reportModel.addColumn("Phone");
        reportModel.addColumn("Email"); reportModel.addColumn("Status");
        try {
            List<Tenant> tenants = tenantService.getAllTenants();
            for (Tenant t : tenants) {
                reportModel.addRow(new Object[]{
                    t.getCredential(), t.getFirstName(), t.getLastName(),
                    t.getPhoneNumber(), t.getEmail(), t.getStatus()
                });
            }
            summaryArea.setText("Total tenants: " + tenants.size());
        } catch (RuntimeException e) {
            summaryArea.setText("No tenant records found.");
        }
    }

    private void reportAllProperties() {
        currentReportTitle = "All Properties";
        reportModel.setRowCount(0);
        reportModel.setColumnCount(0);
        reportModel.addColumn("ID"); reportModel.addColumn("Type");
        reportModel.addColumn("Address"); reportModel.addColumn("Location");
        reportModel.addColumn("Rental (N$)"); reportModel.addColumn("Available");

        int total = 0;
        try {
            for (House h : houseService.getAllHouses()) {
                reportModel.addRow(new Object[]{
                    h.getID(), "House", h.getFullAddress(), h.getLocation(),
                    h.getRentalCost(), h.isAvailability()
                });
                total++;
            }
        } catch (RuntimeException ignored) {}
        try {
            for (Apartment a : apartService.getAllApartments()) {
                reportModel.addRow(new Object[]{
                    a.getID(), "Apartment", a.getFullAddress(), a.getLocation(),
                    a.getRentalCost(), a.isAvailability()
                });
                total++;
            }
        } catch (RuntimeException ignored) {}
        try {
            for (TownHouse t : townService.getAllTownHouses()) {
                reportModel.addRow(new Object[]{
                    t.getID(), "Townhouse", t.getFullAddress(), t.getLocation(),
                    t.getRentalCost(), t.isAvailability()
                });
                total++;
            }
        } catch (RuntimeException ignored) {}
        summaryArea.setText("Total properties: " + total);
    }

    private void reportActiveLeases() {
        currentReportTitle = "Active Leases";
        reportModel.setRowCount(0);
        reportModel.setColumnCount(0);
        reportModel.addColumn("Lease ID"); reportModel.addColumn("Start Date");
        reportModel.addColumn("End Date"); reportModel.addColumn("Rent (N$)");
        reportModel.addColumn("Security Dep."); reportModel.addColumn("Grace (days)");
        try {
            List<Lease> leases = leaseService.getAllLeases();
            int count = 0;
            for (Lease l : leases) {
                if (l.getEndDate().isAfter(java.time.LocalDate.now())) {
                    reportModel.addRow(new Object[]{
                        l.getLeaseID(), l.getStartDate(), l.getEndDate(),
                        l.getRentAmount(), l.getSecurityDeposit(), l.getGracePeriod()
                    });
                    count++;
                }
            }
            summaryArea.setText("Active leases: " + count + " / Total: " + leases.size());
        } catch (RuntimeException e) {
            summaryArea.setText("No lease records found.");
        }
    }

    private void reportAllPayments() {
        currentReportTitle = "All Payments";
        reportModel.setRowCount(0);
        reportModel.setColumnCount(0);
        reportModel.addColumn("Receipt"); reportModel.addColumn("Partial");
        reportModel.addColumn("Amount (N$)"); reportModel.addColumn("Date");
        reportModel.addColumn("Status"); reportModel.addColumn("Lease ID");
        try {
            List<Payment> payments = paymentService.getAllPayments();
            double total = 0;
            for (Payment p : payments) {
                reportModel.addRow(new Object[]{
                    p.getReceipt(), p.isPartial(), p.getAmount(),
                    p.getPaymentDate(), p.getStatus(), p.getLeaseID()
                });
                total += p.getAmount();
            }
            summaryArea.setText(String.format(
                "Total payments: %d | Total collected: N$ %.2f",
                payments.size(), total));
        } catch (RuntimeException e) {
            summaryArea.setText("No payment records found.");
        }
    }

    private void reportSummary() {
        currentReportTitle = "System Summary";
        reportModel.setRowCount(0);
        reportModel.setColumnCount(0);
        reportModel.addColumn("Category"); reportModel.addColumn("Count");

        StringBuilder sb = new StringBuilder("=== SYSTEM SUMMARY ===\n");

        int tenantCount = 0;
        try { tenantCount = tenantService.getAllTenants().size(); } catch (RuntimeException ignored) {}

        int houseCount = 0;
        try { houseCount = houseService.getAllHouses().size(); } catch (RuntimeException ignored) {}

        int aptCount = 0;
        try { aptCount = apartService.getAllApartments().size(); } catch (RuntimeException ignored) {}

        int townCount = 0;
        try { townCount = townService.getAllTownHouses().size(); } catch (RuntimeException ignored) {}

        int leaseCount = 0;
        try { leaseCount = leaseService.getAllLeases().size(); } catch (RuntimeException ignored) {}

        int payCount = 0;
        try { payCount = paymentService.getAllPayments().size(); } catch (RuntimeException ignored) {}

        reportModel.addRow(new Object[]{"Tenants", tenantCount});
        reportModel.addRow(new Object[]{"Houses", houseCount});
        reportModel.addRow(new Object[]{"Apartments", aptCount});
        reportModel.addRow(new Object[]{"Townhouses", townCount});
        reportModel.addRow(new Object[]{"Total Properties", houseCount + aptCount + townCount});
        reportModel.addRow(new Object[]{"Leases", leaseCount});
        reportModel.addRow(new Object[]{"Payments", payCount});

        sb.append("Tenants: ").append(tenantCount).append("\n");
        sb.append("Properties: ").append(houseCount + aptCount + townCount).append("\n");
        sb.append("Leases: ").append(leaseCount).append("\n");
        sb.append("Payments: ").append(payCount);
        summaryArea.setText(sb.toString());
    }

    // ── Export ───────────────────────────────────────────────────────────────

    private void exportCSV()   { runExport(new CSVExporter()); }
    private void exportPDF()   { runExport(new PDFExporter()); }
    private void exportExcel() { runExport(new ExcelExporter()); }

    private void runExport(ReportExporter exporter) {
        if (reportModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Generate a report first, then click an export button.",
                "Nothing to export", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ReportData data = snapshotCurrentReport();

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export as " + exporter.getFormatName());
        chooser.setSelectedFile(new File(suggestedFileName(data, exporter)));
        chooser.setFileFilter(new FileNameExtensionFilter(
            exporter.getFormatName() + " (*." + exporter.getDefaultExtension() + ")",
            exporter.getDefaultExtension()));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File target = chooser.getSelectedFile();
        if (!target.getName().toLowerCase().endsWith("." + exporter.getDefaultExtension())) {
            target = new File(target.getParentFile(), target.getName() + "." + exporter.getDefaultExtension());
        }

        try {
            exporter.export(data, target);
            JOptionPane.showMessageDialog(this,
                "Exported to:\n" + target.getAbsolutePath(),
                "Export complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Export failed: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ReportData snapshotCurrentReport() {
        List<String> cols = new ArrayList<>();
        for (int c = 0; c < reportModel.getColumnCount(); c++) {
            cols.add(reportModel.getColumnName(c));
        }
        List<List<Object>> rows = new ArrayList<>();
        for (int r = 0; r < reportModel.getRowCount(); r++) {
            List<Object> row = new ArrayList<>();
            for (int c = 0; c < reportModel.getColumnCount(); c++) {
                row.add(reportModel.getValueAt(r, c));
            }
            rows.add(row);
        }
        return new ReportData(currentReportTitle, cols, rows, summaryArea.getText());
    }

    private String suggestedFileName(ReportData data, ReportExporter exporter) {
        String safe = data.getTitle().toLowerCase()
            .replaceAll("[^a-z0-9]+", "_")
            .replaceAll("^_+|_+$", "");
        if (safe.isEmpty()) safe = "report";
        return safe + "_" + java.time.LocalDate.now() + "." + exporter.getDefaultExtension();
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
