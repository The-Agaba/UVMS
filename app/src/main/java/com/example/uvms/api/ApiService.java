package com.example.uvms.api;

import com.example.uvms.models.License;
import com.example.uvms.models.Policy;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("business_type") // Replace with your mock endpoint
    Call<List<String>> getBusinessType();




}

