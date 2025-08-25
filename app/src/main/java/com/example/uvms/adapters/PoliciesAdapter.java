package com.example.uvms.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.models.Policy;

import java.util.ArrayList;
import java.util.List;

public class PoliciesAdapter extends RecyclerView.Adapter<PoliciesAdapter.PolicyViewHolder> {

    private List<Policy> policyList;        // current displayed list
    private List<Policy> originalPolicies;  // full backup list

    public PoliciesAdapter(List<Policy> policies) {
        this.policyList = policies != null ? policies : new ArrayList<>();
        this.originalPolicies = new ArrayList<>(this.policyList);
    }

    // Update data from API
    public void updateData(List<Policy> newPolicies) {
        if (newPolicies == null) return;

        policyList.clear();
        policyList.addAll(newPolicies);

        originalPolicies.clear();
        originalPolicies.addAll(newPolicies);

        notifyDataSetChanged();
    }

    // Filter by search query and category
    public void filter(String query, String category) {
        List<Policy> filteredList = new ArrayList<>();

        for (Policy policy : originalPolicies) {
            boolean matchesQuery = query == null || query.isEmpty() ||
                    policy.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    policy.getContent().toLowerCase().contains(query.toLowerCase());

            boolean matchesCategory = category.equalsIgnoreCase("All") ||
                    (policy.getCategory() != null &&
                            policy.getCategory().equalsIgnoreCase(category));

            if (matchesQuery && matchesCategory) {
                filteredList.add(policy);
            }
        }

        policyList.clear();
        policyList.addAll(filteredList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PolicyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_policy_review, parent, false);
        return new PolicyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PolicyViewHolder holder, int position) {
        Policy policy = policyList.get(position);

        holder.titleTextView.setText(policy.getTitle());
        holder.contentTextView.setText(policy.getContent());
        holder.scopeTextView.setText("Scope: " + policy.getScope());
        holder.dateTextView.setText("Posted on: " + policy.getDatePosted());

        if (policy.getCollegeId() != null) {
            holder.collegeIdTextView.setVisibility(View.VISIBLE);
            holder.collegeIdTextView.setText("College ID: " + policy.getCollegeId());
        } else {
            holder.collegeIdTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return policyList == null ? 0 : policyList.size();
    }

    static class PolicyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, scopeTextView, collegeIdTextView, dateTextView;

        public PolicyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_view_title);
            contentTextView = itemView.findViewById(R.id.text_view_content);
            scopeTextView = itemView.findViewById(R.id.text_view_scope);
            collegeIdTextView = itemView.findViewById(R.id.text_view_college_id);
            dateTextView = itemView.findViewById(R.id.text_view_date);
        }
    }
}