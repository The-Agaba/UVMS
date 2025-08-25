package com.example.uvms.activities;

import android.content.Intent;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uvms.R;
import com.google.android.material.appbar.MaterialToolbar;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private TextView tvName, tvStatus;

    private TextView tvPersonalName, tvPersonalEmail, tvPersonalPhone;

    private TextView tvBusinessName, tvBusinessType, tvBusinessId;

    private Button btnEditProfile, btnLogout;

    private TextView tvMemberSince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        EdgeToEdge.enable(this);

        // Apply insets to your main container
        View mainContainer = findViewById(R.id.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(mainContainer, (v, insets) -> {
            Insets systemBars = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()).toPlatformInsets();
            }

            // Apply padding so content is not hidden by status/nav bars
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            }

            return insets;
        });
        // Bind Views
        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        tvStatus = findViewById(R.id.tvStatus);

        tvPersonalName = findViewById(R.id.tvPersonalName);
        tvPersonalEmail = findViewById(R.id.tvPersonalEmail);
        tvPersonalPhone = findViewById(R.id.tvPersonalPhone);

        tvBusinessName = findViewById(R.id.tvBusinessName);
        tvBusinessType = findViewById(R.id.tvBusinessType);
        tvBusinessId = findViewById(R.id.tvBusinessId);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);

        tvMemberSince = findViewById(R.id.tvMemberSince);

        // Top app bar back button
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Load dummy data
        loadDummyUserData();

        // Handle button clicks
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);

            // Pass current values
            intent.putExtra("name", ((TextView) findViewById(R.id.tvPersonalName)).getText().toString());
            intent.putExtra("email", ((TextView) findViewById(R.id.tvPersonalEmail)).getText().toString());
            intent.putExtra("phone", ((TextView) findViewById(R.id.tvPersonalPhone)).getText().toString());
            intent.putExtra("businessName", ((TextView) findViewById(R.id.tvBusinessName)).getText().toString());
            intent.putExtra("businessType", ((TextView) findViewById(R.id.tvBusinessType)).getText().toString());
            intent.putExtra("businessId", ((TextView) findViewById(R.id.tvBusinessId)).getText().toString());

            startActivityForResult(intent, 1001);
        });

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show();
            // Example: Navigate to login screen
            // startActivity(new Intent(this, LoginActivity.class));
            // finish();
        });
    }

    // Receive result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            ((TextView)findViewById(R.id.tvPersonalName)).setText(data.getStringExtra("name"));
            ((TextView)findViewById(R.id.tvPersonalEmail)).setText(data.getStringExtra("email"));
            ((TextView)findViewById(R.id.tvPersonalPhone)).setText(data.getStringExtra("phone"));
            ((TextView)findViewById(R.id.tvBusinessName)).setText(data.getStringExtra("businessName"));
            ((TextView)findViewById(R.id.tvBusinessType)).setText(data.getStringExtra("businessType"));
            ((TextView)findViewById(R.id.tvBusinessId)).setText(data.getStringExtra("businessId"));
        }
    }


    private void loadDummyUserData() {
        // Header
        tvName.setText("Frank Masillago");
        tvStatus.setText("Active");

        // Personal Info
        tvPersonalName.setText("Glory Masillago");
        tvPersonalEmail.setText("glory.masillago@example.com");
        tvPersonalPhone.setText("+255 712 345 678");

        // Business Info
        tvBusinessName.setText("Masillago Petroleum Co.");
        tvBusinessType.setText("Oil & Gas Trading");
        tvBusinessId.setText("BP-009876");

        // Member Since
        tvMemberSince.setText("Member since: Jan 2023");

        // Profile picture (keep placeholder for now)
        imgProfile.setImageResource(R.drawable.ic_building);
    }
}