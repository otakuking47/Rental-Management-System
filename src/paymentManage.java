import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class paymentManage {

    private int receipt;
    private boolean isPartial;
    private double amount;
    private String paymentDate;
    private String status;
    private LeaseManage lease;

    // Database credentials (EDIT THESE)
    private static final String URL = "jdbc:mysql://localhost:3306/your_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    // Default constructor 
    public PaymentManage() {
        this.status = "Pending";
        this.isPartial = false;
    }

    public PaymentManage(int receipt, boolean isPartial, double amount,
                         String paymentDate, String status, LeaseManage lease) {
        this.receipt = receipt;
        this.isPartial = isPartial;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
        this.lease = lease;
    }

    public void updateStatus() {
        if (lease == null) {
            System.out.println("No lease associated with this payment.");
            return;
        }

        double rentDue = lease.getRentAmount();

        if (amount <= 0) {
            this.status = "Unpaid";
        } else if (amount < rentDue) {
            this.status = "Partial";
            this.isPartial = true;
        } else {
            this.status = "Paid";
            this.isPartial = false;
        }

        System.out.println("Payment status updated to: " + this.status);
    }

    public void generateInvoice() {
        System.out.println("========================================");
        System.out.println("           PAYMENT INVOICE              ");
        System.out.println("========================================");
        System.out.println("Receipt No.    : " + receipt);
        System.out.println("Payment Date   : " + paymentDate);
        System.out.println("Amount Paid    : N$" + String.format("%.2f", amount));
        System.out.println("Partial Payment: " + (isPartial ? "Yes" : "No"));
        System.out.println("Status         : " + status);

        if (lease != null) {
            System.out.println("----------------------------------------");
            System.out.println("Lease ID       : " + lease.getLeaseID());
            System.out.println("Rent Due       : N$" + String.format("%.2f", lease.getRentAmount()));
            System.out.println("Due Date       : " + lease.getDueDate());
            System.out.println("Grace Period   : " + lease.getGracePeriod() + " day(s)");
            System.out.println("Late Penalty   : " + (lease.getLatePenaltyRate() * 100) + "%");
        }

        System.out.println("========================================");
    }

    //  DATABASE METHOD (EDIT THESE)
    public void saveToDatabase() {
        String sql = "INSERT INTO payments (receipt, is_partial, amount, payment_date, status, lease_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, receipt);
            ps.setBoolean(2, isPartial);
            ps.setDouble(3, amount);
            ps.setString(4, paymentDate);
            ps.setString(5, status);
            ps.setInt(6, lease != null ? lease.getLeaseID() : 0);

            ps.executeUpdate();
            System.out.println("Payment saved to database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public LeaseManage getLease() {
        return lease;
    }

    public void setLease(LeaseManage lease) {
        this.lease = lease;
    }

    @Override
    public String toString() {
        return "PaymentManage{" +
                "receipt=" + receipt +
                ", isPartial=" + isPartial +
                ", amount=" + amount +
                ", paymentDate='" + paymentDate + '\'' +
                ", status='" + status + '\'' +
                ", leaseID=" + (lease != null ? lease.getLeaseID() : "N/A") +
                '}';
    }
}
