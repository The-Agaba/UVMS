package com.example.uvms.models;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class License implements Serializable {

    @SerializedName("license_id")
    private int licenseId;

    @SerializedName("application")
    private Application application;

    // Can be full Vendor object or numeric ID
    @SerializedName("vendor")
    private Object vendorRaw;

    @SerializedName("licenseNumber")
    @Nullable
    private String licenseNumber;

    @SerializedName("issueDate")
    @Nullable
    private String issueDate;

    @SerializedName("expiryDate")
    @Nullable
    private String expiryDate;

    @SerializedName("licenseFilePath")
    @Nullable
    private String licenseFilePath;

    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("renewalRequests")
    private List<Integer> renewalRequests;

    private boolean expanded = false;
    private String status;

    public License() {}

    // --- Getters & Setters ---
    public int getLicenseId() { return licenseId; }
    public void setLicenseId(int licenseId) { this.licenseId = licenseId; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public Object getVendorRaw() { return vendorRaw; }
    public void setVendorRaw(Object vendorRaw) { this.vendorRaw = vendorRaw; }

    @Nullable
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(@Nullable String licenseNumber) { this.licenseNumber = licenseNumber; }

    @Nullable
    public String getIssueDate() { return issueDate; }
    public void setIssueDate(@Nullable String issueDate) { this.issueDate = issueDate; }

    @Nullable
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(@Nullable String expiryDate) { this.expiryDate = expiryDate; }

    @Nullable
    public String getLicenseFilePath() { return licenseFilePath; }
    public void setLicenseFilePath(@Nullable String licenseFilePath) { this.licenseFilePath = licenseFilePath; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public List<Integer> getRenewalRequests() { return renewalRequests; }
    public void setRenewalRequests(List<Integer> renewalRequests) { this.renewalRequests = renewalRequests; }

    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }

    public String getStatus() {
        return status != null ? status : (isActive ? "ACTIVE" : "EXPIRED");
    }
    public void setStatus(String status) { this.status = status; }

    // --- Robust vendor access ---
    @Nullable
    public Vendor getVendor() {
        if (vendorRaw instanceof Vendor) return (Vendor) vendorRaw;
        return null;
    }

    @Nullable
    public Integer getVendorId() {
        if (vendorRaw == null) return null;

        // Full Vendor object
        if (vendorRaw instanceof Vendor) return ((Vendor) vendorRaw).getVendorId();

        // Numeric vendor ID, could be Integer, Long, Double, etc.
        if (vendorRaw instanceof Number) return ((Number) vendorRaw).intValue();

        return null;
    }

    public String getVendorFullName() {
        Vendor v = getVendor();
        return v != null ? v.getFirstName() + " " + v.getLastName() : "-";
    }

    // --- UI helpers ---
    public int getStatusColor() {
        switch (getStatus().toUpperCase()) {
            case "ACTIVE": return 0xFF4CAF50;   // Green
            case "EXPIRED": return 0xFFF44336;  // Red
            case "PENDING": return 0xFFFF9800;  // Orange
            case "REJECTED": return 0xFF9E9E9E; // Grey
            default: return 0xFF2196F3;         // Blue
        }
    }

    public String getSafeString(@Nullable String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }
}
