package com.example.uvms.api;

import com.example.uvms.models.Vendor;
import com.example.uvms.request_response.RegisterRequest;


import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RegisterService {

    @POST("news")
    Call<Vendor> registerVendor(@Body RegisterRequest request);


    @GET("vendors") // or your mock endpoint returning the JSON
    Call<List<Vendor>> getVendors();




}

