package com.btl_ptit.hotelbooking.data.model;

public class MyHotel {
    private String id;
    private String name, avatar;

    public MyHotel() {
    }

    public MyHotel(String id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        MyHotel myHotel = (MyHotel) other;
        return getId().equals(myHotel.getId()) &&
                getName().equals(myHotel.getName()) &&
                getAvatar().equals(myHotel.getAvatar());
    }
}
