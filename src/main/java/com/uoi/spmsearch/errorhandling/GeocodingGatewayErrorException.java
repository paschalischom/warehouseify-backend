package com.uoi.spmsearch.errorhandling;

public class GeocodingGatewayErrorException extends RuntimeException {

    public GeocodingGatewayErrorException(String googleError) {
        super(GeocodingGatewayErrorException.createErrorMessage(googleError));
    }

    private static String createErrorMessage(String googleError) {
        return "Geocoding failed due to a Google Service error. Response was: " + googleError;
    }
}
