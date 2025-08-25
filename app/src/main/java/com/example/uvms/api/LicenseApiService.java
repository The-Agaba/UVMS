package com.example.uvms.api;

import com.example.uvms.models.License;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LicenseApiService {
    @GET("license") // your mock API endpoint
    Call<List<License>> getLicenses();
}
