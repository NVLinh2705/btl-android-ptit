package com.btl_ptit.hotelbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.dto.Policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PolicyAdapter extends RecyclerView.Adapter<PolicyAdapter.PolicyViewHolder> {

    private static final Map<String, Integer> POLICY_ICONS = Map.ofEntries(
            Map.entry("CHECKIN", R.drawable.ic_checkin_out),
            Map.entry("CHECKOUT", R.drawable.ic_checkin_out),
            Map.entry("CANCELLATION", R.drawable.ic_cancellation),
            Map.entry("PETS", R.drawable.ic_pets),
            Map.entry("CHILDREN", R.drawable.ic_children),
            Map.entry("PAYMENT", R.drawable.ic_payment),
            Map.entry("SMOKING", R.drawable.ic_smoking),
            Map.entry("EXTRA_BED", R.drawable.ic_extra_bed)
    );

    private static final int DEFAULT_POLICY_ICON = R.drawable.ic_checkin_out;

    private final List<Policy> items = new ArrayList<>();

    public void submitList(List<Policy> policies) {
        items.clear();
        if (policies != null) {
            items.addAll(policies);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PolicyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_policy_row, parent, false);
        return new PolicyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PolicyViewHolder holder, int position) {
        Policy policy = items.get(position);

        Integer iconRes = POLICY_ICONS.get(policy.getTypeCode());
        holder.imgPolicyIcon.setImageResource(iconRes != null ? iconRes : DEFAULT_POLICY_ICON);
        ImageViewCompat.setImageTintList(
                holder.imgPolicyIcon,
                android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.bright_blue)
                )
        );
        holder.txtPolicyName.setText(policy.getTypeName());
        holder.txtPolicyContent.setText(policy.getContent());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PolicyViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgPolicyIcon;
        final TextView txtPolicyName;
        final TextView txtPolicyContent;

        PolicyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPolicyIcon = itemView.findViewById(R.id.img_policy_icon);
            txtPolicyName = itemView.findViewById(R.id.txt_policy_name);
            txtPolicyContent = itemView.findViewById(R.id.txt_policy_content);
        }
    }
}
