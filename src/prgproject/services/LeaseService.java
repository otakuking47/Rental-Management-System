/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prgproject.services;

/**
 *
 * @author Collin
 */
public class LeaseService extends prgproject.DAO.LeaseDao{
    public static int validLease(double rentAmount, double securityDeposit, double latePenaltyRate, int gracePeriod, LocalDate startDate, LocalDate endDate)
            throws IllegalArgumentException {

        if (rentAmount <= 0) {
            JOptionPane.showMessageDialog(null, "Rent amount must be greater than 0", "ERROR", JOptionPane.ERROR_MESSAGE);

        }
        if (securityDeposit < 0) {
            JOptionPane.showMessageDialog(null, "Security deposit cannot be negative", "ERROR", JOptionPane.ERROR_MESSAGE);

        }
        if (latePenaltyRate < 0) {
            JOptionPane.showMessageDialog(null, "Late penalty rate cannot be negative", "ERROR", JOptionPane.ERROR_MESSAGE);

        }
        if (gracePeriod < 0) {
            JOptionPane.showMessageDialog(null, "Grace period can not be negative", "ERROR", JOptionPane.ERROR_MESSAGE);

        }
        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(null, "Start date and end date are required", "ERROR", JOptionPane.ERROR_MESSAGE);

        }
        if (!endDate.isAfter(startDate)) {
            JOptionPane.showMessageDialog(null, "End date musr be after start date", "ERROR", JOptionPane.ERROR_MESSAGE);

        }
    }
}
