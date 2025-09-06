package com.example.uvms.models;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class License implements Serializable {

    @SerializedName("license_id")
    private int licenseId;

    @SerializedName("application")
    private Application application; // now full Application object

    @SerializedName("vendor")
    private Vendor vendor; // now full Vendor object

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

    public License() {}

    // --- Getters ---
    public int getLicenseId() { return licenseId; }
    public Application getApplication() { return application; }
    public Vendor getVendor() { return vendor; }
    @Nullable public String getLicenseNumber() { return licenseNumber; }
    @Nullable public String getIssueDate() { return issueDate; }
    @Nullable public String getExpiryDate() { return expiryDate; }
    @Nullable public String getLicenseFilePath() { return licenseFilePath; }
    public boolean isActive() { return isActive; }
    public List<Integer> getRenewalRequests() { return renewalRequests; }
    public boolean isExpanded() { return expanded; }

    // --- Setters ---
    public void setLicenseId(int licenseId) { this.licenseId = licenseId; }
    public void setApplication(Application application) { this.application = application; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }
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
        if (status != null) return status;
        return isActive ? "ACTIVE" : "EXPIRED";
    }

    public int getStatusColor() {
        String s = getStatus().toUpperCase();
        switch (s) {
            case "ACTIVE": return 0xFF4CAF50;
            case "EXPIRED": return 0xFFF44336;
            case "PENDING": return 0xFFFF9800;
            case "REJECTED": return 0xFF9E9E9E;
            default: return 0xFF2196F3;
        }
    }

    public String getSafeString(@Nullable String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }
}
