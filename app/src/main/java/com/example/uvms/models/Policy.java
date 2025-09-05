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

    @SerializedName("admin")   // matches the nested object
    private Admin admin;

    @SerializedName("datePosted")  // camelCase from JSON
    private String datePosted;

    @SerializedName("isActive")   // camelCase from JSON
    private boolean isActive;

    // Optional: for local UI categorization
    private String category;

    // --- NEW: Read more / Read less toggle ---
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

    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }

    public String getDatePosted() { return datePosted; }
    public void setDatePosted(String datePosted) { this.datePosted = datePosted; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // --- Read more / Read less ---
    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }
}
