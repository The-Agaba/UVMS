package com.example.uvms.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewApplicationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout emptyView;
    private Button btnInviteVendor;
    private TabLayout tabLayout;

    private ApplicationAdapter adapter;
    private List<Application> applicationList = new ArrayList<>();

    private final int vendorId = 1; // Replace with actual logged-in vendor ID

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_viewapplications, container, false);

        recyclerView = view.findViewById(R.id.rv_applications);
        emptyView = view.findViewById(R.id.empty_applications);

        tabLayout = view.findViewById(R.id.tab_status);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ApplicationAdapter(getContext(), applicationList, app ->
                Toast.makeText(getContext(),
                        "Clicked Application ID #" + app.getApplicationId(),
                        Toast.LENGTH_SHORT).show()
        );
        recyclerView.setAdapter(adapter);


        setupTabs();
        fetchApplications();

        return view;
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Pending"));
        tabLayout.addTab(tabLayout.newTab().setText("Approved"));
        tabLayout.addTab(tabLayout.newTab().setText("Denied"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) { filterByStatus(tab.getText().toString()); }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) { filterByStatus(tab.getText().toString()); }
        });
    }

    private void filterByStatus(String status) {
        List<Application> filtered = new ArrayList<>();
        if (status.equalsIgnoreCase("All")) {
            filtered.addAll(applicationList);
        } else {
            for (Application app : applicationList) {
                if (app.getStatus().equalsIgnoreCase(status)) filtered.add(app);
            }
        }
        adapter.updateData(filtered);
        toggleEmptyView(filtered.isEmpty());
    }

    private void toggleEmptyView(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void fetchApplications() {
        ApplicationApiService apiService = RetrofitClient.getInstance()
                .create(ApplicationApiService.class);

        apiService.getApplications(vendorId).enqueue(new Callback<List<Application>>() {
            @Override
            public void onResponse(Call<List<Application>> call, Response<List<Application>> response) {
                if (!isAdded()) return;

                List<Application> apps = response.body();
                if (apps != null && !apps.isEmpty()) {
                    applicationList.clear();
                    applicationList.addAll(apps);
                    filterByStatus(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString());
                } else {
                    toggleEmptyView(true);
                }
            }

            @Override
            public void onFailure(Call<List<Application>> call, Throwable t) {
                if (isAdded()) {
                    toggleEmptyView(true);
                    Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
