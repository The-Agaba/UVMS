package com.example.uvms.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

            // Vendor info from SharedPreferences
            SharedPreferences prefs = requireContext().getSharedPreferences("uvms_prefs", Context.MODE_PRIVATE);
            String firstName = prefs.getString("user_first_name", "N/A");
            String lastName = prefs.getString("user_last_name", "N/A");
            String tin = prefs.getString("user_tin_number", "N/A");
            String phone = prefs.getString("user_phone_number", "N/A");
            String company = prefs.getString("user_company_name", "N/A");
            String businessType = prefs.getString("business_type", "N/A");

            TextView tvVendorName = view.findViewById(R.id.tvVendorName);
            TextView tvVendorTIN = view.findViewById(R.id.tvVendorTIN);
            TextView tvVendorPhone = view.findViewById(R.id.tvVendorPhone);
            TextView tvVendorCompany = view.findViewById(R.id.tvVendorCompany);
            TextView tvVendorBusinessType = view.findViewById(R.id.tvVendorBusinessType);

            tvVendorName.setText("Vendor: " + firstName + " " + lastName);
            tvVendorTIN.setText("TIN: " + tin);
            tvVendorPhone.setText("Phone: " + phone);
            tvVendorCompany.setText("Company: " + company);
            tvVendorBusinessType.setText("Business Type: " + businessType);

            // Plot info
            TextView tvPlot = view.findViewById(R.id.tvPlotId);
            if (license.getApplication() != null) {
                tvPlot.setText("Plot ID: " + license.getApplication().getPlotId());
            } else {
                tvPlot.setText("Plot ID: N/A");
            }

            // License Status
            TextView tvStatus = view.findViewById(R.id.tvLicenseStatus);
            tvStatus.setText("Status: " + license.getSafeString(license.getStatus(), "N/A"));

            // Dates
            TextView tvIssue = view.findViewById(R.id.tvIssueDate);
            tvIssue.setText("Issued: " + formatDate(license.getIssueDate()));

            TextView tvExpiry = view.findViewById(R.id.tvExpiryDate);
            tvExpiry.setText("Expires: " + formatDate(license.getExpiryDate()));

            // File path
            TextView tvFile = view.findViewById(R.id.tvLicenseFilePath);
            String filePath = license.getLicenseFilePath();
            if (filePath == null || filePath.trim().isEmpty()) {
                tvFile.setVisibility(View.GONE);
            } else {
                tvFile.setVisibility(View.VISIBLE);
                tvFile.setText("File: " + filePath);
            }
        }

        return view;
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "-";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(isoDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return isoDate;
        }
    }
}
