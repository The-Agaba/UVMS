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

public class LicenseDetailFragment extends Fragment {

    private static final String ARG_LICENSE = "arg_license";

    private License license;

    // --- Views ---
    private TextView tvLicenseNumber, tvLicenseId, tvApplicationId, tvVendorId,
            tvStatus, tvIssuedDate, tvExpiryDate, tvFilePath;

    public LicenseDetailFragment() {
        // Required empty public constructor
    }

    /** Create fragment with License object */
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

        // Initialize views
        tvLicenseNumber = view.findViewById(R.id.tvDetailsLicenseNumber);
        tvLicenseId = view.findViewById(R.id.tvDetailsLicenseId);
        tvApplicationId = view.findViewById(R.id.tvDetailsApplicationId);
        tvVendorId = view.findViewById(R.id.tvDetailsVendorId);
        tvStatus = view.findViewById(R.id.tvDetailsStatus);
        tvIssuedDate = view.findViewById(R.id.tvDetailsIssuedDate);
        tvExpiryDate = view.findViewById(R.id.tvDetailsExpiryDate);
        tvFilePath = view.findViewById(R.id.tvDetailsFilePath);

        // Bind data safely
        if (license != null) {
            tvLicenseNumber.setText("License Number: " + license.getSafeString(license.getLicenseNumber(), "N/A"));
            tvLicenseId.setText("License ID: " + license.getLicenseId());
            tvApplicationId.setText("Application ID: " + license.getApplicationId());
            tvVendorId.setText("Vendor ID: " + license.getVendorId());
            tvStatus.setText("Status: " + license.getSafeString(license.getStatus(), "N/A"));
            tvStatus.setTextColor(license.getStatusColor());
            tvIssuedDate.setText("Issued Date: " + license.getSafeString(license.getIssueDate(), "-"));
            tvExpiryDate.setText("Expiry Date: " + license.getSafeString(license.getExpiryDate(), "-"));
            tvFilePath.setText("License File Path: " + license.getSafeString(license.getLicenseFilePath(), "N/A"));
        }

        return view;
    }
}