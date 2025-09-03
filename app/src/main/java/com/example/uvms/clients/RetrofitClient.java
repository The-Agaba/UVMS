package com.example.uvms.clients;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.uvms.api.LicenseApiService;
import com.example.uvms.api.LoginService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    private static final String BASE_URL = "https://uvmsapiv1.onrender.com/api/";

    private static Retrofit retrofit;

    /**
     * Get Retrofit instance with token from SharedPreferences
     */
    public static Retrofit getInstance(Context context) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder();

                        // Get token from SharedPreferences
                        SharedPreferences prefs = context.getSharedPreferences("uvms_prefs", Context.MODE_PRIVATE);
                        String token = prefs.getString("auth_token", null);
                        if (token != null) {
                            requestBuilder.header("Authorization", "Bearer " + token);
                        }

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    })
                    .build();

            Log.d(TAG, "Creating new Retrofit instance with BASE_URL: " + BASE_URL);
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            Log.d(TAG, "Retrofit instance created successfully.");
        } else {
            Log.d(TAG, "Reusing existing Retrofit instance.");
        }
        return retrofit;
    }

    /**
     * Login service (no token required)
     */
    public static LoginService getLoginService() {
        return getInstanceWithoutToken().create(LoginService.class);
    }

    /**
     * API service (requires token)
     */
    public static LicenseApiService getApiService(Context context) {
        return getInstance(context).create(LicenseApiService.class);
    }

    /**
     * Separate Retrofit instance without token for login
     */
    private static Retrofit getInstanceWithoutToken() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
