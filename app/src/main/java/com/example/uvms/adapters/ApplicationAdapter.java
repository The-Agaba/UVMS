package com.example.uvms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.models.ApplicationItem;
import com.google.android.material.chip.Chip;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.AppVH> {

    public interface OnApplicationClickListener {
        void onApplicationClicked(ApplicationItem app);
    }

    private final Context ctx;
    private final List<ApplicationItem> items;
    private final OnApplicationClickListener listener;

    public ApplicationAdapter(Context ctx, List<ApplicationItem> items, OnApplicationClickListener listener) {
        this.ctx = ctx;
        this.items = items;
        this.listener = listener;
    }

    @NonNull @Override
    public AppVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_application, parent, false);
        return new AppVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppVH h, int pos) {
        ApplicationItem a = items.get(pos);
        h.title.setText(a.getTenderName());
        h.date.setText("Submitted: " + a.getSubmissionDate());
        h.status.setText(a.getStatus());
        h.itemView.setOnClickListener(v -> listener.onApplicationClicked(a));
    }

    @Override public int getItemCount() { return items.size(); }

    static class AppVH extends RecyclerView.ViewHolder {
        TextView title, date;
        Chip status;

        AppVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtApplicationTender);
            date = itemView.findViewById(R.id.txtApplicationDate);
            status = itemView.findViewById(R.id.chipStatus);
        }
    }
}