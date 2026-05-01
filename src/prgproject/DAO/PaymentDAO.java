/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prgproject.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import prgproject.model.Payment;
import prgproject.utils.DBConnection;

/**
 *
 * @author Collin
 */
public class PaymentDAO {

    public List<Payment> getAllPayments() {

        String sql = "SELECT * FROM payments";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
            List<Payment> paymentList = new ArrayList<>();
            
                while (rs.next()) {
                    Payment payment = new Payment(
                            rs.getInt("receipt"),
                         rs.getBoolean("is_partial"),
                            rs.getDouble("amount"), 
                            rs.getString("payment_date"), 
                            rs.getString("status"), 
                            rs.getInt("lease_id")
                    );

                    paymentList.add(payment);
                }
            
            return paymentList;

        } catch (SQLException e) {
            throw new RuntimeException("Faild to load all payments due to ", e);
        }
        
    }

// saves this Payment to the database
    public int savePayment(Payment payment) {
        
        String sql = "INSERT INTO payments (receipt, is_partial, amount, payment_date, status, lease_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, payment.getReceipt());
            ps.setBoolean(2, payment.isPartial());
            ps.setDouble(3, payment.getAmount());
            ps.setString(4, payment.getPaymentDate());
            ps.setString(5, payment.getStatus());
            ps.setInt(6, payment.getLeaseID());

            return ps.executeUpdate();
            

        } catch (SQLException e) {
            throw new RuntimeException("Faild to save payment: " + payment.getReceipt() + " due to ", e);
        }
    }

// updates a specific Payment
    public int updatePayment(Payment payment) {
        String sql = "UPDATE payments "
                + " isPartial = ?, amount = ?, "
                + " paymentDate = ?, status = ?, leaseID = ?"
                + "WHERE receipt = ? ";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql);) {
            
            ps.setBoolean(1, payment.isPartial());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getPaymentDate());
            ps.setString(4, payment.getStatus());
            ps.setInt(5, payment.getLeaseID());
            ps.setInt(6, payment.getReceipt());
            
            return ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Faild to update payment:" + payment.getReceipt() + " due to ", e);
        }
    }

    //deletes a Payment
    public int deletePayment(int receipt) {
        String sql = "DELETE FROM payments WHERE receipt =?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, receipt);
            
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Faild to delete payment: " + receipt + " due to ", e);
        }
    }

}
