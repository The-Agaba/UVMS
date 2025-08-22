package com.example.uvms.models;

public class RenewalRequest {
    private int renewalId;
    private int licenseId;
    private String requestDate;
    private String status; // "pending", "approved", "denied"

    // Getters & Setters
    public int getRenewalId() { return renewalId; }
    public void setRenewalId(int renewalId) { this.renewalId = renewalId; }

    public int getLicenseId() { return licenseId; }
    public void setLicenseId(int licenseId) { this.licenseId = licenseId; }

    public String getRequestDate() { return requestDate; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
