package com.example.uvms.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.models.License;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.LicenseViewHolder> {

    private Context context;
    private List<License> licenseList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(License license);
    }

    public LicenseAdapter(Context context, List<License> licenseList, OnItemClickListener listener) {
        this.context = context;
        this.licenseList = licenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LicenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_license, parent, false);
        return new LicenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LicenseViewHolder holder, int position) {
        License license = licenseList.get(position);

        holder.tvLicenseId.setText(String.valueOf(license.getLicenseId()));

        // 🔹 Format dates nicely (Sep 05, 2025)
        holder.tvIssuedDate.setText("Issued: " + formatDate(license.getIssueDate()));
        holder.tvExpiryDate.setText("Expires: " + formatDate(license.getExpiryDate()));

        // Dynamically compute status if missing
        String status = license.getStatus() != null ? license.getStatus() :
                (license.isActive() ? "ACTIVE" : "EXPIRED");
        holder.tvLicenseStatus.setText(status);

        // Status bubble
        int statusColor;
        switch (status.toUpperCase()) {
            case "ACTIVE": statusColor = context.getColor(R.color.green); break;
            case "EXPIRED": statusColor = context.getColor(R.color.red); break;
            case "PENDING": statusColor = context.getColor(R.color.yellow); break;
            case "REJECTED": statusColor = context.getColor(R.color.gray); break;
            default: statusColor = context.getColor(R.color.gray); break;
        }

        holder.tvLicenseStatus.setTextColor(statusColor);
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(12f);
        bg.setColor(statusColor);
        holder.tvLicenseStatus.setBackground(bg);

        // Show/hide renewal button
        holder.btnRequestRenewal.setVisibility(license.isExpanded() ? View.VISIBLE : View.GONE);

        // Item click listener → open detail fragment
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(license);
        });

        // Renewal button click
        holder.btnRequestRenewal.setOnClickListener(v ->
                Toast.makeText(context,
                        "Renewal requested for License ID " + license.getLicenseId(),
                        Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return licenseList.size();
    }

    public static class LicenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvLicenseId, tvLicenseStatus, tvIssuedDate, tvExpiryDate;
        Button btnRequestRenewal;

        public LicenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLicenseId = itemView.findViewById(R.id.tv_license_id);
            tvLicenseStatus = itemView.findViewById(R.id.tv_license_status);
            tvIssuedDate = itemView.findViewById(R.id.tv_issued_date);
            tvExpiryDate = itemView.findViewById(R.id.tv_expiry_date);
            btnRequestRenewal = itemView.findViewById(R.id.btn_request_renewal);
        }
    }

    public void updateData(List<License> newList) {
        licenseList.clear();
        licenseList.addAll(newList);
        notifyDataSetChanged();
    }

    // 🔹 Helper to format ISO date into "Sep 05, 2025"
    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "-";

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(isoDate);

            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return isoDate; // fallback to raw string if parsing fails
        }
    }
}
