package com.example.uvms.models;

import com.google.gson.annotations.SerializedName;

public class Application{

    @SerializedName("application_id")
    private int applicationId;

    @SerializedName("vendor")
    private int vendorId;

    @SerializedName("plot")
    private int plotId;

    @SerializedName("applicationDate")
    private String applicationDate;

    @SerializedName("status")
    private String status;

    @SerializedName("submittedContractPath")
    private String submittedContractPath;

    @SerializedName("approvedContractPath")
    private String approvedContractPath;

    @SerializedName("reviewedBy")
    private String reviewedBy;

    @SerializedName("reviewedAt")
    private String reviewedAt;

    @SerializedName("feedback")
    private String feedback;

    @SerializedName("license")
    private Integer license; // can be null → use Integer instead of int

    // --- Getters ---
    public int getApplicationId() {
        return applicationId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public int getPlotId() {
        return plotId;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public String getStatus() {
        return status;
    }

    public String getSubmittedContractPath() {
        return submittedContractPath;
    }

    public String getApprovedContractPath() {
        return approvedContractPath;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public String getReviewedAt() {
        return reviewedAt;
    }

    public String getFeedback() {
        return feedback;
    }

    public Integer getLicense() {
        return license;
    }
}
