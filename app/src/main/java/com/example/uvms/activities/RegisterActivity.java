package com.example.uvms.activities;

import android.app.AlertDialog;
import android.content.Intent;
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

import com.example.uvms.R;
import com.example.uvms.clients.RetrofitClient;
import com.example.uvms.api.RegisterService;
import com.example.uvms.models.Vendor;
import com.example.uvms.request_response.RegisterRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
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

        passwordLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

        // Spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.business_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBusinessType.setAdapter(adapter);

        // Retrofit service
        registerService = RetrofitClient.getInstance().create(RegisterService.class);

        // Button clicks
        btnRegister.setOnClickListener(v -> submitRegistration());
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
            return;
        }

        RegisterRequest request = new RegisterRequest(firstName, lastName, email, password, phone, companyName, tin, address, businessType);

        registerService.registerVendor(request).enqueue(new Callback<Vendor>() {


            @Override
            public void onResponse(Call<Vendor> call, Response<Vendor> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                } else {
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("Registration Failed")
                            .setMessage("Please try again.")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Vendor> call, Throwable t) {
                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle(" ⚠️⚠️Error⚠️⚠️")
                        .setMessage("An error occurred. Please try again.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .setNegativeButton("Retry", (dialog, which) -> submitRegistration())
                        .show();

                t.printStackTrace();
                Log.e("RegistrationActivity", "Error: " + t);

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
}
