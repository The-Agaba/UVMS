package com.example.uvms.models;

import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("notification_id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("isRead")
    private boolean read;

    @SerializedName("vendor")
    private Vendor vendor;  // nested vendor object

    // For RecyclerView expand/collapse
    private boolean expanded = false;

    public Notification(int id, String title, String message, String createdAt, boolean read, Vendor vendor) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.read = read;
        this.vendor = vendor;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }
}
