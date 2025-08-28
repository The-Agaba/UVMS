package com.example.uvms.models;

import android.graphics.Color;

public class Tender {
    public String id;          // Tender ID, e.g., TNDR-001
    public String title;       // Tender title
    public String buyer;       // Buyer/organization name
    public String category;    // Category: Goods, Works, Services
    public String location;    // Location of the tender
    public String deadline;    // Deadline string, e.g., "Tue, 2 Sep 2025 • 16:00 EAT"
    public String status;      // Status: Open, Closed, etc.
    public String documentUrl; // URL for tender documents
    public String budget;      // Optional: tender budget

    public Tender(String id, String title, String buyer, String category, String location,
                  String deadline, String status, String documentUrl) {
        this.id = id;
        this.title = title;
        this.buyer = buyer;
        this.category = category;
        this.location = location;
        this.deadline = deadline;
        this.status = status;
        this.documentUrl = documentUrl;
        this.budget = "TZS 120M"; // default placeholder, can be customized
    }

    public String getBuyer() {
        return buyer;
    }
    public String getDeadline() {
        return deadline;
    }
    public String getDocumentUrl() {
        return documentUrl;
    }
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getCategory() {
        return category;
    }
    public String getLocation() {
        return location;
    }
    public String getStatus() {
        return status;
    }
    public String getBudget() {
        return budget;
    }
    public void setBudget(String budget) {
        this.budget = budget;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSafeString(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }
    public int getStatusColor() {
        switch (status) {
            case "Open":
                return Color.parseColor("#008000"); // Green
            case "Closed":
                return Color.parseColor("#FF0000"); // Red
    }

    return Color.parseColor("#000000"); // Black
}

}
