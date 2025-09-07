package com.example.uvms.api;

import com.example.uvms.models.Application;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApplicationApiService {

    @GET("applications")
    Call<List<Application>> getApplications();

    @Multipart
    @POST("apply_tender")
    Call<ResponseBody> applyTender(
            @Part("plot_id") RequestBody plotId,
            @Part MultipartBody.Part proposal,
            @Part MultipartBody.Part document
    );
}
