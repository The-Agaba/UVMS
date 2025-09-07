package com.example.uvms.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Plot implements Serializable {

    @SerializedName("plot_id")
    private int plotId;

    @SerializedName("tender")
    private int tenderId;

    @SerializedName("plotNumber")
    private String plotNumber;

    @SerializedName("locationDescription")
    private String locationDescription;

    @SerializedName("isAvailable")
    private boolean isAvailable;

    @SerializedName("application")
    private List<Integer> applicationIds;

    // --- Getters ---
    public int getPlotId() { return plotId; }
    public int getTenderId() { return tenderId; }
    public String getPlotNumber() { return plotNumber; }
    public String getLocationDescription() { return locationDescription; }
    public boolean isAvailable() { return isAvailable; }
    public List<Integer> getApplicationIds() { return applicationIds; }
}
