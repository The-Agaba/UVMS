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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TenderAdapter extends RecyclerView.Adapter<TenderAdapter.TenderViewHolder> {

    private final Context context;
    private List<Tender> tenderList;
    private Map<Integer, String> collegeMap = new HashMap<>(); // id → name

    public TenderAdapter(Context context, List<Tender> tenderList) {
        this.context = context;
        this.tenderList = tenderList;
    }

    // allow updating collegeMap from fragment
    public void setCollegeMap(Map<Integer, String> collegeMap) {
        this.collegeMap = collegeMap != null ? collegeMap : new HashMap<>();
        notifyDataSetChanged();
    }

    public void updateList(List<Tender> tenders) {
        this.tenderList = tenders;
        notifyDataSetChanged();
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

        holder.title.setText(tender.getTitle());
        holder.description.setText(tender.getDescription());
        holder.id.setText("UDOM/TRD/" + tender.getTenderId());

        // College name lookup
        String collegeName = collegeMap.get(tender.getCollegeId());
        if (collegeName == null) {
            collegeName = "Unknown College (ID: " + tender.getCollegeId() + ")";
        }

        holder.deadline.setText("Deadline: " + tender.getDeadlineDate());

        // Set click listener for the whole card
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TenderDetailsActivity.class);
            intent.putExtra("tender_id", tender.getTenderId());
            intent.putExtra("college_name", collegeMap.get(tender.getCollegeId()));
            intent.putExtra("tender_title", tender.getTitle());
            intent.putExtra("tender_description", tender.getDescription());
            intent.putExtra("tender_post_date", tender.getCreatedAt());
            intent.putExtra("tender_deadline", tender.getDeadlineDate());
            context.startActivity(intent);
        });

        // Set click listener for "Apply" text view
        holder.applyTextView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ApplyTenderActivity.class);
            intent.putExtra("tender_id", tender.getTenderId());
            intent.putExtra("tender_title", tender.getTitle());
            context.startActivity(intent);
        });


    }


    @Override
    public int getItemCount() {
        return tenderList != null ? tenderList.size() : 0;
    }

    static class TenderViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, id, deadline, applyTextView;

        public TenderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            id = itemView.findViewById(R.id.tvTenderId);
            deadline = itemView.findViewById(R.id.tvDeadline);
            applyTextView = itemView.findViewById(R.id.tvApply);
        }
    }
}
