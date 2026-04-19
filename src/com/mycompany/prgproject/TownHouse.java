package propertymanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// A TownHouse is a Property with a unit number and optional backyard
public class TownHouse extends Property {

    private int unitNo;
    private boolean backyard;

    // Default constructor
    public TownHouse() {
        super();
    }

    // Full constructor
    public TownHouse(String floorSize, String fullAddress, String location,
                     double marketValue, double rentalCost, boolean availability,
                     int unitNo, boolean backyard) {
        super("TownHouse", floorSize, fullAddress, location, marketValue, rentalCost, availability);
        this.unitNo = unitNo;
        this.backyard = backyard;
    }

    // Returns a readable summary of this townhouse
    @Override
    public String getDetails() {
        return "TownHouse [ID=" + getPropertyID() +
               " | Unit=" + unitNo +
               " | Address=" + getFullAddress() +
               " | Location=" + getLocation() +
               " | Floor=" + getFloorSize() +
               " | Backyard=" + backyard +
               " | MarketValue=" + getMarketValue() +
               " | Rental=" + getRentalCost() +
               " | Available=" + isAvailability() + "]";
    }

    // ── Database Methods ────────────────────────────────────────────────────

    // Saves this TownHouse to the database
    public void saveToDB(Connection conn) throws SQLException {
        String sql = "INSERT INTO townhouses " +
                     "(unit_no, backyard, property_type, floor_size, full_address, " +
                     "location, market_value, rental_cost, availability) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, unitNo);
        ps.setBoolean(2, backyard);
        ps.setString(3, getPropertyType());
        ps.setString(4, getFloorSize());
        ps.setString(5, getFullAddress());
        ps.setString(6, getLocation());
        ps.setDouble(7, getMarketValue());
        ps.setDouble(8, getRentalCost());
        ps.setBoolean(9, isAvailability());
        ps.executeUpdate();
        ps.close();
    }

    // Loads a TownHouse from the database using its ID.
    // Returns null if no matching record is found.
    public static TownHouse loadFromDB(Connection conn, int propertyID) throws SQLException {
        String sql = "SELECT * FROM townhouses WHERE property_id = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, propertyID);

        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            return null; // No record found
        }

        TownHouse townhouse = new TownHouse(
            rs.getString("floor_size"),
            rs.getString("full_address"),
            rs.getString("location"),
            rs.getDouble("market_value"),
            rs.getDouble("rental_cost"),
            rs.getBoolean("availability"),
            rs.getInt("unit_no"),
            rs.getBoolean("backyard")
        );

        rs.close();
        ps.close();

        return townhouse;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public int getUnitNo() { return unitNo; }
    public void setUnitNo(int unitNo) { this.unitNo = unitNo; }

    public boolean isBackyard() { return backyard; }
    public void setBackyard(boolean backyard) { this.backyard = backyard; }
}
