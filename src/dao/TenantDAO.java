package dao;

import model.TenantManage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TenantDAO {
    
    private static final String URL = "jdbc:mysql://localhost:3306/james_rental";
    private static final String USER = "root";
    private static final String PASS = "";

    // THIS LINE FIXES THE "No suitable driver" ERROR
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found! Check JAR.");
            e.printStackTrace();
        }
    }

    public void addTenant(TenantManage tenant) {
        String sql = "INSERT INTO tenant VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tenant.getCredential());
            ps.setString(2, tenant.getFirstName());
            ps.setString(3, tenant.getLastName());
            ps.setInt(4, tenant.getPhoneNumber());
            ps.setString(5, tenant.getEmail());
            ps.setString(6, tenant.getStatus());
            ps.executeUpdate();
            System.out.println("✅ Tenant added to database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<TenantManage> getAllTenants() {
        List<TenantManage> list = new ArrayList<>();
        String sql = "SELECT * FROM tenant";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TenantManage t = new TenantManage(
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getInt("credential"),
                    rs.getInt("phoneNumber"),
                    rs.getString("email"),
                    rs.getString("status")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}