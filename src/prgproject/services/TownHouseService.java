/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prgproject.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import prgproject.model.TownHouse;
import prgproject.model.Lease;
import prgproject.model.Payment;
import prgproject.model.Tenant;
import prgproject.DAO.TownHouseDAO;
import prgproject.DAO.LeaseDao;
import prgproject.DAO.PaymentDAO;

public class TownHouseService {

    private final TownHouseDAO townHouseDAO = new TownHouseDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    

    public List<TownHouse> getAllTownHouses() {
        return townHouseDAO.getAllTownHouses();
    }

    public List<TownHouse> getAvailableTownHouses() {
        List<TownHouse> all = townHouseDAO.getAllTownHouses();
        List<TownHouse> available = new ArrayList<>();
        if (all == null) {
            return available;
        }
        for (TownHouse t : all) {
            if (t.isAvailability()) {
                available.add(t);
            }
        }
        return available;
    }

    public List<TownHouse> getOccupiedTownHouses() {
        List<TownHouse> all = townHouseDAO.getAllTownHouses();
        List<TownHouse> occupied = new ArrayList<>();
        if (all == null) {
            return occupied;
        }
        for (TownHouse t : all) {
            if (!t.isAvailability()) {
                occupied.add(t);
            }
        }
        return occupied;
    }

    public void addTownHouse(TownHouse townHouse) {
        validateTownHouse(townHouse);
        townHouseDAO.saveTownHouse(townHouse);
    }

    public void modifyTownHouse(TownHouse townHouse) {
        validateTownHouse(townHouse);
        townHouseDAO.updateTownHouse(townHouse);
    }

    public void removeTownHouse(TownHouse townHouse) {
        if (townHouse == null) {
            throw new IllegalArgumentException("TownHouse cannot be null.");
        }
        townHouseDAO.deleteTownHouse(townHouse);
    }

    public double calculateLatePenalty(Lease lease, LocalDate paymentDate) {
        if (lease == null || paymentDate == null) {
            return 0.0;
        }

        LocalDate dueDate = lease.getDueDate();
        LocalDate graceCutoff = dueDate.plusDays(lease.getGracePeriod());

        if (!paymentDate.isAfter(graceCutoff)) {
            return 0.0; // within grace period
        }
        long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(graceCutoff, paymentDate);
        double penalty = lease.getRentAmount() * lease.getLatePenaltyRate() * overdueDays;

        return Math.round(penalty * 100.0) / 100.0;
    }

    public double calculateTotalOwed(Lease lease, LocalDate paymentDate) {
        if (lease == null) {
            return 0.0;
        }
        return lease.getRentAmount() + calculateLatePenalty(lease, paymentDate);
    }

    public double calculateOutstandingBalance(Lease lease, double amountPaid, LocalDate paymentDate) {
        double totalOwed = calculateTotalOwed(lease, paymentDate);
        double balance = totalOwed - amountPaid;
        return Math.max(0.0, Math.round(balance * 100.0) / 100.0);
    }

