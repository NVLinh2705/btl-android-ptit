# Facility Icons — Source & Setup Guide

## How to add icons in Android Studio

1. Right-click `res/drawable` → **New → Vector Asset**
2. Select **Clip Art** → search the Material icon name in the table below
3. Set **Name** to the `res/drawable` filename in the table
4. Click **Finish**

All icons below are from the **Material Symbols** set (available built-in in Android Studio).

---

## Icon Mapping Table

| Drawable filename            | Material Icons search term       | Facility name(s)                          |
|------------------------------|----------------------------------|-------------------------------------------|
| `ic_facility_wifi`           | `wifi`                           | Free WiFi                                 |
| `ic_facility_ac`             | `ac_unit`                        | Air conditioning                          |
| `ic_facility_bathroom`       | `bathroom`                       | Private bathroom, Ensuite bathroom        |
| `ic_facility_shower`         | `shower`                         | Shower                                    |
| `ic_facility_bathtub`        | `bathtub`                        | Bathtub                                   |
| `ic_facility_toiletries`     | `soap`                           | Free toiletries                           |
| `ic_facility_hairdryer`      | `hair_dryer` *(or use `air`)*    | Hairdryer                                 |
| `ic_facility_towels`         | `dry_cleaning`                   | Towels                                    |
| `ic_facility_linen`          | `bed`                            | Linen                                     |
| `ic_facility_wardrobe`       | `checkroom`                      | Wardrobe                                  |
| `ic_facility_desk`           | `desk`                           | Desk                                      |
| `ic_facility_tv`             | `tv`                             | Flat-screen TV                            |
| `ic_facility_minibar`        | `liquor`                         | Minibar, Bar                              |
| `ic_facility_room_service`   | `room_service`                   | Room service                              |
| `ic_facility_restaurant`     | `restaurant`                     | Restaurant                                |
| `ic_facility_parking`        | `local_parking`                  | Parking, Free private parking             |
| `ic_facility_shuttle`        | `airport_shuttle`                | Airport shuttle                           |
| `ic_facility_family`         | `family_restroom`                | Family rooms                              |
| `ic_facility_no_smoking`     | `smoke_free`                     | Non-smoking rooms                         |
| `ic_facility_pets`           | `pets`                           | Pets allowed                              |
| `ic_facility_gym`            | `fitness_center`                 | Fitness centre                            |
| `ic_facility_spa`            | `spa`                            | Spa & wellness, Sauna, Massage            |
| `ic_facility_pool`           | `pool`                           | Outdoor/Indoor/Rooftop/Infinity pool      |
| `ic_facility_city_view`      | `location_city`                  | City view                                 |
| `ic_facility_sea_view`       | `waves`                          | Sea view                                  |
| `ic_facility_garden_view`    | `park`                           | Garden view                               |
| `ic_facility_pool_view`      | `pool`                           | Pool view                                 |
| `ic_facility_balcony`        | `balcony`                        | Balcony                                   |
| `ic_facility_terrace`        | `deck`                           | Terrace, Patio                            |
| `ic_facility_courtyard_view` | `yard`                           | Inner courtyard view, Courtyard view      |
| `ic_facility_fridge`         | `kitchen`                        | Refrigerator                              |
| `ic_facility_microwave`      | `microwave`                      | Microwave                                 |
| `ic_facility_kettle`         | `coffee_maker`                   | Electric kettle                           |
| `ic_facility_coffee`         | `local_cafe`                     | Coffee machine                            |
| `ic_facility_reception`      | `support_agent`                  | 24h front desk                            |
| `ic_facility_luggage`        | `luggage`                        | Luggage storage                           |
| `ic_facility_elevator`       | `elevator`                       | Lift                                      |
| `ic_facility_safe`           | `lock`                           | Safety deposit box                        |
| `ic_facility_housekeeping`   | `cleaning_services`              | Daily housekeeping                        |
| `ic_facility_laundry`        | `local_laundry_service`          | Laundry                                   |
| `ic_facility_security`       | `security`                       | 24h security                              |
| `ic_facility_cctv`           | `videocam`                       | CCTV                                      |
| `ic_facility_accessible`     | `accessible`                     | Wheelchair accessible                     |
| `ic_facility_soundproof`     | `hearing_disabled`               | Soundproofing                             |
| `ic_facility_default`        | `check_circle_outline`           | Fallback for unmapped facilities          |

---

## Alternative: Use Material Symbols font (no individual SVGs needed)

Instead of individual drawables, you can use the **Material Symbols** font:

### 1. Add dependency in `build.gradle`
```groovy
implementation "androidx.compose.material:material-icons-extended:$compose_version"
// OR for XML views, use the Google Fonts API:
```

### 2. Or use Google Fonts API in `res/font/`
```xml
<!-- res/font/material_symbols_outlined.xml -->
<font-family xmlns:app="http://schemas.android.com/apk/res-auto">
    <font
        app:fontProviderAuthority="com.google.android.gms.fonts"
        app:fontProviderPackage="com.google.android.gms"
        app:fontProviderQuery="Material Symbols Outlined"
        app:fontProviderCerts="@array/com_google_android_gms_fonts_certs" />
</font-family>
```

---

## Gradle dependencies needed for both activities

```groovy
// build.gradle (app)
dependencies {
    // Material Components
    implementation "com.google.android.material:material:1.11.0"

    // FlexboxLayout — for the 2-column facility grid
    implementation "com.google.android.flexbox:flexbox:3.0.0"

    // Image loading
    implementation "com.github.bumptech.glide:glide:4.16.0"

    // ViewPager2 (photo gallery)
    implementation "androidx.viewpager2:viewpager2:1.0.0"
}
```

---

## Icon size recommendation

- In `item_facility_chip.xml`: **18dp × 18dp**
- In detail screen `activity_room_type_detail.xml`: **20dp × 20dp**
- Tint: `?attr/colorOnSurfaceVariant` (auto adapts to light/dark theme)