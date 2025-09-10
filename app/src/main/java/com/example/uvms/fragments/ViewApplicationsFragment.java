package com.example.uvms.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.adapters.ApplicationAdapter;
import com.example.uvms.api.ApplicationApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.Application;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewApplicationsFragment extends Fragment {

    private RecyclerView recyclerApplications;
    private LinearLayout emptyView;
    private TabLayout tabStatus;
    private View progressLoader;

    private ApplicationAdapter adapter;
    private List<Application> allApplications = new ArrayList<>();

    private int loggedInVendorId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewapplications, container, false);

        recyclerApplications = view.findViewById(R.id.recyclerApplications);
        emptyView = view.findViewById(R.id.empty_applications);
        tabStatus = view.findViewById(R.id.tab_status);
        progressLoader = view.findViewById(R.id.progress_loader);

        adapter = new ApplicationAdapter(getContext(), new ArrayList<>());
        recyclerApplications.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerApplications.setAdapter(adapter);

        // --- SAFELY get logged-in vendor ID ---
        SharedPreferences prefs = requireContext().getSharedPreferences("uvms_prefs", Context.MODE_PRIVATE);
        String vendorIdStr = prefs.getString("user_id", "-1"); // read as String
        try {
            loggedInVendorId = Integer.parseInt(vendorIdStr); // parse to int
        } catch (NumberFormatException e) {
            loggedInVendorId = -1; // fallback if parsing fails
        }

        setupTabs();
        fetchApplications();

        return view;
    }

    private void setupTabs() {
        tabStatus.addTab(tabStatus.newTab().setText("ALL"));
        tabStatus.addTab(tabStatus.newTab().setText("PENDING"));
        tabStatus.addTab(tabStatus.newTab().setText("APPROVED"));
        tabStatus.addTab(tabStatus.newTab().setText("REJECTED"));

        tabStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterApplications(tab.getText().toString());
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void fetchApplications() {
        showLoading(true);

        ApplicationApiService api = RetrofitClient.getInstance(requireContext())
                .create(ApplicationApiService.class);

        Call<List<Application>> call = api.getApplications();
        call.enqueue(new Callback<List<Application>>() {
            @Override
            public void onResponse(Call<List<Application>> call, Response<List<Application>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    allApplications = response.body();
                    filterApplications(tabStatus.getTabAt(tabStatus.getSelectedTabPosition()).getText().toString());
                } else {
                    Toast.makeText(getContext(), "Failed to fetch applications", Toast.LENGTH_SHORT).show();
                    showEmpty(true);
                }
            }

            @Override
            public void onFailure(Call<List<Application>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showEmpty(true);
            }
        });
    }

    private void filterApplications(String status) {
        // Filter applications by vendor ID
        List<Application> vendorApplications = allApplications.stream()
                .filter(app -> app.getVendorId() == loggedInVendorId)
                .collect(Collectors.toList());

        // Filter by status if needed
        List<Application> filtered;
        if ("ALL".equalsIgnoreCase(status)) {
            filtered = vendorApplications;
        } else {
            filtered = vendorApplications.stream()
                    .filter(app -> status.equalsIgnoreCase(app.getStatus()))
                    .collect(Collectors.toList());
        }

        adapter.updateList(filtered);
        showEmpty(filtered.isEmpty());
    }

    private void showEmpty(boolean show) {
        emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerApplications.setVisibility(show ? View.GONE : View.VISIBLE);
        progressLoader.setVisibility(View.GONE);
    }

    private void showLoading(boolean loading) {
        progressLoader.setVisibility(loading ? View.VISIBLE : View.GONE);
        recyclerApplications.setVisibility(loading ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }
}
