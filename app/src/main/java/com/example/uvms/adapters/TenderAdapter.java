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
import com.example.uvms.activities.ApplyTenderActivity;
import com.example.uvms.activities.TenderDetailsActivity;
import com.example.uvms.models.Tender;
import java.util.List;

public class TenderAdapter extends RecyclerView.Adapter<TenderAdapter.TenderViewHolder> {

    private final Context context;
    private List<Tender> tenderList;

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

        holder.tvTitle.setText(tender.getTitle() != null ? tender.getTitle() : "N/A");
        holder.tvDescription.setText(tender.getDescription() != null ? tender.getDescription() : "N/A");
        holder.tvTenderId.setText(String.valueOf(tender.getTenderId()));
        holder.tvDeadline.setText("Closes: " + (tender.getDeadlineDate() != null ? tender.getDeadlineDate() : "N/A"));

        // Open details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TenderDetailsActivity.class);
            intent.putExtra("tenderId", tender.getTenderId());
            intent.putExtra("tenderTitle", tender.getTitle());
            intent.putExtra("tenderBuyer", tender.getCreatedBy() != null ? tender.getCreatedBy().getName() : "N/A");
            intent.putExtra("tenderStatus", tender.getStatus());
            intent.putExtra("tenderDeadline", tender.getDeadlineDate());
            intent.putExtra("tenderDocUrl", tender.getContractTemplatePath());
            intent.putExtra("tenderCollege", tender.getCollegeName());
            context.startActivity(intent);
        });

        // Apply button
        holder.tvApply.setOnClickListener(v -> {
            Intent intent = new Intent(context, ApplyTenderActivity.class);
            intent.putExtra("tenderId", tender.getTenderId());
            intent.putExtra("tenderDocUrl", tender.getContractTemplatePath());
            intent.putExtra("tenderCollege", tender.getCollegeName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tenderList != null ? tenderList.size() : 0;
    }

    public void updateList(List<Tender> newTenders) {
        this.tenderList = newTenders;
        notifyDataSetChanged();
    }

    static class TenderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTenderId, tvDeadline, tvDescription, tvApply;

        public TenderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTenderId = itemView.findViewById(R.id.tvTenderId);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvApply = itemView.findViewById(R.id.tvApply);
        }
    }
}
