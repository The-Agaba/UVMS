package com.example.uvms.adapters;

import android.content.Context;
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

    private final Context context;
    private final List<License> licenseList;
    private final OnItemClickListener listener;

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

        if (license == null) return;

        // License ID
        holder.tvLicenseId.setText(license.getSafeString(license.getLicenseNumber(), "N/A"));

        // License Status
        String status = license.getSafeString(license.getStatus(), "EXPIRED").toUpperCase();
        holder.tvLicenseStatus.setText(status);

        // Status color mapping
        int statusColor = getStatusColor(status);

        // Status bubble & text
        holder.statusBubble.setBackgroundColor(statusColor);
        holder.tvLicenseStatus.setTextColor(statusColor);

        // Dates
        holder.tvIssuedDate.setText(formatDate(license.getIssueDate()));
        holder.tvExpiryDate.setText(formatDate(license.getExpiryDate()));

        // Always show renewal button
        holder.btnRequestRenewal.setVisibility(View.VISIBLE);

        // Card click → open detail fragment
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(license);
        });

        // Renewal button click
        holder.btnRequestRenewal.setOnClickListener(v ->
                Toast.makeText(context,
                        "Renewal requested for License " + license.getSafeString(license.getLicenseNumber(), "N/A"),
                        Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return licenseList.size();
    }

    public void updateData(List<License> newList) {
        licenseList.clear();
        if (newList != null) {
            licenseList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    // --- ViewHolder ---
    public static class LicenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvLicenseId, tvLicenseStatus, tvIssuedDate, tvExpiryDate;
        View statusBubble;
        Button btnRequestRenewal;

        public LicenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLicenseId = itemView.findViewById(R.id.tv_license_id);
            tvLicenseStatus = itemView.findViewById(R.id.tv_license_status);
            tvIssuedDate = itemView.findViewById(R.id.tv_issued_date);
            tvExpiryDate = itemView.findViewById(R.id.tv_expiry_date);
            statusBubble = itemView.findViewById(R.id.status_bubble);
            btnRequestRenewal = itemView.findViewById(R.id.btn_request_renewal);
        }
    }

    // --- Helpers ---

    private int getStatusColor(String status) {
        switch (status) {
            case "ACTIVE": return context.getColor(R.color.green);
            case "EXPIRED": return context.getColor(R.color.red);
            case "PENDING": return context.getColor(R.color.yellow);
            case "REJECTED": return context.getColor(R.color.gray);
            default: return context.getColor(R.color.gray);
        }
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "-";
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = input.parse(isoDate);
            SimpleDateFormat output = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return output.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return isoDate;
        }
    }
}
