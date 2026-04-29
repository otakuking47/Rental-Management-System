/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prgproject.services;

/**
 *
 * @author Collin
 */
public class PaymentService extends prgproject.DAO.PaymentDAO{
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
    
}
