package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hotel {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("host_id")
    private String hostId;

    @SerializedName("address")
    private String address;

    @SerializedName("base_currency")
    private String baseCurrency;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("longitude")
    private Double longitude;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("province_code")
    private String provinceCode;

    @SerializedName("district_code")
    private String districtCode;

    @SerializedName("ward_code")
    private String wardCode;

}

