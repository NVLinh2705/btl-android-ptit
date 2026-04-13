package com.btl_ptit.hotelbooking.data.model;

import java.util.List;

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

    // ── Constructors ──────────────────────────────────────────────────────

    public RoomType() {}

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

    // ── Getters & Setters ─────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getHotelId() { return hotelId; }
    public void setHotelId(int hotelId) { this.hotelId = hotelId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getMaxGuests() { return maxGuests; }
    public void setMaxGuests(int maxGuests) { this.maxGuests = maxGuests; }

    public int getBedCount() { return bedCount; }
    public void setBedCount(int bedCount) { this.bedCount = bedCount; }

    public String getBedType() { return bedType; }
    public void setBedType(String bedType) { this.bedType = bedType; }

    public double getBasePricePerNight() { return basePricePerNight; }
    public void setBasePricePerNight(double basePricePerNight) { this.basePricePerNight = basePricePerNight; }

    public boolean isHasFreeCancellation() { return hasFreeCancellation; }
    public void setHasFreeCancellation(boolean hasFreeCancellation) { this.hasFreeCancellation = hasFreeCancellation; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public String getView() { return view; }
    public void setView(String view) { this.view = view; }

    public List<Facility> getFacilities() { return facilities; }
    public void setFacilities(List<Facility> facilities) { this.facilities = facilities; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    /** Returns true if only 1 room is left available */
    public boolean isLastRoom() { return quantity == 1; }

    /** Formatted bed summary, e.g. "1 extra-large double bed" */
    public String getBedSummary() {
        return bedCount + " " + (bedType != null ? bedType.toLowerCase() : "bed") + (bedCount > 1 ? "s" : "");
    }

    /** Formatted area string, e.g. "20 m²" */
    public String getAreaLabel() {
        if (area == null) return null;
        int a = area.intValue();
        return "Size: " + a + " m\u00B2";
    }
}