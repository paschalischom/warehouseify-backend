package com.uoi.spmsearch.dto.googlemaps;

import java.util.List;

public class GoogleMapsForwardGeocodingResponseResult extends GoogleMapsResponseResult {

    public GoogleMapsForwardGeocodingResponseResult(String status, List<Result> results, String errorMessage) {
        super(status, results, errorMessage);
    }

    public GoogleMapsForwardGeocodingResponseResult() {
    }
}
