package com.btl_ptit.hotelbooking.data.session;

import com.btl_ptit.hotelbooking.data.model.RoomType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * In-memory room selection store.
 * Data is reset when process dies or when leaving hotel flow explicitly.
 */
public final class RoomSelectionStore {

    private static Integer currentHotelId = null;
    private static final Map<Integer, SelectedRoom> selectedRooms = new LinkedHashMap<>();

    private RoomSelectionStore() {
    }

    public static synchronized void attachHotel(int hotelId) {
        if (currentHotelId == null || currentHotelId != hotelId) {
            clear();
            currentHotelId = hotelId;
        }
    }

    public static synchronized void addRoom(RoomType roomType) {
        if (roomType == null) return;
        if (currentHotelId == null || currentHotelId != roomType.getHotelId()) {
            attachHotel(roomType.getHotelId());
        }

        SelectedRoom selected = selectedRooms.get(roomType.getId());
        if (selected == null) {
            selected = new SelectedRoom(roomType, 0);
            selectedRooms.put(roomType.getId(), selected);
        }
        selected.count += 1;
    }

    public static synchronized void setRoomCount(RoomType roomType, int count) {
        if (roomType == null) return;
        if (currentHotelId == null || currentHotelId != roomType.getHotelId()) {
            attachHotel(roomType.getHotelId());
        }

        if (count <= 0) {
            selectedRooms.remove(roomType.getId());
            return;
        }

        SelectedRoom selected = selectedRooms.get(roomType.getId());
        if (selected == null) {
            selected = new SelectedRoom(roomType, count);
            selectedRooms.put(roomType.getId(), selected);
        } else {
            selected.roomType.setTotalPrice(roomType.getTotalPrice());
            selected.roomType.setBasePricePerNight(roomType.getBasePricePerNight());
            selected.count = count;
        }
    }

    public static synchronized int getRoomCount(int roomTypeId) {
        SelectedRoom selected = selectedRooms.get(roomTypeId);
        return selected == null ? 0 : selected.count;
    }

    public static synchronized void removeRoom(int roomTypeId) {
        selectedRooms.remove(roomTypeId);
    }

    public static synchronized int getSelectedRoomCount() {
        int total = 0;
        for (SelectedRoom item : selectedRooms.values()) {
            total += item.count;
        }
        return total;
    }

    public static synchronized int getSelectedBedCount() {
        int total = 0;
        for (SelectedRoom item : selectedRooms.values()) {
            total += item.count * Math.max(0, item.roomType.getBedCount());
        }
        return total;
    }

    public static synchronized double getTotalPrice() {
        double total = 0;
        for (SelectedRoom item : selectedRooms.values()) {
            double base = item.roomType.getTotalPrice() > 0
                    ? item.roomType.getTotalPrice()
                    : item.roomType.getBasePricePerNight();
            total += item.count * base;
        }
        return total;
    }

    public static synchronized boolean hasSelection() {
        return getSelectedRoomCount() > 0;
    }

    public static synchronized void clear() {
        selectedRooms.clear();
        currentHotelId = null;
    }

    private static final class SelectedRoom {
        final RoomType roomType;
        int count;

        SelectedRoom(RoomType roomType, int count) {
            this.roomType = roomType;
            this.count = count;
        }
    }
}

