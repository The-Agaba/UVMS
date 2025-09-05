package com.example.uvms.models;

import android.graphics.Color;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class License implements Serializable {

    @SerializedName("license_id")
    private int licenseId;

    @SerializedName("application")
    private int applicationId;

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

    @SerializedName("status")
    @Nullable
    private String status;

    @SerializedName("isActive")
    private boolean isActive;

    // --- Related entities ---
    @SerializedName("vendor")
    private Vendor vendor;

    @SerializedName("college")
    private College college;

    @SerializedName("plot")
    private Plot plot;

    @SerializedName("tender")
    private Tender tender;

    // --- Track expansion for RecyclerView ---
    private boolean expanded = false;

    // --- Empty constructor for Retrofit/Gson ---
    public License() {}

    // --- Getters ---
    public int getLicenseId() { return licenseId; }
    public int getApplicationId() { return applicationId; }
    @Nullable public String getLicenseNumber() { return licenseNumber; }
    @Nullable public String getIssueDate() { return issueDate; }
    @Nullable public String getExpiryDate() { return expiryDate; }
    @Nullable public String getLicenseFilePath() { return licenseFilePath; }
    @Nullable public String getStatus() { return status; }
    public boolean isActive() { return isActive; }

    public Vendor getVendor() { return vendor; }
    public College getCollege() { return college; }
    public Plot getPlot() { return plot; }
    public Tender getTender() { return tender; }

    public boolean isExpanded() { return expanded; }

    // --- Setters ---
    public void setLicenseId(int licenseId) { this.licenseId = licenseId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
    public void setLicenseNumber(@Nullable String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setIssueDate(@Nullable String issueDate) { this.issueDate = issueDate; }
    public void setExpiryDate(@Nullable String expiryDate) { this.expiryDate = expiryDate; }
    public void setLicenseFilePath(@Nullable String licenseFilePath) { this.licenseFilePath = licenseFilePath; }
    public void setStatus(@Nullable String status) { this.status = status; }
    public void setActive(boolean active) { isActive = active; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }
    public void setCollege(College college) { this.college = college; }
    public void setPlot(Plot plot) { this.plot = plot; }
    public void setTender(Tender tender) { this.tender = tender; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }

    // --- Helper Methods ---
    public int getStatusColor() {
        if (status == null) return Color.parseColor("#2196F3"); // Default Blue
        switch (status.toUpperCase()) {
            case "ACTIVE": return Color.parseColor("#4CAF50"); // Green
            case "EXPIRED": return Color.parseColor("#F44336"); // Red
            case "PENDING": return Color.parseColor("#FF9800"); // Orange
            case "REJECTED": return Color.parseColor("#9E9E9E"); // Gray
            default: return Color.parseColor("#2196F3"); // Blue
        }
    }

    public String getSafeString(@Nullable String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }
}
