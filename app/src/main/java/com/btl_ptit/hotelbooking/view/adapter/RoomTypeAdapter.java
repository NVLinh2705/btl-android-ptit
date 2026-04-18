package com.btl_ptit.hotelbooking.view.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayout;
import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.model.Facility;
import com.btl_ptit.hotelbooking.data.model.RoomType;
import com.btl_ptit.hotelbooking.data.session.RoomSelectionStore;
import com.btl_ptit.hotelbooking.utils.CurrencyUtils;

import java.util.List;

public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.RoomTypeViewHolder> {

    public interface OnRoomActionListener {
        void onRoomClicked(RoomType roomType);
        void onSelectRoom(RoomType roomType);
        void onRemoveRoom(RoomType roomType);
    }

    private final List<RoomType> roomTypes;
    private final Context context;
    private final OnRoomActionListener listener;

    public RoomTypeAdapter(Context context, List<RoomType> roomTypes, OnRoomActionListener listener) {
        this.context = context;
        this.roomTypes = roomTypes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_type, parent, false);
        return new RoomTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomTypeViewHolder holder, int position) {
        holder.bind(roomTypes.get(position));
    }

    @Override
    public int getItemCount() { return roomTypes.size(); }

    // ── ViewHolder ────────────────────────────────────────────────────────

    class RoomTypeViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvBedInfo, tvArea, tvGuests,
                 tvCancellation, tvPrepayment, tvPrice,
                 tvOriginalPrice, tvLastRoom;
        ImageView ivThumbnail;
        ImageView btnRemoveSelection;
        FlexboxLayout flexFacilities;
        View layoutArea, layoutLastRoom;
        com.google.android.material.button.MaterialButton btnSelect;

        RoomTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName      = itemView.findViewById(R.id.tvRoomName);
            tvBedInfo       = itemView.findViewById(R.id.tvBedInfo);
            tvArea          = itemView.findViewById(R.id.tvArea);
            layoutArea      = itemView.findViewById(R.id.layoutArea);
            tvGuests        = itemView.findViewById(R.id.tvGuests);
            tvCancellation  = itemView.findViewById(R.id.tvCancellation);
            tvPrepayment    = itemView.findViewById(R.id.tvPrepayment);
            tvPrice         = itemView.findViewById(R.id.tvPrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvLastRoom      = itemView.findViewById(R.id.tvLastRoom);
            layoutLastRoom  = itemView.findViewById(R.id.layoutLastRoom);
            ivThumbnail     = itemView.findViewById(R.id.ivThumbnail);
            btnRemoveSelection = itemView.findViewById(R.id.btnRemoveSelection);
            flexFacilities  = itemView.findViewById(R.id.flexFacilities);
            btnSelect       = itemView.findViewById(R.id.btnSelect);
        }

        void bind(RoomType room) {
            // ── Name ─────────────────────────────────────────────────
            tvRoomName.setText(room.getName());

            // ── Bed info ─────────────────────────────────────────────
            tvBedInfo.setText(room.getBedSummary());

            // ── Area ─────────────────────────────────────────────────
            if (room.getArea() != null) {
                tvArea.setText(room.getAreaLabel());
                layoutArea.setVisibility(View.VISIBLE);
            } else {
                layoutArea.setVisibility(View.GONE);
            }

            // ── Thumbnail ─────────────────────────────────────────────
            List<String> images = room.getImageUrls();
            if (images != null && !images.isEmpty()) {
                Glide.with(context)
                        .load(images.get(0))
                        .apply(new RequestOptions()
                                .transform(new RoundedCorners(12))
                                .placeholder(R.drawable.placeholder_room)
                                .error(R.drawable.placeholder_room))
                        .into(ivThumbnail);
            } else {
                ivThumbnail.setImageResource(R.drawable.placeholder_room);
            }

            // ── Facilities ────────────────────────────────────────────
            populateFacilities(flexFacilities, room.getFacilities(), 6 /* max shown in list */);

            // ── Guests ───────────────────────────────────────────────
            tvGuests.setText(context.getString(R.string.price_for_n_adults, room.getMaxGuests()));

            // ── Cancellation label ────────────────────────────────────
            if (!TextUtils.isEmpty(room.getCancellationPolicy())) {
                tvCancellation.setText(room.getCancellationPolicy());
                tvCancellation.setTextColor(context.getColor(R.color.green_700));
            } else if (room.isHasFreeCancellation()) {
                tvCancellation.setText(R.string.free_cancellation);
                tvCancellation.setTextColor(context.getColor(R.color.green_700));
            } else {
                tvCancellation.setText(R.string.total_cost_to_cancel);
                tvCancellation.setTextColor(context.getColor(R.color.green_700));
            }

            // ── Prepayment ────────────────────────────────────────────
            if (!TextUtils.isEmpty(room.getPaymentPolicy())) {
                tvPrepayment.setText(room.getPaymentPolicy());
            } else {
                String noPrep = context.getString(R.string.no_prepayment_needed);
                String rest   = context.getString(R.string.pay_at_property);
                SpannableString ss = new SpannableString(noPrep + rest);
                ss.setSpan(new StyleSpan(Typeface.BOLD), 0, noPrep.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvPrepayment.setText(ss);
            }
            tvPrepayment.setTextColor(context.getColor(R.color.green_700));

            // ── Price ─────────────────────────────────────────────────
            String formattedPrice = CurrencyUtils.formatVnd(room.getBasePricePerNight());
            tvPrice.setText(formattedPrice);
            tvOriginalPrice.setVisibility(View.GONE); // set visible + text if discounted

            // ── Last room warning ─────────────────────────────────────
            if (room.isLastRoom()) {
                layoutLastRoom.setVisibility(View.VISIBLE);
                tvLastRoom.setText(R.string.we_have_1_left);
            } else if (room.getQuantity() > 0 && room.getQuantity() <= 3) {
                layoutLastRoom.setVisibility(View.VISIBLE);
                tvLastRoom.setText(context.getString(R.string.we_have_n_left, room.getQuantity()));
            } else {
                layoutLastRoom.setVisibility(View.GONE);
            }

            // ── Select click ──────────────────────────────────────────
            btnSelect.setOnClickListener(v -> {
                if (listener != null) listener.onSelectRoom(room);
            });
            btnRemoveSelection.setOnClickListener(v -> {
                if (listener != null) listener.onRemoveRoom(room);
            });
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onRoomClicked(room);
            });

            bindSelectionState(room);
        }

        private void bindSelectionState(RoomType room) {
            int selectedCount = RoomSelectionStore.getRoomCount(room.getId());
            boolean isSelected = selectedCount > 0;

            if (isSelected) {
                btnSelect.setText(context.getString(R.string.selected_room_button, selectedCount));
                btnSelect.setIconResource(R.drawable.ic_arrow_down);
                btnSelect.setIconGravity(com.google.android.material.button.MaterialButton.ICON_GRAVITY_END);
                btnSelect.setIconPadding(8);
                btnSelect.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.toolbar_blue)));
                btnSelect.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.white)));
                btnSelect.setTextColor(ContextCompat.getColor(context, R.color.toolbar_blue));
                btnSelect.setStrokeWidth(2);
                btnSelect.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.toolbar_blue)));
                btnRemoveSelection.setVisibility(View.VISIBLE);
            } else {
                btnSelect.setText(R.string.select);
                btnSelect.setIcon(null);
                btnSelect.setIconGravity(com.google.android.material.button.MaterialButton.ICON_GRAVITY_TEXT_START);
                btnSelect.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.toolbar_blue)));
                btnSelect.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                btnSelect.setStrokeWidth(0);
                btnRemoveSelection.setVisibility(View.GONE);
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Inflates facility icon+label rows into a FlexboxLayout.
     * @param maxItems max number to show; pass Integer.MAX_VALUE to show all.
     */
    public void populateFacilities(FlexboxLayout flex, List<Facility> facilities, int maxItems) {
        flex.removeAllViews();
        if (facilities == null || facilities.isEmpty()) return;

        LayoutInflater inflater = LayoutInflater.from(context);
        int count = Math.min(facilities.size(), maxItems);

        for (int i = 0; i < count; i++) {
            Facility fac = facilities.get(i);
            View chip = inflater.inflate(R.layout.item_facility_chip, flex, false);

            ImageView icon = chip.findViewById(R.id.imgIcon);
            TextView  name = chip.findViewById(R.id.txtName);

            // Resolve drawable by name
            int resId = context.getResources().getIdentifier(
                    fac.getIconResName(), "drawable", context.getPackageName());
            if (resId != 0) icon.setImageResource(resId);
            else            icon.setImageResource(R.drawable.ic_facility_default);

            name.setText(!TextUtils.isEmpty(fac.getNameVi()) ? fac.getNameVi() : fac.getName());
            flex.addView(chip);
        }
    }

}