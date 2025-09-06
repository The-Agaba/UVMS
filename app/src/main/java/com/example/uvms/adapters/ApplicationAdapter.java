package com.example.uvms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.models.Application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private final Context context;
    private List<Application> applicationList;

    public ApplicationAdapter(Context context, List<Application> applicationList) {
        this.context = context;
        this.applicationList = applicationList;
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        Application item = applicationList.get(position);

        holder.tvAppId.setText("Application #" + item.getApplicationId());

        // Format date
        holder.tvDate.setText("Submitted on: " + formatDate(item.getApplicationDate()));

        holder.tvSubmittedContract.setText(item.getSubmittedContractPath() != null
                ? "Submitted Contract: " + item.getSubmittedContractPath()
                : "Submitted Contract: None");

        holder.tvApprovedContract.setText(item.getApprovedContractPath() != null
                ? "Approved Contract: " + item.getApprovedContractPath()
                : "Approved Contract: Awaiting approval");

        // Set status text and color dynamically
        holder.tvStatus.setText(item.getStatus() != null ? item.getStatus() : "PENDING");
        int statusColor;
        switch (holder.tvStatus.getText().toString().toUpperCase(Locale.ROOT)) {
            case "APPROVED":
                statusColor = context.getColor(R.color.uvmsSuccessGreen);
                break;
            case "REJECTED":
                statusColor = context.getColor(R.color.ErrorRed);
                break;
            default:
                statusColor = context.getColor(R.color.uvmsAccentCyan); // PENDING or unknown
        }
        holder.tvStatus.setTextColor(statusColor);
    }

    @Override
    public int getItemCount() {
        return applicationList != null ? applicationList.size() : 0;
    }

    public void updateList(List<Application> newList) {
        this.applicationList = newList;
        notifyDataSetChanged();
    }

    // --- Helper for date formatting ---
    private String formatDate(String isoDate) {
        if (isoDate == null) return "N/A";
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
        // ✅ Wrap EAT in quotes so it’s treated as text
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy • HH:mm 'EAT'", Locale.getDefault());
        try {
            Date date = inputFormat.parse(isoDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return isoDate;
        }
    }

    static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppId, tvDate, tvStatus, tvSubmittedContract, tvApprovedContract;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppId = itemView.findViewById(R.id.tvAppId);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvSubmittedContract = itemView.findViewById(R.id.tvSubmittedContract);
            tvApprovedContract = itemView.findViewById(R.id.tvApprovedContract);
        }
    }
}
