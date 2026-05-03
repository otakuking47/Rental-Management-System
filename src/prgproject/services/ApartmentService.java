
package prgproject.services;

import java.util.List;
import prgproject.DAO.ApartmentDao;
import prgproject.model.Apartment;

public class ApartmentService {

    private ApartmentDao apartmentDao;

    public ApartmentService() {
        this.apartmentDao = new ApartmentDao();
    }

    // Get all apartments
    public List<Apartment> getAllApartments() {
        List<Apartment> apartments = apartmentDao.getAllApartments();

        if (apartments == null || apartments.isEmpty()) {
            throw new RuntimeException("No apartments found.");
        }

        return apartments;
    }

    // Add new apartment
    public void addApartment(Apartment apartment) {

        validateApartment(apartment);

        int result = apartmentDao.saveApartment(apartment);

        if (result == 0) {
            throw new RuntimeException("Failed to save apartment.");
        }
    }

    // Update apartment
    public void updateApartment(Apartment apartment) {

        validateApartment(apartment);

        int result = apartmentDao.updateApartment(apartment);

        if (result == 0) {
            throw new RuntimeException("Failed to update apartment with ID: " + apartment.getID());
        }
    }

    // Delete apartment
    public void deleteApartment(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("Invalid apartment ID.");
        }

        int result = apartmentDao.deleteApartment(id);

        if (result == 0) {
            throw new RuntimeException("Failed to delete apartment with ID: " + id);
        }
    }

    // Business logic validation
    private void validateApartment(Apartment apartment) {

        if (apartment == null) {
            throw new IllegalArgumentException("Apartment cannot be null.");
        }

        if (apartment.getUnitNo() <= 0) {
            throw new IllegalArgumentException("Invalid unit number.");
        }

        if (apartment.getFullAddress() == null || apartment.getFullAddress().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }

        if (apartment.getLocation() == null || apartment.getLocation().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty.");
        }

        if (apartment.getMarketValue() < 0) {
            throw new IllegalArgumentException("Market value cannot be negative.");
        }

        if (apartment.getRentalCost() < 0) {
            throw new IllegalArgumentException("Rental cost cannot be negative.");
        }

        if (apartment.getFloorLevel() < 0) 
        throw new IllegalArgumentException("Floor level cannot be negative.");
        }
    }
