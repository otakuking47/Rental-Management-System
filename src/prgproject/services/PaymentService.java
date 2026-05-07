package prgproject.services;

import java.util.List;
import prgproject.DAO.PaymentDAO;
import prgproject.model.*;

public class PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final LeaseService leaseService = new LeaseService();

    public List<Payment> getAllPayments() {
        try {
            List<Payment> payments = paymentDAO.getAllPayments();
            return payments == null ? java.util.Collections.emptyList() : payments;
        } catch (RuntimeException e) {
            throw new RuntimeException("Service error: Unable to fetch payments.", e);
        }
    }

    public void savePayment(Payment payment) {
        validatePayment(payment);
        int result = paymentDAO.savePayment(payment);
        if (result == 0) throw new RuntimeException("Failed to save payment.");
    }

    public void recordPayment(Payment payment) {
        validatePayment(payment);
        try {
            int leaseId = payment.getLeaseID();
            Lease lease = leaseService.getLeaseById(leaseId);
            if (lease == null) throw new RuntimeException("Lease not found: " + leaseId);

            double rent = lease.getRentAmount();
            double totalPaid = paymentDAO.getTotalPaidByLeaseId(leaseId);
            double newTotal = totalPaid + payment.getAmount();

            // monthly tracking (kept for parity with original logic)
            String month = payment.getPaymentDate() != null && payment.getPaymentDate().length() >= 7
                    ? payment.getPaymentDate().substring(0, 7)
                    : "";
            if (!month.isEmpty()) {
                paymentDAO.getMonthlyTotalPaid(leaseId, month);
            }

            int daysLate = 0;
            leaseService.calculateLatePenalty(lease, daysLate, payment.getAmount());

            double balance = rent - newTotal;
            if (balance > 0) {
                payment.setStatus("PARTIAL");
                payment.setPartial(true);
            } else {
                payment.setStatus("FULL");
                payment.setPartial(false);
            }

            int result = paymentDAO.savePayment(payment);
            if (result == 0) throw new RuntimeException("Payment failed.");

        } catch (RuntimeException e) {
            throw new RuntimeException("Service error: Unable to process payment.", e);
        }
    }

    public double getMonthlyBalance(int leaseId, String month) {
        double rent = leaseService.getRentAmount(leaseId);
        double paid = paymentDAO.getMonthlyTotalPaid(leaseId, month);
        return rent - paid;
    }

    public void updatePayment(Payment payment) {
        validatePayment(payment);
        int result = paymentDAO.updatePayment(payment);
        if (result == 0) throw new RuntimeException("Failed to update payment.");
    }

    public void deletePayment(int receipt) {
        if (receipt <= 0) throw new IllegalArgumentException("Invalid receipt.");
        int result = paymentDAO.deletePayment(receipt);
        if (result == 0) throw new RuntimeException("Failed to delete payment.");
    }

    private void validatePayment(Payment payment) {
        if (payment == null) throw new IllegalArgumentException("Payment cannot be null.");
        if (payment.getAmount() <= 0) throw new IllegalArgumentException("Amount must be greater than 0.");
        if (payment.getLeaseID() <= 0) throw new IllegalArgumentException("Invalid lease ID.");
    }
}
