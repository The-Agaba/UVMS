package com.example.uvms.api;

import com.example.uvms.models.License;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Part;

public interface LicenseApiService {

    @GET("licenses")
    Call<List<License>> getLicenses();

    @GET("licenses/{id}")
    Call<License> getLicenseById(@Path("id") int id);

    @Multipart
    @POST("licenses/renew")
    Call<ResponseBody> renewLicense(
            @Part("license_number") RequestBody licenseNumber,
            @Part("company_name") RequestBody companyName,
            @Part("expiry_date") RequestBody expiryDate,
            @Part MultipartBody.Part licenseDocument
    );
}
