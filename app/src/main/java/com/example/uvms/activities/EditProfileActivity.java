package com.example.uvms.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uvms.R;
import com.example.uvms.api.VendorApiService;
import com.example.uvms.clients.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                android.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()).toPlatformInsets();

                // 🔹 Replace systemBars.top with a fixed padding (e.g. 24dp)
                int topPadding = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        0, // you can adjust this value
                        v.getResources().getDisplayMetrics()
                );

                v.setPadding(systemBars.left, topPadding, systemBars.right, systemBars.bottom);
            }
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

        // Prefill inputs
        prefillInputs();

        btnSave.setOnClickListener(v -> updateProfilePartial());
    }

    private void prefillInputs() {
        etFullName.setText(prefs.getString("user_first_name", "") + " " + prefs.getString("user_last_name", ""));
        etEmail.setText(prefs.getString("user_email", ""));
        etPhone.setText(prefs.getString("user_phone_number", ""));
        etBusinessName.setText(prefs.getString("user_company_name", ""));
        etBusinessType.setText(prefs.getString("business_type", ""));
        etBusinessId.setText(prefs.getString("user_tin_number", ""));
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

        int vendorId = Integer.parseInt(prefs.getString("user_id", "0"));

        // Split first and last name
        String firstName = null, lastName = null;
        if (!fullName.isEmpty()) {
            firstName = fullName.contains(" ") ? fullName.split(" ", 2)[0] : fullName;
            lastName = fullName.contains(" ") ? fullName.split(" ", 2)[1] : "";
        }

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
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            saveToPreferences(updateFields);
                            returnResult(updateFields);
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Log.e(TAG, "Update failed: " + response.code() + " " + errorBody);
                                Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(EditProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Network error", t);
                    }
                });
    }

    private void saveToPreferences(Map<String, Object> fields) {
        SharedPreferences.Editor editor = prefs.edit();
        if (fields.containsKey("firstName")) editor.putString("user_first_name", (String) fields.get("firstName"));
        if (fields.containsKey("lastName")) editor.putString("user_last_name", (String) fields.get("lastName"));
        if (fields.containsKey("email")) editor.putString("user_email", (String) fields.get("email"));
        if (fields.containsKey("phoneNumber")) editor.putString("user_phone_number", (String) fields.get("phoneNumber"));
        if (fields.containsKey("companyName")) editor.putString("user_company_name", (String) fields.get("companyName"));
        if (fields.containsKey("businessType")) editor.putString("business_type", (String) fields.get("businessType"));
        if (fields.containsKey("tinNumber")) editor.putString("user_tin_number", (String) fields.get("tinNumber"));
        editor.apply();
    }

    private void returnResult(Map<String, Object> fields) {
        Intent resultIntent = new Intent();
        if (fields.containsKey("firstName") || fields.containsKey("lastName"))
            resultIntent.putExtra("name",
                    (fields.containsKey("firstName") ? fields.get("firstName") : "") + " " +
                            (fields.containsKey("lastName") ? fields.get("lastName") : ""));
        if (fields.containsKey("email")) resultIntent.putExtra("email", (String) fields.get("email"));
        if (fields.containsKey("phoneNumber")) resultIntent.putExtra("phone", (String) fields.get("phoneNumber"));
        if (fields.containsKey("companyName")) resultIntent.putExtra("businessName", (String) fields.get("companyName"));
        if (fields.containsKey("businessType")) resultIntent.putExtra("businessType", (String) fields.get("businessType"));
        if (fields.containsKey("tinNumber")) resultIntent.putExtra("businessId", (String) fields.get("tinNumber"));
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
