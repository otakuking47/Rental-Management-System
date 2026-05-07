/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package prgproject.Reports;

/**
 * ReportExporter Interface
 * Used for Polymorphism - allows different export formats
 */
public interface ReportExporter {

    /**
     * Export report data to specific format
     * @param data The formatted report content
     */
    void export(String data);
}
