package com.example.uvms.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uvms.R;
import com.example.uvms.api.LoginService;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.request_response.LoginRequest;
import com.example.uvms.request_response.LoginResponse;
import com.google.android.gms.common.SignInButton;
import com.google.android.material.button.MaterialButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private TextView registerTextView, forgotPasswordTextView;
    private MaterialButton loginButton;
    private SignInButton googleSignInButton;
    private EditText emailInput, passwordInput;

    private Drawable defaultIcon;
    private AnimatedVectorDrawableCompat loadingDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize views
        emailInput = findViewById(R.id.etEmail);
        passwordInput = findViewById(R.id.etPassword);
        registerTextView = findViewById(R.id.tvRegister);
        forgotPasswordTextView = findViewById(R.id.forgotPassword);
        googleSignInButton = findViewById(R.id.btnGoogleSignIn);
        loginButton = findViewById(R.id.btnLogin);

        // Load icons
        defaultIcon = ContextCompat.getDrawable(this, R.drawable.ic_login);
        loadingDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.animated_loader);

        // Apply insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Navigate to RegisterActivity
        registerTextView.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        // Forgot password
        forgotPasswordTextView.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class)));

        // Google sign-in (mock)
        googleSignInButton.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Google Sign-In Feature")
                        .setMessage("This feature is currently not available. Please use another login method.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show());

        // Login button
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable button & show loader
            setLoadingState(true);
            loginVendor(email, password);
        });
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            loginButton.setText("Loading.....");
            loginButton.setIconTint(null);
            loginButton.setEnabled(false);
            if (loadingDrawable != null) {
                loginButton.setIcon(loadingDrawable);
                loginButton.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
                loginButton.setIconPadding(8);
                loadingDrawable.start();
            }
        } else {
            loginButton.setEnabled(true);
            loginButton.setText("Login");
            loginButton.setIcon(defaultIcon);
        }
    }

    private void loginVendor(String email, String password) {
        LoginService apiService = RetrofitClient.getLoginService();
        LoginRequest request = new LoginRequest(email, password);

        apiService.loginVendor(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoadingState(false); // Restore button state
                Log.d(TAG, "Raw response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Response body: " + loginResponse);

                    Toast.makeText(LoginActivity.this,
                            loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    if (loginResponse.isSuccess()) {
                        Log.d(TAG, "Login successful. Token: " + loginResponse.getAccessToken());

                        getSharedPreferences("uvms_prefs", MODE_PRIVATE)
                                .edit()
                                .putString("auth_token", loginResponse.getAccessToken())
                                .apply();

                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        showErrorDialog(loginResponse.getMessage());
                    }
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoadingState(false); // Restore button state
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                showErrorDialog("Network error. Please check your connection.");
            }
        });
    }

    private void handleError(Response<LoginResponse> response) {
        try {
            String errorBody = response.errorBody() != null
                    ? response.errorBody().string() : "null";
            Log.e(TAG, "Error response body: " + errorBody);

            String errorMessage = "Login failed";
            try {
                JSONObject jsonObject = new JSONObject(errorBody);
                if (jsonObject.has("message")) {
                    errorMessage = jsonObject.getString("message");
                }
            } catch (Exception parseException) {
                Log.e(TAG, "Failed to parse error message", parseException);
            }

            showErrorDialog(errorMessage);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing errorBody", e);
            showErrorDialog("Login failed. Please try again.");
        }
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Login Failed")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
