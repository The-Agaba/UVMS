package com.example.uvms.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uvms.R;
import com.example.uvms.adapters.ApplicationAdapter;
import com.example.uvms.models.ApplicationItem;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applications);

        RecyclerView rv = findViewById(R.id.recyclerApplications);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<ApplicationItem> applications = new ArrayList<>();
        applications.add(new ApplicationItem("101","Office Furniture Tender","2025-08-15","Pending"));
        applications.add(new ApplicationItem("102","IT Equipment Tender","2025-08-10","Approved"));

        ApplicationAdapter adapter = new ApplicationAdapter(this, applications, app -> {
            Intent i = new Intent(ApplicationsActivity.this, ApplicationDetailActivity.class);
            i.putExtra("app_id", app.getId());
            i.putExtra("tender_name", app.getTenderName());
            i.putExtra("submitted", app.getSubmissionDate());
            i.putExtra("status", app.getStatus());
            startActivity(i);
        });
        rv.setAdapter(adapter);
    }

}