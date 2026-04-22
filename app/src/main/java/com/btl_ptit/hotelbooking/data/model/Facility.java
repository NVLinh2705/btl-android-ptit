package com.btl_ptit.hotelbooking.data.model;

public class Facility {
    private int id;
    private String name;
    private String nameVi;
    private int facilityTypeId;

    public Facility() {}

    public Facility(int id, String name, String nameVi, int facilityTypeId) {
        this.id = id;
        this.name = name;
        this.nameVi = nameVi;
        this.facilityTypeId = facilityTypeId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameVi() { return nameVi; }
    public void setNameVi(String nameVi) { this.nameVi = nameVi; }

    public int getFacilityTypeId() { return facilityTypeId; }
    public void setFacilityTypeId(int facilityTypeId) { this.facilityTypeId = facilityTypeId; }

    /**
     * Maps a facility name to a Material Symbols / built-in vector drawable resource name.
     * All icons listed here are available in the Material Design icon set and should be
     * added to res/drawable/ as vector assets via Android Studio's Vector Asset tool
     * (File → New → Vector Asset → Clip Art, search the name below).
     *
     * Name mapping (facility name → drawable resource name):
     *
     *   Free WiFi            → ic_facility_wifi
     *   Air conditioning     → ic_facility_ac
     *   Private bathroom     → ic_facility_bathroom
     *   Shower               → ic_facility_shower
     *   Bathtub              → ic_facility_bathtub
     *   Free toiletries      → ic_facility_toiletries
     *   Hairdryer            → ic_facility_hairdryer
     *   Towels               → ic_facility_towels
     *   Linen                → ic_facility_linen
     *   Wardrobe             → ic_facility_wardrobe
     *   Desk                 → ic_facility_desk
     *   Flat-screen TV       → ic_facility_tv
     *   Minibar              → ic_facility_minibar
     *   Room service         → ic_facility_room_service
     *   Restaurant           → ic_facility_restaurant
     *   Parking              → ic_facility_parking
     *   Airport shuttle      → ic_facility_shuttle
     *   Family rooms         → ic_facility_family
     *   Non-smoking rooms    → ic_facility_no_smoking
     *   Pets allowed         → ic_facility_pets
     *   Fitness centre       → ic_facility_gym
     *   Spa & wellness       → ic_facility_spa
     *   Outdoor pool         → ic_facility_pool
     *   City view            → ic_facility_city_view
     *   Sea view             → ic_facility_sea_view
     *   Garden view          → ic_facility_garden_view
     *   Pool view            → ic_facility_pool_view
     *   Balcony              → ic_facility_balcony
     *   Terrace              → ic_facility_terrace
     *   Refrigerator         → ic_facility_fridge
     *   Microwave            → ic_facility_microwave
     *   Electric kettle      → ic_facility_kettle
     *   Coffee machine       → ic_facility_coffee
     *   24h front desk       → ic_facility_reception
     *   Luggage storage      → ic_facility_luggage
     *   Lift                 → ic_facility_elevator
     *   Safety deposit box   → ic_facility_safe
     *   Daily housekeeping   → ic_facility_housekeeping
     *   Laundry              → ic_facility_laundry
     *   24h security         → ic_facility_security
     *   CCTV                 → ic_facility_cctv
     *   Wheelchair accessible→ ic_facility_accessible
     *   Ensuite bathroom     → ic_facility_ensuite
     *   Patio                → ic_facility_patio
     *   Inner courtyard view → ic_facility_courtyard_view
     *   (default / unknown)  → ic_facility_default
     */
    public String getIconResName() {
        if (name == null) return "ic_facility_default";
        switch (name.trim().toLowerCase()) {
            case "free wifi":              return "ic_facility_wifi";
            case "air conditioning":       return "ic_facility_ac";
            case "private bathroom":
            case "ensuite bathroom":       return "ic_facility_bathroom";
            case "shower":                 return "ic_facility_shower";
            case "bathtub":                return "ic_facility_bathtub";
            case "free toiletries":        return "ic_facility_toiletries";
            case "hairdryer":              return "ic_facility_hairdryer";
            case "towels":                 return "ic_facility_towels";
            case "linen":                  return "ic_facility_linen";
            case "wardrobe":               return "ic_facility_wardrobe";
            case "desk":                   return "ic_facility_desk";
            case "flat-screen tv":         return "ic_facility_tv";
            case "minibar":                return "ic_facility_minibar";
            case "room service":           return "ic_facility_room_service";
            case "restaurant":             return "ic_facility_restaurant";
            case "bar":                    return "ic_facility_minibar";
            case "free private parking":
            case "on-site parking":
            case "parking":               return "ic_facility_parking";
            case "airport shuttle":        return "ic_facility_shuttle";
            case "family rooms":           return "ic_facility_family";
            case "non-smoking rooms":      return "ic_facility_no_smoking";
            case "pets allowed":           return "ic_facility_pets";
            case "fitness centre":         return "ic_facility_gym";
            case "spa & wellness":
            case "sauna":
            case "massage":               return "ic_facility_spa";
            case "outdoor pool":
            case "indoor pool":
            case "rooftop pool":
            case "infinity pool":          return "ic_facility_pool";
            case "city view":              return "ic_facility_city_view";
            case "sea view":               return "ic_facility_sea_view";
            case "garden view":            return "ic_facility_garden_view";
            case "pool view":              return "ic_facility_pool_view";
            case "balcony":                return "ic_facility_balcony";
            case "terrace":
            case "patio":                  return "ic_facility_terrace";
            case "inner courtyard view":
            case "courtyard view":         return "ic_facility_courtyard_view";
            case "refrigerator":           return "ic_facility_fridge";
            case "microwave":              return "ic_facility_microwave";
            case "electric kettle":        return "ic_facility_kettle";
            case "coffee machine":         return "ic_facility_coffee";
            case "24h front desk":         return "ic_facility_reception";
            case "luggage storage":        return "ic_facility_luggage";
            case "lift":                   return "ic_facility_elevator";
            case "safety deposit box":     return "ic_facility_safe";
            case "daily housekeeping":     return "ic_facility_housekeeping";
            case "laundry":                return "ic_facility_laundry";
            case "24h security":           return "ic_facility_security";
            case "cctv":                   return "ic_facility_cctv";
            case "wheelchair accessible":  return "ic_facility_accessible";
            case "soundproofing":          return "ic_facility_soundproof";
            default:                       return "ic_facility_default";
        }
    }
}