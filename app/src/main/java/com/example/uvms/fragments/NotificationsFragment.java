package com.example.uvms.fragments;

import android.os.Bundle;
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

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvNoNotifications;
    private NotificationsAdapter adapter;
    private List<Notification> notifications = new ArrayList<>();
    private NotificationApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoNotifications = view.findViewById(R.id.tvNoNotifications); // New TextView in XML

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationsAdapter(notifications, new NotificationsAdapter.OnNotificationActionListener() {
            @Override
            public void onMarkRead(Notification notification) {
                markNotificationRead(notification);
            }

            @Override
            public void onDelete(Notification notification) {
                deleteNotification(notification);
            }
        });
        recyclerView.setAdapter(adapter);

        if (getActivity() instanceof HomeActivity) {
            apiService = ((HomeActivity) getActivity()).getNotificationApiService();
        }

        fetchNotifications();

        return view;
    }

    private void fetchNotifications() {
        if (apiService == null) {
            Toast.makeText(getContext(), "API not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        apiService.getNotifications().enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    notifications.clear();
                    notifications.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    updateBadgeCount();
                    toggleNoNotificationsView();
                } else {
                    notifications.clear();
                    adapter.notifyDataSetChanged();
                    updateBadgeCount();
                    toggleNoNotificationsView();
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markNotificationRead(Notification notification) {
        if (apiService == null) return;

        apiService.markAsRead(notification.getNotificationId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    notification.setRead(true);
                    adapter.notifyDataSetChanged();
                    updateBadgeCount();
                } else {
                    Toast.makeText(getContext(), "Failed to mark as read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNotification(Notification notification) {
        if (apiService == null) return;

        apiService.deleteNotification(notification.getNotificationId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    notifications.remove(notification);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Notification deleted", Toast.LENGTH_SHORT).show();
                    updateBadgeCount();
                    toggleNoNotificationsView();
                } else {
                    Toast.makeText(getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBadgeCount() {
        if (getActivity() instanceof HomeActivity) {
            int unreadCount = 0;
            for (Notification n : notifications) {
                if (!n.isRead()) unreadCount++;
            }
            ((HomeActivity) getActivity()).updateNotificationBadge(unreadCount);
        }
    }

    private void toggleNoNotificationsView() {
        if (notifications.isEmpty()) {
            tvNoNotifications.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoNotifications.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
