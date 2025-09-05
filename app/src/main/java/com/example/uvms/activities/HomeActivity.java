package com.example.uvms.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.uvms.BaseActivity;
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

public class HomeActivity extends BaseActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView navTitle, navTitleDesc, notificationBadge;
    private ImageView notificationBell;
    private boolean suppressNavCallback = false;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "uvms_prefs";

    private NotificationApiService notificationApiService;
    private boolean doubleBackToExitPressedOnce = false;
    private final Handler backPressHandler = new Handler();

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

        notificationApiService = RetrofitClient.getInstance(this).create(NotificationApiService.class);

        if (savedInstanceState == null) {
            switchFragment(new HomeFragment(), true);
        }

        setupBottomNavigation();
        getSupportFragmentManager().addOnBackStackChangedListener(this::syncUIWithCurrentFragment);

        notificationBell.setOnClickListener(v -> switchFragment(new NotificationsFragment(), true));

        // Update notification badge initially
        new Handler().postDelayed(this::updateNotificationBadgeFromApi, 500);
    }

    public void switchFragment(Fragment fragment, boolean addToBackStack) {
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.mainContainer, fragment, tag);

        boolean isBottomNavFragment = bottomNavMap.containsValue(fragment.getClass());
        if (addToBackStack || isBottomNavFragment) tx.addToBackStack(tag);

        tx.setReorderingAllowed(true);
        tx.commit();
        getSupportFragmentManager().executePendingTransactions();
        syncUIWithCurrentFragment();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (suppressNavCallback) return true;
            Class<? extends Fragment> fragClass = bottomNavMap.get(item.getItemId());
            if (fragClass != null) {
                try {
                    Fragment fragment = fragClass.getDeclaredConstructor().newInstance();
                    switchFragment(fragment, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error opening fragment", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });
    }

    public void syncUIWithCurrentFragment() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
        if (current == null) return;

        String key = current.getTag() != null ? current.getTag() : current.getClass().getSimpleName();
        String[] titleDesc = fragmentTitles.get(key);
        navTitle.setText(titleDesc != null ? titleDesc[0] : "");
        navTitleDesc.setText(titleDesc != null ? titleDesc[1] : "");

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

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
        boolean isBottom = bottomNavMap.containsValue(current.getClass());

        if (count > 1) {
            getSupportFragmentManager().popBackStack();
        } else if (isBottom) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            backPressHandler.postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            switchFragment(new HomeFragment(), true);
        }
        getSupportFragmentManager().executePendingTransactions();
        syncUIWithCurrentFragment();
    }

    public NotificationApiService getNotificationApiService() {
        if (notificationApiService == null) {
            notificationApiService = RetrofitClient.getInstance(this).create(NotificationApiService.class);
        }
        return notificationApiService;
    }

    public int getCurrentVendorId() {
        return prefs.getInt("vendor_id", -1);
    }

    public void updateNotificationBadge(int unreadCount) {
        if (notificationBadge == null) return;
        notificationBadge.setVisibility(unreadCount > 0 ? View.VISIBLE : View.GONE);
        if (unreadCount > 0) notificationBadge.setText(String.valueOf(unreadCount));
    }

    public void updateNotificationBadgeFromApi() {
        //int vendorId = getCurrentVendorId();
        int vendorId = 1;

        if (vendorId == -1 || notificationApiService == null) return;

        notificationApiService.getNotificationsForVendor(vendorId).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                int unreadCount = 0;
                if (response.isSuccessful() && response.body() != null) {
                    for (Notification n : response.body()) if (!n.isRead()) unreadCount++;
                }
                updateNotificationBadge(unreadCount);
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                updateNotificationBadge(0);
            }
        });
    }
}
