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
    private String scope; // university or college

    @SerializedName("college_id")
    private Integer collegeId; // nullable

    @SerializedName("posted_by")
    private int postedBy;

    @SerializedName("date_posted")
    private String datePosted;

    @SerializedName("is_active")
    private boolean isActive;

    // Optional: You can still use category for local filtering
    private String category; // Food, Safety, Quality, Compliance

    // Getters & Setters
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

    public int getPostedBy() { return postedBy; }
    public void setPostedBy(int postedBy) { this.postedBy = postedBy; }

    public String getDatePosted() { return datePosted; }
    public void setDatePosted(String datePosted) { this.datePosted = datePosted; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
