package com.example.uvms.api;

import com.example.uvms.models.Tender;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface TenderApiService {
    @GET("tenders") // your endpoint
    Call<List<Tender>> getAllTenders();
}
