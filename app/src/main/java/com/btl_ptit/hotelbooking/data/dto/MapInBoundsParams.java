package com.btl_ptit.hotelbooking.data.dto;

import com.google.android.gms.maps.model.LatLngBounds;

public class MapInBoundsParams {
    private LatLngBounds bounds;
    private float zoom;
    private int page, limit;

    public MapInBoundsParams(LatLngBounds bounds, float zoom) {
        this.bounds = bounds;
        this.zoom = zoom;
    }

    public MapInBoundsParams(LatLngBounds bounds, float zoom, int page, int limit) {
        this.bounds = bounds;
        this.zoom = zoom;
        this.page = page;
        this.limit = limit;
    }

    public LatLngBounds getBounds() {
        return bounds;
    }

    public void setBounds(LatLngBounds bounds) {
        this.bounds = bounds;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
