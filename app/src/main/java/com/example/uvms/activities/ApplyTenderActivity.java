package com.example.uvms.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uvms.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ApplyTenderActivity extends AppCompatActivity {

    private String tenderId, title, deadline, budget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_tender);

        tenderId = getIntent().getStringExtra("tender_id");
        title    = getIntent().getStringExtra("title");
        deadline = getIntent().getStringExtra("deadline");
        budget   = getIntent().getStringExtra("budget");

        TextView tvTitle = findViewById(R.id.tvTenderTitle);
        TextView chipDeadline = findViewById(R.id.chipTenderDeadline);
        TextView chipBudget = findViewById(R.id.chipTenderBudget);
        TextInputEditText editBid = findViewById(R.id.editBidAmount);
        TextInputEditText editNotes = findViewById(R.id.editProposalNotes);
        MaterialButton btnAttach = findViewById(R.id.btnAttachDoc);
        MaterialButton btnSubmit = findViewById(R.id.btnSubmitApplication);

        tvTitle.setText(title);
        chipDeadline.setText("Deadline: " + deadline);
        chipBudget.setText("Budget: " + budget);

        btnAttach.setOnClickListener(v ->
                Toast.makeText(this, "Attach flow (TODO)", Toast.LENGTH_SHORT).show()
        );

        btnSubmit.setOnClickListener(v -> {
            String bid = editBid.getText() == null ? "" : editBid.getText().toString().trim();
            String notes = editNotes.getText() == null ? "" : editNotes.getText().toString().trim();
            if (bid.isEmpty()) {
                editBid.setError("Enter a bid amount");
                return;
            }
            // TODO: call your API here
            Toast.makeText(this, "Submitted application for " + title, Toast.LENGTH_LONG).show();
            finish();
        });
    }
}