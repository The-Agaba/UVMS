package com.example.uvms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uvms.R;
import com.example.uvms.slide.SlideAdapter;
import com.example.uvms.slide.SlideItem;

import java.util.ArrayList;
import java.util.List;

public class Landing_ScreenActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private SlideAdapter slideAdapter;
    private Handler slideHandler = new Handler();
    private Runnable slideRunnable;
    private final int slideDelay = 2000; // 2 seconds

    Button btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_landing_screen);


        btnGetStarted = findViewById(R.id.btnGetStarted);
        viewPager = findViewById(R.id.viewPager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainConstraint), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int originalLeft = v.getPaddingLeft();
            int originalTop = v.getPaddingTop();
            int originalRight = v.getPaddingRight();
            int originalBottom = v.getPaddingBottom();
            v.setPadding( systemBars.left + originalLeft,
                    systemBars.top + originalTop,
                    systemBars.right + originalRight,
                    systemBars.bottom + originalBottom);
            return insets;
        });

        // Prepare slides
        List<SlideItem> slides = new ArrayList<>();
        slides.add(new SlideItem(R.drawable.ic_welcome, getString(R.string.slide_title_1),
                getString(R.string.slide_desc_1)));
        slides.add(new SlideItem(R.drawable.ic_vendor, getString(R.string.slide_title_2),
                getString(R.string.slide_desc_2)));
        slides.add(new SlideItem(R.drawable.ic_license, getString(R.string.slide_title_3),
                getString(R.string.slide_desc_3)));
        slides.add(new SlideItem(R.drawable.ic_policy, getString(R.string.slide_title_4),
                getString(R.string.slide_desc_4)));
        slides.add(new SlideItem(R.drawable.ic_security, getString(R.string.slide_title_5),
                getString(R.string.slide_desc_5)));
        slides.add(new SlideItem(R.drawable.ic_future, getString(R.string.slide_title_6),
                getString(R.string.slide_desc_6)));

        // Set adapter
        slideAdapter = new SlideAdapter(slides);
        viewPager.setAdapter(slideAdapter);

        // Auto-scroll setup
        slideRunnable = new Runnable() {
            @Override
            public void run() {
                int nextSlide = (viewPager.getCurrentItem() + 1) % slides.size();
                viewPager.setCurrentItem(nextSlide, true);
                slideHandler.postDelayed(this, slideDelay);
            }
        };

        btnGetStarted.setOnClickListener(v -> {
            Intent intent=new Intent(Landing_ScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        slideHandler.postDelayed(slideRunnable, slideDelay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(slideRunnable);
    }

}