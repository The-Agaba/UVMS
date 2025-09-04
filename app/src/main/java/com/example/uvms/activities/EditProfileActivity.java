package com.example.uvms.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPhone, etBusinessName, etBusinessType, etBusinessId;
    private Button btnSave;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "uvms_prefs";
    private static final String TAG = "EditProfileActivity";

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

        // Toolbar back button
        MaterialToolbar toolbar = findViewById(R.id.topAppBarEdit);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Inputs
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etBusinessName = findViewById(R.id.etBusinessName);
        etBusinessType = findViewById(R.id.etBusinessType);
        etBusinessId = findViewById(R.id.etBusinessId);
        btnSave = findViewById(R.id.btnSaveProfile);

        // Prefill inputs with Intent or SharedPreferences fallback
        etFullName.setText(getIntent().getStringExtra("name") != null
                ? getIntent().getStringExtra("name")
                : prefs.getString("user_first_name", "") + " " + prefs.getString("user_last_name", ""));
        etEmail.setText(getIntent().getStringExtra("email") != null
                ? getIntent().getStringExtra("email")
                : prefs.getString("user_email", ""));
        etPhone.setText(getIntent().getStringExtra("phone") != null
                ? getIntent().getStringExtra("phone")
                : prefs.getString("user_phone_number", ""));
        etBusinessName.setText(getIntent().getStringExtra("businessName") != null
                ? getIntent().getStringExtra("businessName")
                : prefs.getString("user_company_name", ""));
        etBusinessType.setText(getIntent().getStringExtra("businessType") != null
                ? getIntent().getStringExtra("businessType")
                : prefs.getString("business_type", ""));
        etBusinessId.setText(getIntent().getStringExtra("businessId") != null
                ? getIntent().getStringExtra("businessId")
                : prefs.getString("user_tin_number", ""));

        btnSave.setOnClickListener(v -> updateProfilePartial());
    }

    private void updateProfilePartial() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String businessName = etBusinessName.getText().toString().trim();
        String businessType = etBusinessType.getText().toString().trim();
        String businessId = etBusinessId.getText().toString().trim();

        // Nothing to update
        if (fullName.isEmpty() && email.isEmpty() && phone.isEmpty() &&
                businessName.isEmpty() && businessType.isEmpty() && businessId.isEmpty()) {
            Toast.makeText(this, "Nothing to update", Toast.LENGTH_SHORT).show();
            return;
        }

        int vendorId;
        try {
            vendorId = Integer.parseInt(prefs.getString("user_id", "0"));
        } catch (Exception e) {
            vendorId = 0;
        }

        // Split first and last name if fullName is provided
        String firstName = null, lastName = null;
        if (!fullName.isEmpty()) {
            firstName = fullName.contains(" ") ? fullName.split(" ", 2)[0] : fullName;
            lastName = fullName.contains(" ") ? fullName.split(" ", 2)[1] : "";
        }

        // Build partial update map
        Map<String, Object> updateFields = new HashMap<>();
        if (firstName != null) updateFields.put("firstName", firstName);
        if (lastName != null) updateFields.put("lastName", lastName);
        if (!email.isEmpty()) updateFields.put("email", email);
        if (!phone.isEmpty()) updateFields.put("phoneNumber", phone);
        if (!businessName.isEmpty()) updateFields.put("companyName", businessName);
        if (!businessType.isEmpty()) updateFields.put("businessType", businessType);
        if (!businessId.isEmpty()) updateFields.put("tinNumber", businessId);

        VendorApiService apiService = RetrofitClient.getInstance(this).create(VendorApiService.class);
        String token = "Bearer " + prefs.getString("auth_token", "");
        apiService.updateVendorPartial(token, vendorId, updateFields)
                .enqueue(new Callback<Vendor>() {
                    @Override
                    public void onResponse(Call<Vendor> call, Response<Vendor> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Vendor vendor = response.body();
                            saveToPreferencesPartial(vendor);
                            returnResult(vendor);
                            Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Log.e(TAG, "Update failed: " + response.code() + " " + errorBody);
                                Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + response.code(), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Vendor> call, Throwable t) {
                        Toast.makeText(EditProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Network error", t);
                    }
                });
    }

    private void saveToPreferencesPartial(Vendor vendor) {
        SharedPreferences.Editor editor = prefs.edit();
        if (vendor.getFirstName() != null) editor.putString("user_first_name", vendor.getFirstName());
        if (vendor.getLastName() != null) editor.putString("user_last_name", vendor.getLastName());
        if (vendor.getEmail() != null) editor.putString("user_email", vendor.getEmail());
        if (vendor.getPhoneNumber() != null) editor.putString("user_phone_number", vendor.getPhoneNumber());
        if (vendor.getCompanyName() != null) editor.putString("user_company_name", vendor.getCompanyName());
        if (vendor.getBusinessType() != null) editor.putString("business_type", vendor.getBusinessType());
        if (vendor.getTinNumber() != null) editor.putString("user_tin_number", vendor.getTinNumber());
        editor.apply();
    }

    private void returnResult(Vendor vendor) {
        Intent resultIntent = new Intent();
        if (vendor.getFirstName() != null || vendor.getLastName() != null)
            resultIntent.putExtra("name", (vendor.getFirstName() != null ? vendor.getFirstName() : "") + " " + (vendor.getLastName() != null ? vendor.getLastName() : ""));
        if (vendor.getEmail() != null) resultIntent.putExtra("email", vendor.getEmail());
        if (vendor.getPhoneNumber() != null) resultIntent.putExtra("phone", vendor.getPhoneNumber());
        if (vendor.getCompanyName() != null) resultIntent.putExtra("businessName", vendor.getCompanyName());
        if (vendor.getBusinessType() != null) resultIntent.putExtra("businessType", vendor.getBusinessType());
        if (vendor.getTinNumber() != null) resultIntent.putExtra("businessId", vendor.getTinNumber());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
