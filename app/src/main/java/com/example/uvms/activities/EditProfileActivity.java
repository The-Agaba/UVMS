package com.example.uvms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uvms.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    TextInputEditText etFullName, etEmail, etPhone, etBusinessName, etBusinessType, etBusinessId;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

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
        btnSave = findViewById(R.id.btnSaveProfile);

        // Prefill with current values (passed from ProfileActivity)
        Intent intent = getIntent();
        etFullName.setText(intent.getStringExtra("name"));
        etEmail.setText(intent.getStringExtra("email"));
        etPhone.setText(intent.getStringExtra("phone"));
        etBusinessName.setText(intent.getStringExtra("businessName"));
        etBusinessType.setText(intent.getStringExtra("businessType"));
        etBusinessId.setText(intent.getStringExtra("businessId"));

        // Save button click
        btnSave.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", etFullName.getText().toString());
            resultIntent.putExtra("email", etEmail.getText().toString());
            resultIntent.putExtra("phone", etPhone.getText().toString());
            resultIntent.putExtra("businessName", etBusinessName.getText().toString());
            resultIntent.putExtra("businessType", etBusinessType.getText().toString());
            resultIntent.putExtra("businessId", etBusinessId.getText().toString());

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}