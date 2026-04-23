package com.btl_ptit.hotelbooking.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
    
    /**
     * Tính toán thời gian delay từ hiện tại cho đến (dateStr - daysBefore)
     * @param dateStr Định dạng yyyy-MM-dd
     * @param daysBefore Số ngày muốn nhắc trước
     * @return milliseconds delay
     */
    public static long getDelayUntil(String dateStr, int daysBefore) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date targetDate = sdf.parse(dateStr);
            if (targetDate == null) return -1;

            long currentTime = System.currentTimeMillis();
            // Chuyển về milliseconds và trừ đi số ngày nhắc trước
            long targetTimeMillis = targetDate.getTime() - (daysBefore * 24L * 60 * 60 * 1000);
            
            // Tính toán delay so với hiện tại
            long delay = targetTimeMillis - currentTime;
            
            // Nếu đã qua thời điểm nhắc nhở (delay < 0) nhưng vẫn chưa đến ngày mục tiêu
            if (delay < 0) {
                if (currentTime < targetDate.getTime()) {
                    return 5000; // Trả về 5 giây để hiện thông báo ngay lập tức
                }
                return -1;
            }
            
            return delay;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
