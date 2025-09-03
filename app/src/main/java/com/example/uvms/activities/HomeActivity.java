package com.example.uvms.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.uvms.fragments.HomeFragment;
import com.example.uvms.fragments.NotificationsFragment;
import com.example.uvms.fragments.TendersFragment;
import com.example.uvms.models.Notification;
import com.example.uvms.api.NotificationApiService;
import com.example.uvms.clients.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NotificationsFragment.OnNotificationsLoadedListener {

    private BottomNavigationView bottomNavigationView;
    private TextView navTitle, navTitleDesc, badge;
    private ImageView notificationBell;
    private boolean suppressNavCallback = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        EdgeToEdge.enable(this);

        navTitle = findViewById(R.id.navTitle);
        navTitleDesc = findViewById(R.id.navTitle_desc);
        badge = findViewById(R.id.badge);
        notificationBell = findViewById(R.id.navNotificationIcon);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            switchFragment(new HomeFragment(), "HomeFragment", false);
        }

        setupBottomNavigation();
        setupNotificationBell();

        // Fetch unread notifications immediately to show badge
        fetchUnreadNotifications();

        // Update titles and bottom nav on back stack changes
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
            if (current != null) {
                String tag = current.getClass().getSimpleName();
                updateNavState(tag);
                updateNavTitle(tag);
            }
        });
    }

    //** Bottom navigation click handling
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (suppressNavCallback) return true;

            int id = item.getItemId();
            if (id == R.id.businessFragment) {
                switchFragment(new HomeFragment(), "HomeFragment", false);
            } else if (id == R.id.tendersFragment) {
                switchFragment(new TendersFragment(), "TendersFragment", false);
            } else if (id == R.id.profileActivity) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
            return true;
        });
    }

    //Notification bell click opens NotificationsFragment
    private void setupNotificationBell() {
        notificationBell.setOnClickListener(v ->
                switchFragment(new NotificationsFragment(), "NotificationsFragment", true)
        );
    }

    //Switch fragments safely
    private void switchFragment(Fragment fragment, String tag, boolean addToBackStack) {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
        if (current == null || !current.getClass().equals(fragment.getClass())) {
            FragmentTransaction tx = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, fragment, tag);
            if (addToBackStack) tx.addToBackStack(tag);
            tx.commit();

            updateNavState(tag);
            updateNavTitle(tag);
        }
    }

    //Bottom nav highlighting
    public void updateNavState(String tag) {
        if (bottomNavigationView == null) return;

        suppressNavCallback = true;
        try {
            if ("HomeFragment".equals(tag)) {
                checkOnly(R.id.businessFragment);
            } else if ("TendersFragment".equals(tag)) {
                checkOnly(R.id.tendersFragment);
            } else {
                clearChecks(); // Other fragments including Profile → no highlight
            }
        } finally {
            suppressNavCallback = false;
        }
    }

    private void checkOnly(int itemId) {
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i)
                    .setChecked(bottomNavigationView.getMenu().getItem(i).getItemId() == itemId);
        }
    }

    private void clearChecks() {
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    //Top bar titles
    public void updateNavTitle(String tag) {
        if (navTitle == null || navTitleDesc == null) return;

        switch (tag) {
            case "HomeFragment":
                navTitle.setText("Business");
                navTitleDesc.setText("Welcome Username");
                break;
            case "TendersFragment":
                navTitle.setText("Tenders");
                navTitleDesc.setText("Browse available tenders");
                break;
            case "NotificationsFragment":
                navTitle.setText("Notifications");
                navTitleDesc.setText("Stay updated with alerts");
                break;
            case "PoliciesFragment":
                navTitle.setText("Policies");
                navTitleDesc.setText("Review business policies");
                break;
            case "SettingsFragment":
                navTitle.setText("Settings");
                navTitleDesc.setText("Customize your preferences");
                break;
            case "HelpFragment":
                navTitle.setText("Help & Support");
                navTitleDesc.setText("Get assistance and FAQs");
                break;
            case "LicenseDetailFragment":
                navTitle.setText("License Detail");
                navTitleDesc.setText("");
                break;
            case "ViewApplicationsFragment":
                navTitle.setText("My Applications");
                navTitleDesc.setText("Check your applications");
                break;
            case "ContractFragment":
                navTitle.setText("Contracts");
                navTitleDesc.setText("Check your approved contracts");
                break;

            default:
                navTitle.setText("");
                navTitleDesc.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
        if (current != null) {
            String tag = current.getClass().getSimpleName();
            updateNavState(tag);
            updateNavTitle(tag);
        }
    }

    // Callback from NotificationsFragment
    @Override
    public void onNotificationsLoaded(List<Notification> notifications) {
        updateBadge(notifications);
    }

    //Fetch notifications from API to update badge
    private void fetchUnreadNotifications() {
        NotificationApiService apiService = RetrofitClient.getInstance(this)
                .create(NotificationApiService.class);

        apiService.getNotifications().enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateBadge(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                // optional: handle failure
            }
        });
    }

    //Update badge count
    private void updateBadge(List<Notification> notifications) {
        int unreadCount = 0;
        for (Notification n : notifications) {
            if (!n.isRead()) unreadCount++;
        }

        if (unreadCount > 0) {
            badge.setVisibility(TextView.VISIBLE);
            badge.setText(String.valueOf(unreadCount));
        } else {
            badge.setVisibility(TextView.GONE);
        }
    }
}
