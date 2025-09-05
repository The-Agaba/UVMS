package com.example.uvms.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uvms.R;

public class Splash_ScreenActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "uvms_prefs";
    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        // Apply system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Start logo animation if available
        ImageView logo = findViewById(R.id.logo);
        if (logo != null && logo.getDrawable() instanceof AnimatedVectorDrawable) {
            ((AnimatedVectorDrawable) logo.getDrawable()).start();
        }

        // Delay and navigate based on login status
        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

            Intent intent = new Intent(Splash_ScreenActivity.this,
                    isLoggedIn ? HomeActivity.class : Landing_ScreenActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
