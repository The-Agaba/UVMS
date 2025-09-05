package com.example.uvms.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uvms.BaseActivity;
import com.example.uvms.R;
import com.example.uvms.api.ApplicationApiService;
import com.example.uvms.clients.RetrofitClient;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplyTenderActivity extends BaseActivity {

    private static final int REQUEST_CODE_FILE_PICKER = 1001;

    private EditText etCompanyName, etContactPerson, etEmail, etProposal;
    private Button btnSubmitApplication, btnUploadDoc;
    private TextView tvSelectedDoc;

    private String tenderId;
    private Uri selectedFileUri = null;
    private String tenderDocUrl = null;

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
        tenderDocUrl = getIntent().getStringExtra("tenderDocUrl");

        if (tenderDocUrl != null && !tenderDocUrl.isEmpty()) {
            tvSelectedDoc.setText(getString(R.string.selected) + tenderDocUrl);
        }

        btnUploadDoc.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(Intent.createChooser(intent, "Select Document"), REQUEST_CODE_FILE_PICKER);
        });

        btnSubmitApplication.setOnClickListener(v -> submitApplication());
    }

    private void submitApplication() {
        String company = etCompanyName.getText().toString().trim();
        String contact = etContactPerson.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String proposal = etProposal.getText().toString().trim();

        if (company.isEmpty() || contact.isEmpty() || email.isEmpty() || proposal.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedFileUri == null && (tenderDocUrl == null || tenderDocUrl.isEmpty())) {
            Toast.makeText(this, "Please upload or use tender document", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build request bodies
        RequestBody tenderIdBody = RequestBody.create(MediaType.parse("text/plain"), tenderId);
        RequestBody companyBody = RequestBody.create(MediaType.parse("text/plain"), company);
        RequestBody contactBody = RequestBody.create(MediaType.parse("text/plain"), contact);
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody proposalBody = RequestBody.create(MediaType.parse("text/plain"), proposal);

        MultipartBody.Part filePart = null;
        if (selectedFileUri != null) {
            try {
                File file = createTempFileFromUri(selectedFileUri);
                RequestBody fileRequest = RequestBody.create(MediaType.parse("application/pdf"), file);
                filePart = MultipartBody.Part.createFormData("document", file.getName(), fileRequest);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "File error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
        }

        // Make API call
        ApplicationApiService api = RetrofitClient.getInstance(this).create(ApplicationApiService.class);

        Call<ResponseBody> call = api.applyTender(
                tenderIdBody,
                companyBody,
                contactBody,
                emailBody,
                proposalBody,
                filePart
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ApplyTenderActivity.this, "Application submitted successfully!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(ApplyTenderActivity.this, "Error: " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e("ApplyTender", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ApplyTenderActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ApplyTender", "Failure", t);
            }
        });
    }

    private File createTempFileFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("upload", ".pdf", getCacheDir());
        OutputStream outputStream = openFileOutput(tempFile.getName(), MODE_PRIVATE);

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();
        return tempFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FILE_PICKER && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                // Check file size
                Cursor returnCursor = getContentResolver().query(selectedFileUri, null, null, null, null);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                long fileSize = returnCursor.getLong(sizeIndex);
                returnCursor.close();

                long maxSize = 5 * 1024 * 1024; // 5MB
                if (fileSize <= maxSize) {
                    tvSelectedDoc.setText(getString(R.string.selected) + selectedFileUri.getLastPathSegment());
                } else {
                    selectedFileUri = null;
                    tvSelectedDoc.setText("");
                    Toast.makeText(this, "File size exceeds 5MB limit. Please select a smaller PDF.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
