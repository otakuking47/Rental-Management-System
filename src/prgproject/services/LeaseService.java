package prgproject.services;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import prgproject.model.*;
import prgproject.DAO.LeaseDao;
import prgproject.DAO.PropertyDao;

public class LeaseService {

    private final LeaseDao leaseDb = new LeaseDao();
    private final PropertyDao propertyDb = new PropertyDao();

    public List<Lease> getAllLeases() {
        try {
            List<Lease> leases = leaseDb.getAllLeases();
            return leases == null ? java.util.Collections.emptyList() : leases;
        } catch (RuntimeException e) {
            throw new RuntimeException("Service error: Unable to fetch leases.", e);
        }
    }

    public void createLease(Lease lease) {
        validateLease(lease);
        Property property = propertyDb.getById(lease.getPropertyID());
        if (property != null && !property.isAvailability()) {
            throw new IllegalArgumentException("Property already occupied");
        }
        if (leaseDb.existsActiveLease(lease.getPropertyID())) {
            throw new IllegalArgumentException("Property already has an active lease");
        }
        lease.setIsActive(true);
        leaseDb.saveLease(lease);
        if (property != null) {
            property.setAvailability(false);
            propertyDb.updateProperty(property);
        }
    }

    public void saveLease(Lease lease) {
        validateLease(lease);
        int result = leaseDb.saveLease(lease);
        if (result == 0) throw new RuntimeException("Failed to save lease.");
    }

    public void updateLease(Lease lease) {
        validateLease(lease);
        int result = leaseDb.updateLease(lease);
        if (result == 0) throw new RuntimeException("Failed to update lease ID: " + lease.getLeaseID());
    }

    public void deleteLease(int id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid lease ID.");
        int result = leaseDb.deleteLease(id);
        if (result == 0) throw new RuntimeException("Failed to delete lease ID: " + id);
    }

    public void terminateLease(int leaseId) {
        Lease lease = leaseDb.getLeaseById(leaseId);
        if (lease == null) throw new IllegalArgumentException("Lease not found");
        if (!lease.isIsActive()) throw new IllegalArgumentException("Lease already terminated");
        lease.setIsActive(false);
        leaseDb.updateLease(lease);
        Property property = propertyDb.getById(lease.getPropertyID());
        if (property != null) {
            property.setAvailability(true);
            propertyDb.updateProperty(property);
        }
    }

    public Lease getLeaseById(int id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid Lease ID: " + id);
        try {
            return leaseDb.getLeaseById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Database error while getting Lease ID: " + id, e);
        }
    }

    public double getRentAmount(int leaseId) {
        Lease lease = getLeaseById(leaseId);
        return lease == null ? 0.0 : lease.getRentAmount();
    }

    public double calculateLatePenalty(Lease lease, int daysLate, double amount) {
        if (lease == null || daysLate <= lease.getGracePeriod()) return 0.0;
        int overdueDays = daysLate - lease.getGracePeriod();
        double penalty = amount * lease.getLatePenaltyRate() * overdueDays;
        return Math.round(penalty * 100.0) / 100.0;
    }

    public List<Integer> getLeaseDuration(int id) {
        Lease lease = leaseDb.getLeaseById(id);
        if (lease == null) return new ArrayList<>(List.of(0, 0, 0));
        Period period = Period.between(lease.getStartDate(), lease.getEndDate());
        return new ArrayList<>(List.of(period.getYears(), period.getMonths(), period.getDays()));
    }

    private void validateLease(Lease lease) {
        if (lease == null) throw new IllegalArgumentException("Lease cannot be null.");
        if (lease.getRentAmount() <= 0) throw new IllegalArgumentException("Rent amount must be greater than 0.");
        if (lease.getSecurityDeposit() < 0) throw new IllegalArgumentException("Security deposit cannot be negative.");
        if (lease.getLatePenaltyRate() < 0) throw new IllegalArgumentException("Late penalty rate cannot be negative.");
        if (lease.getGracePeriod() < 0) throw new IllegalArgumentException("Grace period cannot be negative.");
        if (lease.getStartDate() == null || lease.getEndDate() == null)
            throw new IllegalArgumentException("Start date and end date are required.");
        if (!lease.getEndDate().isAfter(lease.getStartDate()))
            throw new IllegalArgumentException("End date must be after start date.");
    }
}
