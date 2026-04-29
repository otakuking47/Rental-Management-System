/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package prgproject.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import prgproject.model.Lease;
import prgproject.utils.DBConnection;

/**
 *
 * @author Collin
 */
// Im not touching this when there are town files WTF
public class LeaseDao {

    public static void validLease(double rentAmount, double securityDeposit, double latePenaltyRate, int gracePeriod, LocalDate startDate, LocalDate endDate)
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

    public List<Lease> getAllLeases() {

        String sql = "SELECT * FROM leases";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
            List<Lease> leaseList = new ArrayList<>();
            if (!rs.next()) {
                return null; // No record found
            } else {
                while (rs.next()) {
                    Lease lease = new Lease(
                            rs.getInt("leaseID"),
                            rs.getDate("startDate").toLocalDate(),
                            rs.getDate("endDate").toLocalDate(),
                            rs.getDouble("rentAmount"),
                            rs.getDouble("securityDeposit"),
                            rs.getDouble("latePenaltyRate"),
                            rs.getInt("gracePeriod")
                    );

                    leaseList.add(lease);
                }
            }
            rs.close();
            ps.close();
            con.close();

            return leaseList;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

// saves this Lease to the database
    public static void saveLease(Lease lease) {
        
        String sql = "Insert into leaseManager (leaseID, startDate, endDate, rentAmount, securityDeposit, latePenalityRate, gracePeriod) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try(Connection con = DBConnection.getConnection();PreparedStatement stmt = con.prepareStatement(sql);) {
            
            validLease(lease.getRentAmount(), lease.getSecurityDeposit(), lease.getLatePenaltyRate(),
                    lease.getGracePeriod(), lease.getStartDate(), lease.getEndDate());
            
            stmt.setInt(1, lease.getLeaseID());
            stmt.setDate(2, java.sql.Date.valueOf(lease.getStartDate()));
            stmt.setDate(3, java.sql.Date.valueOf(lease.getEndDate()));
            stmt.setDouble(4, lease.getRentAmount());
            stmt.setDouble(5, lease.getSecurityDeposit());
            stmt.setDouble(6, lease.getLatePenaltyRate());
            stmt.setInt(7, lease.getGracePeriod());
            
            stmt.executeUpdate();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

// updates a specific Lease
    public void updateLease(Lease lease) {
        String sql = "UPDATE leases "
                + "startDate = ?, endDate = ?, rentAmount = ?, securityDeposit = ?, latePenalityRate = ?, gracePeriod = ?"
                + "WHERE leaseID = ? ";

        try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(sql);) {

            stmt.setInt(1, lease.getLeaseID());
            stmt.setDate(2, java.sql.Date.valueOf(lease.getStartDate()));
            stmt.setDate(3, java.sql.Date.valueOf(lease.getEndDate()));
            stmt.setDouble(4, lease.getRentAmount());
            stmt.setDouble(5, lease.getSecurityDeposit());
            stmt.setDouble(6, lease.getLatePenaltyRate());
            stmt.setInt(7, lease.getGracePeriod());

            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    //deletes a Lease
    public void deleteLease(Lease lease) {
        String sql = "DELETE FROM lease WHERE ID =?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, lease.getLeaseID());
            ps.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
