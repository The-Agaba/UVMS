package com.example.uvms.api;



import com.example.uvms.models.Notification;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface NotificationApiService {

    // 1. Fetch all notifications
    @GET("Notifications")
    Call<List<Notification>> getNotifications();

    //  Mark a specific notification as read
    @PATCH("Notifications/{id}/read")
    Call<Void> markAsRead(@Path("id") int id);

    // 3. Delete a specific notification
    @DELETE("Notifications/{id}")
    Call<Void> deleteNotification(@Path("id") int id);
}
