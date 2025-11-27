package com.inovarka.myormawa.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.Organization;
import java.util.List;

public class OrganizationAdapter extends RecyclerView.Adapter<OrganizationAdapter.OrganizationViewHolder> {

    private Context context;
    private List<Organization> organizations;
    private OnOrganizationClickListener listener;

    public interface OnOrganizationClickListener {
        void onOrganizationClick(Organization organization);
    }

    public OrganizationAdapter(Context context, List<Organization> organizations, OnOrganizationClickListener listener) {
        this.context = context;
        this.organizations = organizations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrganizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_organization, parent, false);
        return new OrganizationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizationViewHolder holder, int position) {
        holder.bind(organizations.get(position));
    }

    @Override
    public int getItemCount() {
        return organizations.size();
    }

    public void updateOrganizations(List<Organization> newOrganizations) {
        this.organizations = newOrganizations;
        notifyDataSetChanged();
    }

    class OrganizationViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgLogo;
        private final TextView txtName, txtCategory, txtDescription;

        public OrganizationViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLogo = itemView.findViewById(R.id.img_organization_logo);
            txtName = itemView.findViewById(R.id.txt_organization_name);
            txtCategory = itemView.findViewById(R.id.txt_organization_category);
            txtDescription = itemView.findViewById(R.id.txt_organization_description);
            itemView.findViewById(R.id.txt_member_count).setVisibility(View.GONE);
        }

        public void bind(Organization org) {
            txtName.setText(org.getName());
            txtCategory.setText(org.getCategory());
            txtDescription.setText(org.getDescription());

            if (org.getLogoUrl() != null && !org.getLogoUrl().isEmpty()) {
                Glide.with(context).load(org.getLogoUrl())
                        .placeholder(R.drawable.ic_graduation_filled)
                        .error(R.drawable.ic_graduation_filled).into(imgLogo);
            } else {
                imgLogo.setImageResource(R.drawable.ic_graduation_filled);
            }

            setCategoryStyle(org.getCategory());
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onOrganizationClick(org);
            });
        }

        private void setCategoryStyle(String category) {
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(6 * itemView.getResources().getDisplayMetrics().density);

            String bgColor = "#E8EEFF", textColor = "#2C4EEF";
            switch (category) {
                case "Lembaga": bgColor = "#E5E5E5"; textColor = "#455A64"; break;
                case "Rohani": bgColor = "#A9FEAF"; textColor = "#388E3C"; break;
                case "Minat": bgColor = "#FFECCE"; textColor = "#E65100"; break;
                case "Seni": bgColor = "#FBDFFF"; textColor = "#7B1FA2"; break;
                case "Olahraga": bgColor = "#FAD1D7"; textColor = "#C62828"; break;
            }

            bg.setColor(Color.parseColor(bgColor));
            txtCategory.setBackground(bg);
            txtCategory.setTextColor(Color.parseColor(textColor));
        }
    }
}