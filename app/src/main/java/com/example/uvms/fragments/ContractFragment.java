package com.example.uvms.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.adapters.ContractAdapter;
import com.example.uvms.api.LicenseApiService;
import com.example.uvms.api.PlotApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.License;
import com.example.uvms.models.Plot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContractFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContractAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;

    private LicenseApiService licenseService;
    private PlotApiService plotService;
    private String loggedInEmail;
    private final Map<Integer, Plot> plotMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contract, container, false);

        // Initialize UI
        recyclerView = view.findViewById(R.id.recyclerViewContracts);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize adapter with Activity context for PDF dialogs
        adapter = new ContractAdapter(requireActivity(), new ArrayList<>(), license ->
                Toast.makeText(getContext(),
                        "Clicked: " + license.getLicenseNumber(),
                        Toast.LENGTH_SHORT).show()
        );
        recyclerView.setAdapter(adapter);

        // Get logged-in user email
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("uvms_prefs", Context.MODE_PRIVATE);
        loggedInEmail = prefs.getString("user_email", null);
        Log.d("ContractFragment", "Logged-in email: " + loggedInEmail);

        // Initialize API services
        licenseService = RetrofitClient.getLicenseService(requireContext());
        plotService = RetrofitClient.getPlotService(requireContext());

        // Fetch data
        fetchPlots();

        return view;
    }

    /** Fetch all plots and store in a map for quick lookup */
    private void fetchPlots() {
        progressBar.setVisibility(View.VISIBLE);

        plotService.getAllPlots().enqueue(new Callback<List<Plot>>() {
            @Override
            public void onResponse(@NonNull Call<List<Plot>> call, @NonNull Response<List<Plot>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Plot plot : response.body()) {
                        plotMap.put(plot.getPlotId(), plot);
                    }
                    fetchLicenses(); // Fetch licenses after plots are loaded
                } else {
                    progressBar.setVisibility(View.GONE);
                    showErrorDialog("Failed to load plots. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Plot>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                showErrorDialog("Network error loading plots: " + t.getMessage());
            }
        });
    }

    /** Fetch licenses, filter by user and active status, attach plots */
    private void fetchLicenses() {
        licenseService.getLicenses().enqueue(new Callback<List<License>>() {
            @Override
            public void onResponse(@NonNull Call<List<License>> call, @NonNull Response<List<License>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<License> allLicenses = response.body();

                    // Filter active licenses
                    List<License> activeLicenses = allLicenses.stream()
                            .filter(License::isActive)
                            .collect(Collectors.toList());

                    // Filter licenses belonging to logged-in user
                    List<License> userLicenses = activeLicenses.stream()
                            .filter(l -> l.getVendor() != null && l.getVendor().getEmail() != null)
                            .filter(l -> l.getVendor().getEmail().equalsIgnoreCase(loggedInEmail))
                            .collect(Collectors.toList());

                    // Attach Plot objects to each License's Application
                    for (License license : userLicenses) {
                        if (license.getApplication() != null && license.getApplication().getPlotId() != null) {
                            int plotId = license.getApplication().getPlotId();
                            if (plotMap.containsKey(plotId)) {
                                license.getApplication().setPlot(plotMap.get(plotId));
                            }
                        }
                    }

                    if (userLicenses.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyView.setText(activeLicenses.isEmpty() ? "No active licenses found." : "No licenses found for you.");
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.updateData(userLicenses);
                    }

                } else {
                    showErrorDialog("Failed to load licenses. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<License>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                showErrorDialog("Network or API error: " + t.getMessage());
            }
        });
    }

    /** Show a simple alert dialog for errors */
    private void showErrorDialog(String message) {
        if (getContext() == null) return;

        new AlertDialog.Builder(getContext())
                .setTitle("Error")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
