package com.uoi.spmsearch.dto.googlemaps;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GoogleMapsReverseGeocodingResponseResult extends GoogleMapsResponseResult {

    @JsonProperty("plus_code")
    PlusCode plusCode;

    public GoogleMapsReverseGeocodingResponseResult(String status, List<Result> results, String errorMessage, PlusCode plusCode) {
        super(status, results, errorMessage);
        this.plusCode = plusCode;
    }

    public GoogleMapsReverseGeocodingResponseResult() {}

    public PlusCode getPlusCode() {
        return plusCode;
    }

    public void setPlusCode(PlusCode plusCode) {
        this.plusCode = plusCode;
    }
}
