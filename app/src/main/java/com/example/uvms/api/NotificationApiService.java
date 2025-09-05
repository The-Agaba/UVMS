package com.example.uvms.api;

import com.example.uvms.models.Notification;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NotificationApiService {

    // 1. Fetch all notifications
    @GET("notifications")
    Call<List<Notification>> getNotifications();

    // 2. Fetch a single notification by ID
    @GET("notifications/{id}")
    Call<Notification> getNotification(@Path("id") int id);

    // 3. Create a new notification
    @POST("notifications")
    Call<Notification> createNotification(@Body Notification notification);

    // 4. Update an existing notification completely
    @PUT("notifications/{id}")
    Call<Notification> updateNotification(@Path("id") int id, @Body Notification notification);

    // 5. Mark a specific notification as read (partial update)
    @PATCH("notifications/{id}/read")
    Call<Void> markAsRead(@Path("id") int id);

    // 6. Delete a specific notification
    @DELETE("notifications/{id}")
    Call<Void> deleteNotification(@Path("id") int id);
}
