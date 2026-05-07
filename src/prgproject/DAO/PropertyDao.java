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
import prgproject.model.Property;
import prgproject.utils.DBConnection;

/**
 *
 * @author Collin
 */
public class PropertyDao {
    public List<Property> getAllProperty() {

        String sql = "SELECT * FROM property";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
            List<Property> propertyList = new ArrayList<>();

            while (rs.next()) {
                Property property = new Property(
                        rs.getInt("propertyID"),
                        rs.getString("type"),
                        rs.getString("floor_size"),
                        rs.getString("full_address"),
                        rs.getString("location"),
                        rs.getDouble("market_value"),
                        rs.getDouble("rental_cost"),
                        rs.getBoolean("availability")
                );

                propertyList.add(property);
            }

            return propertyList;

        } catch (SQLException e) {
            throw new RuntimeException("Faild to load all from property due to ", e);
        }
    }

// saves this Property to the database
    public int saveProperty(Property property) {
        String sql = "INSERT INTO property "
                + "(propertyID, type, floor_size, full_address, "
                + "location, market_value, rental_cost, availability) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql);) {

            ps.setInt(1, property.getID());
            ps.setString(2, property.getType());
            ps.setString(3, property.getFloorSize());
            ps.setString(4, property.getFullAddress());
            ps.setString(5, property.getLocation());
            ps.setDouble(6, property.getMarketValue());
            ps.setDouble(7, property.getRentalCost());
            ps.setBoolean(8, property.isAvailability());
            

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Faild to save property id: " + property.getID() + " due to ", e);
        }
    }

// updates a specific property
    public int updateProperty(Property property) {
        String sql = "UPDATE property "
                + "SET type = ?, floor_size = ?, full_address = ?, "
                + "location = ?, market_value = ?, rental_cost = ?, availability = ?"
                + "WHERE propertyID = ? ";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql);) {
             
            ps.setString(1, property.getType());
            ps.setString(2, property.getFloorSize());
            ps.setString(3, property.getFullAddress());
            ps.setString(4, property.getLocation());
            ps.setDouble(5, property.getMarketValue());
            ps.setDouble(6, property.getRentalCost());
            ps.setBoolean(7, property.isAvailability());
            ps.setInt(8, property.getID());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Faild to update property: " + property.getID() + " due to ", e);
        }
    }

    //deletes a Property
    public int deleteProperty(int id) {
        String sql = "DELETE FROM property WHERE propertyID =?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Faild to delete property: " + id + " due to ", e);
        }
    }
    
    public Property getById(int id){
        String sql = "SELECT * FROM property WHERE propertyID = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql);) {
            Property property = null;
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                
                property = new Property(
                        rs.getInt("propertyID"),
                        rs.getString("type"),
                        rs.getString("floor_size"),
                        rs.getString("full_address"),
                        rs.getString("location"),
                        rs.getDouble("market_value"),
                        rs.getDouble("rental_cost"),
                        rs.getBoolean("availability")
                );

            }

            return property;

        } catch (SQLException e) {
            throw new RuntimeException("Faild to load all from property due to ", e);
        }
    }
}
