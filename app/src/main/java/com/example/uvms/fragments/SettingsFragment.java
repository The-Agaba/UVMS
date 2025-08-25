package com.example.uvms.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.uvms.R;
import com.example.uvms.activities.LoginActivity;

public class SettingsFragment extends Fragment {

    private SwitchCompat switchDarkMode;
    private Spinner spinnerLanguage;
    private Button btnLogout, btnDeleteAccount;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize views
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);

        // SharedPreferences setup
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Load saved dark mode
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        switchDarkMode.setChecked(isDarkMode);

        // Load saved language
        String selectedLanguage = sharedPreferences.getString("language", "English");

        // --- Dark Mode Listener ---
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("isDarkMode", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // --- Language Spinner setup ---
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.languages,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        int position = adapter.getPosition(selectedLanguage);
        spinnerLanguage.setSelection(position);

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String lang = parent.getItemAtPosition(pos).toString();
                editor.putString("language", lang);
                editor.apply();
                // Optional: update app locale
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // --- Logout Button
        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            if (getActivity() != null) getActivity().finish();
        });

        // --- Delete Account Button ---
        btnDeleteAccount.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        if (getActivity() != null) getActivity().finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return view;
    }
}
