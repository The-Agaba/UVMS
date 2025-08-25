package com.example.uvms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.models.Tender;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.List;

public class TenderAdapter extends RecyclerView.Adapter<TenderAdapter.TenderVH> {

    public interface OnTenderClickListener {
        void onTenderClicked(Tender tender);
        void onApplyClicked(Tender tender);
    }

    private final Context context;
    private final List<Tender> tenders;
    private final OnTenderClickListener listener;

    public TenderAdapter(Context context, List<Tender> tenders, OnTenderClickListener listener) {
        this.context = context;
        this.tenders = tenders;
        this.listener = listener;
    }

    @NonNull @Override
    public TenderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_tender, parent, false);
        return new TenderVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TenderVH h, int pos) {
        Tender t = tenders.get(pos);
        h.title.setText(t.getTitle());
        h.chipDeadline.setText("Deadline: " + t.getDeadline());
        h.chipBudget.setText("Budget: " + t.getBudget());

        h.itemView.setOnClickListener(v -> listener.onTenderClicked(t));
        h.btnApply.setOnClickListener(v -> listener.onApplyClicked(t));
    }

    @Override public int getItemCount() { return tenders.size(); }

    static class TenderVH extends RecyclerView.ViewHolder {
        TextView title;
        Chip chipDeadline, chipBudget;
        MaterialButton btnApply;

        TenderVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTenderTitle);
            chipDeadline = itemView.findViewById(R.id.chipDeadline);
            chipBudget = itemView.findViewById(R.id.chipBudget);
            btnApply = itemView.findViewById(R.id.btnApplyTender);
        }
    }
}