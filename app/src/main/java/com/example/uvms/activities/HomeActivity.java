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
import androidx.fragment.app.FragmentTransaction;

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

public class HomeActivity extends AppCompatActivity implements NotificationsFragment.OnNotificationsLoadedListener {

    private List<Notification> notifications;
    private TextView badge;
    private TextView navTitle;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Assign views
        bottomNavigation = findViewById(R.id.bottomNavigation);
        ImageView notificationIcon = findViewById(R.id.navNotificationIcon);
        navTitle = findViewById(R.id.navTitle);
        badge = findViewById(R.id.badge);

        // Fetch notifications
        fetchNotifications();

        // Bottom nav listener using if-else
        bottomNavigation.setOnItemSelectedListener(item -> {
            // Clear nav title on any bottom nav click
            navTitle.setText("");

            if (item.getItemId() == R.id.profileActivity) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (item.getItemId() == R.id.businessFragment) {
                switchFragment(new HomeFragment(), HomeFragment.class.getSimpleName(), false);
                return true;
            } else if (item.getItemId() == R.id.tendersFragment) {
                switchFragment(new TendersFragment(), TendersFragment.class.getSimpleName(), false);
                return true;
            }
            return false;
        });

        // Default fragment → Home
        if (savedInstanceState == null) {
            switchFragment(new HomeFragment(), HomeFragment.class.getSimpleName(), false);
        }

        // Notification icon click → hide nav title
        notificationIcon.setOnClickListener(v -> {
            navTitle.setText("");
            switchFragment(new NotificationsFragment(),
                    NotificationsFragment.class.getSimpleName(),
                    true);
        });

        // Listen for back stack changes
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
            if (current != null) updateNavState(current.getClass().getSimpleName());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
        if (current != null) updateNavState(current.getClass().getSimpleName());
    }

    private void switchFragment(Fragment fragment, String tag, boolean addToBackStack) {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.mainContainer);

        if (current == null || !current.getClass().equals(fragment.getClass())) {
            FragmentTransaction tx = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, fragment, tag);

            if (addToBackStack) tx.addToBackStack(tag);
            tx.commit();

            updateNavState(tag);
        }
    }

    // Update bottom nav only; nav title is already hidden
    private void updateNavState(String tag) {
        if (bottomNavigation == null) return;

        if (HomeFragment.class.getSimpleName().equals(tag)) {
            highlightBottomNavItem(R.id.businessFragment);
        } else if (TendersFragment.class.getSimpleName().equals(tag)) {
            highlightBottomNavItem(R.id.tendersFragment);
        } else {
            clearBottomNavHighlight();
        }
    }

    private void highlightBottomNavItem(int itemId) {
        for (int i = 0; i < bottomNavigation.getMenu().size(); i++) {
            bottomNavigation.getMenu().getItem(i).setChecked(bottomNavigation.getMenu().getItem(i).getItemId() == itemId);
        }
    }

    private void clearBottomNavHighlight() {
        for (int i = 0; i < bottomNavigation.getMenu().size(); i++) {
            bottomNavigation.getMenu().getItem(i).setChecked(false);
        }
    }

    private void fetchNotifications() {
        NotificationApiService apiService = RetrofitClient.getInstance().create(NotificationApiService.class);
        Call<List<Notification>> call = apiService.getNotifications();
        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null)
                    onNotificationsLoaded(response.body());
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) { }
        });
    }

    @Override
    public void onNotificationsLoaded(List<Notification> notifications) {
        this.notifications = notifications;
        updateBadge(countUnread());
    }

    private int countUnread() {
        int count = 0;
        if (notifications != null) {
            for (Notification n : notifications)
                if (!n.isRead()) count++;
        }
        return count;
    }

    private void updateBadge(int unread) {
        if (badge == null) return;
        badge.setVisibility(unread > 0 ? View.VISIBLE : View.GONE);
        badge.setText(unread > 0 ? String.valueOf(unread) : "");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
        if (current != null) updateNavState(current.getClass().getSimpleName());
    }
}
