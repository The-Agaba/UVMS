package com.example.uvms.activities;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.uvms.R;
import com.example.uvms.api.NotificationApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.fragments.HomeFragment;
import com.example.uvms.fragments.NotificationsFragment;
import com.example.uvms.fragments.TendersFragment;
import com.example.uvms.models.Notification;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AlertDialog;


public class HomeActivity extends AppCompatActivity implements NotificationsFragment.OnNotificationsLoadedListener {

    private List<Notification> notifications; // store all notifications
    private TextView badge; // cache the badge reference (avoid findViewById repeatedly)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        ImageView notificationIcon = findViewById(R.id.navNotificationIcon);
        TextView navTitle = findViewById(R.id.navTitle);
        badge = findViewById(R.id.badge); // 🔹 assign once

        fetchNotifications();

        // Open NotificationsFragment when bell icon is clicked
        notificationIcon.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainContainer, new NotificationsFragment())
                    .commit();
            navTitle.setText(R.string.notification_title);
        });

        // Default fragment → Home
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, new HomeFragment())
                .commit();

        // Handle bottom navigation switching
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment = null;

            if (id == R.id.profileActivity) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (id == R.id.businessFragment) {
                selectedFragment = new HomeFragment();
                navTitle.setText(R.string.home_title);
            } else if (id == R.id.tendersFragment) {
                selectedFragment = new TendersFragment();
                navTitle.setText(R.string.tenders_title);
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainContainer, selectedFragment)
                        .commit();
            }
            return true;
        });
    }


    // 🔹 Fetch notifications from API (same as in NotificationsFragment)
    private void fetchNotifications() {
        NotificationApiService apiService = RetrofitClient.getInstance()
                .create(NotificationApiService.class);

        Call<List<Notification>> call = apiService.getNotifications();
        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    onNotificationsLoaded(response.body()); // reuse callback
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                // You could log or show a toast here
            }
        });
    }


    // Callback from NotificationsFragment
    @Override
    public void onNotificationsLoaded(List<Notification> notifications) {
        this.notifications = notifications;
        updateBadge(countUnread());
    }

    // Calculate unread notifications
    private int countUnread() {
        if (notifications == null) return 0;
        int count = 0;
        for (Notification n : notifications) {
            if (!n.isRead()) count++;
        }
        return count;
    }

    //  Update the badge UI
    private void updateBadge(int unread) {
        if (badge == null) return; // safety
        if (unread > 0) {
            badge.setVisibility(View.VISIBLE);
            badge.setText(String.valueOf(unread));
        } else {
            badge.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
