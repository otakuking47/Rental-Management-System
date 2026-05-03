
package prgproject.services;

import java.util.List;
import prgproject.DAO.TenantDao;
import prgproject.model.Tenant;

public class TenantService {

    private TenantDao tenantDao;

    public TenantService() {
        this.tenantDao = new TenantDao();
    }

    // Retrieve all tenants
    public List<Tenant> getAllTenants() {

        List<Tenant> tenants = tenantDao.getAllTenants();

        if (tenants == null || tenants.isEmpty()) {
            throw new RuntimeException("No tenants found.");
        }

        return tenants;
    }

    // Add new tenant
    public void addTenant(Tenant tenant) {

        validateTenant(tenant);

        int result = tenantDao.saveTenant(tenant);

        if (result == 0) {
            throw new RuntimeException("Failed to save tenant.");
        }
    }

    // Update tenant
    public void updateTenant(Tenant tenant) {

        validateTenant(tenant);

        int result = tenantDao.updateTenant(tenant);

        if (result == 0) {
            throw new RuntimeException("Failed to update tenant with ID: " + tenant.getCredential());
        }
    }

    // Delete tenant
    public void deleteTenant(int credential) {

        if (credential <= 0) {
            throw new IllegalArgumentException("Invalid tenant credential.");
        }

        int result = tenantDao.deleteTenant(credential);

        if (result == 0) {
            throw new RuntimeException("Failed to delete tenant with ID: " + credential);
        }
    }

    // Business logic validation
    private void validateTenant(Tenant tenant) {

        if (tenant == null) {
            throw new IllegalArgumentException("Tenant cannot be null.");
        }

        if (tenant.getFirstName() == null || tenant.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty.");
        }

        if (tenant.getLastName() == null || tenant.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty.");
        }

        if (tenant.getCredential() <= 0) {
            throw new IllegalArgumentException("Invalid credential.");
        }

        if (tenant.getPhoneNumber() <= 0) {
            throw new IllegalArgumentException("Invalid phone number.");
        }

        if (tenant.getEmail() == null || !tenant.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email address.");
        }

        if (tenant.getStatus() == null || tenant.getStatus().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty.");
        }
    }
}