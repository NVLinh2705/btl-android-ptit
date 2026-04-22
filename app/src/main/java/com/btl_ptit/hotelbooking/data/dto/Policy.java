package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Policy {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("type_id")
    private int typeId;

    @SerializedName("type_code")
    private String typeCode;

    @SerializedName("type_name")
    private String typeName;

}

