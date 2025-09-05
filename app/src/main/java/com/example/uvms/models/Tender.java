package com.example.uvms.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Tender implements Serializable {

    @SerializedName("tender_id")
    private int tenderId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("deadlineDate")
    private String deadlineDate;

    @SerializedName("status")
    private String status;

    @SerializedName("contractTemplatePath")
    private String contractTemplatePath;

    @SerializedName("createdBy")
    private Admin createdBy;

    @SerializedName("college")
    private College college;  // ✅ Added field for nested college object

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // --- Getters & Setters ---
    public int getTenderId() { return tenderId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDeadlineDate() { return deadlineDate; }
    public String getStatus() { return status; }
    public String getContractTemplatePath() { return contractTemplatePath; }
    public Admin getCreatedBy() { return createdBy; }
    public College getCollege() { return college; } // ✅ Getter for College
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    // --- Helper for college name ---
    public String getCollegeName() {
        return college != null ? college.getCollegeName() : "N/A";
    }

    public void setCollegeName(String collegeName) {
    }
}
