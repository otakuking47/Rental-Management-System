package propertymanagement;

public class TownHouse extends Property {

    private int unitNo;
    private boolean backyard;

    public TownHouse() {
        super();
    }

    public TownHouse(String floorSize, String fullAddress, String location,
                     double marketValue, double rentalCost, boolean availability,
                     int unitNo, boolean backyard) {

        super("TownHouse", floorSize, fullAddress, location,
              marketValue, rentalCost, availability);
        this.unitNo = unitNo;
        this.backyard = backyard;
    }


    public String getDetails() {
        return String.format(
            "TownHouse [ID=%d | Unit=%d | Address=%s | Location=%s | " +
            "Floor=%s | Backyard=%b | MarketValue=%.2f | Rental=%.2f | Available=%b]",
            getPropertyID(), unitNo, getFullAddress(), getLocation(),
            getFloorSize(), backyard, getMarketValue(),
            getRentalCost(), isAvailability());
    }

    // ── Getters & Setters ───────────────────────────────────────────────────
    public int getUnitNo() {
        return unitNo;
    }
    public void setUnitNo(int unitNo) {
        this.unitNo = unitNo;
    }

    public boolean isBackyard() {
        return backyard;
    }
    public void setBackyard(boolean b) {
        this.backyard = b;
    }
}
