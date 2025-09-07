package com.example.uvms.api;

import com.example.uvms.models.Plot;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlotApiService {

    // Fetch all plots
    @GET("plots")
    Call<List<Plot>> getAllPlots();

    // fetch a plot by ID if needed
    @GET("plots/{plotId}")
    Call<Plot> getPlotById(@Path("plotId") int plotId);

    // fetch plot by tender
    @GET("plots")
    Call<List<Plot>> getPlotsByTender(@Query("tender") int tenderId);

}
