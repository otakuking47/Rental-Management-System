package prgproject.model;

public class Apartment extends Property {

    private int unitNo, floorLevel;
    private boolean hasElevator, hasBackyard;

    public Apartment() {
        super();
    }

    public Apartment(int id, String floorSize, String fullAddress, String location,
            double marketValue, double rentalCost, boolean availability, 
            int unitNo, int floorLevel, boolean hasElevator, boolean hasBackyard) {
        super(id, "House", floorSize, fullAddress, location,
                marketValue, rentalCost, availability);
        this.unitNo = unitNo;
        this.floorLevel = floorLevel;
        this.hasElevator = hasElevator;
        this.hasBackyard = hasBackyard;
    }

    @Override
    public String getDetails() {
        return String.format(
                "House [ID=%d | Address=%s | Location=%s | Floor=%s | "
                + " | MarketValue=%.2f | Rental=%.2f | Available=%b]"
                + "| Unit number=%d | Floor Level=%d | Has an Elevator=%s | Has a Backyard=%s |",
                getID(), getFullAddress(), getLocation(),
                getFloorSize(), getMarketValue(),
                getRentalCost(), isAvailability(), getUnitNo(), getFloorLevel(),
                isHasElevator(), isHasBackyard());
    }

    public int getUnitNo() {
        return unitNo;
    }

    public int getFloorLevel() {
        return floorLevel;
    }

    public boolean isHasElevator() {
        return hasElevator;
    }

    public boolean isHasBackyard() {
        return hasBackyard;
    }

    public void setUnitNo(int unitNo) {
        this.unitNo = unitNo;
    }

    public void setFloorLevel(int floorLevel) {
        this.floorLevel = floorLevel;
    }

    public void setHasElevator(boolean hasElevator) {
        this.hasElevator = hasElevator;
    }

    public void setHasBackyard(boolean hasBackyard) {
        this.hasBackyard = hasBackyard;
    }

    
}
