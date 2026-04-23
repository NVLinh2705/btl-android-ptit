package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelFacility {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("name_vi")
    private String nameVi;

    @SerializedName("type_id")
    private int typeId;

    @SerializedName("type_name")
    private String typeName;

    @SerializedName("type_name_vi")
    private String typeNameVi;

}

