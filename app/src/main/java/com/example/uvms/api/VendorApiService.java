package com.example.uvms.api;

import com.example.uvms.models.Vendor;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VendorApiService {
    @PUT("vendors/{id}")
    Call<Vendor> updateVendor(
            @Header("Authorization") String token,
            @Path("id") int vendorId,
            @Body Vendor vendor
    );


    @PATCH("vendors/{id}/")
    Call<Vendor> updateVendorPartial(
            @Header("Authorization") String token,
            @Path("id") int vendorId,
            @Body Map<String, Object> fields
    );
}
