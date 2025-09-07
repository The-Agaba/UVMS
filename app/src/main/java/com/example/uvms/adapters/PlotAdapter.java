package com.example.uvms.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.models.Plot;

import java.util.List;

public class PlotAdapter extends RecyclerView.Adapter<PlotAdapter.PlotViewHolder> {

    private final Context context;
    private final List<Plot> plotList;
    private final OnPlotClickListener listener;

    public interface OnPlotClickListener {
        void onPlotClick(Plot plot);
    }

    public PlotAdapter(Context context, List<Plot> plotList, OnPlotClickListener listener) {
        this.context = context;
        this.plotList = plotList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Using a simple TextView as item
        TextView tv = new TextView(context);
        tv.setPadding(24, 24, 24, 24);
        tv.setTextSize(16f);
        tv.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new PlotViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull PlotViewHolder holder, int position) {
        Plot plot = plotList.get(position);
        String info = plot.getPlotNumber() + " - " + plot.getLocationDescription() +
                " (" + (plot.isAvailable() ? "Available" : "Not Available") + ")";
        holder.textView.setText(info);

        holder.textView.setOnClickListener(v -> listener.onPlotClick(plot));
    }

    @Override
    public int getItemCount() {
        return plotList.size();
    }

    public void updateData(List<Plot> updatedList) {
        plotList.clear();
        plotList.addAll(updatedList);
        notifyDataSetChanged();
    }

    public static class PlotViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public PlotViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
