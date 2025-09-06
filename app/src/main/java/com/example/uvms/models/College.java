package com.example.uvms.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class College implements Serializable {

    @SerializedName("college_id")
    private int collegeId;

    @SerializedName("college_name")
    private String collegeName;

    // Tenders are IDs (numbers) in your API
    @SerializedName("tenders")
    private List<Integer> tenderIds;

    // Policies are also IDs (numbers)
    @SerializedName("policies")
    private List<Integer> policyIds;

    // Getters
    public int getCollegeId() {
        return collegeId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public List<Integer> getTenderIds() {
        return tenderIds;
    }

    public List<Integer> getPolicyIds() {
        return policyIds;
    }
}
