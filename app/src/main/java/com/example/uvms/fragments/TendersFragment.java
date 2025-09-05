package com.example.uvms.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.SearchView;
import com.example.uvms.R;
import com.example.uvms.adapters.TenderAdapter;
import com.example.uvms.api.TenderApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.Tender;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    private MaterialButton btnRetry;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ChipGroup chipGroupColleges;
    private SearchView searchView;
    private TenderApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tenders, container, false);

        recyclerView = view.findViewById(R.id.recyclerTenders);
        progressBar = view.findViewById(R.id.progressBar);
        emptyState = view.findViewById(R.id.emptyState);
        errorState = view.findViewById(R.id.errorState);
        btnRetry = view.findViewById(R.id.btnRetry);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        chipGroupColleges = view.findViewById(R.id.chipGroupFilters);
        searchView = view.findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TenderAdapter(getContext(), filteredTenderList);
        recyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getTenderService(requireContext());

        fetchTenders();

        btnRetry.setOnClickListener(v -> fetchTenders());
        swipeRefreshLayout.setOnRefreshListener(() -> {
            progressBar.setVisibility(View.GONE); // Ensure progress bar is hidden on swipe refresh
            fetchTenders();
        });

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

    private void fetchTenders() {
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);
        if (!swipeRefreshLayout.isRefreshing()) { // Only show progress bar if not swipe refreshing
            progressBar.setVisibility(View.VISIBLE);
        }

        apiService.getAllTenders().enqueue(new Callback<List<Tender>>() {
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
                }
            }

            @Override
            public void onFailure(Call<List<Tender>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                errorState.setVisibility(View.VISIBLE);
                chipGroupColleges.removeAllViews();
            }
        });
    }

    private void populateCollegeChips(List<Tender> tenders) {
        chipGroupColleges.removeAllViews();
        if (tenders == null || tenders.isEmpty()) return;

        List<String> collegeNames = new ArrayList<>();
        for (Tender tender : tenders) {
            if (tender.getCollegeName() != null && !collegeNames.contains(tender.getCollegeName())) {
                collegeNames.add(tender.getCollegeName());
            }
        }

        for (String collegeName : collegeNames) {
            Chip chip = new Chip(getContext());
            chip.setText(collegeName);
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
                    (tender.getCollegeName() != null && tender.getCollegeName().toLowerCase().contains(query)) ||
                    (tender.getCreatedBy() != null && tender.getCreatedBy().getName().toLowerCase().contains(query));

            boolean matchesCollege = selectedColleges.isEmpty() || selectedColleges.contains(tender.getCollegeName());

            if (matchesSearch && matchesCollege) {
                filteredTenderList.add(tender);
            }
        }

        adapter.updateList(filteredTenderList);
        emptyState.setVisibility(filteredTenderList.isEmpty() && !allTendersList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
