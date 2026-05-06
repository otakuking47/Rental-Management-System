package prgproject.services;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import prgproject.model.*;
import prgproject.DAO.LeaseDao;
import prgproject.DAO.*;

public class LeaseService {

    LeaseDao leaseDb = new LeaseDao();
    PropertyDao propertyDb = new PropertyDao();

    public void createLease(Lease lease) {

        
        Property property = propertyDb.getById(lease.getPropertyID());
        
        if (!property.isAvailability()) {
            throw new IllegalArgumentException("Property already occupied");
        }
        
        if (leaseDb.existsActiveLease(lease.getLeaseID())) {
            throw new IllegalArgumentException("Property already has an active lease");
        }
        
        lease.setIsActive(true);

        leaseDb.saveLease(lease);
        
        property.setAvailability(false);
        propertyDb.updateProperty(property);
    }

    public void terminateLease(int leaseId) {

        Lease lease = leaseDb.getLeaseById(leaseId);

        if (lease == null) {
            throw new IllegalArgumentException("Lease not found");
        }

        if (!lease.isIsActive()) {
            throw new IllegalArgumentException("Lease already terminated");
        }

        lease.setIsActive(false);
        leaseDb.updateLease(lease);

        Property property = propertyDb.getById(lease.getPropertyID());

        property.setAvailability(true);
        propertyDb.updateProperty(property);
    }

    public List<Integer> getLeaseDuration(int id) {
        try {
            Lease lease = leaseDb.getLeaseById(id);
            Period period = Period.between(lease.getStartDate(), lease.getEndDate());
            List<Integer> duration = new ArrayList<>(List.of(period.getYears(), period.getMonths(), period.getDays()));

            return duration;
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }
    }

    private void validateLease(Lease lease) {
        if (lease.getRentAmount() <= 0) {
            throw new IllegalArgumentException("Rent amount must be greater than 0.");
        }
        if (lease.getSecurityDeposit() < 0) {
            throw new IllegalArgumentException("Security deposit cannot be negative.");
        }
        if (lease.getLatePenaltyRate() < 0) {
            throw new IllegalArgumentException("Late penalty rate cannot be negative.");
        }
        if (lease.getGracePeriod() < 0) {
            throw new IllegalArgumentException("Grace period cannot be negative.");
        }
        if (lease.getStartDate() == null || lease.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required.");
        }
        if (!lease.getEndDate().isAfter(lease.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date.");
        }
    }
}
