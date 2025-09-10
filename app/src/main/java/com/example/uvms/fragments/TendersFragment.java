package com.example.uvms.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.uvms.R;
import com.example.uvms.adapters.TenderAdapter;
import com.example.uvms.api.TenderApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.College;
import com.example.uvms.models.Tender;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TendersFragment extends Fragment {

    private RecyclerView recyclerView;
    private TenderAdapter adapter;
    private List<Tender> allTendersList = new ArrayList<>();
    private List<Tender> filteredTenderList = new ArrayList<>();
    private ProgressBar progressBar;
    private LinearLayout emptyState, errorState;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ChipGroup chipGroupColleges;
    private SearchView searchView;
    private TenderApiService apiService;

    private Map<Integer, String> collegeMap = new HashMap<>(); // collegeId -> collegeName

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tenders, container, false);

        recyclerView = view.findViewById(R.id.recyclerTenders);
        progressBar = view.findViewById(R.id.progressBar);
        emptyState = view.findViewById(R.id.emptyState);
        errorState = view.findViewById(R.id.errorState);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        chipGroupColleges = view.findViewById(R.id.chipGroupFilters);
        searchView = view.findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // ❌ old: adapter = new TenderAdapter(getContext(), filteredTenderList, collegeMap);
        adapter = new TenderAdapter(getContext(), filteredTenderList);
        recyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getTenderService(requireContext());

        fetchColleges(); // fetch college names first, then tenders

        swipeRefreshLayout.setOnRefreshListener(this::fetchTenders);

        chipGroupColleges.setOnCheckedStateChangeListener((group, checkedIds) -> filterTenders());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTenders();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTenders();
                return false;
            }
        });

        return view;
    }

    private void fetchColleges() {
        RetrofitClient.getCollegeService(requireContext()).getAllColleges().enqueue(new Callback<List<College>>() {
            @Override
            public void onResponse(Call<List<College>> call, Response<List<College>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (College c : response.body()) {
                        collegeMap.put(c.getCollegeId(), c.getCollegeName());
                        Log.d("CollegeMap", "Loaded colleges: " + collegeMap.toString());

                    }
                    // update adapter with fresh college map
                    adapter.setCollegeMap(collegeMap);
                    fetchTenders();
                } else {
                    showError();
                    Log.e("TendersFragment", "Failed to load colleges: " + response.code());
                    fetchTenders(); // still fetch tenders even if colleges fail
                }
            }

            @Override
            public void onFailure(Call<List<College>> call, Throwable t) {
                showError();
                Log.e("TendersFragment", "Error fetching colleges: " + t.getMessage());
                fetchTenders(); // fallback
            }
        });
    }

    private void fetchTenders() {
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);
        if (!swipeRefreshLayout.isRefreshing()) progressBar.setVisibility(View.VISIBLE);

        apiService.getActiveTenders("ACTIVE").enqueue(new Callback<List<Tender>>() {
            @Override
            public void onResponse(Call<List<Tender>> call, Response<List<Tender>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    allTendersList = response.body();
                    populateCollegeChips(allTendersList);
                    filterTenders();
                } else {
                    allTendersList.clear();
                    filteredTenderList.clear();
                    adapter.updateList(filteredTenderList);
                    emptyState.setVisibility(View.VISIBLE);
                    chipGroupColleges.removeAllViews();
                    Log.e("TendersFragment", "Failed to load tenders: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Tender>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                showError();
                Log.e("TendersFragment", "Error fetching tenders: " + t.getMessage());
            }
        });
    }

    private void populateCollegeChips(List<Tender> tenders) {
        chipGroupColleges.removeAllViews();
        if (tenders.isEmpty()) return;

        HashSet<Integer> uniqueCollegeIds = new HashSet<>();
        for (Tender t : tenders) {
            uniqueCollegeIds.add(t.getCollegeId());
        }

        for (int collegeId : uniqueCollegeIds) {
            Chip chip = new Chip(getContext());
            chip.setText(collegeMap.getOrDefault(collegeId, "N/A"));
            chip.setCheckable(true);
            chipGroupColleges.addView(chip);
        }
    }

    private void filterTenders() {
        String query = searchView.getQuery().toString().trim().toLowerCase();

        List<Integer> checkedChipIds = chipGroupColleges.getCheckedChipIds();
        List<String> selectedColleges = new ArrayList<>();
        for (int id : checkedChipIds) {
            Chip chip = chipGroupColleges.findViewById(id);
            if (chip != null) selectedColleges.add(chip.getText().toString());
        }

        filteredTenderList.clear();
        for (Tender tender : allTendersList) {
            boolean matchesSearch = query.isEmpty() ||
                    (tender.getTitle() != null && tender.getTitle().toLowerCase().contains(query)) ||
                    (tender.getDescription() != null && tender.getDescription().toLowerCase().contains(query));

            String collegeName = collegeMap.getOrDefault(tender.getCollegeId(), "N/A");
            boolean matchesCollege = selectedColleges.isEmpty() || selectedColleges.contains(collegeName);

            if (matchesSearch && matchesCollege) filteredTenderList.add(tender);
        }

        adapter.updateList(filteredTenderList);
        emptyState.setVisibility(filteredTenderList.isEmpty() && !allTendersList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showError() {
        errorState.setVisibility(View.VISIBLE);
        chipGroupColleges.removeAllViews();
    }
}
