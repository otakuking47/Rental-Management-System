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

// Im not touching this when there are town files WTF
public class LeaseDao {

    public List<Lease> getAllLeases() {

        String sql = "SELECT * FROM leases";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
            List<Lease> leaseList = new ArrayList<>();

            while (rs.next()) {
                Lease lease = new Lease(
                        rs.getInt("leaseID"),
                        rs.getInt("tenantID"),
                        rs.getInt("propertyID"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getDouble("rent_amount"),
                        rs.getDouble("security_deposit"),
                        rs.getDouble("late_penalty_rate"),
                        rs.getInt("grace_period"),
                        rs.getBoolean("is_active")
                );

                leaseList.add(lease);
            }

            return leaseList;

        } catch (SQLException e) {
            throw new RuntimeException("Faild to load all leases due to ", e);
        }
    }

// saves this Lease to the database
    public int saveLease(Lease lease) {

        String sql = "INSERT INTO leases (leaseID, tenantID, propertyID, start_date, end_date, due_date, rent_amount, security_deposit, late_penalty_rate, grace_period, is_active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(sql);) {

            stmt.setInt(1, lease.getLeaseID());
            stmt.setInt(2, lease.getTenantID());
            stmt.setInt(3, lease.getPropertyID());
            stmt.setDate(4, java.sql.Date.valueOf(lease.getStartDate()));
            stmt.setDate(5, java.sql.Date.valueOf(lease.getEndDate()));
            stmt.setDate(6, java.sql.Date.valueOf(lease.getDueDate()));
            stmt.setDouble(7, lease.getRentAmount());
            stmt.setDouble(8, lease.getSecurityDeposit());
            stmt.setDouble(9, lease.getLatePenaltyRate());
            stmt.setInt(10, lease.getGracePeriod());
            stmt.setBoolean(11, lease.isIsActive());

            return stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Faild save to lease due to ", e);
        }
    }

    public Lease getLeaseByProperty(int propertyId) {
        String sql = "SELECT * FROM leases WHERE propertyID = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql);) {

            ps.setInt(1, propertyId);
            ResultSet rs = ps.executeQuery();
            Lease lease = null;

            while (rs.next()) {

                lease = new Lease(
                        rs.getInt("leaseID"),
                        rs.getInt("tenantID"),
                        rs.getInt("propertyID"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getDouble("rent_amount"),
                        rs.getDouble("security_deposit"),
                        rs.getDouble("late_penalty_rate"),
                        rs.getInt("grace_period"),
                        rs.getBoolean("is_active")
                );
            }

            return lease;

        } catch (SQLException e) {
            throw new RuntimeException("Faild to load all leases due to ", e);
        }
    }

// updates a specific Lease
    public int updateLease(Lease lease) {
        String sql = "UPDATE leases "
                + "SET tenantID = ?, propertyID = ?, start_date = ?, end_date = ?, due_date = ?, rent_amount = ?, security_deposit = ?, late_penalty_rate = ?, grace_period = ?, is_active = ? "
                + "WHERE leaseID = ? ";

        try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(sql);) {

            stmt.setInt(1, lease.getTenantID());
            stmt.setInt(2, lease.getPropertyID());
            stmt.setDate(3, java.sql.Date.valueOf(lease.getStartDate()));
            stmt.setDate(4, java.sql.Date.valueOf(lease.getEndDate()));
            stmt.setDate(5, java.sql.Date.valueOf(lease.getDueDate()));
            stmt.setDouble(6, lease.getRentAmount());
            stmt.setDouble(7, lease.getSecurityDeposit());
            stmt.setDouble(8, lease.getLatePenaltyRate());
            stmt.setInt(9, lease.getGracePeriod());
            stmt.setBoolean(10, lease.isIsActive());
            stmt.setInt(11, lease.getLeaseID());

            return stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Faild to update record ID: " + lease.getLeaseID() + " due to ", e);
        }
    }

    //deletes a Lease
    public int deleteLease(int id) {
        String sql = "DELETE FROM leases WHERE leaseID = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Faild delete lease ID: " + id + " due to ", e);
        }
    }

    public Lease getLeaseById(int id) {
        String sql = "SELECT * FROM leases WHERE leaseID = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            Lease lease = null;

            while (rs.next()) {
                lease = new Lease(
                        rs.getInt("leaseID"),
                        rs.getInt("tenantID"),
                        rs.getInt("propertyID"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getDouble("rent_amount"),
                        rs.getDouble("security_deposit"),
                        rs.getDouble("late_penalty_rate"),
                        rs.getInt("grace_period"),
                        rs.getBoolean("is_active")
                );
            }

            return lease;
        } catch (SQLException e) {
            throw new RuntimeException("Faild to load lease due to ", e);
        }
    }

    public boolean existsActiveLease(int id) {
        String sql = "SELECT is_active FROM leases WHERE propertyID = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql);) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                return rs.getBoolean("is_active");
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Faild to load all leases due to ", e);
        }
    }
}
