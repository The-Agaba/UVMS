package com.example.uvms.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uvms.R;
import com.example.uvms.api.LicenseApiService;
import com.example.uvms.clients.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RenewLicenseActivity extends AppCompatActivity {

    private TextInputEditText editLicenseNumber, editCompanyName, editExpiryDate;
    private MaterialButton btnUploadDoc, btnSubmitRenewal;

    private Uri pickedFile = null;

    // File picker
    private final ActivityResultLauncher<String[]> pickDocLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    pickedFile = uri;
                    Toast.makeText(this, "File selected", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renew_license);

        // Bind views
        editLicenseNumber = findViewById(R.id.editLicenseNumber);
        editCompanyName = findViewById(R.id.editCompanyName);
        editExpiryDate = findViewById(R.id.editExpiryDate);
        btnUploadDoc = findViewById(R.id.btnUploadDoc);
        btnSubmitRenewal = findViewById(R.id.btnSubmitRenewal);

        // Expiry date picker
        editExpiryDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Expiry Date")
                    .build();

            picker.addOnPositiveButtonClickListener(selection -> {
                if (selection != null) {
                    String formatted = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(selection);
                    editExpiryDate.setText(formatted);
                }
            });

            picker.show(getSupportFragmentManager(), "expiryPicker");
        });

        // Upload document button
        btnUploadDoc.setOnClickListener(v -> pickDocLauncher.launch(new String[]{"application/pdf", "image/*"}));

        // Submit renewal button
        btnSubmitRenewal.setOnClickListener(v -> submitRenewal());
    }

    private void submitRenewal() {
        String license = editLicenseNumber.getText().toString().trim();
        String company = editCompanyName.getText().toString().trim();
        String expiry = editExpiryDate.getText().toString().trim();

        if (license.isEmpty() || company.isEmpty() || expiry.isEmpty() || pickedFile == null) {
            Toast.makeText(this, "Please complete all fields and upload a document", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            // Convert Uri to byte array
            InputStream inputStream = getContentResolver().openInputStream(pickedFile);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] bytes = buffer.toByteArray();
            inputStream.close();

            // Create MultipartBody.Part
            RequestBody fileReqBody = RequestBody.create(bytes, MediaType.parse(getContentResolver().getType(pickedFile)));
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("license_document", "license.pdf", fileReqBody);

            // Create form fields
            RequestBody licenseBody = RequestBody.create(license, MediaType.parse("text/plain"));
            RequestBody companyBody = RequestBody.create(company, MediaType.parse("text/plain"));
            RequestBody expiryBody = RequestBody.create(expiry, MediaType.parse("text/plain"));

            // Retrofit call
            LicenseApiService api = RetrofitClient.getLicenseService(this);
            Call<ResponseBody> call = api.renewLicense(licenseBody, companyBody, expiryBody, filePart);

            Toast.makeText(this, "Submitting Renewal...", Toast.LENGTH_SHORT).show();

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RenewLicenseActivity.this, "Renewal submitted successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(RenewLicenseActivity.this, "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(RenewLicenseActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "File read error", Toast.LENGTH_LONG).show();
        }
    }
}
