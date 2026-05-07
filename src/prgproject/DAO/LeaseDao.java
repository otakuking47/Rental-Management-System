package prgproject.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import prgproject.model.Lease;
import prgproject.utils.DBConnection;

// Im not touching this when there are town files WTF
public class LeaseDao {

    public List<Lease> getAllLeases() {

        String sql = "SELECT * FROM leaseTable";

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
            throw new RuntimeException("Faild to load all record due to ", e);
        }
    }

// saves this Lease to the database
    public int saveLease(Lease lease) {

        String sql = "Insert into leaseTable (leaseID, tenantID, propertyID, start_date, end_date, due_date, rent_amount, security_deposit, late_penalty_rate, grace_period, is_active) "
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
        String sql = "SELECT * FROM leaseTable WHERE propertyID = ?";

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
            throw new RuntimeException("Faild to load all record due to ", e);
        }
    }

// updates a specific Lease
    public int updateLease(Lease lease) {
        String sql = "UPDATE leaseTable "
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
            throw new RuntimeException("Faild to update record leaseID: " + lease.getLeaseID() + " due to ", e);
        }
    }

    //deletes a Lease
    public int deleteLease(int id) {
        String sql = "DELETE FROM lease WHERE leaseID =?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Faild delete lease leaseID: " + id + " due to ", e);
        }
    }

    public Lease getLeaseById(int id) {
        String sql = "SELECT * FROM leaseTable WHERE leaseID = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ) {
            
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
        String sql = "SELECT is_active FROM leaseTable WHERE propertyID = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql);) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                return rs.getBoolean("is_active");
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Faild to load data due to ", e);
        }
    }

    public static void main(String[] args) {

        LeaseDao dao = new LeaseDao();

        // =========================
        // CREATE TEST
        // =========================
        Lease newLease = new Lease(
                1, // leaseID
                101, // tenantID
                201, // propertyID
                LocalDate.of(2026, 1, 1), // startDate
                LocalDate.of(2026, 12, 31), // endDate
                LocalDate.of(2026, 1, 5), // dueDate
                8500.00, // rentAmount
                8500.00, // securityDeposit
                5.0, // late_penalty_rate
                7, // gracePeriod
                true // isActive
        );

        try {

            int rowsInserted = dao.saveLease(newLease);
            System.out.println("Inserted Rows: " + rowsInserted);

            // =========================
            // READ ALL TEST
            // =========================
            List<Lease> leaseTable = dao.getAllLeases();

            System.out.println("\n=== ALL LEASES ===");

            for (Lease lease : leaseTable) {
                System.out.println(lease);
            }

            // =========================
            // GET BY PROPERTY TEST
            // =========================
            Lease propertyLease = dao.getLeaseByProperty(201);

            System.out.println("\n=== LEASE BY PROPERTY ===");
            System.out.println(propertyLease);

            // =========================
            // UPDATE TEST
            // =========================
            newLease.setRentAmount(9500.00);

            int rowsUpdated = dao.updateLease(newLease);

            System.out.println("\nUpdated Rows: " + rowsUpdated);

            // =========================
            // EXISTS TEST
            // =========================
            boolean active = dao.existsActiveLease(201);

            System.out.println("\nActive Lease Exists: " + active);

            // =========================
            // DELETE TEST
            // =========================
            int rowsDeleted = dao.deleteLease(1);

            System.out.println("\nDeleted Rows: " + rowsDeleted);

        } catch (Exception e) {

            System.out.println("Something detonated:");
            e.printStackTrace();
        }
    }

}
