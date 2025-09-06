package com.example.uvms.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.uvms.R;
import com.example.uvms.adapters.PoliciesAdapter;
import com.example.uvms.api.PoliciesApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.Policy;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PoliciesFragment extends Fragment {

    private RecyclerView recyclerView;
    private PoliciesAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private LinearLayout emptyStateLayout;
    private TextView emptyMessage;
    private Button btnRetry;
    private SearchView searchView;
    private ChipGroup filterChipGroup;

    private String currentQuery = "";
    private String currentCategory = "All";

    public PoliciesFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_policies, container, false);

        recyclerView = view.findViewById(R.id.policiesRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        emptyMessage = view.findViewById(R.id.emptyMessage);
        btnRetry = view.findViewById(R.id.btnRetry);
        searchView = view.findViewById(R.id.searchView);
        filterChipGroup = view.findViewById(R.id.filterChipGroup);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PoliciesAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::fetchPolicies);
        btnRetry.setOnClickListener(v -> fetchPolicies());
        setupSearchAndFilter();

        fetchPolicies();

        return view;
    }

    private void setupSearchAndFilter() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                adapter.filter(currentQuery, currentCategory);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                adapter.filter(currentQuery, currentCategory);
                return true;
            }
        });

        filterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip selectedChip = group.findViewById(checkedId);
            currentCategory = (selectedChip != null) ? selectedChip.getText().toString() : "All";
            adapter.filter(currentQuery, currentCategory);
        });
    }

    private void fetchPolicies() {
        showLoading();

        if (!isNetworkAvailable()) {
            swipeRefreshLayout.setRefreshing(false);
            showRetryState("You’re offline. Please check your internet connection.");
            return;
        }

        PoliciesApiService service = RetrofitClient.getInstance(requireContext())
                .create(PoliciesApiService.class);

        service.getAllPolicies().enqueue(new Callback<List<Policy>>() {
            @Override
            public void onResponse(@NonNull Call<List<Policy>> call, @NonNull Response<List<Policy>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Policy> policies = response.body();
                    if (policies.isEmpty()) {
                        showEmptyState("No policies available at the moment.");
                    } else {
                        adapter.updateData(policies);
                        hideEmptyState();
                    }
                } else {
                    showRetryState("We couldn’t load the policies. Please try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Policy>> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);

                // Log for debugging but don’t show raw exception to user
                t.printStackTrace();

                String message;
                if (t instanceof SocketTimeoutException) {
                    message = "The request timed out. Please try again.";
                } else if (t instanceof UnknownHostException) {
                    message = "Unable to connect. Check your internet connection.";
                } else {
                    message = "Something went wrong. Please try again.";
                }

                showRetryState(message);
            }
        });
    }

    private void showLoading() {
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        emptyMessage.setVisibility(View.GONE);
        btnRetry.setVisibility(View.GONE);
    }

    private void showRetryState(String message) {
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        emptyMessage.setVisibility(View.VISIBLE);
        emptyMessage.setText(message);
        btnRetry.setVisibility(View.VISIBLE);
    }

    private void showEmptyState(String message) {
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        emptyMessage.setVisibility(View.VISIBLE);
        emptyMessage.setText(message);
        btnRetry.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }
}
