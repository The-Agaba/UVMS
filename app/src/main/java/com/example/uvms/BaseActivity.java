package com.example.uvms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uvms.activities.LoginActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected SharedPreferences prefs;
    private static final String PREFS_NAME = "uvms_prefs";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        validateLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        validateLogin(); // Check again when user returns to the activity
    }

    private void validateLogin() {
        String token = prefs.getString("auth_token", null);
        if (token == null || token.isEmpty()) {
            // Redirect to LoginActivity and clear activity history
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
