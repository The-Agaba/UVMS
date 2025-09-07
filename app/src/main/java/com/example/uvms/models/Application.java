package com.example.uvms.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Application implements Serializable {

    @SerializedName("application_id")
    private int applicationId;

    @SerializedName("vendor")
    private int vendorId;

    // Can be either an int (plot ID) or a full Plot object
    @SerializedName("plot")
    private Object plotRaw;

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
    private Integer license; // can be null

    // --- Getters ---

    public int getApplicationId() {
        return applicationId;
    }

    public int getVendorId() {
        return vendorId;
    }

    /**
     * Returns Plot ID if API returned an int.
     */
    public Integer getPlotId() {
        return (plotRaw instanceof Number) ? ((Number) plotRaw).intValue() : null;
    }

    /**
     * Returns full Plot object if API returned an object or was manually set.
     */
    public Plot getPlot() {
        return (plotRaw instanceof Plot) ? (Plot) plotRaw : null;
    }

    /**
     * Manually set the Plot object.
     * This preserves existing functionality while allowing runtime assignment.
     */
    public void setPlot(Plot plot) {
        this.plotRaw = plot;
    }

    /**
     * Returns a safe, readable Plot info string for display.
     */
    public String getPlotInfo() {
        Plot plot = getPlot();
        if (plot != null) {
            String number = plot.getPlotNumber() != null ? plot.getPlotNumber() : "-";
            String location = plot.getLocationDescription() != null ? plot.getLocationDescription() : "-";
            return number + " - " + location;
        } else if (getPlotId() != null) {
            return "Plot ID: " + getPlotId();
        }
        return "-";
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
