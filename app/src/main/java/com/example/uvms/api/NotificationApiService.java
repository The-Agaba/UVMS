package com.example.uvms.api;

import com.example.uvms.models.Notification;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface NotificationApiService {

    @GET("notifications")
    Call<List<Notification>> getNotificationsForVendor(@Query("vendorId") int vendorId);

    @PATCH("notifications/{id}/read")
    Call<Notification> markAsRead(@Path("id") int id);

    @DELETE("notifications/{id}")
    Call<Void> deleteNotification(@Path("id") int id);

    @GET("notifications")
    Call<List<Notification>> getNotificationsForUser(@Query("email") String email);

    @GET("notifications")
    Call<List<Notification>> getAllNotifications();


}
