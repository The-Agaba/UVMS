package com.example.uvms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uvms.R;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.api.ApiService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ChipGroup chipGroupBusinessType; // if using chips
    private Spinner spinnerBusinessType;      // if using dropdown
    private ApiService apiService;

    private Button registerButton;
    private ImageButton btnBack;
    private TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        registerButton = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);
        loginLink = findViewById(R.id.tvLoginLink);
        spinnerBusinessType = findViewById(R.id.spinnerBusinessType);       // if using spinner

        // Initialize Retrofit API
        apiService = RetrofitClient.getInstance().create(ApiService.class);

        // Load business types
        loadBusinessTypes();

        // Register button click
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Back button click
        btnBack.setOnClickListener(v -> finish());

        // Login link click
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadBusinessTypes() {
        apiService.getBusinessType().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> businessTypes = response.body();
                    // Uncomment one depending on your layout

                    // Option 1: Populate ChipGroup
                    if (chipGroupBusinessType != null) {
                        populateChipGroup(businessTypes);
                    }

                    // Option 2: Populate Spinner
                    if (spinnerBusinessType != null) {
                        populateSpinner(businessTypes);
                    }

                } else {
                    Toast.makeText(RegisterActivity.this, "Failed to load business types", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(RegisterActivity.this, "Error fetching business types", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateChipGroup(List<String> businessTypes) {
        chipGroupBusinessType.removeAllViews();
        for (String type : businessTypes) {
            Chip chip = new Chip(this);
            chip.setText(type);
            chip.setCheckable(true);
            chipGroupBusinessType.addView(chip);
        }
    }

    private void populateSpinner(List<String> businessTypes) {
        // Simple ArrayAdapter for spinner
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                businessTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBusinessType.setAdapter(adapter);
    }

    // Optional: Get selected business type from ChipGroup
    public String getSelectedBusinessTypeFromChips() {
        if (chipGroupBusinessType == null) return null;
        int selectedId = chipGroupBusinessType.getCheckedChipId();
        if (selectedId != -1) {
            Chip selectedChip = findViewById(selectedId);
            return selectedChip.getText().toString();
        }
        return null;
    }

    // Optional: Get selected business type from Spinner
    public String getSelectedBusinessTypeFromSpinner() {
        if (spinnerBusinessType == null) return null;
        return spinnerBusinessType.getSelectedItem() != null ?
                spinnerBusinessType.getSelectedItem().toString() : null;
    }
}
