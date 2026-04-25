package com.btl_ptit.hotelbooking.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager2.widget.ViewPager2;

import com.btl_ptit.hotelbooking.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MyUtils {

    /**
     * Kiểm tra xem sự thay đổi giữa hai vùng nhìn trên bản đồ có đáng kể hay không.
     * Trả về true nếu thay đổi nhỏ (không đáng kể), false nếu thay đổi lớn. Thay đổi nhỏ thì không call API
     */
    public static boolean isMapChangeInsignificant(
            LatLngBounds oldBounds, float oldZoom,
            LatLngBounds newBounds, float newZoom) {

        // 1. Ngưỡng thay đổi Zoom (lệch dưới 0.3 thì coi như chưa đổi)
        boolean isZoomInsignificant = Math.abs(oldZoom - newZoom) < Constants.ZOOM_LEVEL_LOWER_THRESHOLD;

        // 2. Ngưỡng thay đổi khoảng cách Tâm bản đồ
        LatLng oldCenter = oldBounds.getCenter();
        LatLng newCenter = newBounds.getCenter();

        float[] results = new float[1];
        Location.distanceBetween(
                oldCenter.latitude, oldCenter.longitude,
                newCenter.latitude, newCenter.longitude,
                results
        );

        // Giả sử lệch dưới 200 mét thì coi như không đổi
        boolean isDistanceInsignificant = results[0] < Constants.DISTANCE_LOWER_THRESHOLD;

        return isZoomInsignificant && isDistanceInsignificant;
    }

    public static int dpToPx(int dp) {
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                Resources.getSystem().getDisplayMetrics()
        );
        return px;
    }

    public static BitmapDescriptor createMarkerIcon(Context mContext, String price, boolean isSelected, boolean isFavourite) {
        // Inflate layout marker
        View markerView = LayoutInflater.from(mContext).inflate(R.layout.custom_marker_item, null);
        TextView tvPrice = markerView.findViewById(R.id.tv_price_marker);
        ImageView imgHeart = markerView.findViewById(R.id.img_heart);
        View root = markerView.findViewById(R.id.marker_root);

        tvPrice.setText(price);

        // Xử lý màu sắc nếu được chọn (tùy chọn)
        if (isSelected) {
            root.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
            tvPrice.setTextColor(Color.WHITE);
            if (isFavourite) {
                // Trái tim màu trắng khi nền marker màu xanh
                ImageViewCompat.setImageTintList(imgHeart, ColorStateList.valueOf(Color.WHITE));
                imgHeart.setVisibility(View.VISIBLE);
            } else {
                // Trái tim màu xanh khi nền marker màu trắng
                ImageViewCompat.setImageTintList(imgHeart, ColorStateList.valueOf(Color.parseColor("#3F51B5")));
                imgHeart.setVisibility(View.GONE);
            }
        } else {
            if (isFavourite) {
                ImageViewCompat.setImageTintList(imgHeart, ColorStateList.valueOf(Color.parseColor("#3F51B5")));
                imgHeart.setVisibility(View.VISIBLE);
            } else {
                imgHeart.setVisibility(View.GONE);
            }
        }


        // Đo và vẽ View ra Bitmap
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerView.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static PowerMenu getPowerMenuInHotelInBound(Context mContext) {
        return new PowerMenu.Builder(mContext)
                // 1. Thêm các Item (Text, Icon)
                .addItem(new PowerMenuItem(mContext.getString(R.string.like), false, R.drawable.ic_heart_filled))
                .addItem(new PowerMenuItem(mContext.getString(R.string.see_directions_label),false,  R.drawable.outline_directions_24))
                .addItem(new PowerMenuItem(mContext.getString(R.string.detail), false, R.drawable.outline_info_24))
                .setLifecycleOwner((LifecycleOwner) mContext)
                .setMenuRadius(10f)                      // Bo góc
                .setMenuShadow(10f)                      // Đổ bóng
                .setShowBackground(false)
                .setFocusable(true)      // Click ra ngoài thì tự đóng
                .setIconSize(20)         // Kích thước icon (dp)
                .setTextSize(13)
                .setIconPadding(4)      // Khoảng cách giữa icon và chữ
                .setPadding(4)          // Khoảng cách bên trong menu
                .setIconColor(Color.BLACK)
                .setTextColor(ContextCompat.getColor(mContext, R.color.colorOutline_mediumContrast))
                .setTextGravity(Gravity.START)
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setSelectedMenuColor(ContextCompat.getColor(mContext, R.color.md_theme_inversePrimary_mediumContrast))
                .build();
    }

    public static void hideViewPager(ViewPager2 viewPager) {
        if (viewPager != null) {
            viewPager.animate()
                    .translationY(viewPager.getHeight() + 100) // Đẩy xuống dưới màn hình
                    .alpha(0.0f) // Làm mờ dần
                    .setDuration(300)
                    .withEndAction(() -> viewPager.setVisibility(View.GONE))
                    .start();
        }
    }

    public static void showViewPager(ViewPager2 viewPager) {
        if (viewPager != null) {
            viewPager.setVisibility(View.VISIBLE);
            viewPager.animate()
                    .translationY(0) // Kéo ngược lại vị trí cũ
                    .alpha(1.0f)
                    .setDuration(300)
                    .start();
        }
    }

    public static String myFormatDate(Long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date(timeInMillis));
    }

    public static String myFormatDateForSessionManager(Long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date(timeInMillis));
    }

    public static String formatToViewDate(String inputDate) {
        // Định dạng đầu vào khớp với database/API
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // Định dạng đầu ra để hiển thị cho người dùng
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(inputDate);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputDate;
    }

    public static void setupBottomSheet(Dialog dialog, FragmentActivity fragmentActivity) {
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                // Dùng WRAP_CONTENT để nó tự co theo nội dung khi nội dung ngắn
                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

                // 2. Set Max Height để nếu nội dung quá dài, nó không vượt quá 90% màn hình
                int screenHeight = fragmentActivity.getResources().getDisplayMetrics().heightPixels;
                behavior.setMaxHeight((int) (screenHeight * 0.90));

                // 3. Ép trạng thái ban đầu là EXPANDED
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                // 4. Bỏ qua trạng thái lửng lơ (Collapsed), vuốt xuống là đóng luôn
                behavior.setSkipCollapsed(true);
            }
        }
    }
}
