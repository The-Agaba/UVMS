package com.example.uvms.api;

import com.example.uvms.request_response.LoginRequest;
import com.example.uvms.request_response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {
    @POST("auth/login")
    Call<LoginResponse> loginVendor(@Body LoginRequest loginRequest);
}
