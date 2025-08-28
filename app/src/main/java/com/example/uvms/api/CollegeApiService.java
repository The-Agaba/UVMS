package com.example.uvms.api;

import com.example.uvms.models.College;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CollegeApiService {
    @GET("colleges/{id}") // replace with your mock API endpoint
    Call<College> getCollegeById(@Path("id") int collegeId);
}