    public double calculateTotalMonthlyRentalIncome() {
        List<TownHouse> occupied = getOccupiedTownHouses();
        double total = 0.0;
        for (TownHouse t : occupied) {
            total += t.getRentalCost();
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public void assignTenantToTownHouse(TownHouse townHouse, Tenant tenant, Lease lease) {
        if (townHouse == null) {
            throw new IllegalArgumentException("TownHouse cannot be null.");
        }
        if (tenant == null) {
            throw new IllegalArgumentException("Tenant cannot be null.");
        }
        if (lease == null) {
            throw new IllegalArgumentException("Lease cannot be null.");
        }

        if (!townHouse.isAvailability()) {
            throw new IllegalStateException(
                    "TownHouse unit " + townHouse.getUnitNo() + " is not available for rent."
            );
        }

        validateLease(lease);

        LeaseDao.saveLease(lease);

        townHouse.setAvailability(false);
        townHouseDAO.updateTownHouse(townHouse);
    }

    public void recordPayment(Payment payment, Lease lease, LocalDate paymentDate) {
        validatePayment(payment);

        String status = determinePaymentStatus(payment.getAmount(), lease);
        payment.setStatus(status);
        payment.setPartial("Partial".equals(status));

        paymentDAO.savePayment(payment);
    }

    public void vacateTownHouse(TownHouse townHouse, Lease lease) {
        if (townHouse == null) {
            throw new IllegalArgumentException("TownHouse cannot be null.");
        }
        if (lease == null) {
            throw new IllegalArgumentException("Lease cannot be null.");
        }

        if (LocalDate.now().isBefore(lease.getEndDate())) {
            throw new IllegalStateException(
                    "Lease has not yet ended. End date is: " + lease.getEndDate()
            );
        }

        townHouse.setAvailability(true);
        townHouseDAO.updateTownHouse(townHouse);
    }

    public String generateInvoice(Payment payment, Lease lease) {
        if (payment == null) {
            return "No payment data available.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           PAYMENT INVOICE              \n");
        sb.append("========================================\n");
        sb.append("Receipt No.: ").append(payment.getReceipt()).append("\n");
        sb.append("Payment Date: ").append(payment.getPaymentDate()).append("\n");
        sb.append("Amount Paid: N$").append(String.format("%.2f", payment.getAmount())).append("\n");
        sb.append("Partial Payment: ").append(payment.isPartial() ? "Yes" : "No").append("\n");
        sb.append("Status: ").append(payment.getStatus()).append("\n");

        if (lease != null) {
            LocalDate payDate = LocalDate.parse(payment.getPaymentDate());
            double penalty = calculateLatePenalty(lease, payDate);
            double balance = calculateOutstandingBalance(lease, payment.getAmount(), payDate);

            sb.append("----------------------------------------\n");
            sb.append("Lease ID: ").append(lease.getLeaseID()).append("\n");
            sb.append("Rent Due: N$").append(String.format("%.2f", lease.getRentAmount())).append("\n");
            sb.append("Due Date: ").append(lease.getDueDate()).append("\n");
            sb.append("Grace Period: ").append(lease.getGracePeriod()).append(" day(s)\n");
            sb.append("Late Penalty: N$").append(String.format("%.2f", penalty)).append("\n");
            sb.append("Balance Owed: N$").append(String.format("%.2f", balance)).append("\n");
        }

        sb.append("========================================\n");
        return sb.toString();
    }

    public String determinePaymentStatus(double amountPaid, Lease lease) {
        if (lease == null || amountPaid <= 0) {
            return "Unpaid";
        }
        if (amountPaid >= lease.getRentAmount()) {
            return "Paid";
        }
        return "Partial";
    }

    public boolean isAvailable(TownHouse townHouse) {
        return townHouse != null && townHouse.isAvailability();
    }

    public boolean isLeaseActive(Lease lease) {
        if (lease == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return !today.isBefore(lease.getStartDate()) && !today.isAfter(lease.getEndDate());
    }

    private void validateTownHouse(TownHouse townHouse) {
        if (townHouse == null) {
            throw new IllegalArgumentException("TownHouse cannot be null.");
        }
        if (townHouse.getFullAddress() == null || townHouse.getFullAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Full address is required.");
        }
        if (townHouse.getLocation() == null || townHouse.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Location is required.");
        }
        if (townHouse.getRentalCost() <= 0) {
            throw new IllegalArgumentException("Rental cost must be greater than 0.");
        }
        if (townHouse.getMarketValue() <= 0) {
            throw new IllegalArgumentException("Market value must be greater than 0.");
        }
        if (townHouse.getUnitNo() <= 0) {
            throw new IllegalArgumentException("Unit number must be greater than 0.");
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

    private void validatePayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null.");
        }
        if (payment.getAmount() < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative.");
        }
        if (payment.getPaymentDate() == null || payment.getPaymentDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Payment date is required.");
        }
        if (payment.getLeaseID() <= 0) {
            throw new IllegalArgumentException("A valid lease ID is required.");
        }
    }
}
