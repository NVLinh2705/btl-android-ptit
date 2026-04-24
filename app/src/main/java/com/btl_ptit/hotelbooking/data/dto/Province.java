package com.btl_ptit.hotelbooking.data.dto;

import java.util.List;

public class Province {
    private String name;
    private int code;
    private List<Ward> wards;

    public Province() {
    }

    public String getName( ) {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Ward> getDistricts() {
        return wards;
    }

    public void setDistricts(List<Ward> wards) {
        this.wards = wards;
    }
}
