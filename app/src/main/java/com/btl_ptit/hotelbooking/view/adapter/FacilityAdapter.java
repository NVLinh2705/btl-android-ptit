package com.btl_ptit.hotelbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.R;

import java.util.ArrayList;
import java.util.List;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.FacilityVH> {

    public static class FacilityItem {
        public final @DrawableRes int iconRes;
        public final String name;

        public FacilityItem(@DrawableRes int iconRes, String name) {
            this.iconRes = iconRes;
            this.name = name;
        }
    }

    private final List<FacilityItem> items = new ArrayList<>();

    public void submitList(List<FacilityItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FacilityVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_facility_chip, parent, false);
        return new FacilityVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityVH holder, int position) {
        FacilityItem item = items.get(position);
        holder.txtName.setText(item.name);
        holder.imgIcon.setImageResource(item.iconRes);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FacilityVH extends RecyclerView.ViewHolder {
        final ImageView imgIcon;
        final TextView txtName;

        FacilityVH(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtName = itemView.findViewById(R.id.txtName);
        }
    }
}

