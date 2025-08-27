package com.example.uvms.activities;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uvms.R;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Enable edge-to-edge display
        EdgeToEdge.enable(this);

        // Apply window insets safely
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contactActivity), (v, insets) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                android.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()).toPlatformInsets();
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            }
            return insets;
        });

        // Set all text using string resources
        TextView title = findViewById(R.id.contactTitle);
        TextView emailText = findViewById(R.id.emailText);
        TextView phoneText = findViewById(R.id.phoneText);
        TextView addressText = findViewById(R.id.addressText);
        TextView additionalInfo = findViewById(R.id.additionalInfo);

        title.setText(R.string.contact_title);
        emailText.setText(R.string.contact_email);
        phoneText.setText(R.string.contact_phone);
        addressText.setText(R.string.contact_address);
        additionalInfo.setText(R.string.contact_additional_info);
    }
}
