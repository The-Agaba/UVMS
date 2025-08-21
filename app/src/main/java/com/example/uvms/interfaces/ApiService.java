package com.example.uvms.interfaces;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("business_type") // Replace with your mock endpoint
    Call<List<String>> getBusinessType();
}
