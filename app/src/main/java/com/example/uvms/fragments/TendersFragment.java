package com.example.uvms.fragments;

import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.uvms.R;
import com.example.uvms.adapters.TenderAdapter;
import com.example.uvms.models.Tender;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class TendersFragment extends Fragment {

    private RecyclerView recyclerTenders;
    private TenderAdapter adapter;
    private List<Tender> tenderList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout emptyState, errorState;
    private ProgressBar progressBar;
    private SearchView searchView;
    private ChipGroup chipGroupFilters;
    private MaterialSwitch switchOpenOnly;
    private MaterialButton btnRetry;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tenders, container, false);

        recyclerTenders = view.findViewById(R.id.recyclerTenders);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        emptyState = view.findViewById(R.id.emptyState);
        errorState = view.findViewById(R.id.errorState);
        progressBar = view.findViewById(R.id.progressBar);
        searchView = view.findViewById(R.id.searchView);
        chipGroupFilters = view.findViewById(R.id.chipGroupFilters);
        switchOpenOnly = view.findViewById(R.id.switchOpenOnly);
        btnRetry = view.findViewById(R.id.btnRetry);

        recyclerTenders.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TenderAdapter(getContext(), tenderList);
        recyclerTenders.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadTenders);
        btnRetry.setOnClickListener(v -> loadTenders());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTenders(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTenders(newText);
                return true;
            }
        });

        // Chip filters listener
        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // No need to iterate through chips individually if you just need to refilter
            // The filterTenders method will consider all currently checked chips
            filterTenders(searchView.getQuery().toString());
        });
        // Allow multiple chips to be selected
        chipGroupFilters.setSelectionRequired(false);

        switchOpenOnly.setOnCheckedChangeListener((buttonView, isChecked) ->
                filterTenders(searchView.getQuery().toString()));

        loadTenders(); // Load hardcoded data for preview

        return view;
    }

    private void loadTenders() {
        progressBar.setVisibility(View.VISIBLE);
        swipeRefresh.setRefreshing(false);
        tenderList.clear();

        // Hardcoded tenders
        tenderList.add(new Tender("TNDR-001", "Supply and Delivery of ICT Equipment", "Ministry of Education", "CIVE",
                "Dodoma", "Tue, 2 Sep 2025 • 16:00 EAT", "Open", "https://example.com/doc1.pdf"));
        tenderList.add(new Tender("TNDR-002", "Construction of New Library", "City Council", "CNMS",
                "Dar es Salaam", "Mon, 15 Sep 2025 • 12:00 EAT", "Open", "https://example.com/doc2.pdf"));
        tenderList.add(new Tender("TNDR-003", "IT Consultancy Services", "Private Company", "COBE",
                "Arusha", "Fri, 5 Oct 2025 • 17:00 EAT", "Closed", "https://example.com/doc3.pdf"));

        progressBar.setVisibility(View.GONE);
        updateUI();
    }

    private void updateUI() {
        if (adapter.tenderList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerTenders.setVisibility(View.GONE);
            errorState.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerTenders.setVisibility(View.VISIBLE);
            errorState.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    private void filterTenders(String query) {
        List<Tender> filtered = new ArrayList<>();
        boolean openOnly = switchOpenOnly.isChecked();

        for (Tender t : tenderList) {
            boolean matchesQuery = TextUtils.isEmpty(query) ||
                    t.title.toLowerCase().contains(query.toLowerCase()) ||
                    t.buyer.toLowerCase().contains(query.toLowerCase()) ||
                    t.id.toLowerCase().contains(query.toLowerCase());

            boolean matchesChip = false; // Default to false if no chips are selected, or true if selection is not required
            List<Integer> checkedChipIds = chipGroupFilters.getCheckedChipIds();

            if (checkedChipIds.isEmpty()) {
                matchesChip = true; // If no chips are selected, all categories match
            } else {
                for (Integer chipId : checkedChipIds) {
                    Chip chip = chipGroupFilters.findViewById(chipId);
                    if (chip != null && t.category.equalsIgnoreCase(chip.getText().toString())) {
                        matchesChip = true;
                        break; // Match found, no need to check other selected chips for this tender
                    }
                }
            }
            for (int i = 0; i < chipGroupFilters.getChildCount(); i++) {

            boolean matchesOpen = !openOnly || t.status.equalsIgnoreCase("Open");

            if (matchesQuery && matchesChip && matchesOpen) {
                filtered.add(t);
            }
        }

        adapter.tenderList = filtered;
        updateUI();
    }
}}
