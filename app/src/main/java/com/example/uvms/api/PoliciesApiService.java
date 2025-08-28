package com.example.uvms.api; // Adjust package name as needed

import com.example.uvms.models.Policy;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface PoliciesApiService {

    // Example: Get all policies
    @GET("Policy")
    Call<List<Policy>> getAllPolicies();

    // Example: Get policies by scope (e.g., university or college)
    @GET("Policy")
    Call<List<Policy>> getPoliciesByScope(@Query("scope") String scope);

    // Example: Get policies for a specific college
    @GET("Policy")
    Call<List<Policy>> getPoliciesByCollege(@Query("collegeId") int collegeId);

    // Example: Get a single policy by its ID
    @GET("Policy/{id}")
    Call<Policy> getPolicyById(@Path("id") int policyId);
}