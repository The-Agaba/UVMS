package com.example.uvms.api;

import com.example.uvms.models.Vendor;
import com.example.uvms.request_response.RegisterRequest;
import com.example.uvms.request_response.RegisterResponse;


import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RegisterService {

    @POST("auth/register")
    Call<RegisterResponse> registerVendor(@Body RegisterRequest request);


    @GET("vendors")
    Call<List<Vendor>> getVendors();




}

