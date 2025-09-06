package com.example.uvms.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.adapters.LicenseAdapter;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.License;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContractFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout emptyView;
    private TextView emptyMessage;
    private SearchView searchView;

    private LicenseAdapter adapter;
    private final List<License> licenseList = new ArrayList<>();
    private final List<License> filteredList = new ArrayList<>();

    private int vendorId = 1; // default, can be overridden by arguments

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contract, container, false);

        // Get vendorId from arguments if provided
        if (getArguments() != null) {
            vendorId = getArguments().getInt("vendor_id", 1);
        }

        // Initialize views
        recyclerView = view.findViewById(R.id.rv_contracts);
        emptyView = view.findViewById(R.id.empty_contracts);
        emptyMessage = emptyView.findViewById(R.id.emptyMessage);
        searchView = view.findViewById(R.id.search_contracts);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LicenseAdapter(getContext(), filteredList, license -> {
            // Do nothing on click
        });
        recyclerView.setAdapter(adapter);

        setupSearchView();
        fetchContracts();

        return view;
    }

    /** Setup SearchView for live filtering */
    private void setupSearchView() {
        searchView.setQueryHint(getString(R.string.search_contracts));
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterContracts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContracts(newText);
                return true;
            }
        });
    }

    /** Filter contracts by search query */
    private void filterContracts(String query) {
        filteredList.clear();
        if (query == null || query.isEmpty()) {
            filteredList.addAll(licenseList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (License license : licenseList) {
                if (license.getSafeString(license.getLicenseNumber(), "")
                        .toLowerCase().contains(lowerQuery)) {
                    filteredList.add(license);
                }
            }
        }
        adapter.updateData(filteredList);
        toggleEmptyView(filteredList.isEmpty(), filteredList.isEmpty() ? "No contracts found" : null);
    }

    /** Show/hide empty view with optional message */
    private void toggleEmptyView(boolean isEmpty, @Nullable String message) {
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

        if (isEmpty && message != null) {
            emptyMessage.setText(message);
        }
    }

    /** Fetch contracts from API filtered by vendor ID */
    private void fetchContracts() {
        // Get the API service
        RetrofitClient.getLicenseService(requireContext())
                .getLicenses()
                .enqueue(new Callback<List<License>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<License>> call,
                                           @NonNull Response<List<License>> response) {
                        if (!isAdded()) return;

                        licenseList.clear();

                        if (response.isSuccessful() && response.body() != null) {
                            for (License license : response.body()) {
                                if (license.getVendorId() == vendorId) {
                                    licenseList.add(license);
                                }
                            }

                            filterContracts(searchView.getQuery().toString());

                            // No contracts at all
                            if (licenseList.isEmpty()) {
                                toggleEmptyView(true, "No contracts found");
                            }

                        } else if (response.code() == 401 || response.code() == 403) {
                            toggleEmptyView(true, "Unauthorized. Please login.");
                        } else {
                            toggleEmptyView(true, "Failed to load contracts.");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<License>> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;

                        String message;
                        if (t instanceof SocketTimeoutException) {
                            message = "Request timed out. Please try again.";
                        } else if (t instanceof UnknownHostException) {
                            message = "No internet connection. Please check your network.";
                        } else {
                            message = "Failed to load contracts.";
                        }

                        toggleEmptyView(true, message);
                    }
                });
    }
}
