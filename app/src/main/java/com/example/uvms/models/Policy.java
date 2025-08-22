package com.example.uvms.models;

public class Policy {
    private int policyId;
    private String title;
    private String content;
    private String scope; // "university" or "college"
    private Integer collegeId; // Nullable
    private int postedBy;
    private String datePosted;
    private boolean isActive;

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
}
