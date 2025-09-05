package com.example.uvms.models;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class License implements Serializable {

    @SerializedName("license_id")
    private int licenseId;

    @SerializedName("application")
    private int applicationId;

    @SerializedName("vendor")
    private int vendorId; // Matches backend ID

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

    // Track expansion for RecyclerView
    private boolean expanded = false;

    // Computed status for UI
    private String status;

    // --- Empty constructor ---
    public License() {}

    // --- Getters ---
    public int getLicenseId() { return licenseId; }
    public int getApplicationId() { return applicationId; }
    public int getVendorId() { return vendorId; }
    @Nullable public String getLicenseNumber() { return licenseNumber; }
    @Nullable public String getIssueDate() { return issueDate; }
    @Nullable public String getExpiryDate() { return expiryDate; }
    @Nullable public String getLicenseFilePath() { return licenseFilePath; }
    public boolean isActive() { return isActive; }
    public List<Integer> getRenewalRequests() { return renewalRequests; }
    public boolean isExpanded() { return expanded; }

    // --- Setters ---
    public void setLicenseId(int licenseId) { this.licenseId = licenseId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
    public void setVendorId(int vendorId) { this.vendorId = vendorId; }
    public void setLicenseNumber(@Nullable String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setIssueDate(@Nullable String issueDate) { this.issueDate = issueDate; }
    public void setExpiryDate(@Nullable String expiryDate) { this.expiryDate = expiryDate; }
    public void setLicenseFilePath(@Nullable String licenseFilePath) { this.licenseFilePath = licenseFilePath; }
    public void setActive(boolean active) { isActive = active; }
    public void setRenewalRequests(List<Integer> renewalRequests) { this.renewalRequests = renewalRequests; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }
    public void setStatus(String status) { this.status = status; }

    // --- Helper methods ---
    public String getStatus() {
        // Compute status dynamically if missing
        if (status != null) return status;
        return isActive ? "ACTIVE" : "EXPIRED";
    }

    public int getStatusColor() {
        String s = getStatus().toUpperCase();
        switch (s) {
            case "ACTIVE": return 0xFF4CAF50; // Green
            case "EXPIRED": return 0xFFF44336; // Red
            case "PENDING": return 0xFFFF9800; // Orange
            case "REJECTED": return 0xFF9E9E9E; // Gray
            default: return 0xFF2196F3; // Blue
        }
    }

    public String getSafeString(@Nullable String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }
}
