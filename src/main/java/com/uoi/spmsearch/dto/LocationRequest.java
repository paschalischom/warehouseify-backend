package com.uoi.spmsearch.dto;

import static java.lang.Double.NaN;

public class LocationRequest {

    double latitude = NaN;
    double longitude = NaN;

    public LocationRequest() {}

    public LocationRequest(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String encodeReverseGeocodingUrl() {
        return latitude + "," + longitude;
    }
}
