package com.example.uvms.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uvms.R;

public class ApplyTenderActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_FILE_PICKER = 1001;

    private EditText etCompanyName, etContactPerson, etEmail, etProposal;
    private Button btnSubmitApplication, btnUploadDoc;
    private TextView tvSelectedDoc;

    private String tenderId;
    private Uri selectedFileUri = null; // store selected file URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_tender);

        // Initialize views
        etCompanyName = findViewById(R.id.etCompanyName);
        etContactPerson = findViewById(R.id.etContactPerson);
        etEmail = findViewById(R.id.etEmail);
        etProposal = findViewById(R.id.etProposal);
        btnSubmitApplication = findViewById(R.id.btnSubmitApplication);
        btnUploadDoc = findViewById(R.id.btnUploadDoc);
        tvSelectedDoc = findViewById(R.id.tvSelectedDoc);

        tenderId = getIntent().getStringExtra("tenderId");
        String tenderDocUrl = getIntent().getStringExtra("tenderDocUrl");

        // If a document URL was passed, show it as pre-selected
        if (tenderDocUrl != null && !tenderDocUrl.isEmpty()) {
            tvSelectedDoc.setText("Document from tender: " + tenderDocUrl);
            // Optionally, you could download it to local storage or keep URL reference
            // TODO: Implement automatic attachment of downloaded doc when submitting
        }

        btnUploadDoc.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "Select Document"), REQUEST_CODE_FILE_PICKER);
        });

        btnSubmitApplication.setOnClickListener(v -> {
            String company = etCompanyName.getText().toString().trim();
            String contact = etContactPerson.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String proposal = etProposal.getText().toString().trim();

            if (company.isEmpty() || contact.isEmpty() || email.isEmpty() || proposal.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            } else if (selectedFileUri == null && (tenderDocUrl == null || tenderDocUrl.isEmpty())) {
                Toast.makeText(this, "Please upload or use tender document", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: API call here
                // If selectedFileUri != null → upload local file
                // Else → send tenderDocUrl as reference
                Toast.makeText(this, "Application submitted for Tender ID: " + tenderId, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FILE_PICKER && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                tvSelectedDoc.setText("Selected: " + selectedFileUri.getLastPathSegment());
            }
        }
    }
}
