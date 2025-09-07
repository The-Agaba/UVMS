package com.example.uvms.clients;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.uvms.api.CollegeApiService;
import com.example.uvms.api.LicenseApiService;
import com.example.uvms.api.LoginService;
import com.example.uvms.api.PlotApiService;
import com.example.uvms.api.TenderApiService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String TAG = "RetrofitClient";
    private static final String BASE_URL = "https://uvmsapiv1.onrender.com/api/";

    private static Retrofit retrofitWithToken;
    private static Retrofit retrofitWithoutToken;

    /** -------------------- Retrofit WITH token -------------------- */
    public static Retrofit getInstance(Context context) {
        if (retrofitWithToken == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder();

                        SharedPreferences prefs = context.getSharedPreferences("uvms_prefs", Context.MODE_PRIVATE);
                        String token = prefs.getString("auth_token", null);
                        if (token != null) {
                            builder.header("Authorization", "Bearer " + token);
                        }

                        return chain.proceed(builder.build());
                    })
                    .build();

            Log.d(TAG, "Creating Retrofit WITH token instance...");
            retrofitWithToken = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitWithToken;
    }

    /** -------------------- Retrofit WITHOUT token -------------------- */
    private static Retrofit getInstanceWithoutToken() {
        if (retrofitWithoutToken == null) {
            Log.d(TAG, "Creating Retrofit WITHOUT token instance...");
            retrofitWithoutToken = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitWithoutToken;
    }

    /** -------------------- Services -------------------- */
    public static LoginService getLoginService() {
        return getInstanceWithoutToken().create(LoginService.class);
    }

    public static LicenseApiService getLicenseService(Context context) {
        return getInstance(context).create(LicenseApiService.class);
    }

    public static TenderApiService getTenderService(Context context) {
        return getInstance(context).create(TenderApiService.class);
    }

    public static CollegeApiService getCollegeService(Context context) {
        return getInstance(context).create(CollegeApiService.class);
    }

    // ✅ Added PlotApiService getter
    public static PlotApiService getPlotService(Context context) {
        return getInstance(context).create(PlotApiService.class);
    }
}
