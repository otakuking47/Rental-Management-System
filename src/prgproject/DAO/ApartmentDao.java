package prgproject.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import prgproject.model.Apartment;
import prgproject.utils.DBConnection;

public class ApartmentDao {

    public List<Apartment> getAllApartments() {

        String sql = "SELECT * FROM ApartmentTable";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Apartment> apartmentList = new ArrayList<>();

            while (rs.next()) {
                Apartment apartment = new Apartment(
                        rs.getInt("propertyID"),
                        rs.getString("floor_size"),
                        rs.getString("full_address"),
                        rs.getString("location"),
                        rs.getDouble("market_value"),
                        rs.getDouble("rental_cost"),
                        rs.getBoolean("availablity"),
                        rs.getInt("unit_no"),
                        rs.getInt("floor_level"),
                        rs.getBoolean("elevator"),
                        rs.getBoolean("backyard")
                );

                apartmentList.add(apartment);
            }

            return apartmentList;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load all ApartmentTable due to ", e);
        }
    }

    // saves this Apartment to the database
    public int saveApartment(Apartment apartment) {
        String sql = "INSERT INTO ApartmentTable "
                + "(propertyID, unit_no, backyard, floor_size, full_address, "
                + "location, market_value, rental_cost, availablity, floor_level, elevator) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, apartment.getID());
            ps.setInt(2, apartment.getUnitNo());
            ps.setBoolean(3, apartment.isHasBackyard());
            ps.setString(4, apartment.getFloorSize());
            ps.setString(5, apartment.getFullAddress());
            ps.setString(6, apartment.getLocation());
            ps.setDouble(7, apartment.getMarketValue());
            ps.setDouble(8, apartment.getRentalCost());
            ps.setBoolean(9, apartment.isAvailability());
            ps.setInt(10, apartment.getFloorLevel());
            ps.setBoolean(11, apartment.isHasElevator());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save record id: " + apartment.getID() + " due to ", e);
        }
    }

    // updates a specific apartment
    public int updateApartment(Apartment apartment) {
        
        String sql = "UPDATE ApartmentTable SET "
                + "unit_no = ?, backyard = ?, floor_size = ?, full_address = ?, "
                + "location = ?, market_value = ?, rental_cost = ?, availablity = ?, "
                + "floor_level = ?, elevator = ? "
                + "WHERE propertyID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, apartment.getUnitNo());
            ps.setBoolean(2, apartment.isHasBackyard());
            ps.setString(3, apartment.getFloorSize());
            ps.setString(4, apartment.getFullAddress());
            ps.setString(5, apartment.getLocation());
            ps.setDouble(6, apartment.getMarketValue());
            ps.setDouble(7, apartment.getRentalCost());
            ps.setBoolean(8, apartment.isAvailability());
            ps.setInt(9, apartment.getFloorLevel());
            ps.setBoolean(10, apartment.isHasElevator());
            ps.setInt(11, apartment.getID());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update record id: " + apartment.getID() + " due to ", e);
        }
    }

    // deletes an Apartment
    public int deleteApartment(int id) {
        String sql = "DELETE FROM ApartmentTable WHERE propertyID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete apartment: " + id + " due to ", e);
        }
    }

    public Apartment getById(int id) {
        
        String sql = "SELECT * FROM ApartmentTable WHERE propertyID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Apartment(
                            rs.getInt("propertyID"),
                            rs.getString("floor_size"),
                            rs.getString("full_address"),
                            rs.getString("location"),
                            rs.getDouble("market_value"),
                            rs.getDouble("rental_cost"),
                            rs.getBoolean("availablity"),
                            rs.getInt("unit_no"),
                            rs.getInt("floor_level"),
                            rs.getBoolean("elevator"),
                            rs.getBoolean("backyard")
                    );
                }
            }

             return null;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load apartment by id: " + id + " due to ", e);
        }
    }
}