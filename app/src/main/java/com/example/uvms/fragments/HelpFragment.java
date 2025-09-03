package com.example.uvms.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.uvms.R;
import com.example.uvms.activities.ContactActivity;
import com.example.uvms.activities.EditProfileActivity;
import com.example.uvms.activities.PasswordResetActivity;
import com.example.uvms.activities.ProfileActivity;

public class HelpFragment extends Fragment {

    public HelpFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        // Initialize all cards
        initCards(view);

        return view;
    }

    //Initialize cards with icons, text, and actions
    private void initCards(View root) {
        setupCard(root, R.id.cardAccount, R.drawable.ic_acc_settings, R.string.menu_account_settings, this::openProfileActivity);
        setupCard(root, R.id.cardLoginPassword, R.drawable.ic_password_reset, R.string.menu_login_password, this::openEditProfileActivity);
        setupCard(root, R.id.cardPrivacy, R.drawable.ic_privacy, R.string.menu_privacy_security, this::showPrivacyDialog);
        setupCard(root, R.id.cardMarketplace, R.drawable.ic_marketplace, R.string.menu_marketplace, this::showComingSoonDialog);
        setupCard(root, R.id.cardContact, R.drawable.calling_icon, R.string.menu_contact_us, this::openContactActivity);
        setupCard(root, R.id.cardReport, R.drawable.ic_problem, R.string.menu_report_issue, this::showComingSoonDialog);
    }

    //Generic card setup method
    private void setupCard(View root, int cardId, int iconRes, int textRes, Runnable action) {
        View card = root.findViewById(cardId);
        ImageView icon = card.findViewById(R.id.cardIcon);
        TextView text = card.findViewById(R.id.cardText);

        icon.setImageResource(iconRes);
        text.setText(textRes);

        card.setOnClickListener(v -> action.run());
    }

    private void openProfileActivity() {
        startActivity(new Intent(getContext(), ProfileActivity.class));
    }

    private void openEditProfileActivity() {
        startActivity(new Intent(getContext(), PasswordResetActivity.class));
    }

    private void openContactActivity() {
        startActivity(new Intent(getContext(), ContactActivity.class));
    }


    private void showPrivacyDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Privacy & Security")
                .setMessage(
                        "Your privacy is important. This app collects minimal personal data " +
                                "necessary for account management and service functionality. " +
                                "Data is stored securely and will not be shared without your consent, " +
                                "in accordance with Tanzanian Data Protection regulations (The Data Protection Act, 2019). " +
                                "You have the right to access and delete your data anytime."
                )
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }

    //Show Coming Soon Dialog
    private void showComingSoonDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Coming Soon")
                .setMessage("This feature is coming soon!")
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
