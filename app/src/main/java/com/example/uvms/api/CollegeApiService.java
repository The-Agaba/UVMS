package com.example.uvms.api;

import com.example.uvms.models.College;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CollegeApiService {

    // Fetch all colleges (with nested tenders and admins)
    @GET("colleges")
    Call<List<College>> getColleges();

    // Optional: fetch single college by ID
    @GET("colleges/{id}")
    Call<College> getCollegeById(@retrofit2.http.Path("id") int collegeId);
}
