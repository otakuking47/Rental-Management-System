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

            return townHouseList;

        } catch (SQLException e) {
            throw new RuntimeException("Faild to fetch all records from townHouse due to: ", e);
        }
    }

// saves this TownHouse to the database
    public int saveTownHouse(TownHouse t) {
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
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Faild to save record to townHouse due to: ", e);
        }
    }

// updates a specific Townhouse
    public int updateTownHouse(TownHouse t) {
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

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Faild ro update record ID:" + t.getID() + " due to ", e);
        }
    }

    //deletes a TownHouse
    public int deleteTownHouse(int id) {
        String sql = "DELETE FROM townhouse WHERE ID =?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Faild to delete ID: " + id + " due to ", e);
        }
    }

}
