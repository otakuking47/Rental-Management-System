
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
        try {
            List<Apartment> apartments = apartmentDao.getAllApartments();

            if (apartments == null || apartments.isEmpty()) {
                throw new RuntimeException("No apartments found.");
            }

            return apartments;

        } catch (RuntimeException e) {
            throw new RuntimeException("Service error: Unable to fetch apartments.", e);

        } catch (Exception e) { 
            throw new RuntimeException("Unexpected error while fetching apartments.", e);
        }
    }

    // Add new apartment
    public void addApartment(Apartment apartment) {

        validateApartment(apartment);

        try {
            int result = apartmentDao.saveApartment(apartment);

            if (result == 0) {
                throw new RuntimeException("Failed to save apartment.");
            }

        } catch (RuntimeException e) {
            throw new RuntimeException("Service error: Unable to save apartment.", e);

        } catch (Exception e) { 
            throw new RuntimeException("Unexpected error while saving apartment.", e);
        }
    }

    // Update apartment
    public void updateApartment(Apartment apartment) {

        validateApartment(apartment);

        try {
            int result = apartmentDao.updateApartment(apartment);

            if (result == 0) {
                throw new RuntimeException("Failed to update apartment with ID: " + apartment.getID());
            }

        } catch (RuntimeException e) {
            throw new RuntimeException("Service error: Unable to update apartment.", e);

        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while updating apartment.", e);
        }
    }

    // Delete apartment
    public void deleteApartment(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("Invalid apartment ID.");
        }

        try {
            int result = apartmentDao.deleteApartment(id);

            if (result == 0) {
                throw new RuntimeException("Failed to delete apartment with ID: " + id);
            }

        } catch (RuntimeException e) {
            throw new RuntimeException("Service error: Unable to delete apartment.", e);

        } catch (Exception e) { 
            throw new RuntimeException("Unexpected error while deleting apartment.", e);
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