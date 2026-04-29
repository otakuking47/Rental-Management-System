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
import javax.swing.JOptionPane;
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
            if (!rs.next()) {
                return null; // No record found
            } else {
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
            }

            return townHouseList;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    public void saveTenant(Tenant tenant) {
        String sql = "INSERT INTO tenants (credential, firstName, lastName, phoneNumber, email, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tenant.getCredential());
            ps.setString(2, tenant.getFirstName());
            ps.setString(3, tenant.getLastName());
            ps.setInt(4, tenant.getPhoneNumber());
            ps.setString(5, tenant.getEmail());
            ps.setString(6, tenant.getStatus());

            ps.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateTenant(Tenant tenant) {
        String sql = "UPDATE tenants SET firstName=?, lastName=?, phoneNumber=?, email=?, status=? "
                + "WHERE credential=?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tenant.getFirstName());
            ps.setString(2, tenant.getLastName());
            ps.setInt(3, tenant.getPhoneNumber());
            ps.setString(4, tenant.getEmail());
            ps.setString(5, tenant.getStatus());
            ps.setInt(6, tenant.getCredential());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, " Tenant updated successfully! ", "Success", JOptionPane.INFORMATION_MESSAGE); // this could be useful
            } else {
                JOptionPane.showMessageDialog(null, " No tenant found", "Fail to Update", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //deletes a Tenant
    public void deleteTenant(Tenant tenant) {
        String sql = "DELETE FROM tenants WHERE credential = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tenant.getCredential());
            ps.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
