/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prgproject.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import prgproject.utils.Hasher;
import prgproject.utils.DBConnection;

public class AdminDao{

    public static boolean authenticate(String username, String password) {
        String passHash, storedPass = null;

        if (password.isEmpty() || username.isEmpty()) {
            JOptionPane.showMessageDialog(null, "missing values in field", "Missing", JOptionPane.WARNING_MESSAGE);
        } else {

            passHash = Hasher.hashPassword(password);

            String sql = "SELECT passwordHash FROM admin WHERE username = ?";

            try (Connection con = DBConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, username);

                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    storedPass = rs.getString("passwordHash");
                }

                return passHash.equals(storedPass);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return false;
    }

}
