package com.inovarka.myormawa.views.dashboard.member;

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
import com.inovarka.myormawa.utils.Constants;
import com.inovarka.myormawa.views.auth.LoginActivity;

import static android.content.Context.MODE_PRIVATE;

public class ProfileMemberFragment extends Fragment {

    private TextView txtAvatar, txtUserName, txtUserNim, txtUserDepartemen, txtUserJabatan;
    private CardView cardDataPribadi, cardGantiPassword, cardLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupStatusBar();
        initViews(view);
        loadUserData();
        setupListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload data setiap kali fragment resume (untuk update email setelah berhasil diubah)
        loadUserData();
    }

    private void setupStatusBar() {
        if (getActivity() != null && getActivity().getWindow() != null) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.primary_blue));

            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
            controller.setAppearanceLightStatusBars(false);
        }
    }

    private void initViews(View view) {
        txtAvatar = view.findViewById(R.id.txt_avatar);
        txtUserName = view.findViewById(R.id.txt_user_name);
        txtUserNim = view.findViewById(R.id.txt_user_nim);
        txtUserDepartemen = view.findViewById(R.id.txt_user_departemen);
        txtUserJabatan = view.findViewById(R.id.txt_user_jabatan);
        cardDataPribadi = view.findViewById(R.id.card_data_pribadi);
        cardGantiPassword = view.findViewById(R.id.card_ganti_password);
        cardLogout = view.findViewById(R.id.card_logout);
    }

    private void loadUserData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);

        String fullName = prefs.getString(Constants.KEY_FULL_NAME, "Hilmi Madani");
        String nim = prefs.getString(Constants.KEY_NIM, "E41242025");
        String departemen = prefs.getString("departemen", "Humas");
        String jabatan = prefs.getString("jabatan", "Sekretaris");

        txtUserName.setText(fullName);
        txtUserNim.setText(nim);
        txtUserDepartemen.setText(departemen);
        txtUserJabatan.setText(jabatan);
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
        cardDataPribadi.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DataPribadiMemberActivity.class);
            startActivity(intent);
        });

        cardGantiPassword.setOnClickListener(v -> {
            // Navigate to Change Password Activity
            Intent intent = new Intent(getActivity(), ChangePasswordOldMemberActivity.class);
            startActivity(intent);
        });

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
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, false);
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}