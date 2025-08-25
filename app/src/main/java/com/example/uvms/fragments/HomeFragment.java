package com.example.uvms.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.ImageView;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Animatable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        recyclerLicense = view.findViewById(R.id.recyclerLicense);
        quickActionsGrid = view.findViewById(R.id.quickActionsGrid);
        loader=view.findViewById(R.id.loaderView);


        // Setup license recycler view (horizontal layout)
        recyclerLicense.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        // Initialize adapter with empty list
        licenseAdapter = new LicenseAdapter(getContext(), new ArrayList<>());
        recyclerLicense.setAdapter(licenseAdapter);

        // Setup quick actions
        setupQuickActions();

        // Fetch data from API
        fetchLicensesFromApi();

        return view;
    }

    /**
     * Adds quick action cards to the grid
     */
    private void setupQuickActions() {
        QuickAction[] quickActions = {
                new QuickAction("View Policies", getString(R.string.desc_view_policies), R.drawable.ic_policy_view),
                new QuickAction("My Profile", getString(R.string.desc_my_profile), R.drawable.ic_person),
                new QuickAction("Documents", getString(R.string.desc_documents), R.drawable.ic_document),
                new QuickAction("Tenders", getString(R.string.desc_tenders), R.drawable.ic_tenders),
                new QuickAction("Settings", getString(R.string.desc_settings), R.drawable.ic_maintanance),
                new QuickAction("Help & Support", getString(R.string.desc_help_support), R.drawable.ic_help)
        };

        for (QuickAction action : quickActions) {
            View card = createActionCard(action);
            quickActionsGrid.addView(card);
        }
    }

    /**
     * Creates a single quick action card
     */
    private View createActionCard(QuickAction action) {
        View card = LayoutInflater.from(getContext())
                .inflate(R.layout.item_quick_action, quickActionsGrid, false);

        TextView title = card.findViewById(R.id.actionTitle);
        TextView subtitle = card.findViewById(R.id.actionSubtitle);
        ImageView icon = card.findViewById(R.id.actionIcon);

        // Bind data
        title.setText(action.getTitle());
        subtitle.setText(action.getSubtitle());
        icon.setImageResource(action.getIconRes());

        // Handle click
        card.setOnClickListener(v -> handleActionClick(action.getTitle()));

        return card;
    }

    /**
     * Maps quick action titles to corresponding fragments
     */
    private Fragment getFragmentForAction(String actionTitle) {
        Map<String, Fragment> actionMap = new HashMap<>();
        actionMap.put("View Policies", new PoliciesFragment());
        actionMap.put("My Profile", new ProfileFragment());
        actionMap.put("Documents", new DocumentFragment());
        actionMap.put("Tenders", new TendersFragment());
        actionMap.put("Settings", new SettingsFragment());
        actionMap.put("Help & Support", new HelpFragment());

        return actionMap.get(actionTitle);
    }


     //* Handles quick action click

    private void handleActionClick(String actionTitle) {
        Fragment fragment = getFragmentForAction(actionTitle);

        if (fragment != null && getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, fragment)
                    .commit();
        } else {
            Toast.makeText(getContext(),
                    "No fragment found for: " + actionTitle,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetch licenses from API and update UI
     */
    private void fetchLicensesFromApi() {
        loader.setVisibility(View.VISIBLE);
        Drawable d=loader.getDrawable();
        if(d instanceof Animatable)
            ((Animatable)d).start();

        LicenseApiService licenseApiService =
                RetrofitClient.getInstance().create(LicenseApiService.class);


        Call<List<License>> call = licenseApiService.getLicenses();

        call.enqueue(new Callback<List<License>>() {


            @Override
            public void onResponse(Call<List<License>> call, Response<List<License>> response) {
                loader.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<License> licenses = response.body();
                    licenseAdapter.updateData(licenses);

                    Log.d("API_RESPONSE", "Licenses loaded: " + licenses.size());
                } else {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("ERROR")
                            .setMessage("Failed to load your Licences")
                            .setPositiveButton("Retry", (dialog, which) -> fetchLicensesFromApi())
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                                if (getActivity() != null) {
                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.mainContainer, new HomeFragment())
                                            .commit();
                                }
                            })
                            .setCancelable(false)
                            .show();

                    Toast.makeText(getContext(),
                            "Failed to load licenses",
                            Toast.LENGTH_SHORT).show();

                    if (response.body() != null) {
                        Log.d("API_RESPONSE", "Licenses (failed): " + response.body().size());
                    } else {
                        Log.d("API_RESPONSE", "Licenses response body is NULL");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<License>> call, Throwable t) {
                loader.setVisibility(View.VISIBLE);

                new AlertDialog.Builder(requireContext())
                        .setTitle("ERROR")
                        .setMessage("Network Error/Server Error")
                        .setPositiveButton("Retry", (dialog, which) -> fetchLicensesFromApi())
                        .setNegativeButton("Cancel", (dialog, which) -> {
                                    dialog.dismiss();
                                })
                        .setCancelable(false)
                        .show();

                Log.e("API_ERROR", "Error: " + t.getMessage(), t);
            }
        });
    }

    /**
     * Model for quick action cards
     */
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
