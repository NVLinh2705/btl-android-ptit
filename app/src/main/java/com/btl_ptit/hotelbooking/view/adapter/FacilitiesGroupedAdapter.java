package com.btl_ptit.hotelbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.dto.HotelFacility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FacilitiesGroupedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final List<RowItem> rows = new ArrayList<>();
    private final List<HotelFacility> source = new ArrayList<>();

    public void submitList(List<HotelFacility> facilities) {
        source.clear();
        if (facilities != null) {
            source.addAll(facilities);
        }
        filter("");
    }

    public void filter(String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        rows.clear();

        Map<String, List<HotelFacility>> grouped = new LinkedHashMap<>();
        Map<String, Integer> groupedTypeIds = new LinkedHashMap<>();
        for (HotelFacility item : source) {
            String facilityName = item.getNameVi() != null ? item.getNameVi() : item.getName();
            if (!q.isEmpty() && (facilityName == null || !facilityName.toLowerCase(Locale.ROOT).contains(q))) {
                continue;
            }
            String group = item.getTypeNameVi() != null ? item.getTypeNameVi() : item.getTypeName();
            if (group == null || group.trim().isEmpty()) {
                group = "Khác";
            }
            grouped.computeIfAbsent(group, key -> new ArrayList<>()).add(item);
            if (!groupedTypeIds.containsKey(group)) {
                groupedTypeIds.put(group, item.getTypeId());
            }
        }

        for (Map.Entry<String, List<HotelFacility>> entry : grouped.entrySet()) {
            rows.add(RowItem.header(entry.getKey(), groupedTypeIds.get(entry.getKey())));
            for (HotelFacility facility : entry.getValue()) {
                rows.add(RowItem.item(facility));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).isHeader ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_facility_group_header, parent, false);
            return new HeaderVH(view);
        }
        View view = inflater.inflate(R.layout.item_facility_group_item, parent, false);
        return new ItemVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RowItem row = rows.get(position);
        if (holder instanceof HeaderVH) {
            ((HeaderVH) holder).txtHeader.setText(row.headerTitle);
            ((HeaderVH) holder).imgHeaderIcon.setImageResource(resolveTypeIcon(row.typeId));
        } else if (holder instanceof ItemVH) {
            String name = row.facility.getNameVi() != null ? row.facility.getNameVi() : row.facility.getName();
            ((ItemVH) holder).txtFacilityName.setText(name);
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    private static class HeaderVH extends RecyclerView.ViewHolder {
        final ImageView imgHeaderIcon;
        final TextView txtHeader;

        HeaderVH(@NonNull View itemView) {
            super(itemView);
            imgHeaderIcon = itemView.findViewById(R.id.imgFacilityGroupIcon);
            txtHeader = itemView.findViewById(R.id.txtFacilityGroupHeader);
        }
    }

    private static class ItemVH extends RecyclerView.ViewHolder {
        final TextView txtFacilityName;

        ItemVH(@NonNull View itemView) {
            super(itemView);
            txtFacilityName = itemView.findViewById(R.id.txtFacilityItemName);
        }
    }

    private static class RowItem {
        final boolean isHeader;
        final String headerTitle;
        final Integer typeId;
        final HotelFacility facility;

        private RowItem(boolean isHeader, String headerTitle, Integer typeId, HotelFacility facility) {
            this.isHeader = isHeader;
            this.headerTitle = headerTitle;
            this.typeId = typeId;
            this.facility = facility;
        }

        static RowItem header(String title, Integer typeId) {
            return new RowItem(true, title, typeId, null);
        }

        static RowItem item(HotelFacility facility) {
            return new RowItem(false, null, null, facility);
        }
    }

    private int resolveTypeIcon(Integer typeId) {
        if (typeId == null) return R.drawable.ic_facility_default;
        switch (typeId) {
            case 1:
                return R.drawable.ic_facility_bathroom;
            case 2:
                return R.drawable.ic_bed;
            case 3:
                return R.drawable.ic_facility_city_view;
            case 4:
                return R.drawable.ic_facility_fridge;
            case 5:
                return R.drawable.ic_facility_tv;
            case 6:
                return R.drawable.ic_facility_restaurant;
            case 7:
                return R.drawable.ic_facility_pool;
            case 8:
                return R.drawable.ic_facility_spa;
            case 9:
                return R.drawable.ic_facility_default;
            case 10:
                return R.drawable.ic_facility_reception;
            case 11:
                return R.drawable.ic_facility_housekeeping;
            case 12:
                return R.drawable.ic_facility_security;
            case 13:
                return R.drawable.ic_facility_default;
            case 14:
                return R.drawable.ic_facility_accessible;
            case 15:
                return R.drawable.ic_facility_local_parking;
            default:
                return R.drawable.ic_facility_default;
        }
    }
}

