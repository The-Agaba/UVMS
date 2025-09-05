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
import retrofit2.http.Query;

public interface ApplicationApiService {

    /**
     * Fetch all applications for a given vendor.
     *
     *
     * @return a list of Application objects
     */

    @GET("applications")
    Call<List<Application>> getApplications();


    @Multipart
    @POST("apply_tender")
    Call<ResponseBody> applyTender(
            @Part("tender_id") RequestBody tenderId,
            @Part("company_name") RequestBody companyName,
            @Part("contact_person") RequestBody contactPerson,
            @Part("email") RequestBody email,
            @Part("proposal") RequestBody proposal,
            @Part MultipartBody.Part document
    );
}
