package com.example.uvms.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.Pdf_Util;
import com.example.uvms.R;
import com.example.uvms.models.Application;
import com.example.uvms.models.License;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ContractViewHolder> {

    private final Activity activity; // Need Activity for SAF
    private final List<License> licenseList;
    private final OnLicenseClickListener listener;

    private final SimpleDateFormat inputFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private final SimpleDateFormat outputFormat =
            new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

    public interface OnLicenseClickListener {
        void onLicenseClick(License license);
    }

    public ContractAdapter(Activity activity, List<License> licenseList, OnLicenseClickListener listener) {
        this.activity = activity;
        this.licenseList = licenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_contract, parent, false);
        return new ContractViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContractViewHolder holder, int position) {
        License license = licenseList.get(position);

        // Clear previous details
        if (holder.licenseDetails != null) holder.licenseDetails.removeAllViews();

        bindLicenseDetails(holder, license);

        // Item click listener
        holder.itemView.setOnClickListener(v -> listener.onLicenseClick(license));

        // Save PDF button: launch "Save As" dialog
        holder.btnSavePdf.setOnClickListener(v -> {
            String suggestedFileName = "License_" + license.getLicenseNumber() + ".pdf";
            Pdf_Util.promptSavePdf(activity, suggestedFileName);
        });
    }

    @Override
    public int getItemCount() {
        return licenseList.size();
    }

    public void updateData(List<License> updatedList) {
        licenseList.clear();
        licenseList.addAll(updatedList);
        notifyDataSetChanged();
    }

    // --- Helper Methods ---
    private void bindLicenseDetails(ContractViewHolder holder, License license) {
        addDetail(holder.licenseDetails, "License Number: ", license.getSafeString(license.getLicenseNumber(), "-"));
        addDetail(holder.licenseDetails, "Vendor Name: ", license.getVendor() != null ? license.getVendorFullName() : "-");
        addDetail(holder.licenseDetails, "Company: ", license.getVendor() != null ? license.getVendor().getCompanyName() : "-");
        addDetail(holder.licenseDetails, "Business Type: ", license.getVendor() != null ? license.getVendor().getBusinessType() : "-");
        addDetail(holder.licenseDetails, "TIN: ", license.getVendor() != null ? license.getVendor().getTinNumber() : "-");
        addDetail(holder.licenseDetails, "Business Address: ", license.getVendor() != null ? license.getVendor().getBusinessAddress() : "-");
        addDetail(holder.licenseDetails, "Phone: ", license.getVendor() != null ? license.getVendor().getPhoneNumber() : "-");
        addDetail(holder.licenseDetails, "Email: ", license.getVendor() != null ? license.getVendor().getEmail() : "-");

        addDetail(holder.licenseDetails, "Issue Date: ", formatDate(license.getIssueDate()));
        addDetail(holder.licenseDetails, "Expiry Date: ", formatDate(license.getExpiryDate()));

        Application app = license.getApplication();
        addDetail(holder.licenseDetails, "Plot: ", app != null ? app.getPlotInfo() : "-");

        addDetail(holder.licenseDetails, "Status: ", safeText(license.getStatus()));
    }

    private String formatDate(String isoDate) {
        if (isoDate == null) return "-";
        try {
            Date date = inputFormat.parse(isoDate);
            return date != null ? outputFormat.format(date) : "-";
        } catch (ParseException e) {
            Log.e("ContractAdapter", "Date parse error: " + e.getMessage());
            return "-";
        }
    }

    private String safeText(String value) {
        return (value == null || value.trim().isEmpty()) ? "-" : value;
    }

    private void addDetail(LinearLayout container, String title, String value) {
        TextView tv = new TextView(activity);
        tv.setText(getBoldTitle(title, value));
        tv.setTextSize(14f);
        tv.setTextColor(0xFF333333);
        tv.setPadding(0, 4, 0, 4);
        container.addView(tv);
    }

    private SpannableString getBoldTitle(String title, String value) {
        SpannableString ss = new SpannableString(title + value);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    // --- ViewHolder ---
    public static class ContractViewHolder extends RecyclerView.ViewHolder {
        LinearLayout licenseDetails;
        ImageView qrCode;
        MaterialButton btnSavePdf;

        public ContractViewHolder(@NonNull View itemView) {
            super(itemView);
            licenseDetails = itemView.findViewById(R.id.licenseDetails);
            qrCode = itemView.findViewById(R.id.qrCode);
            btnSavePdf = itemView.findViewById(R.id.btnPrint); // Reusing layout button
        }
    }
}
