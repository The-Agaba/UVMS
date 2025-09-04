package com.example.uvms.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uvms.R;
import com.example.uvms.api.VendorApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.Vendor;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    TextInputEditText etFullName, etEmail, etPhone, etBusinessName, etBusinessType, etBusinessId;
    Button btnSave;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "uvms_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editProfile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Top app bar back button
        MaterialToolbar toolbar = findViewById(R.id.topAppBarEdit);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Inputs
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etBusinessName = findViewById(R.id.etBusinessName);
        etBusinessType = findViewById(R.id.etBusinessType);
        etBusinessId = findViewById(R.id.etBusinessId);
        //etBusinessAddress = findViewById(R.id.etBusinessAddress); // add in XML
        btnSave = findViewById(R.id.btnSaveProfile);

        // Prefill with current values (from ProfileActivity or prefs)
        Intent intent = getIntent();
        etFullName.setText(intent.getStringExtra("name"));
        etEmail.setText(intent.getStringExtra("email"));
        etPhone.setText(intent.getStringExtra("phone"));
        etBusinessName.setText(intent.getStringExtra("businessName"));
        etBusinessType.setText(intent.getStringExtra("businessType"));
        etBusinessId.setText(intent.getStringExtra("businessId"));


        btnSave.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String businessName = etBusinessName.getText().toString().trim();
            String businessType = etBusinessType.getText().toString().trim();
            String businessId = etBusinessId.getText().toString().trim();


            String firstName = fullName.contains(" ") ? fullName.split(" ", 2)[0] : fullName;
            String lastName = fullName.contains(" ") ? fullName.split(" ", 2)[1] : "";

            // Correct vendorId from prefs (String → int)
            int vendorId = 0;
            try {
                vendorId = Integer.parseInt(prefs.getString("user_id", "0"));
            } catch (Exception e) {
                vendorId = 0;
            }

            // Build Vendor object
            Vendor updatedVendor = new Vendor(
                    vendorId,
                    email,
                    null,
                    firstName,
                    lastName,
                    null,
                    phone,
                    businessName,
                    businessId,
                    null,
                    null,
                    null,
                    true,
                    businessType,
                    null,
                    null,
                    null,
                    null
            );

            VendorApiService apiService = RetrofitClient.getInstance(this).create(VendorApiService.class);
            String token = "Bearer " + prefs.getString("auth_token", "");
            apiService.updateVendor(token, updatedVendor.getVendorId(), updatedVendor)
                    .enqueue(new Callback<Vendor>() {

            @Override
                public void onResponse(Call<Vendor> call, Response<Vendor> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Vendor vendor = response.body();

                        // Save updated values
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("user_id", String.valueOf(vendor.getVendorId()));
                        editor.putString("user_first_name", vendor.getFirstName());
                        editor.putString("user_last_name", vendor.getLastName());
                        editor.putString("user_email", vendor.getEmail());
                        editor.putString("user_phone_number", vendor.getPhoneNumber());
                        editor.putString("user_company_name", vendor.getCompanyName());
                        editor.putString("business_type", vendor.getBusinessType());
                        editor.putString("user_tin_number", vendor.getTinNumber());
                        editor.putString("user_business_address", vendor.getBusinessAddress());
                        editor.apply();

                        // Return updated data
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("name", vendor.getFirstName() + " " + vendor.getLastName());
                        resultIntent.putExtra("email", vendor.getEmail());
                        resultIntent.putExtra("phone", vendor.getPhoneNumber());
                        resultIntent.putExtra("businessName", vendor.getCompanyName());
                        resultIntent.putExtra("businessType", vendor.getBusinessType());
                        resultIntent.putExtra("businessId", vendor.getTinNumber());
                        resultIntent.putExtra("businessAddress", vendor.getBusinessAddress());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Vendor> call, Throwable t) {
                    Toast.makeText(EditProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
