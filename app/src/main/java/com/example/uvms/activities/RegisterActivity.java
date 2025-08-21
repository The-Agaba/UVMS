package com.example.uvms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uvms.R;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.interfaces.ApiService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ChipGroup chipGroupBusinessType;
    private ApiService apiService;

    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Edge-to-edge support
        setContentView(R.layout.activity_register);

        registerButton=findViewById(R.id.btnRegister);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        chipGroupBusinessType = findViewById(R.id.chipGroupBusinessType);

        // Initialize Retrofit API
        apiService = RetrofitClient.getInstance().create(ApiService.class);

        // Load business types
        loadBusinessTypes();
        registerButton.setOnClickListener(v ->
        {
            // Navigate to MainActivity
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
                    populateChipGroup(businessTypes);
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
        chipGroupBusinessType.removeAllViews(); // Clear previous chips

        for (String type : businessTypes) {
            Chip chip = new Chip(this);
            chip.setText(type);
            chip.setCheckable(true);
            chipGroupBusinessType.addView(chip);
        }
    }

    // Optional: Get selected business type
    public String getSelectedBusinessType() {
        int selectedId = chipGroupBusinessType.getCheckedChipId();
        if (selectedId != -1) {
            Chip selectedChip = findViewById(selectedId);
            return selectedChip.getText().toString();
        }
        return null;
    }
}
