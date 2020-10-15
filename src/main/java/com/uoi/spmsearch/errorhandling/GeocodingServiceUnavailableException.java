package com.uoi.spmsearch.errorhandling;

public class GeocodingServiceUnavailableException extends RuntimeException {

    public GeocodingServiceUnavailableException(String googleError) {
        super(GeocodingServiceUnavailableException.createErrorMessage(googleError));
    }

    private static String createErrorMessage(String googleError) {
        return "Geocoding failed due to a Google Service error. Response was: " + googleError;
    }
}
