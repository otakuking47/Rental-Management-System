package main.java.com.mycompany.prgproject;

import main.java.com.mycompany.prgproject.LeaseManage;
import java.util.ArrayList;
import java.util.List;

public class TenantManage {

    private String firstName;
    private String lastName;
    private int credential;
    private int phoneNumber;
    private String email;
    private String status;
    private List<LeaseManage> leaseHistory;

    public TenantManage(String firstName, String lastName, int credential,
                        int phoneNumber, String email, String status) {
        validateInput(firstName, lastName, email, status);
        this.firstName = firstName;
        this.lastName = lastName;
        this.credential = credential;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.status = status;
        this.leaseHistory = new ArrayList<>();
    }

    private void validateInput(String firstName, String lastName, String email, String status) {
        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("First and last name cannot be empty.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        if (!"Active".equals(status) && !"Blacklisted".equals(status)) {
            throw new IllegalArgumentException("Status must be 'Active' or 'Blacklisted'.");
        }
    }

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public int getCredential() { return credential; }
    public void setCredential(int credential) { this.credential = credential; }

    public int getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(int phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<LeaseManage> getLeaseHistory() {
        return new ArrayList<>(leaseHistory);
    }

    public void updateDetail(String firstName, String lastName, int phoneNumber,
                             String email, String status) {
        validateInput(firstName, lastName, email, status);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.status = status;
        System.out.println("✅ Tenant details updated successfully.");
    }

    public void removeTenet() {
        System.out.println("🗑️ Tenant " + firstName + " " + lastName + " removed.");
        leaseHistory.clear();
    }

    public String getHistory() {
        if (leaseHistory.isEmpty()) return "No lease history.";
        StringBuilder sb = new StringBuilder("=== Lease History for " + firstName + " " + lastName + " ===\n");
        for (LeaseManage lease : leaseHistory) {
            sb.append(lease.getDetail()).append("\n");
        }
        return sb.toString();
    }

    public void addLease(LeaseManage lease) {
        if (lease != null) leaseHistory.add(lease);
    }

    @Override
    public String toString() {
        return String.format("Tenant: %s %s | ID: %d | Phone: %d | Email: %s | Status: %s | Leases: %d",
                firstName, lastName, credential, phoneNumber, email, status, leaseHistory.size());
    }
}