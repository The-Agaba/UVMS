package com.example.uvms.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uvms.R;
import com.example.uvms.models.Notification;
import java.util.ArrayList;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> implements Filterable {

    private List<Notification> notifications;         // Displayed list
    private final List<Notification> notificationsFull; // Original list
    private final OnNotificationActionListener listener;

    public interface OnNotificationActionListener {
        void onMarkRead(Notification notification);
        void onDelete(Notification notification);
    }

    public NotificationsAdapter(List<Notification> notifications, OnNotificationActionListener listener) {
        this.notifications = new ArrayList<>(notifications);
        this.notificationsFull = new ArrayList<>(notifications);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.isExpanded() ? notification.getMessage() : getPreview(notification.getMessage()));
        holder.time.setText(notification.getCreatedAt());

        holder.btnMarkRead.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);

        // Toggle expand/collapse
        holder.itemView.setOnClickListener(v -> {
            notification.setExpanded(!notification.isExpanded());
            notifyItemChanged(position);
        });

        // Mark as read
        holder.btnMarkRead.setOnClickListener(v -> {
            notification.setRead(true);
            listener.onMarkRead(notification);
            notifyItemChanged(position);
        });

        // Delete notification
        holder.btnDelete.setOnClickListener(v -> {
            listener.onDelete(notification);
            notifications.remove(notification);
            notificationsFull.remove(notification);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private String getPreview(String message) {
        String[] words = message.split("\\s+");
        if (words.length <= 10) return message;
        return String.join(" ", java.util.Arrays.copyOfRange(words, 0, 10)) + "...";
    }

    // 🔹 Implement filter
    @Override
    public Filter getFilter() {
        return notificationFilter;
    }

    private final Filter notificationFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Notification> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(notificationsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Notification notif : notificationsFull) {
                    if (notif.getTitle().toLowerCase().contains(filterPattern) ||
                            notif.getMessage().toLowerCase().contains(filterPattern)) {
                        filteredList.add(notif);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifications.clear();
            //noinspection unchecked
            notifications.addAll((List<Notification>) results.values);
            notifyDataSetChanged();
        }
    };

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, time;
        ImageButton btnMarkRead, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);
            time = itemView.findViewById(R.id.notification_time);
            btnMarkRead = itemView.findViewById(R.id.btn_mark_read);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
