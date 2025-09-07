package com.example.uvms.fragments;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.activities.HomeActivity;
import com.example.uvms.activities.ProfileActivity;
import com.example.uvms.adapters.LicenseAdapter;
import com.example.uvms.api.LicenseApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.License;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ImageView loader;
    private RecyclerView recyclerLicense;
    private LicenseAdapter licenseAdapter;
    private GridLayout quickActionsGrid;
    private CardView failedCard;
    private LinearLayout licenseStatusContainer;
    private View activeQuickActionCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        loader = view.findViewById(R.id.loaderView);
        recyclerLicense = view.findViewById(R.id.recyclerLicense);
        quickActionsGrid = view.findViewById(R.id.quickActionsGrid);
        failedCard = view.findViewById(R.id.license_card_failed);
        licenseStatusContainer = view.findViewById(R.id.license_container);

        recyclerLicense.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        licenseAdapter = new LicenseAdapter(requireContext(), new ArrayList<>(), license -> {
            if (!isAdded()) return;
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, LicenseDetailFragment.newInstance(license))
                    .addToBackStack(LicenseDetailFragment.class.getSimpleName())
                    .setReorderingAllowed(true)
                    .commit();
        });
        recyclerLicense.setAdapter(licenseAdapter);

        setupQuickActions();
        fetchLicensesFromApi();

        return view;
    }

    private void setupQuickActions() {
        QuickAction[] quickActions = {
                new QuickAction("View Policies", getString(R.string.desc_view_policies), R.drawable.ic_policy_view),
                new QuickAction("Contracts", getString(R.string.desc_my_profile), R.drawable.ic_contract),
                new QuickAction("My Profile", getString(R.string.desc_my_profile_two), R.drawable.ic_person),
                new QuickAction("Tenders", getString(R.string.desc_tenders), R.drawable.ic_tenders),
                new QuickAction("Settings", getString(R.string.desc_settings), R.drawable.ic_maintanance),
                new QuickAction("Help & Support", getString(R.string.desc_help_support), R.drawable.ic_help)
        };

        for (QuickAction action : quickActions) {
            View card = LayoutInflater.from(getContext()).inflate(R.layout.item_quick_action, quickActionsGrid, false);

            ((TextView) card.findViewById(R.id.actionTitle)).setText(action.getTitle());
            ((TextView) card.findViewById(R.id.actionSubtitle)).setText(action.getSubtitle());
            ((ImageView) card.findViewById(R.id.actionIcon)).setImageResource(action.getIconRes());

            card.setOnClickListener(v -> handleActionClick(action.getTitle(), card));
            quickActionsGrid.addView(card);
        }
    }

    private void handleActionClick(String actionTitle, View clickedCard) {
        Map<String, Fragment> actionMap = new HashMap<>();
        actionMap.put("View Policies", new PoliciesFragment());
        actionMap.put("Contracts", new ContractFragment());
        actionMap.put("Tenders", new TendersFragment());
        actionMap.put("Settings", new SettingsFragment());
        actionMap.put("Help & Support", new HelpFragment());

        if ("My Profile".equals(actionTitle)) {
            startActivity(new Intent(requireContext(), ProfileActivity.class));
            return;
        }

        Fragment fragment = actionMap.get(actionTitle);
        if (fragment != null && isAdded()) {
            Fragment current = getParentFragmentManager().findFragmentById(R.id.mainContainer);
            if (current == null || !current.getClass().equals(fragment.getClass())) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, fragment)
                        .addToBackStack(fragment.getClass().getSimpleName())
                        .setReorderingAllowed(true)
                        .commit();
                highlightQuickAction(clickedCard);

                if (getActivity() instanceof HomeActivity) {
                    ((HomeActivity) getActivity()).syncUIWithCurrentFragment();
                }
            }
        }
    }

    private void highlightQuickAction(View clickedCard) {
        if (activeQuickActionCard != null) {
            activeQuickActionCard.setBackgroundResource(R.drawable.bg_quick_action_normal);
        }
        clickedCard.setBackgroundResource(R.drawable.bg_quick_action_active);
        activeQuickActionCard = clickedCard;
    }

    private void fetchLicensesFromApi() {
        loader.setVisibility(View.VISIBLE);
        Drawable d = loader.getDrawable();
        if (d instanceof Animatable) ((Animatable) d).start();

        LicenseApiService apiService = RetrofitClient.getInstance(requireContext()).create(LicenseApiService.class);
        apiService.getLicenses().enqueue(new Callback<List<License>>() {
            @Override
            public void onResponse(Call<List<License>> call, Response<List<License>> response) {
                loader.setVisibility(View.GONE);
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<License> allLicenses = response.body();

                    // ✅ Get logged-in email from SharedPreferences
                    String loggedInEmail = requireContext()
                            .getSharedPreferences("uvms_prefs", getContext().MODE_PRIVATE)
                            .getString("user_email", null);

                    // Filter licenses for this user
                    List<License> myLicenses = new ArrayList<>();
                    if (loggedInEmail != null) {
                        for (License license : allLicenses) {
                            if (license.getVendor() != null &&
                                    loggedInEmail.equalsIgnoreCase(license.getVendor().getEmail())) {
                                myLicenses.add(license);
                            }
                        }
                    }

                    if (!myLicenses.isEmpty()) {
                        failedCard.setVisibility(View.GONE);
                        licenseAdapter.updateData(myLicenses);
                        updateLicenseStatusCards(myLicenses);
                    } else {
                        failedCard.setVisibility(View.VISIBLE);
                        updateLicenseStatusCards(null);
                        Toast.makeText(requireContext(), "No licenses found for your account", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    failedCard.setVisibility(View.VISIBLE);
                    updateLicenseStatusCards(null);
                }
            }

            @Override
            public void onFailure(Call<List<License>> call, Throwable t) {
                loader.setVisibility(View.GONE);
                failedCard.setVisibility(View.VISIBLE);
                updateLicenseStatusCards(null);
            }
        });
    }

    private void updateLicenseStatusCards(List<License> licenses) {
        licenseStatusContainer.removeAllViews();
        Map<String, Integer> statusSummary = new HashMap<>();
        statusSummary.put("ACTIVE", 0);
        statusSummary.put("PENDING", 0);
        statusSummary.put("REJECTED", 0);
        statusSummary.put("EXPIRED", 0);

        if (licenses != null) {
            for (License l : licenses) {
                String status = l.getStatus() != null ? l.getStatus().toUpperCase() : "EXPIRED";
                statusSummary.put(status, statusSummary.getOrDefault(status, 0) + 1);
            }
        }

        for (Map.Entry<String, Integer> entry : statusSummary.entrySet()) {
            if (!isAdded()) return;

            View card = LayoutInflater.from(getContext()).inflate(R.layout.item_status_card, licenseStatusContainer, false);
            ((TextView) card.findViewById(R.id.statusCount)).setText(String.valueOf(entry.getValue()));
            ((TextView) card.findViewById(R.id.statusLabel)).setText(entry.getKey());

            int color = requireContext().getColor(R.color.gray);
            if ("ACTIVE".equals(entry.getKey())) color = requireContext().getColor(R.color.green);
            else if ("PENDING".equals(entry.getKey())) color = requireContext().getColor(R.color.yellow);
            else if ("REJECTED".equals(entry.getKey())) color = requireContext().getColor(R.color.red);

            Drawable bg = card.getBackground();
            if (bg instanceof GradientDrawable) ((GradientDrawable) bg).setColor(color);

            licenseStatusContainer.addView(card);
        }
    }

    private static class QuickAction {
        private final String title, subtitle;
        private final int iconRes;

        QuickAction(String title, String subtitle, int iconRes) {
            this.title = title;
            this.subtitle = subtitle;
            this.iconRes = iconRes;
        }

        String getTitle() { return title; }
        String getSubtitle() { return subtitle; }
        int getIconRes() { return iconRes; }
    }
}
