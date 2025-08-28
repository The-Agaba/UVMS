package com.example.uvms.fragments;

import android.app.AlertDialog;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        loader = view.findViewById(R.id.loaderView);
        recyclerLicense = view.findViewById(R.id.recyclerLicense);
        quickActionsGrid = view.findViewById(R.id.quickActionsGrid);
        failedCard = view.findViewById(R.id.license_card_failed);
        licenseStatusContainer = view.findViewById(R.id.license_container);

        // Setup RecyclerView
        recyclerLicense.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        licenseAdapter = new LicenseAdapter(getContext(), new ArrayList<>(), license -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, LicenseDetailFragment.newInstance(license))
                    .addToBackStack(null)
                    .commit();
        });
        recyclerLicense.setAdapter(licenseAdapter);

        setupQuickActions();
        fetchLicensesFromApi();

        return view;
    }

    /** Quick Actions Setup */
    private void setupQuickActions() {
        QuickAction[] quickActions = {
                new QuickAction("View Policies", getString(R.string.desc_view_policies), R.drawable.ic_policy_view),
                new QuickAction("Contracts", getString(R.string.desc_my_profile), R.drawable.ic_contract),
                new QuickAction("My Applications", getString(R.string.desc_documents), R.drawable.ic_application),
                new QuickAction("Tenders", getString(R.string.desc_tenders), R.drawable.ic_tenders),
                new QuickAction("Settings", getString(R.string.desc_settings), R.drawable.ic_maintanance),
                new QuickAction("Help & Support", getString(R.string.desc_help_support), R.drawable.ic_help)
        };

        for (QuickAction action : quickActions) {
            View card = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_quick_action, quickActionsGrid, false);

            TextView title = card.findViewById(R.id.actionTitle);
            TextView subtitle = card.findViewById(R.id.actionSubtitle);
            ImageView icon = card.findViewById(R.id.actionIcon);

            title.setText(action.getTitle());
            subtitle.setText(action.getSubtitle());
            icon.setImageResource(action.getIconRes());

            card.setOnClickListener(v -> handleActionClick(action.getTitle()));
            quickActionsGrid.addView(card);
        }
    }

    /** Quick Action Navigation */
    private void handleActionClick(String actionTitle) {
        Map<String, Fragment> actionMap = new HashMap<>();
        actionMap.put("View Policies", new PoliciesFragment());
        actionMap.put("Contracts", new ContractFragment());
        actionMap.put("My Applications", new ViewApplicationsFragment());
        actionMap.put("Tenders", new TendersFragment());
        actionMap.put("Settings", new SettingsFragment());
        actionMap.put("Help & Support", new HelpFragment());

        Fragment fragment = actionMap.get(actionTitle);
        if (fragment != null && isAdded()) {
            Fragment currentFragment = getParentFragmentManager().findFragmentById(R.id.mainContainer);
            if (currentFragment == null || !currentFragment.getClass().equals(fragment.getClass())) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, fragment)
                        .addToBackStack(fragment.getClass().getSimpleName())
                        .commit();
            } else {
                Toast.makeText(getContext(), actionTitle + " is already open", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Fetch licenses from API and update RecyclerView + status cards */
    private void fetchLicensesFromApi() {
        loader.setVisibility(View.VISIBLE);
        Drawable d = loader.getDrawable();
        if (d instanceof Animatable) ((Animatable) d).start();

        LicenseApiService apiService = RetrofitClient.getInstance().create(LicenseApiService.class);
        Call<List<License>> call = apiService.getLicenses();

        call.enqueue(new Callback<List<License>>() {
            @Override
            public void onResponse(Call<List<License>> call, Response<List<License>> response) {
                loader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    failedCard.setVisibility(View.GONE);
                    List<License> licenses = response.body();
                    licenseAdapter.updateData(licenses);
                    updateLicenseStatusCards(licenses);
                    Log.d("API_RESPONSE", "Licenses loaded: " + licenses.size());
                } else {
                    failedCard.setVisibility(View.VISIBLE);
                    updateLicenseStatusCards(null); // Show 0s when empty
                    Toast.makeText(getContext(), "No licenses found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<License>> call, Throwable t) {
                loader.setVisibility(View.GONE);
                if (isAdded()) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("⚠️ Error")
                            .setMessage("An error occurred: Please check your connection and try again.")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                }
                failedCard.setVisibility(View.VISIBLE);
                updateLicenseStatusCards(null); // Show 0s on failure
                Log.e("API_ERROR", t.getMessage(), t);
            }
        });
    }

    /** Update License Status Cards dynamically and preserve shape */
    private void updateLicenseStatusCards(List<License> licenses) {
        licenseStatusContainer.removeAllViews();

        Map<String, Integer> statusSummary = new HashMap<>();
        statusSummary.put("ACTIVE", 0);
        statusSummary.put("PENDING", 0);
        statusSummary.put("REJECTED", 0);

        if (licenses != null) {
            for (License license : licenses) {
                String status = license.getStatus();
                statusSummary.put(status, statusSummary.getOrDefault(status, 0) + 1);
            }
        }

        for (Map.Entry<String, Integer> entry : statusSummary.entrySet()) {
            View card = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_status_card, licenseStatusContainer, false);

            TextView count = card.findViewById(R.id.statusCount);
            TextView label = card.findViewById(R.id.statusLabel);

            count.setText(String.valueOf(entry.getValue()));
            label.setText(entry.getKey());

            // Determine color
            int color;
            switch (entry.getKey()) {
                case "ACTIVE": color = getResources().getColor(R.color.green); break;
                case "PENDING": color = getResources().getColor(R.color.yellow); break;
                case "REJECTED": color = getResources().getColor(R.color.red); break;
                default: color = getResources().getColor(R.color.gray);
            }

            // Preserve shape while changing color
            GradientDrawable bg = (GradientDrawable) card.getBackground();
            bg.setColor(color);

            // Spacing
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 10, 10, 10);
            card.setLayoutParams(params);

            licenseStatusContainer.addView(card);
        }
    }

    /** QuickAction Model */
    private static class QuickAction {
        private final String title;
        private final String subtitle;
        private final int iconRes;

        public QuickAction(String title, String subtitle, int iconRes) {
            this.title = title;
            this.subtitle = subtitle;
            this.iconRes = iconRes;
        }

        public String getTitle() { return title; }
        public String getSubtitle() { return subtitle; }
        public int getIconRes() { return iconRes; }
    }
}
