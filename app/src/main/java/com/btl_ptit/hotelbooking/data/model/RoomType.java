package com.btl_ptit.hotelbooking.data.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class RoomType {
    private int id;
    private int hotelId;
    private String name;
    private String description;
    private int maxGuests;
    private int bedCount;
    private String bedType;
    private double basePricePerNight;
    private boolean hasFreeCancellation;
    private boolean isActive;
    private int quantity;
    private Double area;      // m²  — nullable
    private String view;      // e.g. "Sea view" — nullable
    private List<Facility> facilities;
    private List<String> imageUrls;
    private int nights;
    private double totalPrice;
    private String cancellationPolicy;
    private String paymentPolicy;

    // ── Constructors ──────────────────────────────────────────────────────

    public RoomType(int id, int hotelId, String name, String description,
                    int maxGuests, int bedCount, String bedType,
                    double basePricePerNight, boolean hasFreeCancellation,
                    boolean isActive, int quantity,
                    Double area, String view,
                    List<Facility> facilities, List<String> imageUrls) {
        this.id = id;
        this.hotelId = hotelId;
        this.name = name;
        this.description = description;
        this.maxGuests = maxGuests;
        this.bedCount = bedCount;
        this.bedType = bedType;
        this.basePricePerNight = basePricePerNight;
        this.hasFreeCancellation = hasFreeCancellation;
        this.isActive = isActive;
        this.quantity = quantity;
        this.area = area;
        this.view = view;
        this.facilities = facilities;
        this.imageUrls = imageUrls;
    }

    /** Returns true if only 1 room is left available */
    public boolean isLastRoom() { return quantity == 1; }

    /** Formatted bed summary, e.g. "1 extra-large double bed" */
    public String getBedSummary() {
        return bedCount + " " + (bedType != null ? bedType.toLowerCase() : "giường");
    }

    /** Formatted area string, e.g. "20 m²" */
    public String getAreaLabel() {
        if (area == null) return null;
        int a = area.intValue();
        return "Diện tích: " + a + " m\u00B2";
    }
}