package prgproject.model;

import java.time.LocalDate;

public class Lease {
    private int leaseID;
    private LocalDate startDate;
    private LocalDate endDate;
    private double rentAmount;
    private double securityDeposit;
    private double latePenaltyRate;
    private int gracePeriod;

    public Lease(int leaseID, LocalDate startDate, LocalDate endDate, double rentAmount,
            double securityDeposit, double latePenaltyRate, int gracePeriod ) {
        this.leaseID = leaseID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentAmount = rentAmount;
        this.securityDeposit = securityDeposit;
        this.latePenaltyRate = latePenaltyRate;
        this.gracePeriod = gracePeriod;
    }

    

    

    public int getLeaseID() {
        return leaseID;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getSecurityDeposit() {
        return securityDeposit;
    }

    public double getLatePenaltyRate() {
        return latePenaltyRate;
    }

    public void setSecurityDeposit(double securityDeposit) {
        this.securityDeposit = securityDeposit;
    }

    public void setLatePenaltyRate(double latePenaltyRate) {
        this.latePenaltyRate = latePenaltyRate;
    }

    public void setGracePeriod(int gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public double getRentAmount() {
        return rentAmount;
    }

    public void setLeaseID(int leaseID) {
        this.leaseID = leaseID;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setRentAmount(double rentAmount) {
        this.rentAmount = rentAmount;
    }

}
