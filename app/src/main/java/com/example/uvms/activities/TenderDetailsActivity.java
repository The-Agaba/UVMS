package com.example.uvms.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uvms.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TenderDetailsActivity extends AppCompatActivity {

    private TextView tvTenderTitle, tvTenderId, tvTenderBuyer,
            tvTenderCategory, tvTenderLocation, tvTenderDeadline,
            tvTenderBudget, tvTenderStatus;
    private Button btnApplyNow, btnDownloadDocs;

    // Example hardcoded tender document URL (replace with API-driven URL later)
    private String tenderDocumentUrl = "https://example.com/tenderdocs/sample.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tender_details);

        // Initialize views
        tvTenderTitle = findViewById(R.id.tvTenderTitle);
        tvTenderId = findViewById(R.id.tvTenderId);
        tvTenderBuyer = findViewById(R.id.tvTenderBuyer);
        tvTenderCategory = findViewById(R.id.tvTenderCategory);
        tvTenderLocation = findViewById(R.id.tvTenderLocation);
        tvTenderDeadline = findViewById(R.id.tvTenderDeadline);
        tvTenderBudget = findViewById(R.id.tvTenderBudget);
        tvTenderStatus = findViewById(R.id.tvTenderStatus);
        btnApplyNow = findViewById(R.id.btnApplyNow);
        btnDownloadDocs = findViewById(R.id.btnDownloadTenderDoc);

        // Example hardcoded tender details (replace with API-driven data)
        tvTenderTitle.setText("Construction of New School Block");
        tvTenderId.setText("ID: TNDR-001");
        tvTenderBuyer.setText("City Council Procurement Office");
        tvTenderCategory.setText("Construction");
        tvTenderLocation.setText("Dar es Salaam, Tanzania");
        tvTenderDeadline.setText(formatDate(System.currentTimeMillis() + 7*24*60*60*1000)); // 1 week from now
        tvTenderBudget.setText(formatCurrency(50000000));
        tvTenderStatus.setText("Open");

        // Download Tender Docs button
        btnDownloadDocs.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tenderDocumentUrl));
            startActivity(browserIntent);
        });

        // Inside onCreate(), replace Apply Now button listener:

        btnApplyNow.setOnClickListener(v -> {
            Intent applyIntent = new Intent(TenderDetailsActivity.this, ApplyTenderActivity.class);
            applyIntent.putExtra("tenderId", "TNDR-001");
            // Pass document URL to ApplyTenderActivity
            applyIntent.putExtra("tenderDocUrl", tenderDocumentUrl);
            startActivity(applyIntent);
        });


        /*
         * TODO:
         * - Replace hardcoded tender details with API-driven data
         * - Handle multiple tender documents
         * - Optionally show preview of document list before download
         */
    }

    // Format deadline date
    private String formatDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    // Format budget in Tanzanian Shillings
    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("sw", "TZ"));
        return formatter.format(amount);
    }
}
