package com.example.uvms.adapters;

import com.example.uvms.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.models.License;

import java.util.List;

public class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.LicenseViewHolder> {

    private Context context;
    private List<License> licenseList;

    public LicenseAdapter(Context context, List<License> licenseList) {
        this.context = context;
        this.licenseList = licenseList;
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

        // ✅ Always use String.valueOf() to prevent resource ID crash
        holder.tvLicenseId.setText(String.valueOf(license.getLicenseId()));
        holder.tvLicenseStatus.setText(String.valueOf(license.getStatus()));
        holder.tvIssuedDate.setText(String.valueOf(license.getIssueDate()));
        holder.tvExpiryDate.setText(String.valueOf(license.getExpiryDate()));

        // If your model returns a color int, keep it
        holder.tvLicenseStatus.setBackgroundColor(license.getStatusColor());

        holder.btnRequestRenewal.setOnClickListener(v ->
                Toast.makeText(context, "Renewal requested for " + license.getLicenseId(), Toast.LENGTH_SHORT).show()
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
