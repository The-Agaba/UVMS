package com.example.uvms.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.uvms.R;
import com.example.uvms.api.LoginService;
import com.example.uvms.api.VendorDashboardData;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.models.Vendor;
import com.example.uvms.request_response.LoginRequest;
import com.example.uvms.request_response.LoginResponse;
import com.google.android.gms.common.SignInButton;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.System.currentTimeMillis;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String PREFS_NAME = "uvms_prefs";

    private TextView registerTextView, forgotPasswordTextView;
    private MaterialButton loginButton;
    private SignInButton googleSignInButton;
    private EditText emailInput, passwordInput;
    private Drawable defaultIcon;
    private AnimatedVectorDrawableCompat loadingDrawable;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Redirect if token exists and not expired
        String existingToken = prefs.getString("auth_token", null);
        if (existingToken != null && !existingToken.isEmpty()
                && currentTimeMillis() < prefs.getLong("expires_in", 0)) {
            navigateToHome();
            return;
        } else {
            prefs.edit().clear().apply(); // clear invalid token
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        emailInput = findViewById(R.id.etEmail);
        passwordInput = findViewById(R.id.etPassword);
        registerTextView = findViewById(R.id.tvRegister);
        forgotPasswordTextView = findViewById(R.id.forgotPassword);
        googleSignInButton = findViewById(R.id.btnGoogleSignIn);
        loginButton = findViewById(R.id.btnLogin);

        defaultIcon = ContextCompat.getDrawable(this, R.drawable.ic_login);
        loadingDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.animated_loader);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupListeners() {
        registerTextView.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        forgotPasswordTextView.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class)));

        googleSignInButton.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Google Sign-In Feature")
                        .setMessage("This feature is currently not available. Please use another login method.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show());

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            setLoadingState(true);
            loginVendor(email, password);
        });
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            loginButton.setText("Loading...");
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
                setLoadingState(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    if (loginResponse.isSuccess()) {
                        // Save token
                        storeLoginResponse(loginResponse);
                        // Fetch full vendor data
                        fetchFullVendorData(loginResponse.getAccessToken());
                    } else {
                        showErrorDialog(loginResponse.getMessage());
                    }
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoadingState(false);
                showErrorDialog("Network error: " + t.getMessage());
                Log.e(TAG, "Network error", t);
            }
        });
    }

    private void fetchFullVendorData(String token) {
        VendorDashboardData api = RetrofitClient.getInstance(this).create(VendorDashboardData.class);
        String authHeader = "Bearer " + token;

        api.getData(authHeader).enqueue(new Callback<Vendor>() {
            @Override
            public void onResponse(Call<Vendor> call, Response<Vendor> response) {
                setLoadingState(false);
                if (response.isSuccessful() && response.body() != null) {
                    storeVendorData(response.body());
                    navigateToHome();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to fetch full profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Vendor> call, Throwable t) {
                setLoadingState(false);
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeLoginResponse(LoginResponse loginResponse) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("auth_token", loginResponse.getAccessToken());
        editor.putString("token_type", loginResponse.getTokenType());
        editor.putLong("expires_in", loginResponse.getExpiresIn());
        editor.putBoolean("login_success", loginResponse.isSuccess());
        editor.putString("login_message", loginResponse.getMessage());
        editor.apply();
    }

    private void storeVendorData(Vendor vendor) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_id", String.valueOf(vendor.getVendorId()));
        editor.putString("user_email", vendor.getEmail());
        editor.putString("user_first_name", vendor.getFirstName());
        editor.putString("user_last_name", vendor.getLastName());
        editor.putString("user_full_name", vendor.getFirstName() + " " + vendor.getLastName());
        editor.putString("user_company_name", vendor.getCompanyName());
        editor.putString("user_tin_number", vendor.getTinNumber());
        editor.putString("user_business_address", vendor.getBusinessAddress());
        editor.putString("user_profile_picture", vendor.getProfilePicturePath());
        editor.putString("user_welcome_message", "Welcome, " + vendor.getFirstName() + "!");
        editor.putString("user_last_login", vendor.getLastLogin());
        editor.putString("user_created_at", vendor.getRegistrationDate()); // use registrationDate as createdAt
        editor.putBoolean("user_is_active", vendor.isActive());
        editor.putString("user_phone_number", vendor.getPhoneNumber());
        editor.putString("user_role", vendor.getRole());
        editor.putString("user_updated_at", vendor.getUpdatedAt());
        editor.putString("user_deleted_at", vendor.getDeletedAt());
        editor.putString("business_type", vendor.getBusinessType());
        editor.apply();
    }

    private void navigateToHome() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    private void handleError(Response<LoginResponse> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
            String errorMessage = "Login failed";
            try {
                JSONObject jsonObject = new JSONObject(errorBody);
                if (jsonObject.has("message")) errorMessage = jsonObject.getString("message");
            } catch (Exception e) {
                Log.e(TAG, "Failed to parse error message", e);
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
