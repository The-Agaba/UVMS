package com.example.uvms.api;

import com.example.uvms.models.Plot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PlotApiService {
    @GET("plots/{tenderId}") // replace with your mock API endpoint
    Call<Plot> getPlotByTenderId(@Path("tenderId") int tenderId);
}
