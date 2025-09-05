package com.example.uvms.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Plot implements Serializable {

    @SerializedName("plot_id")
    private int plotId;

    @SerializedName("plotNumber")
    private String plotNumber;

    @SerializedName("locationDescription")
    private String locationDescription;

    @SerializedName("isAvailable")
    private boolean isAvailable;

    // Getters
    public int getPlotId() { return plotId; }
    public String getPlotNumber() { return plotNumber; }
    public String getLocationDescription() { return locationDescription; }
    public boolean isAvailable() { return isAvailable; }
}
