package com.mycompany.prgproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class Apartment{
      public static void validApartment(int unitNo,int floorLevel)
            throws IllegalArgumentException{
        if(unitNo <=0){
            throw new IllegalArgumentException("Please enter a postive number");
        }
        if (floorLevel < 1){
            throw new IllegalArgumentException("Floor level must be at least 1");
        }
    }
    public static void insertApartment(Connection conn,int unitNo,int floorLevel,boolean elevator,boolean backyard){
        try {
            validApartment(unitNo,floorLevel);
            String sql = "Insert into Apartment";
             PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,unitNo);
            stmt.setInt(2,floorLevel);
            stmt.setBoolean(3,elevator);
            stmt.setBoolean(4,backyard);
            stmt.executeUpdate();
            
            System.out.println("Apartment has been added successfully");
        } catch (IllegalArgumentException e){
            System.out.println("Input Error:"+ e.getMessage());
        } catch (SQLIntegrityConstraintViolationException e){
            System.out.println("Error Unit number" + unitNo + "already exists");
        } catch (SQLException e){
            System.out.println("Database Error: Could not add apartment.Please try again.");
        } 
        
    }
}