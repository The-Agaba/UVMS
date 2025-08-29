package com.example.uvms.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.adapters.NotificationsAdapter;
import com.example.uvms.api.NotificationApiService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment implements Filterable {





    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;

    // 🔹 Listener to notify HomeActivity about changes
    private OnNotificationsLoadedListener listener;
    private ProgressBar progressBar;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    //  Define callback interface for badge updates
    public interface OnNotificationsLoadedListener {
        void onNotificationsLoaded(List<Notification> notifications);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Initialize the listener
        if (context instanceof OnNotificationsLoadedListener) {
            listener = (OnNotificationsLoadedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null; // avoid memory leaks
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = view.findViewById(R.id.progressBar);

        fetchNotifications(); //  Load notifications when fragment starts

        SearchView searchView = view.findViewById(R.id.searchViewMessages);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null) adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) adapter.getFilter().filter(newText);
                return false;
            }
        });


        return view;
    }

    private void fetchNotifications() {
        progressBar.setVisibility(View.VISIBLE);

        NotificationApiService apiService = RetrofitClient.getInstance()
                .create(NotificationApiService.class);

        Call<List<Notification>> call = apiService.getNotifications();
        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Notification> notifications = response.body();

                    //  Notify HomeActivity about the list (so it can update badge)
                    if (listener != null) {
                        listener.onNotificationsLoaded(notifications);
                    }

                    adapter = new NotificationsAdapter(notifications, new NotificationsAdapter.OnNotificationActionListener() {
                        @Override
                        public void onMarkRead(Notification notification) {
                            notification.setRead(true);

                            adapter.notifyDataSetChanged();

                            // 🔹 Update badge count in HomeActivity
                            if (listener != null) listener.onNotificationsLoaded(notifications);
                        }

                        @Override
                        public void onDelete(Notification notification) {
                            notifications.remove(notification);
                            adapter.notifyDataSetChanged();

                            //  Update badge count in HomeActivity
                            if (listener != null) listener.onNotificationsLoaded(notifications);
                        }
                    });

                    recyclerView.setAdapter(adapter);

                } else {
                    if(isAdded()) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("ERROR")
                                .setMessage("Failed to load your notifications")
                                .setPositiveButton("Retry", (dialog, which) -> fetchNotifications())
                                .setNegativeButton("Cancel", (dialog, which) -> {
                                    dialog.dismiss();

                                })
                                .setCancelable(false)
                                .show();

                    }

                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                new AlertDialog.Builder(requireContext())
                        .setTitle("ERROR")
                        .setMessage("Check your connection")
                        .setPositiveButton("Retry", (dialog, which) -> fetchNotifications())
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();

                        })
                        .setCancelable(false)
                        .show();

                Log.e("ContractFragment", "API Error: ", t);

             }
        });
    }
}
