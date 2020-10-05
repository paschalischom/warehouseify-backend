package com.uoi.spmsearch.errorhandling;

public class InvalidGeocodingArgumentsException extends RuntimeException {

    public InvalidGeocodingArgumentsException(String params) {
        super(InvalidGeocodingArgumentsException.createErrorMessage(params));
    }

    private static String createErrorMessage(String params) {
        return "Wrong or missing geocoding arguments. Parameters: " + params;
    }

}
