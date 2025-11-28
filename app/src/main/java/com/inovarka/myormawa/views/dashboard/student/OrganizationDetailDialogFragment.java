package com.inovarka.myormawa.views.dashboard.student;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.Organization;

public class OrganizationDetailDialogFragment extends DialogFragment {

    private static final String ARG_ORGANIZATION = "organization";
    private Organization organization;

    public static OrganizationDetailDialogFragment newInstance(Organization organization) {
        OrganizationDetailDialogFragment fragment = new OrganizationDetailDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ORGANIZATION, organization);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            organization = (Organization) getArguments().getSerializable(ARG_ORGANIZATION);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_organization_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnClose = view.findViewById(R.id.btn_close_dialog);
        ImageView imgLogo = view.findViewById(R.id.img_organization_logo_detail);
        TextView txtName = view.findViewById(R.id.txt_organization_name_detail);
        Chip chipCategory = view.findViewById(R.id.chip_category_detail);
        TextView txtDescription = view.findViewById(R.id.txt_description_detail);
        TextView txtVision = view.findViewById(R.id.txt_vision_detail);
        TextView txtMission = view.findViewById(R.id.txt_mission_detail);
        TextView txtEmail = view.findViewById(R.id.txt_email_detail);
        TextView txtContactPerson = view.findViewById(R.id.txt_contact_person_detail);
        Button btnCloseBottom = view.findViewById(R.id.btn_close_detail);

        if (organization != null) {
            txtName.setText(organization.getName());
            txtDescription.setText(organization.getDescription());
            txtVision.setText(organization.getVision() != null && !organization.getVision().isEmpty()
                    ? organization.getVision() : "Belum ada visi");
            txtMission.setText(organization.getMission() != null && !organization.getMission().isEmpty()
                    ? organization.getMission() : "Belum ada misi");
            txtEmail.setText(organization.getEmail() != null && !organization.getEmail().isEmpty()
                    ? organization.getEmail() : "-");
            txtContactPerson.setText(organization.getContactPerson() != null && !organization.getContactPerson().isEmpty()
                    ? organization.getContactPerson() : "-");

            // Set category chip with dynamic styling
            setCategoryStyle(chipCategory, organization.getCategory());

            // Load logo
            if (organization.getLogoUrl() != null && !organization.getLogoUrl().isEmpty()) {
                Glide.with(requireContext())
                        .load(organization.getLogoUrl())
                        .placeholder(R.drawable.ic_graduation_filled)
                        .error(R.drawable.ic_graduation_filled)
                        .into(imgLogo);
            } else {
                imgLogo.setImageResource(R.drawable.ic_graduation_filled);
            }
        }

        // Close buttons
        btnClose.setOnClickListener(v -> dismiss());
        btnCloseBottom.setOnClickListener(v -> dismiss());
    }

    private void setCategoryStyle(Chip chip, String category) {
        String bgColor = "#E8EEFF", textColor = "#2C4EEF";

        switch (category) {
            case "Lembaga":
                bgColor = "#E5E5E5";
                textColor = "#455A64";
                break;
            case "Akademik":
                bgColor = "#E8EEFF";
                textColor = "#2C4EEF";
                break;
            case "Rohani":
                bgColor = "#A9FEAF";
                textColor = "#388E3C";
                break;
            case "Minat":
                bgColor = "#FFECCE";
                textColor = "#E65100";
                break;
            case "Seni":
                bgColor = "#FBDFFF";
                textColor = "#7B1FA2";
                break;
            case "Olahraga":
                bgColor = "#FAD1D7";
                textColor = "#C62828";
                break;
        }

        chip.setText(category);
        chip.setChipBackgroundColorResource(android.R.color.transparent);
        chip.setTextColor(Color.parseColor(textColor));

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(6 * getResources().getDisplayMetrics().density);
        bg.setColor(Color.parseColor(bgColor));
        chip.setBackground(bg);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}