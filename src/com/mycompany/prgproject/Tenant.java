package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TenantManage {


    private String firstName;
    private String lastName;
    private int credential;
    private int phoneNumber;
    private String email;
    private String status;
    private List<LeaseManage> leaseHistory;


    private static final String URL = "jdbc:mysql://localhost:3306/james_rental";
    private static final String USER = "root";
    private static final String PASS = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println(" MySQL Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println(" MySQL Driver not found!");
        }
    }

    public TenantManage(String firstName, String lastName, int credential,
                        int phoneNumber, String email, String status) {
        validateInput(firstName, lastName, email, status);
        this.firstName = firstName;
        this.lastName = lastName;
        this.credential = credential;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.status = status;
        this.leaseHistory = new ArrayList<>();
    }

    private void validateInput(String firstName, String lastName, String email, String status) {
        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name and last name cannot be empty.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        if (!"Active".equals(status) && !"Blacklisted".equals(status)) {
            throw new IllegalArgumentException("Status must be 'Active' or 'Blacklisted'.");
        }
    }

    // ==================== Getters & Setters ====================
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public int getCredential() { return credential; }
    public void setCredential(int credential) { this.credential = credential; }

    public int getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(int phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<LeaseManage> getLeaseHistory() {
        return new ArrayList<>(leaseHistory);
    }

    public void updateDetail(String firstName, String lastName, int phoneNumber,
                             String email, String status) {
        validateInput(firstName, lastName, email, status);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.status = status;
    }

    public void removeTenet() {
        System.out.println(" Tenant " + firstName + " " + lastName + " removed.");
        leaseHistory.clear();
    }

    public String getHistory() {
        if (leaseHistory.isEmpty()) return "No lease history available.";
        StringBuilder sb = new StringBuilder("=== Lease History for " + firstName + " " + lastName + " ===\n");
        for (LeaseManage lease : leaseHistory) {
            sb.append(lease.getDetail()).append("\n");
        }
        return sb.toString();
    }

    public void addLease(LeaseManage lease) {
        if (lease != null) leaseHistory.add(lease);
    }

    public void addTenant() {
        String sql = "INSERT INTO tenant (credential, firstName, lastName, phoneNumber, email, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, credential);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setInt(4, phoneNumber);
            ps.setString(5, email);
            ps.setString(6, status);

            ps.executeUpdate();
            System.out.println(" Tenant added successfully! ID: " + credential);

        } catch (SQLException e) {
            System.err.println(" Add failed: " + e.getMessage());
        }
    }

    public void updateTenant() {
        String sql = "UPDATE tenant SET firstName=?, lastName=?, phoneNumber=?, email=?, status=? "
                   + "WHERE credential=?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setInt(3, phoneNumber);
            ps.setString(4, email);
            ps.setString(5, status);
            ps.setInt(6, credential);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println(" Tenant updated successfully! ID: " + credential);
            } else {
                System.out.println(" No tenant found with ID: " + credential);
            }

        } catch (SQLException e) {
            System.err.println(" Update failed: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (ID: " + credential + ", Status: " + status + ")";
    }

    public static class LeaseManage {
        private int leaseID;
        private String startDate;
        private String endDate;
        private double rentAmount;

        public LeaseManage(int leaseID, String startDate, String endDate, double rentAmount) {
            this.leaseID = leaseID;
            this.startDate = startDate;
            this.endDate = endDate;
            this.rentAmount = rentAmount;
        }

        public String getDetail() {
            return "Lease #" + leaseID + " | " + startDate + " to " + endDate + " | Rent: N$" + rentAmount;
        }
    }


    public static void main(String[] args) {

        TenantManage t1 = new TenantManage("Seele", "Vol", 47973, 812345678, "seele@example.com", "Active");
        t1.addTenant();
        t1.updateTenant();
    }
}
