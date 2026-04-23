package com.btl_ptit.hotelbooking.data.model;

import com.btl_ptit.hotelbooking.interfaces.DiffUtilModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MyRoomType implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("max_guests")
    private int maxGuests;

    @SerializedName("bed_type")
    private String bedType;

    @SerializedName("view")
    private String view;

    @SerializedName("area")
    private String area;

    public MyRoomType() {
    }

    public MyRoomType(String id, String name, String description, int maxGuests, String bedType, String view, String area) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxGuests = maxGuests;
        this.bedType = bedType;
        this.view = view;
        this.area = area;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
