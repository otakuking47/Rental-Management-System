/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prgproject.Reports;

import java.io.FileWriter;
import java.io.IOException;

/**
 * PDF Exporter (Simplified version)
 */
public class PDFExporter implements ReportExporter {

    @Override
    public void export(String data) {
        String fileName = "Rental_Status_Report.pdf.txt";   // Can be upgraded to real PDF later

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("=================== RENTAL REPORT ===================\n\n");
            writer.write(data);
            writer.write("\n\n=================== END OF REPORT ===================");
            
            System.out.println(" Report exported successfully to: " + fileName);
        } catch (IOException e) {
            System.err.println(" Failed to export PDF: " + e.getMessage());
        }
    }
}
