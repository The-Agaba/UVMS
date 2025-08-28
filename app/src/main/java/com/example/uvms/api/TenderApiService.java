package com.example.uvms.api;

import com.example.uvms.models.Tender;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TenderApiService {
    @GET("tenders/{id}") // replace with your mock API endpoint
    Call<Tender> getTenderById(@Path("id") int tenderId);
}
