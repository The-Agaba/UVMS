package com.example.uvms.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.uvms.BaseActivity;
import com.example.uvms.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

public class TenderDetailsActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private TextView tvTitle, tvTenderId, tvDescription, tvPostDate, tvDeadline;
    private Chip chipCollege;
    private MaterialButton btnDownload, btnApply;
    private String contractUrl, tenderTitle, tenderCollege;
    private int tenderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tender_details);

        // --- Find views ---
        tvTitle = findViewById(R.id.tvTenderTitle);
        chipCollege = findViewById(R.id.chipTenderCollege);
        tvTenderId = findViewById(R.id.tvTenderId);
        tvDescription = findViewById(R.id.tvTenderDescription);
        tvPostDate = findViewById(R.id.tvTenderPostDate);
        tvDeadline = findViewById(R.id.tvTenderDeadline);
        btnDownload = findViewById(R.id.btnDownloadContract);
        btnApply = findViewById(R.id.btnApplyTender);

        // --- Get intent extras ---
        tenderId = getIntent().getIntExtra("tenderId", -1);
        tenderTitle = getIntent().getStringExtra("tenderTitle");
        tenderCollege = getIntent().getStringExtra("tenderCollege");
        String tenderDescription = getIntent().getStringExtra("tenderDescription");
        String tenderPostDate = getIntent().getStringExtra("tenderPostDate");
        String tenderDeadline = getIntent().getStringExtra("tenderDeadline");
        contractUrl = getIntent().getStringExtra("tenderDocUrl");

        // --- Populate UI ---
        tvTitle.setText(tenderTitle != null ? tenderTitle : "N/A");
        chipCollege.setText(tenderCollege != null ? tenderCollege : "N/A");
        tvTenderId.setText(tenderId != -1 ? "UDOM/ICT/" + tenderId : "N/A");
        tvDescription.setText(tenderDescription != null ? tenderDescription : "N/A");
        tvPostDate.setText(tenderPostDate != null ? "Posted on: " + tenderPostDate : "N/A");
        tvDeadline.setText(tenderDeadline != null ? "Closes: " + tenderDeadline : "N/A");

        // --- Button Listeners ---
        btnDownload.setOnClickListener(v -> downloadContract());
        btnApply.setOnClickListener(v -> openApplyTenderActivity());
    }

    private void downloadContract() {
        if (contractUrl == null || contractUrl.isEmpty()) {
            Toast.makeText(this, "No contract available to download.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check storage permission for Android < 10
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                return;
            }
        }
        // Download contract
        try {
            Uri uri = Uri.parse(contractUrl);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(tenderTitle);
            request.setDescription("Downloading tender contract...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());

            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            dm.enqueue(request);

            Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openApplyTenderActivity() {
        Intent intent = new Intent(this, ApplyTenderActivity.class);
        intent.putExtra("tenderId", tenderId);
        intent.putExtra("tenderTitle", tenderTitle);
        intent.putExtra("tenderCollege", tenderCollege);
        intent.putExtra("tenderDocUrl", contractUrl);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadContract();
            } else {
                Toast.makeText(this, "Storage permission denied.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
