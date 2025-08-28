package com.example.uvms.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uvms.R;

public class ApplicationDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_detail);

        String tenderName = getIntent().getStringExtra("tender_name");
        String submitted  = getIntent().getStringExtra("submitted");
        String status     = getIntent().getStringExtra("status");

        TextView tvName = findViewById(R.id.tvAppTenderName);
        TextView tvSubmitted = findViewById(R.id.tvAppSubmittedOn);
        TextView chipStatus = findViewById(R.id.chipAppStatus);

        tvName.setText(tenderName);
        tvSubmitted.setText("Submitted: " + submitted);
        chipStatus.setText("Status: " + status);
    }
}
