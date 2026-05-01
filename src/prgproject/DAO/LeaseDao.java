/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package prgproject.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import prgproject.model.Lease;
import prgproject.utils.DBConnection;

/**
 *
 * @author Collin
 */
// Im not touching this when there are town files WTF
public class LeaseDao {

    public List<Lease> getAllLeases() {

        String sql = "SELECT * FROM leases";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
            List<Lease> leaseList = new ArrayList<>();
            
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
         

            return leaseList;

        } catch (SQLException e) {
            throw new RuntimeException("Faild to load all leases due to ", e);
        }
    }

// saves this Lease to the database
    public static int saveLease(Lease lease) {
        
        String sql = "Insert into leaseManager (leaseID, startDate, endDate, rentAmount, securityDeposit, latePenalityRate, gracePeriod) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try(Connection con = DBConnection.getConnection();PreparedStatement stmt = con.prepareStatement(sql);) {
            
            stmt.setInt(1, lease.getLeaseID());
            stmt.setDate(2, java.sql.Date.valueOf(lease.getStartDate()));
            stmt.setDate(3, java.sql.Date.valueOf(lease.getEndDate()));
            stmt.setDouble(4, lease.getRentAmount());
            stmt.setDouble(5, lease.getSecurityDeposit());
            stmt.setDouble(6, lease.getLatePenaltyRate());
            stmt.setInt(7, lease.getGracePeriod());
            
            return stmt.executeUpdate();

        
        } catch (SQLException e) {
            throw new RuntimeException("Faild save to lease due to ", e);
        }
    }

// updates a specific Lease
    public int updateLease(Lease lease) {
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

            return stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Faild to update record ID: " + lease.getLeaseID() + " due to ", e);
        }
    }

    //deletes a Lease
    public int deleteLease(int id) {
        String sql = "DELETE FROM lease WHERE ID =?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Faild delete lease ID: " + id + " due to ", e);
        }
    }

}
