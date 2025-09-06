package com.example.uvms.models;

import com.google.gson.annotations.SerializedName;

public class Policy {

    @SerializedName("policy_id")
    private int policyId;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("scope")
    private String scope;

    @SerializedName("college_id")
    private Integer collegeId; // nullable

    @SerializedName("admin")
    private Integer adminId; // simplified: store just the ID

    @SerializedName("datePosted")
    private String datePosted;

    @SerializedName("isActive")
    private boolean isActive;

    // Optional for local UI
    private String category;

    // Read more / Read less toggle
    private transient boolean expanded = false;

    // --- Getters & Setters ---
    public int getPolicyId() { return policyId; }
    public void setPolicyId(int policyId) { this.policyId = policyId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public Integer getCollegeId() { return collegeId; }
    public void setCollegeId(Integer collegeId) { this.collegeId = collegeId; }

    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }

    public String getDatePosted() { return datePosted; }
    public void setDatePosted(String datePosted) { this.datePosted = datePosted; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }
}
