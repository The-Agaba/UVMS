package com.example.uvms.activities;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView logo = findViewById(R.id.logo);
        if (logo != null && logo.getDrawable() instanceof AnimatedVectorDrawable) {
            AnimatedVectorDrawable anim = (AnimatedVectorDrawable) logo.getDrawable();
            anim.start();
        }

        // delay and navigate to MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Splash_ScreenActivity.this, Landing_ScreenActivity.class);
            startActivity(intent);
            finish();
        }, 3500); // 3 seconds
    }
}
