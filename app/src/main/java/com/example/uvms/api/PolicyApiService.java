package com.example.uvms.api;

import com.example.uvms.models.Policy;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PolicyApiService {
    @GET("policy_table")
    Call<List<Policy>> getPolicies();

    @GET("policy_table/{id}")
    Call<Policy> getPolicyById(@Path("id") int id);

    @GET("policy_table")
    Call<List<Policy>> searchPolicies(@Query("search") String query);
}
