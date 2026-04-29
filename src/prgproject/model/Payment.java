package prgproject.model;

public class Payment {

    private int receipt;
    private boolean isPartial;
    private double amount;
    private String paymentDate;
    private String status;
    private int leaseID;

    // Default constructor 
    public Payment() {
        this.status = "Pending";
        this.isPartial = false;
    }

    public Payment(int receipt, boolean isPartial, double amount,
                         String paymentDate, String status, int leaseID) {
        this.receipt = receipt;
        this.isPartial = isPartial;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
        this.leaseID = leaseID;
    }    

    // Getters and Setters
    
    public int getReceipt() {
        return receipt;
    }

    public void setReceipt(int receipt) {
        this.receipt = receipt;
    }

    public boolean isPartial() {
        return isPartial;
    }

    public void setPartial(boolean isPartial) {
        this.isPartial = isPartial;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLeaseID() {
        return leaseID;
    }

    public void setLeaseID(int leaseID) {
        this.leaseID = leaseID;
    }

    public String displayDetails() {
        return "PaymentManage{" +
                "receipt=" + receipt +
                ", isPartial=" + isPartial +
                ", amount=" + amount +
                ", paymentDate='" + paymentDate + '\'' +
                ", status='" + status + '\'' +
                ", leaseID=" + leaseID +
                '}';
    }
}
