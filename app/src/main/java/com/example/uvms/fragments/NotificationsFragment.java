package com.example.uvms.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.activities.HomeActivity;
import com.example.uvms.adapters.NotificationsAdapter;
import com.example.uvms.api.NotificationApiService;
import com.example.uvms.models.Notification;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private static final String PREFS_NAME = "uvms_prefs";
    private static final String KEY_EMAIL = "email";

    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private TextView emptyView;
    private ProgressBar progressBar;

    private List<Notification> notifications = new ArrayList<>();
    private HomeActivity homeActivity;
    private NotificationApiService notificationApiService;
    private SharedPreferences sharedPreferences;

    private static final String TAG = "NotificationsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeActivity = (HomeActivity) getActivity();
        if (homeActivity == null) return;

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        emptyView = view.findViewById(R.id.tvNoNotifications);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationsAdapter(notifications, new NotificationsAdapter.NotificationActionListener() {
            @Override
            public void onMarkRead(Notification notification) {
                markNotificationAsRead(notification);
            }

            @Override
            public void onDelete(Notification notification) {
                deleteNotification(notification);
            }
        });
        recyclerView.setAdapter(adapter);

        notificationApiService = homeActivity.getNotificationApiService();
        loadNotificationsByEmail();
    }

    /** ---------------- Load Notifications by Email ---------------- */
    private void loadNotificationsByEmail() {
        String email = sharedPreferences.getString(KEY_EMAIL, "");
        if (email.isEmpty()) {
            showEmptyView("No email found in preferences");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);

        notificationApiService.getAllNotifications() // change this to GET /notifications
                .enqueue(new Callback<List<Notification>>() {
                    @Override
                    public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                        progressBar.setVisibility(View.GONE);
                        notifications.clear();

                        if (response.isSuccessful() && response.body() != null) {
                            for (Notification n : response.body()) {
                                if (n.getVendor() != null && email.equalsIgnoreCase(n.getVendor().getEmail())) {
                                    notifications.add(n);
                                }
                            }

                            if (notifications.isEmpty()) {
                                showEmptyView("No notifications for your email");
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                            updateBadge();
                        } else {
                            showEmptyView("No notifications found");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Notification>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        showEmptyView("Failed to load notifications");
                        Toast.makeText(getContext(), "Failed to load notifications", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error loading notifications: " + t.getMessage());
                    }
                });
    }

    /** ---------------- Mark Notification as Read ---------------- */
    private void markNotificationAsRead(Notification notification) {
        if (notification.isRead()) return;

        notificationApiService.markAsRead(notification.getId())
                .enqueue(new Callback<Notification>() {
                    @Override
                    public void onResponse(Call<Notification> call, Response<Notification> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            notification.setRead(true);
                            adapter.notifyDataSetChanged();
                            updateBadge();
                        }
                    }

                    @Override
                    public void onFailure(Call<Notification> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed to mark as read", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /** ---------------- Delete Notification ---------------- */
    private void deleteNotification(Notification notification) {
        notificationApiService.deleteNotification(notification.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        notifications.remove(notification);
                        adapter.notifyDataSetChanged();
                        updateEmptyView();
                        updateBadge();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed to delete notification", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /** ---------------- Update Empty View ---------------- */
    private void updateEmptyView() {
        if (notifications.isEmpty()) {
            showEmptyView("No notifications");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showEmptyView(String message) {
        emptyView.setText(message);
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    /** ---------------- Update Notification Badge ---------------- */
    private void updateBadge() {
        int unreadCount = 0;
        for (Notification n : notifications) if (!n.isRead()) unreadCount++;
        homeActivity.updateNotificationBadge(unreadCount);
    }
}
