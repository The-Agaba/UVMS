package com.example.uvms.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.adapters.LicenseAdapter;
import com.example.uvms.api.LicenseApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.License;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContractFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout emptyView;
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

        // Read vendor ID from arguments if provided
        if (getArguments() != null) {
            vendorId = getArguments().getInt("vendor_id", 1);
        }

        // Initialize views
        recyclerView = view.findViewById(R.id.rv_contracts);
        emptyView = view.findViewById(R.id.empty_contracts);
        searchView = view.findViewById(R.id.search_contracts);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LicenseAdapter(getContext(), filteredList, license ->
                Toast.makeText(getContext(),
                        "Clicked License #" + license.getLicenseId(),
                        Toast.LENGTH_SHORT).show()
        );
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
        toggleEmptyView(filteredList.isEmpty());
    }

    /** Show/hide empty view */
    private void toggleEmptyView(boolean isEmpty) {
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    /** Fetch contracts from API filtered by vendor ID */
    private void fetchContracts() {
        LicenseApiService apiService = RetrofitClient.getInstance()
                .create(LicenseApiService.class);

        apiService.getLicenses().enqueue(new Callback<List<License>>() {
            @Override
            public void onResponse(Call<List<License>> call, Response<List<License>> response) {
                if (!isAdded()) return;

                licenseList.clear();
                List<License> licenses = response.body();
                if (licenses != null) {
                    for (License license : licenses) {
                        if (license.getVendorId() == vendorId) {
                            licenseList.add(license);
                        }
                    }
                }

                filterContracts(searchView.getQuery().toString());
            }

            @Override
            public void onFailure(Call<List<License>> call, Throwable t) {
                if (isAdded()) {
                    toggleEmptyView(true);
                    Toast.makeText(requireContext(),
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
