package com.example.uvms.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.uvms.fragments.*;
import com.example.uvms.models.Notification;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView navTitle, navTitleDesc, notificationBadge;
    private ImageView notificationBell;
    private boolean suppressNavCallback = false;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "uvms_prefs";

    private NotificationApiService notificationApiService;

    // Fragment titles & descriptions for all fragments
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

    // Bottom nav mapping
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

        // Retrofit for Notifications
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://uvmsapiv1.onrender.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        notificationApiService = retrofit.create(NotificationApiService.class);

        // Load default fragment
        if (savedInstanceState == null) {
            switchFragment(new HomeFragment(), false);
        }

        setupBottomNavigation();

        // Sync UI when back stack changes
        getSupportFragmentManager().addOnBackStackChangedListener(this::syncUIWithCurrentFragment);

        // Click bell to open notifications
        notificationBell.setOnClickListener(v -> switchFragment(new NotificationsFragment(), true));

        // Update notification badge
        updateNotificationBadgeFromApi();
    }

    /** ---------------- Fragment Switching ---------------- */
    public void switchFragment(Fragment fragment, boolean addToBackStack) {
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();

        // Hide all existing fragments
        for (Fragment f : getSupportFragmentManager().getFragments()) {
            tx.hide(f);
        }

        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (existingFragment == null) {
            tx.add(R.id.mainContainer, fragment, tag);
        } else {
            tx.show(existingFragment);
        }

        if (addToBackStack && !bottomNavMap.containsValue(fragment.getClass())) {
            tx.addToBackStack(tag);
        }

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
                    switchFragment(fragment, false);
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

        updateNavTitle(current);
        highlightBottomNav(current);
    }

    /** ---------------- Update Titles ---------------- */
    private void updateNavTitle(Fragment fragment) {
        String key = fragment.getTag() != null ? fragment.getTag() : fragment.getClass().getSimpleName();
        String[] titleDesc = fragmentTitles.get(key);

        if (titleDesc != null) {
            navTitle.setText(titleDesc[0]);
            navTitleDesc.setText(titleDesc[1]);
        } else {
            navTitle.setText("");
            navTitleDesc.setText("");
        }
    }

    /** ---------------- Highlight Bottom Nav ---------------- */
    private void highlightBottomNav(Fragment fragment) {
        suppressNavCallback = true;
        try {
            boolean isBottom = bottomNavMap.containsValue(fragment.getClass());
            bottomNavigationView.setVisibility(isBottom ? View.VISIBLE : View.GONE);

            for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
                bottomNavigationView.getMenu().getItem(i).setChecked(false);
            }

            if (isBottom) {
                for (Map.Entry<Integer, Class<? extends Fragment>> entry : bottomNavMap.entrySet()) {
                    if (entry.getValue().equals(fragment.getClass())) {
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
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
            if (current != null && !(current instanceof HomeFragment)) {
                switchFragment(new HomeFragment(), false);
            } else {
                super.onBackPressed();
            }
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
