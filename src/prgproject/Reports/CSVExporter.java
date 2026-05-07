/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prgproject.Reports;

import java.io.FileWriter;
import java.io.IOException;

/**
 * CSV Exporter Implementation
 */
public class CSVExporter implements ReportExporter {

    @Override
    public void export(String data) {
        String fileName = "Rental_Status_Report.csv";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(data);
            System.out.println(" Report exported successfully to: " + fileName);
        } catch (IOException e) {
            System.err.println(" Failed to export CSV: " + e.getMessage());
        }
    }
}
