package com.example.uvms.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.activities.TenderDetailsActivity;
import com.example.uvms.activities.ApplyTenderActivity;
import com.example.uvms.models.Tender;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.List;

public class TenderAdapter extends RecyclerView.Adapter<TenderAdapter.TenderViewHolder> {

    private Context context;
    public List<Tender> tenderList;

    public TenderAdapter(Context context, List<Tender> tenderList) {
        this.context = context;
        this.tenderList = tenderList;
    }

    @NonNull
    @Override
    public TenderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tender, parent, false);
        return new TenderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TenderViewHolder holder, int position) {
        Tender tender = tenderList.get(position);

        holder.tvTitle.setText(tender.title);
        holder.tvBuyer.setText(tender.buyer);
        holder.tvTenderId.setText(tender.id);
        holder.chipStatus.setText(tender.status);
        holder.chipCategory.setText(tender.category);
        holder.chipLocation.setText(tender.location);
        holder.tvDeadline.setText("Closes: " + tender.deadline);
        holder.tvBudget.setText(tender.budget);

        // View details
        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TenderDetailsActivity.class);
            intent.putExtra("tenderId", tender.id);
            intent.putExtra("tenderTitle", tender.title);
            intent.putExtra("tenderBuyer", tender.buyer);
            intent.putExtra("tenderCategory", tender.category);
            intent.putExtra("tenderLocation", tender.location);
            intent.putExtra("tenderDeadline", tender.deadline);
            intent.putExtra("tenderStatus", tender.status);
            intent.putExtra("tenderDocUrl", tender.documentUrl);
            context.startActivity(intent);
        });

        // Apply
        holder.btnApply.setOnClickListener(v -> {
            Intent intent = new Intent(context, ApplyTenderActivity.class);
            intent.putExtra("tenderId", tender.id);
            intent.putExtra("tenderDocUrl", tender.documentUrl);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tenderList.size();
    }

    static class TenderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBuyer, tvTenderId, tvDeadline, tvBudget;
        Chip chipStatus, chipCategory, chipLocation;
        MaterialButton btnView, btnApply;

        public TenderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBuyer = itemView.findViewById(R.id.tvBuyer);
            tvTenderId = itemView.findViewById(R.id.tvTenderId);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvBudget = itemView.findViewById(R.id.tvBudget);

            chipStatus = itemView.findViewById(R.id.chipStatus);
            chipCategory = itemView.findViewById(R.id.chipCategory);
            chipLocation = itemView.findViewById(R.id.chipLocation);

            btnView = itemView.findViewById(R.id.btnView);
            btnApply = itemView.findViewById(R.id.btnApply);
        }
    }
}
