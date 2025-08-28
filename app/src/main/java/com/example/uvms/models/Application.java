package com.example.uvms.models;

import android.graphics.Color;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Application implements Serializable {

    @SerializedName("application_id")
    private int applicationId;

    @SerializedName("vendor_id")
    private int vendorId;

    @SerializedName("plot_id")
    private int plotId;

    @SerializedName("application_date")
    private String applicationDate;

    @SerializedName("status")
    private String status; // "pending", "approved", "denied"

    @SerializedName("submitted_contract_path")
    @Nullable
    private String submittedContractPath;

    @SerializedName("approved_contract_path")
    @Nullable
    private String approvedContractPath;

    @SerializedName("reviewed_by")
    @Nullable
    private Integer reviewedBy;

    @SerializedName("reviewed_at")
    @Nullable
    private String reviewedAt;

    @SerializedName("feedback")
    @Nullable
    private String feedback;

    // --- Empty constructor for Retrofit/Gson ---
    public Application() {}

    // --- Getters & Setters ---
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

    @Nullable
    public String getSubmittedContractPath() { return submittedContractPath; }
    public void setSubmittedContractPath(@Nullable String submittedContractPath) { this.submittedContractPath = submittedContractPath; }

    @Nullable
    public String getApprovedContractPath() { return approvedContractPath; }
    public void setApprovedContractPath(@Nullable String approvedContractPath) { this.approvedContractPath = approvedContractPath; }

    @Nullable
    public Integer getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(@Nullable Integer reviewedBy) { this.reviewedBy = reviewedBy; }

    @Nullable
    public String getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(@Nullable String reviewedAt) { this.reviewedAt = reviewedAt; }

    @Nullable
    public String getFeedback() { return feedback; }
    public void setFeedback(@Nullable String feedback) { this.feedback = feedback; }

    // --- Helper Methods ---
    public String getSafeString(@Nullable String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }

    public int getStatusColor() {
        if (status == null) return Color.parseColor("#2196F3"); // Default Blue
        switch (status.toLowerCase()) {
            case "approved": return Color.parseColor("#4CAF50"); // Green
            case "pending": return Color.parseColor("#FF9800"); // Orange
            case "denied": return Color.parseColor("#F44336"); // Red
            default: return Color.parseColor("#2196F3"); // Blue
        }
    }
}
