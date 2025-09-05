package com.example.uvms.models;

import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("notification_id")
    private int id;

    @SerializedName("vendor")
    private Vendor vendor; // Nested vendor object

    @SerializedName("admin")
    private Object admin; // can be null for now

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("isRead")
    private boolean read;

    @SerializedName("relatedEntity")
    private String relatedEntity; // Can later map to enum

    @SerializedName("relatedEntityId")
    private int relatedEntityId;

    @SerializedName("createdAt")
    private String createdAt;

    // RecyclerView expand/collapse
    private boolean expanded = false;

    // Getters and Setters
    public int getId() { return id; }
    public Vendor getVendor() { return vendor; }
    public Object getAdmin() { return admin; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public String getRelatedEntity() { return relatedEntity; }
    public int getRelatedEntityId() { return relatedEntityId; }
    public String getCreatedAt() { return createdAt; }
    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }



    // Nested Vendor class
    public static class Vendor {
        @SerializedName("vendorId")
        private int vendorId;

        @SerializedName("email")
        private String email;

        @SerializedName("firstName")
        private String firstName;

        @SerializedName("lastName")
        private String lastName;

        @SerializedName("companyName")
        private String companyName;

        public int getVendorId() { return vendorId; }
        public String getEmail() { return email; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getCompanyName() { return companyName; }
    }

    // RelatedEntity enum
    public enum RelatedEntity {
        APPLICATION, LICENSE, RENEWAL
    }
}
