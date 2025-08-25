package com.example.uvms.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.adapters.PoliciesAdapter;
import com.example.uvms.api.PolicyApiService;
import com.example.uvms.api.PolicyApiService;
import com.example.uvms.api.PolicyApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.Policy;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PoliciesFragment extends Fragment {

    private static final String TAG = "PolicyFragment";

    private RecyclerView policiesRecyclerView;
    private PoliciesAdapter policyAdapter;
    private SearchView searchView;
    private ChipGroup filterChipGroup;
    private List<Policy> policies = new ArrayList<>();

    private String currentQuery = "";
    private String currentCategory = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_policies, container, false);

        policiesRecyclerView = view.findViewById(R.id.policiesRecyclerView);
        searchView = view.findViewById(R.id.searchView);
        filterChipGroup = view.findViewById(R.id.filterChipGroup);

        policiesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        policyAdapter = new PoliciesAdapter(policies);
        policiesRecyclerView.setAdapter(policyAdapter);

        setupSearchAndFilter();
        fetchPolicies();

        return view;
    }

    private void setupSearchAndFilter() {
        // SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                policyAdapter.filter(currentQuery, currentCategory);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                policyAdapter.filter(currentQuery, currentCategory);
                return true;
            }
        });

        // ChipGroup listener
        filterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip selectedChip = group.findViewById(checkedId);
            currentCategory = (selectedChip != null) ? selectedChip.getText().toString() : "All";
            policyAdapter.filter(currentQuery, currentCategory);
        });
    }

    private void fetchPolicies() {
        PolicyApiService service = RetrofitClient.getInstance()
                .create(PolicyApiService.class);

        service.getAllPolicies().enqueue(new Callback<List<Policy>>() {
            @Override
            public void onResponse(@NonNull Call<List<Policy>> call,
                                   @NonNull Response<List<Policy>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    policyAdapter.updateData(response.body());
                    Toast.makeText(requireContext(),
                            "Loaded " + response.body().size() + " policies",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Policies loaded: " + response.body().size());
                } else {
                    Toast.makeText(requireContext(),
                            "Failed to load policies: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error response: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Policy>> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(),
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network failure", t);
            }
        });
    }
}