/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package prgproject.Reports;

import java.time.LocalDate;

/**
 * Report Class - Generates report content
 */
public class Report {

    /**
     * Generates a sample rental report
     * You can expand this later to pull real data from DAO
     */
    public String generateRentalReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("JAMES PROPERTY RENTAL MANAGEMENT SYSTEM\n");
        sb.append("========================================\n");
        sb.append("Report Generated: ").append(LocalDate.now()).append("\n\n");
        sb.append("SUMMARY\n");
        sb.append("Total Properties: 52\n");
        sb.append("Active Tenants   : 45\n");
        sb.append("Overdue Payments : 8\n");
        sb.append("Total Revenue    : N$ 245,800\n\n");
        sb.append("DETAILED REPORT\n");
        sb.append("---------------------------------\n");
        // You can add more dynamic data here later
        return sb.toString();
    }

    /**
     * Export report using chosen exporter (Polymorphism)
     */
    public void exportReport(ReportExporter exporter) {
        String reportData = generateRentalReport();
        exporter.export(reportData);
    }
}
