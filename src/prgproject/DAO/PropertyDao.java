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

        String sql = "SELECT * FROM PropertyTable";

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
                        rs.getBoolean("availablity")
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
        String sql = "INSERT INTO PropertyTable "
                + "(propertyID, type, floor_size, full_address, "
                + "location, market_value, rental_cost, availablity) "
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
        String sql = "UPDATE PropertyTable "
                + "SET type = ?, floor_size = ?, full_address = ?, "
                + "location = ?, market_value = ?, rental_cost = ?, availablity = ? "
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
        String sql = "DELETE FROM PropertyTable WHERE propertyID =?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Faild to delete property: " + id + " due to ", e);
        }
    }
    
    public Property getById(int id){
        String sql = "SELECT * FROM PropertyTable WHERE propertyID = ?";

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
                        rs.getBoolean("availablity")
                );

            }

            return property;

        } catch (SQLException e) {
            throw new RuntimeException("Faild to load all from property due to ", e);
        }
    }
    
    public static void main(String[] args) {
 
        PropertyDao dao = new PropertyDao();
 
        // ── 1. INSERT ────────────────────────────────────────────────────────────
        // These IDs (1 and 2) match exactly the propertyIDs used in TestTownHouseDao.
        // PropertyTable rows MUST exist before TownHouseTable rows can be inserted
        // because TownHouse.propertyID is a FK that references PropertyTable.
        System.out.println("=== INSERT ===");
 
        Property p1 = new Property(
                1,                           // propertyID  (matches th1 in TownHouseDao test)
                "townhouse",                 // type
                "120m2",                     // floor_size
                "12 Acacia Ave, Windhoek",   // full_address
                "Windhoek North",            // location
                1_500_000.00,                // market_value
                8_500.00,                    // rental_cost
                true                         // availablity
        );
 
        Property p2 = new Property(
                2,                           // propertyID  (matches th2 in TownHouseDao test)
                "townhouse",
                "95m2",
                "7 Palm Street, Windhoek",
                "Pioneerspark",
                1_200_000.00,
                7_000.00,
                false
        );
 
        int rows1 = dao.saveProperty(p1);
        System.out.println("Inserted p1 (expect 1): " + rows1);
 
        int rows2 = dao.saveProperty(p2);
        System.out.println("Inserted p2 (expect 1): " + rows2);
 
        // ── 2. READ ALL ──────────────────────────────────────────────────────────
        System.out.println("\n=== GET ALL ===");
 
        List<Property> all = dao.getAllProperty();
        if (all.isEmpty()) {
            System.out.println("No properties found.");
        } else {
            for (Property p : all) {
                printProperty(p);
            }
        }
 
        // ── 3. READ BY ID ────────────────────────────────────────────────────────
        System.out.println("\n=== GET BY ID (propertyID = 1) ===");
 
        Property fetched = dao.getById(1);
        if (fetched != null) {
            printProperty(fetched);
        } else {
            System.out.println("No record found for propertyID = 1");
        }
 
        // ── 4. UPDATE ────────────────────────────────────────────────────────────
        // Keep values in sync with what TestTownHouseDao updates on th1
        System.out.println("\n=== UPDATE (propertyID = 1) ===");
 
        Property p1Updated = new Property(
                1,
                "townhouse",
                "130m2",                     // updated floor_size  (matches th1 update)
                "12 Acacia Ave, Windhoek",
                "Windhoek North",
                1_600_000.00,                // updated market_value
                9_000.00,                    // updated rental_cost
                false                        // no longer available
        );
 
        int updated = dao.updateProperty(p1Updated);
        System.out.println("Rows updated (expect 1): " + updated);
 
        Property afterUpdate = dao.getById(1);
        if (afterUpdate != null) {
            System.out.println("After update:");
            printProperty(afterUpdate);
        }
 
        // ── 5. DELETE ────────────────────────────────────────────────────────────
        // Delete propertyID = 2 AFTER the TownHouseDao test has already deleted
        // the matching townhouse child row — otherwise the FK will block this delete.
        // Run TestTownHouseDao first, then re-run this block, or remove the townhouse
        // row manually before deleting here.
        System.out.println("\n=== DELETE (propertyID = 2) ===");
 
        int deleted = dao.deleteProperty(2);
        System.out.println("Rows deleted (expect 1): " + deleted);
 
        Property afterDelete = dao.getById(2);
        if (afterDelete == null) {
            System.out.println("propertyID = 2 successfully deleted.");
        } else {
            System.out.println("ERROR: propertyID = 2 still exists.");
        }
 
        // ── 6. FINAL STATE ───────────────────────────────────────────────────────
        System.out.println("\n=== FINAL STATE (all records) ===");
 
        List<Property> finalList = dao.getAllProperty();
        if (finalList.isEmpty()) {
            System.out.println("Table is empty.");
        } else {
            for (Property p : finalList) {
                printProperty(p);
            }
        }
 
        System.out.println("\nAll tests completed.");
        System.out.println("You can now run TestTownHouseDao — parent rows are in place.");
    }
 
    // ── Helper ──────────────────────────────────────────────────────────────────
    private static void printProperty(Property p) {
        System.out.printf(
                "  ID=%-3d | Type=%-10s | Floor=%-6s | Address=%-30s | Location=%-20s | Market=%10.2f | Rent=%8.2f | Available=%b%n",
                p.getID(),
                p.getType(),
                p.getFloorSize(),
                p.getFullAddress(),
                p.getLocation(),
                p.getMarketValue(),
                p.getRentalCost(),
                p.isAvailability()
        );
    }
}
