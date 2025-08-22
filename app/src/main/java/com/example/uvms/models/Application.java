package com.example.uvms.models;

public class Application {
    private int applicationId;
    private int vendorId;
    private int plotId;
    private String applicationDate;
    private String status; // "pending", "approved", "denied"
    private String submittedContractPath;
    private String approvedContractPath; // Nullable
    private Integer reviewedBy; // Nullable
    private String reviewedAt; // Nullable
    private String feedback; // Nullable

    // Getters & Setters
    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public int getVendorId() { return vendorId; }
    public void setVendorId(int vendorId) { this.vendorId = vendorId; }

    public int getPlotId() { return plotId; }
    public void setPlotId(int plotId) { this.plotId = plotId; }

    public String getApplicationDate() { return applicationDate; }
    public void setApplicationDate(String applicationDate) { this.applicationDate = applicationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSubmittedContractPath() { return submittedContractPath; }
    public void setSubmittedContractPath(String submittedContractPath) { this.submittedContractPath = submittedContractPath; }

    public String getApprovedContractPath() { return approvedContractPath; }
    public void setApprovedContractPath(String approvedContractPath) { this.approvedContractPath = approvedContractPath; }

    public Integer getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(Integer reviewedBy) { this.reviewedBy = reviewedBy; }

    public String getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(String reviewedAt) { this.reviewedAt = reviewedAt; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
