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
import com.example.uvms.models.Vendor;
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
            Vendor vendor = license.getVendor();
            TextView tvVendorName = view.findViewById(R.id.tvVendorName);
            TextView tvVendorTIN = view.findViewById(R.id.tvVendorTIN);
            TextView tvVendorPhone = view.findViewById(R.id.tvVendorPhone);
            TextView tvVendorCompany = view.findViewById(R.id.tvVendorCompany);
           TextView tvVendorBusinessType = view.findViewById(R.id.tvVendorBusinessType);

            if (vendor != null) {
                tvVendorName.setText("Vendor: " + vendor.getFirstName() + " " + vendor.getLastName());
                tvVendorTIN.setText("TIN: " + vendor.getTinNumber());
                tvVendorPhone.setText("Phone: " + vendor.getPhoneNumber());
                tvVendorCompany.setText("Company: " + vendor.getCompanyName());
                tvVendorBusinessType.setText("Business Type: " + vendor.getBusinessType());
            } else {
                tvVendorName.setText("Vendor: N/A");
                tvVendorTIN.setText("TIN: N/A");
                tvVendorPhone.setText("Phone: N/A");
                tvVendorCompany.setText("Company: N/A");
                tvVendorBusinessType.setText("Business Type: N/A");
            }

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
