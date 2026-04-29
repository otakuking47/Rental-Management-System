package prgproject.model;

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
    public TownHouse(int ID, String floorSize, String fullAddress, String location,
                     double marketValue, double rentalCost, boolean availability,
                     int unitNo, boolean backyard) {
        super(ID, "TownHouse", floorSize, fullAddress, location, marketValue, rentalCost, availability);
        this.unitNo = unitNo;
        this.backyard = backyard;
    }

    // Returns a readable summary of this townhouse
    @Override
    public String getDetails() {
        return "TownHouse [ID=" + getID() +
               " | Unit=" + unitNo +
               " | Address=" + getFullAddress() +
               " | Location=" + getLocation() +
               " | Floor=" + getFloorSize() +
               " | Backyard=" + backyard +
               " | MarketValue=" + getMarketValue() +
               " | Rental=" + getRentalCost() +
               " | Available=" + isAvailability() + "]";
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public int getUnitNo() { return unitNo; }
    public void setUnitNo(int unitNo) { this.unitNo = unitNo; }

    public boolean isBackyard() { return backyard; }
    public void setBackyard(boolean backyard) { this.backyard = backyard; }
}
