package prgproject.model;

import javax.swing.JOptionPane;

public class Tenant {

    private String firstName;
    private String lastName;
    private int credential;
    private int phoneNumber;
    private String email;
    private String status;

    public Tenant(String firstName, String lastName, int credential,
            int phoneNumber, String email, String status) {
        validateInput(firstName, lastName, email, status);
        this.firstName = firstName;
        this.lastName = lastName;
        this.credential = credential;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.status = status;
    }

    private void validateInput(String firstName, String lastName, String email, String status) {
        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {

            JOptionPane.showMessageDialog(null, "First name and last name cannot be empty.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        if (email == null || !email.contains("@")) {
            JOptionPane.showMessageDialog(null, "Invalid email address.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        if (!"Active".equals(status) && !"Blacklisted".equals(status)) {
            JOptionPane.showMessageDialog(null, "Status must be 'Active' or 'Blacklisted'.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== Getters & Setters ====================
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getCredential() {
        return credential;
    }

    public void setCredential(int credential) {
        this.credential = credential;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
