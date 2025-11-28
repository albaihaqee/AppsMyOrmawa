package com.inovarka.myormawa.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.Member;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<Member> memberList;

    public MemberAdapter(List<Member> memberList) {
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = memberList.get(position);

        holder.tvInitial.setText(member.getInitial().toUpperCase());
        holder.tvName.setText(member.getName());
        holder.tvDepartment.setText(member.getDepartment());
        holder.tvPosition.setText(member.getPosition());
        holder.tvProdi.setText(member.getProdi());
        holder.tvPhone.setText(member.getPhone());

    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitial, tvName, tvDepartment, tvPosition, tvProdi, tvPhone;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitial = itemView.findViewById(R.id.tv_initial);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDepartment = itemView.findViewById(R.id.tv_department);
            tvPosition = itemView.findViewById(R.id.tv_position);
            tvProdi = itemView.findViewById(R.id.tv_prodi);
            tvPhone = itemView.findViewById(R.id.tv_phone);
        }
    }
}
