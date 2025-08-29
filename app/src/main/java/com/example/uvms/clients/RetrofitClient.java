package com.example.uvms.clients;

import com.example.uvms.api.LicenseApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL =
            "https://api.mockfly.dev/mocks/4c1c6ccb-a305-4f13-a05b-4490b1541114/"; // ✅ must end with /

    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Add this method to get the API service
    public static LicenseApiService getApiService() {
        return getInstance().create(LicenseApiService.class);
    }
}
