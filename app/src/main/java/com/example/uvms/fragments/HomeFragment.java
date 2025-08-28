package com.example.uvms.fragments;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.adapters.LicenseAdapter;
import com.example.uvms.api.CollegeApiService;
import com.example.uvms.api.LicenseApiService;
import com.example.uvms.api.PlotApiService;
import com.example.uvms.api.TenderApiService;
import com.example.uvms.api.VendorApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.College;
import com.example.uvms.models.License;
import com.example.uvms.models.Plot;
import com.example.uvms.models.Tender;
import com.example.uvms.models.Vendor;

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

        recyclerLicense = view.findViewById(R.id.recyclerLicense);
        quickActionsGrid = view.findViewById(R.id.quickActionsGrid);
        loader = view.findViewById(R.id.loaderView);

        // --- Setup RecyclerView ---
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

        // --- Setup Quick Actions ---
        setupQuickActions();

        // --- Fetch licenses from API ---
        fetchLicensesWithDetails();

        return view;
    }

    /** Quick Actions Setup */
    private void setupQuickActions() {
        QuickAction[] quickActions = {
                new QuickAction("View Policies", getString(R.string.desc_view_policies), R.drawable.ic_policy_view),
                new QuickAction("Contracts", getString(R.string.desc_my_profile), R.drawable.ic_person),
                new QuickAction("My Applications", getString(R.string.desc_documents), R.drawable.ic_document),
                new QuickAction("Tenders", getString(R.string.desc_tenders), R.drawable.ic_tenders),
                new QuickAction("Settings", getString(R.string.desc_settings), R.drawable.ic_maintanance),
                new QuickAction("Help & Support", getString(R.string.desc_help_support), R.drawable.ic_help)
        };

        for (QuickAction action : quickActions) {
            View card = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_quick_action, quickActionsGrid, false);

            ImageView icon = card.findViewById(R.id.actionIcon);
            TextView title = card.findViewById(R.id.actionTitle);
            TextView subtitle = card.findViewById(R.id.actionSubtitle);

            icon.setImageResource(action.getIconRes());
            icon.setScaleType(ScaleType.CENTER_INSIDE);
            title.setText(action.getTitle());
            subtitle.setText(action.getSubtitle());

            card.setOnClickListener(v -> handleActionClick(action.getTitle()));

            quickActionsGrid.addView(card);
        }
    }

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
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, fragment)
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();
        } else if (isAdded()) {
            Toast.makeText(requireContext(), "No fragment found for: " + actionTitle, Toast.LENGTH_SHORT).show();
        }
    }

    /** Fetch Licenses + Related Entities */
    private void fetchLicensesWithDetails() {
        showLoader(true);

        LicenseApiService apiService = RetrofitClient.getInstance().create(LicenseApiService.class);
        apiService.getLicenses().enqueue(new Callback<List<License>>() {
            @Override
            public void onResponse(Call<List<License>> call, Response<List<License>> response) {
                showLoader(false);
                if (!isAdded()) return;

                List<License> licenses = response.body();
                if (licenses != null && !licenses.isEmpty()) {
                    for (License license : licenses) {
                        fetchVendor(license);
                        fetchCollege(license);
                        fetchTender(license);
                        fetchPlot(license);
                    }
                    licenseAdapter.updateData(licenses);
                } else {
                    Toast.makeText(requireContext(), "No licenses found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<License>> call, Throwable t) {
                showLoader(false);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
                Log.e("API_ERROR", t.getMessage(), t);
            }
        });
    }

    private void showLoader(boolean show) {
        if (loader != null) {
            loader.setVisibility(show ? View.VISIBLE : View.GONE);
            Drawable d = loader.getDrawable();
            if (d instanceof Animatable) {
                if (show) ((Animatable) d).start();
                else ((Animatable) d).stop();
            }
        }
    }

    // --- Fetch Related Entities ---
    private void fetchVendor(License license) {
        VendorApiService api = RetrofitClient.getInstance().create(VendorApiService.class);
        api.getVendorById(license.getVendorId()).enqueue(new Callback<Vendor>() {
            @Override
            public void onResponse(Call<Vendor> call, Response<Vendor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    license.setVendor(response.body());
                    licenseAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<Vendor> call, Throwable t) {}
        });
    }

    private void fetchCollege(License license) {
        CollegeApiService api = RetrofitClient.getInstance().create(CollegeApiService.class);
        api.getCollegeById(license.getApplicationId()).enqueue(new Callback<College>() {
            @Override
            public void onResponse(Call<College> call, Response<College> response) {
                if (response.isSuccessful() && response.body() != null) {
                    license.setCollege(response.body());
                    licenseAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<College> call, Throwable t) {}
        });
    }

    private void fetchTender(License license) {
        TenderApiService api = RetrofitClient.getInstance().create(TenderApiService.class);
        api.getTenderById(license.getApplicationId()).enqueue(new Callback<Tender>() {
            @Override
            public void onResponse(Call<Tender> call, Response<Tender> response) {
                if (response.isSuccessful() && response.body() != null) {
                    license.setTender(response.body());
                    licenseAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<Tender> call, Throwable t) {}
        });
    }

    private void fetchPlot(License license) {
        PlotApiService api = RetrofitClient.getInstance().create(PlotApiService.class);
        api.getPlotByTenderId(license.getApplicationId()).enqueue(new Callback<Plot>() {
            @Override
            public void onResponse(Call<Plot> call, Response<Plot> response) {
                if (response.isSuccessful() && response.body() != null) {
                    license.setPlot(response.body());
                    licenseAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<Plot> call, Throwable t) {}
        });
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
