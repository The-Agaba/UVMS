package com.example.uvms.api;

import com.example.uvms.models.Vendor;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface VendorApiService {
    @GET("vendors/{id}") // replace with your mock API endpoint
    Call<Vendor> getVendorById(@Path("id") int vendorId);
}
