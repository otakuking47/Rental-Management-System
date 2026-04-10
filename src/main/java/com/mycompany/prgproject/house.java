
public class House extends Property {

    private double plotSize;

    public House() {
        super();
    }

    public House(String floorSize, String fullAddress, String location,
                 double marketValue, double rentalCost, boolean availability,
                 double plotSize) {
        super("House", floorSize, fullAddress, location,
              marketValue, rentalCost, availability);
        this.plotSize = plotSize;
    }

    public String getDetails() {
        return String.format(
            "House [ID=%d | Address=%s | Location=%s | Floor=%s | " +
            "PlotSize=%.2f | MarketValue=%.2f | Rental=%.2f | Available=%b]",
            getPropertyID(), getFullAddress(), getLocation(),
            getFloorSize(), plotSize, getMarketValue(),
            getRentalCost(), isAvailability());
    }

    public double getPlotSize() {
        return plotSize;
    }
    public void setPlotSize(double size) {
        this.plotSize = size;
    }
}
