package com.example.uvms.models;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("notification_id")
    private int notificationId;

    @SerializedName("vendor_id")
    private Integer vendorId; // nullable

    @SerializedName("admin_id")
    private Integer adminId; // nullable

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("is_read")
    private boolean isRead;

    @SerializedName("related_entity")
    private String relatedEntity;

    @SerializedName("related_entity_id")
    private Integer relatedEntityId;

    @SerializedName("created_at")
    private String createdAt;

    public Notification(int notificationId, Integer vendorId, Integer adminId, String title, String message, boolean isRead, String relatedEntity, int relatedEntityId, String createdAt) {
        this.notificationId = notificationId;
        this.vendorId = vendorId;
        this.adminId = adminId;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.relatedEntity = relatedEntity;
        this.relatedEntityId = relatedEntityId;
        this.createdAt = createdAt;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getRelatedEntity() {
        return relatedEntity;
    }

    public void setRelatedEntity(String relatedEntity) {
        this.relatedEntity = relatedEntity;
    }

    public int getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(int relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    private boolean isExpanded = false;

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

}

