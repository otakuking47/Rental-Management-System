/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prgproject.Reports;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Excel Exporter (uses CSV format - Excel can open it)
 */
public class ExcelExporter implements ReportExporter {

    @Override
    public void export(String data) {
        String fileName = "Rental_Status_Report.xls";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(data);
            System.out.println(" Report exported successfully to: " + fileName);
        } catch (IOException e) {
            System.err.println(" Failed to export Excel: " + e.getMessage());
        }
    }
}
