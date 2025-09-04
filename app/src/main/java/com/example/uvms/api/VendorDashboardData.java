package com.example.uvms.api;

import com.example.uvms.models.Vendor;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface VendorDashboardData {
    @GET("vendors/dashboard")
    Call<Vendor> getData(@Header("Authorization") String token);
}
