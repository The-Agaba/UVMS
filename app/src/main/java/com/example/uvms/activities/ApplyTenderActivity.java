package com.example.uvms.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uvms.R;
import com.example.uvms.api.ApplicationApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.Application;
import com.example.uvms.models.Plot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplyTenderActivity extends AppCompatActivity {

    private TextView tvTenderTitle, tvCollegeName, tvCompanyName, tvContactPerson, tvEmail;
    private Spinner spinnerPlots;
    private Button btnSubmitApplication;

    private int tenderId;
    private List<Plot> plots = new ArrayList<>();
    private ArrayAdapter<String> plotAdapter;

    private int vendorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_tender);

        // 🔹 Views
        tvTenderTitle = findViewById(R.id.tvTenderTitle);
        tvCollegeName = findViewById(R.id.tvCollegeName);
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvContactPerson = findViewById(R.id.tvContactPerson);
        tvEmail = findViewById(R.id.tvEmail);
        spinnerPlots = findViewById(R.id.spinnerPlots);
        btnSubmitApplication = findViewById(R.id.btnSubmitApplication);

        // 🔹 Tender info from Intent
        tenderId = getIntent().getIntExtra("tender_id", -1);
        String tenderTitle = getIntent().getStringExtra("tender_title");
        String collegeName = getIntent().getStringExtra("college_name");
        tvTenderTitle.setText(tenderTitle);
        tvCollegeName.setText(collegeName);

        // 🔹 Vendor info from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("uvms_prefs", Context.MODE_PRIVATE);
        vendorId = Integer.parseInt(prefs.getString("user_id", "-1"));  // Ensure vendor_id was saved during login
        String companyName = prefs.getString("user_company_name", "-");
        String contactPerson = prefs.getString("user_full_name", "-");
        String email = prefs.getString("user_email", "-");

        tvCompanyName.setText(companyName);
        tvContactPerson.setText(contactPerson);
        tvEmail.setText(email);

        // 🔹 Setup spinner adapter
        plotAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        plotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlots.setAdapter(plotAdapter);

        // 🔹 Fetch plots for this tender
        fetchPlots();

        // 🔹 Submit button
        btnSubmitApplication.setOnClickListener(v -> submitApplication());
    }

    private void fetchPlots() {
        RetrofitClient.getPlotService(this).getPlotsByTender(tenderId).enqueue(new Callback<List<Plot>>() {
            @Override
            public void onResponse(Call<List<Plot>> call, Response<List<Plot>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    plots = response.body();
                    List<String> plotStrings = new ArrayList<>();
                    for (Plot p : plots) {
                        plotStrings.add(p.getPlotNumber() + " - " + p.getLocationDescription());
                    }
                    plotAdapter.clear();
                    plotAdapter.addAll(plotStrings);
                    plotAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ApplyTenderActivity.this, "Failed to load plots", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Plot>> call, Throwable t) {
                Toast.makeText(ApplyTenderActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitApplication() {
        if (plots.isEmpty()) {
            Toast.makeText(this, "No plots available", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPosition = spinnerPlots.getSelectedItemPosition();
        if (selectedPosition == -1) {
            Toast.makeText(this, "Please select a plot", Toast.LENGTH_SHORT).show();
            return;
        }

        int plotId = plots.get(selectedPosition).getPlotId();

        // 🔹 Build request body
        Application request = new Application();
        try {
            // we don’t use setters except for plot
            request.setPlot(plots.get(selectedPosition));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("vendor", vendorId);  // from logged-in vendor
        requestBody.put("plot", plotId);      // selected plot

        RetrofitClient.getInstance(this)
                .create(ApplicationApiService.class)
                .createApplication(requestBody)
                .enqueue(new Callback<Application>() {
                    @Override
                    public void onResponse(Call<Application> call, Response<Application> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(ApplyTenderActivity.this, "Application submitted!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ApplyTenderActivity.this, "Failed to submit", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Application> call, Throwable t) {
                        Toast.makeText(ApplyTenderActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
