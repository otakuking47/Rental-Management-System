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
import javax.swing.JOptionPane;
import prgproject.model.TownHouse;
import prgproject.utils.DBConnection;

/**
 *
 * @author Collin
 */

// Loads a TownHouse from the database using its ID.
// Returns null if no matching record is found.
public class TownHouseDAO {

    public List<TownHouse> getAllTownHouses() {
        
        String sql = "SELECT * FROM townhouses";
        
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
            List<TownHouse> townHouseList = new ArrayList<>();
            if (!rs.next()) {
                return null; // No record found
            } else {
                while (rs.next()) {
                    TownHouse townhouse = new TownHouse(
                            rs.getInt("ID"),
                            rs.getString("floor_size"),
                            rs.getString("full_address"),
                            rs.getString("location"),
                            rs.getDouble("market_value"),
                            rs.getDouble("rental_cost"),
                            rs.getBoolean("availability"),
                            rs.getInt("unit_no"),
                            rs.getBoolean("backyard")
                    );

                    townHouseList.add(townhouse);
                }
            }
            
            
            return townHouseList;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

// saves this TownHouse to the database
    public void saveTownHouse(TownHouse t) {
        String sql = "INSERT INTO townhouses "
                + "(ID, unit_no, backyard, floor_size, full_address, "
                + "location, market_value, rental_cost, availability) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql);) {

            ps.setInt(1, t.getID());
            ps.setInt(2, t.getUnitNo());
            ps.setBoolean(3, t.isBackyard());

            ps.setString(4, t.getFloorSize());
            ps.setString(5, t.getFullAddress());
            ps.setString(6, t.getLocation());
            ps.setDouble(7, t.getMarketValue());
            ps.setDouble(8, t.getRentalCost());

            ps.setBoolean(9, t.isAvailability());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
    
// updates a specific Townhouse
    public void updateTownHouse(TownHouse t) {
        String sql = "UPDATE townhouses "
                + "unit_no = ?, backyard = ?, floor_size = ?, full_address = ?, "
                + "location = ?, market_value = ?, rental_cost = ?, availability = ? "
                + "WHERE ID = ? ";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql);) {

            ps.setInt(1, t.getUnitNo());
            ps.setBoolean(2, t.isBackyard());

            ps.setString(3, t.getFloorSize());
            ps.setString(4, t.getFullAddress());
            ps.setString(5, t.getLocation());
            ps.setDouble(6, t.getMarketValue());
            ps.setDouble(7, t.getRentalCost());
            ps.setBoolean(8, t.isAvailability());
            ps.setInt(9, t.getID());

            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //deletes a TownHouse
    public void deleteTownHouse(TownHouse t){
        String sql = "DELETE FROM townhouse WHERE ID =?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)){
            
            ps.setInt(1, t.getID());
            ps.executeUpdate();
            
        }catch( SQLException e){
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
