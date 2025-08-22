package com.example.uvms.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.uvms.R;
import com.example.uvms.fragments.HomeFragment;
import com.example.uvms.fragments.TendersFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        // Set default fragment on launch
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, new HomeFragment())
                .commit();

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            Fragment selectedFragment = null;

            if (id == R.id.profileActivity) {
                // Launch ProfileActivity
                startActivity(new Intent(this, com.example.uvms.activities.ProfileActivity.class));
                return true;
            } else if (id == R.id.businessFragment) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.tendersFragment) {
                selectedFragment = new TendersFragment();
            }
            // Add more else-if for other fragments if needed

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainContainer, selectedFragment)
                        .commit();
            }

            return true;
        });
    }
}
