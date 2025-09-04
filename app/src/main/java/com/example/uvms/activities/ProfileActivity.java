package com.example.uvms.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uvms.R;
import com.google.android.material.appbar.MaterialToolbar;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "uvms_prefs";
    private static final int EDIT_PROFILE_REQUEST = 1001;

    private ImageView imgProfile;
    private TextView tvName, tvStatus;
    private TextView tvPersonalName, tvPersonalEmail, tvPersonalPhone;
    private TextView tvBusinessName, tvBusinessType, tvBusinessId;
    private TextView tvMemberSince;
    private Button btnEditProfile, btnLogout;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String token = prefs.getString("auth_token", null);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_profile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();
        loadUserData();
        setupListeners();
    }

    private void bindViews() {
        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        tvStatus = findViewById(R.id.tvStatus);

        tvPersonalName = findViewById(R.id.tvPersonalName);
        tvPersonalEmail = findViewById(R.id.tvPersonalEmail);
        tvPersonalPhone = findViewById(R.id.tvPersonalPhone);

        tvBusinessName = findViewById(R.id.tvBusinessName);
        tvBusinessType = findViewById(R.id.tvBusinessType);
        tvBusinessId = findViewById(R.id.tvBusinessId);

        tvMemberSince = findViewById(R.id.tvMemberSince);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        btnEditProfile.setOnClickListener(v -> {
            Intent editIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            // Pass current values to EditProfile
            editIntent.putExtra("name", prefs.getString("user_first_name", "") + " " + prefs.getString("user_last_name", ""));
            editIntent.putExtra("email", prefs.getString("user_email", ""));
            editIntent.putExtra("phone", prefs.getString("user_phone_number", ""));
            editIntent.putExtra("businessName", prefs.getString("user_company_name", ""));
            editIntent.putExtra("businessType", prefs.getString("business_type", ""));
            editIntent.putExtra("businessId", prefs.getString("user_tin_number", ""));
            startActivityForResult(editIntent, EDIT_PROFILE_REQUEST);
        });
    }

    private void loadUserData() {
        String firstName = prefs.getString("user_first_name", "N/A");
        String lastName = prefs.getString("user_last_name", "N/A");
        String email = prefs.getString("user_email", "N/A");
        String phone = prefs.getString("user_phone_number", "N/A");
        String companyName = prefs.getString("user_company_name", "N/A");
        String businessType = prefs.getString("business_type", "N/A");
        String tinNumber = prefs.getString("user_tin_number", "N/A");
        boolean isActive = prefs.getBoolean("user_is_active", false);
        String createdAt = prefs.getString("user_created_at", "N/A");

        tvName.setText(companyName);
        tvStatus.setText(isActive ? "Active" : "Inactive");

        tvPersonalName.setText(firstName + " " + lastName);
        tvPersonalEmail.setText(email);
        tvPersonalPhone.setText(phone);

        tvBusinessName.setText(companyName);
        tvBusinessType.setText(businessType);
        tvBusinessId.setText(tinNumber);

        tvMemberSince.setText("Member since: " + createdAt);

        imgProfile.setImageResource(R.drawable.ic_building);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Reload updated info
            loadUserData();
        }
    }
}
