package com.mycompany.prgproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;


public class Lease{
     public static void validLease(double rentAmount, double securityDeposit,double latePenaltyRate, int gracePeriod,LocalDate startDate, LocalDate endDate) 
         throws IllegalArgumentException {
         
         if(rentAmount <= 0 ){
             throw new IllegalArgumentException("Rent amount must be greater than 0");
         }
         if(securityDeposit < 0){
             throw new IllegalArgumentException("Security deposit cannot be negative");
         }
         if(latePenaltyRate < 0){
             throw new IllegalArgumentException("Late penalty rate cannot be negative");
         }
         if(gracePeriod < 0){
             throw new IllegalArgumentException("Grace period can not be negative"); 
         }
         if(startDate == null || endDate ==null){
             throw new IllegalArgumentException("Start date and end date are required");
         }
         if(!endDate.isAfter(startDate)){
             throw new IllegalArgumentException("End date musr be after start date");
         }
     }
    public static void insertLease(Connection conn, LocalDate startDate, LocalDate endDate,double rentAmount, double securityDeposit,LocalDate dueDate, int gracePeriod, double latePenaltyRate) {
        try{
            validLease(rentAmount,securityDeposit,latePenaltyRate,gracePeriod,startDate,endDate); 
            String sql = "Insert into leaseManager";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            stmt.setDouble(3, rentAmount);
            stmt.setDouble(4,securityDeposit);
            stmt.setInt(6,gracePeriod);
            stmt.setDouble(7, latePenaltyRate);
            stmt.executeUpdate();
            
                System.out.println("Lease has been added successfully");
    }catch (IllegalArgumentException e){
        System.out.println("Input Error"+ e.getMessage());
    }catch (SQLException e){
        System.out.println("Database Error: Could not add lease. Please try again.");
    }
    }

    double getRentAmount() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    int getLeaseID() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    String getDueDate() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    int getLatePenaltyRate() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    String getGracePeriod() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}


