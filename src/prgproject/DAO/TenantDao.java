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
import prgproject.model.Tenant;
import prgproject.utils.DBConnection;

/**
 *
 * @author Collin
 */
public class TenantDao {

    public List<Tenant> getAllTenants() {

        String sql = "SELECT * FROM townhouses";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
            List<Tenant> townHouseList = new ArrayList<>();
            
                while (rs.next()) {
                    Tenant tenant = new Tenant(
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getInt("credential"),
                            rs.getInt("phoneNumber"),
                            rs.getString("email"),
                            rs.getString("status")
                    );

                    townHouseList.add(tenant);
                }
       
            return townHouseList;

        } catch (SQLException e) {
            throw new RuntimeException("Faild to get all records from Tenants due to ", e);
        }
    }

    public int saveTenant(Tenant tenant) {
        String sql = "INSERT INTO tenants (credential, firstName, lastName, phoneNumber, email, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tenant.getCredential());
            ps.setString(2, tenant.getFirstName());
            ps.setString(3, tenant.getLastName());
            ps.setInt(4, tenant.getPhoneNumber());
            ps.setString(5, tenant.getEmail());
            ps.setString(6, tenant.getStatus());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Faild to save to Tenant of ID: " + tenant.getCredential() + "due to ", e);
        }
    }

    public int updateTenant(Tenant tenant) {
        String sql = "UPDATE tenants SET firstName=?, lastName=?, phoneNumber=?, email=?, status=? "
                + "WHERE credential=?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tenant.getFirstName());
            ps.setString(2, tenant.getLastName());
            ps.setInt(3, tenant.getPhoneNumber());
            ps.setString(4, tenant.getEmail());
            ps.setString(5, tenant.getStatus());
            ps.setInt(6, tenant.getCredential());

            return ps.executeUpdate();
            

        } catch (SQLException e) {
            throw new RuntimeException("Faild update record ID: " + tenant.getCredential() + " due to ", e);
        }
    }

    public int deleteTenant(int credential) {
        String sql = "DELETE FROM tenants WHERE credential = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, credential);
            
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Faild to delete ID: " + credential + " due to ", e);
        }
    }
}
