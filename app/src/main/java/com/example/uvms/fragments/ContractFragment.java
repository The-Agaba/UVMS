package com.example.uvms.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private Button btnCreateContract;
    private EditText searchEditText;

    private LicenseAdapter adapter;
    private List<License> licenseList = new ArrayList<>();
    private List<License> filteredList = new ArrayList<>();

    private int vendorId = 1; // default, will be overridden by arguments

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contract, container, false);

        // Read vendor ID from arguments
        if (getArguments() != null) {
            vendorId = getArguments().getInt("vendor_id", 1);
        }

        recyclerView = view.findViewById(R.id.rv_contracts);
        emptyView = view.findViewById(R.id.empty_contracts);
        btnCreateContract = view.findViewById(R.id.btn_create_contract);
        searchEditText = view.findViewById(R.id.search_contracts);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LicenseAdapter(getContext(), filteredList, license ->
                Toast.makeText(getContext(),
                        "Clicked License #" + license.getLicenseId(),
                        Toast.LENGTH_SHORT).show()
        );
        recyclerView.setAdapter(adapter);

        btnCreateContract.setOnClickListener(v ->
                Toast.makeText(getContext(), "Create Contract Clicked", Toast.LENGTH_SHORT).show()
        );

        setupSearch();
        fetchContracts();

        return view;
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterContracts(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterContracts(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(licenseList);
        } else {
            for (License l : licenseList) {
                if (l.getSafeString(l.getLicenseNumber(), "").toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(l);
                }
            }
        }
        adapter.updateData(filteredList);
        toggleEmptyView(filteredList.isEmpty());
    }

    private void toggleEmptyView(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void fetchContracts() {
        LicenseApiService apiService = RetrofitClient.getInstance()
                .create(LicenseApiService.class);

        // Fetch licenses filtered by vendor
        apiService.getLicenses().enqueue(new Callback<List<License>>() {
            @Override
            public void onResponse(Call<List<License>> call, Response<List<License>> response) {
                if (!isAdded()) return;

                List<License> licenses = response.body();
                if (licenses != null && !licenses.isEmpty()) {
                    licenseList.clear();
                    for (License license : licenses) {
                        if (license.getVendorId() == vendorId) {
                            licenseList.add(license);
                        }
                    }
                    filterContracts(searchEditText.getText().toString());
                } else {
                    toggleEmptyView(true);
                }
            }

            @Override
            public void onFailure(Call<List<License>> call, Throwable t) {
                if (isAdded()) {
                    toggleEmptyView(true);
                    Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
