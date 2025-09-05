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
            TextView tvLicenseNumber = view.findViewById(R.id.tvLicenseNumber);
            tvLicenseNumber.setText(license.getSafeString(license.getLicenseNumber(), "N/A"));

            if (license.getVendor() != null) {
                TextView tvVendorName = view.findViewById(R.id.tvVendorName);
                tvVendorName.setText("Vendor: " + license.getSafeString(license.getVendor().getCompanyName(), "N/A"));

                TextView tvVendorTIN = view.findViewById(R.id.tvVendorTIN);
                tvVendorTIN.setText("TIN: " + license.getSafeString(license.getVendor().getTinNumber(), "N/A"));
            }

            if (license.getCollege() != null) {
                TextView tvCollege = view.findViewById(R.id.tvCollegeName);
                tvCollege.setText("College: " + license.getSafeString(license.getCollege().getCollegeName(), "N/A"));
            }

            if (license.getTender() != null) {
                TextView tvTender = view.findViewById(R.id.tvTenderTitle);
                tvTender.setText("Tender: " + license.getSafeString(license.getTender().getTitle(), "N/A"));
            }
        }

        return view;
    }
}
