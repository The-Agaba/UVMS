package com.example.uvms.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.uvms.R;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.api.RegisterService;
import com.example.uvms.request_response.RegisterRequest;
import com.example.uvms.request_response.RegisterResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout firstNameLayout, lastNameLayout, emailLayout, passwordLayout,
            phoneLayout, companyLayout, tinLayout, addressLayout;
    private TextInputEditText etFirstName, etLastName, etEmail, etPassword,
            etPhone, etCompanyName, etTin, etAddress;
    private Spinner spinnerBusinessType;
    private MaterialButton btnRegister;
    private ImageButton btnBack;
    private TextView loginLink;

    private RegisterService registerService;


    private Drawable defaultIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        firstNameLayout = findViewById(R.id.firstNameInputLayout);
        lastNameLayout = findViewById(R.id.lastNameInputLayout);
        emailLayout = findViewById(R.id.emailInputLayout);
        passwordLayout = findViewById(R.id.passwordInputLayout);
        phoneLayout = findViewById(R.id.phoneInputLayout);
        companyLayout = findViewById(R.id.companyNameInputLayout);
        tinLayout = findViewById(R.id.tinInputLayout);
        addressLayout = findViewById(R.id.addressInputLayout);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etCompanyName = findViewById(R.id.etCompanyName);
        etTin = findViewById(R.id.etTin);
        etAddress = findViewById(R.id.etAddress);

        spinnerBusinessType = findViewById(R.id.spinnerBusinessType);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);
        loginLink = findViewById(R.id.tvLoginLink);

        // Save default icon AFTER initialization
        defaultIcon = btnRegister.getIcon();

        passwordLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

        // Spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.business_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBusinessType.setAdapter(adapter);


        registerService = RetrofitClient.getInstance(this).create(RegisterService.class);

        // Button clicks
        btnRegister.setOnClickListener(v -> {
            btnRegister.setEnabled(false);

            // Load animated vector
            AnimatedVectorDrawableCompat loadingDrawable =
                    AnimatedVectorDrawableCompat.create(this, R.drawable.animated_loader);

            btnRegister.setIconTint(null);
            btnRegister.setIcon(loadingDrawable);
            btnRegister.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
            btnRegister.setIconPadding(8);
            loadingDrawable.start();
            btnRegister.setText("Loading...");

            if (loadingDrawable != null) loadingDrawable.start();
            submitRegistration();
        });

        btnBack.setOnClickListener(v -> finish());
        loginLink.setOnClickListener(v -> navigateToLogin());
    }

    private void submitRegistration() {
        String firstName = Objects.requireNonNull(etFirstName.getText()).toString().trim();
        String lastName = Objects.requireNonNull(etLastName.getText()).toString();
        String email = Objects.requireNonNull(etEmail.getText()).toString();
        String password = Objects.requireNonNull(etPassword.getText()).toString();
        String phone = Objects.requireNonNull(etPhone.getText()).toString();
        String companyName = Objects.requireNonNull(etCompanyName.getText()).toString();
        String tin = Objects.requireNonNull(etTin.getText()).toString();
        String address = Objects.requireNonNull(etAddress.getText()).toString();
        String businessType = spinnerBusinessType.getSelectedItem().toString();

        if (!validateForm(firstName, lastName, email, password, phone, companyName, tin, address, businessType)) {
            btnRegister.setEnabled(true);
            return;
        }

        RegisterRequest request = new RegisterRequest(firstName, lastName, email, password, phone, companyName, tin, address, businessType);

        registerService.registerVendor(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {


                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    Toast.makeText(RegisterActivity.this,
                            registerResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    if (registerResponse.isSuccess()) {
                        navigateToLogin();
                    } else {
                        btnRegister.setIcon(defaultIcon);
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Retry Register ");
                        showErrorDialog(registerResponse.getMessage());
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        RegisterResponse errorResponse = new Gson().fromJson(errorBody, RegisterResponse.class);
                        showErrorDialog(errorResponse != null && errorResponse.getMessage() != null ?
                                errorResponse.getMessage() : "Registration failed. Please try again.");
                    } catch (Exception e) {
                        Log.e("RegisterActivity", "Error parsing errorBody", e);
                        showErrorDialog("Registration failed. Please try again.");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponse> call, Throwable t) {
                btnRegister.setIcon(defaultIcon);
                btnRegister.setEnabled(true);
                btnRegister.setText("Register");
                showErrorDialog("An error occurred: " + t.getMessage());
            }
        });
    }

    private boolean validateForm(String firstName, String lastName, String email, String password,
                                 String phone, String companyName, String tin, String address, String businessType) {

        boolean isValid = true;
        firstNameLayout.setError(null);
        lastNameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
        phoneLayout.setError(null);
        companyLayout.setError(null);
        tinLayout.setError(null);
        addressLayout.setError(null);

        if (firstName.isEmpty()) { firstNameLayout.setError("First name is required"); isValid = false; }
        if (lastName.isEmpty()) { lastNameLayout.setError("Last name is required"); isValid = false; }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { emailLayout.setError("Valid Email is required"); isValid = false; }
        if (password.isEmpty()) { passwordLayout.setError("Password is required"); isValid = false; }
        else if (password.length() < 8) { passwordLayout.setError("Password must be at least 8 characters"); isValid = false; }
        else if (!password.matches(".*[a-zA-Z].*")) { passwordLayout.setError("Password must contain at least one letter"); isValid = false; }
        else if (!password.matches(".*\\d.*")) { passwordLayout.setError("Password must contain at least one number"); isValid = false; }

        if (phone.isEmpty()) { phoneLayout.setError("Phone number is required"); isValid = false; }
        if (companyName.isEmpty()) { companyLayout.setError("Company name is required"); isValid = false; }
        if (tin.isEmpty()) { tinLayout.setError("TIN is required"); isValid = false; }
        if (address.isEmpty()) { addressLayout.setError("Address is required"); isValid = false; }

        if (businessType.equals("Select type")) {
            Toast.makeText(this, "Select business type", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void navigateToLogin() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(RegisterActivity.this)
                .setTitle("Registration Failed")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
