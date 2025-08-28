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
import com.example.uvms.models.College;
import com.example.uvms.models.License;
import com.example.uvms.models.Plot;
import com.example.uvms.models.Tender;
import com.example.uvms.models.Vendor;

public class LicenseDetailFragment extends Fragment {

    private static final String ARG_LICENSE = "arg_license";
    private License license;

    private TextView tvLicenseNumber, tvStatus, tvIssuedDate, tvExpiryDate, tvFilePath;
    private TextView tvVendorName, tvVendorTIN;
    private TextView tvCollegeName;
    private TextView tvTenderTitle;
    private TextView tvPlotNumber;

    public LicenseDetailFragment() {}

    public static LicenseDetailFragment newInstance(License license) {
        LicenseDetailFragment fragment = new LicenseDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LICENSE, license);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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

        tvLicenseNumber = view.findViewById(R.id.tvDetailsLicenseNumber);
        tvStatus = view.findViewById(R.id.tvDetailsStatus);
        tvIssuedDate = view.findViewById(R.id.tvDetailsIssuedDate);
        tvExpiryDate = view.findViewById(R.id.tvDetailsExpiryDate);
        tvFilePath = view.findViewById(R.id.tvDetailsFilePath);

        tvVendorName = view.findViewById(R.id.tvVendorName);
        tvVendorTIN = view.findViewById(R.id.tvVendorTIN);

        tvCollegeName = view.findViewById(R.id.tvCollegeName);
        tvTenderTitle = view.findViewById(R.id.tvTenderTitle);
        tvPlotNumber = view.findViewById(R.id.tvPlotNumber);

        if (license != null) {
            tvLicenseNumber.setText("License Number: " + license.getSafeString(license.getLicenseNumber(), "N/A"));
            tvStatus.setText("Status: " + license.getSafeString(license.getStatus(), "N/A"));
            tvStatus.setTextColor(license.getStatusColor());

            tvIssuedDate.setText("Issued Date: " + license.getSafeString(license.getIssueDate(), "-"));
            tvExpiryDate.setText("Expiry Date: " + license.getSafeString(license.getExpiryDate(), "-"));
            tvFilePath.setText("License File: " + license.getSafeString(license.getLicenseFilePath(), "N/A"));

            Vendor vendor = license.getVendor();
            if (vendor != null) {
                tvVendorName.setText("Vendor: " + vendor.getCompanyName());
                tvVendorTIN.setText("TIN: " + vendor.getTinNumber());
            } else {
                tvVendorName.setText("Vendor: N/A");
                tvVendorTIN.setText("TIN: N/A");
            }

            College college = license.getCollege();
            tvCollegeName.setText(college != null ? "College: " + college.getCollegeName() : "College: N/A");

            Tender tender = license.getTender();
            tvTenderTitle.setText(tender != null ? "Tender: " + tender.getTitle() : "Tender: N/A");

            Plot plot = license.getPlot();
            tvPlotNumber.setText(plot != null ? "Plot Number: " + plot.getPlotNumber() : "Plot Number: N/A");
        }

        return view;
    }
}
