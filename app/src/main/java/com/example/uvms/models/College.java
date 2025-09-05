package com.example.uvms.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class College implements Serializable {

    @SerializedName("college_id")
    private int collegeId;

    @SerializedName("college_name")
    private String collegeName;

    @SerializedName("admins")
    private List<Admin> admins;

    @SerializedName("tenders")
    private List<Tender> tenders;

    // Getters
    public int getCollegeId() { return collegeId; }
    public String getCollegeName() { return collegeName; }
    public List<Admin> getAdmins() { return admins; }
    public List<Tender> getTenders() { return tenders; }
}
