package com.uoi.spmsearch.dto;

public class RTreeLeaf extends RTreeBase {
    GeoPoint geopoint;

    public RTreeLeaf(String id, int distance, GeoPoint geopoint) {
        super(id, distance);
        this.geopoint = geopoint;
    }

    public RTreeLeaf() {
    }

    public GeoPoint getGeopoint() {
        return geopoint;
    }

    public void setGeopoint(GeoPoint geopoint) {
        this.geopoint = geopoint;
    }
}
