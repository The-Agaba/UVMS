package com.example.uvms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_plot, parent, false);
        return new PlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlotViewHolder holder, int position) {
        Plot plot = plotList.get(position);

        holder.tvPlotNumber.setText(plot.getPlotNumber());
        holder.tvPlotLocation.setText(plot.getLocationDescription());
        holder.tvPlotAvailability.setText(plot.isAvailable() ? "Available" : "Not Available");

        holder.itemView.setOnClickListener(v -> listener.onPlotClick(plot));
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

    static class PlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlotNumber, tvPlotLocation, tvPlotAvailability;

        public PlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlotNumber = itemView.findViewById(R.id.tvPlotNumber);
            tvPlotLocation = itemView.findViewById(R.id.tvPlotLocation);
            tvPlotAvailability = itemView.findViewById(R.id.tvPlotAvailability);
        }
    }
}
