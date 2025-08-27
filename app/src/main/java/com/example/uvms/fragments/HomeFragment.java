package com.example.uvms.fragments;

import android.app.AlertDialog;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
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
import com.example.uvms.activities.HomeActivity;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        recyclerLicense = view.findViewById(R.id.recyclerLicense);
        quickActionsGrid = view.findViewById(R.id.quickActionsGrid);
        loader = view.findViewById(R.id.loaderView);
        failedCard = view.findViewById(R.id.license_card_failed);

        // Setup RecyclerView
        recyclerLicense.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        licenseAdapter = new LicenseAdapter(getContext(), new ArrayList<>(), license -> {
            // Open LicenseDetailFragment
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, LicenseDetailFragment.newInstance(license))
                    .addToBackStack("LicenseDetailFragment")
                    .commit();

            // Update HomeActivity title/state
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).updateNavTitle("LicenseDetailFragment");
                ((HomeActivity) getActivity()).updateNavState("LicenseDetailFragment");
            }
        });
        recyclerLicense.setAdapter(licenseAdapter);

        // Setup Quick Actions
        setupQuickActions();

        // Fetch licenses from API
        fetchLicensesFromApi();

        return view;
    }

    /** Setup Quick Actions grid */
    private void setupQuickActions() {
        QuickAction[] actions = {
                new QuickAction("View Policies", getString(R.string.desc_view_policies), R.drawable.ic_policy_view),
                new QuickAction("Contracts", getString(R.string.desc_my_profile), R.drawable.ic_person),
                new QuickAction("View Applications", getString(R.string.desc_documents), R.drawable.ic_document),
                new QuickAction("Tenders", getString(R.string.desc_tenders), R.drawable.ic_tenders),
                new QuickAction("Settings", getString(R.string.desc_settings), R.drawable.ic_maintanance),
                new QuickAction("Help & Support", getString(R.string.desc_help_support), R.drawable.ic_help)
        };

        for (QuickAction action : actions) {
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

    private void handleActionClick(String actionTitle) {
        Map<String, Fragment> actionMap = new HashMap<>();
        actionMap.put("View Policies", new PoliciesFragment());
        actionMap.put("Contracts", new ViewApplicationsFragment());
        actionMap.put("View Applications", new ViewApplicationsFragment());
        actionMap.put("Tenders", new TendersFragment());
        actionMap.put("Settings", new SettingsFragment());
        actionMap.put("Help & Support", new HelpFragment());

        Fragment fragment = actionMap.get(actionTitle);
        if (fragment != null && isAdded()) {
            Fragment current = getParentFragmentManager().findFragmentById(R.id.mainContainer);
            if (current == null || !current.getClass().equals(fragment.getClass())) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, fragment)
                        .addToBackStack(fragment.getClass().getSimpleName())
                        .commit();

                // Update HomeActivity title/state
                if (getActivity() instanceof HomeActivity) {
                    ((HomeActivity) getActivity()).updateNavTitle(fragment.getClass().getSimpleName());
                    ((HomeActivity) getActivity()).updateNavState(fragment.getClass().getSimpleName());
                }
            }
        }
    }

    /** Fetch licenses from API */
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
                if (response.isSuccessful() && response.body() != null) {
                    licenseAdapter.updateData(response.body());
                    Log.d("API_RESPONSE", "Licenses loaded: " + response.body().size());
                } else {
                    failedCard.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "No licenses found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<License>> call, Throwable t) {
                loader.setVisibility(View.GONE);
                failedCard.setVisibility(View.VISIBLE);

                new AlertDialog.Builder(getContext())
                        .setTitle("⚠️ Error")
                        .setMessage("An error occurred. Please check your connection and try again.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();

                Log.e("API_ERROR", t.getMessage(), t);
            }
        });
    }

    /** QuickAction model */
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
