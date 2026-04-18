package com.btl_ptit.hotelbooking.data.session;

import com.btl_ptit.hotelbooking.data.model.RoomType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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

    public static synchronized List<SelectionItem> getSelectionItems() {
        if (selectedRooms.isEmpty()) {
            return Collections.emptyList();
        }

        List<SelectionItem> items = new ArrayList<>();
        for (SelectedRoom selected : selectedRooms.values()) {
            double unitPrice = selected.roomType.getTotalPrice() > 0
                    ? selected.roomType.getTotalPrice()
                    : selected.roomType.getBasePricePerNight();
            items.add(new SelectionItem(selected.roomType, selected.count, unitPrice));
        }
        return items;
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

    public static final class SelectionItem {
        private final RoomType roomType;
        private final int count;
        private final double unitPrice;

        private SelectionItem(RoomType roomType, int count, double unitPrice) {
            this.roomType = roomType;
            this.count = count;
            this.unitPrice = unitPrice;
        }

        public RoomType getRoomType() {
            return roomType;
        }

        public int getCount() {
            return count;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public double getLineTotal() {
            return unitPrice * count;
        }
    }
}

