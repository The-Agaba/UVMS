package com.example.uvms.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.activities.ApplyTenderActivity;
import com.example.uvms.adapters.TenderAdapter;
import com.example.uvms.models.Tender;

import java.util.ArrayList;
import java.util.List;

public class TendersFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tenders, container, false);

        RecyclerView rv = view.findViewById(R.id.recyclerTenders);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Tender> tenders = new ArrayList<>();
        tenders.add(new Tender("1", "Supply of Office Furniture", "2025-09-01", "$10,000"));
        tenders.add(new Tender("2", "IT Equipment Procurement", "2025-09-10", "$25,000"));
        tenders.add(new Tender("3", "Supply of Office Furniture", "2025-09-01", "$10,000"));
        tenders.add(new Tender("4", "IT Equipment Procurement", "2025-09-10", "$25,000"));

        TenderAdapter adapter = new TenderAdapter(getContext(), tenders, new TenderAdapter.OnTenderClickListener() {
            @Override
            public void onTenderClicked(Tender t) {
                // Open Apply page as well when whole card tapped (you could also open details first)
                Intent i = new Intent(getContext(), ApplyTenderActivity.class);
                i.putExtra("tender_id", t.getId());
                i.putExtra("title", t.getTitle());
                i.putExtra("deadline", t.getDeadline());
                i.putExtra("budget", t.getBudget());
                startActivity(i);
            }

            @Override
            public void onApplyClicked(Tender t) {
                onTenderClicked(t); // same destination
            }
        });

        rv.setAdapter(adapter);
        return view;
    }
}