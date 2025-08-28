package com.example.uvms.models;

public class ApplicationItem {
    private String id;
    private String tenderName;
    private String submissionDate;
    private String status;

    public ApplicationItem(String id, String tenderName, String submissionDate, String status) {
        this.id = id;
        this.tenderName = tenderName;
        this.submissionDate = submissionDate;
        this.status = status;
    }

    public String getId() { return id; }
    public String getTenderName() { return tenderName; }
    public String getSubmissionDate() { return submissionDate; }
    public String getStatus() { return status; }
}