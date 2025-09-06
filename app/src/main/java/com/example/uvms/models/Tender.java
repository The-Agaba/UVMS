package com.example.uvms.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Tender implements Serializable {

    @SerializedName("tender_id")
    private int tenderId;

    @SerializedName("college")
    private int collegeId; // Only integer, matches API

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
    private Object createdBy; // Can be Admin object or Map, optional

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Getters
    public int getTenderId() { return tenderId; }
    public int getCollegeId() { return collegeId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDeadlineDate() { return deadlineDate; }
    public String getStatus() { return status; }
    public String getContractTemplatePath() { return contractTemplatePath; }
    public Object getCreatedBy() { return createdBy; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
