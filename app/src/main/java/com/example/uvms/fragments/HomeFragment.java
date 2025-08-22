package com.example.uvms.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.adapters.LicenseAdapter;
import com.example.uvms.api.LicenseApiService;
import com.example.uvms.models.License;
import com.example.uvms.R;
import com.example.uvms.clients.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

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

        // Horizontal layout for licenses
        recyclerLicense.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // ✅ Set an empty adapter to avoid RecyclerView warning
        licenseAdapter = new LicenseAdapter(getContext(), new ArrayList<>());
        recyclerLicense.setAdapter(licenseAdapter);

        setupQuickActions();
        fetchLicensesFromApi();

        return view;
    }

    private void setupQuickActions() {
        QuickAction[] quickActions = {
                new QuickAction("View Policies", "University guidelines", R.drawable.ic_policy_view),
                new QuickAction("My Profile", "Update information", R.drawable.ic_person),
                new QuickAction("Documents", "View all documents", R.drawable.ic_document),
                new QuickAction("Notifications", "Check updates", R.drawable.ic_notification),
                new QuickAction("Settings", "App preferences", R.drawable.ic_maintanance),
                new QuickAction("Help & Support", "Get assistance", R.drawable.ic_help)
        };

        for (QuickAction action : quickActions) {
            View card = createActionCard(action);
            quickActionsGrid.addView(card);
        }
    }

    private View createActionCard(QuickAction action) {
        View card = LayoutInflater.from(getContext())
                .inflate(R.layout.item_quick_action, quickActionsGrid, false);

        TextView title = card.findViewById(R.id.actionTitle);
        TextView subtitle = card.findViewById(R.id.actionSubtitle);
        ImageView icon = card.findViewById(R.id.actionIcon);

        icon.setImageResource(action.getIconRes());
        title.setText(action.getTitle());
        subtitle.setText(action.getSubtitle());

        card.setOnClickListener(v -> handleActionClick(action.getTitle()));

        return card;
    }

    private void handleActionClick(String actionTitle) {
        Toast.makeText(getContext(), "Clicked: " + actionTitle, Toast.LENGTH_SHORT).show();
    }

    private void fetchLicensesFromApi() {
        LicenseApiService licenseApiService = RetrofitClient.getInstance().create(LicenseApiService.class);
        Call<List<License>> call = licenseApiService.getLicenses();

        call.enqueue(new Callback<List<License>>() {
            @Override
            public void onResponse(Call<List<License>> call, Response<List<License>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // ✅ Update adapter data
                    licenseAdapter.updateData(response.body());
                } else {
                    Toast.makeText(getContext(), "Failed to load licenses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<License>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class QuickAction {
        private final String title, subtitle;
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
