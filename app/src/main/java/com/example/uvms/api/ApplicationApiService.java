package com.example.uvms.api;

import com.example.uvms.models.Application;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApplicationApiService {

    /**
     * Fetch all applications for a given vendor.
     *
     * @param vendorId the logged-in vendor's ID
     * @return a list of Application objects
     */
    @GET("applications") // replace with your real endpoint
    Call<List<Application>> getApplications(@Query("vendor_id") int vendorId);
}
