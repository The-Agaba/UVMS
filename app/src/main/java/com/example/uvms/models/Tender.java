package com.example.uvms.models;

public class Tender {
    private int tenderId;
    private int collegeId;
    private String title;
    private String description;
    private String deadlineDate;
    private String status; // "draft", "open", "closed", "awarded"
    private String contractTemplatePath;
    private int createdBy;
    private String createdAt;
    private String updatedAt;

    // Getters & Setters
    public int getTenderId() { return tenderId; }
    public void setTenderId(int tenderId) { this.tenderId = tenderId; }

    public int getCollegeId() { return collegeId; }
    public void setCollegeId(int collegeId) { this.collegeId = collegeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDeadlineDate() { return deadlineDate; }
    public void setDeadlineDate(String deadlineDate) { this.deadlineDate = deadlineDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getContractTemplatePath() { return contractTemplatePath; }
    public void setContractTemplatePath(String contractTemplatePath) { this.contractTemplatePath = contractTemplatePath; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
