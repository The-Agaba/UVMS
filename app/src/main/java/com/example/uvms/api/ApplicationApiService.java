package com.example.uvms.api;

import com.example.uvms.models.Application;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;
import java.util.Map;

public interface ApplicationApiService {

    @POST("applications")
    Call<Application> createApplication(@Body Map<String, Object> body);


    @GET("applications")
    Call<List<Application>> getApplications();


    @GET("applications/vendor/{vendorId}")
    Call<List<Application>> getApplicationsByVendor(@Path("vendorId") int vendorId);

    @GET("applications/{id}")
    Call<Application> getApplication(@Path("id") int id);
}
