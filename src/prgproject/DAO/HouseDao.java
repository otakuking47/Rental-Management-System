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
import prgproject.model.House;
import prgproject.utils.DBConnection;

/**
 *
 * @author Collin
 */
public class HouseDao {
        public List<House> getAllHouses() {
        
        String sql = "SELECT * FROM houses";
        
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
            List<House> houseList = new ArrayList<>();
            
                while (rs.next()) {
                    House house = new House(
                            rs.getInt("ID"),
                            rs.getString("floor_size"),
                            rs.getString("full_address"),
                            rs.getString("location"),
                            rs.getDouble("market_value"),
                            rs.getDouble("rental_cost"),
                            rs.getBoolean("availability"),
                            rs.getDouble("Plot_size")
                    );

                    houseList.add(house);
                }
            
            
            return houseList;
            
        } catch (SQLException e) {
            throw new RuntimeException("Faild to load all from house due to ", e);
        }
    }

// saves this House to the database
    public int saveHouse(House house) {
        String sql = "INSERT INTO houses "
                + "(ID, floor_size, full_address, "
                + "location, market_value, rental_cost, availability, plot_size) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql);) {

            ps.setInt(1, house.getID());
            ps.setString(2, house.getFloorSize());
            ps.setString(3, house.getFullAddress());
            ps.setString(4, house.getLocation());
            ps.setDouble(5, house.getMarketValue());
            ps.setDouble(6, house.getRentalCost());
            ps.setBoolean(7, house.isAvailability());
            ps.setDouble(8, house.getPlotSize());
            
            return ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Faild to save house id: " + house.getID() + " due to " , e);
        }
    }
    
// updates a specific Townhouse
    public int updateHouse(House house) {
        String sql = "UPDATE houses "
                + "floor_size = ?, full_address = ?, "
                + "location = ?, market_value = ?, rental_cost = ?, availability = ?, plot_size = ? "
                + "WHERE ID = ? ";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql);) {

            ps.setString(1, house.getFloorSize());
            ps.setString(2, house.getFullAddress());
            ps.setString(3, house.getLocation());
            ps.setDouble(4, house.getMarketValue());
            ps.setDouble(5, house.getRentalCost());
            ps.setBoolean(6, house.isAvailability());
            ps.setDouble(7, house.getPlotSize());
            ps.setInt(8, house.getID());

            return ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Faild to update house: " + house.getID() + " due to " , e);
        }
    }
    
    //deletes a House
    public int deleteHouse(int id){
        String sql = "DELETE FROM houses WHERE ID =?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)){
            
            ps.setInt(1, id);
            
            return ps.executeUpdate();
            
        }catch( SQLException e){
                throw new RuntimeException("Faild to delete house: " + id + " due to " , e);
        }
    }
    
}
