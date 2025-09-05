package com.example.uvms.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.uvms.R;
import com.example.uvms.api.NotificationApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.fragments.*;
import com.example.uvms.models.Notification;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView navTitle, navTitleDesc, notificationBadge;
    private ImageView notificationBell;
    private boolean suppressNavCallback = false;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "uvms_prefs";

    private NotificationApiService notificationApiService;
    private boolean doubleBackToExitPressedOnce = false;
    private final Handler backPressHandler = new Handler();

    /** Fragment titles & descriptions */
    private final Map<String, String[]> fragmentTitles = new HashMap<String, String[]>() {{
        put(HomeFragment.class.getSimpleName(), new String[]{"Business", "Manage your business here"});
        put(TendersFragment.class.getSimpleName(), new String[]{"Tenders", "Browse available tenders"});
        put(ViewApplicationsFragment.class.getSimpleName(), new String[]{"Applications", "Check your applications"});
        put(NotificationsFragment.class.getSimpleName(), new String[]{"Notifications", "Stay updated with alerts"});
        put(PoliciesFragment.class.getSimpleName(), new String[]{"Policies", "Review business policies"});
        put(SettingsFragment.class.getSimpleName(), new String[]{"Settings", "Customize your preferences"});
        put(HelpFragment.class.getSimpleName(), new String[]{"Help & Support", "Get assistance and FAQs"});
        put(LicenseDetailFragment.class.getSimpleName(), new String[]{"License Detail", ""});
        put(ContractFragment.class.getSimpleName(), new String[]{"Contracts", "Check your approved contracts"});
    }};

    /** Bottom navigation mapping */
    private final Map<Integer, Class<? extends Fragment>> bottomNavMap = new HashMap<Integer, Class<? extends Fragment>>() {{
        put(R.id.businessFragment, HomeFragment.class);
        put(R.id.tendersFragment, TendersFragment.class);
        put(R.id.myApplicationsFragment, ViewApplicationsFragment.class);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        navTitle = findViewById(R.id.navTitle);
        navTitleDesc = findViewById(R.id.navTitle_desc);
        notificationBell = findViewById(R.id.navNotificationIcon);
        notificationBadge = findViewById(R.id.badge);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });

        // Initialize Notification API via RetrofitClient
        notificationApiService = RetrofitClient.getInstance(this).create(NotificationApiService.class);

        // Load default fragment
        if (savedInstanceState == null) {
            switchFragment(new HomeFragment(), true);
        }

        setupBottomNavigation();

        // Sync UI whenever back stack changes
        getSupportFragmentManager().addOnBackStackChangedListener(this::syncUIWithCurrentFragment);

        // Open notifications on bell click
        notificationBell.setOnClickListener(v -> switchFragment(new NotificationsFragment(), true));

        // Load notification badge count
        updateNotificationBadgeFromApi();
    }

    /** ---------------- Fragment Switching ---------------- */
    public void switchFragment(Fragment fragment, boolean addToBackStack) {
        String tag = fragment.getClass().getSimpleName();

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.mainContainer, fragment, tag);

        // Always add bottom nav fragments to back stack
        boolean isBottomNavFragment = bottomNavMap.containsValue(fragment.getClass());
        if (addToBackStack || isBottomNavFragment) {
            tx.addToBackStack(tag);
        }

        tx.setReorderingAllowed(true);
        tx.commit();

        getSupportFragmentManager().executePendingTransactions();
        syncUIWithCurrentFragment();
    }

    /** ---------------- Bottom Navigation ---------------- */
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (suppressNavCallback) return true;

            Class<? extends Fragment> fragClass = bottomNavMap.get(item.getItemId());
            if (fragClass != null) {
                try {
                    Fragment fragment = fragClass.newInstance();
                    switchFragment(fragment, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error opening fragment", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });
    }

    /** ---------------- UI Sync ---------------- */
    public void syncUIWithCurrentFragment() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
        if (current == null) return;

        // Update title & description
        String key = current.getTag() != null ? current.getTag() : current.getClass().getSimpleName();
        String[] titleDesc = fragmentTitles.get(key);
        if (titleDesc != null) {
            navTitle.setText(titleDesc[0]);
            navTitleDesc.setText(titleDesc[1]);
        } else {
            navTitle.setText("");
            navTitleDesc.setText("");
        }

        // Bottom nav highlight
        suppressNavCallback = true;
        try {
            boolean isBottom = bottomNavMap.containsValue(current.getClass());
            bottomNavigationView.setVisibility(isBottom ? View.VISIBLE : View.GONE);

            for (int i = 0; i < bottomNavigationView.getMenu().size(); i++)
                bottomNavigationView.getMenu().getItem(i).setChecked(false);

            if (isBottom) {
                for (Map.Entry<Integer, Class<? extends Fragment>> entry : bottomNavMap.entrySet()) {
                    if (entry.getValue().equals(current.getClass())) {
                        bottomNavigationView.setSelectedItemId(entry.getKey());
                        break;
                    }
                }
            }
        } finally {
            suppressNavCallback = false;
        }
    }

    /** ---------------- Back Press ---------------- */
    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
        boolean isBottom = bottomNavMap.containsValue(current.getClass());

        if (count > 1) {
            getSupportFragmentManager().popBackStack();
        } else if (isBottom) {
            // Double back to exit
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            backPressHandler.postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            // Non-bottom fragment → go to home
            switchFragment(new HomeFragment(), true);
        }

        getSupportFragmentManager().executePendingTransactions();
        syncUIWithCurrentFragment();
    }

    /** ---------------- Notifications ---------------- */
    public NotificationApiService getNotificationApiService() {
        return notificationApiService;
    }

    public void fetchNotifications(OnNotificationsFetched listener) {
        notificationApiService.getNotifications().enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onFetched(response.body());
                } else {
                    listener.onError("No notifications found");
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void updateNotificationBadge(int unreadCount) {
        if (notificationBadge == null) return;
        if (unreadCount > 0) {
            notificationBadge.setVisibility(View.VISIBLE);
            notificationBadge.setText(String.valueOf(unreadCount));
        } else {
            notificationBadge.setVisibility(View.GONE);
        }
    }

    public void updateNotificationBadgeFromApi() {
        fetchNotifications(new OnNotificationsFetched() {
            @Override
            public void onFetched(List<Notification> notifications) {
                int unreadCount = 0;
                for (Notification n : notifications) if (!n.isRead()) unreadCount++;
                updateNotificationBadge(unreadCount);
            }

            @Override
            public void onError(String message) {
                updateNotificationBadge(0);
            }
        });
    }

    public interface OnNotificationsFetched {
        void onFetched(List<Notification> notifications);
        void onError(String message);
    }
}
