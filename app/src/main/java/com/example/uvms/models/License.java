package com.example.uvms.models;

public class License {
    private int licenseId;
    private int applicationId;
    private int vendorId;
    private String licenseNumber;
    private String issueDate;
    private String expiryDate;
    private String licenseFilePath;

    private String status;
    private boolean isActive;

    public License(int licenseId, int applicationId, int vendorId, String licenseNumber, String issueDate, String expiryDate, String licenseFilePath, String status, boolean isActive) {
        this.licenseId = licenseId;
        this.applicationId = applicationId;
        this.vendorId = vendorId;
        this.licenseNumber = licenseNumber;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.licenseFilePath = licenseFilePath;
        this.status = status;
        this.isActive = isActive;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getters & Setters
    public int getLicenseId() { return licenseId; }
    public void setLicenseId(int licenseId) { this.licenseId = licenseId; }

    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public int getVendorId() { return vendorId; }
    public void setVendorId(int vendorId) { this.vendorId = vendorId; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getLicenseFilePath() { return licenseFilePath; }
    public void setLicenseFilePath(String licenseFilePath) { this.licenseFilePath = licenseFilePath; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }


    public int getStatusColor() {
        switch (status.toUpperCase()) {
            case "ACTIVE":
                return android.graphics.Color.parseColor("#4CAF50"); // Green
            case "EXPIRED":
                return android.graphics.Color.parseColor("#F44336"); // Red
            case "PENDING":
                return android.graphics.Color.parseColor("#FF9800"); // Orange
            case "REJECTED":
                return android.graphics.Color.parseColor("#9E9E9E"); // Gray
            default:
                return android.graphics.Color.parseColor("#2196F3"); // Blue
        }
    }
}


