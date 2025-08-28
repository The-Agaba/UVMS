package com.example.uvms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.models.Application;
import com.google.android.material.chip.Chip;
import android.widget.TextView;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.AppVH> {

    public interface OnApplicationClickListener {
        void onApplicationClicked(Application app);
    }

    private final Context ctx;
    private final List<Application> items;
    private final OnApplicationClickListener listener;

    public ApplicationAdapter(Context ctx, List<Application> items, OnApplicationClickListener listener) {
        this.ctx = ctx;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_application, parent, false);
        return new AppVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppVH h, int pos) {
        Application a = items.get(pos);

        h.title.setText(a.getSafeString(a.getFeedback(), "Application #" + a.getApplicationId()));
        h.date.setText("Submitted: " + a.getSafeString(a.getApplicationDate(), "N/A"));

        h.status.setText(a.getSafeString(a.getStatus(), "N/A"));
        h.status.setChipBackgroundColorResource(android.R.color.transparent); // remove default bg
        h.status.setTextColor(a.getStatusColor());

        h.itemView.setOnClickListener(v -> listener.onApplicationClicked(a));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

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

    // --- Update data dynamically ---
    public void updateData(List<Application> newList) {
        items.clear();
        items.addAll(newList);
        notifyDataSetChanged();
    }
}
