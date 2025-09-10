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
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.BaseActivity;
import com.example.uvms.R;
import com.example.uvms.adapters.PlotAdapter;
import com.example.uvms.api.PlotApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.Plot;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TenderDetailsActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private TextView tvTitle, tvTenderId, tvDescription, tvPostDate, tvDeadline, tvCollege;
    private MaterialButton btnDownload, btnApply;
    private String contractUrl, tenderTitle, tenderCollege, tenderDescription, tenderPostDate, tenderDeadline;
    private int tenderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tender_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_tender_details), (v, insets) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                android.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()).toPlatformInsets();


                int topPadding = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        0,
                        v.getResources().getDisplayMetrics()
                );

                v.setPadding(systemBars.left, topPadding, systemBars.right, systemBars.bottom);
            }
            return insets;
        });

        // --- Find views ---
        tvTitle = findViewById(R.id.tvTenderTitle);
        tvCollege = findViewById(R.id.tvCollegeName);
        tvTenderId = findViewById(R.id.tvTenderId);
        tvDescription = findViewById(R.id.tvTenderDescription);
        tvPostDate = findViewById(R.id.tvTenderPostDate);
        tvDeadline = findViewById(R.id.tvTenderDeadline);
        btnDownload = findViewById(R.id.btnDownloadContract);
        btnApply = findViewById(R.id.btnApplyTender);

        RecyclerView rvPlots = findViewById(R.id.lvPlots);

        // --- Get intent extras ---
        tenderId = getIntent().getIntExtra("tender_id", -1);
        tenderTitle = getIntent().getStringExtra("tender_title");
        tenderCollege = getIntent().getStringExtra("college_name");
        tenderDescription = getIntent().getStringExtra("tender_description");
        tenderPostDate = getIntent().getStringExtra("tender_post_date");
        tenderDeadline = getIntent().getStringExtra("tender_deadline");
        contractUrl = getIntent().getStringExtra("contract_url");

        // --- Populate UI ---
        tvTitle.setText(tenderTitle != null ? tenderTitle : "N/A");
        tvCollege.setText(tenderCollege != null ? tenderCollege : "N/A");
        tvTenderId.setText(tenderId != -1 ? "UDOM/ICT/" + tenderId : "N/A");
        tvDescription.setText(tenderDescription != null ? tenderDescription : "N/A");
        tvPostDate.setText(tenderPostDate != null ? "Posted on: " + tenderPostDate : "N/A");
        tvDeadline.setText(tenderDeadline != null ? "Closes: " + tenderDeadline : "N/A");

        // --- Button Listeners ---
        btnDownload.setOnClickListener(v -> downloadContract());
        btnApply.setOnClickListener(v -> openApplyTenderActivity());

        // --- Setup Plots RecyclerView ---
        List<Plot> plotList = new ArrayList<>();
        PlotAdapter plotAdapter = new PlotAdapter(this, plotList, plot -> {
            // Optional: handle plot click if needed
        });
        rvPlots.setLayoutManager(new LinearLayoutManager(this));
        rvPlots.setAdapter(plotAdapter);

        // --- Fetch plots for this tender ---
        PlotApiService plotApi = RetrofitClient.getPlotService(this);
        plotApi.getPlotsByTender(tenderId).enqueue(new Callback<List<Plot>>() {
            @Override
            public void onResponse(Call<List<Plot>> call, Response<List<Plot>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    plotAdapter.updateData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Plot>> call, Throwable t) {
                Toast.makeText(TenderDetailsActivity.this, "Failed to fetch plots", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadContract() {
        if (contractUrl == null || contractUrl.isEmpty()) {
            Toast.makeText(this, "No contract available to download.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                return;
            }
        }

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
