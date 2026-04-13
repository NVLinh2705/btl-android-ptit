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
import com.btl_ptit.hotelbooking.data.dto.HotelFacility;
import com.btl_ptit.hotelbooking.data.model.Facility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.FacilityVH> {

    private static final Map<String, Integer> FACILITY_ICONS = Map.ofEntries(
            Map.entry("Private bathroom", Integer.valueOf(R.drawable.ic_facility_bathroom)),
            Map.entry("Air conditioning", Integer.valueOf(R.drawable.ic_facility_ac)),
            Map.entry("Free Wi-Fi", Integer.valueOf(R.drawable.ic_facility_wifi)),
            Map.entry("Flat-screen TV", Integer.valueOf(R.drawable.ic_facility_tv)),
            Map.entry("Minibar", Integer.valueOf(R.drawable.ic_facility_minibar)),
            Map.entry("Room service", Integer.valueOf(R.drawable.ic_facility_room_service)),
            Map.entry("Restaurant", Integer.valueOf(R.drawable.ic_facility_restaurant)),
            Map.entry("Free private parking", Integer.valueOf(R.drawable.ic_facility_local_parking)),
            Map.entry("Airport shuttle", Integer.valueOf(R.drawable.ic_facility_airport_shuttle)),
            Map.entry("Non-smoking rooms", Integer.valueOf(R.drawable.ic_facility_no_smoking)),
            Map.entry("Wheelchair accessible", Integer.valueOf(R.drawable.ic_facility_accessible)),
            Map.entry("CCTV in common areas", Integer.valueOf(R.drawable.ic_facility_cctv)),
            Map.entry("Elevator", Integer.valueOf(R.drawable.ic_facility_elevator)),
            Map.entry("Electric kettle", Integer.valueOf(R.drawable.ic_facility_kettle)),
            Map.entry("Coffee machine", Integer.valueOf(R.drawable.ic_facility_coffee_maker)),
            Map.entry("Refrigerator", Integer.valueOf(R.drawable.ic_facility_fridge)),
            Map.entry("Microwave", Integer.valueOf(R.drawable.ic_facility_microwave)),
            Map.entry("City view", Integer.valueOf(R.drawable.ic_facility_city_view)),
            Map.entry("Bathtub", Integer.valueOf(R.drawable.ic_facility_bathtub)),
            Map.entry("Shower", Integer.valueOf(R.drawable.ic_facility_shower)),
            Map.entry("Hairdryer", Integer.valueOf(R.drawable.ic_facility_hairdryer)),
            Map.entry("Linen", Integer.valueOf(R.drawable.ic_facility_linen)),
            Map.entry("Towels", Integer.valueOf(R.drawable.ic_facility_towels)),
            Map.entry("Wardrobe", Integer.valueOf(R.drawable.ic_facility_wardrobe)),
            Map.entry("Desk", Integer.valueOf(R.drawable.ic_facility_desk)),
            Map.entry("Indoor pool", Integer.valueOf(R.drawable.ic_facility_pool)),
            Map.entry("Free toiletries", Integer.valueOf(R.drawable.ic_facility_toiletries)),
            Map.entry("Spa and wellness centre", Integer.valueOf(R.drawable.ic_facility_spa)),
            Map.entry("Daily housekeeping", Integer.valueOf(R.drawable.ic_facility_housekeeping)),
            Map.entry("24-hour front desk", Integer.valueOf(R.drawable.ic_facility_reception)),
            Map.entry("24-hour security", Integer.valueOf(R.drawable.ic_facility_security))
    );
    public static class FacilityItem {
        public final @DrawableRes int iconRes;
        public final String name;

        public FacilityItem(@DrawableRes int iconRes, String name) {
            this.iconRes = iconRes;
            this.name = name;
        }
    }

    private final List<HotelFacility> facilities = new ArrayList<>();

    private final List<FacilityItem> items = new ArrayList<>();

    public void submitList(List<HotelFacility> facilities) {
        this.facilities.clear();
        if (facilities != null) {
            this.facilities.addAll(facilities);
            for (HotelFacility f : facilities) {
                int iconRes = FACILITY_ICONS.getOrDefault(f.getName(), R.drawable.ic_facility_default);
                items.add(new FacilityItem(iconRes, f.getNameVi()));
            }
        }

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
        FacilityItem item = this.items.get(position);
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
