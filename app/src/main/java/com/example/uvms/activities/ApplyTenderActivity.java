package com.example.uvms.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uvms.BaseActivity;
import com.example.uvms.R;
import com.example.uvms.api.ApplicationApiService;
import com.example.uvms.api.PlotApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.Plot;
import com.example.uvms.models.Vendor;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplyTenderActivity extends BaseActivity {

    private static final int REQUEST_CODE_PROPOSAL = 1001;
    private static final int REQUEST_CODE_CONTRACT = 1002;

    private TextView tvCompanyName, tvContactPerson, tvEmail, tvProposalFile, tvContractFile, tvCollegeName, tvTenderTitle;
    private Spinner spinnerPlots;
    private Button btnUploadProposal, btnUploadContract, btnSubmit;

    private Vendor loggedVendor;
    private int tenderId;
    private Uri proposalUri = null;
    private Uri contractUri = null;
    private List<Plot> plotList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_tender);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_apply_tender), (v, insets) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                android.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()).toPlatformInsets();
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            }
            return insets;
        });

        // --- Initialize views ---
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvContactPerson = findViewById(R.id.tvContactPerson);
        tvEmail = findViewById(R.id.tvEmail);
        tvProposalFile = findViewById(R.id.tvProposalFile);
        tvContractFile = findViewById(R.id.tvContractFile);
        tvCollegeName = findViewById(R.id.tvCollegeName);
        tvTenderTitle = findViewById(R.id.tvTenderTitle);
        spinnerPlots = findViewById(R.id.spinnerPlots);
        btnUploadProposal = findViewById(R.id.btnUploadProposal);
        btnUploadContract = findViewById(R.id.btnUploadContract);
        btnSubmit = findViewById(R.id.btnSubmitApplication);

        // --- Get intent data ---
        tenderId = getIntent().getIntExtra("tenderId", -1);
        String tenderTitle = getIntent().getStringExtra("tenderTitle");
        String tenderCollege = getIntent().getStringExtra("tenderCollege");
        String contractUrl = getIntent().getStringExtra("tenderDocUrl");
        loggedVendor = (Vendor) getIntent().getSerializableExtra("vendor");

        if (loggedVendor != null) {
            tvCompanyName.setText(loggedVendor.getCompanyName());
            tvContactPerson.setText(loggedVendor.getFirstName() + " " + loggedVendor.getLastName());
            tvEmail.setText(loggedVendor.getEmail());
        }

        tvTenderTitle.setText(tenderTitle);
        tvCollegeName.setText(tenderCollege);

        fetchPlotsForTender();

        btnUploadProposal.setOnClickListener(v -> pickFile(REQUEST_CODE_PROPOSAL));
        btnUploadContract.setOnClickListener(v -> pickFile(REQUEST_CODE_CONTRACT));
        btnSubmit.setOnClickListener(v -> submitApplication());
    }

    private void fetchPlotsForTender() {
        PlotApiService api = RetrofitClient.getInstance(this).create(PlotApiService.class);
        api.getPlotsByTender(tenderId).enqueue(new Callback<List<Plot>>() {
            @Override
            public void onResponse(Call<List<Plot>> call, Response<List<Plot>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    plotList = response.body();
                    List<String> plotNames = new ArrayList<>();
                    for (Plot plot : plotList) {
                        String available = plot.isAvailable() ? "(Available)" : "(Not Available)";
                        plotNames.add(plot.getPlotNumber() + " - " + plot.getLocationDescription() + " " + available);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ApplyTenderActivity.this,
                            android.R.layout.simple_spinner_item, plotNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPlots.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Plot>> call, Throwable t) {
                Toast.makeText(ApplyTenderActivity.this, "Failed to fetch plots: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void pickFile(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                long maxSize = 5 * 1024 * 1024;
                Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                long fileSize = returnCursor.getLong(sizeIndex);
                returnCursor.close();
                if (fileSize <= maxSize) {
                    if (requestCode == REQUEST_CODE_PROPOSAL) {
                        proposalUri = uri;
                        tvProposalFile.setText("Proposal: " + uri.getLastPathSegment());
                    } else if (requestCode == REQUEST_CODE_CONTRACT) {
                        contractUri = uri;
                        tvContractFile.setText("Contract: " + uri.getLastPathSegment());
                    }
                } else {
                    Toast.makeText(this, "File exceeds 5MB", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void submitApplication() {
        if (spinnerPlots.getSelectedItemPosition() == -1) {
            Toast.makeText(this, "Select a plot", Toast.LENGTH_SHORT).show();
            return;
        }
        if (proposalUri == null) {
            Toast.makeText(this, "Upload proposal PDF", Toast.LENGTH_SHORT).show();
            return;
        }
        if (contractUri == null) {
            Toast.makeText(this, "Upload contract PDF", Toast.LENGTH_SHORT).show();
            return;
        }

        Plot selectedPlot = plotList.get(spinnerPlots.getSelectedItemPosition());

        // Build request
        RequestBody tenderBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(tenderId));
        RequestBody plotBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedPlot.getPlotId()));

        MultipartBody.Part proposalPart = createMultipartFromUri(proposalUri, "proposal");
        MultipartBody.Part contractPart = createMultipartFromUri(contractUri, "document");

        ApplicationApiService api = RetrofitClient.getInstance(this).create(ApplicationApiService.class);
        Call<ResponseBody> call = api.applyTender(plotBody, proposalPart, contractPart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ApplyTenderActivity.this, "Application submitted successfully!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(ApplyTenderActivity.this, "Submission failed: " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e("ApplyTender", "Error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ApplyTenderActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private MultipartBody.Part createMultipartFromUri(Uri uri, String partName) {
        try {
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

            RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), tempFile);
            return MultipartBody.Part.createFormData(partName, tempFile.getName(), requestFile);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "File error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }
}
