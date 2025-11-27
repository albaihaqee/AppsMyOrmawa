package com.inovarka.myormawa.views.dashboard.student;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.views.auth.LoginActivity;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private static final String PREFS_NAME = "MyOrmawaPrefs";

    private TextView txtAvatar, txtUserName, txtUserNim, txtUserProdi, txtUserAngkatan;
    private CardView cardDataPribadi, cardGantiPassword, cardLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        setupStatusBar();
        initViews(view);
        loadUserData();
        setupListeners();

        return view;
    }

    private void setupStatusBar() {
        if (getActivity() != null && getActivity().getWindow() != null) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.primary_blue));

            WindowInsetsControllerCompat windowInsetsController =
                    new WindowInsetsControllerCompat(window, window.getDecorView());
            windowInsetsController.setAppearanceLightStatusBars(false);
        }
    }

    private void initViews(View view) {
        txtAvatar = view.findViewById(R.id.txt_avatar);
        txtUserName = view.findViewById(R.id.txt_user_name);
        txtUserNim = view.findViewById(R.id.txt_user_nim);
        txtUserProdi = view.findViewById(R.id.txt_user_prodi);
        txtUserAngkatan = view.findViewById(R.id.txt_user_angkatan);
        cardDataPribadi = view.findViewById(R.id.card_data_pribadi);
        cardGantiPassword = view.findViewById(R.id.card_ganti_password);
        cardLogout = view.findViewById(R.id.card_logout);
    }

    private void loadUserData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String fullName = prefs.getString("full_name", "Hilmi Madani");
        String nim = prefs.getString("nim", "E41242025");
        String prodi = prefs.getString("program_studi", "Teknik Informatika");
        String angkatan = prefs.getString("angkatan", "2024");

        txtUserName.setText(fullName);
        txtUserNim.setText(nim);
        txtUserProdi.setText(prodi);
        txtUserAngkatan.setText(angkatan);
        txtAvatar.setText(getInitials(fullName));
    }

    private String getInitials(String name) {
        String[] parts = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) initials.append(part.charAt(0));
        }
        return initials.toString().toUpperCase();
    }

    private void setupListeners() {
        cardDataPribadi.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DataPribadiActivity.class)));

        cardGantiPassword.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ChangePasswordOldActivity.class)));

        cardLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }

        MaterialButton btnNo = dialog.findViewById(R.id.btn_no);
        MaterialButton btnYes = dialog.findViewById(R.id.btn_yes);

        btnNo.setOnClickListener(v -> dialog.dismiss());
        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            performLogout();
        });

        dialog.show();
    }

    private void performLogout() {
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("is_logged_in", false);
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}