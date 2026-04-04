package main.java.com.mycompany.prgproject;

public class LeaseManage {
    private int leaseID;
    private String startDate;
    private String endDate;
    private double rentAmount;

    public LeaseManage(int leaseID, String startDate, String endDate, double rentAmount) {
        this.leaseID = leaseID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentAmount = rentAmount;
    }

    public String getDetail() {
        return "Lease #" + leaseID + " | " + startDate + " to " + endDate + " | Rent: N$" + rentAmount;
    }
}