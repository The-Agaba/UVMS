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

import java.util.List;

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
        holder.tvLicenseStatus.setText(license.getSafeString(license.getStatus(), "N/A"));
        holder.tvIssuedDate.setText(license.getSafeString(license.getIssueDate(), "-"));
        holder.tvExpiryDate.setText(license.getSafeString(license.getExpiryDate(), "-"));

        // Status bubble
        int statusColor = license.getStatusColor();
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

        // Renewal button click (optional)
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
}
