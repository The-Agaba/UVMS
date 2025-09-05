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
import com.example.uvms.R;
import com.example.uvms.adapters.TenderAdapter;
import com.example.uvms.api.TenderApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.Tender;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TendersFragment extends Fragment {

    private RecyclerView recyclerView;
    private TenderAdapter adapter;
    private List<Tender> tenderList = new ArrayList<>();
    private ProgressBar progressBar;
    private LinearLayout emptyState, errorState;
    private MaterialButton btnRetry;
    private SwipeRefreshLayout swipeRefreshLayout;

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

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TenderAdapter(getContext(), tenderList);
        recyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getTenderService(requireContext());


        fetchTenders();

        btnRetry.setOnClickListener(v -> fetchTenders());
        swipeRefreshLayout.setOnRefreshListener(this::fetchTenders);

        return view;
    }

    private void fetchTenders() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);

        apiService.getAllTenders().enqueue(new Callback<List<Tender>>() {
            @Override
            public void onResponse(Call<List<Tender>> call, Response<List<Tender>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    tenderList = response.body();

                    // Set collegeName from nested object
                    for (Tender t : tenderList) {
                        if (t.getCreatedBy() != null && t.getCreatedBy().getName() != null) {
                            t.setCollegeName(t.getCollegeName()); // or fetch from your nested college object if you have it
                        }
                    }

                    adapter.updateList(tenderList);
                } else {
                    emptyState.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Tender>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                errorState.setVisibility(View.VISIBLE);
            }
        });
    }
}
