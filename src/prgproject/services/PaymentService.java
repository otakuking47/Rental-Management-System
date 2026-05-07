package prgproject.services;

import prgproject.DAO.PaymentDAO;
import prgproject.model.*;

public class PaymentService {

    private PaymentDAO paymentDAO = new PaymentDAO();
    private LeaseService leaseService = new LeaseService();

    public void recordPayment(Payment payment) {

        validatePayment(payment);

        try {
            int leaseId = payment.getLeaseID();

            Lease lease = leaseService.getLeaseById(leaseId);

            double rent = lease.getRentAmount();

            double totalPaid = paymentDAO.getTotalPaidByLeaseId(leaseId);

            double newTotal = totalPaid + payment.getAmount();

            // MONTHLY TRACKING
            String month = payment.getPaymentDate().substring(0, 7);

            double monthlyPaid = paymentDAO.getMonthlyTotalPaid(leaseId, month);

            // LATE PENALTY (example daysLate = 0 for now)
            int daysLate = 0;

            double penalty = leaseService.calculateLatePenalty(
                    lease,
                    daysLate,
                    payment.getAmount()
            );

            double finalAmount = payment.getAmount() + penalty;

            double balance = rent - newTotal;

            if (balance > 0) {
                payment.setStatus("PARTIAL");
                payment.setPartial(true);
            } else {
                payment.setStatus("FULL");
                payment.setPartial(false);
            }

            int result = paymentDAO.savePayment(payment);

            if (result == 0) {
                throw new RuntimeException("Payment failed.");
            }

        } catch (RuntimeException e) {
            throw new RuntimeException("Service error: Unable to process payment.", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error processing payment.", e);
        }
    }

    public double getMonthlyBalance(int leaseId, String month) {

        double rent = leaseService.getRentAmount(leaseId);
        double paid = paymentDAO.getMonthlyTotalPaid(leaseId, month);

        return rent - paid;
    }

    public void updatePayment(Payment payment) {

        validatePayment(payment);

        try {
            int result = paymentDAO.updatePayment(payment);

            if (result == 0) {
                throw new RuntimeException("Failed to update payment.");
            }

        } catch (RuntimeException e) {
            throw new RuntimeException("Service error: Unable to update payment.", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error updating payment.", e);
        }
    }

    public void deletePayment(int receipt) {

        if (receipt <= 0) {
            throw new IllegalArgumentException("Invalid receipt.");
        }

        try {
            int result = paymentDAO.deletePayment(receipt);

            if (result == 0) {
                throw new RuntimeException("Failed to delete payment.");
            }

        } catch (RuntimeException e) {
            throw new RuntimeException("Service error: Unable to delete payment.", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error deleting payment.", e);
        }
    }

    private void validatePayment(Payment payment) {

        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null.");
        }

        if (payment.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }

        if (payment.getLeaseID() <= 0) {
            throw new IllegalArgumentException("Invalid lease ID.");
        }
    }
}