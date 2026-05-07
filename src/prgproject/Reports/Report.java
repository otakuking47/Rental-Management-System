package prgproject.Reports;

import java.time.LocalDate;

/**
 * Lightweight helper for legacy callers — the live UI builds ReportData
 * directly in ReportPanel and exports through the ReportExporter strategy.
 */
public class Report {

    public String generateRentalReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("JAMES PROPERTY RENTAL MANAGEMENT SYSTEM\n");
        sb.append("========================================\n");
        sb.append("Report Generated: ").append(LocalDate.now()).append("\n");
        return sb.toString();
    }
}
