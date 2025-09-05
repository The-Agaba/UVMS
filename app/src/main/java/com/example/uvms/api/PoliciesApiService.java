package com.example.uvms.api;

import com.example.uvms.models.Policy;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface PoliciesApiService {

    // Get all policies
    @GET("policies")
    Call<List<Policy>> getAllPolicies();

    // Get policies by scope (e.g., university or college)
    @GET("policies")
    Call<List<Policy>> getPoliciesByScope(@Query("scope") String scope);

    // Get policies for a specific college
    @GET("policies")
    Call<List<Policy>> getPoliciesByCollege(@Query("collegeId") int collegeId);

    // Get a single policy by its ID
    @GET("policies/{id}")
    Call<Policy> getPolicyById(@Path("id") int policyId);
}
