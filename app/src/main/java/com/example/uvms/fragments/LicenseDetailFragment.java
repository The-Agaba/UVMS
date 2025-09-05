package com.example.uvms.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.uvms.R;
import com.example.uvms.models.License;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LicenseDetailFragment extends Fragment {

    private static final String ARG_LICENSE = "arg_license";
    private License license;

    public static LicenseDetailFragment newInstance(License license) {
        LicenseDetailFragment fragment = new LicenseDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LICENSE, license);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            license = (License) getArguments().getSerializable(ARG_LICENSE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_license_detail, container, false);

        if (license != null) {
            // License Number
            TextView tvLicenseNumber = view.findViewById(R.id.tvLicenseNumber);
            tvLicenseNumber.setText(license.getSafeString(license.getLicenseNumber(), "N/A"));

            // Vendor info
            TextView tvVendorName = view.findViewById(R.id.tvVendorName);
            tvVendorName.setText("Vendor ID: " + license.getVendorId());

            TextView tvVendorTIN = view.findViewById(R.id.tvVendorTIN);
            tvVendorTIN.setText("TIN: N/A"); // Not available in current model

            // College info
            TextView tvCollege = view.findViewById(R.id.tvCollegeName);
            tvCollege.setText("College: N/A");

            // Tender info
            TextView tvTender = view.findViewById(R.id.tvTenderTitle);
            tvTender.setText("Tender: N/A");

            // License Status
            TextView tvStatus = view.findViewById(R.id.tvLicenseStatus);
            tvStatus.setText("Status: " + license.getSafeString(license.getStatus(), "N/A"));

            // Issue & Expiry dates with nice formatting
            TextView tvIssue = view.findViewById(R.id.tvIssueDate);
            tvIssue.setText("Issued: " + formatDate(license.getIssueDate()));

            TextView tvExpiry = view.findViewById(R.id.tvExpiryDate);
            tvExpiry.setText("Expires: " + formatDate(license.getExpiryDate()));

            // License File Path — hide if empty
            TextView tvFile = view.findViewById(R.id.tvLicenseFilePath);
            String filePath = license.getLicenseFilePath();
            if (filePath == null || filePath.trim().isEmpty()) {
                tvFile.setVisibility(View.GONE); // hide if no file
            } else {
                tvFile.setVisibility(View.VISIBLE);
                tvFile.setText("File: " + filePath);
            }
        }

        return view;
    }

    // 🔹 Helper method to format ISO date -> "Sep 05, 2025"
    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "-";

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(isoDate);

            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return isoDate; // fallback: show raw if parsing fails
        }
    }
}