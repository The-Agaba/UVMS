package com.example.uvms.api;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VendorApiService {

    // Full update (PUT) - sends a complete Vendor object
    @PUT("vendors/{id}")
    Call<ResponseBody> updateVendor(
            @Header("Authorization") String token,
            @Path("id") int vendorId,
            @Body Object vendor
    );

    // Partial update (PATCH) - sends only fields you want to edit
    @PATCH("vendors/{id}/")
    Call<ResponseBody> updateVendorPartial(
            @Header("Authorization") String token,
            @Path("id") int vendorId,
            @Body Map<String, Object> fields
    );
}
