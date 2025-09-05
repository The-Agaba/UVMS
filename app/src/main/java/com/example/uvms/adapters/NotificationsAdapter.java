package com.example.uvms.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.models.Notification;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    public interface NotificationActionListener {
        void onMarkRead(Notification notification);
        void onDelete(Notification notification);
    }

    private final List<Notification> notifications;
    private final NotificationActionListener listener;

    public NotificationsAdapter(List<Notification> notifications, NotificationActionListener listener) {
        this.notifications = notifications;
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

        // Show "Mark as Read" only if notification is unread
        holder.btnMarkRead.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);

        // Expand/collapse message on item click
        holder.itemView.setOnClickListener(v -> {
            notification.setExpanded(!notification.isExpanded());
            notifyItemChanged(position);
        });

        // Mark as read action
        holder.btnMarkRead.setOnClickListener(v -> listener.onMarkRead(notification));

        // Delete action
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(notification));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    // Show first 10 words as preview
    private String getPreview(String message) {
        String[] words = message.split("\\s+");
        if (words.length <= 10) return message;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(words[i]).append(" ");
        }
        sb.append("...");
        return sb.toString();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, time;
        ImageButton btnMarkRead, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);
            time = itemView.findViewById(R.id.notification_time);
            btnMarkRead = itemView.findViewById(R.id.btn_mark_read);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
