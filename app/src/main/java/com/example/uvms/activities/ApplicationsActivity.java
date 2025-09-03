package com.example.uvms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class ApplicationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ApplicationAdapter adapter;
    private List<Application> applications = new ArrayList<>();
    private LinearLayout emptyView;
    private TabLayout tabLayout;

    private int vendorId = 1; // to replace with actual logged-in vendor ID dynamically

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applications);

        recyclerView = findViewById(R.id.recyclerApplications);
        emptyView = findViewById(R.id.empty_applications);
        tabLayout = findViewById(R.id.tab_status);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ApplicationAdapter(this, new ArrayList<>(), app -> {
            Intent i = new Intent(ApplicationsActivity.this, ApplicationDetailActivity.class);
            i.putExtra("application_id", app.getApplicationId());
            i.putExtra("tender_name", app.getPlotId()); // Or another field if available
            i.putExtra("submitted", app.getApplicationDate());
            i.putExtra("status", app.getStatus());
            startActivity(i);
        });

        recyclerView.setAdapter(adapter);

        setupTabs();
        fetchApplications(vendorId);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Pending"));
        tabLayout.addTab(tabLayout.newTab().setText("Approved"));
        tabLayout.addTab(tabLayout.newTab().setText("Denied"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterApplications(tab.getText().toString());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterApplications(String status) {
        List<Application> filtered = new ArrayList<>();
        for (Application a : applications) {
            if (status.equalsIgnoreCase("All") || a.getStatus().equalsIgnoreCase(status)) {
                filtered.add(a);
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

    private void fetchApplications(int vendorId) {
        ApplicationApiService apiService = RetrofitClient.getInstance(this)
                .create(ApplicationApiService.class);


        Call<List<Application>> call = apiService.getApplications(vendorId);
        call.enqueue(new Callback<List<Application>>() {
            @Override
            public void onResponse(Call<List<Application>> call, Response<List<Application>> response) {
                if (!isFinishing() && response.body() != null) {
                    applications.clear();
                    applications.addAll(response.body());
                    filterApplications(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString());
                } else {
                    toggleEmptyView(true);
                }
            }

            @Override
            public void onFailure(Call<List<Application>> call, Throwable t) {
                toggleEmptyView(true);
                Toast.makeText(ApplicationsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
