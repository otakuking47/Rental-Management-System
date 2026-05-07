package prgproject.services;

import java.util.List;
import prgproject.DAO.HouseDao;
import prgproject.model.House;

public class HouseService {

    private final HouseDao houseDao = new HouseDao();

    public List<House> getAllHouses() {
        try {
            List<House> houses = houseDao.getAllHouses();
            return houses == null ? java.util.Collections.emptyList() : houses;
        } catch (RuntimeException e) {
            throw new RuntimeException("Service error: Unable to fetch houses.", e);
        }
    }

    public House getById(int id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid house ID.");
        return houseDao.getById(id);
    }

    public void saveHouse(House house) {
        validateHouse(house);
        int result = houseDao.saveHouse(house);
        if (result == 0) throw new RuntimeException("Failed to save house.");
    }

    public void updateHouse(House house) {
        validateHouse(house);
        int result = houseDao.updateHouse(house);
        if (result == 0) throw new RuntimeException("Failed to update house ID: " + house.getID());
    }

    public void deleteHouse(int id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid house ID.");
        int result = houseDao.deleteHouse(id);
        if (result == 0) throw new RuntimeException("Failed to delete house ID: " + id);
    }

    private void validateHouse(House house) {
        if (house == null) throw new IllegalArgumentException("House cannot be null.");
        if (house.getFullAddress() == null || house.getFullAddress().isEmpty())
            throw new IllegalArgumentException("Address cannot be empty.");
        if (house.getLocation() == null || house.getLocation().isEmpty())
            throw new IllegalArgumentException("Location cannot be empty.");
        if (house.getMarketValue() < 0)
            throw new IllegalArgumentException("Market value cannot be negative.");
        if (house.getRentalCost() < 0)
            throw new IllegalArgumentException("Rental cost cannot be negative.");
        if (house.getPlotSize() < 0)
            throw new IllegalArgumentException("Plot size cannot be negative.");
    }
}
